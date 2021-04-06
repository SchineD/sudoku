import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class SudokuSolverParallel {

    public final static int BOARD_SIZE = 9;

    private final static BigInteger CUTOFF_SEARCHSPACE = new BigDecimal("1E25").toBigInteger();

    public static final ForkJoinPool pool = new ForkJoinPool();

    private int fixed;

    private int[][] board;

    public SudokuSolverParallel(int[][] board) {

        this.board = board;
        computeFixed();
    }

    /** Creates a deep copy of another {@link SudokuSolverParallel} */
    public SudokuSolverParallel(SudokuSolverParallel o) {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            board[i] = Arrays.copyOf(o.board[i], o.board[i].length);
        fixed = o.fixed;
    }

    private int solve(int row, int col) {

        if (col == BOARD_SIZE) {

            // Board.printBoard(board);
            return 1;
        }

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

    public int parallelSolve() {
        return pool.invoke(new SolverTask(this));
    }

    private boolean isValid(int row, int col, int val) {
        // check for equals numbers on the column and the row
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (getCell(row, i) == val || getCell(i, col) == val)
                return false;
        }


        int rowSec = (row / 3) * 3;
        int colSec = (col / 3) * 3;


        int row1 = (row + 2) % 3;
        int row2 = (row + 4) % 3;
        int col1 = (col + 2) % 3;
        int col2 = (col + 4) % 3;

        if (getCell(row1 + rowSec, col1 + colSec) == val) return false;
        if (getCell(row2 + rowSec, col1 + colSec) == val) return false;
        if (getCell(row1 + rowSec, col2 + colSec) == val) return false;
        if (getCell(row2 + rowSec, col2 + colSec) == val) return false;

        return true;
    }
    
    public int getCell(int row, int col) {
        return board[row][col];
    }
    
    public void setCell(int row, int col, int val) {
        if (!isEmpty(row, col) && val == 0)
            fixed--;
        else if (isEmpty(row, col) && val != 0)
            fixed++;
        board[row][col] = val;
    }
    
    public boolean isEmpty(int row, int col) {
        return getCell(row, col) == 0;
    }
    
    private void computeFixed() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (getCell(row, col) != 0)
                    fixed++;
            }
        }
    }
    
    public BigInteger getSearchSpace() {
        BigInteger solSpace = BigInteger.valueOf(1);
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (getCell(row, col) == 0) {
                    int candidates = 0;
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(row, col, num))
                            candidates++;
                    }
                    solSpace = solSpace.multiply(BigInteger.valueOf(candidates));
                }
            }
        }
        return solSpace;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++)
                sb.append(getCell(x, y) + " ");
            sb.append('\n');
        }
        return sb.toString();
    }


    private class SolverTask extends RecursiveTask<Integer> {
        private static final long serialVersionUID = 7541995084434430895L;
        private SudokuSolverParallel board;
        private int row, col;

        public SolverTask(int row, int col, SudokuSolverParallel board) {
            this.board = board;
            this.col = col;
            this.row = row;
        }

        public SolverTask(SudokuSolverParallel board) {
            this(0, 0, board);
        }

        @Override
        protected Integer compute() {
            // if the search space is smaller than the cutoff, solve sequentially
            if (board.getSearchSpace().compareTo(CUTOFF_SEARCHSPACE) < 0)
                return board.solve(row, col);
            else
                return parallelSolve();
        }
        
        private int parallelSolve() {

            // done!
            if (col == BOARD_SIZE)
                return 1;

            if (!board.isEmpty(row, col)) {
                SolverTask task = row < BOARD_SIZE - 1 ? new SolverTask(row + 1, col, new SudokuSolverParallel(board.board)) : new SolverTask(0, col + 1, new SudokuSolverParallel(board.board));

                return task.compute();
            }

            // try all possible numbers
            List<SolverTask> tasks = new ArrayList<>();
            for (int num = 1; num <= BOARD_SIZE; num++) {
                if (board.isValid(row, col, num)) {
                    board.setCell(row, col, num);
                    tasks.add(row < BOARD_SIZE - 1 ? new SolverTask(row + 1, col, new SudokuSolverParallel(board)) : new SolverTask(0, col + 1, new SudokuSolverParallel(board)));
                    board.setCell(row, col, 0);
                }
            }
            board = null;

            // the result of this thread
            int res = 0;
            // execute in parallel all the actions in the queue. The last action in the queue does not fork but get executed in this thread.
            SolverTask continuation = tasks.get(tasks.size() - 1);
            tasks.remove(tasks.size() - 1);

            for (SolverTask task : tasks)
                task.fork();

            res += continuation.compute();

            // wait for the forked tasks to finish
            for (SolverTask task : tasks)
                res += task.join();

            return res;
        }
    }

}