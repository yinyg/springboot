package tech.hiyinyougen.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import tech.hiyinyougen.springboot.async.event.TestEvent;

/**
 * @author yinyg
 * @date 2020/8/28
 * @description
 */
@Service
public class TestEventService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishEvent() {
        for (int i = 0; i < 1000; ++i) {
            eventPublisher.publishEvent(new TestEvent(this));
            System.out.println(111);
        }
    }
}
