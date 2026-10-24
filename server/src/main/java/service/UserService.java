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

    // register helper
    public LoginResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("empty request");
        }

        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("already taken");
        }

        userDAO.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));

        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, registerRequest.username()));

        return new LoginResult(registerRequest.username(), authToken);
    }

    // login helper
    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException("empty request");
        }

        UserData user = userDAO.getUser(loginRequest.username());

        if (user == null) {
            throw new UnauthorizedException("user not found");
        }

        if (user.password().equals(loginRequest.password())) {
            String authToken = UUID.randomUUID().toString();
            authDAO.createAuth(new AuthData(authToken, loginRequest.username()));

            return new LoginResult(loginRequest.username(), authToken);
        } else {
            throw new UnauthorizedException("incorrect password");
        }
    }

    // logout helper
    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }

}
