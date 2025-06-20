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

    // Arrays para diferentes estilos de tetrominós
    private Bitmap[] tetrominoStyle1Bitmaps;  // Estilo 1
    private Bitmap[] tetrominoStyle2Bitmaps;  // Estilo 2
    private Bitmap[] tetrominoBlockBitmaps;   // Bloques individuales

    // Efectos y fondos
    private Bitmap backgroundBitmap;
    private Bitmap borderBitmap;
    private Bitmap[] effectsBitmaps;      // Efectos fx_clean
    private Bitmap[] patternBitmaps;      // Patrones

    private int currentStyle = 1; // 1 o 2 para alternar estilos

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAllBitmaps();
        spawnTetromino();
    }

    private void loadAllBitmaps() {
        // ===== TETROMINÓS ESTILO 1 =====
        tetrominoStyle1Bitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_i1_1),       // I-piece estilo 1
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_j1_1),     // J-piece estilo 1
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_j1_1),     // L-piece (usar J como base)
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_o1_1),     // O-piece estilo 1
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_s1_1),     // S-piece estilo 1
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_t1_1),     // T-piece estilo 1
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_z1_1)      // Z-piece estilo 1
        };

        // ===== TETROMINÓS ESTILO 2 =====
        tetrominoStyle2Bitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_i2_2),       // I-piece estilo 2
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_j2_1),     // J-piece estilo 2
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_j2_1),     // L-piece (usar J como base)
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_o1_2),     // O-piece estilo 2
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_s1_2),     // S-piece estilo 2
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_t2_1),     // T-piece estilo 2
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_z2_1)      // Z-piece estilo 2
        };

        // ===== BLOQUES INDIVIDUALES =====
        tetrominoBlockBitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_1), // Bloque tipo 1
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_2), // Bloque tipo 2
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_3), // Bloque tipo 3
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_4), // Bloque tipo 4
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_5), // Bloque tipo 5
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_6), // Bloque tipo 6
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_7)  // Bloque tipo 7
        };

        // ===== EFECTOS VISUALES =====
        effectsBitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean01),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean02),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean03),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean04),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean05),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean06),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean07),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean08),
                BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean09)
        };

        // ===== PATRONES =====
        patternBitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.pattern01),
                BitmapFactory.decodeResource(getResources(), R.drawable.pattern02),
                BitmapFactory.decodeResource(getResources(), R.drawable.pattern03),
                BitmapFactory.decodeResource(getResources(), R.drawable.pattern04)
        };

        // ===== FONDO Y BORDE =====
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        borderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.border);
    }

    // Método para obtener el bitmap actual del tetrominó según el estilo
    private Bitmap getCurrentTetrominoBitmap(int type) {
        if (currentStyle == 1) {
            return tetrominoStyle1Bitmaps[type % tetrominoStyle1Bitmaps.length];
        } else {
            return tetrominoStyle2Bitmaps[type % tetrominoStyle2Bitmaps.length];
        }
    }

    // Método para cambiar estilo de tetrominós
    public void switchTetrominoStyle() {
        currentStyle = (currentStyle == 1) ? 2 : 1;
        invalidate();
    }

    public int getScore() {
        return score;
    }

    public void restartGame() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                board[r][c] = -1; // -1 significa vacío
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
        if (currentTetromino == null) return false;
        int[][] shape = currentTetromino.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    int boardY = currentTetromino.y + r;
                    int boardX = currentTetromino.x + c;
                    if (boardY >= 0 && boardY < board.length &&
                            boardX >= 0 && boardX < board[0].length &&
                            board[boardY][boardX] != -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void spawnTetromino() {
        int type = (int) (Math.random() * 7); // Solo 7 tipos básicos
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

        // Dibuja el fondo
        if (backgroundBitmap != null) {
            canvas.drawBitmap(
                    Bitmap.createScaledBitmap(backgroundBitmap, getWidth(), getHeight(), true),
                    0, 0, null
            );
        }

        // Dibuja el borde (opcional)
        if (borderBitmap != null) {
            canvas.drawBitmap(
                    Bitmap.createScaledBitmap(borderBitmap, getWidth(), getHeight(), true),
                    0, 0, null
            );
        }

        // Dibuja la puntuación
        paint.setColor(Color.WHITE);
        paint.setTextSize(48);
        paint.setShadowLayer(4, 2, 2, Color.BLACK);
        canvas.drawText("Score: " + score, 20, 60, paint);
        canvas.drawText("Style: " + currentStyle, 20, 120, paint);

        // Dibuja la cuadrícula (opcional)
        paint.setColor(Color.LTGRAY);
        paint.setAlpha(30);
        for (int i = 0; i <= 20; i++) {
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, paint);
        }
        for (int i = 0; i <= 10; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), paint);
        }
        paint.setAlpha(255); // Restaurar opacidad

        // Dibuja los bloques ya colocados en el tablero usando bloques individuales
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                int type = board[row][col];
                if (type >= 0 && type < tetrominoBlockBitmaps.length) {
                    Bitmap blockBitmap = tetrominoBlockBitmaps[type];
                    int x = col * cellSize;
                    int y = row * cellSize;
                    canvas.drawBitmap(
                            Bitmap.createScaledBitmap(blockBitmap, cellSize, cellSize, true),
                            x, y, null
                    );
                }
            }
        }

        // Dibuja el tetrominó actual usando el estilo seleccionado
        if (currentTetromino != null) {
            int[][] shape = currentTetromino.getShape();
            Bitmap tetroBitmap = getCurrentTetrominoBitmap(currentTetromino.type);
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[r].length; c++) {
                    if (shape[r][c] != 0) {
                        int x = (currentTetromino.x + c) * cellSize;
                        int y = (currentTetromino.y + r) * cellSize;
                        canvas.drawBitmap(
                                Bitmap.createScaledBitmap(tetroBitmap, cellSize, cellSize, true),
                                x, y, null
                        );
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
        if (currentTetromino != null) {
            currentTetromino.x--;
            invalidate();
        }
    }

    public void moveRight() {
        if (currentTetromino != null) {
            currentTetromino.x++;
            invalidate();
        }
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
