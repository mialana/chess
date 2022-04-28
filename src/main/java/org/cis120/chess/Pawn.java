package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Pawn extends Piece {
    boolean passant = false;

    public Pawn(int row, int col, Color color, boolean firstMove, ChessBoard mainBoard) {
        super(row, col, color, mainBoard);
        this.firstMove = firstMove;
    }

    @Override
    public boolean movePiece(int row, int col) {
        if (this.isLegalMove(row, col) && mainBoard.whoseTurn == this.color && Math.abs(this.row - row) == 1
                &&
                Math.abs(this.col - col) == 1 && !this.mainBoard.isOccupied(row, col)) {
            this.mainBoard.whiteList.remove(this.mainBoard.findPiece(this.row, col));
            this.mainBoard.blackList.remove(this.mainBoard.findPiece(this.row, col));
            this.row = row;
            this.col = col;
            if (this.mainBoard.whoseTurn == Color.WHITE) {
                this.mainBoard.whoseTurn = Color.BLACK;
            } else {
                this.mainBoard.whoseTurn = Color.WHITE;
            }
            this.mainBoard.updateBoard();
            return true;
        } else if (this.isLegalMove(row, col) && mainBoard.whoseTurn == this.color
                && Math.abs(this.row - row) == 2) {
            this.passant = true;
            boolean wasMoved = super.movePiece(row, col);
            this.firstMove = false;
            return wasMoved;
        } else {
            boolean wasMoved = super.movePiece(row, col);
            if (wasMoved) {
                this.firstMove = false;
            }
            if (this.row == 0 || this.row == 7) {
                RunChess.pawnSwap(this, Chess.cellSize, RunChess.getChess());
            }
            return wasMoved;
        }
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bimg = null;
        try {
            if (this.color == Color.WHITE) {
                bimg = ImageIO.read(new File("files/pawn_white.png"));
            } else {
                bimg = ImageIO.read(new File("files/pawn_black.png"));
            }
            Image img = bimg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(img, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Image does not exist");
        }
    }

    @Override
    public boolean isViableMove(int row, int col) {
        if (this.col == col && !mainBoard.isOccupied(row, col)) // moving piece forward
        {
            if (this.color == Color.WHITE) {
                if (this.row - row == 1) {
                    return true;
                }
                if (this.row - row == 2 && !mainBoard.isOccupied(this.row - 1, col)
                        && this.firstMove) {
                    return true;
                }
            }
            if (this.color == Color.BLACK) {
                if (this.row - row == -1) {
                    return true;
                }
                if (this.row - row == -2 && !mainBoard.isOccupied(this.row + 1, col)
                        && this.firstMove) {
                    return true;
                }
            }
        }
        if (Math.abs(this.col - col) == 1 && this.hasOpponent(row, col)) // taking piece
                                                                                    // diagonally
        {
            if (this.color == Color.WHITE) {
                if (this.row - row == 1) {
                    return true;
                }
            }
            if (this.color == Color.BLACK) {
                if (this.row - row == -1) {
                    return true;
                }
            }
        }
        if (Math.abs(this.col - col) == 1 && this.hasOpponent(this.row, col)) // passant
        {
            Piece p = this.mainBoard.findPiece(this.row, col);
            if (p instanceof Pawn) {
                if (((Pawn) p).passant) {
                    if (this.color == Color.WHITE) {
                        if (this.row - row == 1) {
                            return true;
                        }
                    } else {
                        if (this.row - row == -1) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    @Override
    public boolean isLegalMove(int row, int col) {
        if (this.isViableMove(row, col)) {
            int actualRow = this.row;
            int actualCol = this.col;
            
            this.row = row;
            this.col = col;
            this.mainBoard.updateBoard();
            
            boolean kingInCheck = super.isInCheck(actualRow,actualCol);
            this.mainBoard.updateBoard();
            return !kingInCheck;
        }
        return false;

    }

}