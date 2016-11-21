package tech.jasontubman.breakneck;

import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;

import tech.jasontubman.breakneck.Triangles.Diamond;
import tech.jasontubman.breakneck.Triangles.LeftTriangle;
import tech.jasontubman.breakneck.Triangles.RightTriangle;

/**
 * Created by Jason on 21/11/2016.
 */

public class ObstacleManager {
    private ArrayList<Obstacle> obstacles;
    private int playerGap;
    private int obstacleGap;
    private int obstacleHeight;
    private int color;
    private float speed;
    private long startTime;
    private long initTime;


    public ObstacleManager(int playerGap, int obstacleGap, int obstacleHeight, int color) {
        this.playerGap = playerGap;
        this.obstacleGap = obstacleGap;
        this.obstacleHeight = obstacleHeight;
        this.color = color;


        startTime = initTime = System.currentTimeMillis();

        obstacles = new ArrayList<>();

        createObstacles();

    }

    public boolean playerCollided(Player player) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.playerCollided(player)){
                return true;
            }
        }
        return false;
    }

    private void createObstacles() {
        int obstacleY = -5*Constants.screenHeight/4;
        while (obstacleY < 0) {
            int random = (int) (1 + Math.random() * 2);
            double offset = (1 + Math.random() * 1);
            switch (random) {

                case 1 : {
                    obstacles.add(new LeftTriangle(Color.BLACK, 0, obstacleY, offset));
                    obstacleY += obstacleHeight + obstacleGap;
                    break;
                }
                case 2 : {
                    obstacles.add(new RightTriangle(Color.BLACK, Constants.screenWidth/2, obstacleY, offset));
                    obstacleY += obstacleHeight + obstacleGap;
                    break;
                }
                case 3 : {
                    obstacles.add(new Diamond(Color.BLACK, 0, obstacleY, offset));
                    obstacleY += obstacleHeight + obstacleGap;
                    break;
                }
            }
        }
    }
    public void update(){
        int elapsedTime = (int)(System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        this.speed = (float)(Math.sqrt(1 + (startTime - initTime)/2000)) * Constants.screenHeight/10000.0f;
        for(Obstacle obstacle : obstacles) {
            obstacle.moveObstacle(speed*elapsedTime);
        }
        if (obstacles.get(obstacles.size() -1).getTop() >= Constants.screenHeight) {
            int obstacleY = obstacles.get(0).getTop() - obstacleHeight - obstacleGap;
            int random = (int) (1 + Math.random() * 2);
            int offset = (int) (1 + Math.random() * 1);
            switch (random) {

                case 1 : {
                    obstacles.add(new LeftTriangle(Color.BLACK, 0, obstacleY, offset));
                    break;
                }
                case 2 : {
                    obstacles.add(new RightTriangle(Color.BLACK, 0, obstacleY, offset));
                    break;
                }
                case 3 : {
                    obstacles.add(new Diamond(Color.BLACK, 0, obstacleY, offset));
                    break;
                }
            }
            obstacles.remove(obstacles.size() - 1);

        }

    }

    public void draw(Canvas canvas) {
        for (Obstacle obstacle: obstacles){
            obstacle.draw(canvas);
        }

    }

}
