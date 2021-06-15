package at.liucheng.nacosconsuladapter.service;


import at.liucheng.nacosconsuladapter.config.NacosConsulAdapterProperties;
import at.liucheng.nacosconsuladapter.model.Result;
import at.liucheng.nacosconsuladapter.model.ServiceInstancesHealth;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Returns Services and List of Service with its last changed
 */
public interface RegistrationService {

    /**
     * get all service name
     * 获取所有服务名称
     *
     * @param waitMills
     * @param index
     * @return
     */
    Mono<Result<Map<String, List<Object>>>> getServiceNames(long waitMills, Long index);

    /**
     * get speci service all instance
     * 获取指定服务所有实例
     *
     * @param serviceName
     * @param waitMillis
     * @param index
     * @return
     */
    Mono<Result<List<ServiceInstancesHealth>>> getServiceInstancesHealth(String serviceName, long waitMillis, Long index);

}