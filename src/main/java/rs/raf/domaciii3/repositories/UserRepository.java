package rs.raf.domaciii3.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.raf.domaciii3.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String email);
}
