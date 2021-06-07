package at.liucheng.nacosconsuladapter;

import at.liucheng.nacosconsuladapter.config.NacosConsulAdapterProperties;
import at.liucheng.nacosconsuladapter.controller.AgentController;
import at.liucheng.nacosconsuladapter.controller.ServiceController;
import at.liucheng.nacosconsuladapter.service.RegistrationService;
import at.liucheng.nacosconsuladapter.service.impl.DirectRegistrationService;
import at.liucheng.nacosconsuladapter.service.impl.LongPollingRegistrationService;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * @description:
 * @author: liucheng
 * @createTime:2021/6/3 21:25
 */
@EnableConfigurationProperties({NacosConsulAdapterProperties.class})
//@EnableConfigurationProperties
@Slf4j
public class NacosConsulAdapterConfig {
    @Bean
    @ConditionalOnProperty(
            value = {"nacos-consul-adapter.mode"}, havingValue = "direct"
    )
    @ConditionalOnBean({ReactiveDiscoveryClient.class})
    public RegistrationService directRegistrationService(ReactiveDiscoveryClient reactiveDiscoveryClient) {
        log.info("创建直接的请求bean");
        return new DirectRegistrationService(reactiveDiscoveryClient);
    }

    @Bean
    @ConditionalOnProperty(
            value = {"nacos-consul-adapter.mode"}, havingValue = "long-polling"
    )
    @ConditionalOnBean({ReactiveDiscoveryClient.class})
    public RegistrationService longPollingRegistrationService(NacosConsulAdapterProperties nacosConsulAdapterProperties,
                                                              DiscoveryClient discoveryClient, NacosServiceManager nacosServiceManager,
                                                              NacosDiscoveryProperties nacosDiscoveryProperties) {
        log.info("创建长轮询的请求bean");
        return new LongPollingRegistrationService(nacosConsulAdapterProperties, discoveryClient, nacosServiceManager, nacosDiscoveryProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public AgentController agentController() {
        return new AgentController();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceController serviceController(RegistrationService registrationService) {
        return new ServiceController(registrationService);
    }

}
