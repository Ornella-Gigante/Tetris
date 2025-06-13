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

        paint.setColor(Color.LTGRAY);
        int cellSize = getWidth() / 10;

        // Dibujar líneas horizontales
        for (int i = 0; i <= 20; i++) {
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, paint);
        }

        // Dibujar líneas verticales
        for (int i = 0; i <= 10; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), paint);
        }
    }
}
