# nacos consul adapter(for Prometheus)
由于Prometheus只提供了consul为注册中心的配置方式，如果想使用prometheus监控使用其他注册中心的服务就需要适配。本项目受Eureka Consul Adapter项目的启发，实现了Nacos Consul Adapter。
在Eureka的适配实现中作者使用的是Rx-Java,本系统是使用Reactor3实现和Spring Cloud GateWay的技术栈一致。
只前也有Nacos Consul Adapter的实现在GitHub开源，但是由于Consul在某个版本修改了健康检测接口的路径，所以在高版本的Prometheus就无法使用了。之前的版本也未提供长轮询实现。

## 特性
项目一共只有三个接口:  
1. /v1/agent/self：返回数据中心的名称。这个接口暂时返回固定内容，未提供配置。
2. /v1/catalog/services：返回注册中心所有服务名称。
3. /v1/catalog/service/{service}：返回指定名称服务的所有实例。


本项目提供两种模式请求接口：直接查询和长轮询。
直接查询：Prometheus调用本项目，本项目直接请求Nacos服务端获取内容。
长轮询：本项目使用Reactor3来实现长轮询，在指定的等待时间内如果注册信息发生变化会立即返回否则会一直等待直到指定时间流逝。在这种模式下不会直接调用Nacos服务器，而是请求的本地Nacos客户端的内容。

可以将本项目引入到Spring Cloud GateWay,在Prometheus中指定网关地址。

## 配置选项
nacos-consul-adapter.mode：使用模式。direct：直接查询，long-polling：长轮询。默认为长轮询。
nacos-consul-adapter.serviceNameIntervalMills:在长轮询模式下，请求服务名称的间隔时间。  

## 要求

JDK 1.8+
spring-cloud-starter-alibaba-nacos-discovery 2.2+
spring-boot 2.3+
