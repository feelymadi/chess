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

    public ChessBoard() {
        board = new char[9][9];
    }

    @Override
    public String toString() {
        String result = "";
        for (int y = 1; y < 9; y++){
            for (int x = 1; x < 9; x++){
                if (this.board[x][y] == '\0') {
                    result += "| ";
                } else {
                    result += "|" + this.board[x][y];
                }
            }
            result += "|\n";
        }
        return result;
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
