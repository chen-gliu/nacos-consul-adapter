package at.liucheng.nacosconsuladapter.service.impl;

import at.liucheng.nacosconsuladapter.config.NacosConsulAdapterProperties;
import at.liucheng.nacosconsuladapter.model.Result;
import at.liucheng.nacosconsuladapter.model.ServiceInstancesHealth;
import at.liucheng.nacosconsuladapter.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Supplier;

/**
 * @description:直接请求注册中心模式
 * @author: lc
 * @createDate: 2021/5/31
 */
@RequiredArgsConstructor
@Slf4j
public class DirectRegistrationService implements RegistrationService {

    private final ReactiveDiscoveryClient reactiveDiscoveryClient;

    @Override
    public Mono<Result<Map<String, List<Object>>>> getServiceNames(long waitMillis, Long index) {
        return reactiveDiscoveryClient.getServices()
                .collectList()
                .switchIfEmpty(Mono.just(Collections.emptyList()))
                .map(serviceList -> {
                    Set<String> set = new HashSet<>();
                    set.addAll(serviceList);
                    Map<String, List<Object>> result = new HashMap<>(serviceList.size());
                    for (String item : set) {
                        result.put(item, Collections.emptyList());
                    }
                    return result;
                })
                .map(data -> new Result<>(data, System.currentTimeMillis()));
    }

    @Override
    public Mono<Result<List<ServiceInstancesHealth>>> getServiceInstancesHealth(String serviceName, long waitMillis, Long index) {
        return reactiveDiscoveryClient.getInstances(serviceName)
                .map(serviceInstance -> {
                    ServiceInstancesHealth.Node node = ServiceInstancesHealth.Node.builder()
                            .address(serviceInstance.getHost())
                            .id(serviceInstance.getInstanceId())
                            //todo 数据中心
                            .dataCenter("dc1")
                            .build();
                    ServiceInstancesHealth.Service service = ServiceInstancesHealth.Service.builder()
                            .service(serviceInstance.getServiceId())
                            .id(serviceInstance.getServiceId() + "-" + serviceInstance.getPort())
                            .port(serviceInstance.getPort())
                            .build();
                    return ServiceInstancesHealth.builder().node(node).service(service).build();
                })
                .collectList()
                .map(data -> new Result<>(data, System.currentTimeMillis()));
    }


}
