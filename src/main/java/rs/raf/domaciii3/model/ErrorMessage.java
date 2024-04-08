package rs.raf.domaciii3.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Entity
public class ErrorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Machine machine;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @NotBlank(message = "Operation is mandatory field")
    private String operationFailed;

    @Column(nullable = false)
    @NotBlank(message = "Message is mandatory field")
    private String messageError;

    @Column(nullable = false)
    private String dateError;

}
