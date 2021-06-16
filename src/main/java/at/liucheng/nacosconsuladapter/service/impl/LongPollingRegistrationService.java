package at.liucheng.nacosconsuladapter.service.impl;

import at.liucheng.nacosconsuladapter.config.NacosConsulAdapterProperties;
import at.liucheng.nacosconsuladapter.model.Result;
import at.liucheng.nacosconsuladapter.model.ServiceInstancesHealth;
import at.liucheng.nacosconsuladapter.service.RegistrationService;
import at.liucheng.nacosconsuladapter.utils.NacosServiceCenter;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
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
        nacosServiceCenter = new NacosServiceCenter(namingService);
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
        return namingService.getAllInstances(serviceName).stream().map(instance -> {
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
