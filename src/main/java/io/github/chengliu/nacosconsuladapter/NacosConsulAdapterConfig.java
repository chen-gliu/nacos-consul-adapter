package io.github.chengliu.nacosconsuladapter;

import io.github.chengliu.nacosconsuladapter.config.NacosConsulAdapterProperties;
import io.github.chengliu.nacosconsuladapter.controller.AgentController;
import io.github.chengliu.nacosconsuladapter.controller.ServiceController;
import io.github.chengliu.nacosconsuladapter.service.RegistrationService;
import io.github.chengliu.nacosconsuladapter.service.impl.DirectRegistrationService;
import io.github.chengliu.nacosconsuladapter.service.impl.LongPollingRegistrationService;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * @description:
 * @author: liucheng
 * @createTime:2021/6/3 21:25
 */
@EnableConfigurationProperties
@Slf4j
public class NacosConsulAdapterConfig {

    @Bean
    @ConditionalOnMissingBean
    public NacosConsulAdapterProperties nacosConsulAdapterProperties() {
        return new NacosConsulAdapterProperties();
    }

//    @Bean
//    @ConditionalOnProperty(
//            value = "nacos-consul-adapter.mode", havingValue = "direct"
//    )
//    public RegistrationService directRegistrationService(ReactiveDiscoveryClient reactiveDiscoveryClient) {
//        log.info("创建直接的请求bean");
//        return new DirectRegistrationService(reactiveDiscoveryClient);
//    }


    //    @Bean
//    @ConditionalOnProperty(name = "nacos-consul-adapter.mode", havingValue = "long-polling")
//    public RegistrationService longPollingRegistrationService(NacosConsulAdapterProperties nacosConsulAdapterProperties,
//                                                              DiscoveryClient discoveryClient, NacosServiceManager nacosServiceManager,
//                                                              NacosDiscoveryProperties nacosDiscoveryProperties) {
//
//        log.info("创建长轮询的请求bean");
//        return new LongPollingRegistrationService(nacosConsulAdapterProperties, discoveryClient, nacosServiceManager, nacosDiscoveryProperties);
//    }
    @Bean
    public RegistrationService registrationService(NacosConsulAdapterProperties nacosConsulAdapterProperties,
                                                   DiscoveryClient discoveryClient, NacosServiceManager nacosServiceManager,
                                                   NacosDiscoveryProperties nacosDiscoveryProperties, ReactiveDiscoveryClient reactiveDiscoveryClient) {
        if (NacosConsulAdapterProperties.DIRECT_MODE.equals(nacosConsulAdapterProperties.getMode())) {
            log.info("使用直接查询模式");
            return new DirectRegistrationService(reactiveDiscoveryClient);
        }
        log.info("使用长轮询模式");
        return new LongPollingRegistrationService(nacosConsulAdapterProperties, discoveryClient, nacosServiceManager, nacosDiscoveryProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public AgentController agentController() {
        return new AgentController();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("nacosConsulAdapterProperties")
    public ServiceController serviceController(RegistrationService registrationService) {
        return new ServiceController(registrationService);
    }

}
