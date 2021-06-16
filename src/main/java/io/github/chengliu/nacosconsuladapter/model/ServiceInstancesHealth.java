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
