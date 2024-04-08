package rs.raf.domaciii3.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.raf.domaciii3.model.Machine;
import rs.raf.domaciii3.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MachineRepository extends CrudRepository<Machine, Long> {
    @Query(value = "SELECT * FROM Machine m where m.user_userid = ?1", nativeQuery = true)
    List<Machine> findAllByUser(Long userId);

    @Query(value="SELECT * FROM Machine m where m.name LIKE %?1% and m.status LIKE %?2% and m.date_created BETWEEN ?3 AND ?4", nativeQuery = true)
    List<Machine> findMachinesByName(String name, String status, String dateFrom, String dateTo);

    @Modifying
    @Transactional
    @Query(value="UPDATE Machine m SET m.active = false where m.id = ?1", nativeQuery = true)
    void removeMachine(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Machine m SET m.status = 'RUNNING' where m.id = ?1", nativeQuery = true)
    void startMachine(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Machine m SET m.status = 'STOPPED' where m.id = ?1", nativeQuery = true)
    void stopMachine(Long id);

}
