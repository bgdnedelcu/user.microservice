package com.bogdan.user.microservice.controller;

import com.bogdan.user.microservice.Service.UserService;
import com.bogdan.user.microservice.exceptions.ResourceNotFoundException;
import com.bogdan.user.microservice.view.User;
import com.bogdan.user.microservice.view.dto.AllUsersView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("videoplatform/api/account")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/allAccounts")
    public List<User> getAllUsers(){
        return userService.getListOfUsers();
    }

    @GetMapping("/userByEmail")
    public User getUserByEmail(@RequestBody String email){
        return userService.getUserByEmail(email);
    }

    @GetMapping("userByID/{id}")
    public ResponseEntity getUserById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/allUsersInfo")
    public List<AllUsersView> allUsersViewList(){
        return userService.getAllUsersInfo();
    }

    @PostMapping("/register")
    public ResponseEntity createAccount(@RequestBody User user){
        return userService.createAccount(user);
    }

    @GetMapping("finishregistration/{key}")
    public ResponseEntity finishRegistration(@PathVariable("key") final String key) {
        return userService.finishRegistration(key);
    }

}
