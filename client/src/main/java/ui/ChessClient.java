package ui;

import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameInfo;
import model.ListGamesResult;

import static java.lang.Math.abs;

public class ChessClient {

    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade(8080);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to CHESS! type Help to get started.\n");
        String authToken = null;
        State state = State.SIGNED_OUT;

        while (true) {
            // INPUT
            System.out.print("<<" + state + ">> ");
            String[] inputs = scanner.nextLine().trim().split("\\s+");
            String command = formated(inputs[0]);

            // REGISTER REQUEST
            if (command.equals("register") && state == State.SIGNED_OUT) {
                StateAuthRecord result = registerRequest(facade, inputs);
                state = result.state();
                authToken = result.authToken();
            } else if (command.equals("register") && state == State.SIGNED_IN) {
                System.out.println("You are already logged in. " +
                        "Please sign out before attempting to register.\n");
            }

            // LOGIN REQUEST
            if (command.equals("login") && state == State.SIGNED_OUT) {
                StateAuthRecord result = loginRequest(facade, inputs);
                state = result.state();
                authToken = result.authToken();
            } else if (command.equals("login") && state == State.SIGNED_IN) {
                System.out.println("You are already logged in. Please sign out before attempting to login.\n");
            }

            // CREATE GAME REQUEST
            if (command.equals("create") && state == State.SIGNED_IN) {
                createRequest(facade, inputs, authToken);
            } else if (command.equals("create") && state == State.SIGNED_OUT) {
                // state error catch
                System.out.println("You are logged out. " +
                        "Please login or register before attempting to create a game.\n");
            }

            // LIST GAMES REQUEST
            if (command.equals("list") && state == State.SIGNED_IN) {
                listRequest(facade, authToken);
            } else if (command.equals("list") && state == State.SIGNED_OUT) {
                // state error catch
                System.out.println("You are logged out. " +
                        "Please login or register before attempting to list games.\n");
            }

            // PLAY GAME REQUEST
            if (command.equals("join") && state == State.SIGNED_IN) {
                playRequest(facade, inputs, authToken);
            } else if (command.equals("join") && state == State.SIGNED_OUT) {
                // state error catch
                System.out.println("You are logged out. " +
                        "Please login or register before attempting to join a game.\n");
            }

            // OBSERVE GAME REQUEST
            if (command.equals("observe") && state == State.SIGNED_IN) {
                observeRequest(facade, inputs, authToken);

            } else if (command.equals("observe") && state == State.SIGNED_OUT) {
                // state error catch
                System.out.println("You are logged out. " +
                        "Please login or register before attempting to observe a game.\n");
            }

            // LOGOUT REQUEST
            if (command.equals("logout") && state == State.SIGNED_IN) {
                StateAuthRecord result = logoutRequest(facade, authToken);
                state = result.state();
                authToken = result.authToken();
            } else if  (command.equals("help") && state == State.SIGNED_IN) {
                // state error catch
                System.out.println("You are logged out. " +
                        "Please login or register before attempting logout.\n");
            }

            // HELP REQUEST
            if (command.equals("help") && state == State.SIGNED_OUT) {
                System.out.print(signedOutHelpMenu);
            } else if  (command.equals("help") && state == State.SIGNED_IN) {
                System.out.print(signedInHelpMenu);
            }

            // QUIT REQUEST (disregards state)
            if (command.equals("quit")) {
                state = State.SIGNED_OUT;
                authToken = null;
                break;
            }
        }
    }

    private static StateAuthRecord registerRequest(ServerFacade facade, String[] inputs) {
        if (inputs.length == 4) {
            String username = inputs[1];
            String password = inputs[2];
            String email = inputs[3];
            try {
                // successful sign in
                var result = facade.register(username, password, email);
                String authToken = result.authToken();
                System.out.println("Welcome " + username + "! You have successfully registered and signed in.");
                return new StateAuthRecord(State.SIGNED_IN, authToken);
            } catch (ResponseException e) {
                // sign in error
                System.out.println("Error code: " + e.code());
                System.out.println("Error message: " + e.getMessage());
                return new StateAuthRecord(State.SIGNED_OUT, null);
            }
        } else {
            System.out.println("You didn't provide the correct inputs to register, " +
                    "please try again in this format register <USERNAME> <PASSWORD> <EMAIL>\n");
            return new StateAuthRecord(State.SIGNED_OUT, null);
        }
    }

    private static StateAuthRecord loginRequest (ServerFacade facade, String[] inputs){
        if (inputs.length == 3) {
            String username = inputs[1];
            String password = inputs[2];
            try {
                // successful sign in
                var result = facade.login(username, password);
                String authToken = result.authToken();
                State state = State.SIGNED_IN;
                System.out.println("Welcome back " + username + "! You have successfully signed in.");
                return new StateAuthRecord(state, authToken);
            } catch (ResponseException e) {
                // sign in error
                System.out.println(e.getMessage());
                return new StateAuthRecord(State.SIGNED_OUT, null);
            }
        } else {
            System.out.println("You didn't provide the correct inputs to login, " +
                    "please try again in this format login <USERNAME> <PASSWORD>\n");
            return new StateAuthRecord(State.SIGNED_OUT, null);
        }
    }

    private static void createRequest (ServerFacade facade, String[] inputs, String authToken){
        if (inputs.length > 1) {
            String gameName = String.join(" ", Arrays.copyOfRange(inputs, 1, inputs.length));
            try {
                // successful game creation
                facade.create(gameName, authToken);
                System.out.println("You have successfully created a game named " + gameName + ".");
            } catch (ResponseException e) {
                // game creation error catch
                System.out.println(e.getMessage());
            }
        } else {
            // input error catch
            System.out.println("You didn't provide the correct inputs to create a game, " +
                    "please try again in this format create <GAMENAME>\n");
        }
    }

    private static void listRequest (ServerFacade facade, String authToken) {
        try {
            ListGamesResult result = facade.listGames(authToken);
            System.out.println("NUM | GAME NAME                | WHITE               | BLACK");
            int index = 0;
            for (GameInfo game : result.games()) {
                index++;
                printGameInList(index, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
        } catch (ResponseException e) {
            // game creation error catch
            System.out.println(e.getMessage());
        }
    }

    private static void playRequest (ServerFacade facade, String[] inputs, String authToken){
        if (inputs.length == 3 && (formated(inputs[2]).equals("white") || formated(inputs[2]).equals("black"))) {

            // initialize values
            String playerColor = null;

            // store color value
            if (formated(inputs[2]).equals("white")){
                playerColor = "WHITE";
            } else if (formated(inputs[2]).equals("black")) {
                playerColor = "BLACK";
            }

            try {
                // store game index
                int num = Integer.parseInt(inputs[1])-1;
                var result = facade.listGames(authToken);
                GameInfo gameInfo = result.games().get(num);
                int gameID = gameInfo.gameID();

                // successful game creation
                facade.playGame(authToken, playerColor, gameID);

                // draw board
                ChessGame game = new ChessGame();
                ChessBoardDrawer drawer = new ChessBoardDrawer();

                if (playerColor.equals("WHITE")){
                    drawer.drawBoard(game.getBoard(), ChessGame.TeamColor.WHITE);
                } else {
                    drawer.drawBoard(game.getBoard(), ChessGame.TeamColor.BLACK);
                }

            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Invalid game number. " +
                        "Please enter an integer in this format join <ID> [WHITE|BLACK]");
            } catch (ResponseException e) {
                System.out.println(e.getMessage());
            }

        } else {
            // input error catch
            System.out.println("You didn't provide the correct inputs to join a game, " +
                    "please try again in this format join <ID> [WHITE|BLACK]\n");
        }
    }

    private static void observeRequest (ServerFacade facade, String[] inputs, String authToken){
        if (inputs.length == 2) {

            try {
                // store game index
                int num = Integer.parseInt(inputs[1])-1;
                var result = facade.listGames(authToken);
                GameInfo gameInfo = result.games().get(num);
                int gameID = gameInfo.gameID();

                // successful game observation
                ChessGame game = new ChessGame();
                ChessBoardDrawer drawer = new ChessBoardDrawer();

                // draw board
                drawer.drawBoard(game.getBoard(), ChessGame.TeamColor.WHITE);

            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Invalid game number. " +
                        "Please enter an integer in this format observe <ID> ");
            } catch (ResponseException e) {
                System.out.println(e.getMessage());
            }

        } else {
            // input error catch
            System.out.println("You didn't provide the correct inputs to observe a game, " +
                    "please try again in this format observe <ID>\n");
        }
    }

    private static StateAuthRecord logoutRequest(ServerFacade facade, String authToken) {
        try {
            facade.logout(authToken);
            return new StateAuthRecord(State.SIGNED_OUT, null);
        } catch (ResponseException e) {
            // game creation error catch
            System.out.println(e.getMessage());
            return new StateAuthRecord(State.SIGNED_IN, authToken);
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

    public static void printGameInList(Integer num, String gameName, String white, String black) {
        if (white == null) {
            white = "none";
        }

        if (black == null) {
            black = "none";
        }

        String paddedNum = num + " ".repeat(abs(num.toString().length() - 4)) + "| ";
        String paddedGameName = gameName + " ".repeat(abs(gameName.length() - 25)) + "| ";
        String paddedWhiteName = white + " ".repeat(abs(white.length() - 20)) + "| ";
        System.out.println(paddedNum + paddedGameName + paddedWhiteName + black);
    }

    public static String gameListHeader = """
            NUM | GAME NAME                | WHITE               | BLACK
            """;

    public static String formated(String string) {
        return string.toLowerCase().trim();
    }
}
