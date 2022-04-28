package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Queen extends Piece {

    public Queen(int row, int col, Color color, ChessBoard mainBoard) {
        super(row, col, color, mainBoard);
    }

    @Override
    public boolean isViableMove(int row, int col) {
        Bishop b = new Bishop(this.row, this.col, this.color, this.mainBoard);
        Rook r = new Rook(this.row, this.col, this.color, false, this.mainBoard);
        return b.isViableMove(row, col) || r.isViableMove(row, col);
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bimg = null;
        try {
            if (this.color == Color.WHITE) {
                bimg = ImageIO.read(new File("files/queen_white.png"));
            } else {
                bimg = ImageIO.read(new File("files/queen_black.png"));
            }
            Image img = bimg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(img, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Image does not exist");
        }
    }
}
