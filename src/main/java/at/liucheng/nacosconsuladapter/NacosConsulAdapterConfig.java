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
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @Bean
    @ConditionalOnProperty(
            value = {"nacos-consul-adapter.mode"}, havingValue = "direct"
    )
    public RegistrationService directRegistrationService(ReactiveDiscoveryClient reactiveDiscoveryClient) {
        log.info("创建直接的请求bean");
        return new DirectRegistrationService(reactiveDiscoveryClient);
    }

    @Bean
    @ConditionalOnProperty(
            value = {"nacos-consul-adapter.mode"}, havingValue = "long-polling"
    )
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
    @DependsOn("nacosConsulAdapterProperties")
    public ServiceController serviceController(RegistrationService registrationService) {
        return new ServiceController(registrationService);
    }

}
