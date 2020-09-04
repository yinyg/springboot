package tech.hiyinyougen.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.hiyinyougen.springboot.config.RabbitMQConfig;
import tech.hiyinyougen.springboot.model.ResultModel;
import tech.hiyinyougen.springboot.model.UserModel;
import tech.hiyinyougen.springboot.util.SendMqUtils;

/**
 * @author yinyg
 * @date 2020/7/13
 * @description
 */
@RestController
@RequestMapping("/mq")
public class MqController {
    @Autowired
    private SendMqUtils sendMqUtils;

    @GetMapping("/publishMqTest")
    public ResultModel publishMqTest() {
        UserModel userModel = UserModel.builder().username("yinyg").age(18).build();
        sendMqUtils.publish(RabbitMQConfig.EXCHANGE, RabbitMQConfig.QUEUE, userModel);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }

    @GetMapping("/publishMqTestWithTransaction")
    public ResultModel publishMqTestWithTransaction() {
        UserModel userModel = UserModel.builder().username("yinyg").age(18).build();
        sendMqUtils.publishWithTransaction(RabbitMQConfig.EXCHANGE, RabbitMQConfig.QUEUE, userModel);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }

    @GetMapping("/publishMqTestWithConfirm")
    public ResultModel publishMqTestWithConfirm() {
        UserModel userModel = UserModel.builder().username("yinyg").age(18).build();
        sendMqUtils.publishWithConfirm(RabbitMQConfig.EXCHANGE, RabbitMQConfig.QUEUE, userModel);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }

    @GetMapping("/publishMqTestWithAsyncConfirm")
    public ResultModel publishMqTestWithAsyncConfirm() {
        UserModel userModel = UserModel.builder().username("yinyg").age(18).build();
        sendMqUtils.publishWithAsyncConfirm(RabbitMQConfig.EXCHANGE, RabbitMQConfig.QUEUE, userModel);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }

    @GetMapping("/publishMqTestWithPersistent")
    public ResultModel publishMqTestWithPersistent() {
        UserModel userModel = UserModel.builder().username("yinyg").age(18).build();
        sendMqUtils.publishWithPersistent(RabbitMQConfig.EXCHANGE, RabbitMQConfig.QUEUE, userModel);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }

    /**
     * @description 延时队列实现一
     * @return tech.hiyinyougen.springboot.model.ResultModel
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    @GetMapping("/publishMqTestWithDLX")
    public ResultModel publishMqTestWithDLX() {
        UserModel userModel = UserModel.builder().username("yinyg").age(18).build();
        UserModel userModel2 = UserModel.builder().username("zhangsan").age(20).build();
        sendMqUtils.publishMqTestWithDLX(RabbitMQConfig.EXCHANGE, RabbitMQConfig.TTLQUEUE, userModel, 1000 * 60);
        sendMqUtils.publishMqTestWithDLX(RabbitMQConfig.EXCHANGE, RabbitMQConfig.TTLQUEUE, userModel2, 1000 * 30);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }

    /**
     * @description 延时队列实现一
     * @return tech.hiyinyougen.springboot.model.ResultModel
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    @GetMapping("/publishMqTestWithDelayedMessageQueue")
    public ResultModel publishMqTestWithDelayedMessageQueue() {
        UserModel userModel = UserModel.builder().username("yinyg").age(18).build();
        UserModel userModel2 = UserModel.builder().username("zhangsan").age(20).build();
        sendMqUtils.publishMqTestWithDelayedMessageQueue(RabbitMQConfig.DELAYEDMESSAGEEXCHANGE, RabbitMQConfig.DELAYEDMESSAGEQUEUE, userModel, 1000 * 60);
        sendMqUtils.publishMqTestWithDelayedMessageQueue(RabbitMQConfig.DELAYEDMESSAGEEXCHANGE, RabbitMQConfig.DELAYEDMESSAGEQUEUE, userModel2, 1000 * 30);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }
}
