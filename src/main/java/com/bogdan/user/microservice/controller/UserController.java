package com.bogdan.user.microservice.controller;

import com.bogdan.user.microservice.exceptions.ResourceNotFoundException;
import com.bogdan.user.microservice.service.UserService;
import com.bogdan.user.microservice.service.UtilityService;
import com.bogdan.user.microservice.view.PlayList;
import com.bogdan.user.microservice.view.User;
import com.bogdan.user.microservice.view.dto.EditAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("videoplatform/api/account")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UtilityService utilityService;

    public UserController(UserService userService, UtilityService utilityService) {
        this.userService = userService;
        this.utilityService = utilityService;
    }

    @GetMapping("finishregistration/{key}")
    public ResponseEntity finishRegistration(@PathVariable("key") final String key) {
        return userService.finishRegistration(key);
    }

    @GetMapping("channelName")
    public String getChannelNameByEmail(final String email) throws ResourceNotFoundException {
        return userService.getChannelNameByEmail(utilityService.getEmailFromToken());
    }

    @GetMapping("userById/{id}")
    public User getUserById(@PathVariable("id") final Long id) throws ResourceNotFoundException {
        return userService.getUserById(id);
    }

    @GetMapping("channelNameById/{id}")
    public String getChannelNameByUserId(@PathVariable("id") final Long id) throws ResourceNotFoundException {
        return userService.getChannelNameByUserId(id);
    }

    @GetMapping("getIdByChannelName/{channelName}")
    public ResponseEntity<Long> getIdByChannelName(@PathVariable("channelName") final String channelName) {
        return ResponseEntity.ok(userService.getIdByChannelName(channelName));
    }

    @GetMapping("playlistsByEmailFromToken")
    public List<PlayList> getAllPlayLists(final String email) {
        return userService.getAllPlayListsByEmail(utilityService.getEmailFromToken());
    }

    @GetMapping("playlistsByUserId/{userId}")
    public List<PlayList> getPlayListsByUserId(@PathVariable("userId") final Long userId) throws ResourceNotFoundException {
        return userService.getPlayListsByUserId(userId);
    }

    @GetMapping("playlistTitleByPlaylistId/{playlistId}")
    public String getPlaylistTitleByPlaylistId(@PathVariable("playlistId") final Long playlistId) {
        return userService.getPlaylistTitleByPlaylistId(playlistId);
    }

    @PostMapping(value = "getIdByEmail")
    public Long getIdByEmail(@RequestBody User user) {
        return userService.getIdByEmail(user.getEmail());
    }

    @PostMapping("createNewPlayList")
    public ResponseEntity addPlayList(@RequestBody final PlayList playlist) {
        return userService.addPlayList(playlist, utilityService.getEmailFromToken());
    }

    @PostMapping("register")
    public ResponseEntity createAccount(@RequestBody User user) {
        return userService.createAccount(user);
    }

    @PutMapping("editPlaylistTitle/{id}")
    public ResponseEntity updatePlaylistTitle(@PathVariable final Long id, @RequestParam final String title) {
        return userService.updatePlaylistTitle(id, title);
    }

    @PutMapping("updateAccount")
    public ResponseEntity updateAccount(@RequestBody final EditAccount editAccount) {
        return userService.updateAccount(editAccount);
    }

    @DeleteMapping("deletePlaylistById/{id}")
    public ResponseEntity deletePlayListById(@PathVariable("id") final Long id) {
        return userService.deletePlaylist(id);
    }

}
