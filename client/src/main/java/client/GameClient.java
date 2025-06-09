package client;

import chess.*;
import chess.ChessGame;
import model.GameData;
import websocket.commands.UserGameCommand;

import static ui.EscapeSequences.*;
import java.util.*;

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
        System.out.println(activeGame);
    }

    public GameClient(Client other) {
        super(other);
        try {
            websocketClient = new WebsocketClient(serverFacade.getServerUrl(), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(activeGame);
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
            case "move" -> move(parameters);
            case "resign" -> resign();
            case "highlight" -> highlight(parameters);
            default -> help();
        };
    }

    //string fns
    public String help() {
        return SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - Show available commands\n" +
                SET_TEXT_COLOR_BLUE + "redraw" + SET_TEXT_COLOR_WHITE + " - Redraw board\n" +
                SET_TEXT_COLOR_BLUE + "leave" + SET_TEXT_COLOR_WHITE + " - Leave game\n" +
                SET_TEXT_COLOR_BLUE + "move <start position> <end position> <promotion type>" + SET_TEXT_COLOR_WHITE +
                " - make specified move" + " (only players may make moves) - type a column letter and row number for"+
                " the positions, and if the move leads to a pawn promotion, type the specified piece type desired, lowercased." +
                SET_TEXT_COLOR_BLUE + "resign" + SET_TEXT_COLOR_WHITE + " - Resign (only players can resign)\n" +
                SET_TEXT_COLOR_BLUE + "highlight <position>" + SET_TEXT_COLOR_WHITE + " - Highlight available moves for"+
                " a piece located at a given psoition. Type the position using the column letter and row number, ex. a4.";
    }

    public String redraw() {
        return getBoardString(game.game().getBoard());
    }

    public String leave() {
        websocketClient.sendUserCommand(UserGameCommand.CommandType.LEAVE);        return "leave";
    }

    public String move(String ... params) {
        if (params.length < 2 || params.length > 3) {
            return "The move command takes two or three arguments, the current position of the piece you want to move and" +
                    " the position you want to move it to, and optionally the type a pawn will be promoted to. Please try again.";
        }
        ChessPosition startPosition = parsePositionParameter(params[0]);
        ChessPosition endPosition = parsePositionParameter(params[1]);
        if(startPosition==null||endPosition==null){
            return "please try again, the given positions are in the wrong format.";
        }
        ChessPiece.PieceType promotionPiece;
        if (params.length < 3) {
            promotionPiece = null;
        }else if(!game.game().getIfInProgress()){
            return "This game ended.";
        }
        else {
            promotionPiece = getPromotionPiece(params[2]);
            if (promotionPiece == null) {
                return "Invalid promotion type. Please try again.";
            }
        }
        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
        websocketClient.sendMove(move);
        return "";
    }

    public String resign() {
        if (activeGame) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Resign? (type 'yes' or 'no') \n>>> ");
            if (scanner.nextLine().equals("yes")) {
                websocketClient.sendUserCommand(UserGameCommand.CommandType.RESIGN);
            }
            return "Resigned.";
        } else {//an observer could try...
            return "Only players can resign.";
        }
    }

    public String highlight(String ... params) {
        if (params.length != 1) {
            return "'highlight' requires the position of the piece you want to see available moves for." +
                    " Please try again.";
        }
        ChessPosition position = parsePositionParameter(params[0]);
        if (position == null) {
            return "'highlight' requires specification of a board position, ex. f7.";
        }
        Collection<ChessMove> validMoves = game.game().validMoves(position);
        return getHighlightedBoardString(game.game().getBoard(), position, getEndPositions(validMoves));
    }


    //print board stuff fns
    public void printNotification(String message) {
        System.out.println(message);
        System.out.print(">>> ");
    }

    public void printBoard() {
        System.out.println(getBoardString(game.game().getBoard()));
        System.out.print(">>> ");
    }

    private String getBoardString(ChessBoard board) {
        return getHighlightedBoardString(board, null, null);
    }

    public String getHighlightedBoardString(ChessBoard board, ChessPosition currentPosition, Set<ChessPosition> validMoves) {
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
                    SET_BG_COLOR_DARK_GREEN, evenRow ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY, currentPosition, validMoves));
            rowNumber += stepSize;
            evenRow = !evenRow;
        }
        printedBoard.append(letters);
        printedBoard.append(RESET_TEXT_COLOR);
        System.out.print(printedBoard);
        return "\n" +printedBoard;
    }

    String printBoardRow(ChessPiece[] row, int rowNumber, String firstColor, String lastColor, ChessPosition selectedPosition,
                         Set<ChessPosition> validMoves) {
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
            String squareColor;
            ChessPosition currentSquare = new ChessPosition(rowNumber, i + 1);
            if (validMoves!=null && validMoves.contains(currentSquare)) {
                squareColor = SET_BG_COLOR_GREEN;
            } else if (currentSquare.equals(selectedPosition)) {
                squareColor = SET_BG_COLOR_RED;
            } else {
                squareColor = colorSwitch ? firstColor : lastColor;
            }
            printedRow.append(printBoardSquare(piece, squareColor));
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

    //more position fns
    ChessPosition parsePositionParameter(String parameter) {
        try {
            Integer column = switch (parameter.substring(0, 1)) {
                case "a" -> 1;
                case "b" -> 2;
                case "c" -> 3;
                case "d" -> 4;
                case "e" -> 5;
                case "f" -> 6;
                case "g" -> 7;
                case "h" -> 8;
                default -> null;
            };
            int row = Integer.parseInt(parameter.substring(1));
            if (row > 8 || row < 1 || column == null) {
                return null;
            }
            return new ChessPosition(row, column);
        } catch (Exception e) {
            return null;
        }
    }

    Set<ChessPosition> getEndPositions(Collection<ChessMove> moves) {
        Set<ChessPosition> positions = new HashSet<>();
        for (ChessMove move : moves) {
            positions.add(move.getEndPosition());
        }
        return positions;
    }

    ChessPiece.PieceType getPromotionPiece(String type) {
        return switch (type) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }
}
