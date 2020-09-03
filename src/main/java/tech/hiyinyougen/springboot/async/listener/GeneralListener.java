package tech.hiyinyougen.springboot.async.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tech.hiyinyougen.springboot.async.event.TestEvent;

/**
 * @Author yinyg
 * @CreateTime 2020/6/29 15:20
 * @Description
 */
@Component
@Slf4j
public class GeneralListener {
    @Async
    @EventListener
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if (event instanceof TestEvent) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
            }
        } catch (Exception e) {
            log.warn(ExceptionUtils.getFullStackTrace(e));
        }
    }
}
