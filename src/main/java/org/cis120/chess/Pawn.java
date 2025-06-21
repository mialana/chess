package org.cis120.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Pawn extends Piece {
    boolean canBePassant = false;

    public Pawn(int row, int col, Color color, boolean firstMove, ChessBoard pieceBoard) {
        super(row, col, color, pieceBoard);
        this.firstMove = firstMove;
    }

    @Override
    public void draw(Graphics2D g2D, int size) {
        BufferedImage bImg = null;
        try {
            if (this.color == Color.WHITE) {
                bImg = ImageIO.read(new File("files/pawn_white.png"));
            } else {
                bImg = ImageIO.read(new File("files/pawn_black.png"));
            }
            Image readyImg = bImg.getScaledInstance(size, size, Image.SCALE_FAST);
            g2D.drawImage(readyImg, size * col, size * row, null);
        } catch (IOException e) {
            System.out.println("Error finding image of piece.");
        }
    }

    @Override
    public boolean movePiece(int row, int col) {
        // will never occur that is legal move and diagonal move and not occupied space if
        // not in an en passant situation
        if (this.isLegalMove(row, col) && pieceBoard.whoseTurn == this.color && Math.abs(this.row - row) == 1
                && Math.abs(this.col - col) == 1 && !this.pieceBoard.isOccupied(row, col)) {
            // delete passant-ed pawn and move pawn to desired location
            this.pieceBoard.whiteList.remove(this.pieceBoard.findPiece(this.row, col));
            this.pieceBoard.blackList.remove(this.pieceBoard.findPiece(this.row, col));
            this.row = row;
            this.col = col;
            if (this.pieceBoard.whoseTurn == Color.WHITE) {
                this.pieceBoard.whoseTurn = Color.BLACK;
            } else {
                this.pieceBoard.whoseTurn = Color.WHITE;
            }
            this.pieceBoard.updateBoard();
            return true;
        } else if (this.isLegalMove(row, col) && pieceBoard.whoseTurn == this.color
                && Math.abs(this.row - row) == 2) {
            // after any 2-step move, a piece becomes prone to en passant.
            this.canBePassant = true;
            boolean wasMoved = super.movePiece(row, col);
            this.firstMove = false;
            return wasMoved;
        } else {
            // handles normal (1 step forward move)
            boolean wasMoved = super.movePiece(row, col);
            if (wasMoved) {
                // cause "normal" pieces don't care about firstMove
                this.firstMove = false;
            }
            // handles pawn swap
            if (this.row == 0 || this.row == 7) {
                RunChess.pawnSwap(this, Chess.cellSize, RunChess.getChess());
            }
            return wasMoved;
        }
    }

    @Override
    public boolean isViableMove(int row, int col) {
        if (this.col == col && !pieceBoard.isOccupied(row, col)) {
            if (this.color == Color.WHITE) {
                // handles normal movement (1 step forward)
                if (this.row - row == 1) {
                    return true;
                }
                // handles 2 step forward movement
                if (this.row - row == 2 && !pieceBoard.isOccupied(this.row - 1, col)
                        && this.firstMove) {
                    return true;
                }
            }
            if (this.color == Color.BLACK) {
                // same as above except flipped
                if (this.row - row == -1) {
                    return true;
                }
                if (this.row - row == -2 && !pieceBoard.isOccupied(this.row + 1, col)
                        && this.firstMove) {
                    return true;
                }
            }
        }
        // handles diagonal movement (when pawn "eats" a piece)
        if (Math.abs(this.col - col) == 1 && this.hasOpponent(row, col)) {
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
        // handles en passant movement
        if (Math.abs(this.col - col) == 1 && this.hasOpponent(this.row, col)) {
            Piece possiblePassant = this.pieceBoard.findPiece(this.row, col);
            if (possiblePassant instanceof Pawn) {
                if (((Pawn) possiblePassant).canBePassant) {
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
}