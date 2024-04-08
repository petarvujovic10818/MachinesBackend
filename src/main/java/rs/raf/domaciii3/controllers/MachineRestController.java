package rs.raf.domaciii3.controllers;

import org.springframework.data.jpa.repository.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.raf.domaciii3.model.ErrorMessage;
import rs.raf.domaciii3.model.Machine;
import rs.raf.domaciii3.model.User;
import rs.raf.domaciii3.repositories.ErrorRepository;
import rs.raf.domaciii3.services.ErrorService;
import rs.raf.domaciii3.services.MachineService;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/machines")
public class MachineRestController {

    private final MachineService machineService;
    private final ErrorService errorService;
    private final ErrorRepository errorRepository;

    private SimpMessagingTemplate simpMessagingTemplate;

    public MachineRestController(MachineService machineService, SimpMessagingTemplate simpMessagingTemplate, ErrorService errorService, ErrorRepository errorRepository) {
        this.machineService = machineService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.errorService = errorService;
        this.errorRepository = errorRepository;
    }

    @GetMapping(value = "/all/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllMachines(@PathVariable Long userId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_search_machines"))){
            return ResponseEntity.ok(machineService.findAllByUser(userId));
        }else {
            return ResponseEntity.status(403).build();
        }
        //return userService.findAll();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<?> getMachineById(@PathVariable Long id){ //bilo RequestParam("userId") ?userId=1
        Optional<Machine> optionalMachine = machineService.findById(id);
        if(optionalMachine.isPresent()) {
            return ResponseEntity.ok(optionalMachine.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping(value="/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllMachinesFilter(@RequestParam("name") String name, @RequestParam("status") String status,
                                                  @RequestParam("dateFrom") String dateFrom, @RequestParam("dateTo") String dateTo){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(Machine.getWorking()){
            return ResponseEntity.status(405).build();
        }
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_search_machines"))){
            return ResponseEntity.ok(machineService.findMachinesByName(name, status, dateFrom, dateTo));
        }else {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMachine(@Valid @RequestBody Machine machine){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(Machine.getWorking()){
            ErrorMessage err = new ErrorMessage();
            err.setMachine(null);
            err.setUserId(machine.getUser().getUserID());
            err.setMessageError("MACHINE IS ALREADY WORKING!");
            err.setOperationFailed("CREATE");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            err.setDateError(sdf.format(new Date()));
            errorRepository.save(err);
            return ResponseEntity.status(405).build();
        }
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_create_machines"))){
            return ResponseEntity.ok(machineService.save(machine));
        }else {
            return ResponseEntity.status(403).build();
        }
    }
    //stavlja machine active flag na false
    @PatchMapping(path="/{id}")
    public ResponseEntity<?> removeMachine(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Machine> m = machineService.findById(id);
        if(m.isPresent()){
            if (!m.get().isActive()) {
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("THIS MACHINE IS ALREADY INACTIVE!");
                err.setOperationFailed("DESTROY");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }

            if(Machine.getWorking()){
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("MACHINE IS ALREADY WORKING!");
                err.setOperationFailed("DESTROY");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }
        }
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_destroy_machines"))){
            machineService.removeMachine(id);
            return ResponseEntity.ok().build();
       }
        else {
            return ResponseEntity.status(403).build();
        }
    }
    //status machine stavalja na RUNNING
    @PatchMapping(path = "/start/{id}")
    public ResponseEntity<?> startMachine(@PathVariable Long id) throws InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Machine> m = machineService.findById(id);
        if(m.isPresent()) {
            if (m.get().getStatus().equals("RUNNING")) {
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("CANT START RUNNING MACHINE!");
                err.setOperationFailed("START");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }
            if(Machine.getWorking()){
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("MACHINE IS ALREADY STARTING!");
                err.setOperationFailed("START");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }
        }

        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_start_machines"))){
            m.ifPresent(machine -> Machine.setWorking(true));
            machineService.startMachine(id);
            m.ifPresent(machine -> Machine.setWorking(false));
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(403).build();
        }

    }

    @PatchMapping(path = "/start-test/{id}")
    public ResponseEntity<?> startTestMachine(@PathVariable Long id, @RequestParam("extra") String extra) throws InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Machine> m = machineService.findById(id);
        if(extra.equals("CHECK")) {
            //Optional<Machine> m = machineService.findById(id);
            if (m.isPresent()) {
                if (m.get().getStatus().equals("RUNNING")) {
                    ErrorMessage err = new ErrorMessage();
                    err.setMachine(m.get());
                    err.setUserId(m.get().getUser().getUserID());
                    err.setMessageError("CANT START RUNNING MACHINE!");
                    err.setOperationFailed("START");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    err.setDateError(sdf.format(new Date()));
                    errorRepository.save(err);
                    return ResponseEntity.status(405).build();
                }
                if (Machine.getWorking()) {
                    ErrorMessage err = new ErrorMessage();
                    err.setMachine(m.get());
                    err.setUserId(m.get().getUser().getUserID());
                    err.setMessageError("MACHINE IS ALREADY STARTING!");
                    err.setOperationFailed("START");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    err.setDateError(sdf.format(new Date()));
                    errorRepository.save(err);
                    return ResponseEntity.status(405).build();
                }
            }
            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("can_start_machines"))) {
                m.ifPresent(machine -> Machine.setWorking(true));
                //machineService.startMachine(id);
                //m.ifPresent(machine -> Machine.setWorking(false));
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        } else if(extra.equals("INIT") && Machine.getWorking()){
            //m.ifPresent(machine -> Machine.setWorking(true));
            machineService.startMachine(id);
            m.ifPresent(machine -> Machine.setWorking(false));
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(405).build();
        //return ResponseEntity.ok("something");
    }

    @PatchMapping(path = "/stop-test/{id}")
    public ResponseEntity<?> stopTestMachine(@PathVariable Long id, @RequestParam("extra") String extra) throws InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Machine> m = machineService.findById(id);
        if(extra.equals("CHECK")) {
            //Optional<Machine> m = machineService.findById(id);
            if (m.isPresent()) {
                if (m.get().getStatus().equals("STOPPED")) {
                    ErrorMessage err = new ErrorMessage();
                    err.setMachine(m.get());
                    err.setUserId(m.get().getUser().getUserID());
                    err.setMessageError("CANT STOP STOPPED MACHINE!");
                    err.setOperationFailed("STOP");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    err.setDateError(sdf.format(new Date()));
                    errorRepository.save(err);
                    return ResponseEntity.status(405).build();
                }
                if (Machine.getWorking()) {
                    ErrorMessage err = new ErrorMessage();
                    err.setMachine(m.get());
                    err.setUserId(m.get().getUser().getUserID());
                    err.setMessageError("MACHINE IS ALREADY STOPPING!");
                    err.setOperationFailed("STOP");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    err.setDateError(sdf.format(new Date()));
                    errorRepository.save(err);
                    return ResponseEntity.status(405).build();
                }
            }
            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("can_stop_machines"))) {
                m.ifPresent(machine -> Machine.setWorking(true));
                //machineService.startMachine(id);
                //m.ifPresent(machine -> Machine.setWorking(false));
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        } else if(extra.equals("INIT") && Machine.getWorking()){
            //m.ifPresent(machine -> Machine.setWorking(true));
            machineService.stopMachine(id);
            m.ifPresent(machine -> Machine.setWorking(false));
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(405).build();
        //return ResponseEntity.ok("something");
    }

    @PatchMapping(path = "/restart-test/{id}")
    public ResponseEntity<?> restartTestMachine(@PathVariable Long id, @RequestParam("extra") String extra) throws InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Machine> m = machineService.findById(id);
        if(extra.equals("CHECK")) {
            //Optional<Machine> m = machineService.findById(id);
            if (m.isPresent()) {
                if (m.get().getStatus().equals("STOPPED")) {
                    ErrorMessage err = new ErrorMessage();
                    err.setMachine(m.get());
                    err.setUserId(m.get().getUser().getUserID());
                    err.setMessageError("CANT RESTART STOPPED MACHINE!");
                    err.setOperationFailed("RESTART");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    err.setDateError(sdf.format(new Date()));
                    errorRepository.save(err);
                    return ResponseEntity.status(405).build();
                }
                if (Machine.getWorking()) {
                    ErrorMessage err = new ErrorMessage();
                    err.setMachine(m.get());
                    err.setUserId(m.get().getUser().getUserID());
                    err.setMessageError("MACHINE IS ALREADY STOPPING!");
                    err.setOperationFailed("RESTART");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    err.setDateError(sdf.format(new Date()));
                    errorRepository.save(err);
                    return ResponseEntity.status(405).build();
                }
            }
            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("can_restart_machines"))) {
                m.ifPresent(machine -> Machine.setWorking(true));
                //machineService.startMachine(id);
                //m.ifPresent(machine -> Machine.setWorking(false));
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        } else if(extra.equals("INIT") && Machine.getWorking()){
            //m.ifPresent(machine -> Machine.setWorking(true));
            machineService.stopMachineRestart(id);
            //m.ifPresent(machine -> Machine.setWorking(false));
            return ResponseEntity.ok().build();
        } else if(extra.equals("INIT2") && Machine.getWorking()){
            machineService.startMachineRestart(id);
            m.ifPresent(machine -> Machine.setWorking(false));
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(405).build();
        //return ResponseEntity.ok("something");
    }

    @PatchMapping(path = "/start/scheduled/{id}")
    public ResponseEntity<?> startMachineScheduled(@PathVariable Long id, @RequestParam String seconds, @RequestParam String minutes,
                                                   @RequestParam String hours, @RequestParam String day, @RequestParam String month){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_start_machines"))){
            machineService.startMachineScheduled(id, seconds, minutes, hours, day, month);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(403).build();
        }

    }

    @PatchMapping(path = "/stop/scheduled/{id}")
    public ResponseEntity<?> stopMachineScheduled(@PathVariable Long id, @RequestParam String seconds, @RequestParam String minutes,
                                                   @RequestParam String hours, @RequestParam String day, @RequestParam String month) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_stop_machines"))){
            machineService.stopMachineScheduled(id, seconds, minutes, hours, day, month);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(403).build();
        }

    }

    @PatchMapping(path = "/restart/scheduled/{id}")
    public ResponseEntity<?> restartMachineScheduled(@PathVariable Long id, @RequestParam String seconds, @RequestParam String minutes,
                                                  @RequestParam String hours, @RequestParam String day, @RequestParam String month){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_restart_machines"))){
            machineService.restartMachineScheduled(id, seconds, minutes, hours, day, month);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(403).build();
        }

    }

    @MessageMapping("/start-machine")
    @SendTo("/topic/start")
    public String socketStartMachine(@Payload String message){

        return "200 OK";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/start-machine")
    public String sendAnswer(){
        this.simpMessagingTemplate.convertAndSend("/topic/start", "START_READY");
        return "200 OK";
    }

    //stavlja status machine na STOPPED
    @PatchMapping(path = "/stop/{id}")
    public ResponseEntity<?> stopMachine(@PathVariable Long id) throws InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Machine> m = machineService.findById(id);
        if(m.isPresent()) {
            if (m.get().getStatus().equals("STOPPED")) {
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("CANT STOP STOPPED MACHINE!");
                err.setOperationFailed("STOP");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }
            if(Machine.getWorking()){
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("MACHINE IS ALREADY STOPPING!");
                err.setOperationFailed("STOP");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }
        }
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_stop_machines"))){
            m.ifPresent(machine -> Machine.setWorking(true));
            machineService.stopMachine(id);
            m.ifPresent(machine -> Machine.setWorking(false));
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(403).build();
        }
    }

    @PatchMapping(path = "/restart/{id}")
    public ResponseEntity<?> restartMachine(@PathVariable Long id) throws InterruptedException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Machine> m = machineService.findById(id);
        if(m.isPresent()) {
            if (m.get().getStatus().equals("STOPPED")) {
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("CANT RESTART STOPPED MACHINE!");
                err.setOperationFailed("RESTART");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }
            if(Machine.getWorking()){
                ErrorMessage err = new ErrorMessage();
                err.setMachine(m.get());
                err.setUserId(m.get().getUser().getUserID());
                err.setMessageError("MACHINE IS ALREADY WORKING!");
                err.setOperationFailed("RESTART");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                err.setDateError(sdf.format(new Date()));
                errorRepository.save(err);
                return ResponseEntity.status(405).build();
            }
        }
        if(auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("can_restart_machines"))){
            m.ifPresent(machine -> Machine.setWorking(true));
            machineService.stopMachineRestart(id);
            machineService.startMachineRestart(id);
            m.ifPresent(machine -> Machine.setWorking(false));
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(403).build();
        }
    }



}
