package com.example.project_oop;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/*
GamePanel class extends JPanel implements ActionListener.
This class creates the entire game: variable declarations, variable settings and game mechanics
 */

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'D';
    boolean running = false;
    boolean inGame = false;
    Timer timer;
    Random random;
    private final Clip[] music = new Clip[2];

    /*
     *construct a new GamePanel.
    */
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        setClip();
        startGame();
    }

    /*
    * method called to draw the Intro Screen
    */
    public void showIntroScreen(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Press SPACE to start", (SCREEN_WIDTH - metrics.stringWidth("Press SPACE to start")) / 2, (SCREEN_HEIGHT) / 2);
    }

    /*
     * method called to start the game
     */
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    /*
     * method that shows what to if inGame is true or false,
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!inGame) {
            showIntroScreen(g);
        } else {
            draw(g);
        }
        g.dispose();
    }

    /*
     * method that draws the game, if the game is running it draws all components
     * if not it calls gameover method
     */

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(0, 51, 10));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else if (i > 0 && i < 6) {
                    g.setColor(new Color(10, 79, 20));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else if (i >= 6 && i < 11) {
                    g.setColor(new Color(28, 114, 41));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else if (i >= 11 && i < 16) {
                    g.setColor(new Color(44, 140, 55));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(77, 201, 90));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    /*
     * method called to put the new apple in a randomic position
     */

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    /*
     * method that controls snake's movements
     */

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        //checks if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        //checks if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //checks if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        //check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }

    }

    /*
     * method that shows the gameover Screen
     */
    public void gameOver(Graphics g) {
        music[0].stop();
        //Score text
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (SCREEN_WIDTH - metrics2.stringWidth("GAME OVER")) / 2, SCREEN_HEIGHT / 2);
        music[1].start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    /*
     * method that associate the events with the key that I have to
     * press to make that particular movement
     */

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (inGame) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            } else {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    inGame = true;
                    startGame();
                    music[0].setMicrosecondPosition(0);
                    music[1].setMicrosecondPosition(0);
                    music[0].start();
                    music[0].loop(music[0].LOOP_CONTINUOUSLY);
                }
            }
        }
    }

    /*
     * method that sets the music clip in the game
     */
    private void setClip() {
        try{
            AudioInputStream ais1=
                    AudioSystem.getAudioInputStream(new File("C:\\Users\\Utente\\IdeaProjects\\project_oop\\src\\main\\resources\\snakeTheme.wav"));
            AudioInputStream ais2=
                    AudioSystem.getAudioInputStream(new File("C:\\Users\\Utente\\IdeaProjects\\project_oop\\src\\main\\resources\\gameoverTheme.wav"));
            for(int i=0;i<2;i++) {
                music[i]=AudioSystem.getClip();
            }
            music[0].open(ais1);
            music[1].open(ais2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
