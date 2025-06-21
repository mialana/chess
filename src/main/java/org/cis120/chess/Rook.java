package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Rook extends Piece {

    public Rook(int row, int col, Color color, boolean firstMove, ChessBoard pieceBoard) {
        super(row, col, color, pieceBoard);
        this.firstMove = firstMove;
    }


    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bImg = null;
        try {
            if (this.color == Color.WHITE) {
                bImg = ImageIO.read(new File("files/rook_white.png"));
            } else {
                bImg = ImageIO.read(new File("files/rook_black.png"));
            }
            Image readyImg = bImg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(readyImg, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Error finding image of piece.");
        }
    }

    // rook cares about whether first move due to castling
    // only small change needed
    @Override
    public boolean movePiece(int row, int col) {
        boolean moved = super.movePiece(row, col);
        if (moved) {
            this.firstMove = false;
        }
        return moved;
    }

    @Override
    public boolean isViableMove(int row, int col) {
        // if same place, return false
        if (this.row == row && this.col == col) {
            return false;
        }
        // if same row
        if (this.row == row) {
            for (int i = Math.min(this.col, col) + 1; i < Math
                    .max(this.col, col); i++) {
                // if wants to go through pieces
                if (this.pieceBoard.isOccupied(row, i)) {
                    return false;
                }
            }
            // if there is a teammate there, return false
            return !this.hasTeammate(row, col);
        }
        // same thing as above except with columns
        if (this.col == col) {
            for (int i = Math.min(this.row, row) + 1; i < Math.max(this.row, row); i++) {
                if (this.pieceBoard.isOccupied(i, col)) {
                    return false;
                }
            }
            return !this.hasTeammate(row, col);
        }
        return false;
    }

}