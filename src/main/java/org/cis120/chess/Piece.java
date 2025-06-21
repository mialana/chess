package org.cis120.chess;

import java.awt.*;

public abstract class Piece {
    ChessBoard pieceBoard;
    Color color;

    int row;
    int col;

    boolean firstMove;

    // each piece keeps track of their position, which player they belong to, and their board.
    public Piece(int row, int col, Color color, ChessBoard pieceBoard) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.pieceBoard = pieceBoard;
    }

    public abstract boolean isViableMove(int row, int col);

    public abstract void draw(Graphics2D g2D, int size);

    public Color getColor() {
        return this.color;
    }

    public boolean hasOpponent(int row, int col) {
        if (!this.pieceBoard.isOccupied(row, col)) {
            return false;
        }
        return this.pieceBoard.findPiece(row, col).color != this.color;
    }

    public boolean hasTeammate(int row, int col) {
        if (!this.pieceBoard.isOccupied(row, col)) {
            return false;
        }
        return this.pieceBoard.findPiece(row, col).color == this.color;
    }

    // this method actually performs the move
    public boolean movePiece(int row, int col) {
        // if the clicked cell is legal (and viable) and it's that person's turn.
        if (this.isLegalMove(row, col) && pieceBoard.whoseTurn == this.color) {
            // handles "eating" of enemy's piece.
            if (this.pieceBoard.isOccupied(row, col)) {
                this.pieceBoard.whiteList.remove(this.pieceBoard.findPiece(row, col));
                this.pieceBoard.blackList.remove(this.pieceBoard.findPiece(row, col));
            }
            // change's piece's position to new position
            this.row = row;
            this.col = col;
            // after a move, then should make sure all pawns cannot be passant.
            if (this.pieceBoard.whoseTurn == Color.WHITE) {
                for (Piece p : this.pieceBoard.blackList) {
                    if (p instanceof Pawn) {
                        ((Pawn) p).canBePassant = false;
                    }
                }
                // change turns
                this.pieceBoard.whoseTurn = Color.BLACK;
            } else {
                for (Piece p : this.pieceBoard.whiteList) {
                    if (p instanceof Pawn) {
                        ((Pawn) p).canBePassant = false;
                    }
                }
                this.pieceBoard.whoseTurn = Color.WHITE;
            }

            // update board, verify that move successful with return true.
            this.pieceBoard.updateBoard();
            return true;
        }
        return false;
    }

    // checks if moving to the spot will put the king into check, adds piece back into suitable list.
    public boolean isLegalMove(int row, int col) {
        if (this.isViableMove(row, col)) {
            // save actual position
            int actualRow = this.row;
            int actualCol = this.col;

            // find piece
            Piece foundPiece = this.pieceBoard.findPiece(row, col);
            this.row = row;
            this.col = col;

            // remove the piece from the board and update
            this.pieceBoard.whiteList.remove(foundPiece);
            this.pieceBoard.blackList.remove(foundPiece);
            this.pieceBoard.updateBoard();

            // checks if king is now in check
            boolean inCheck = false;
            if (this.color == Color.WHITE) {
                for (Piece p : this.pieceBoard.whiteList) {
                    if (p instanceof King) {
                        inCheck = ((King) p).inCheck();
                    }
                }
            }
            if (this.color == Color.BLACK) {
                for (Piece p : this.pieceBoard.blackList) {
                    if (p instanceof King) {
                        inCheck = ((King) p).inCheck();
                    }
                }
            }
            this.row = actualRow;
            this.col = actualCol;

            // adds piece back
            if (foundPiece != null) {
                this.pieceBoard.addPieceToList(foundPiece);
            }
            this.pieceBoard.updateBoard();
            return !inCheck;
        }
        return false;
    }
}
