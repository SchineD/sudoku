public class Board {

    private static final int BOARD_SIZE = 9;
    private static final int BOARD_START_INDEX = 0;

    // difficult board
    private final int[][] board = {
            {0, 2, 0, 0, 0, 0, 0, 4, 1},
            {0, 0, 7, 0, 9, 0, 0, 0, 0},
            {5, 8, 0, 0, 0, 3, 0, 0, 0},
            {0, 0, 0, 5, 0, 0, 2, 9, 0},
            {3, 6, 0, 0, 0, 8, 1, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 6, 0, 0, 0, 8},
            {0, 0, 1, 0, 0, 0, 9, 0, 5},
            {8, 0, 0, 0, 5, 0, 0, 7, 0}
    };

    // very hard board
    private final int[][] board2 = {
            {0, 0, 0, 9, 0, 0, 0, 0, 0},
            {7, 3, 0, 0, 0, 0, 0, 6, 0},
            {0, 0, 2, 0, 0, 4, 0, 0, 0},
            {5, 6, 0, 1, 8, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 9, 7, 0},
            {8, 4, 0, 0, 7, 0, 0, 0, 0},
            {0, 9, 6, 8, 0, 0, 0, 0, 0},
            {0, 8, 0, 0, 0, 0, 2, 0, 0},
            {0, 0, 0, 0, 0, 0, 3, 0, 5}
    };

    //easy board
    private final int[][] board3 = {
            {0, 2, 0, 5, 0, 3, 0, 0, 1},
            {0, 5, 0, 0, 0, 0, 3, 4, 9},
            {0, 7, 0, 1, 9, 0, 0, 5, 6},
            {0, 0, 0, 0, 0, 0, 7, 0, 0},
            {7, 3, 0, 2, 0, 0, 5, 0, 8},
            {0, 0, 5, 0, 7, 4, 1, 3, 0},
            {0, 0, 8, 3, 0, 9, 0, 0, 0},
            {3, 0, 2, 0, 0, 6, 9, 0, 0},
            {1, 9, 7, 0, 5, 2, 6, 0, 3}
    };
/*
    private final int[][] board4 = {
            {0, 2, 0, 5, 0, 3, 0, 0, 1},
            {0, 5, 0, 0, 0, 0, 3, 4, 9},
            {0, 7, 0, 1, 9, 0, 0, 5, 6},
            {0, 0, 0, 0, 0, 0, 7, 0, 0},
            {7, 3, 0, 2, 0, 0, 5, 0, 8},
            {0, 0, 5, 0, 7, 4, 1, 3, 0},
            {0, 0, 8, 3, 0, 9, 0, 0, 0},
            {3, 0, 2, 0, 0, 6, 9, 0, 0},
            {1, 9, 7, 0, 5, 2, 6, 0, 3}
    };
*/
    public int[][] getBoard() {
        return board;
    }

    public int[][] getBoard2() {
        return board2;
    }

    public void printBoard(int[][] board) {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            for (int column = BOARD_START_INDEX; column < BOARD_SIZE; column++) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println();
        }
    }
}
