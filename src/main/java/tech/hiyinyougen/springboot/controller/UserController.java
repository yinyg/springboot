package tech.hiyinyougen.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import tech.hiyinyougen.springboot.dao.UserDao;
import tech.hiyinyougen.springboot.domain.User;
import tech.hiyinyougen.springboot.model.ResultModel;
import tech.hiyinyougen.springboot.model.UserModel;
import tech.hiyinyougen.springboot.repository.UserRepository;
import tech.hiyinyougen.springboot.service.UserService;

import java.util.List;

/**
 * @Author yinyg
 * @CreateTime 2020/6/29 10:26
 * @Description
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/findAll")
    public ResultModel findAll() {
        List<UserModel> userModelList = userDao.selectAll();
        return ResultModel.builder().success(Boolean.TRUE).data(userModelList).build();
    }

    @PostMapping("save")
    public ResultModel save(@RequestBody UserModel userModel) {
        this.userService.save(userModel);
        return ResultModel.builder().success(Boolean.TRUE).build();
    }

    @GetMapping("/findAllByJpa")
    public ResultModel findAllByJpa(@PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return ResultModel.builder().success(Boolean.TRUE).data(page).build();
    }
}
