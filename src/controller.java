import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class controller {

    private static final int PREF_W = 900;
    private static final int PREF_H = 600;

    private JFrame frame;
    private DrawPanel panel;

    private final List<VisualObject> visualObjects = new ArrayList<>();

    private final Object commandLock = new Object();
    private volatile Character lastCommand = null;

    public void initialize(String windowTitle) {
        if (frame != null) return;

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame(windowTitle == null ? "Dice2" : windowTitle);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            panel = new DrawPanel();
            panel.setPreferredSize(new Dimension(PREF_W, PREF_H));
            panel.setBackground(new Color(20, 120, 60)); // table green

            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            installKeyBindings(panel);
            panel.setFocusable(true);
            panel.requestFocusInWindow();
        });
    }

    public void render(Player p1, Player p2, List<DiceFace> diceFaces) {
        Objects.requireNonNull(p1, "p1");
        Objects.requireNonNull(p2, "p2");

        List<VisualObject> next = new ArrayList<>();
        int margin = 30;

        // Player 1 (top)
        next.add(new VisualLabel(margin, margin, p1.getName(), new Font("SansSerif", Font.BOLD, 18)));
        next.add(new VisualCard(
                margin,
                margin + 30,
                180,
                240,
                p1.getCurrentCard() == null ? "(no card)" : p1.getCurrentCard().toString()
        ));

        // Player 2 (bottom)
        int bottomY = PREF_H - margin - 310;
        next.add(new VisualLabel(margin, bottomY - 30, p2.getName(), new Font("SansSerif", Font.BOLD, 18)));
        next.add(new VisualCard(
                margin,
                bottomY,
                180,
                240,
                p2.getCurrentCard() == null ? "(no card)" : p2.getCurrentCard().toString()
        ));

        // Dice (center)
        int diceStartX = margin + 240;
        int diceStartY = (PREF_H / 2) - 40;
        int dieSize = 70;
        int gap = 12;

        if (diceFaces != null) {
            for (int i = 0; i < diceFaces.size(); i++) {
                DiceFace face = diceFaces.get(i);
                String label = (face == null) ? "?" : face.toString();
                int x = diceStartX + i * (dieSize + gap);
                next.add(new VisualDie(x, diceStartY, dieSize, dieSize, label));
            }
        }

        SwingUtilities.invokeLater(() -> {
            visualObjects.clear();
            visualObjects.addAll(next);
            if (panel != null) panel.repaint();
        });
    }


    public char waitForCommand(Set<Character> allowed) {
        Objects.requireNonNull(allowed, "allowed");

        while (true) {
            Character c;
            synchronized (commandLock) {
                while (lastCommand == null) {
                    try {
                        commandLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return 'n';
                    }
                }
                c = lastCommand;
                lastCommand = null;
            }

            char normalized = Character.toLowerCase(c);
            if (allowed.contains(normalized)) return normalized;
        }
    }

    private void installKeyBindings(JComponent target) {
        InputMap im = target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = target.getActionMap();

        bindKey(im, am, "y", () -> pushCommand('y'));
        bindKey(im, am, "n", this::closeWindow);
        bindKey(im, am, "r", () -> pushCommand('r'));
        bindKey(im, am, "t", () -> pushCommand('t'));
    }

    private static void bindKey(InputMap im, ActionMap am, String key, Runnable action) {
        String actionName = "cmd_" + key;

        im.put(KeyStroke.getKeyStroke(key), actionName);
        im.put(KeyStroke.getKeyStroke(key.toUpperCase()), actionName);

        am.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void pushCommand(char c) {
        synchronized (commandLock) {
            lastCommand = c;
            commandLock.notifyAll();
        }
    }

    // ----------------------------
    // Visual-side model (NOT logic)
    // ----------------------------

    private abstract static class VisualObject {
        final int x;
        final int y;

        VisualObject(int x, int y) {
            this.x = x;
            this.y = y;
        }

        abstract void draw(Graphics2D g2);
    }

    private static final class VisualLabel extends VisualObject {
        private final String text;
        private final Font font;

        VisualLabel(int x, int y, String text, Font font) {
            super(x, y);
            this.text = text;
            this.font = font;
        }

        @Override
        void draw(Graphics2D g2) {
            g2.setFont(font);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y + g2.getFontMetrics().getAscent());
        }
    }

    private static final class VisualCard extends VisualObject {
        private final int w;
        private final int h;
        private final String label;

        VisualCard(int x, int y, int w, int h, String label) {
            super(x, y);
            this.w = w;
            this.h = h;
            this.label = label;
        }

        @Override
        void draw(Graphics2D g2) {
            g2.setColor(new Color(245, 245, 245));
            g2.fillRoundRect(x, y, w, h, 18, 18);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, w, h, 18, 18);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
            g2.setColor(Color.BLACK);

            int padding = 10;
            drawWrappedText(g2, label, x + padding, y + padding, w - padding * 2, h - padding * 2);
        }
    }

    private static final class VisualDie extends VisualObject {
        private final int w;
        private final int h;
        private final String faceLabel;

        VisualDie(int x, int y, int w, int h, String faceLabel) {
            super(x, y);
            this.w = w;
            this.h = h;
            this.faceLabel = faceLabel;
        }

        @Override
        void draw(Graphics2D g2) {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, w, h, 14, 14);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, w, h, 14, 14);

            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - fm.stringWidth(faceLabel)) / 2;
            int ty = y + (h + fm.getAscent()) / 2 - 4;
            g2.drawString(faceLabel, tx, ty);
        }
    }

    private final class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (VisualObject vo : visualObjects) {
                    vo.draw(g2);
                }
            } finally {
                g2.dispose();
            }
        }
    }

    public void closeWindow() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
                frame = null;
                panel = null;
            }
        });
        // If you want the whole program to stop:
        System.exit(0);
    }

    private static void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxW, int maxH) {
        if (text == null) return;

        FontMetrics fm = g2.getFontMetrics();
        int lineH = fm.getHeight();
        int curY = y + fm.getAscent();

        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String next = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(next) <= maxW) {
                line.setLength(0);
                line.append(next);
                continue;
            }

            if (curY - y > maxH) return;
            g2.drawString(line.toString(), x, curY);
            curY += lineH;

            line.setLength(0);
            line.append(word);
        }

        if (!line.isEmpty() && (curY - y) <= maxH) {
            g2.drawString(line.toString(), x, curY);
        }
    }
}
