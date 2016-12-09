package tech.jasontubman.breakneck;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Jason on 9/12/2016.
 */

public class Speed implements Obstacle {

    private Rect shieldRect = new Rect();
    private int x;
    private int y;

    public Speed(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public void moveObstacle(float y) {
        this.y += y;
    }

    @Override
    public boolean playerCollided(Player player) {
        if (shieldRect.intersect(player.getRectangle())) {
            return true;
        } else {
            return  false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawBitmap(Assets.resizedspeedBuff, x, y, paint);

    }

    @Override
    public void update() {
        shieldRect.set(x, y, x + Constants.screenWidth/21, y + Constants.screenWidth/21);
    }

    @Override
    public int getTop() {
        return this.y;
    }
}
