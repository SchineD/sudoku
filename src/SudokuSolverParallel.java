import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class SudokuSolverParallel {

    public static final int BOARD_SIZE = 9;
    private static final int START_INDEX = 0;
    private static final BigInteger TRESHOLD = new BigDecimal("1E40").toBigInteger();

    public static final ForkJoinPool pool = new ForkJoinPool();

    private int[][] board;

    public SudokuSolverParallel(int[][] board) {

        this.board = board;
    }

    public SudokuSolverParallel(SudokuSolverParallel solver) {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            board[i] = Arrays.copyOf(solver.board[i], solver.board[i].length);
    }

    private int solve(int row, int col) {
        // done!
        if (col == BOARD_SIZE) {
            printBoard(board);
            return 1;
        }
        // fixed cell, move on!
        if (!isEmpty(row, col))
            return row < BOARD_SIZE - 1 ? solve(row + 1, col) : solve(0, col + 1);

        int res = 0;
        for (int num = 1; num <= BOARD_SIZE; num++) {
            if (isValid(row, col, num)) {
                setCell(row, col, num);
                res += row < BOARD_SIZE - 1 ? solve(row + 1, col) : solve(0, col + 1);
                setCell(row, col, 0);
            }
        }
        return res;
    }

    private class ParallelTask extends RecursiveTask<Integer> {
        private SudokuSolverParallel board;
        private int row, col;

        public ParallelTask(int row, int col, SudokuSolverParallel board) {
            this.board = board;
            this.col = col;
            this.row = row;
        }

        public ParallelTask(SudokuSolverParallel board) {
            this(0, 0, board);
        }

        @Override
        protected Integer compute() {

            if (board.getSearchSpace().compareTo(TRESHOLD) < 0)
                return board.solve(row, col);
            else
                return parallelSolve();
        }

        private int parallelSolve() {

            // done!
            if (col == BOARD_SIZE)
                return 1;

            if (!board.isEmpty(row, col)) {
                ParallelTask task = row < BOARD_SIZE - 1 ? new ParallelTask(row + 1, col, new SudokuSolverParallel(board.board)) : new ParallelTask(0, col + 1, new SudokuSolverParallel(board.board));

                return task.compute();
            }

            // try all possible numbers
            List<ParallelTask> tasks = new ArrayList<>();
            for (int value = 1; value <= BOARD_SIZE; value++) {
                if (board.isValid(row, col, value)) {
                    board.setCell(row, col, value);
                    tasks.add(row < BOARD_SIZE - 1 ? new ParallelTask(row + 1, col, new SudokuSolverParallel(board)) : new ParallelTask(0, col + 1, new SudokuSolverParallel(board)));
                    board.setCell(row, col, 0);
                }
            }
            board = null;

            int result = 0;

            ParallelTask endresultTask = tasks.get(tasks.size() - 1);
            tasks.remove(tasks.size() - 1);

            for (ParallelTask task : tasks)
                task.fork();

            result += endresultTask.compute();

            for (ParallelTask task : tasks)
                result += task.join();

            return result;
        }
    }

    public int parallelSolve() {
        return pool.invoke(new ParallelTask(this));
    }

    private boolean isValid(int row, int col, int value) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (getCell(row, i) == value || getCell(i, col) == value)
                return false;
        }

        int row_subSection = (row / 3) * 3;
        int col_subSection = (col / 3) * 3;

        int row1 = (row + 2) % 3;
        int row2 = (row + 4) % 3;
        int col1 = (col + 2) % 3;
        int col2 = (col + 4) % 3;

        if (getCell(row1 + row_subSection, col1 + col_subSection) == value) return false;
        if (getCell(row2 + row_subSection, col1 + col_subSection) == value) return false;
        if (getCell(row1 + row_subSection, col2 + col_subSection) == value) return false;
        if (getCell(row2 + row_subSection, col2 + col_subSection) == value) return false;

        return true;
    }
    
    public int getCell(int row, int col) {
        return board[row][col];
    }
    
    public void setCell(int row, int col, int value) {
        board[row][col] = value;
    }
    
    public boolean isEmpty(int row, int col) {
        return getCell(row, col) == 0;
    }

    public BigInteger getSearchSpace() {
        BigInteger possibleValuesLeftToCompute = BigInteger.valueOf(1);
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (getCell(row, col) == 0) {
                    int candidates = 0;
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(row, col, num))
                            candidates++;
                    }
                    possibleValuesLeftToCompute = possibleValuesLeftToCompute.multiply(BigInteger.valueOf(candidates));
                }
            }
        }
        return possibleValuesLeftToCompute;
    }

    public void printBoard(int[][] board) {
        for (int row = START_INDEX; row < BOARD_SIZE; row++) {
            for (int column = START_INDEX; column < BOARD_SIZE; column++) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println();
        }
    }

}