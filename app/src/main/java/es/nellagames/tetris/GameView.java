package es.nellagames.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {
    private final Paint paint = new Paint();

    private final int[][] board = new int[20][10];
    private Tetromino currentTetromino;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        spawnTetromino(); // Llamar a spawnTetromino en el constructor una sola vez
    }

    private void spawnTetromino() {
        int type = (int) (Math.random() * Tetromino.SHAPES.length);
        currentTetromino = new Tetromino(type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cellSize = getWidth() / 10;

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

}
