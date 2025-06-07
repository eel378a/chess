package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;

import static ui.EscapeSequences.*;
import java.util.Arrays;

public class GameClient extends Client {
    private WebsocketClient websocketClient;

    public GameClient(ServerFacade serverFacade) {
        super(serverFacade);
        try {
            websocketClient = new WebsocketClient(serverFacade.getServerUrl(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame()));
        printBoard();
    }

    public GameClient(Client other) {
        super(other);
        try {
            websocketClient = new WebsocketClient(serverFacade.getServerUrl(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        printBoard();
    }

    @Override
    String eval(String line) {
        String[] arguments = line.split(" ");
        String command = (arguments.length > 0) ? arguments[0] : "leave";
        String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
        //going to need ^ String[] parameters for later commands
        return switch (command) {
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> move();
            case "resign" -> resign();
            case "highlight" -> highlight();
            default -> help();
        };    }

    //string fns
    public String help() {
        return SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - Show available commands\n" +
                SET_TEXT_COLOR_BLUE + "redraw" + SET_TEXT_COLOR_WHITE + " - Redraw board\n" +
                SET_TEXT_COLOR_BLUE + "leave" + SET_TEXT_COLOR_WHITE + " - Leave game\n" +
                SET_TEXT_COLOR_BLUE + "move <id> <WHITE|BLACK>" + SET_TEXT_COLOR_WHITE + " - make specified move" +
                " (only players may make moves)" +
                SET_TEXT_COLOR_BLUE + "resign" + SET_TEXT_COLOR_WHITE + " - Resign (only players can resign)\n" +
                SET_TEXT_COLOR_BLUE + "highlight <piece>" + SET_TEXT_COLOR_WHITE + " - Highlight available moves for a given piece";
    }

    public String redraw() {
        return getBoardString(game.game().getBoard());
    }

    public String leave() {
        websocketClient.sendLeave();
        return "leave";
    }

    public String move() {
        throw new RuntimeException("Not implemented");
    }

    public String resign() {
        throw new RuntimeException("Not implemented");
    }

    public String highlight() {
        throw new RuntimeException("Not implemented");
    }


    //print board stuff fns
    public void printNotification(String message) {
        System.out.println(message);
        System.out.print(">>> ");
    }

    public void printBoard() {
        System.out.println(getBoardString(game.game().getBoard()));
    }

    public String getBoardString(ChessBoard board) {
        String letters = getLetters();
        StringBuilder printedBoard = new StringBuilder(letters);
        int rowNumber;
        int stepSize;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            rowNumber = 8;
            stepSize = -1;
        } else {
            rowNumber = 1;
            stepSize = 1;
        }
        boolean evenRow = true;
        for (int i = rowNumber - 1; (i < 8 && i >= 0); i += stepSize) {
            printedBoard.append(printBoardRow(board.getBoard()[i], rowNumber, evenRow ? SET_BG_COLOR_LIGHT_GREY :
                    SET_BG_COLOR_DARK_GREEN, evenRow ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY));
            rowNumber += stepSize;
            evenRow = !evenRow;
        }
        printedBoard.append(letters);
        printedBoard.append(RESET_TEXT_COLOR);
        System.out.print(printedBoard);
        return printedBoard.toString();
    }

    String printBoardRow(ChessPiece[] row, int rowNumber, String firstColor, String lastColor) {
        StringBuilder printedRow = new StringBuilder(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK + " " + rowNumber +
                " ");
        boolean colorSwitch = true;
        int columnNumber;
        int stepSize;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            columnNumber = 1;
            stepSize = 1;
        } else {
            columnNumber = 8;
            stepSize = -1;
        }
        for (int i = columnNumber - 1; i < 8 && i >= 0; i += stepSize) {
            ChessPiece piece = row[i];
            printedRow.append(printBoardSquare(piece, colorSwitch ? firstColor : lastColor));
            colorSwitch = !colorSwitch;
        }
        printedRow.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLACK + " " + rowNumber + " " + RESET_BG_COLOR +
                "\n");
        return printedRow.toString();
    }

    String printBoardSquare(ChessPiece piece, String squareColor) {
        String pieceCharacter;
        String pieceColor = "";
        if (piece == null) {
            pieceCharacter = EMPTY;
        } else {
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                pieceColor = SET_TEXT_COLOR_WHITE;
            } else {
                pieceColor = SET_TEXT_COLOR_BLACK;
            }
            pieceCharacter = switch (piece.getPieceType()) {
                case ChessPiece.PieceType.KING -> BLACK_KING;
                case ChessPiece.PieceType.QUEEN -> BLACK_QUEEN;
                case ChessPiece.PieceType.ROOK -> BLACK_ROOK;
                case ChessPiece.PieceType.BISHOP -> BLACK_BISHOP;
                case ChessPiece.PieceType.KNIGHT -> BLACK_KNIGHT;
                case ChessPiece.PieceType.PAWN -> BLACK_PAWN;
            };
        }
        return squareColor + pieceColor + pieceCharacter;
    }

    String getLetters() {
        String whiteLetters = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLUE + "    " + "a" + "   " + "b" + "   " + "c" + "  "
                + "d" + "   " + "e" + "  " + "f" + "   " + "g" + "  " + "h" + "    " + RESET_BG_COLOR + "\n";
        String blackLetters = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLUE + "    " + "h" + "   " + "g" + "   " + "f" + "  "
                + "e" + "   " + "d" + "  " + "c" + "   "+ "b" + "   "+ "a" + "    " + RESET_BG_COLOR + "\n";
        return color.equals(ChessGame.TeamColor.WHITE) ? whiteLetters : blackLetters;
    }
}
