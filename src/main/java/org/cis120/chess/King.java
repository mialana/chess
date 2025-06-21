package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(int row, int col, Color color, boolean firstMove, ChessBoard pieceBoard) {
        super(row, col, color, pieceBoard);
        this.firstMove = firstMove;
    }


    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bImg = null;
        try {
            if (this.color == Color.WHITE) {
                bImg = ImageIO.read(new File("files/king_white.png"));
            } else {
                bImg = ImageIO.read(new File("files/king_black.png"));
            }
            Image readyImg = bImg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(readyImg, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Error finding image of piece.");
        }
    }

    // handles castling movement
    @Override
    public boolean movePiece(int row, int col) {
        // if is legal, it's their turn, and the piece in the desired position is on their team.
        // won't ever be legal and hasTeammate in a non-castling situation
        if (this.isLegalMove(row, col) && pieceBoard.whoseTurn == this.color &&
                this.hasTeammate(row, col)) {
            Piece foundRook = this.pieceBoard.findPiece(row, col);
            // right side castle
            if (foundRook.col > this.col) {
                this.col = this.col + 2;
                foundRook.col = this.col - 1;
            } else {
                // left side castle
                this.col = this.col - 2;
                foundRook.col = this.col + 1;
            }
            // both have moved now.
            foundRook.firstMove = false;
            this.firstMove = false;
            if (this.pieceBoard.whoseTurn == Color.WHITE) {
                this.pieceBoard.whoseTurn = Color.BLACK;
            } else {
                this.pieceBoard.whoseTurn = Color.WHITE;
            }
            this.pieceBoard.updateBoard();
            return true;
        } else {
            boolean wasMoved = super.movePiece(row, col);
            if (wasMoved) {
                this.firstMove = false;
            }
            return wasMoved;
        }
    }

    @Override
    public boolean isViableMove(int row, int col) {
        // can't move to where it already is
        if (this.row == row && this.col == col) {
            return false;
        }
        // returns true if position is 1 spot away from where it is, and there's no pieces there
        if (Math.abs(this.row - row) <= 1 && Math.abs(this.col - col) <= 1) {
            return !this.hasTeammate(row, col);
        }
        // handles castling logic
        // checks that king is still at first move
        if (this.firstMove && this.hasTeammate(row, col)) {
            // checks that rook is still at first move
            if (this.pieceBoard.findPiece(row, col) instanceof Rook &&
                    ((Rook) this.pieceBoard.findPiece(row, col)).firstMove) {
                // makes sure none of the places in between king and rook are occupied
                for (int i = Math.min(this.col, col) + 1; i < Math
                        .max(this.col, col); i++) {
                    if (this.pieceBoard.isOccupied(this.row, i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // determines if king is in check
    public boolean inCheck() {
        // sets oppPieces to the opponent's pieces
        List<Piece> oppPieces = new ArrayList<>();
        if (this.color == Color.WHITE) {
            oppPieces = this.pieceBoard.blackList;
        } else {
            oppPieces = this.pieceBoard.whiteList;
        }
        for (Piece oppP : oppPieces) {
            // if other player can move to where King is, they are in check.
            if (oppP.isViableMove(this.row, this.col)) {
                return true;
            }
        }
        return false;
    }

    // game over occurs when none of the pieces can make any moves
    public boolean isGameOver() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (this.color == Color.WHITE) {
                    for (Piece p : this.pieceBoard.whiteList) {
                        if (p.isLegalMove(row, col)) {
                            return false;
                        }
                    }
                } else {
                    for (Piece p : this.pieceBoard.blackList) {
                        if (p.isLegalMove(row, col)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
