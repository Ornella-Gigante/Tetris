package es.nellagames.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {
    private final Paint paint = new Paint();
    private final int FALL_DELAY = 500; // ms
    private final int[][] board = new int[20][10];
    private Tetromino currentTetromino;
    private int score = 0;
    private boolean isPaused = false;
    private Bitmap[] tetrominoBitmaps;
    private Bitmap backgroundBitmap;

    private void loadBitmaps() {
        tetrominoBitmaps = new Bitmap[] {
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_i),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_j),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_l),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_o),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_s),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_t),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_z)
        };
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        spawnTetromino(); // Inicializa la primera pieza
    }

    public int getScore() {

        return score;
    }

    public void restartGame() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                board[r][c] = 0;
            }
        }
        score = 0;
        spawnTetromino();
        invalidate();
        resumeGame();
    }

    public interface GameOverListener {
        void onGameOver();
    }

    private GameOverListener gameOverListener;

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    private void checkGameOver() {
        if (isGameOver() && gameOverListener != null) {
            gameOverListener.onGameOver();
        }
    }

    private boolean isGameOver() {
        // Ejemplo simple: si la posición inicial está ocupada
        int[][] shape = currentTetromino.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0 && board[currentTetromino.y + r][currentTetromino.x + c] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void spawnTetromino() {
        int type = (int) (Math.random() * Tetromino.SHAPES.length);
        currentTetromino = new Tetromino(type);
        checkGameOver();
    }

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cellSize = getWidth() / 10;

        if (backgroundBitmap != null) {
            canvas.drawBitmap(
                    Bitmap.createScaledBitmap(backgroundBitmap, getWidth(), getHeight(), true),
                    0, 0, null
            );
        }

        // Dibuja la puntuación
        paint.setColor(Color.BLACK);
        paint.setTextSize(48);
        canvas.drawText("Score: " + score, 20, 60, paint);

        // Dibuja la cuadrícula
        paint.setColor(Color.LTGRAY);
        for (int i = 0; i <= 20; i++) {
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, paint);
        }
        for (int i = 0; i <= 10; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), paint);
        }

        // Dibuja el tetrominó actual
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

    private final Runnable gameLoop = new Runnable() {
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
        // Aquí deberías añadir la lógica de rotación del tetrominó
        // Por ejemplo: currentTetromino.rotate();
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
