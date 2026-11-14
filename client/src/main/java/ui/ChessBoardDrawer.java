package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardDrawer {

    public void drawBoard(ChessBoard board, ChessGame.TeamColor teamColor) {
        // WHITE
        if (teamColor == ChessGame.TeamColor.WHITE){
            System.out.println("    a  b  c  d  e  f  g  h");
            for (int row = 8; row >= 1; row--) {
                System.out.print(" " + row + " ");
                for (int col = 1; col <= 8; col++) {
                    drawSquare(board, row, col);
                }
                System.out.print(" " + row + " ");
                System.out.println();
            }
            System.out.println("    a  b  c  d  e  f  g  h");
        // BLACK
        } else {
            System.out.println("    h  g  f  e  d  c  b  a");
            for (int row = 1; row <= 8; row++) {
                System.out.print(" " + row + " ");
                for (int col = 8; col >= 1; col--) {
                    drawSquare(board, row, col);
                }
                System.out.print(" " + row + " ");
                System.out.println();
            }
            System.out.println("    h  g  f  e  d  c  b  a");
        }
    }

    private void drawSquare(ChessBoard board, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);

        String textColor = EscapeSequences.RESET_TEXT_COLOR;

        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                textColor = EscapeSequences.SET_TEXT_COLOR_RED;
            } else {
                textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
            }
        }

        if ((row + col) % 2 == 0) {
            System.out.print(
                    EscapeSequences.SET_BG_COLOR_BLACK +
                            textColor +
                            choosePiece(piece) +
                            EscapeSequences.RESET_TEXT_COLOR +
                            EscapeSequences.RESET_BG_COLOR
            );
        } else {
            System.out.print(
                    EscapeSequences.SET_BG_COLOR_WHITE +
                            textColor +
                            choosePiece(piece) +
                            EscapeSequences.RESET_TEXT_COLOR +
                            EscapeSequences.RESET_BG_COLOR
            );
        }
    }

    private String choosePiece (ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return EscapeSequences.WHITE_PAWN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            return EscapeSequences.BLACK_PAWN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return EscapeSequences.WHITE_ROOK;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            return EscapeSequences.BLACK_ROOK;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return EscapeSequences.WHITE_KNIGHT;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            return EscapeSequences.BLACK_KNIGHT;
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return EscapeSequences.WHITE_BISHOP;
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            return EscapeSequences.BLACK_BISHOP;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return EscapeSequences.WHITE_KING;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            return EscapeSequences.BLACK_KING;
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return EscapeSequences.WHITE_QUEEN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            return EscapeSequences.BLACK_QUEEN;
        }
        return EscapeSequences.EMPTY;
    }


}
