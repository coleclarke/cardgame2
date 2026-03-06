/*
TEST CODE

This program creates multiple objects that implement the
Displayable interface (VisualLabel, VisualCard, VisualDie).

These objects are stored in a list and rendered to the screen
through the DrawPanel class.

Because all objects implement Displayable, the display system
can render any new visual class added later without changing
the rendering framework. This demonstrates a reusable display framework.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;



interface Displayable {

    int getX();
    int getY();

    void draw(Graphics2D g2);
}

public class controller {

    private static final int PREF_W = 900;
    private static final int PREF_H = 600;

    private JFrame frame;
    private DrawPanel panel;

    private final List<Displayable> visualObjects = new ArrayList<>();

    private final Object commandLock = new Object();
    private volatile Character lastCommand = null;

    public void initialize(String windowTitle) {

        if (frame != null) return;

        SwingUtilities.invokeLater(() -> {

            frame = new JFrame(windowTitle == null ? "Dice2" : windowTitle);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            panel = new DrawPanel();
            panel.setPreferredSize(new Dimension(PREF_W, PREF_H));
            panel.setBackground(new Color(20,120,60));

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

        List<Displayable> next = new ArrayList<>();
        int margin = 30;

        next.add(new VisualLabel(margin,margin,p1.getName(),
                new Font("SansSerif",Font.BOLD,18)));

        next.add(new VisualCard(
                margin,
                margin+30,
                180,
                240,
                p1.getCurrentCard()==null?"(no card)":p1.getCurrentCard().toString()
        ));

        int bottomY = PREF_H-margin-230;

        next.add(new VisualLabel(margin,bottomY-30,p2.getName(),
                new Font("SansSerif",Font.BOLD,18)));

        next.add(new VisualCard(
                margin,
                bottomY,
                180,
                240,
                p2.getCurrentCard()==null?"(no card)":p2.getCurrentCard().toString()
        ));

        int diceStartX = margin+240;
        int diceStartY = (PREF_H/2)-40;
        int dieSize = 70;
        int gap = 12;

        if(diceFaces!=null){
            for(int i=0;i<diceFaces.size();i++){

                DiceFace face = diceFaces.get(i);
                String label = face==null?"?":face.toString();

                int x = diceStartX+i*(dieSize+gap);

                next.add(new VisualDie(x,diceStartY,dieSize,dieSize,label));
            }
        }

        next.add(new VisualLabel(275,120,
                "Y to continue, R to restart, N to quit",
                new Font("SansSerif",Font.BOLD,18)));

        SwingUtilities.invokeLater(() -> {

            visualObjects.clear();
            visualObjects.addAll(next);

            if(panel!=null) panel.repaint();
        });
    }

    public char waitForCommand(Set<Character> allowed){

        while(true){

            Character c;

            synchronized(commandLock){

                while(lastCommand==null){
                    try{
                        commandLock.wait();
                    }
                    catch(InterruptedException e){
                        Thread.currentThread().interrupt();
                        return 'n';
                    }
                }

                c = lastCommand;
                lastCommand = null;
            }

            char normalized = Character.toLowerCase(c);

            if(allowed.contains(normalized)) return normalized;
        }
    }

    private void installKeyBindings(JComponent target){

        InputMap im = target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = target.getActionMap();

        bindKey(im,am,"y",()->pushCommand('y'));
        bindKey(im,am,"n",this::closeWindow);
        bindKey(im,am,"r",()->pushCommand('r'));
        bindKey(im,am,"t",()->pushCommand('t'));
    }

    private static void bindKey(InputMap im,ActionMap am,String key,Runnable action){

        String actionName="cmd_"+key;

        im.put(KeyStroke.getKeyStroke(key),actionName);
        im.put(KeyStroke.getKeyStroke(key.toUpperCase()),actionName);

        am.put(actionName,new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                action.run();
            }
        });
    }

    private void pushCommand(char c){

        synchronized(commandLock){

            lastCommand = c;
            commandLock.notifyAll();
        }
    }

    private static final class VisualLabel implements Displayable{

        private final int x;
        private final int y;
        private final String text;
        private final Font font;

        VisualLabel(int x,int y,String text,Font font){

            this.x=x;
            this.y=y;
            this.text=text;
            this.font=font;
        }

        VisualLabel(VisualLabel other){

            this.x=other.x;
            this.y=other.y;
            this.text=other.text;
            this.font=other.font;
        }

        public int getX(){return x;}
        public int getY(){return y;}

        public void draw(Graphics2D g2){

            g2.setFont(font);
            g2.setColor(Color.WHITE);
            g2.drawString(text,x,y+g2.getFontMetrics().getAscent());
        }
    }

    private static final class VisualCard implements Displayable{

        private final int x;
        private final int y;
        private final int w;
        private final int h;
        private final String label;

        VisualCard(int x,int y,int w,int h,String label){

            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
            this.label=label;
        }

        VisualCard(VisualCard other){

            this.x=other.x;
            this.y=other.y;
            this.w=other.w;
            this.h=other.h;
            this.label=other.label;
        }

        public int getX(){return x;}
        public int getY(){return y;}

        public void draw(Graphics2D g2){

            g2.setColor(new Color(245,245,245));
            g2.fillRoundRect(x,y,w,h,18,18);

            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x,y,w,h,18,18);

            g2.drawString(label,x+10,y+20);
        }
    }

    private static final class VisualDie implements Displayable{

        private final int x;
        private final int y;
        private final int w;
        private final int h;
        private final String faceLabel;

        VisualDie(int x,int y,int w,int h,String faceLabel){

            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
            this.faceLabel=faceLabel;
        }

        VisualDie(VisualDie other){

            this.x=other.x;
            this.y=other.y;
            this.w=other.w;
            this.h=other.h;
            this.faceLabel=other.faceLabel;
        }

        public int getX(){return x;}
        public int getY(){return y;}

        public void draw(Graphics2D g2){

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x,y,w,h,14,14);

            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x,y,w,h,14,14);

            g2.drawString(faceLabel,x+w/2,y+h/2);
        }
    }

    private final class DrawPanel extends JPanel{

        protected void paintComponent(Graphics g){

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            try{

                for(Displayable vo : visualObjects){
                    vo.draw(g2);
                }

            }finally{
                g2.dispose();
            }
        }
    }

    public void closeWindow(){

        SwingUtilities.invokeLater(()->{

            if(frame!=null){

                frame.dispose();
                frame=null;
                panel=null;
            }
        });

        System.exit(0);
    }
}
