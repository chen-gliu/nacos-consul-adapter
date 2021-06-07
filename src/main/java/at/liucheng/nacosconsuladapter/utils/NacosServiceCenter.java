package at.liucheng.nacosconsuladapter.utils;

import at.liucheng.nacosconsuladapter.listeners.ServiceChangeListener;
import at.liucheng.nacosconsuladapter.model.Result;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: liucheng
 * @createTime:2021/6/1 14:02
 */
@Slf4j
public class NacosServiceCenter {

    private Long DEFAULT_VERSION = 1L;
    private StampedLock stampedLock = new StampedLock();
    private Set<String> services = null;
    private Set<String> backServices = null;
    private ReentrantLock writeServiceLock = new ReentrantLock();
    private NacosNamingService nacosNamingService;
    //todo 定义一个listener,监听服务变更通知
    /**
     * 定义一个所有服务的变更版本记录
     */

    private Map<String, AtomicLong> serviceVersionMap = new ConcurrentHashMap<>();

    private EmitterProcessor<Result<String>> emitterProcessor = EmitterProcessor.create(3, true);

    /**
     * 消费所有热数据源数据消费者
     */
    Disposable allChangeConsumer = null;


    public NacosServiceCenter(NacosNamingService nacosNamingService) {
        this.nacosNamingService = nacosNamingService;
    }

    /**
     * 返回的集合只能做读取操作，不能做修改。修改会出现并发问题。
     *
     * @return all service name
     */
    public Set<String> getServiceNames() {
        long stamp = stampedLock.tryReadLock();
        if (stamp <= 0) {
            return backServices;
        }
        try {
            return services;
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }



    //todo 这个方法应该启动的时候自动调用
    public void initSetNames(List<String> newServiceNameList) {
        log.debug("first set services’s name");
        services = new HashSet<>(newServiceNameList);
        subscribe(services);
        //订阅热数据源实时修改数据
        allChangeConsumer = emitterProcessor.subscribe(change -> log.debug("consume service change {}:{}.", change.getData(), change.getChangeIndex()));
    }

    /**
     * 获取服务变化热数据源
     *
     * @param serviceName
     * @return
     */
    public Flux<Result<String>> getChangeHotSource(String serviceName) {
        return emitterProcessor.filter(result -> result.getData().equals(serviceName));
    }

    /**
     * 设置新的服务
     *
     * @param newServiceNameList
     */
    public void setServiceNames(List<String> newServiceNameList) {
        if (!writeServiceLock.tryLock()) {
            log.debug("未抢占到writeServiceLock");
            return;
        }
        try {
            //加锁
            if (services.containsAll(newServiceNameList)) {
                log.debug("服务未发生变化无需进行修改");
                return;
            }
            //订阅新增加的服务/取消订阅下线的服务
            backServices = services;
            long stamp = stampedLock.writeLock();
            try {
                log.debug("服务发生变化，修改缓存服务名称");
                services = new HashSet<>(newServiceNameList);
                //subscribe new service
                subscribe(services.stream().filter(e -> !backServices.contains(e)).collect(Collectors.toSet()));
            } finally {
                stampedLock.unlockWrite(stamp);
            }
            backServices = null;
        } finally {
            writeServiceLock.unlock();
        }
    }

    /**
     * 获取指定服务版本号
     *
     * @param serviceName
     * @return
     */
    public Long getServiceVersion(String serviceName) {
        Long version = serviceVersionMap.getOrDefault(serviceName, new AtomicLong(0)).get();
        log.debug("{} version is {}", serviceName, version);
        return version;
    }

    /**
     * 订阅nacos服务变更事件
     *
     * @param serviceNames
     */
    private void subscribe(Set<String> serviceNames) {
        serviceNames.stream().forEach(serviceName -> subscribe(serviceName, new ServiceChangeListener(serviceName, this)));
    }

    private void subscribe(String serviceName, EventListener listener) {
        try {
            nacosNamingService.subscribe(serviceName, listener);
            log.debug("subscribe new service: {}", serviceName);
        } catch (NacosException e) {
            log.error("{} subscribe nacos fail.fail message:{}", serviceName, e.getErrMsg());
        }
    }

    /**
     * 将变更服务推送到热数据源
     *
     * @param serviceName
     */
    public void publish(String serviceName) {
        Long version = incrementServiceVersion(serviceName);
        emitterProcessor.onNext(new Result<String>(serviceName, version));
    }

    /**
     * 增加服务变成版本号
     *
     * @param serviceName
     * @return
     */
    public Long incrementServiceVersion(String serviceName) {
        log.debug("increment {} version.", serviceName);
        return serviceVersionMap.computeIfAbsent(serviceName, x -> new AtomicLong(DEFAULT_VERSION)).incrementAndGet();
    }

    /**
     * 销毁方法
     */
    public void destroy() {
        allChangeConsumer.dispose();
    }
}
