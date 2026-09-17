package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private String name;
    char[][] board;
    int boardHeight;
    int boardWidth;

    public ChessBoard() {
        boardHeight = 9;
        boardWidth = 9;
        board = new char[boardHeight][boardWidth];
    }

    @Override
    public String toString() {
        String result = "";
        for (int row = boardHeight - 1; row >= 1; row--){
            for (int col = 1; col < boardWidth; col++){
                if (this.board[row][col] == '\0') {
                    result += "| ";
                } else {
                    result += "|" + this.board[row][col];
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
                if (this.board[row][col] != that.board[row][col]) {
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}
