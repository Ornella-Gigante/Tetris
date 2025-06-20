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
    private final int FALL_DELAY = 1000; // ms - Velocidad más lenta
    private final int[][] board = new int[20][10];
    private Tetromino currentTetromino;
    private int score = 0;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    private Bitmap[] tetrominoStyle1Bitmaps;
    private Bitmap[] tetrominoBlockBitmaps;
    private Bitmap backgroundBitmap;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeBoard();
        loadAllBitmaps();
        spawnTetromino();
        startGameLoop();
    }

    private void initializeBoard() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                board[r][c] = -1; // -1 significa vacío
            }
        }
    }

    private void loadAllBitmaps() {
        tetrominoStyle1Bitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_i1_1),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_j1_1),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_j1_1), // L-piece
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_o1_1),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_s1_1),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_t1_1),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_z1_1)
        };

        tetrominoBlockBitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_1),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_2),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_3),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_4),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_5),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_6),
                BitmapFactory.decodeResource(getResources(), R.drawable.tetromino_block1_7)
        };

        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    }

    public int getScore() {
        return score;
    }

    public void restartGame() {
        initializeBoard();
        score = 0;
        isGameOver = false;
        removeCallbacks(gameLoop);
        spawnTetromino();
        startGameLoop();
        invalidate();
    }

    public interface GameOverListener {
        void onGameOver();
    }

    private GameOverListener gameOverListener;

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    private void spawnTetromino() {
        int type = (int) (Math.random() * 7);
        currentTetromino = new Tetromino(type);

        // Verificar game over inmediatamente
        if (checkCollision()) {
            isGameOver = true;
            pauseGame();
            if (gameOverListener != null) {
                gameOverListener.onGameOver();
            }
        }
    }

    private void startGameLoop() {
        if (!isPaused && !isGameOver) {
            postDelayed(gameLoop, FALL_DELAY);
        }
    }

    public void pauseGame() {
        isPaused = true;
        removeCallbacks(gameLoop);
    }

    public void resumeGame() {
        if (isPaused && !isGameOver) {
            isPaused = false;
            startGameLoop();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calcular área de juego centrada
        int gameAreaMargin = getWidth() / 8;
        int gameAreaWidth = getWidth() - (gameAreaMargin * 2);
        int cellSize = gameAreaWidth / 10;
        int gameAreaHeight = cellSize * 20;
        int gameAreaTop = (getHeight() - gameAreaHeight) / 2;

        // Dibuja el fondo
        if (backgroundBitmap != null) {
            canvas.drawBitmap(
                    Bitmap.createScaledBitmap(backgroundBitmap, getWidth(), getHeight(), true),
                    0, 0, null
            );
        }

        // Dibuja la puntuación
        paint.setColor(Color.WHITE);
        paint.setTextSize(48);
        paint.setShadowLayer(4, 2, 2, Color.BLACK);
        canvas.drawText("Score: " + score, 20, 60, paint);

        // Dibuja los bloques fijos en el tablero
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                int type = board[row][col];
                if (type >= 0 && type < tetrominoBlockBitmaps.length) {
                    Bitmap blockBitmap = tetrominoBlockBitmaps[type];
                    int x = gameAreaMargin + (col * cellSize);
                    int y = gameAreaTop + (row * cellSize);
                    canvas.drawBitmap(
                            Bitmap.createScaledBitmap(blockBitmap, cellSize, cellSize, true),
                            x, y, null
                    );
                }
            }
        }

        // Dibuja SOLO el tetrominó actual (una sola pieza)
        if (currentTetromino != null && !isGameOver) {
            int[][] shape = currentTetromino.getShape();
            if (currentTetromino.type < tetrominoStyle1Bitmaps.length) {
                Bitmap tetroBitmap = tetrominoStyle1Bitmaps[currentTetromino.type];
                for (int r = 0; r < shape.length; r++) {
                    for (int c = 0; c < shape[r].length; c++) {
                        if (shape[r][c] != 0) {
                            int x = gameAreaMargin + ((currentTetromino.x + c) * cellSize);
                            int y = gameAreaTop + ((currentTetromino.y + r) * cellSize);
                            // Solo dibujar si está dentro del área visible
                            if (currentTetromino.y + r >= 0) {
                                canvas.drawBitmap(
                                        Bitmap.createScaledBitmap(tetroBitmap, cellSize, cellSize, true),
                                        x, y, null
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    // ... (código anterior sin cambios)

    // Game loop corregido - UNA PIEZA A LA VEZ
    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (!isPaused && !isGameOver && currentTetromino != null) {
                // Mover la pieza hacia abajo
                currentTetromino.y++;

                // Verificar si hay colisión
                if (checkCollision()) {
                    // Retroceder la pieza
                    currentTetromino.y--;

                    // Fijar la pieza al tablero
                    fixTetromino();
                    clearLines();

                    // ESPERAR antes de generar nueva pieza
                    postDelayed(() -> {
                        spawnTetromino();
                        invalidate();

                        // Continuar el game loop solo si no es game over
                        if (!isGameOver && !isPaused) {
                            postDelayed(this, FALL_DELAY);
                        }
                    }, 300); // Espera 300ms antes de nueva pieza
                } else {
                    // Continuar cayendo
                    invalidate();
                    postDelayed(this, FALL_DELAY);
                }
            }
        }
    };

// ... (resto del código sin cambios)

    private void fixTetromino() {
        int[][] shape = currentTetromino.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    int boardY = currentTetromino.y + r;
                    int boardX = currentTetromino.x + c;
                    if (boardY >= 0 && boardY < board.length &&
                            boardX >= 0 && boardX < board[0].length) {
                        board[boardY][boardX] = currentTetromino.type;
                    }
                }
            }
        }
    }

    private void clearLines() {
        int linesCleared = 0;
        for (int row = board.length - 1; row >= 0; row--) {
            boolean fullLine = true;
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == -1) {
                    fullLine = false;
                    break;
                }
            }

            if (fullLine) {
                // Mover todas las líneas hacia abajo
                for (int moveRow = row; moveRow > 0; moveRow--) {
                    for (int col = 0; col < board[moveRow].length; col++) {
                        board[moveRow][col] = board[moveRow - 1][col];
                    }
                }
                // Limpiar la línea superior
                for (int col = 0; col < board[0].length; col++) {
                    board[0][col] = -1;
                }
                linesCleared++;
                row++; // Verificar la misma fila nuevamente
            }
        }

        if (linesCleared > 0) {
            score += linesCleared * 100;
        }
    }

    public void moveLeft() {
        if (currentTetromino != null && !isGameOver && !isPaused) {
            currentTetromino.x--;
            if (checkCollision()) {
                currentTetromino.x++; // Revertir
            }
            invalidate();
        }
    }

    public void moveRight() {
        if (currentTetromino != null && !isGameOver && !isPaused) {
            currentTetromino.x++;
            if (checkCollision()) {
                currentTetromino.x--; // Revertir
            }
            invalidate();
        }
    }

    public void rotate() {
        if (currentTetromino != null && !isGameOver && !isPaused) {
            currentTetromino.rotate();
            if (checkCollision()) {
                currentTetromino.rotateBack();
            }
            invalidate();
        }
    }

    private boolean checkCollision() {
        if (currentTetromino == null) return false;

        int[][] shape = currentTetromino.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    int boardX = currentTetromino.x + c;
                    int boardY = currentTetromino.y + r;

                    // Verificar límites
                    if (boardX < 0 || boardX >= board[0].length || boardY >= board.length) {
                        return true;
                    }

                    // Verificar colisión con bloques existentes
                    if (boardY >= 0 && board[boardY][boardX] != -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startGameLoop();
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
