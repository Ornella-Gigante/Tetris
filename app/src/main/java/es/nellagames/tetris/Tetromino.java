package es.nellagames.tetris;

public class Tetromino {
    public static final int[][][] SHAPES = {
            // I
            {{1, 1, 1, 1}},
            // J
            {{1, 0, 0}, {1, 1, 1}},
            // L
            {{0, 0, 1}, {1, 1, 1}},
            // O
            {{1, 1}, {1, 1}},
            // S
            {{0, 1, 1}, {1, 1, 0}},
            // T
            {{0, 1, 0}, {1, 1, 1}},
            // Z
            {{1, 1, 0}, {0, 1, 1}}
    };

    public int type;
    public int rotation;
    public int x, y;

    public Tetromino(int type) {
        this.type = type;
        this.rotation = 0;
        this.x = 3;
        this.y = 0;
    }

    public int[][] getShape() {
        return SHAPES[type];
    }

    public void rotate() {
    }

    public void rotateBack() {
    }
}
