package rs.raf.domaciii3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.raf.domaciii3.model.ErrorMessage;
import rs.raf.domaciii3.model.Machine;
import rs.raf.domaciii3.repositories.ErrorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ErrorService implements IService<ErrorMessage,Long>{

    private final ErrorRepository errorRepository;

    @Autowired
    public ErrorService(ErrorRepository errorRepository){
        this.errorRepository = errorRepository;
    }

    @Override
    public <S extends ErrorMessage> S save(S var1) {
        return null;
    }

    @Override
    public Optional<ErrorMessage> findById(Long var1) {
        return Optional.empty();
    }

    @Override
    public List<ErrorMessage> findAll() {
        return null;
    }

    @Override
    public void deleteById(Long var1) {

    }

    public List<ErrorMessage> findAllByUserId(Long userId){

        return errorRepository.findAllByUserId(userId);
    }

}
