package rs.raf.domaciii3.services;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.raf.domaciii3.model.Permission;
import rs.raf.domaciii3.model.User;
import rs.raf.domaciii3.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserService implements IService<User, Long>, UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public <S extends User> S save(S user) { //add user
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long userID) { //find by id
        return userRepository.findById(userID);
    }

    @Override
    public List<User> findAll() { //get users
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void deleteById(Long userID) {
        userRepository.deleteById(userID);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username){
        User myUser = this.userRepository.findByUsername(username);
        if(myUser == null) {
            throw new UsernameNotFoundException("User: "+username+" not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for(int i=0; i<myUser.getPermissions().size(); i++){
            authorities.add(new SimpleGrantedAuthority(myUser.getPermissions().get(i).getName()));
        }

        return new org.springframework.security.core.userdetails.User(myUser.getUsername(),myUser.getPassword(), authorities);
    }

    public User findUserByUsername(String username){
        return this.userRepository.findByUsername(username);
    }


}
