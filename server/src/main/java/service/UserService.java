package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    // initializes
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    // constructs
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("empty request");
        }

        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new BadRequestException("already taken");
        }

        userDAO.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));

        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, registerRequest.username()));

        return new LoginResult(registerRequest.username(), authToken);
    }

}
