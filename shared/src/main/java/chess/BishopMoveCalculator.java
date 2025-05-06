package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator extends THEcalculator{
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessGame.TeamColor pieceColor;

    BishopMoveCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor){
        this.board = board;
        this.position = position;
        this.pieceColor = pieceColor;
    }

    //override for collection for chessMove array?? put @Override here??
    public Collection<ChessMove> pieceMoves(){
        Collection<ChessMove> array = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        //DIAGONAL movements, leftUp, leftDown, rightUp, rightDown, while outOfBoundsCheck is good
        ChessPosition leftUp = new ChessPosition(row+1, col-1);
        while(outOfBoundsCheck(leftUp)){
            if(board.getPiece(leftUp) == null) {
                ChessMove move = new ChessMove(position, leftUp, null);
                array.add(move);
                leftUp = new ChessPosition(leftUp.getRow() + 1, leftUp.getColumn() - 1);
            }
            else if (board.getPiece(leftUp)!=null && board.getPiece(leftUp).getTeamColor() != pieceColor){
                ChessMove move = new ChessMove(position, leftUp, null);
                array.add(move);
                break;
            }
            else{break;}
        }
        ChessPosition leftDown = new ChessPosition(row-1, col-1);
        while(outOfBoundsCheck(leftDown)){
            if(board.getPiece(leftDown) == null) {
                ChessMove move = new ChessMove(position, leftDown, null);
                array.add(move);
                leftDown = new ChessPosition(leftDown.getRow() - 1, leftDown.getColumn() - 1);
            }
            else if (board.getPiece(leftDown)!=null && board.getPiece(leftDown).getTeamColor() != pieceColor){
                //if its not an empty space but the space has a piece from the other team,
                //the accepted move to add to the moves collection array would make the space null
                ChessMove move = new ChessMove(position, leftDown, null);
                array.add(move);
                break;
            }
            else{break;}
        }
        ChessPosition RightUp = new ChessPosition(row+1, col+1);
        while(outOfBoundsCheck(RightUp)){
            if(board.getPiece(RightUp) == null) {
                ChessMove move = new ChessMove(position, RightUp, null);
                array.add(move);
                RightUp = new ChessPosition(RightUp.getRow() + 1, RightUp.getColumn() - 1);
            }
            else if (board.getPiece(RightUp)!=null && board.getPiece(RightUp).getTeamColor() != pieceColor){
                ChessMove move = new ChessMove(position, RightUp, null);
                array.add(move);
                break;
            }
            else{break;}
        }
        ChessPosition rightDown = new ChessPosition(row+1, col+1);
        while(outOfBoundsCheck(rightDown)){
            if(board.getPiece(rightDown) == null) {
                ChessMove move = new ChessMove(position, rightDown, null);
                array.add(move);
                RightUp = new ChessPosition(rightDown.getRow() + 1, rightDown.getColumn() - 1);
            }
            else if (board.getPiece(rightDown)!=null && board.getPiece(rightDown).getTeamColor() != pieceColor){
                ChessMove move = new ChessMove(position, rightDown, null);
                array.add(move);
                break;
            }
            else{break;}
        }
        //we have checked all diagonal options to array and can now return it :)
        return array;
    }

    //check out of bounds with end position
    private boolean outOfBoundsCheck(ChessPosition end) {
        int endRow = end.getRow();
        int endCol = end.getColumn();
        if (endRow <= 0 || endRow >= 9 || endCol <= 0 || endCol >= 9 ) {
            return false;
        }
        else {
            return true;
        }
    }
}
