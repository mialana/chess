package org.cis120.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Chess extends JPanel {
    public ChessBoard mainCb;
    public static int cellSize = 75;
    private Piece curr = null;

    public Chess() {
        this.mainCb = ChessBoard.newGameBoard();
    }

    public Chess(String filePath) {
        BufferedReader br = null;
        List<String> pieceLines = new ArrayList<>();
        List<List<String>> pieceSplit = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(filePath));
            String next = br.readLine();
            while (next != null) {
                pieceLines.add(next);
                next = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        for (String line : pieceLines) {
            List<String> splitString = new ArrayList<>();
            for (String s : line.split(",")) {
                splitString.add(s);
            }
            pieceSplit.add(splitString);
        }
        Iterator<List<String>> iter = pieceSplit.iterator();

        Map<String, Color> colorMap = Map.of("BLACK", Color.BLACK, "WHITE", Color.WHITE);
        ChessBoard cb = new ChessBoard(colorMap.get(iter.next().get(0)));
        this.mainCb = cb;
        while (iter.hasNext()) {
            List<String> list = iter.next();
            try {
                if (list.size() == 5) {
                    cb.addPiece(
                            (Piece) Class.forName(list.get(0)).getConstructor(
                                    int.class, int.class, Color.class, boolean.class,
                                    ChessBoard.class
                            ).newInstance(
                                    Integer.parseInt(list.get(1)), Integer.parseInt(list.get(2)),
                                    colorMap.get(list.get(3)), Boolean.parseBoolean(list.get(4)), cb
                            )
                    );
                } else {
                    cb.addPiece(
                            (Piece) Class.forName(list.get(0)).getConstructor(
                                    int.class, int.class, Color.class, ChessBoard.class
                            ).newInstance(
                                    Integer.parseInt(list.get(1)), Integer.parseInt(list.get(2)),
                                    colorMap.get(list.get(3)), cb
                            )
                    );
                }
            } catch (Exception e) {
                System.out.println(e + "failed adding piece from csv");
            }
        }
    }

    public void setClickedPiece(Piece p) {
        this.curr = p;
    }

    public void paintBackground(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle2D rec = new Rectangle(i * cellSize, j * cellSize, cellSize, cellSize);
                if ((i + j) % 2 == 0) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(Color.gray);
                }
                g2.fill(rec);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(cellSize * 8, cellSize * 8);
    }

    public void paintPieces(ChessBoard cb, Graphics2D g2) {
        for (Piece p : cb.whiteList) {
            p.draw(g2, cellSize);
        }
        for (Piece p : cb.whiteList) {
            p.draw(g2, cellSize);
        }

    }

    public void paintLegalMoves(Graphics2D g2) {
        if (curr == null) {
            return;
        } else if (curr.color != mainCb.whoseTurn) {
            return;
        }
        Color transparentBlue = new Color(0, 0, 255, 100);
        g2.setColor(transparentBlue);
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (curr.legalMove(row, column)) {
                    g2.fillRect(column * cellSize, row * cellSize, cellSize, cellSize);
                    System.out.println(row + " " + column);
                }
            }
        }
    }

    public void paintCheck(Graphics2D g2) {
        for (Piece p : mainCb.blackList) {
            if (p instanceof King) {
                if (((King) p).isCheck()) {
                    g2.setColor(new Color(255, 0, 0, 100));
                    g2.fillRect(p.column * cellSize, p.row * cellSize, cellSize, cellSize);
                }
            }
        }
        for (Piece p : mainCb.whiteList) {
            if (p instanceof King) {
                if (((King) p).isCheck()) {
                    g2.setColor(new Color(255, 0, 0, 100));
                    g2.fillRect(p.column * cellSize, p.row * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    public void paintStaleCheckmate(Graphics g2) {
        for (Piece p : mainCb.blackList) {
            if (p instanceof King) {
                if (((King) p).isStaleCheckmate()) {
                    g2.setFont(new Font("", Font.BOLD, cellSize));
                    g2.setColor(Color.RED);
                    if (((King) p).isCheck()) {
                        g2.drawString("CHECKMATE!", 0, 3 * cellSize);
                        g2.drawString("WHITE WINS!", 0, 5 * cellSize);
                    } else {
                        g2.drawString("STALEMATE!", 0, 5 * cellSize);
                    }
                }
            }
        }
        for (Piece p : mainCb.whiteList) {
            if (p instanceof King) {
                if (((King) p).isStaleCheckmate()) {
                    g2.setFont(new Font("", Font.BOLD, cellSize));
                    g2.setColor(Color.RED);
                    if (((King) p).isCheck()) {
                        g2.drawString("CHECKMATE!", 0, 3 * cellSize);
                        g2.drawString("BLACK WINS!", 0, 5 * cellSize);
                    } else {
                        g2.drawString("STALEMATE!", 0, 5 * cellSize);
                    }
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("", Font.BOLD, Chess.cellSize / 5));
        paintBackground(g2);
        paintPieces(mainCb, g2);
        paintLegalMoves(g2);
        paintCheck(g2);
        paintStaleCheckmate(g2);
        g2.setColor(new Color(0, 255, 0, 100));
        if (curr != null) {
            g2.fillRect(curr.column * cellSize, curr.row * cellSize, cellSize, cellSize);
        }
    }

    public void saveGame(String filePath) {
        File file = Paths.get(filePath).toFile();
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(file, false));
            if (mainCb.whoseTurn == Color.WHITE) {
                bw.write("WHITE");
            }
            if (mainCb.whoseTurn == Color.BLACK) {
                bw.write("BLACK");
            }
            for (Piece p : mainCb.whiteList) {
                bw.newLine();
                bw.write(p.getClass().getName() + "," + p.row + "," + p.column + "," + "WHITE");
                if (p instanceof Pawn || p instanceof King || p instanceof Rook) {
                    bw.write("," + p.firstMove);
                }
            }
            for (Piece p : mainCb.blackList) {
                bw.newLine();
                bw.write(p.getClass().getName() + "," + p.row + "," + p.column + "," + "BLACK");
                if (p instanceof Pawn || p instanceof King || p instanceof Rook) {
                    bw.write("," + p.firstMove);
                }
            }
            bw.close();
        } catch (IOException e1) {
            System.out.println("IOException in writeStringsToFile");
        }
    }
}
