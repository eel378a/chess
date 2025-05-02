package chess;

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
        throw new RuntimeException("Not implemented");
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
}
