package rs.raf.domaciii3.repositories;

import org.springframework.data.repository.CrudRepository;
import rs.raf.domaciii3.model.Permission;

public interface PermissionRepository extends CrudRepository<Permission, Long> {
}
