package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Queen extends Piece {

    public Queen(int row, int col, Color color, ChessBoard pieceBoard) {
        super(row, col, color, pieceBoard);
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
            Image readyImg = bimg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(readyImg, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Error finding image of piece.");
        }
    }

    // since a queen can essentially do anything a bishop or rook can do, we can take advantage.
    // make sure that there are no castling issues by setting firstMove to false
    @Override
    public boolean isViableMove(int row, int col) {
        Rook testRook = new Rook(this.row, this.col, this.color, false, this.pieceBoard);
        Bishop testBishop = new Bishop(this.row, this.col, this.color, this.pieceBoard);

        return testBishop.isViableMove(row, col) || testRook.isViableMove(row, col);
    }
}
