package rs.raf.domaciii3.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.raf.domaciii3.model.Machine;
import rs.raf.domaciii3.model.Permission;
import rs.raf.domaciii3.model.User;
import rs.raf.domaciii3.repositories.MachineRepository;
import rs.raf.domaciii3.repositories.PermissionRepository;
import rs.raf.domaciii3.repositories.UserRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final MachineRepository machineRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder, PermissionRepository permissionRepository, MachineRepository machineRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.machineRepository = machineRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading data...");

        //all perms
        User user1 = new User();
        user1.setName("Petar");
        user1.setSurname("Petrovic");
        user1.setUsername("pera@raf.rs");
        user1.setPassword(this.passwordEncoder.encode("pera1234"));
        this.userRepository.save(user1);

        //read
        User user2 = new User();
        user2.setName("Milan");
        user2.setSurname("Milanovic");
        user2.setUsername("mica@raf.rs");
        user2.setPassword(this.passwordEncoder.encode("mica1234"));
        this.userRepository.save(user2);

        //create + read
        User user3 = new User();
        user3.setName("Jovan");
        user3.setSurname("Jovanovic");
        user3.setUsername("jova@raf.rs");
        user3.setPassword(this.passwordEncoder.encode("jova1234"));
        this.userRepository.save(user3);

        //update + read
        User user4 = new User();
        user4.setName("Djuradj");
        user4.setSurname("Djurdjevic");
        user4.setUsername("djura@raf.rs");
        user4.setPassword(this.passwordEncoder.encode("djura1234"));
        this.userRepository.save(user4);

        //delete + read
        User user5 = new User();
        user5.setName("Milos");
        user5.setSurname("Milosevic");
        user5.setUsername("mika@raf.rs");
        user5.setPassword(this.passwordEncoder.encode("mika1234"));
        this.userRepository.save(user5);

        //none
        User user6 = new User();
        user6.setName("Vladimir");
        user6.setSurname("Vladimirovic");
        user6.setUsername("vlada@raf.rs");
        user6.setPassword(this.passwordEncoder.encode("vlada1234"));
        this.userRepository.save(user6);

        //read users permission
        Permission read_perm = new Permission();
        read_perm.setName("can_read_users");
        read_perm.addUser(user1);
        read_perm.addUser(user2);
        read_perm.addUser(user3);
        read_perm.addUser(user4);
        read_perm.addUser(user5);
        this.permissionRepository.save(read_perm);

        //add user permission
        Permission create_perm = new Permission();
        create_perm.setName("can_create_users");
        create_perm.addUser(user1);
        create_perm.addUser(user3);
        this.permissionRepository.save(create_perm);

        //edit user permission
        Permission update_perm = new Permission();
        update_perm.setName("can_update_users");
        update_perm.addUser(user1);
        update_perm.addUser(user4);
        this.permissionRepository.save(update_perm);

        //delete user permission
        Permission delete_perm = new Permission();
        delete_perm.setName("can_delete_users");
        delete_perm.addUser(user1);
        delete_perm.addUser(user5);
        this.permissionRepository.save(delete_perm);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = formatter.format(new Date());
        Date date = new GregorianCalendar(2022, Calendar.FEBRUARY, 11).getTime();

        Machine machine1 = new Machine();
        machine1.setName("PerinaPrvaMasina");
        machine1.setStatus("STOPPED");
        machine1.setActive(true);
        machine1.setUser(user1);
        machine1.setDateCreated(formatter.format(date));
        this.machineRepository.save(machine1);

        Machine machine2 = new Machine();
        machine2.setName("PerinaDrugaMasina");
        machine2.setStatus("STOPPED");
        machine2.setActive(false);
        machine2.setUser(user1);
        machine2.setDateCreated(dateString);
        this.machineRepository.save(machine2);

        Machine machine4 = new Machine();
        machine4.setName("PerinaTrecaMasina");
        machine4.setStatus("RUNNING");
        machine4.setActive(false);
        machine4.setUser(user1);
        machine4.setDateCreated(dateString);
        this.machineRepository.save(machine4);

        Machine machine3 = new Machine();
        machine3.setName("MyMachine");
        machine3.setStatus("RUNNING");
        machine3.setActive(true);
        machine3.setUser(user2);
        machine3.setDateCreated(dateString);
        this.machineRepository.save(machine3);

        //search machines permission
        Permission search_machines = new Permission();
        search_machines.addUser(user1);
        search_machines.setName("can_search_machines");
        this.permissionRepository.save(search_machines);

        //start machine permission
        Permission start_machine = new Permission();
        start_machine.addUser(user1);
        start_machine.setName("can_start_machines");
        this.permissionRepository.save(start_machine);

        //stop machine permission
        Permission stop_machine = new Permission();
        stop_machine.addUser(user1);
        stop_machine.setName("can_stop_machines");
        this.permissionRepository.save(stop_machine);

        //restart machine permission
        Permission restart_machine = new Permission();
        restart_machine.addUser(user1);
        restart_machine.setName("can_restart_machines");
        this.permissionRepository.save(restart_machine);

        //create machine permission
        Permission create_machine = new Permission();
        create_machine.addUser(user1);
        create_machine.setName("can_create_machines");
        this.permissionRepository.save(create_machine);

        //destroy machine permission
        Permission destroy_machine = new Permission();
        destroy_machine.addUser(user1);
        destroy_machine.setName("can_destroy_machines");
        this.permissionRepository.save(destroy_machine);

        System.out.println("Data loaded");

    }
}
