package ui;

import chess.*;

import java.util.Arrays;
import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;

public class ChessClient {

    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade(8080);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to CHESS! type Help to get started.\n");
        String authToken = null;
        State state = State.SIGNEDOUT;

        while (true) {
            // INPUT
            String[] inputs = scanner.nextLine().trim().split("\\s+");
            String command = formated(inputs[0]);

            // REGISTER REQUEST
            if (command.equals("register") && state == State.SIGNEDOUT) {
                if (inputs.length == 4) {
                    String username = inputs[1];
                    String password = inputs[2];
                    String email = inputs[3];
                    try {
                        // successful sign in
                        var result = facade.register(username, password, email);
                        authToken = result.authToken();
                        state = State.SIGNEDIN;
                        System.out.println("Welcome " + username + "! You have successfully registered and signed in.");
                    } catch (ResponseException e) {
                        // sign in error
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("You didn't provide the correct inputs to register, " +
                            "please try again in this format register <USERNAME> <PASSWORD> <EMAIL>\n");
                }
            } else if (command.equals("register") && state == State.SIGNEDIN) {
                System.out.println("You are already logged in. Please sign out before attempting to register.\n");
            }

            // LOGIN REQUEST
            if (command.equals("login") && state == State.SIGNEDOUT) {
                if (inputs.length == 3) {
                    String username = inputs[1];
                    String password = inputs[2];
                    try {
                        // successful sign in
                        var result = facade.login(username, password);
                        authToken = result.authToken();
                        state = State.SIGNEDIN;
                        System.out.println("Welcome back " + username + "! You have successfully signed in.");
                    } catch (ResponseException e) {
                        // sign in error
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("You didn't provide the correct inputs to login, " +
                            "please try again in this format login <USERNAME> <PASSWORD>\n");
                }
            } else if (command.equals("login") && state == State.SIGNEDIN) {
                System.out.println("You are already logged in. Please sign out before attempting to login.\n");
            }

            // CREATE GAME REQUEST
            if (command.equals("create") && state == State.SIGNEDIN) {
                if (inputs.length > 1) {
                    String gameName = String.join(" ", Arrays.copyOfRange(inputs, 1, inputs.length));
                    try {
                        // successful game creation
                        facade.create(gameName, authToken);
                        System.out.println("You have successfully created a game called" + gameName + ".");
                    } catch (ResponseException e) {
                        // game creation error catch
                        System.out.println(e.getMessage());
                    }
                } else {
                    // input error catch
                    System.out.println("You didn't provide the correct inputs to create a game, " +
                            "please try again in this format create <GAMENAME>\n");
                }
            } else if (command.equals("create") && state == State.SIGNEDOUT) {
                // state error catch
                System.out.println("You are logged out. " +
                        "Please login or register before attempting to create a game.\n");
            }

            // HELP REQUEST
            if (command.equals("help") && state == State.SIGNEDOUT) {
                System.out.print(signedOutHelpMenu);
            } else if  (command.equals("help") && state == State.SIGNEDIN) {
                System.out.print(signedInHelpMenu);
            }

            // QUIT REQUEST (disregards state)
            if (command.equals("quit")) {
                break;
            }


        }
    }

    public static String signedOutHelpMenu = """
            to create an account - register <USERNAME> <PASSWORD> <EMAIL>
            to login             - login <USERNAME> <PASSWORD>
            to exit application  - quit - exit application
            to see help menu     - help\n""";

    public static String signedInHelpMenu = """
            to create a Game     - create <GAMENAME>
            to see list of games - list
            to join a game       - join <ID> [WHITE|BLACK]
            to observe a game    - observe <ID>
            to logout            - logout
            to exit application  - quit
            to see help menu     - help\n""";

    public static String formated(String string) {
        return string.toLowerCase().trim();
    }
}
