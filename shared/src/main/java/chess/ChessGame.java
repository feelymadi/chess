package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    // variable for turn keeping (0 = white)
    int turn;
    ChessBoard board;

    public ChessGame() {
        this.turn = 0;
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (this.turn == 0){return TeamColor.WHITE;} else {return TeamColor.BLACK;}
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (team == TeamColor.WHITE) {this.turn = 0;} else {this.turn = 1;}
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) != null){
            return board.getPiece(startPosition).pieceMoves(board,startPosition);
        } else {return null;}
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece;
        if (board.getPiece(move.getStartPosition()) != null) {
            movingPiece = board.getPiece(move.getStartPosition());
        } else {
            throw new InvalidMoveException("No piece at start position");
        }
        if (!movingPiece.pieceMoves(board,move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid Move");
        } else {
            // make move
            ChessBoard newBoard = board.deepCopy();
            // starting square
            newBoard.addPiece(move.getStartPosition(),null);
            // ending square
            if (move.getPromotionPiece() != null){
                // promo
                newBoard.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(),move.getPromotionPiece()));
            } else {
                // no promo
                newBoard.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(),movingPiece.getPieceType()));
            }
        }
    }

    public ChessPosition kingPosition(TeamColor teamColor,ChessBoard board) {
        for (int row = 1;row < 9;row++) {
            for (int col = 1; col < 9;col++){
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        // probably won't happen
        return null;
    }

    public Collection<ChessPosition> teamCaptures(ChessBoard board, TeamColor color){
        // team moves
        Collection<ChessPosition> captures = new ArrayList<>();

        // parse board
        for (int row = 1;row < 9;row++) {
            for (int col = 1; col < 9;col++){
                ChessPosition pos = new ChessPosition(row,col);
                if (!(board.getPiece(pos) == null)) {
                    ChessPiece chessPiece = board.getPiece(pos);
                    Collection<ChessMove> moves = chessPiece.pieceMoves(board,pos);
                    for (ChessMove move : moves){
                        captures.add(move.getEndPosition());
                    }
                }
            }
        }
    return captures;
    }




    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // king position
        ChessPosition kingPos = kingPosition(teamColor,board);

        // chess moves for all opposing pieces
        Collection<ChessPosition> captures = teamCaptures(board,teamColor);

        // if king pos in chess moves true
        return captures.contains(kingPos);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return this.board;}
}
