package com.university.backend.controller;

import com.university.backend.entity.User;
import com.university.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5175")
public class UserController {

    @Autowired
    private UserService userService;

    // 添加测试端点
    @GetMapping("/test")
    public String test() {
        return "控制器工作正常 - " + System.currentTimeMillis();
    }

    // 2. 初始化数据 - 使用POST方法，明确路径
    @PostMapping("/initialize-data")
    public ResponseEntity<String> initializeTestData() {
        try {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setEmail("test@university.com");
            user.setRealName("测试用户");
            user.setStudentId("20240001");
            user.setMajor("计算机科学");

            User savedUser = userService.createUser(user);
            return ResponseEntity.ok("测试用户创建成功！ID: " + savedUser.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("初始化失败: " + e.getMessage());
        }
    }

    // 3. 获取所有用户
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 4. 根据ID获取用户 - 使用明确的路径
    @GetMapping("/by-id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. 创建新用户
    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

}