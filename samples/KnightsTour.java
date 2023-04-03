import java.util.*;

public class KnightsTour {

  static final int N = 8;

  static final int[] rowMoves = new int[]{-2, -2, -1, 1, 2, 2, 1, -1};
  static final int[] colMoves = new int[]{-1, 1, 2, 2, 1, -1, -2, -2};

  int[][] board = new int[8][8];
  int visited;

  KnightsTour() {
    visited = 1;
  }

  boolean isValid(int row, int col) {
    return row >= 0 && row < N && col >= 0 && col < N && board[row][col] < 1;
  }

  int accesibility(int row, int col) {
    int ret = 0;
    for (int i = 0; i < rowMoves.length; i++) {
      int nextRow = row + rowMoves[i];
      int nextCol = col + colMoves[i];
      if (isValid(nextRow, nextCol)) ret++;
    }
    return ret;
  }

  static class Move {
    int index;
    int accesibility;

    Move(int index, int accesibility) {
      this.index = index;
      this.accesibility = accesibility;
    }

    public String toString() {
      return "index: " + index + " acc: " + accesibility;
    }
  }

  PriorityQueue<Move> nextMoves(int row, int col) {
    PriorityQueue<Move> moves = new PriorityQueue<>(Comparator.comparingInt(a -> a.accesibility));
    for (int i = 0; i < rowMoves.length; i++) {
      int nextRow = row + rowMoves[i];
      int nextCol = col + colMoves[i];
      if (!isValid(nextRow, nextCol)) continue;
      moves.add(new Move(i, accesibility(nextRow, nextCol)));
    }
    System.out.println(moves);
    return moves;
  }

  boolean solveUtil(int row, int col) {
    Queue<Move> moves = nextMoves(row, col);
    if (moves.isEmpty()) return false;
    for (Move move : moves) {
      int nextRow = row + rowMoves[move.index];
      int nextCol = col + colMoves[move.index];
      visited++;
      board[nextRow][nextCol] = visited;

      if (visited == N*N) return true;

      if (!solveUtil(nextRow, nextCol)) {
        visited--;
        board[nextRow][nextCol] = 0;
      } else {
        return true;
      }
    }
    return false;
  }

  void solve(int row, int col) {
    board[row][col] = visited;
    solveUtil(row, col);
    for (int[] ints : board) {
      System.out.println(Arrays.toString(ints));
    }
  }

  public static void main(String[] args) {
//    new KnightsTour().nextMoves(0, 0);
    new KnightsTour().solve(5, 5);
  }
}
