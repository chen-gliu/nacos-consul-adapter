package at.liucheng.nacosconsuladapter.config;

import ch.qos.logback.core.util.TimeUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author lc
 * @createTime 2021/6/1 9:58
 */
@ConfigurationProperties(prefix = "nacos-consul-adapter")
public class NacosConsulAdapterProperties {
    private Long DEFAULT_INTERVAL_MILLS = TimeUnit.SECONDS.toMillis(5);
    private Long serviceNameIntervalMills = DEFAULT_INTERVAL_MILLS;

    @PostConstruct
    public void init() throws Exception {
        if (Objects.isNull(serviceNameIntervalMills)) {
            serviceNameIntervalMills = DEFAULT_INTERVAL_MILLS;
        }
    }


    public void setServiceNameIntervalMills(Long serviceNameIntervalMills) {
        this.serviceNameIntervalMills = serviceNameIntervalMills;
    }

    public Long getServiceNameIntervalMills() {
        return serviceNameIntervalMills;
    }
}
