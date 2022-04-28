package org.cis120.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class Chess extends JPanel {
    public ChessBoard mainCb;
    public static int cellSize = 75;
    private Piece clicked = null;

    public Chess(String filePath) {
        // splits filePath into individual words.
        BufferedReader br;
        List<String> fileLines = new ArrayList<>();
        List<List<String>> fileWords = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(filePath));
            String next = br.readLine();
            while (next != null) {
                fileLines.add(next);
                next = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("error in finding/reading file");
        }
        for (String line : fileLines) {
            List<String> splitLine = new ArrayList<>();
            Collections.addAll(splitLine, line.split(","));
            fileWords.add(splitLine);
        }

        Iterator<List<String>> wordIter = fileWords.iterator();

        // initializes chessboard with designated starter color.
        Map<String, Color> colorMap = Map.of("WHITE", Color.WHITE, "BLACK", Color.BLACK);
        ChessBoard cb = new ChessBoard(colorMap.get(wordIter.next().get(0)));
        this.mainCb = cb;

        while (wordIter.hasNext()) {
            List<String> wordsList = wordIter.next();
            try {
                if (wordsList.size() == 5) {
                    // finds constructor for class listed in col one of line (either pawn or rook)
                    // and makes new instance of that class.
                    // second column row, third column col, fourth column owner,
                    // fifth col passant/firstMove
                    // adds created instance to desired list
                    cb.addPieceToList(
                            (Piece) Class.forName(wordsList.get(0)).getConstructor(
                                    int.class, int.class, Color.class, boolean.class,
                                    ChessBoard.class
                            ).newInstance(
                                    Integer.parseInt(wordsList.get(1)), Integer.parseInt(wordsList.get(2)),
                                    colorMap.get(wordsList.get(3)), Boolean.parseBoolean(wordsList.get(4)), cb
                            )
                    );
                } else {
                    // same logic except non-pawn and non-rook
                    cb.addPieceToList(
                            (Piece) Class.forName(wordsList.get(0)).getConstructor(
                                    int.class, int.class, Color.class, ChessBoard.class
                            ).newInstance(
                                    Integer.parseInt(wordsList.get(1)), Integer.parseInt(wordsList.get(2)),
                                    colorMap.get(wordsList.get(3)), cb
                            )
                    );
                }
            } catch (Exception e) {
                System.out.println("failed adding piece specified from line.");
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(cellSize * 8, cellSize * 8);
    }

    public void setClicked(Piece clicked) {
        this.clicked = clicked;
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
                bw.write(p.getClass().getName() + "," + p.row + "," + p.col + "," + "WHITE");
                if (p instanceof Pawn || p instanceof King || p instanceof Rook) {
                    bw.write("," + p.firstMove);
                }
            }
            for (Piece p : mainCb.blackList) {
                bw.newLine();
                bw.write(p.getClass().getName() + "," + p.row + "," + p.col + "," + "BLACK");
                if (p instanceof Pawn || p instanceof King || p instanceof Rook) {
                    bw.write("," + p.firstMove);
                }
            }
            bw.close();
        } catch (IOException e1) {
            System.out.println("IOException in writeStringsToFile");
        }
    }

    // below are all helper methods for painting
    private void paintBoard(Graphics2D g2D) {
        // handles painting each cell appropriate color
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle2D cell = new Rectangle(i * cellSize, j * cellSize, cellSize, cellSize);
                if ((i + j) % 2 == 0) {
                    g2D.setColor(new Color(255, 241, 220));
                } else {
                    g2D.setColor(new Color(182, 148, 110));
                }
                // handles filling of each cell
                g2D.fill(cell);
            }
        }
    }

    private List<List<Piece>> makePieceListList() {
        List<List<Piece>> pieceListList = new LinkedList<List<Piece>>();
        pieceListList.add(mainCb.blackList);
        pieceListList.add(mainCb.whiteList);
        return pieceListList;
    }

    // calls the draw method for each individual piece
    private void paintChessPieces(Graphics2D g2D) {
        List<List<Piece>> pieceListList = makePieceListList();
        for (List<Piece> pl : pieceListList) {
            for (Piece p : pl) {
                p.draw(g2D, cellSize);
            }
        }
    }

    private void paintViableAndLegalMoves(Graphics2D g2D) {
        // paint all moves that user is allowed to move to next.
        if (clicked == null) {
            return;
        } else if (clicked.color != mainCb.whoseTurn) {
            return;
        }
        // not exactly sure how to use alpha component, please ignore the varying colors of green.
        g2D.setColor(new Color(110, 182, 146, 125));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // isLegalMove checks if isViableMove inside
                if (clicked.isLegalMove(row, col)) {
                    // based on orientation of chessboard, column and row has to be switched
                    Rectangle2D cell = new Rectangle(col * cellSize, row * cellSize, cellSize, cellSize);
                    g2D.fill(cell);
                }
            }
        }
    }

    private void paintCheck(Graphics2D g2D) {
        // makes an updated list of lists with all the pieces.
        List<List<Piece>> pieceListList = makePieceListList();

        // goes through both piece lists and determines if their kings are in check.
        // paints king red if piece is now in check
        for (List<Piece> pl : pieceListList) {
            for (Piece p : pl) {
                if (p instanceof King) {
                    if (((King) p).inCheck()) {
                        g2D.setColor(new Color(182, 112, 110, 125));
                        g2D.fillRect(p.col * cellSize, p.row * cellSize, cellSize, cellSize);
                    }
                }
            }
        }
    }

    private void paintGameConclusion(Graphics g2D) {
        List<List<Piece>> pieceListList = makePieceListList();
        for (List<Piece> pl : pieceListList) {
            for (Piece p : pl) {
                if (p instanceof King) {
                    if (((King) p).isGameOver()) {
                        g2D.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD | Font.ITALIC, cellSize / 2));
                        g2D.setColor(new Color(119, 140, 201));
                        if (((King) p).inCheck()) {
                            g2D.drawString("CHECKMATE!", ((5 * cellSize) / 2), 4 * cellSize);
                        } else {
                            g2D.drawString("STALEMATE!", (5 / 2) * cellSize, 4 * cellSize);
                        }
                    }
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        // called in RunChess every time ChessBoardListener is triggered (whenever user clicks
        // anywhere on board)
        // also called when new game is loaded or saved game is loaded
        Graphics2D g2D = (Graphics2D) g;
        paintBoard(g2D);
        paintChessPieces(g2D);
        paintViableAndLegalMoves(g2D);
        paintCheck(g2D);
        paintGameConclusion(g2D);
        // paints the clicked piece
        if (clicked != null) {
            g2D.setColor(new Color(255, 255, 255, 125));
            g2D.fillRect(clicked.col * cellSize, clicked.row * cellSize, cellSize, cellSize);
        }
    }
}
