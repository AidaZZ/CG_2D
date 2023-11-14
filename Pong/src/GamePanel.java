import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{
    private AudioClip collisionSound;
    static final int ONE_BILLION=1000000000;
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Board board1;
    Board board2;
    Ball ball;
    Score score;
    CountDown countDown;
    boolean isGameRunning;
    String player1Name;
    String player2Name;
    private Image backgroundImage;

    GamePanel(String player1Name,String player2Name) {
        countDown = new CountDown(1);
        isGameRunning = true;

        newPaddles();
        newBall();

        score = new Score(GAME_WIDTH, GAME_HEIGHT);

        this.setFocusable(true);
        this.addKeyListener(new KeyEventListener());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();

        this.player1Name=player1Name;
        this.player2Name=player2Name;

        String imagePath = "C:\\Users\\User\\Desktop\\Staging_Environment\\1.jpg";
        backgroundImage = new ImageIcon(imagePath).getImage();

        try {
            File soundFile = new File("C:\\Users\\User\\Desktop\\Staging_Environment\\1.wav");
            collisionSound = Applet.newAudioClip(soundFile.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void newBall(){
        random = new Random();
        ball = new Ball((GAME_WIDTH/2) - (BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles () {
        board1 = new Board(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT,1);
        board2 = new Board(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT,2);
    }

    public void paint(Graphics g) {
        graphics = g;
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        draw(g);
    }

    public void draw(Graphics g) {
        board1.draw(g);
        board2.draw(g);
        ball.draw(g);
        score.draw(g);
        countDown.draw(g);

        g.setColor(Color.white);
        g.setFont(new Font("Consolas", Font.BOLD, 30));
        int playerNameY = GAME_HEIGHT - 10;
        g.drawString(player1Name, 50, playerNameY);
        g.drawString(player2Name, GAME_WIDTH - 100, playerNameY);
    }

    public void move () {
        board1.move();
        board2.move();
        ball.move();
    }

    public void checkCollision () {
        if (ball.y <= 0) {
            ball.setYDirection(- ball.ySpeed);
        }
        if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(- ball.ySpeed);
        }

        if (ball.intersects(board1)) {
            ball.xSpeed = Math.abs(ball.xSpeed);
            ball.xSpeed++;
            if (ball.ySpeed > 0)
                ball.ySpeed++;
            else
                ball.ySpeed--;
            ball.setXDirection(ball.xSpeed);
            ball.setYDirection(ball.ySpeed);
        }

        if (ball.intersects(board2)) {
            ball.xSpeed = Math.abs(ball.xSpeed);
            ball.xSpeed++;
            if (ball.ySpeed > 0)
                ball.ySpeed++;
            else
                ball.ySpeed--;
            ball.setXDirection(-ball.xSpeed);
            ball.setYDirection(ball.ySpeed);
        }

        if (board1.y<=0)
            board1.y=0;
        if (board1.y >= (GAME_HEIGHT - PADDLE_HEIGHT))
            board1.y = GAME_HEIGHT - PADDLE_HEIGHT;
        if (board2.y<=0)
            board2.y=0;
        if (board2.y >= (GAME_HEIGHT - PADDLE_HEIGHT))
            board2.y = GAME_HEIGHT - PADDLE_HEIGHT;

        if (ball.x <= 0) {
            score.player2 ++;
            newPaddles();
            newBall();
            System.out.println("Player 2: " + score.player2);
        }
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1 ++;
            newPaddles();
            newBall();
            System.out.println("Player 1: " + score.player1);
        }

        if (ball.intersects(board1) || ball.intersects(board2)) {
            collisionSound.play();
        }
    }

    private void showGameOverDialog(){
        String winner = (score.player1 > score.player2) ? player1Name : player2Name;
        JOptionPane.showMessageDialog(this, "Game Over! Winner: " + winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    public void run (){
        long lastTime = System.nanoTime();
        double framesPerSec = 60.0;
        double ns = ONE_BILLION/framesPerSec ;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;

            if (delta>=1 && isGameRunning) {
                move();
                checkCollision();
                countDown.update();
                repaint();
                delta--;

                if(countDown.secondsLeft <= 0){
                    isGameRunning = false;
                    showGameOverDialog();
                }
            }
        }
    }

    public class KeyEventListener extends KeyAdapter {
        public void keyPressed (KeyEvent e){
            board1.keyPressed(e);
            board2.keyPressed(e);
        }
        public void keyReleased (KeyEvent e){
            board1.keyReleased(e);
            board2.keyReleased(e);
        }
    }
}
