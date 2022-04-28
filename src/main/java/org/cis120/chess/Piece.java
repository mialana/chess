package org.cis120.chess;

import java.awt.*;

public abstract class Piece {
    ChessBoard mainBoard;
    Color color;

    int row;
    int col;

    boolean firstMove;

    // each piece keeps track of their position, which player they belong to, and their board.
    public Piece(int row, int col, Color color, ChessBoard mainBoard) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.mainBoard = mainBoard;
    }

    public abstract boolean isViableMove(int row, int col);

    public abstract void draw(Graphics2D g2D, int size);

    public Color getColor() {
        return this.color;
    }

    public boolean hasOpponent(int row, int col) {
        if (!this.mainBoard.isOccupied(row, col)) {
            return false;
        }
        return this.mainBoard.findPiece(row, col).getColor() != this.color;
    }

    public boolean hasTeammate(int row, int col) {
        if (!this.mainBoard.isOccupied(row, col)) {
            return false;
        }
        return this.mainBoard.findPiece(row, col).getColor() == this.color;
    }

    // this method actually performs the move
    public boolean movePiece(int row, int col) {
        // if the clicked cell is legal (and viable) and it's that person's turn.
        if (this.isLegalMove(row, col) && mainBoard.whoseTurn == this.color) {
            // handles "eating" of enemy's piece.
            if (this.mainBoard.isOccupied(row, col)) {
                this.mainBoard.whiteList.remove(this.mainBoard.findPiece(row, col));
                this.mainBoard.blackList.remove(this.mainBoard.findPiece(row, col));
            }
            // change's piece's position to new position
            this.row = row;
            this.col = col;
            // ADD COMMENT LATER
            if (this.mainBoard.whoseTurn == Color.WHITE) {
                for (Piece p : this.mainBoard.blackList) {
                    if (p instanceof Pawn) {
                        ((Pawn) p).passant = false;
                    }
                }
                // change turns
                this.mainBoard.whoseTurn = Color.BLACK;
            } else {
                for (Piece p : this.mainBoard.whiteList) {
                    if (p instanceof Pawn) {
                        ((Pawn) p).passant = false;
                    }
                }
                this.mainBoard.whoseTurn = Color.WHITE;
            }

            // update board, verify that move successful with return true.
            this.mainBoard.updateBoard();
            return true;
        }
        return false;
    }

    // check if the king will be in check with given row/col, then returns piece back to original row/col
    public boolean isInCheck(int actualRow, int actualCol) {
        
        boolean inCheck = false;
        if (this.color == Color.WHITE) {
            for (Piece p : this.mainBoard.whiteList) {
                if (p instanceof King) {
                    inCheck = ((King) p).inCheck();
                }
            }
        }
        if (this.color == Color.BLACK) {
            for (Piece p : this.mainBoard.blackList) {
                if (p instanceof King) {
                    inCheck = ((King) p).inCheck();
                }
            }
        }
        this.row = actualRow;
        this.col = actualCol;
        return inCheck;
    }

    // checks if moving to the spot will put the king into check, adds piece back into suitable list.
    public boolean isLegalMove(int row, int col) {
        if (this.isViableMove(row, col)) {
            // save actual position
            int actualRow = this.row;
            int actualCol = this.col;

            // find piece
            Piece foundPiece = this.mainBoard.findPiece(row, col);
            this.row = row;
            this.col = col;

            // remove the piece from the board and update
            this.mainBoard.whiteList.remove(foundPiece);
            this.mainBoard.blackList.remove(foundPiece);
            this.mainBoard.updateBoard();

            // checks if king is now in check
            boolean inCheck = isInCheck(actualRow, actualCol);

            // adds piece back
            if (foundPiece != null) {
                this.mainBoard.addPieceToList(foundPiece);
            }
            this.mainBoard.updateBoard();
            return !inCheck;
        }
        return false;
    }
}
