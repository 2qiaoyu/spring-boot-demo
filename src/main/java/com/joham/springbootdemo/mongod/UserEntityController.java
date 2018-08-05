package com.joham.springbootdemo.mongod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * mongodb测试
 *
 * @author joham
 */
@RestController
@RequestMapping("/user")
public class UserEntityController {

    @Autowired
    private UserEntityService userService;

    /**
     * 添加
     *
     * @throws Exception
     */
    @RequestMapping("/add")
    public String testSaveUser(@Valid @RequestBody UserEntity userEntity) {
        userService.saveUser(userEntity);
        return "1";
    }

    /**
     * 查询
     */
    @RequestMapping("/find")
    public ResponseEntity<List<UserEntity>> findUserByUserName() {
//        UserEntity user = userService.findUserByUserName("小明");
        List<UserEntity> userEntityList = userService.find();
        return ResponseEntity.ok(userEntityList);
    }

    @RequestMapping("/find1")
    public ResponseEntity<List<UserEntity>> find1() {
        List<UserEntity> userEntityList = userService.find1();
        return ResponseEntity.ok(userEntityList);
    }

    @RequestMapping("/find2")
    public ResponseEntity<List<UserEntity>> find2() {
        List<UserEntity> userEntityList = userService.find2();
        return ResponseEntity.ok(userEntityList);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public void updateUser() {
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setUserName("天空");
        user.setPassWord("fffxxxx");
        userService.updateUser(user);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public void deleteUserById() {
        userService.deleteUserById(1L);
    }
}
