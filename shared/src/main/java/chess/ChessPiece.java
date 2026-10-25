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

    // piece move helper functions

    // spot has piece
    private boolean hasPiece(ChessBoard board, int row, int col){
        ChessPiece pieceOnSpot = board.getPiece(new ChessPosition(row, col));
        return pieceOnSpot != null;
    }

    // piece on spot is opponent
    private boolean isOpponent(ChessBoard board, int row, int col, ChessPiece piece){
        ChessGame.TeamColor colorOnSpot = board.getPiece(new ChessPosition(row, col)).getTeamColor();
        return colorOnSpot != piece.getTeamColor();
    }

    // pawn diagonal
    private void pawnDiag(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPos, ChessPiece piece, int row, int col, boolean promo) {
        // board.getPiece(new ChessPosition(row, col)).getTeamColor() != (piece.getTeamColor())
        if (hasPiece(board, row, col)
                && isOpponent(board, row, col, piece))
        {
            movePawn(moves, myPos, new ChessPosition(row, col), promo);
        }
    }

    // append pawn move with promotion
    private void movePawn (Collection<ChessMove> moves, ChessPosition start, ChessPosition end, boolean promo){
        PieceType[] promotionPieces = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        if (promo){
            for (PieceType promoPiece : promotionPieces) {
                moves.add(new ChessMove(start, end, promoPiece));
            }
        } else {
            moves.add(new ChessMove(start, end, null));
        }

    }

    // all possible knight moves
    private static final int[][] KNIGHT_MOVES = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    // all possible king moves
    private static final int[][] KING_MOVES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    private void moveValidationKingKnight(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece, int row, int col){
        if (!hasPiece(board, row, col)) {
            moves.add(new ChessMove(myPos, new ChessPosition(row, col), null));
        } else if (isOpponent(board, row, col, piece)) {
            moves.add(new ChessMove(myPos, new ChessPosition(row, col), null));
        }
    }

    // slide
    private boolean slide(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece, int row, int col){
        if (!hasPiece(board, row, col)) {
            moves.add(new ChessMove(myPos, new ChessPosition(row, col), null));
            return true;
        } else if (isOpponent(board, row, col, piece)) {
            moves.add(new ChessMove(myPos, new ChessPosition(row, col), null));
            return false;
        } else {
            return false;
        }
    }

    // slide right
    private void slideRight(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int row = myPos.getRow();
        for (int col = myPos.getColumn()+1; col < board.boardWidth; col++) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
        }
    }

    // slide left
    public void slideLeft(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int row = myPos.getRow();
        for (int col = myPos.getColumn()-1; col > 0; col--) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
        }
    }

    // slide up
    public void slideUp(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int col = myPos.getColumn();
        for (int row = myPos.getRow()+1; row < board.boardHeight; row++) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
        }
    }

    // slide down
    public void slideDown(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int col = myPos.getColumn();
        for (int row = myPos.getRow()-1; row > 0; row--) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
        }
    }

    // slide up and Right
    public void slideUpAndRight(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int row = myPos.getRow() + 1;
        int col = myPos.getColumn() + 1;
        while (row < board.boardHeight && col < board.boardWidth) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
            row++;
            col++;
        }
    }

    // slide up and left
    public void slideUpAndLeft(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int row = myPos.getRow() + 1;
        int col = myPos.getColumn() - 1;
        while (row < board.boardHeight && col > 0) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
            row++;
            col--;
        }
    }

    // slide down and right
    public void slideDownAndRight(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int row = myPos.getRow() - 1;
        int col = myPos.getColumn() + 1;
        while (row > 0 && col < board.boardWidth) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
            row--;
            col++;
        }
    }

    // slide down and left
    public void slideDownAndLeft(ChessBoard board, Collection<ChessMove> moves,ChessPosition myPos, ChessPiece piece){
        int row = myPos.getRow() - 1;
        int col = myPos.getColumn() - 1;
        while (row > 0 && col > 0) {
            if (!slide(board, moves, myPos, piece, row, col)) {
                break;
            }
            row--;
            col--;
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        ChessPiece piece = board.getPiece(myPos);
        Collection<ChessMove> moves = new ArrayList<>();
        if (piece.getPieceType() == PieceType.PAWN) {
            // set direction factor
            int directionFactor;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                directionFactor = 1;
            } else {
                directionFactor = -1;
            }
            // possible promotions
            boolean promo;
            promo = (myPos.getRow() == 2 &&
                    piece.getTeamColor() == ChessGame.TeamColor.BLACK)
                    || (myPos.getRow() == board.boardHeight - 2
                    && piece.getTeamColor() == ChessGame.TeamColor.WHITE);
            // possible move
            int row;
            int col;
            // move forward
            row = myPos.getRow()+directionFactor;
            col = myPos.getColumn();
            if (row >= 1 && row < board.boardHeight && col >= 1 && col < board.boardWidth){
                if (board.getPiece(new ChessPosition(row, col)) == null) {
                    movePawn(moves, myPos, new ChessPosition(row,col), promo);
                }
            }
            //right then left diagonal
            row = myPos.getRow()+directionFactor;
            col = myPos.getColumn()+1;
            if (row >= 1 && row < board.boardHeight && col >= 1 && col < board.boardWidth){
                pawnDiag(board, moves, myPos, piece, row, col, promo);
            }
            row = myPos.getRow()+directionFactor;
            col = myPos.getColumn()-1;
            if (row >= 1 && row < board.boardHeight && col >= 1 && col < board.boardWidth){
                pawnDiag(board, moves, myPos, piece, row, col, promo);
            }
            // first move
            row = myPos.getRow()+(2*directionFactor);
            col = myPos.getColumn();
            if (    (piece.getTeamColor() == ChessGame.TeamColor.WHITE && myPos.getRow() == 2
                    && board.getPiece(new ChessPosition(row-1,col))==null && board.getPiece(new ChessPosition(row,col))==null)
                    || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && myPos.getRow() == board.boardHeight-2
                    && board.getPiece(new ChessPosition(row+1,col))==null && board.getPiece(new ChessPosition(row,col))==null)) {
                moves.add(new ChessMove(myPos, new ChessPosition(row,col),null));
            }
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            slideRight(board, moves, myPos, piece);
            slideLeft(board, moves, myPos, piece);
            slideUp(board, moves, myPos, piece);
            slideDown(board, moves, myPos, piece);
        }
        if (piece.getPieceType() == PieceType.BISHOP) {
            slideUpAndRight(board, moves, myPos, piece);
            slideUpAndLeft(board, moves, myPos, piece);
            slideDownAndRight(board, moves, myPos, piece);
            slideDownAndLeft(board, moves, myPos, piece);
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            int row;
            int col;
            for (int[] knightMove : KNIGHT_MOVES) {
                row = myPos.getRow() + knightMove[0];
                col = myPos.getColumn() + knightMove[1];
                if (row > 0 && row < board.boardHeight && col > 0 && col < board.boardWidth) {
                    moveValidationKingKnight(board, moves, myPos, piece, row, col);
                }
            }
        }
        if (piece.getPieceType() == PieceType.KING) {
            int row;
            int col;
            for (int[] kingMove : KING_MOVES) {
                row = myPos.getRow() + kingMove[0];
                col = myPos.getColumn() + kingMove[1];
                if (row > 0 && row < board.boardHeight && col > 0 && col < board.boardWidth) {
                    moveValidationKingKnight(board, moves, myPos, piece, row, col);
                }
            }
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            slideUpAndRight(board, moves, myPos, piece);
            slideUpAndLeft(board, moves, myPos, piece);
            slideDownAndRight(board, moves, myPos, piece);
            slideDownAndLeft(board, moves, myPos, piece);
            slideRight(board, moves, myPos, piece);
            slideLeft(board, moves, myPos, piece);
            slideUp(board, moves, myPos, piece);
            slideDown(board, moves, myPos, piece);
        }
        return moves;
    }
}
