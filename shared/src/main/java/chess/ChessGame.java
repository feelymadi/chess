package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
        // check if empty
        if (board.getPiece(startPosition) == null){
            return null;
        }

        // get moves and piece
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board,startPosition);
        ChessPiece movingPiece = board.getPiece(startPosition);


        // filter moves
        // wont endanger king
        moves.removeIf(move -> !isSafeMove(board, move, movingPiece.getTeamColor(), movingPiece));

        return moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece;

        // is space empty
        if (board.getPiece(move.getStartPosition()) != null) {
            movingPiece = board.getPiece(move.getStartPosition());
        } else {
            throw new InvalidMoveException("No piece at start position");
        }

        // is this pieces turn
        if (this.getTeamTurn() != movingPiece.getTeamColor()) {
            throw new InvalidMoveException("Moving out of turn");
        }

        // if it cant move to stated location
        ChessBoard newBoard;
        if (!movingPiece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid Move");
        }

        // wont endanger king
        if(!isSafeMove(board, move, movingPiece.getTeamColor(), movingPiece)) {
            throw new InvalidMoveException("Invalid Move: exposes king");
        }

        // make move
        newBoard = board.deepCopy();
        // starting square
        newBoard.addPiece(move.getStartPosition(), null);
        // ending square
        if (move.getPromotionPiece() != null) {
            // promo
            newBoard.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece()));
        } else {
            // no promo
            newBoard.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), movingPiece.getPieceType()));
        }

        // return new board
        this.board = newBoard;

        // switch turns
        if (movingPiece.getTeamColor() == TeamColor.WHITE) {
            this.setTeamTurn(TeamColor.BLACK);
        } else { this.setTeamTurn(TeamColor.WHITE);}
    }

    // return position of specific teams king
    public ChessPosition kingPosition(TeamColor teamColor,ChessBoard board) {
        for (int row = 1;row < 9;row++) {
            for (int col = 1; col < 9;col++){
                ChessPosition pos = new ChessPosition(row,col);
                if (board.getPiece(pos) != null) {
                    ChessPiece piece = board.getPiece(pos);
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        return pos;
                    }
                }

            }
        }
        // probably won't happen
        return null;
    }

    // returns end position of all pieces on specific team
    public Collection<ChessPosition> teamCaptures(ChessBoard board, TeamColor color){
        // team moves
        Collection<ChessPosition> captures = new ArrayList<>();

        // parse board
        for (int row = 1;row < 9;row++) {
            for (int col = 1; col < 9;col++){
                ChessPosition pos = new ChessPosition(row,col);
                if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() == color) {
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

    // makes sure move doesnt expose king
    public boolean isSafeMove (ChessBoard board, ChessMove move, TeamColor color, ChessPiece piece) {
        ChessBoard mockBoard = board.deepCopy();
        ChessPiece mockPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
        // make move
        // starting square
        mockBoard.addPiece(move.getStartPosition(), null);
        // ending square
        if (move.getPromotionPiece() != null) {
            // promo
            mockBoard.addPiece(move.getEndPosition(), new ChessPiece(mockPiece.getTeamColor(), move.getPromotionPiece()));
        } else {
            // no promo
            mockBoard.addPiece(move.getEndPosition(), new ChessPiece(mockPiece.getTeamColor(), mockPiece.getPieceType()));
        }

        // is in check
        // king position
        ChessPosition kingPos = kingPosition(color,mockBoard);


        // chess moves for all opposing pieces
        Collection<ChessPosition> captures;
        if (color == TeamColor.WHITE) {
            captures = teamCaptures(mockBoard,TeamColor.BLACK);
        } else {
            captures = teamCaptures(mockBoard,TeamColor.WHITE);
        }


        for (ChessPosition thisMove : captures) {
            if (thisMove.getRow() == kingPos.getRow() && thisMove.getColumn() == kingPos.getColumn()) {
                // king in check move not safe
                return false;
            }
        }
        // king not in check move safe
        return true;
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
        Collection<ChessPosition> captures;
        if (teamColor == TeamColor.WHITE) {
            captures = teamCaptures(board,TeamColor.BLACK);
        } else {
            captures = teamCaptures(board,TeamColor.WHITE);
        }

        for (ChessPosition move : captures) {
            if (move.getRow() == kingPos.getRow() && move.getColumn() == kingPos.getColumn()) {
                // king in check
                return true;
            }
        }
        // king not in check
        return false;
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
        this.board = board.deepCopy();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return this.board;}
}
