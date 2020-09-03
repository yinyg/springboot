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
}
