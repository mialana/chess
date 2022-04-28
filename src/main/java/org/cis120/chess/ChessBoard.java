package org.cis120.chess;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

// keeps track of current board, whose turn it is, and lists of white and black pieces.
// has helper functions to update board, find the piece at a certain location, see if a
// location is occupied, and add a piece to the desired list.
public class ChessBoard {
    Piece[][] board;
    Color whoseTurn;

    List<Piece> whiteList;
    List<Piece> blackList;

    public ChessBoard(Color whoseTurn) {
        this.board = new Piece[8][8];
        this.whoseTurn = whoseTurn;
        this.whiteList = new ArrayList<>();
        this.blackList = new ArrayList<>();
    }

    public Piece findPiece(int row, int col) {
        return board[row][col];
    }

    public boolean isOccupied(int row, int col) {
        return (board[row][col] != null);
    }

    public void updateBoard() {
        this.board = new Piece[8][8];
        for (Piece p : whiteList) {
            this.board[p.row][p.col] = p;
        }
        for (Piece p : blackList) {
            this.board[p.row][p.col] = p;
        }
    }

    public void addPieceToList(Piece p) {
        if (p.color == Color.WHITE) {
            this.whiteList.add(p);
        } else {
            this.blackList.add(p);
        }
        this.updateBoard();
    }
}