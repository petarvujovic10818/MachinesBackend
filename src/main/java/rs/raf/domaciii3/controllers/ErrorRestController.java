package rs.raf.domaciii3.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.raf.domaciii3.services.ErrorService;
import rs.raf.domaciii3.services.UserService;

@CrossOrigin
@RestController
@RequestMapping("/api/errors")
public class ErrorRestController {

    private final ErrorService errorService;

    public ErrorRestController(ErrorService errorService) {
        this.errorService = errorService;
    }

    @GetMapping(value = "/all/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllErrors(@PathVariable Long id){
        return ResponseEntity.ok(errorService.findAllByUserId(id));
        //return userService.findAll();
    }

}
