package at.liucheng.nacosconsuladapter.config;

import at.liucheng.nacosconsuladapter.NacosConsulAdapterConfig;
import at.liucheng.nacosconsuladapter.service.RegistrationService;
import ch.qos.logback.core.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
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
    public static String DEFAULT_MODE = "long-polling";
    private String mode = DEFAULT_MODE;

//    @PostConstruct
//    public void init() throws Exception {
//
//        if (Objects.isNull(serviceNameIntervalMills)) {
//            serviceNameIntervalMills = DEFAULT_INTERVAL_MILLS;
//        }
//        if (mode == null) {
//            mode = DEFAULT_MODE;
//        }
//    }


    public void setServiceNameIntervalMills(Long serviceNameIntervalMills) {
        this.serviceNameIntervalMills = serviceNameIntervalMills;
    }

    public Long getServiceNameIntervalMills() {
        return serviceNameIntervalMills;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
