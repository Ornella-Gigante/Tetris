package es.nellagames.tetris;

public class Tetromino {
    // Matriz 3D que contiene todas las rotaciones de cada tetrominó
    public static final int[][][][] SHAPES_ROTATIONS = {
            // I-piece (4 rotaciones)
            {
                    {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}},
                    {{0,0,1,0}, {0,0,1,0}, {0,0,1,0}, {0,0,1,0}},
                    {{0,0,0,0}, {0,0,0,0}, {1,1,1,1}, {0,0,0,0}},
                    {{0,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,1,0,0}}
            },
            // J-piece (4 rotaciones)
            {
                    {{1,0,0}, {1,1,1}, {0,0,0}},
                    {{0,1,1}, {0,1,0}, {0,1,0}},
                    {{0,0,0}, {1,1,1}, {0,0,1}},
                    {{0,1,0}, {0,1,0}, {1,1,0}}
            },
            // L-piece (4 rotaciones)
            {
                    {{0,0,1}, {1,1,1}, {0,0,0}},
                    {{0,1,0}, {0,1,0}, {0,1,1}},
                    {{0,0,0}, {1,1,1}, {1,0,0}},
                    {{1,1,0}, {0,1,0}, {0,1,0}}
            },
            // O-piece (no necesita rotación)
            {
                    {{1,1}, {1,1}},
                    {{1,1}, {1,1}},
                    {{1,1}, {1,1}},
                    {{1,1}, {1,1}}
            },
            // S-piece (2 rotaciones)
            {
                    {{0,1,1}, {1,1,0}, {0,0,0}},
                    {{0,1,0}, {0,1,1}, {0,0,1}},
                    {{0,0,0}, {0,1,1}, {1,1,0}},
                    {{1,0,0}, {1,1,0}, {0,1,0}}
            },
            // T-piece (4 rotaciones)
            {
                    {{0,1,0}, {1,1,1}, {0,0,0}},
                    {{0,1,0}, {0,1,1}, {0,1,0}},
                    {{0,0,0}, {1,1,1}, {0,1,0}},
                    {{0,1,0}, {1,1,0}, {0,1,0}}
            },
            // Z-piece (2 rotaciones)
            {
                    {{1,1,0}, {0,1,1}, {0,0,0}},
                    {{0,0,1}, {0,1,1}, {0,1,0}},
                    {{0,0,0}, {1,1,0}, {0,1,1}},
                    {{0,1,0}, {1,1,0}, {1,0,0}}
            }
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
        return SHAPES_ROTATIONS[type][rotation];
    }

    public void rotate() {
        rotation = (rotation + 1) % SHAPES_ROTATIONS[type].length;
    }

    public void rotateBack() {
        rotation = (rotation - 1 + SHAPES_ROTATIONS[type].length) % SHAPES_ROTATIONS[type].length;
    }
}
