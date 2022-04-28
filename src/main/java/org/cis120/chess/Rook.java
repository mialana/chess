package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Rook extends Piece {

    public Rook(int row, int col, Color color, boolean firstMove, ChessBoard mainBoard) {
        super(row, col, color, mainBoard);
        this.firstMove = firstMove;
    }

    @Override
    public boolean movePiece(int row, int col) {
        boolean moved = super.movePiece(row, col);
        if (moved) {
            this.firstMove = false;
        }
        return moved;
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bimg = null;
        try {
            if (this.color == Color.WHITE) {
                bimg = ImageIO.read(new File("files/rook_white.png"));
            } else {
                bimg = ImageIO.read(new File("files/rook_black.png"));
            }
            Image img = bimg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(img, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Image does not exist");
        }
    }

    @Override
    public boolean isViableMove(int row, int col) {
        if (this.row == row && this.col == col) {
            return false;
        }
        if (this.row == row) {
            for (int i = Math.min(this.col, col) + 1; i < Math
                    .max(this.col, col); i++) {
                if (this.mainBoard.isOccupied(row, i)) {
                    return false;
                }
            }
            return !this.hasTeammate(row, col);
        }
        if (this.col == col) {
            for (int i = Math.min(this.row, row) + 1; i < Math.max(this.row, row); i++) {
                if (this.mainBoard.isOccupied(i, col)) {
                    return false;
                }
            }
            return !this.hasTeammate(row, col);
        }
        return false;
    }

}