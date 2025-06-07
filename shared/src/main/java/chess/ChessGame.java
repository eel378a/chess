package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn = TeamColor.WHITE;
    ChessBoard board = new ChessBoard();
    boolean gameInProgress = true;

    public ChessGame() {
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public void swapTeamTurn() {
        if(TeamColor.WHITE == getTeamTurn()){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get and store all legal moves
        Collection<ChessMove> moves = new HashSet<>(board.getPiece(startPosition).pieceMoves(board, startPosition));
        Collection<ChessMove> checkedMoves = new HashSet<>();
        //testMove(move) makes the move on the board temporarily and returns the captured piece (if any), sees if that
        //move puts us in check, and then undoes the testMove to return board to the original state
        for(ChessMove move : moves) {
            ChessPiece capturedPiece = testMove(move);
            if(!isInCheck(board.getPiece(move.getEndPosition()).getTeamColor())){
                checkedMoves.add(move);
            }
            undoTestMove(move, capturedPiece);
        }
        //return legal moves :)
        return checkedMoves;
    }

    public Collection<ChessMove> allValidMoves(TeamColor teamColor) {
        Collection<ChessMove> checkedMoves = new HashSet<>();

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                if((null != board.getPiece(new ChessPosition(j, i))) &&
                        (teamColor == board.getPiece(new ChessPosition(j, i)).getTeamColor()) ) {
                    checkedMoves.addAll(validMoves(new ChessPosition(j, i)));
                }
            }
        }

        return checkedMoves;
    }

    ChessPiece testMove(ChessMove move){
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), movingPiece);

        return capturedPiece;
    }

    void undoTestMove(ChessMove move, ChessPiece capturedPiece){
        ChessPiece movingPiece = board.getPiece(move.getEndPosition());
        board.removePiece(move.getEndPosition());
        board.addPiece(move.getStartPosition(), movingPiece);

        if(capturedPiece != null){
            board.addPiece(move.getEndPosition(), capturedPiece);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);
        if(null == currentPiece){
            throw new InvalidMoveException("You must Move a piece");
        }
        if(getTeamTurn() != currentPiece.getTeamColor()){
            throw new InvalidMoveException("It is the other team's turn");
        }

        //is a valid move??
        Collection<ChessMove> validMovesList = validMoves(startPosition);
        if (!validMovesList.stream().anyMatch(move::equals)) {
            throw new InvalidMoveException("Illegal Move");
        }
        board.removePiece(startPosition);
        if(null == move.getPromotionPiece()){
            board.addPiece(move.getEndPosition(), currentPiece);
        }
        else{
            board.addPiece(move.getEndPosition(), new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece()));
        }

        swapTeamTurn();

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = new ChessPosition(1, 1);
        Collection<ChessMove> opponentMoves = new HashSet<>();
        boolean inCheck = false;
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                if((null != board.getPiece(new ChessPosition(j, i))) &&
                        (ChessPiece.PieceType.KING == board.getPiece(new ChessPosition(j, i)).getPieceType()) &&
                        (teamColor == board.getPiece(new ChessPosition(j, i)).getTeamColor()) ) {
                    kingPosition = new ChessPosition(j, i);
                }
                if((null != board.getPiece(new ChessPosition(j, i))) &&
                        (teamColor != board.getPiece(new ChessPosition(j, i)).getTeamColor()) ) {
                    opponentMoves.addAll(board.getPiece(new ChessPosition(j, i)).pieceMoves(board, new ChessPosition(j, i)));
                }
            }
        }
        for(ChessMove move : opponentMoves) {
            if(kingPosition.equals(move.getEndPosition())){
                inCheck = true;
                break;
            }
        }
        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean isCheckmate = false;
        if(isInCheck(teamColor)){
            Collection<ChessMove> moves = allValidMoves(teamColor);
            if(moves.isEmpty()){
                isCheckmate = true;
            }
        }

        return isCheckmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean isStalemate = false;
        if(!isInCheck(teamColor)){
            Collection<ChessMove> moves = allValidMoves(teamColor);
            if(moves.isEmpty()){
                isStalemate = true;
            }
        }

        return isStalemate;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */

    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    //to measure game play in progress or not
    public boolean getIfInProgress(){
        return gameInProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.gameInProgress = inProgress;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{teamTurn=" + teamTurn + ", chessBoard=" + board + "}";
    }
}