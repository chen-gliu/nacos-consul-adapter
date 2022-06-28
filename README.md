# Nacos Consul Adapter(for Prometheus)
Prometheus官方提供Consul为注册中心的配置方式，配置后可自动获取Consul中所有实例的信息并进行监控。  如果想使用Prometheus监控使用其他注册中心的服务就需要一些额外的适配，适配的目的是让项目提供Consul服务器相同接口。  
本项目是以Nacos为注册中心的适配器，供Prometheus获取注册中心的所有实例信息。本项目受<a href='https://github.com/twinformatics/eureka-consul-adapter'>Eureka Consul Adapter</a>项目的启发，实现了<a href = 'https://github.com/chen-gliu/nacos-consul-adapter'>Nacos Consul Adapter</a>。在Eureka的适配实现中作者使用的是Rx-Java,本系统是使用Reactor3实现和Spring Cloud GateWay的技术栈一致。之前也有<a href='https://github.com/yueyemisi/nacos-consul-adapter'>Nacos Consul Adapter</a>的实现在GitHub开源，但是由于Consul在某个版本修改了健康检测接口的路径，所以在高版本的Prometheus就无法使用了，并且<a href = 'https://github.com/yueyemisi/nacos-consul-adapter'>之前的版本</a>也未提供长轮询实现。  

## 特性
项目一共只有四个接口:  
1. /v1/agent/self：返回数据中心的名称。这个接口暂时返回固定内容，未提供配置。
2. /v1/catalog/services：返回注册中心所有服务名称。
3. /v1/health/service/{appName}：返回指定名称服务的所有实例。
4. /v1/catalog/service/{appName}：返回指定名称服务的所有实例，兼容早期的Prometheus版本。

## 长轮询和直接查询  
本项目提供两种模式请求接口：`直接查询和长轮询`  
*直接查询：* Prometheus调用本项目，本项目直接请求Nacos服务端获取内容。  
*长轮询：* 本项目使用Reactor3来实现长轮询，在指定的等待时间内如果注册信息发生变化会立即返回否则会一直等待直到指定时间流逝。在这种模式下不会直接调用Nacos服务器，而是请求的本地Nacos客户端的内容。  

可以将本项目引入到Spring Cloud GateWay,在Prometheus中指定网关地址。  

## 配置选项
`nacos-consul-adapter.mode`：使用模式。`direct`：直接查询，`long-polling`：长轮询。默认为长轮询。  
`nacos-consul-adapter.serviceNameIntervalMills`:在长轮询模式下，请求服务名称的间隔时间。  


## 要求
JDK 1.8+  
spring-cloud-starter-alibaba-nacos-discovery 2.2+  
spring-boot 2.3+  

较低版本Spring Boot未做测试


## 分支版本对应关系
项目维护了适配`spring-cloud-starter-alibaba-nacos-discovery`的低版本分支, 关系如下。

|          分支           | spring-cloud-starter-alibaba-nacos-discovery | nacos  |
|:---------------------:|----------------------------------------------|--------|
|        0.0.5.M        | 2.2+                                         | 1.4.1+ |
| 0.0.5.M-2.2.0.RELEASE | 2.2.0.RELEASE                                | 1.2.1+ |



## 快速开始  
可以在Spring Cloud Gateway中引入下面的jar包（可以是服务中任意的节点，在Spring Cloud Gateway中原因是，它基于Reactor。如果不是使用Spring WebFlux则还需要引入额外的包），然后在Prometheus中配置Spring Cloud Gateway的一个实例的ip地址就可以了。
项目的开源地址为：https://github.com/chen-gliu/nacos-consul-adapter。对本项目有任何问题和优化建议都可以提issue。
```
        <dependency>
            <groupId>io.github.chen-gliu</groupId>
            <artifactId>nacos-consul-adapter</artifactId>
            <version>version</version>
        </dependency> 
```
如果拉取不到项目，可以在setting文件中添加如下配置:
```$xslt
      <mirror>
		<id>mvnrepository</id>
		<mirrorOf>*</mirrorOf>
		<name>仓库</name>
		<url>https://repo1.maven.org/maven2</url>
	</mirror>
```
在Prometheus中添加如下配置:  
```
 - job_name: 'consul-prometheus-test'
    metrics_path: '/actuator/prometheus'
    consul_sd_configs:
    - server: 引入nacos-consul-adapter实例的ip+端口
      services: []
```
在每个Spring Cloud实例中引入Prometheus监控包：
```
 <dependency>
       <groupId>io.micrometer</groupId>
       <artifactId>micrometer-registry-prometheus</artifactId>
  </dependency>
```
```java
@Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }
```
这样简单的配置后，启动服务就可以在Prometheus中看到注册在Nacos中的服务了。
![Prometheus实际效果图](https://img-blog.csdnimg.cn/20210626171800141.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0xDQlVTSElIQUhB,size_16,color_FFFFFF,t_70)
![Grafana实际效果图](https://img-blog.csdnimg.cn/20210626172040746.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0xDQlVTSElIQUhB,size_16,color_FFFFFF,t_70)
## 感谢  
感谢<a href='https://github.com/twinformatics/eureka-consul-adapter'>Eureka Consul Adapter</a>项目开发人员，项目中部分代码借鉴了<a href='https://github.com/twinformatics/eureka-consul-adapter'>Eureka Consul Adapter</a>的实现。

