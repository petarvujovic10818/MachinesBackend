package rs.raf.domaciii3.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Entity
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory field")
    private String name;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String dateCreated;

    @Transient
    private static boolean working = false;

    public static void setWorking(boolean working){
        Machine.working = working;
    }

    public static boolean getWorking(){
        return Machine.working;
    }

}
