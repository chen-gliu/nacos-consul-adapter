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
@Builder
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
