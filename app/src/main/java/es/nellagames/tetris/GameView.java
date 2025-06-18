package es.nellagames.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class GameView extends View {
    private final Paint paint = new Paint();
    private final int FALL_DELAY = 500; // ms
    private final int[][] board = new int[20][10];
    private Tetromino currentTetromino;
    private int score = 0;





    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        spawnTetromino(); // Llamar a spawnTetromino en el constructor una sola vez
    }

    private boolean isPaused = false;

    public void pauseGame() {
        isPaused = true;
        removeCallbacks(gameLoop);
    }

    public void resumeGame() {
        if (isPaused) {
            isPaused = false;
            postDelayed(gameLoop, FALL_DELAY);
        }
    }


    private void spawnTetromino() {
        int type = (int) (Math.random() * Tetromino.SHAPES.length);
        currentTetromino = new Tetromino(type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cellSize = getWidth() / 10;

        // Draw score in onDraw:
        paint.setColor(Color.BLACK);
        paint.setTextSize(48);
        canvas.drawText("Score: " + score, 20, 60, paint);

        // Draw grid
        paint.setColor(Color.LTGRAY);
        for (int i = 0; i <= 20; i++) {
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, paint);
        }
        for (int i = 0; i <= 10; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), paint);
        }

        // Draw current Tetromino
        if (currentTetromino != null) {
            int[][] shape = currentTetromino.getShape();
            paint.setColor(Color.BLUE);
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[r].length; c++) {
                    if (shape[r][c] != 0) {
                        int x = (currentTetromino.x + c) * cellSize;
                        int y = (currentTetromino.y + r) * cellSize;
                        canvas.drawRect(x, y, x + cellSize, y + cellSize, paint);
                    }
                }
            }
        }
    }

    private Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (!isPaused && currentTetromino != null) {
                currentTetromino.y++;
                invalidate();
                postDelayed(this, FALL_DELAY);
            }
        }
    };

    public void moveLeft() {
        currentTetromino.x--;
        invalidate();
    }

    public void moveRight() {
        currentTetromino.x++;
        invalidate();
    }

    public void rotate() {
        // Add simple rotation logic here
        invalidate();
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(gameLoop, FALL_DELAY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(gameLoop);
    }


    public boolean isPaused() {
        return isPaused;
    }

}