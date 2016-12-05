package tech.jasontubman.breakneck;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import tech.jasontubman.game.R;

import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.view.MotionEvent;

import java.util.Timer;

/**
 * Created by Jason on 21/11/2016.
 */

public class GameplayScene implements Scene {
    private float eventX =0;
    private int numPoints = 0;
    private Player player;
    private Point playerPoint;
    private Player player2;
    private Point playerPoint2;
    private ObstacleManager obstacleManager;
    private int score;
    private Rect r = new Rect();

    private SceneManager sceneManager;

    private StarManager starManager;
    private boolean split;
    private boolean gameOver = false;
    private long timeEnded;

    private ShipSelector selector;

    private ParticleGenerator particleGenerator1;
    private ParticleGenerator particleGenerator2;

    public GameplayScene(SceneManager manager) {
        this.sceneManager = manager;
        selector = new ShipSelector(this.sceneManager.shipChosen + 1, 1);
        player = new Player(new Rect(100, 100, 235, 235), selector.getSprite(), selector.getSpeed());
        playerPoint = new Point(Constants.screenWidth/2, Constants.screenHeight-Constants.screenHeight/4);
        player.update(playerPoint);


        obstacleManager = new ObstacleManager(200, 650, 400, Color.LTGRAY, player);

        starManager = new StarManager(player.getSpeed(), false);

        particleGenerator1 = new ParticleGenerator();
        particleGenerator2 = new ParticleGenerator();

        player2 = new Player(new Rect(100, 100, 235, 235), selector.getSprite(), selector.getSpeed());
        playerPoint2 = new Point(Constants.screenWidth/2, Constants.screenHeight-Constants.screenHeight/4);
        player2.update(playerPoint2);
        player2.setVisible(false);
    }

    public void reset() {
        playerPoint = new Point(Constants.screenWidth/2, Constants.screenHeight-Constants.screenHeight/4);
        player.update(playerPoint);

        playerPoint2 = new Point(Constants.screenWidth/2, Constants.screenHeight-Constants.screenHeight/4);
        player2.update(playerPoint2);
        player2.setVisible(false);

        obstacleManager = new ObstacleManager(200, 650, 400, Color.LTGRAY, player);
        this.score = 0;
        numPoints  = 0;

    }
    @Override
    public void update() {
        if (!gameOver) {

            if (player2.isVisible()) {
                particleGenerator2.addParticle(player2.getX(), player2.getY() + player2.getHeight()-50, 0, true);
                particleGenerator2.update();
                particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, 0, true);
                particleGenerator1.update();
            } else {
                particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, 0, false);
                particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, 0, false);
                particleGenerator1.update();
            }

            player.update(playerPoint);
            player2.update(playerPoint2);

            obstacleManager.update();

            if (!obstacleManager.getcountDown()) {
                addToScore(100);
            }

            starManager.update();

            //RESETING PLAYERPOINT 1
            if (numPoints == 0 && playerPoint.x != Constants.screenWidth/2) {
                resetPoint();
            }

            //MOVING PLAYER 1
            if (numPoints > 0 && !gameOver) {
                if (eventX < Constants.screenWidth/2) {
                    if (!(playerPoint.x < player.getRectangle().width())) {
                        if (obstacleManager.getSpeed() > 0) {
                            playerPoint.set((int) (playerPoint.x - 70 * obstacleManager.getSpeed()), playerPoint.y);
                            particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, -5, false);
                            particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, -5, false);
                        } else {
                                playerPoint.set(playerPoint.x - 70, playerPoint.y);
                                particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, -5, false);
                                particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, -5, false);
                        }
                    } else {
                        if (!split) {
                            playerPoint.set(100, playerPoint.y);
                        }
                    }
                } else if (eventX > Constants.screenWidth/2) {
                    if (!(playerPoint.x > Constants.screenWidth - player.getRectangle().width())) {
                        if (obstacleManager.getSpeed() > 0) {
                            playerPoint.set((int) (playerPoint.x + 70 * obstacleManager.getSpeed()), playerPoint.y);
                            particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, 5, false);
                            particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, 5, false);
                        } else {
                            playerPoint.set(playerPoint.x + 70, playerPoint.y);
                            particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, 5, false);
                            particleGenerator1.addParticle(player.getX(), player.getY() + player.getHeight()-50, 5, false);
                        }
                    }
                    else {
                        playerPoint.set(Constants.screenWidth - 100, playerPoint.y);
                    }
                }
            }
            //IF MULTI TOUCH SPLIT / RESET AT END
            if(!gameOver && numPoints >1) {
                player2.setVisible(true);
                if (!split) {
                    split = true;
                    selector.selectSprite(this.sceneManager.shipChosen + 1, 0.5);
                    player.halfRect(player.getX() - player.getWidth()/2, player.getY());
                    player2.halfRect(player2.getX() - player2.getWidth()/2, player2.getY());
                    playerPoint.y += player.getHeight();
                    playerPoint2.y += player2.getHeight();
                    player.updateSprite(selector.getSprite());
                    player2.updateSprite(selector.getSprite());
                }
                split();

            } else if (!gameOver && numPoints <= 1){
                resetPointTwo();
                if (!split) {

                    player.resetRect(player.getX() - player.getWidth() / 2, player.getY());
                    player2.resetRect(player2.getX() - player2.getWidth() / 2, player2.getY());
                }
                if (playerPoint2.x == playerPoint.x) {
                    player2.setVisible(false);
                }
            }

        }
        if (obstacleManager.playerCollided(player) && !gameOver || obstacleManager.playerCollided(player2) && !gameOver) {
            gameOver = true;
            timeEnded = System.currentTimeMillis();
        }
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.drawRGB(44, 42, 49);

        Paint score = new Paint();
        score.setTextSize(50);
        score.setColor(Color.WHITE);
        drawScore(canvas, score, Integer.toString(this.score));





        starManager.draw(canvas);

        particleGenerator1.draw(canvas);

        if (split) {
            particleGenerator2.draw(canvas);
        }


        player.draw(canvas);
        player2.draw(canvas);

        obstacleManager.draw(canvas);

        //DRAW COG
        Paint paint2 = new Paint();
        BitmapFactory bf = new BitmapFactory();
        Bitmap cog = bf.decodeResource(Constants.currentContext.getResources(), R.drawable.gear);
        Bitmap resizedCog = (Bitmap.createScaledBitmap(cog, Constants.screenWidth/12, Constants.screenWidth/12, false));
        canvas.drawBitmap(resizedCog, (int) (Constants.screenWidth/40), Constants.screenHeight/40, paint2);

        //END OF COG


        if (gameOver) {
            RectF gameOverScreen = new RectF();
            gameOverScreen.set(Constants.screenWidth/12, Constants.screenHeight/4, Constants.screenWidth - Constants.screenWidth/12, Constants.screenHeight-Constants.screenHeight/4);
            Paint paint3 = new Paint();
            paint3.setColor(Color.WHITE);
            canvas.drawRoundRect(gameOverScreen, 10, 10, paint3);
            paint3.setTextSize(150);
            paint3.setColor(Color.DKGRAY);
            Typeface typeface = Typeface.createFromAsset(Constants.currentContext.getAssets(), "spaceage.ttf");
            paint3.setTypeface(typeface);
            centreText(canvas, paint3, "GAME OVER", Constants.screenHeight/2.7f);
            paint3.setTextSize(90);
            centreText(canvas, paint3, "Your Score was: ", Constants.screenHeight/2.3f);
            paint3.setTextSize(140);
            centreText(canvas, paint3, Integer.toString(this.score), Constants.screenHeight/1.9f);

            RectF black = new RectF();
            black.set((int) (Constants.screenWidth/11),  (int) (Constants.screenHeight/3.8),
                    (Constants.screenWidth/11) + (Constants.screenWidth / 12), (int) (Constants.screenHeight/3.8) + Constants.screenHeight / 20);
            canvas.drawRoundRect(black, 5, 5, paint3);

            BitmapFactory bf2 = new BitmapFactory();
            Bitmap menu = bf2.decodeResource(Constants.currentContext.getResources(), R.drawable.home);
            Bitmap resizedMenu = Bitmap.createScaledBitmap(menu, (int) (Constants.screenWidth / 12), Constants.screenHeight / 20, false);
            canvas.drawBitmap(resizedMenu, (int) (Constants.screenWidth/11), (int) (Constants.screenHeight/3.8), paint3);

            Bitmap playAgain = bf2.decodeResource(Constants.currentContext.getResources(), R.drawable.playbutton);
            Bitmap resizedplayAgain = Bitmap.createScaledBitmap(playAgain, (int) (Constants.screenWidth / 1.3), Constants.screenHeight / 10, false);
            canvas.drawBitmap(resizedplayAgain, (int) (Constants.screenWidth/9), (int) (Constants.screenHeight/1.9), paint3);
        }
    }

    @Override
    public void terminate() {
        SceneManager.activeScene = 0;
    }

    @Override
    public void recieveTouch(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!gameOver) {
                    numPoints = event.getPointerCount();
                    eventX = event.getX(0);
                    if (event.getX() > Constants.screenWidth/40 && event.getX() < Constants.screenWidth/40 + Constants.screenWidth/12 &&
                            event.getY() > Constants.screenHeight/40 && event.getY() < Constants.screenHeight/40 + Constants.screenWidth/12) {

                        if (this.sceneManager.getPaused()) {
                            this.sceneManager.setPaused(false);
                        } else {
                            this.sceneManager.setPaused(true);
                        }
                    }

                }
                if (gameOver && (System.currentTimeMillis() - timeEnded) >= 2000) {
                    reset();
                    gameOver = false;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!gameOver) {
                    numPoints = event.getPointerCount();
                    eventX = event.getX(0);
                }
                if (gameOver && (System.currentTimeMillis() - timeEnded) >= 2000) {
                    reset();
                    gameOver = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                numPoints = event.getPointerCount() - 1;
                break;
            }
        }

    }
    private void resetPoint() {
        if (playerPoint.x > (Constants.screenWidth / 2)) {
            if (obstacleManager.getSpeed() > 0) {
                playerPoint.set((int) (playerPoint.x - 70 * obstacleManager.getSpeed()), playerPoint.y);
            } else {
                playerPoint.set(playerPoint.x - 70, playerPoint.y);
            }
            if (playerPoint.x <= (Constants.screenWidth / 2)) {
                playerPoint.set(Constants.screenWidth/2, playerPoint.y);
                selector.selectSprite(this.sceneManager.shipChosen + 1, 1);
                player.updateSprite(selector.getSprite());
                player2.updateSprite(selector.getSprite());
            }
        } else if (playerPoint.x < (Constants.screenWidth / 2)) {
            if (obstacleManager.getSpeed() > 0) {
                playerPoint.set((int) (playerPoint.x + 70 * obstacleManager.getSpeed()), playerPoint.y);
            } else {
                playerPoint.set(playerPoint.x + 70, playerPoint.y);
            }
            if (playerPoint.x >= (Constants.screenWidth / 2)) {
                playerPoint.set(Constants.screenWidth/2, playerPoint.y);
                selector.selectSprite(this.sceneManager.shipChosen + 1, 1);
                player.updateSprite(selector.getSprite());
                player2.updateSprite(selector.getSprite());
            }
        }
        eventX = 0;
        numPoints = 0;
    }
    private void resetPointTwo() {
        if (playerPoint2.x > playerPoint.x) {
            if (obstacleManager.getSpeed() > 0) {
                playerPoint2.set((int) (playerPoint2.x - 70 * obstacleManager.getSpeed()), playerPoint2.y);
            } else {
                playerPoint2.set(playerPoint2.x - 70, playerPoint2.y);
            }
            if (playerPoint2.x <= playerPoint.x) {
                split = false;
                playerPoint.set(playerPoint.x, Constants.screenHeight - Constants.screenHeight/4);
                playerPoint2.set(playerPoint.x, Constants.screenHeight - Constants.screenHeight/4);
                selector.selectSprite(this.sceneManager.shipChosen + 1, 1);
                player.updateSprite(selector.getSprite());
                player2.updateSprite(selector.getSprite());
            }
        } else if (playerPoint2.x < playerPoint.x) {
            if (obstacleManager.getSpeed() > 0) {
                playerPoint2.set((int) (playerPoint2.x + 70 * obstacleManager.getSpeed()), playerPoint2.y);
            } else {
                playerPoint2.set(playerPoint2.x + 70, playerPoint2.y);
            }
            if (playerPoint2.x >= playerPoint.x) {
                split = false;
                playerPoint.set(playerPoint.x, Constants.screenHeight - Constants.screenHeight/4);
                playerPoint2.set(playerPoint.x, Constants.screenHeight - Constants.screenHeight/4);
                selector.selectSprite(this.sceneManager.shipChosen + 1, 1);
                player.updateSprite(selector.getSprite());
                player2.updateSprite(selector.getSprite());
            }
        }
    }

    private void drawScore(Canvas canvas, Paint paint, String text) {
        Typeface typeface = Typeface.createFromAsset(Constants.currentContext.getAssets(), "spaceage.ttf");
        paint.setTypeface(typeface);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 1.1f - r.width() / 2f - r.left;
        float y = 80;
        canvas.drawText(text, x, y, paint);
    }

    public void addToScore(int score){
        if (!gameOver) {
            this.score += score;
        }
    }

    private void split() {
        if (eventX < Constants.screenWidth/2) {
            if (!(playerPoint2.x > Constants.screenWidth - player.getRectangle().width())) {
                if (obstacleManager.getSpeed() > 0) {
                    playerPoint2.set(playerPoint2.x + (int) (70 * obstacleManager.getSpeed()), playerPoint2.y);
                    playerPoint.set(player.getRectangle().width() - 40, playerPoint.y);
                } else {
                    playerPoint2.set(playerPoint2.x +70, playerPoint2.y);
                    playerPoint.set(player.getRectangle().width() - 40, playerPoint.y);
                }
            } else {
                playerPoint2.set(Constants.screenWidth - 60, playerPoint2.y);
                playerPoint.set(player.getRectangle().width() - 40, playerPoint.y);
            }
        } else if (eventX > Constants.screenWidth/2) {
            if (!(playerPoint2.x < player.getRectangle().width())) {
                if (obstacleManager.getSpeed() > 0) {
                    playerPoint2.set(playerPoint2.x - (int) (70 * obstacleManager.getSpeed()), playerPoint2.y);
                    playerPoint.set(Constants.screenWidth - 60, playerPoint.y);
                } else {
                    playerPoint2.set(playerPoint2.x - (70), playerPoint2.y);
                    playerPoint.set(Constants.screenWidth - 60, playerPoint.y);
                }
            } else {
                playerPoint2.set(player.getRectangle().width() - 40, playerPoint2.y);
                playerPoint.set(Constants.screenWidth - 60, playerPoint.y);
            }
        }
    }

    private void centreText(Canvas canvas, Paint paint, String text, float y) {
        Typeface typeface = Typeface.createFromAsset(Constants.currentContext.getAssets(), "spaceage.ttf");
        paint.setTypeface(typeface);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        canvas.drawText(text, x, y, paint);
    }


}
