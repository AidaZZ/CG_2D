import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class CountDown {
    int secondsLeft;
    long roundStartTime;
    final int COUNTDOWN_DURATION;

    CountDown(int minutes) {
        this.secondsLeft = minutes * 60;
        this.COUNTDOWN_DURATION = minutes * 60;
        this.roundStartTime = System.currentTimeMillis();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - roundStartTime;
        secondsLeft = (int) Math.max(0, (COUNTDOWN_DURATION * 1000 - elapsedTime) / 1000);

        if (secondsLeft <= 0) {
            secondsLeft = 0;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Consolas", Font.PLAIN, 30));

        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;

        String timeString = String.format("Time: %02d:%02d", minutes, seconds);
        g.drawString(timeString, 20, 30);
    }
}
