package rs.raf.domaciii3.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory field")
    private String name;

    @NotBlank(message = "Surname is mandatory field")
    @Column(nullable = false)
    private String surname;

    @NotBlank(message = "Username is mandatory field")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is mandatory field")
    private String password;

    @ManyToMany
    @JoinTable(
            name = "users_permissions",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userID"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private List<Permission> permissions = new ArrayList<>();

    //@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    //private List<Machine> machines  = new ArrayList<>();

}
