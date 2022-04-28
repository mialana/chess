package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Knight extends Piece {

    public Knight(int row, int col, Color color, ChessBoard mainBoard) {
        super(row, col, color, mainBoard);
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bimg = null;
        try {
            if (this.color == Color.WHITE) {
                bimg = ImageIO.read(new File("files/knight_white.png"));
            } else {
                bimg = ImageIO.read(new File("files/knight_black.png"));
            }
            Image img = bimg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(img, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Image does not exist");
        }
    }

    @Override
    public boolean isViableMove(int row, int col) {
        if (this.hasTeammate(row, col)) {
            return false;
        }
        if (Math.abs((this.row - row) * (this.col - col)) == 2) {
            return true;
        }
        return false;
    }
}