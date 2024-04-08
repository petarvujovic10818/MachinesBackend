package rs.raf.domaciii3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import rs.raf.domaciii3.model.ErrorMessage;
import rs.raf.domaciii3.model.Machine;
import rs.raf.domaciii3.repositories.ErrorRepository;
import rs.raf.domaciii3.repositories.MachineRepository;
import rs.raf.domaciii3.repositories.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MachineService implements IService<Machine,Long>{

    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;
    private final ErrorRepository errorRepository;

    @Autowired
    public MachineService(@Qualifier("machineRepository") MachineRepository machineRepository, UserRepository userRepository, TaskScheduler taskScheduler, ErrorRepository errorRepository) {
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
        this.errorRepository = errorRepository;
    }

    @Override
    public <S extends Machine> S save(S machine) {
        return machineRepository.save(machine);
    }

    @Override
    public Optional<Machine> findById(Long machineId) {
        return machineRepository.findById(machineId);
    }

    @Override
    public List<Machine> findAll() {
        return (List<Machine>) machineRepository.findAll();
    }

    @Override
    public void deleteById(Long machineId) {
        machineRepository.deleteById(machineId);
    }

    public List<Machine> findAllByUser(Long userId){

        return machineRepository.findAllByUser(userId);
    }

    public List<Machine> findMachinesByName(String name, String status, String dateFrom, String dateTo){
        return machineRepository.findMachinesByName(name, status, dateFrom, dateTo);
    }

    public void removeMachine(Long id){
        this.machineRepository.removeMachine(id);
    }


    public void startMachine(Long id) throws InterruptedException {
        Thread.sleep(10000);
        this.machineRepository.startMachine(id);
    }

    public void sleepMachine() throws InterruptedException{
        Thread.sleep(10000);
    }
    // expression = "0 15 14 * * *" -----> ovo se izvrsava danas u 14:15
    // seconds minutes hours day month *
    public void startMachineScheduled(Long id, String seconds, String minutes, String hours, String day, String month){
        CronTrigger cronTrigger = new CronTrigger(seconds + " " + minutes + " " + hours + " " + day + " " + month + " *");
        System.out.println("Machine will be started in: " + "hours: " + hours + " minutes: " + minutes + " seconds: " + seconds);
        this.taskScheduler.schedule(()->{
            try {
                Thread.sleep(10000);
                this.machineRepository.startMachine(id);
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                System.out.print("[" + time + "]" + " Machine successfully started! ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            this.machineRepository.startMachine(id);
//            String time = new SimpleDateFormat("HH:mm").format(new Date());
//            System.out.print("[" + time + "]" + " Machine successfully started! ");
        }, cronTrigger);
    }

    public void stopMachineScheduled(Long id, String seconds, String minutes, String hours, String day, String month){
        CronTrigger cronTrigger = new CronTrigger(seconds + " " + minutes + " " + hours + " " + day + " " + month + " *");
        System.out.println("Machine will be stopped in: " + "hours: " + hours + " minutes: " + minutes + " seconds: " + seconds);
        this.taskScheduler.schedule(()->{
            try {
                Thread.sleep(10000);
                this.machineRepository.stopMachine(id);
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                System.out.print("[" + time + "]" + " Machine successfully stopped! ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            this.machineRepository.stopMachine(id);
//            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//            System.out.print("[" + time + "]" + " Machine successfully stopped! ");
        }, cronTrigger);
    }

    public void restartMachineScheduled(Long id, String seconds, String minutes, String hours, String day, String month){
        CronTrigger cronTrigger = new CronTrigger(seconds + " " + minutes + " " + hours + " " + day + " " + month + " *");
        System.out.println("Machine will be restarted in: " + "hours: " + hours + " minutes: " + minutes + " seconds: " + seconds);
        this.taskScheduler.schedule(()->{
            try {
                Thread.sleep(5000);
                this.machineRepository.stopMachine(id);
                Thread.sleep(5000);
                this.machineRepository.startMachine(id);
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                System.out.print("[" + time + "]" + " Machine successfully restarted! ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            this.machineRepository.stopMachine(id);
//            this.machineRepository.startMachine(id);
//            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//            System.out.print("[" + time + "]" + " Machine successfully restarted! ");
        }, cronTrigger);
    }

    public void stopMachine(Long id) throws InterruptedException{
        Thread.sleep(10000);
        this.machineRepository.stopMachine(id);
    }

    public void startMachineRestart(Long id) throws InterruptedException{
        Thread.sleep(5000);
        this.machineRepository.startMachine(id);
        //System.out.println("RESTART: Machine started");
    }

    public void stopMachineRestart(Long id) throws InterruptedException{
        Thread.sleep(5000);
        this.machineRepository.stopMachine(id);
        //System.out.println("RESTART: Machine stopped");
    }

}
