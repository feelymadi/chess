package ui;

import chess.*;

import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;

public class ChessClient {

    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade(8080);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to CHESS! type Help to get started.\n");

        while (true) {
            String[] inputs = scanner.nextLine().split(" ");
            String command = formated(inputs[0]);

            if (command.equals("register")) {
                if (inputs.length == 4) {
                    String username = inputs[1];
                    String password = inputs[2];
                    String email = inputs[3];
                    try {
                        var result = facade.register(username, password, email);
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.print("You didn't provide the correct inputs to register, " +
                            "please try again in this format register <USERNAME> <PASSWORD> <EMAIL>\n");
                }
            }

            if (command.equals("login")) {
                if (inputs.length == 3) {
                    String username = inputs[1];
                    String password = inputs[2];
                    System.out.print(username + password);
                } else {
                    System.out.print("You didn't provide the correct inputs to login, " +
                            "please try again in this format login <USERNAME> <PASSWORD>\n");
                }
            }

            if (command.equals("help")) {
                System.out.print(helpMenu);
            }

            if (command.equals("quit")) {
                break;
            }


        }
    }

    public static String helpMenu = """
            to create an account - register <USERNAME> <PASSWORD> <EMAIL>
            to login             - login <USERNAME> <PASSWORD>
            to exit application  - quit - exit application
            to see help menu     - help\n""";

    public static String formated(String string) {
        return string.toLowerCase().trim();
    }
}
