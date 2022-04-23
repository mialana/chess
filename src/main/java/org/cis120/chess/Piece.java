package org.cis120.chess;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    int row;
    int column;
    ChessBoard board;
    Color color;
    boolean firstMove;

    public Piece(int row, int column, Color color, ChessBoard board) {
        this.row = row;
        this.column = column;
        this.color = color;
        this.board = board;
    }

    public boolean move(int row, int column) {
        if (this.legalMove(row, column) && board.whoseTurn == this.color) {
            if (this.board.hasPiece(row, column)) {
                this.board.whiteList.remove(this.board.findPiece(row, column));
                this.board.blackList.remove(this.board.findPiece(row, column));
            }
            this.row = row;
            this.column = column;
            if (this.board.whoseTurn == Color.WHITE) {
                for (Piece p : this.board.blackList) {
                    if (p instanceof Pawn) {
                        ((Pawn) p).passant = false;
                    }
                }
                this.board.whoseTurn = Color.BLACK;
            } else {
                for (Piece p : this.board.whiteList) {
                    if (p instanceof Pawn) {
                        ((Pawn) p).passant = false;
                    }
                }
                this.board.whoseTurn = Color.WHITE;
            }

            this.board.update();
            return true;
        }
        return false;
    }

    public boolean legalMove(int row, int column) {
        if (this.moveNormallyLegal(row, column)) {
            int or = this.row;
            int oc = this.column;
            Piece piece = this.board.findPiece(row, column);
            this.row = row;
            this.column = column;
            this.board.whiteList.remove(piece);
            this.board.blackList.remove(piece);
            this.board.update();
            boolean kingInCheck = false;
            if (this.color == Color.WHITE) {
                for (Piece p : this.board.whiteList) {
                    if (p instanceof King) {
                        kingInCheck = ((King) p).isCheck();
                    }
                }
            }
            if (this.color == Color.BLACK) {
                for (Piece p : this.board.blackList) {
                    if (p instanceof King) {
                        kingInCheck = ((King) p).isCheck();
                    }
                }
            }
            this.row = or;
            this.column = oc;
            if (piece != null) {
                this.board.addPiece(piece);
            }
            this.board.update();
            return !kingInCheck;
        }
        return false;
    }

    public abstract boolean moveNormallyLegal(int row, int column);

    public abstract void draw(Graphics2D g2, int size);

    public Color getColor() {
        return this.color;
    }

    public boolean spaceInCheck(int row, int column) {
        List<Piece> enemyPieces = new ArrayList<>();
        if (this.color == Color.WHITE) {
            enemyPieces = this.board.blackList;
        } else {
            enemyPieces = this.board.whiteList;
        }
        for (Piece ep : enemyPieces) {
            if (ep.moveNormallyLegal(row, column)) {
                return true;
            }
            boolean ret;
            if (ep.color == Color.WHITE) {
                ep.color = Color.BLACK;
                ret = ep.moveNormallyLegal(row, column);
                ep.color = Color.WHITE;
            } else {
                ep.color = Color.WHITE;
                ret = ep.moveNormallyLegal(row, column);
                ep.color = Color.BLACK;
            }
            if (ret) {
                return true;
            }
        }
        return false;
    }

    public Color getOtherColor() {
        if (this.color == Color.WHITE) {
            return Color.BLACK;
        }
        return Color.WHITE;
    }

    public boolean hasTeamPiece(int row, int column) {
        if (!this.board.hasPiece(row, column)) {
            return false;
        }
        return this.board.findPiece(row, column).getColor() == this.color;
    }

    public boolean hasEnemyPiece(int row, int column) {
        if (!this.board.hasPiece(row, column)) {
            return false;
        }
        return this.board.findPiece(row, column).getColor() != this.color;
    }
}
