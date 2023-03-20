package com.bogdan.user.microservice.controller;

import com.bogdan.user.microservice.Service.UserService;
import com.bogdan.user.microservice.view.PlayList;
import com.bogdan.user.microservice.view.User;
import com.bogdan.user.microservice.view.dto.AllUsersView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

//    @GetMapping(value = "/userByEmail")
//    public User getUserByEmail(@RequestBody String email){
//        return userService.getUserByEmail(email);
//    }

    @PostMapping(value = "/getIdByEmail")
    public Long getIdByEmail(@RequestBody User user){
        return userService.getIdByEmail(user.getEmail());
    }

//    @GetMapping("/getIdByEmail")
//    public Long getIdByEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userEmail = authentication.getName();
//        return userService.getIdByEmail(userEmail);
//    }

    @PostMapping("createNewPlayList")
    public ResponseEntity addPlayList(@RequestBody final PlayList playlist, @RequestParam("email") final String email) {
    return userService.addPlayList(playlist, email);
    }

    @GetMapping("playlists")
    public List<PlayList> getAllPlayLists(@RequestParam("email") final String email) {
        return userService.getAllPlayLists(email);
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
