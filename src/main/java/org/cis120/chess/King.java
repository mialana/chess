package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(int row, int col, Color color, boolean firstMove, ChessBoard mainBoard) {
        super(row, col, color, mainBoard);
        this.firstMove = firstMove;
    }

    @Override
    public boolean movePiece(int row, int col) {
        if (this.isLegalMove(row, col) && mainBoard.whoseTurn == this.color &&
                this.hasTeammate(row, col)) {
            Piece castleRook = this.mainBoard.findPiece(row, col);
            if (castleRook.col > this.col) {
                this.col = this.col + 2;
                castleRook.col = this.col - 1;
            } else {
                this.col = this.col - 2;
                castleRook.col = this.col + 1;
            }
            castleRook.firstMove = false;
            this.firstMove = false;
            if (this.mainBoard.whoseTurn == Color.WHITE) {
                this.mainBoard.whoseTurn = Color.BLACK;
            } else {
                this.mainBoard.whoseTurn = Color.WHITE;
            }
            this.mainBoard.updateBoard();
            return true;
        } else {
            boolean moved = super.movePiece(row, col);
            if (moved) {
                this.firstMove = false;
            }
            return moved;
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
            if (this.mainBoard.findPiece(row, col) instanceof Rook &&
                    ((Rook) this.mainBoard.findPiece(row, col)).firstMove) {
                // makes sure none of the places in between king and rook are occupied
                for (int i = Math.min(this.col, col) + 1; i < Math
                        .max(this.col, col); i++) {
                    if (this.mainBoard.isOccupied(this.row, i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bimg = null;
        try {
            if (this.color == Color.WHITE) {
                bimg = ImageIO.read(new File("files/king_white.png"));
            } else {
                bimg = ImageIO.read(new File("files/king_black.png"));
            }
            Image img = bimg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(img, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Image does not exist");
        }
    }

    // determines if king is in check
    public boolean inCheck() {
        // sets oppPieces to the opponent's pieces
        List<Piece> oppPieces = new ArrayList<>();
        if (this.color == Color.WHITE) {
            oppPieces = this.mainBoard.blackList;
        } else {
            oppPieces = this.mainBoard.whiteList;
        }
        for (Piece oppP : oppPieces) {
            // if other player can move to where King is, they are in check.
            if (oppP.isViableMove(this.row, this.col)) {
                return true;
            }
            // only if
            boolean canMove;
            if (oppP.color == Color.WHITE) {
                oppP.color = Color.BLACK;
                canMove = oppP.isViableMove(this.row, this.col);
                oppP.color = Color.WHITE;
            } else {
                oppP.color = Color.WHITE;
                canMove = oppP.isViableMove(this.row, this.col);
                oppP.color = Color.BLACK;
            }
            if (canMove) {
                System.out.println("canMove");
                return true;
            }
        }
        return false;
    }


    public boolean isGameOver() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (this.color == Color.WHITE) {
                    for (Piece p : this.mainBoard.whiteList) {
                        if (p.isLegalMove(row, col)) {
                            return false;
                        }
                    }
                } else {
                    for (Piece p : this.mainBoard.blackList) {
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
