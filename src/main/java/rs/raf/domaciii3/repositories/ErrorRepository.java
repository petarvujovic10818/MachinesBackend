package rs.raf.domaciii3.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.raf.domaciii3.model.ErrorMessage;
import rs.raf.domaciii3.model.Machine;
import rs.raf.domaciii3.model.Permission;

import java.util.List;

@Repository
public interface ErrorRepository extends CrudRepository<ErrorMessage, Long> {

    @Query(value = "SELECT * FROM error_message e where e.user_id = ?1", nativeQuery = true)
    List<ErrorMessage> findAllByUserId(Long userId);

}
