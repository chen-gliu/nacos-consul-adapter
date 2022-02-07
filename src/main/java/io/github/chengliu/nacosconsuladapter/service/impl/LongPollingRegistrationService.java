/**
 * The MIT License
 * Copyright © 2021 liu cheng
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.chengliu.nacosconsuladapter.service.impl;

import io.github.chengliu.nacosconsuladapter.config.NacosConsulAdapterProperties;
import io.github.chengliu.nacosconsuladapter.model.Result;
import io.github.chengliu.nacosconsuladapter.model.ServiceInstancesHealth;
import io.github.chengliu.nacosconsuladapter.service.RegistrationService;
import io.github.chengliu.nacosconsuladapter.utils.NacosServiceCenter;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @description:长轮询模式
 * @author: lc
 * @createDate: 2021/5/31
 */
@Slf4j
public class LongPollingRegistrationService implements RegistrationService, ApplicationRunner {

    private NacosServiceCenter nacosServiceCenter;
    private NacosConsulAdapterProperties nacosConsulAdapterProperties;
    private DiscoveryClient discoveryClient;
    private ScheduledExecutorService executorService;
    private NacosServiceManager nacosServiceManager;
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    private NacosNamingService namingService;


    public LongPollingRegistrationService(NacosConsulAdapterProperties nacosConsulAdapterProperties,
                                          DiscoveryClient discoveryClient, NacosServiceManager nacosServiceManager,
                                          NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.nacosConsulAdapterProperties = nacosConsulAdapterProperties;
        this.discoveryClient = discoveryClient;
        executorService = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r);
            t.setName("at.liucheng.nacos-consul-adapter.service.updater");
            t.setDaemon(true);
            return t;
        });
        this.nacosServiceManager = nacosServiceManager;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        namingService = (NacosNamingService) nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
        nacosServiceCenter = new NacosServiceCenter(namingService, nacosDiscoveryProperties);
    }

    @Override
    public Mono<Result<Map<String, List<Object>>>> getServiceNames(long waitMills, Long index) {
        return Mono.just(nacosServiceCenter.getServiceNames())
                .map(serviceSet -> {
                    Map<String, List<Object>> result = new HashMap<>(serviceSet.size());
                    for (String item : serviceSet) {
                        result.put(item, Collections.emptyList());
                    }
                    return result;
                })
                .map(data -> new Result<>(data, System.currentTimeMillis()));
    }


    @Override
    public Mono<Result<List<ServiceInstancesHealth>>> getServiceInstancesHealth(String serviceName, long waitMillis, Long index) {
        Long version = nacosServiceCenter.getServiceVersion(serviceName);
        //如果index和现在的version不同（只有可能是index 小于version）,说明服务发生了变动马上返回。
        if (index == null || !index.equals(version)) {
            log.debug("{} had changed,direct return.", serviceName);
            return Mono.just(new Result<List<ServiceInstancesHealth>>(getServiceInstance(serviceName), version));
        }
        return nacosServiceCenter.getChangeHotSource(serviceName)
                .map(result -> result.getChangeIndex())
                .timeout(Duration.ofMillis(waitMillis), Flux.just(version))
                .take(1)
                .collectList()
                .map(newVersionList -> {
                    Long newVersion = newVersionList.get(0);
                    if (!version.equals(newVersion)) {
                        log.debug("during long-polling,{} had changed.version is {}", serviceName, newVersion);
                    } else {
                        log.debug("during long-polling,{} not changed.version is {}", serviceName, newVersion);
                    }
                    return new Result<List<ServiceInstancesHealth>>(getServiceInstance(serviceName), newVersion);
                });
    }

    @SneakyThrows
    private List<ServiceInstancesHealth> getServiceInstance(String serviceName) {
        return namingService.selectInstances(serviceName, nacosDiscoveryProperties.getGroup(),true).stream().map(instance -> {
            ServiceInstancesHealth.Node node = ServiceInstancesHealth.Node.builder()
                    .address(instance.getIp())
                    .id(instance.getInstanceId())
                    //todo 数据中心
                    .dataCenter("dc1")
                    .build();
            ServiceInstancesHealth.Service service = ServiceInstancesHealth.Service.builder()
                    .service(serviceName)
                    .id(serviceName + "-" + instance.getPort())
                    .port(instance.getPort())
                    .build();
            return ServiceInstancesHealth.builder().node(node).service(service).build();
        }).collect(Collectors.toList());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        nacosServiceCenter.initSetNames(discoveryClient.getServices());

        this.executorService.scheduleWithFixedDelay(() -> {
            nacosServiceCenter.setServiceNames(discoveryClient.getServices());
        }, 0, nacosConsulAdapterProperties.getServiceNameIntervalMills(), TimeUnit.MILLISECONDS);

    }

    @PreDestroy
    public void shutdown() {

        executorService.shutdownNow();
    }


}
