package application.contoller;

import application.domain.User;
import application.service.SuperService;

import java.util.Optional;

public interface Controller {


    void initializeController(SuperService superService, Optional<User> user);
}
