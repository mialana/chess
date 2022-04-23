package org.cis120.chess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class RunChess implements Runnable {
    private static Chess mainChess;

    @Override
    public void run() {
        final JFrame frame = new JFrame("Chess");
        frame.setLayout(new BorderLayout());
        frame.setSize(8 * Chess.cellSize, 8 * Chess.cellSize);
        RunChess.mainChess = new Chess("files/newGame.txt");

        JButton saveButton = new JButton("SAVE GAME");
        JButton loadButton = new JButton("LOAD GAME");
        JButton newGameButton = new JButton("NEW GAME");

        String whoseTurn = "";
        if (mainChess.mainCb.whoseTurn == Color.WHITE) {
            whoseTurn = "WHITE's TURN";
        } else {
            whoseTurn = "BLACK's TURN";
        }

        JLabel turn_Label = new JLabel(whoseTurn);
        turn_Label.setForeground(Color.WHITE);

        JPanel button_Panel = new JPanel();
        button_Panel.add(saveButton);
        button_Panel.add(loadButton);
        button_Panel.add(newGameButton);
        button_Panel.add(turn_Label);
        button_Panel.setBackground(Color.DARK_GRAY);

        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainChess.saveGame("files/savedGame.txt");
                System.out.println("Game successfully saved");
            }
        });
        loadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainChess.mainCb = new Chess("files/savedGame.txt").mainCb;
                mainChess.repaint();
                System.out.println("Game successfully loaded");
            }
        });
        newGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainChess.mainCb = new Chess("files/newGame.txt").mainCb;
                mainChess.repaint();
                System.out.println("New game successfully loaded");
            }
        });

        mainChess.addMouseListener(new ChessBoardListener(mainChess, turn_Label, button_Panel));

        frame.add(mainChess, BorderLayout.CENTER);
        frame.add(button_Panel, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(false);

        // instructions frame
        final JFrame instructions = new JFrame("Instructions");
        instructions.setSize(400, 350);

        final JPanel instr_panel = new JPanel();
        instr_panel.setLayout(new BorderLayout());

        String text = "\nINSTRUCTIONS: \n- Click once to select a piece and click again to attempt to "
                +
                "move or unselect the piece. \n- If the desired move is illegal, reselect a piece to move. "
                +
                "It is still your turn. \n\nSPECIAL FEATURES: \n- When a pawn gets to the other side of the board, "
                +
                "you will be given the choice to select a Queen, Rook, Knight, or Bishop to swap it with. \n"
                +
                "- Rules regarding Castling and En passant are the same as in a classic game of chess. \n"
                +
                "- Click the new game button to start a new game. \n" +
                "- Click the save game button to save game. NOTE: This will erase the previous saved game, "
                +
                "and you can only save 1 game. \n" +
                "- Click the load game button to load the last saved game.\n";
        JTextArea textArea = new JTextArea();
        textArea.setText(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        instr_panel.add(textArea);

        final JButton instr_close = new JButton("Continue to Game");
        instr_close.addActionListener(e -> {
            frame.setVisible(true);
            instructions.setVisible(false);
        });
        instr_panel.add(instr_close, BorderLayout.PAGE_END);

        instructions.add(instr_panel);
        instructions.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        instructions.setVisible(true);
    }

    private static void addSwapLabel(JPanel swap_panel, int size, String pathname) {
        try {
            swap_panel.add(
                    new JLabel(
                            new ImageIcon(
                                    ImageIO.read(new File(pathname))
                                            .getScaledInstance(size, size, Image.SCALE_FAST)
                            )
                    )
            );
        } catch (IOException e) {
            System.out.println("error finding file.");
        }

    }

    // allow pawn to swap with another piece
    public static void pawnSwap(Pawn p, int size, Chess cg) {
        JFrame swap_frame = new JFrame();
        swap_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel swap_panel = new JPanel();
        swap_frame.add(swap_panel);

        if (p.color == Color.BLACK) {
            addSwapLabel(swap_panel, size, "files/bqueen.png");
            addSwapLabel(swap_panel, size, "files/bknight.png");
            addSwapLabel(swap_panel, size, "files/bbishop.png");
            addSwapLabel(swap_panel, size, "files/brook.png");
        } else {
            addSwapLabel(swap_panel, size, "files/wqueen.png");
            addSwapLabel(swap_panel, size, "files/wknight.png");
            addSwapLabel(swap_panel, size, "files/wbishop.png");
            addSwapLabel(swap_panel, size, "files/wrook.png");
        }

        swap_panel.addMouseListener(new PawnSwapListener(p, swap_frame, cg));

        swap_frame.pack();
        swap_frame.setVisible(true);
    }

    public static Chess getChess() {
        return RunChess.mainChess;
    }
}

// these mouseListener classes are too long to use as anonymous classes
class ChessBoardListener extends MouseAdapter {
    private final Chess cg;
    private final JLabel jl;
    private final JPanel jp;
    Piece curr = null;

    public ChessBoardListener(Chess cg, JLabel jl, JPanel jp) {
        this.cg = cg;
        this.jl = jl;
        this.jp = jp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("user clicked on game board");
        if (cg.mainCb.hasPiece(e.getY() / Chess.cellSize, e.getX() / Chess.cellSize)
                && curr == null) {
            this.curr = cg.mainCb.findPiece(e.getY() / Chess.cellSize, e.getX() / Chess.cellSize);
            cg.setClickedPiece(curr);
        } else if (curr != null) {
            curr.move(e.getY() / Chess.cellSize, e.getX() / Chess.cellSize);
            cg.setClickedPiece(null);
            curr = null;
        }
        cg.repaint();
        if (cg.mainCb.whoseTurn == Color.WHITE) {
            jl.setText("WHITE's TURN");
        } else {
            jl.setText("BLACK's TURN");
        }
        jp.repaint();
    }
}

class PawnSwapListener extends MouseAdapter {
    private final Pawn p;
    private final Chess cg;
    private final JFrame frame;

    public PawnSwapListener(Pawn p, JFrame frame, Chess cg) {
        this.p = p;
        this.frame = frame;
        this.cg = cg;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("successfully chose piece to swap");
        p.board.whiteList.remove(p);
        p.board.blackList.remove(p);
        if ((e.getX() / Chess.cellSize) == 0) {
            p.board.addPiece(new Queen(p.row, p.column, p.color, p.board));
        } else if (e.getX() / Chess.cellSize == 1) {
            p.board.addPiece(new Knight(p.row, p.column, p.color, p.board));
        } else if (e.getX() / Chess.cellSize == 2) {
            p.board.addPiece(new Bishop(p.row, p.column, p.color, p.board));
        } else {
            p.board.addPiece(new Rook(p.row, p.column, p.color, false, p.board));
        }
        cg.repaint();
        frame.setVisible(false);
    }
}