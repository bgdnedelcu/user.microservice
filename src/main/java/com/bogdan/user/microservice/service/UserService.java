package com.bogdan.user.microservice.service;

import com.bogdan.user.microservice.constants.AppConstants;
import com.bogdan.user.microservice.dao.LogsDao;
import com.bogdan.user.microservice.dao.PlayListDao;
import com.bogdan.user.microservice.dao.RegisterDao;
import com.bogdan.user.microservice.dao.UserDao;
import com.bogdan.user.microservice.exceptions.ResourceNotFoundException;
import com.bogdan.user.microservice.util.SendEmail;
import com.bogdan.user.microservice.view.*;
import com.bogdan.user.microservice.view.dto.AllUsersView;
import com.bogdan.user.microservice.view.dto.EditAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final RegisterDao registerDao;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final SendEmail sendEmail;
    private final LogsDao logsDao;
    private final PlayListDao playListDao;
    private final UtilityService utilityService;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder, SendEmail sendEmail,
                       RegisterDao registerDao, LogsDao logsDao, PlayListDao playListDao, UtilityService utilityService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.sendEmail = sendEmail;
        this.registerDao = registerDao;
        this.logsDao = logsDao;
        this.playListDao = playListDao;
        this.utilityService = utilityService;
    }

    public long getIdByEmail(final String email) {
        User user = userDao.findFirstIdByEmail(email).orElseThrow(() -> new UsernameNotFoundException("kh"));
        return user.getId();
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = userDao.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Contul nu a fost gasit!");
        }

        final Logs log = new Logs();
        log.setEmail(email);
        log.setActionDesc("A incercat sa se autentifice");
        log.setTimeStamp(new Date());
        logsDao.save(log);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }


    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User getUserById(final Long id) throws ResourceNotFoundException {
        Optional<User> user = userDao.findById(id);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Nu am gasit id-ul");
        }
        return user.get();

    }

    public String getPlaylistTitleByPlaylistId(final Long id){
        Optional<PlayList> playList = playListDao.findById(id);
        return playList.get().getTitle();
    }

    //to remove
    public List<AllUsersView> getAllUsersInfo() {
        List<AllUsersView> allUsersList = new ArrayList<>();
        List<User> userList = userDao.findAll();

        for (User user : userList) {
            AllUsersView allUsersView = new AllUsersView();
            allUsersView.setId(user.getId());
            allUsersView.setEmail(user.getEmail());
            allUsersView.setRole(user.getRole().getRoleName());
            allUsersView.setChannelName(user.getChannelName());
            allUsersList.add(allUsersView);
        }
        return allUsersList;
    }

    public ResponseEntity createAccount(User user) {
        if (userDao.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Account already exists!");
        }
        User newAccount = new User();
        newAccount.setEmail(user.getEmail());
        newAccount.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug(user.getPassword());
        newAccount.setChannelName(user.getChannelName());

        final Role role = new Role();
        role.setId(1);
        role.setRoleName("user");

        final RegisterCode registerCode = new RegisterCode();
        registerCode.setUsed(0);
        registerCode.setDate(new Date());
        newAccount.setRegisterCode(registerCode);
        newAccount.setRole(role);

        log.debug("asdf: {}", AppConstants.REGISTER_CODE_BASE.concat(String.valueOf(newAccount.getRegisterCode().getId())));
        newAccount = userDao.save(newAccount);

        newAccount.getRegisterCode().setRegisterKey(AppConstants.REGISTER_CODE_BASE.concat(String.valueOf(newAccount.getRegisterCode().getId())));
        userDao.save(newAccount);

        final String registerCodeKey = newAccount.getRegisterCode().getRegisterKey();
        final String email = newAccount.getEmail();

        final Logs log = new Logs();
        log.setEmail(email);
        log.setActionDesc("S-a creat un cont nou");
        log.setTimeStamp(new Date());
        logsDao.save(log);
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            sendEmail.sendRegisterEmail(registerCodeKey, email);
        });
        executorService.shutdown();

        return ResponseEntity.ok().body("Contul a fost inregistrat cu succes");
    }

    public ResponseEntity finishRegistration(final String key) {
        final RegisterCode registerCode = registerDao.findByRegisterKey(key);
        if (registerCode == null || registerCode.getUsed() == 1) {
            return ResponseEntity.badRequest().body("Codul nu exista sau a fost validat deja!");
        }

        registerCode.setUsed(1);

        registerDao.save(registerCode);
        return ResponseEntity.ok().body("Contul a fost creat cu succes!");
    }

    public ResponseEntity addPlayList(final PlayList playList, final String email) {
        final User user = userDao.findByEmail(utilityService.getEmailFromToken());
        playList.setUser(user);
        playList.setTitle(playList.getTitle());
        playListDao.save(playList);

        final Logs log = new Logs();
        log.setEmail(user.getEmail());
        log.setActionDesc("A creat un nou playlist");
        log.setTimeStamp(new Date());
        logsDao.save(log);

        return ResponseEntity.ok("Succes");
    }

    public List<PlayList> getAllPlayListsByEmail(final String email) {
        final User user = userDao.findByEmail(email);
        return new ArrayList<>(user.getPlayListSet());
    }

    public List<PlayList> getPlayListsByUserId(final Long userId) throws ResourceNotFoundException {
        final Optional<User> user = userDao.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Nu am gasit user-ul");
        }
        return new ArrayList<>(user.get().getPlayListSet());
    }

    public String getChannelNameByEmail(String email) throws ResourceNotFoundException {
        final Optional<User> user = userDao.findChannelNameByEmail(utilityService.getEmailFromToken());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Nu am gasit user-ul");
        }
        return user.get().getChannelName();
    }

    public String getChannelNameByUserId(Long id) throws ResourceNotFoundException {
        final Optional<User> user = userDao.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Nu am gasit user-ul");
        }
        return user.get().getChannelName();
    }

    public ResponseEntity deletePlaylist(Long idPlaylist) {
        try {
            playListDao.deleteById(idPlaylist);
            String message = "The playlist with ID " + idPlaylist + " was deleted";
            return ResponseEntity.ok().body(message);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Long getIdByChannelName(final String channelName) {
        final Optional<User> user = userDao.findIdByChannelName(channelName);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Not working");
        }
        return user.get().getId();
    }

    public ResponseEntity updatePlaylistTitle(final Long idPlaylist, final String newTitle) {
        Optional<PlayList> optionalPlayList = playListDao.findById(idPlaylist);
        if (optionalPlayList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PlayList playList = optionalPlayList.get();
        playList.setTitle(newTitle);
        playListDao.save(playList);
        return ResponseEntity.ok().body("The title has been changed");
    }

    public ResponseEntity updateAccount(final EditAccount editAccount) {
        User user = userDao.findByEmail(utilityService.getEmailFromToken());
        log.debug(user.getEmail());
        log.debug(user.getPassword());
        log.debug(editAccount.getNewPassword());
        if (passwordEncoder.matches(editAccount.getCurrentPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(editAccount.getNewPassword()));
            user.setChannelName(editAccount.getNewChannelName());
            userDao.save(user);
            return ResponseEntity.ok().body("Succes");
        }
        return ResponseEntity.badRequest().body("Not ok");
    }

}
