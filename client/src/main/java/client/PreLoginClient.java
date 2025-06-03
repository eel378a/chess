package client;

import data.LoginResponse;
import model.UserData;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PreLoginClient extends Client {

    public PreLoginClient(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public PreLoginClient(Client other) {
        super(other);
    }

    @Override
    public String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "help";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        return switch (command) {
            case "register" -> register(parameters);
            case "login" -> login(parameters);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String help() {
        return SET_TEXT_COLOR_BLUE+"Please type one of the following commands: \n"+
                SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " -> List possible commands\n" +
                SET_TEXT_COLOR_BLUE + "register <username> <password> <email>" + SET_TEXT_COLOR_WHITE + " -> Create an"+
                " account\n" +
                SET_TEXT_COLOR_BLUE + "login <username> <password>" + SET_TEXT_COLOR_WHITE + " -> Login\n" +
                SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " -> Exit application";
    }

    private String register(String ... params) {
        if (params.length == 3) {
            try {
                System.out.println("entered try loop");
                LoginResponse response = serverFacade.register(new UserData(params[0], params[1], params[2]));
                authToken = response.authToken();
                username = response.username();
                System.out.println("Successfully registered :)");
                return "login";
            } catch (HttpExcept e) {
                if (e.getStatusCode() == 403) {
                    return "This username is taken, please try a different username";
                }
            }
            return "Something went wrong in registering, please check your input and try again.";
        }
        else {
            return "To register, you must input a username, password and email. Please try again.";
        }
    }

    private String login(String ... params) {
        if (params.length == 2) { //2, check reg/login tests
            try {
                LoginResponse response = serverFacade.login(params[0], params[1]);
                authToken = response.authToken();
                username = response.username();
                System.out.println("Successfully logged in :)");
                return "login";
            } catch (HttpExcept e) {
                if (e.getStatusCode() == 401) {
                    return "Provided username or password is incorrect";
                }
                return "Something went wrong logging in, please check your input and try again.";
            }catch (Exception e) {
            return "Something went wrong, please check your input and try again.";
            }
        }
        else {
            return "The login command requires two arguments, your username and password. Please try again.";
        }
    }
}