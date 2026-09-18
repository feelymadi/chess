package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        String result = "";
        if (this.type == PieceType.KING) {
            result = "k";
        } else if (this.type == PieceType.QUEEN) {
            result = "q";
        } else if (this.type == PieceType.BISHOP) {
        result = "b";
        } else if (this.type == PieceType.KNIGHT) {
            result = "n";
        } else if (this.type == PieceType.ROOK) {
            result = "r";
        } else if (this.type == PieceType.PAWN) {
            result = "p";
        } else {
            return "ChessPiece{" +
                    "pieceColor=" + pieceColor +
                    ", type=" + type +
                    '}';
        }

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return result.toUpperCase(Locale.ROOT);
        } else {
            return result;
        }
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
    public ChessGame.TeamColor getTeamColor() { return this.pieceColor; }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();
        if (piece.getPieceType() == PieceType.PAWN) {

            // set direction factor
            int directionFactor;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                directionFactor = 1;
            } else {
                directionFactor = -1;
            }
            // check for promotion
            if ((myPosition.getRow() == 2 && piece.getTeamColor() == ChessGame.TeamColor.BLACK) || (myPosition.getRow() == board.boardHeight-2 && piece.getTeamColor() == ChessGame.TeamColor.WHITE)) {
                // moves forward with promo
                if (board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn())) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+directionFactor,myPosition.getColumn()),PieceType.QUEEN));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()+1)) != null && board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()+1)).getTeamColor() != (piece.getTeamColor())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()+1),PieceType.QUEEN));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()-1)) != null && board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()+1)).getTeamColor() != (piece.getTeamColor())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()-1),PieceType.QUEEN));
                }
            } else {
                // moves forward
                // forward
                if (board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn())) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+directionFactor,myPosition.getColumn()),null));
                }
                //right diagonal
                if (board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()+1)) != null && board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()+1)).getTeamColor() != (piece.getTeamColor())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()+1),null));
                }
                // left diagonal
                if (board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()-1)) != null && board.getPiece(new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()-1)).getTeamColor() != (piece.getTeamColor())) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+directionFactor, myPosition.getColumn()-1),null));
                }

            }

            // first move
            if (myPosition.getRow() == 2 || myPosition.getRow() == board.boardHeight-2) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+(2*directionFactor),myPosition.getColumn()),null));
            }

        }


        return moves;
    }
}
