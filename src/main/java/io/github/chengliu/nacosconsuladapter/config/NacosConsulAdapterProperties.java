package io.github.chengliu.nacosconsuladapter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author lc
 * @createTime 2021/6/1 9:58
 */
@ConfigurationProperties(prefix = "nacos-consul-adapter")
public class NacosConsulAdapterProperties {
    private Long DEFAULT_INTERVAL_MILLS = TimeUnit.SECONDS.toMillis(5);
    private Long serviceNameIntervalMills = DEFAULT_INTERVAL_MILLS;
    public static String LONG_POLLING_MODE = "long-polling";
    public static String DIRECT_MODE = "direct";
    public static String DEFAULT_MODE = LONG_POLLING_MODE;
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
