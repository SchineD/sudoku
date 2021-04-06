import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

public class SudokuSolver {

    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int START_INDEX = 0;

    private static final int NO_VALUE = 0;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;

    private static final int ROUNDS = 100;


    public static void main(String[] args) {

    runParallelSolver();

    runSeqSolver();
    }

    private static void runParallelSolver() {

        long startTime;
        long computationTime = 0;

        for(int i = 0; i < ROUNDS; i++) {

            Board board = new Board();
            int[][] board_helper = board.getBoard();
            SudokuSolverParallel solver = new SudokuSolverParallel(board_helper);

            startTime = System.currentTimeMillis();
            solver.parallelSolve();
            computationTime += System.currentTimeMillis() - startTime;
            System.out.println(computationTime);
        }

        System.out.println("-------------PARALLEL-------------");
        System.out.println("Sudoku solved in ~" + (float)computationTime/ROUNDS + "ms.");
    }

    private static void runSeqSolver() {
        long startTime;
        long computationTime = 0;

        for(int i = 0; i < ROUNDS; i++) {
            SudokuSolver solver = new SudokuSolver();
            Board board = new Board();
            int[][] board_helper = board.getBoard();

            startTime = System.currentTimeMillis();
            solver.solve(board_helper);
            computationTime += System.currentTimeMillis() - startTime;
            System.out.println(computationTime);
        }

        //solver.printBoard();

        System.out.println("------------SEQUENTIAL--------------");
        System.out.println("Sudoku solved in ~" + (float)computationTime/ROUNDS + "ms.");
    }

    private void printBoard(int[][] board) {
        for (int row = START_INDEX; row < BOARD_SIZE; row++) {
            for (int column = START_INDEX; column < BOARD_SIZE; column++) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println();
        }
    }

    private boolean solve(int[][] board) {
        for (int row = START_INDEX; row < BOARD_SIZE; row++) {
            for (int column = START_INDEX; column < BOARD_SIZE; column++) {
                if (board[row][column] == NO_VALUE) {
                    for (int k = MIN_VALUE; k <= MAX_VALUE; k++) {
                        board[row][column] = k;
                        if (isValid(board, row, column) && solve(board)) {
                            return true;
                        }
                        board[row][column] = NO_VALUE;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int column) {
        return rowConstraint(board, row) &&
                columnConstraint(board, column) &&
                subsectionConstraint(board, row, column);
    }

    private boolean subsectionConstraint(int[][] board, int row, int column) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        int subsectionRowStart = (row / SUBSECTION_SIZE) * SUBSECTION_SIZE;
        int subsectionRowEnd = subsectionRowStart + SUBSECTION_SIZE;

        int subsectionColumnStart = (column / SUBSECTION_SIZE) * SUBSECTION_SIZE;
        int subsectionColumnEnd = subsectionColumnStart + SUBSECTION_SIZE;

        for (int r = subsectionRowStart; r < subsectionRowEnd; r++) {
            for (int c = subsectionColumnStart; c < subsectionColumnEnd; c++) {
                if (!checkConstraint(board, r, constraint, c)) return false;
            }
        }
        return true;
    }

    private boolean columnConstraint(int[][] board, int column) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        return IntStream.range(START_INDEX, BOARD_SIZE)
                .allMatch(row -> checkConstraint(board, row, constraint, column));
    }

    private boolean rowConstraint(int[][] board, int row) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        return IntStream.range(START_INDEX, BOARD_SIZE)
                .allMatch(column -> checkConstraint(board, row, constraint, column));
    }

    private boolean checkConstraint(int[][] board, int row, boolean[] constraint, int column) {
        if (board[row][column] != NO_VALUE) {
            if (!constraint[board[row][column] - 1]) {
                constraint[board[row][column] - 1] = true;
            } else {
                return false;
            }
        }
        return true;
    }

}