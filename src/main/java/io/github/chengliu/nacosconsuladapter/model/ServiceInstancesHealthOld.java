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
package io.github.chengliu.nacosconsuladapter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @description:
 * @author: liucheng
 * @createTime:2022/6/21 19:22
 */
@Getter
@ToString
public class ServiceInstancesHealthOld {

    @JsonProperty("ID")
    private String id;

    /**
     * nacos的节点名称
     */
    @JsonProperty("Node")
    private String node;

    /**
     * nacos的ip地址
     */
    @JsonProperty("Address")
    private String address;

    @JsonProperty("Datacenter")
    private String dataCenter;

    @JsonProperty("ServiceId")
    private String serviceId;

    @JsonProperty("ServiceName")
    private String serviceName;

    @JsonProperty("ServicePort")
    private Integer servicePort;

    @JsonProperty("ServiceAddress")
    private String serviceAddress;


    public ServiceInstancesHealthOld(ServiceInstancesHealth serviceInstancesHealth) {
        this.id = serviceInstancesHealth.getNode().getId();
        this.dataCenter = serviceInstancesHealth.getNode().getDataCenter();
        this.node = "nacos";
        this.address = serviceInstancesHealth.getNode().getAddress();

        this.serviceId = serviceInstancesHealth.getService().getId();
        this.serviceAddress = serviceInstancesHealth.getNode().getAddress();
        this.servicePort = serviceInstancesHealth.getService().getPort();
        this.serviceName = serviceInstancesHealth.getService().getService();
    }
}
