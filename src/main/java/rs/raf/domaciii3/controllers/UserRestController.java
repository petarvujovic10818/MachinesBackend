package rs.raf.domaciii3.controllers;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rs.raf.domaciii3.model.User;
import rs.raf.domaciii3.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_read_users"))){
                return ResponseEntity.ok(userService.findAll());
            }else {
                return ResponseEntity.status(403).build();
            }
        //return userService.findAll();
    }
    //staviti ovde da mora da ima read perm?
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){ //bilo RequestParam("userId") ?userId=1
        Optional<User> optionalUser = userService.findById(id);
        if(optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //read permission ?
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value="/u/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username){
        User myUser = userService.findUserByUsername(username);
        if(myUser==null){
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(myUser);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody User user){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_create_users"))){
            return ResponseEntity.ok(userService.save(user));
        }else {
            return ResponseEntity.status(403).build();
        }
        //return userService.save(user);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody User user){
        System.out.println("updating");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_update_users"))){
            return ResponseEntity.ok(userService.save(user));
        }else {
            return ResponseEntity.status(403).build();
        }
        //return userService.save(user);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_delete_users"))){
            Optional<User> optionalUser = userService.findById(id);
            if(optionalUser.isPresent()) {
                userService.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }else {
            return ResponseEntity.status(403).build();
        }
    }

}
