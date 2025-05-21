package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition end;
    private final ChessPosition start;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.end = endPosition;
        this.start = startPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
        //throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString(){
        return "Move = startPosition " + start + ", endPosition = " + end+ "promotion = " + promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotionPiece);
    }

    @Override
    public boolean equals(Object obj) {
        if(this ==obj) {return true;}
        if(obj == null) {return false;}
        if(getClass() != obj.getClass()) {return false;}
        ChessMove otro = (ChessMove) obj;
        return Objects.equals(start, otro.start) && Objects.equals(end,otro.end) && Objects.equals(promotionPiece, otro.promotionPiece);
    }

}
