package tech.hiyinyougen.springboot.async.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author yinyg
 * @date 2020/8/28
 * @description
 */
public class TestEvent extends ApplicationEvent {
    public TestEvent(Object source) {
        super(source);
    }
}
