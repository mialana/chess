package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bishop extends Piece {

    public Bishop(int row, int col, Color color, ChessBoard mainBoard) {
        super(row, col, color, mainBoard);
    }

    @Override
    public boolean isViableMove(int row, int col) {
        if (Math.abs(this.row - row) == Math.abs(this.col - col) && this.row != row) {
            if (this.row > row) {
                if (this.col > col) {
                    for (int i = 1; i < Math.abs(this.row - row); i++) {
                        if (this.mainBoard.isOccupied(this.row - i, this.col - i)) {
                            return false;
                        }
                    }
                } else {
                    for (int i = 1; i < Math.abs(this.row - row); i++) {
                        if (this.mainBoard.isOccupied(this.row - i, this.col + i)) {
                            return false;
                        }
                    }
                }
            } else {
                if (this.col > col) {
                    for (int i = 1; i < Math.abs(this.row - row); i++) {
                        if (this.mainBoard.isOccupied(this.row + i, this.col - i)) {
                            return false;
                        }
                    }
                } else {
                    for (int i = 1; i < Math.abs(this.row - row); i++) {
                        if (this.mainBoard.isOccupied(this.row + i, this.col + i)) {
                            return false;
                        }
                    }
                }
            }
            return !this.hasTeammate(row, col);
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bimg = null;
        try {
            if (this.color == Color.WHITE) {
                bimg = ImageIO.read(new File("files/bishop_white.png"));
            } else {
                bimg = ImageIO.read(new File("files/bishop_black.png"));
            }
            Image img = bimg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(img, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Image does not exist");
        }
    }

}