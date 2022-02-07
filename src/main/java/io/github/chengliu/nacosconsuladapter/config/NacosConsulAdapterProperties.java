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
package io.github.chengliu.nacosconsuladapter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 *
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
