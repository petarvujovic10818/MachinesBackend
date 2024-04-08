package rs.raf.domaciii3.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rs.raf.domaciii3.model.Permission;
import rs.raf.domaciii3.model.User;
import rs.raf.domaciii3.requests.LoginRequest;
import rs.raf.domaciii3.responses.LoginResponse;
import rs.raf.domaciii3.services.UserService;
import rs.raf.domaciii3.utils.JwtUtil;

import javax.validation.Valid;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //post request za login, vraca se jwt
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (Exception   e){
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }

        UserDetails optionalUser = userService.loadUserByUsername(loginRequest.getUsername());

        List<GrantedAuthority> authorities = new ArrayList<>(optionalUser.getAuthorities());
        List<String> fixedList = new ArrayList<>();


        for(int i=0;i<authorities.size();i++){
            if(authorities.get(i).toString().equals("can_read_users")){
                System.out.println("User ima permisiju da cita ostale usere");
                fixedList.add("can_read_users");
            }
            if(authorities.get(i).toString().equals("can_create_users")){
                System.out.println("User ima permisiju da dodaje ostale usere");
                fixedList.add("can_create_users");
            }
            if(authorities.get(i).toString().equals("can_update_users")){
                System.out.println("User ima permisiju da menja ostale usere");
                fixedList.add("can_update_users");
            }
            if(authorities.get(i).toString().equals("can_delete_users")){
                System.out.println("User ima permisiju da brise ostale usere");
                fixedList.add("can_delete_users");
            }
            if(authorities.get(i).toString().equals("can_search_machines")){
                fixedList.add("can_search_machines");
            }
            if(authorities.get(i).toString().equals("can_create_machines")){
                fixedList.add("can_create_machines");
            }
            if(authorities.get(i).toString().equals("can_destroy_machines")){
                fixedList.add("can_destroy_machines");
            }
            if(authorities.get(i).toString().equals("can_start_machines")){
                fixedList.add("can_start_machines");
            }
            if(authorities.get(i).toString().equals("can_stop_machines")){
                fixedList.add("can_stop_machines");
            }
            if(authorities.get(i).toString().equals("can_restart_machines")){
                fixedList.add("can_restart_machines");
            }

        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions",fixedList);

//        for(int i=0;i<authorities.size();i++){
//            System.out.println(authorities.get(i) + " je cool");
//            claims.put(authorities.get(i).toString(),authorities.get(i).toString());
//        }


        return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(loginRequest.getUsername(), claims)));
    }

}
