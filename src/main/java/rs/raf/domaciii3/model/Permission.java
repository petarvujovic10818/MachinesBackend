package rs.raf.domaciii3.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Permission{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "users_permissions",
            joinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="user_id", referencedColumnName = "userID")
    )
    @JsonIgnore
    private List<User> users =new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        user.getPermissions().add(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getPermissions().remove(this);
    }

//    @Override
//    public String getAuthority() {
//        return null;
//    }
}
