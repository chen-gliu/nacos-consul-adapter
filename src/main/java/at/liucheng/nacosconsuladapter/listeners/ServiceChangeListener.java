package at.liucheng.nacosconsuladapter.listeners;

import at.liucheng.nacosconsuladapter.utils.NacosServiceCenter;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.utils.NamingUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author lc
 * @description
 * @createDate 2021/5/29
 */
@Slf4j
public class ServiceChangeListener implements EventListener {
    private NacosServiceCenter nacosServiceCenter;
    private String serviceName;

    public ServiceChangeListener(String serviceName, NacosServiceCenter nacosServiceCenter) {
        this.nacosServiceCenter = nacosServiceCenter;
        this.serviceName = serviceName;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof NamingEvent) {
            NamingEvent namingEvent = (NamingEvent) event;
            log.debug("receive {} service change event.", ((NamingEvent) event).getServiceName());
            nacosServiceCenter.publish(NamingUtils.getServiceName(namingEvent.getServiceName()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceChangeListener that = (ServiceChangeListener) o;
        return serviceName.equals(that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName);
    }
}
