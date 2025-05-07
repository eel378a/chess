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
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case PAWN -> {
                int[][] directions = availableDirections(type);
                assert directions != null;
                for (int[] direction : directions) {
                    ChessMove newMove;
                    newMove = getPawnMove(board, myPosition, direction[0], direction[1]);

                    // and on the first move PAWN can move 2...
                    if ((direction[1] == 0) && isFirstMove(pieceColor, myPosition)){
                        ChessMove extraMove = null;
                        if (newMove != null){
                            switch (pieceColor){
                                case WHITE -> extraMove = getPawnMove(board, myPosition, direction[0] + 1, direction[1]);
                                case BLACK -> extraMove = getPawnMove(board, myPosition, direction[0] - 1, direction[1]);
                            }
                        }
                        if (extraMove != null) moves.add(extraMove);
                    }
                    if (newMove != null){
                        if (canPromote(newMove, pieceColor)) {
                            moves.addAll(getPawnPromotions(newMove));
                        } else moves.add(newMove);
                    }
                }
            }
            case QUEEN, BISHOP, ROOK -> {
                int[][] directions = availableDirections(type);
                assert directions != null; //since it was yelling at me that this could be an issue if not asserted
                for (int[] direction : directions) {
                    moves.addAll(findLinearMoves(board, myPosition, direction[0], direction[1]));
                }
            }
            case KING, KNIGHT -> { // both move only one
                int[][] directions = availableDirections(type);
                assert directions != null;
                for (int[] direction : directions) {
                    ChessMove newMove = getOneMove(board, myPosition, direction[0], direction[1]);
                    if (newMove != null) moves.add(newMove);
                }
            }
        }
        return moves;
    }
    //additional functions. availableDirections, spaceIsFull, isOutofBounds, and findLinear, and all the annoying pawn stuff
    private int[][] availableDirections(PieceType type) {
        switch (type) {
            case KING, QUEEN -> { //can go all directions
                return new int[][]{
                        {1, 0},   // up
                        {1, 1},   // up right
                        {0, 1},   // directly right
                        {-1, 1},  // right and down
                        {-1, 0},  // down
                        {-1, -1}, // down/left
                        {0, -1},  // left
                        {1, -1}   // up and left
                };
            }
            case BISHOP -> {
                return new int[][]{
                        {1, 1},   //up/right
                        {-1, 1},  //down/right
                        {-1, -1}, //down/left
                        {1, -1}   //up/ left
                };
            }
            case KNIGHT -> {
                return new int[][]{
                        {2, 1},   //up 2 right 1
                        {1, 2},   // up 1 right 2
                        {-1, 2},  // down 1 over 2
                        {-2, 1},  // down 2 right 1
                        {-2, -1}, // down 2 left 1
                        {-1, -2}, // down 1 left2
                        {1, -2},  // up one left 2
                        {2, -1}   // up 2 left 1
                };

            }
            case ROOK -> {
                return new int[][]{
                        {1, 0},  // up
                        {0, 1},  //down
                        {-1, 0}, // right
                        {0, -1}, //left
                };
            }
            case PAWN -> {
                switch (this.getTeamColor()){
                    case WHITE -> {
                        return new int [][]{
                                {1, -1}, // up and right
                                {1, 0},  //up
                                {1, 1},  // up and left
                        };
                    }
                    case BLACK -> {
                        return new int [][]{
                                {-1, -1},  //down right
                                {-1, 0},   //down
                                {-1, 1},   //down and left
                        };
                    }
                }
            }
        }
        return null;
    }

    private boolean spaceIsFull(ChessBoard board, ChessPosition newPos){
        return board.getPiece(newPos) != null;
    }

    private boolean isOutOfBounds(ChessPosition position) {
        return position.getRow() <= 0 || position.getRow() > 8 || position.getColumn() <= 0 || position.getColumn() > 8;

    }

    private Collection<ChessMove> findLinearMoves(ChessBoard board, ChessPosition myPosition, int vert, int horizon) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        for (int i = 1; i < 8; i++) {  // loops through all squares in a straight line
            int nextRow = myPosition.getRow() + (i * vert);
            int nextCol = myPosition.getColumn() + (i * horizon);
            ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

            if (isOutOfBounds(nextPos)) break; //cuz it's out of bounds

            if (spaceIsFull(board, nextPos)) {
                if (board.getPiece(nextPos).getTeamColor() != this.getTeamColor()){ // space is occupied by enemy
                    possibleMoves.add(new ChessMove(myPosition, nextPos, null));
                }//if occupied by own team, break
                break;
            } else possibleMoves.add(new ChessMove(myPosition, nextPos, null));
        }
        return possibleMoves;
    }
    //king and knight, one move only.
    private ChessMove getOneMove(ChessBoard board, ChessPosition myPosition, int vert, int horizon) {
        int nextRow = myPosition.getRow() + vert;
        int nextCol = myPosition.getColumn() + horizon;
        ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

        if (isOutOfBounds(nextPos)) return null;

        if (spaceIsFull(board, nextPos)) {
            if (board.getPiece(nextPos).getTeamColor() != this.getTeamColor()){ // space is occupied by enemy
                return new ChessMove(myPosition, nextPos, null);
            } else return null;
        } else return new ChessMove(myPosition, nextPos, null);
    }

    //pawn stuff
    private ChessMove getPawnMove(ChessBoard board, ChessPosition myPosition, int vert, int horizon) {
        int nextRow = myPosition.getRow() + vert;
        int nextCol = myPosition.getColumn() + horizon;
        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);

        if (isOutOfBounds(nextPosition)) return null;
        boolean occupied = spaceIsFull(board, nextPosition);
        if (horizon != 0){ // diagonal move
            if (occupied && (board.getPiece(nextPosition).getTeamColor() != this.getTeamColor())) { // space is occupied by enemy
                return new ChessMove(myPosition, nextPosition, null);
            }
        }
        if (horizon == 0) {  // straight forward
            if (!occupied) return new ChessMove(myPosition, nextPosition, null);
        }
        return null;
    }

    private boolean isFirstMove(ChessGame.TeamColor teamColor, ChessPosition myPosition) {
        switch (teamColor){
            case WHITE -> {
                if (myPosition.getRow() == 2)return true;
            }
            case BLACK -> {
                if (myPosition.getRow() == 7)return true;
            }
        }
        return false;
    }

    private Collection<ChessMove> getPawnPromotions(ChessMove newMove) {
        ArrayList<ChessMove> promotions = new ArrayList<>();
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.QUEEN));
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.ROOK));
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.KNIGHT));
        promotions.add(new ChessMove(newMove.getStartPosition(), newMove.getEndPosition(), PieceType.BISHOP));
        return promotions;
    }

    private boolean canPromote(ChessMove newMove, ChessGame.TeamColor pieceColor) {
        switch(pieceColor){
            case WHITE -> {
                if (newMove.getEndPosition().getRow() == 8) return true;
            }
            case BLACK -> {
                if (newMove.getEndPosition().getRow() == 1) return true;
            }
        }
        return false;
    }

    //overrides!!
    @Override
    public String toString() {
        return switch (type) {
            //  look at the statement before ?, if true return first, false return second (thanks, Joseph!)
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
