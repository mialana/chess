package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Knight extends Piece {

    public Knight(int row, int col, Color color, ChessBoard pieceBoard) {
        super(row, col, color, pieceBoard);
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bImg = null;
        try {
            if (this.color == Color.WHITE) {
                bImg = ImageIO.read(new File("files/knight_white.png"));
            } else {
                bImg = ImageIO.read(new File("files/knight_black.png"));
            }
            Image readyImg = bImg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(readyImg, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Error finding image of piece.");
        }
    }

    @Override
    public boolean isViableMove(int row, int col) {
        if (this.hasTeammate(row, col)) {
            return false;
        }
        // accounts for diagonal movement of knight
        return Math.abs((this.row - row) * (this.col - col)) == 2;
    }
}