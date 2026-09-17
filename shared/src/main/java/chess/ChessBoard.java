package chess;

import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private String name;
    ChessPiece[][] board;
    int boardHeight;
    int boardWidth;

    public ChessBoard() {
        boardHeight = 9;
        boardWidth = 9;
        board = new ChessPiece[boardHeight][boardWidth];
    }

    @Override
    public String toString() {
        String result = "";
        for (int row = boardHeight - 1; row >= 1; row--){
            for (int col = 1; col < boardWidth; col++){
                if (this.board[row][col] == null) {
                    result += "| ";
                } else {
                    result += "|" + this.board[row][col].toString();
                }
            }
            result += "|\n";
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        if ((this.boardWidth != that.boardWidth) || (this.boardHeight != that.boardHeight)){
            return false;
        }
        for (int row = 1; row < this.boardHeight; row++){
            for (int col = 1; col < this.boardWidth; col++){
                ChessPiece thisPiece = this.board[row][col];
                ChessPiece thatPiece = that.board[row][col];
                if (!Objects.equals(thisPiece, thatPiece)) {
                    return false;
                }
            }
        }
        return true;
    }

        /**
         * Adds a chess piece to the chessboard
         *
         * @param position where to add the piece to
         * @param piece    the piece to add
         */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        this.board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return this.board[row][col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board = new ChessPiece[boardHeight][boardWidth];
    }
}
