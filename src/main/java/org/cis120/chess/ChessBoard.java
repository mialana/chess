package org.cis120.chess;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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

    public void update() {
        this.board = new Piece[8][8];
        for (Piece p : whiteList) {
            this.board[p.row][p.column] = p;
        }
        for (Piece p : blackList) {
            this.board[p.row][p.column] = p;
        }
    }

    public Piece[][] getBoard() {
        return board.clone();
    }

    public boolean hasPiece(int row, int column) {
        return (board[row][column] != null);
    }

    public Piece findPiece(int row, int column) {
        return board[row][column];
    }

    public void addPiece(Piece p) {
        if (p.color == Color.WHITE) {
            this.whiteList.add(p);
        } else {
            this.blackList.add(p);
        }
        this.update();
    }

    static ChessBoard newGameBoard() {
        System.out.println("new board called");
        ChessBoard gb = new ChessBoard(Color.WHITE);
        for (int i = 0; i < 8; i++) {
            gb.addPiece(new Pawn(6, i, Color.WHITE, true, gb));
            gb.addPiece(new Pawn(1, i, Color.BLACK, true, gb));
        }
        gb.addPiece(new King(7, 4, Color.WHITE, true, gb));
        gb.addPiece(new King(0, 4, Color.BLACK, true, gb));

        gb.addPiece(new Queen(7, 3, Color.WHITE, gb));
        gb.addPiece(new Queen(0, 3, Color.BLACK, gb));

        gb.addPiece(new Rook(7, 0, Color.WHITE, true, gb));
        gb.addPiece(new Rook(0, 0, Color.BLACK, true, gb));
        gb.addPiece(new Rook(7, 7, Color.WHITE, true, gb));
        gb.addPiece(new Rook(0, 7, Color.BLACK, true, gb));

        gb.addPiece(new Knight(7, 1, Color.WHITE, gb));
        gb.addPiece(new Knight(0, 1, Color.BLACK, gb));
        gb.addPiece(new Knight(7, 6, Color.WHITE, gb));
        gb.addPiece(new Knight(0, 6, Color.BLACK, gb));

        gb.addPiece(new Bishop(7, 2, Color.WHITE, gb));
        gb.addPiece(new Bishop(0, 2, Color.BLACK, gb));
        gb.addPiece(new Bishop(7, 5, Color.WHITE, gb));
        gb.addPiece(new Bishop(0, 5, Color.BLACK, gb));

        return gb;
    }
}