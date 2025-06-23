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
    private final int FALL_DELAY = 1000; // ms
    private final int[][] board = new int[20][10];
    private Tetromino currentTetromino;
    private int score = 0;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private int fallDelay = 1000; // ms, velocidad inicial
    private final int MIN_FALL_DELAY = 200; // ms, velocidad mínima
    private final int SPEED_INCREASE_INTERVAL = 30; // segundos o líneas limpiadas para aumentar velocidad
    private final int SPEED_INCREASE_AMOUNT = 50; // ms menos cada intervalo

    private int elapsedTimeSeconds = 0; // Tiempo transcurrido en segundos
    private int linesCleared = 0; // Líneas limpiadas


    // Efecto de fuego animado
    private boolean isFxClean09Active = false;
    private int fxClean09Line = -1;
    private int fxClean09FrameCount = 0;
    private final int FX_CLEAN09_DURATION = 30; // frames para la animación
    private int fxClean09Position = 0;
    private boolean fxClean09DirectionRight = true;

    private Bitmap[] tetrominoStyle1Bitmaps;
    private Bitmap[] tetrominoBlockBitmaps;
    private Bitmap backgroundBitmap;
    private Bitmap fxClean09Bitmap;

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

    private int updateFallDelay() {
        int intervals = (elapsedTimeSeconds / SPEED_INCREASE_INTERVAL) + (linesCleared / 10);
        int newDelay = fallDelay - (intervals * SPEED_INCREASE_AMOUNT);
        if (newDelay < MIN_FALL_DELAY) {
            newDelay = MIN_FALL_DELAY;
        }
        return newDelay;
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

        // Solo cargamos fx_clean09 para la animación de fuego
        fxClean09Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fx_clean09);
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

        // Dibuja el efecto fx_clean09 animado
        if (isFxClean09Active && fxClean09Line >= 0) {
            int x = gameAreaMargin + (fxClean09Position * cellSize);
            int y = gameAreaTop + (fxClean09Line * cellSize);
            canvas.drawBitmap(Bitmap.createScaledBitmap(fxClean09Bitmap, cellSize, cellSize, true), x, y, null);

            // Actualizar posición para el siguiente frame
            if (fxClean09DirectionRight) {
                fxClean09Position++;
                if (fxClean09Position >= 9) fxClean09DirectionRight = false;
            } else {
                fxClean09Position--;
                if (fxClean09Position <= 0) fxClean09DirectionRight = true;
            }

            fxClean09FrameCount++;
            if (fxClean09FrameCount > FX_CLEAN09_DURATION) {
                isFxClean09Active = false;
                fxClean09Line = -1;
            } else {
                postInvalidateOnAnimation();
            }
        }
    }

    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (!isPaused && !isGameOver && currentTetromino != null) {
                currentTetromino.y++;

                if (checkCollision()) {
                    currentTetromino.y--;
                    fixTetromino();
                    clearLines();
                    postDelayed(() -> {
                        spawnTetromino();
                        invalidate();
                        if (!isGameOver && !isPaused) {
                            postDelayed(this, FALL_DELAY);
                        }
                    }, 300);
                } else {
                    invalidate();
                    postDelayed(this, FALL_DELAY);
                }
            }
        }
    };

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
        int linesClearedThisTime = 0;
        for (int row = board.length - 1; row >= 0; row--) {
            boolean fullLine = true;
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == -1) {
                    fullLine = false;
                    break;
                }
            }

            if (fullLine) {
                // Activar efecto de fuego animado
                isFxClean09Active = true;
                fxClean09Line = row;
                fxClean09FrameCount = 0;
                fxClean09Position = 0;
                fxClean09DirectionRight = true;

                // Mover líneas hacia abajo
                for (int moveRow = row; moveRow > 0; moveRow--) {
                    for (int col = 0; col < board[moveRow].length; col++) {
                        board[moveRow][col] = board[moveRow - 1][col];
                    }
                }
                // Limpiar la línea superior
                for (int col = 0; col < board[0].length; col++) {
                    board[0][col] = -1;
                }
                linesClearedThisTime++;
                row++; // Revisar la misma fila otra vez
            }
        }

        if (linesClearedThisTime > 0) {
            linesCleared += linesClearedThisTime;
            score += linesClearedThisTime * 100;
        }
    }

    public void moveLeft() {
        if (currentTetromino != null && !isGameOver && !isPaused) {
            currentTetromino.x--;
            if (checkCollision()) {
                currentTetromino.x++;
            }
            invalidate();
        }
    }

    public void moveRight() {
        if (currentTetromino != null && !isGameOver && !isPaused) {
            currentTetromino.x++;
            if (checkCollision()) {
                currentTetromino.x--;
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

                    if (boardX < 0 || boardX >= board[0].length || boardY >= board.length) {
                        return true;
                    }

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
