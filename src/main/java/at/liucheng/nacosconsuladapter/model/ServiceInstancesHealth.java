package at.liucheng.nacosconsuladapter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @description:consul health class
 * @author: lc
 * @createDate: 2021/5/31
 */
@Builder
@Getter
@ToString
public class ServiceInstancesHealth {
    @JsonProperty("Node")
    private Node node;

    @JsonProperty("Service")
    private Service service;

    @JsonProperty("Checks")
    private List<Check> checks;

    @Getter
    @Builder
    public static class Node {

        @JsonProperty("ID")
        private String id;

        @JsonProperty("Address")
        //nacos默认注册为内网IP
        private String address;

        @JsonProperty("Datacenter")
        private String dataCenter;
    }

    @Getter
    @Builder
    public static class Service {

        @JsonProperty("ID")
        private String id;

        @JsonProperty("Service")
        private String service;

        @JsonProperty("Port")
        private int port;
    }

    @Getter
    @Builder
    public static class Check {

        @JsonProperty("Node")
        private String node;

        @JsonProperty("CheckID")
        private String checkID;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Status")
        private String status;
    }
}
