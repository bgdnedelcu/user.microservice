package com.bogdan.user.microservice.controller;

import com.bogdan.user.microservice.Service.UserService;
import com.bogdan.user.microservice.exceptions.ResourceNotFoundException;
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

    @PostMapping(value = "/getIdByEmail")
    public Long getIdByEmail(@RequestBody User user){
        return userService.getIdByEmail(user.getEmail());
    }


    private String getEmailFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug(authentication.getName());
        return authentication.getName();
    }

    @PostMapping("createNewPlayList")
    public ResponseEntity addPlayList(@RequestBody final PlayList playlist) {
    return userService.addPlayList(playlist, getEmailFromToken());
    }

    @GetMapping("playlists")
    public List<PlayList> getAllPlayLists(final String email) {
        return userService.getAllPlayLists(getEmailFromToken());
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

    @GetMapping("channelName")
    public String getChannelNameByEmail(final String email) throws ResourceNotFoundException {
        return userService.getChannelNameByEmail(getEmailFromToken());
    }

    @GetMapping("userById/{id}")
    public User getUserById(@PathVariable("id") final Long id) throws ResourceNotFoundException {
        return userService.getUserById(id);
    }

    @GetMapping("channelNameById/{id}")
    public String getChannelNameByUserId(@PathVariable("id") final Long id) throws ResourceNotFoundException{
        return userService.getChannelNameByUserId(id);
    }

}
