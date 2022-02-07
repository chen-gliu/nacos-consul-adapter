/**
 * The MIT License
 * Copyright © 2021 liu cheng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
