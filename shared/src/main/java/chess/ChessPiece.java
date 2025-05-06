package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return findPieceMoves(board, myPosition);
    }

    @Override
    public String toString() {
        return switch (type) {
            //  look at the statement before ?, if true return first, false return second
            // capital is team white, lowercase is team black
            case KING -> pieceColor == ChessGame.TeamColor.WHITE ? "K" : "k";
            case QUEEN -> pieceColor == ChessGame.TeamColor.WHITE ? "Q" : "q";
            case BISHOP -> pieceColor == ChessGame.TeamColor.WHITE ? "B" : "b";
            case KNIGHT -> pieceColor == ChessGame.TeamColor.WHITE ? "N" : "n";
            case ROOK -> pieceColor == ChessGame.TeamColor.WHITE ? "R" : "r";
            case PAWN -> pieceColor == ChessGame.TeamColor.WHITE ? "P" : "p";
        };
    }

    @Override
    public boolean equals(Object obj) {
        if(this ==obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        ChessPiece otro = (ChessPiece) obj;
        return pieceColor == otro.pieceColor && type == otro.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    //generate lists of piece moves
    private Collection<ChessMove> findPieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        switch(type) {//moves functions ,accept:  moves, board, myPosition
            case KING:
            case QUEEN:
            case BISHOP:
            case KNIGHT:
            case ROOK:
            case PAWN:
        }
        return moves;
    }
    //find all linear moves, can be diagonal/horizonatl/vertical lines
    private void findLinearMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int directionX, int directionY){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        while(true){
            row += directionX;
            col += directionY;
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);

            if (!newPosition.isInBounds()) {
                break;
            }
            if (board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    moves.add(newMove);
                }
                break;
            }
            moves.add(newMove);
        }
    }
    //bishop, rook, queen moves using linear
    private void BishopMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition){
        findLinearMoves(moves, board, myPosition, 1, 1);
        findLinearMoves(moves, board, myPosition, 1, -1);
        findLinearMoves(moves, board, myPosition, -1, -1);
        findLinearMoves(moves, board, myPosition, -1, 1);
    }
    private void RookMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition){
        findLinearMoves(moves, board, myPosition, 0, 1);
        findLinearMoves(moves, board, myPosition, 0, -1);
        findLinearMoves(moves, board, myPosition, -1, 0);
        findLinearMoves(moves, board, myPosition, -1, 0);
    }
    private void QueenMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition){
        RookMoves(moves, board, myPosition);
        BishopMoves(moves, board, myPosition);
    }
    //king
    private void KingMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition){
        for( int i = myPosition.getRow(); i<=myPosition.getRow()+1; i++){
            for(int j = myPosition.getColumn(); j<=myPosition.getColumn()+1; j++){
                ChessPosition newPosition = new ChessPosition(i, j);
                //check that its a valid positon and moves.add it
            }
        }
    }
    //knight

    //pawn
}
