package client;

import model.GameData;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Arrays;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PostLogin extends Client {

    public PostLogin(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public PostLogin(Client other) {
        super(other);
    }

    @Override
    String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "help";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        return switch (command) {
            case "create" -> create(parameters);
            case "list" -> list();
            case "join" -> join(parameters);
            case "observe" -> observe(parameters);
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String help() {
        return SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " -> List available commands\n" +
                SET_TEXT_COLOR_BLUE + "create <name>" + SET_TEXT_COLOR_WHITE + " -> Create new game\n" +
                SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_WHITE + " -> List all games\n" +
                SET_TEXT_COLOR_BLUE + "join <id> <WHITE|BLACK>" + SET_TEXT_COLOR_WHITE + " -> Join a game as a" +
                "specified color\n" +
                SET_TEXT_COLOR_BLUE + "observe <id>" + SET_TEXT_COLOR_WHITE + " -> Observe game, type game ID\n" +
                SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_WHITE + " -> Logout of your account\n" +
                SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " -> Exit the application";
    }

    private String create(String ... params) {
        if (params.length == 1) {
            try {
                serverFacade.createGame(params[0], authToken);
                return "Successfully created a game.";
            } catch (Exception e) {
                return "Something went wrong creating a game, please check input and try again.";
            }
        }
        else {
            return "The create command requires one argument, the name of the game you want to create. Please try again.";
        }
    }

    private String list() {
        ArrayList<GameData> games = new ArrayList<>(serverFacade.listGames(authToken));
        String response = "";
        for (int i = 0; i < games.size(); i++) {
            response = response.concat(addEntryToGameList(i, games.get(i)));
        }
        return response;
    }

    private String join(String ... params) {
        if (params.length == 2) {
            try {
                if (!params[1].equals("WHITE") && !(params[1].equals("BLACK"))) {
                    return "You must specify either WHITE or BLACK. Type in all caps.";
                }
                GameData selectedGame = gameList.get(Integer.parseInt(params[0]));
                serverFacade.joinGame(params[1], selectedGame.gameID(), authToken);
                System.out.println("You have joined " + selectedGame.gameName());
                game = selectedGame;
                if (params[1].equals("WHITE")) {
                    color = ChessGame.TeamColor.WHITE;
                } else if (params[1].equals("BLACK")) {
                    color = ChessGame.TeamColor.BLACK;
                }
                return "join";
            } catch (NumberFormatException e) {
                return "Please specify the game you want to join using it's number in the list.";
            } catch (Exception e) {
                return switch (e.getMessage()) {
                    case "Error: unauthorized" -> "quit";
                    case "Error: already taken" -> "That color is already taken. Please try again.";
                    default -> "Something went wrong, please check your input and try again.";
                };
            }
        }
        else {
            return "The join command requires two arguments, the ID of the game you want to create and the color you" +
                    " want to play. Please try again.";
        }
    }

    private String observe(String ... params) {
        if (params.length == 1) {
            try {
                game = gameList.get(Integer.parseInt(params[0]));
                color = ChessGame.TeamColor.WHITE;
                if (game == null) {
                    return "This game does not exist";
                }
                return "observe";
            } catch (NumberFormatException e) {
                return "Please specify the game you want to join using its listed number. Use the digit";
            } catch (Exception e) {
                return "Something went wrong, please check your input and try again.";
            }
        }
        else {
            return "The observe command takes one argument, the ID of the game you want to observe. Please try again.";
        }
    }

    private String logout() {
        serverFacade.logout(authToken);
        System.out.println("Successfully logged out");
        return "logout";
    }

    private String addEntryToGameList(int index, GameData gameData) {
        gameList.put(index+1, gameData);
        return (index+1) + " Name: " + gameData.gameName() + ", White: " + gameData.whiteUsername() + ", Black: " +
                gameData.blackUsername() + "\n";
    }
}