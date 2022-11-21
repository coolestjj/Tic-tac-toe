package application.server;

public class ChessGame {

  private static final int[][] chessBoard = new int[3][3];

  public void move(String coordinates) {
    int x = Integer.parseInt(String.valueOf(coordinates.charAt(0)));
    int y = Integer.parseInt(String.valueOf(coordinates.charAt(2)));
    int side = Integer.parseInt(String.valueOf(coordinates.charAt(4)));
    chessBoard[x][y] = side;
    if (determineWinner() != 0) {
      if (determineWinner() == 1) {
        System.out.println("Player1 wins!");
      } else if (determineWinner() == 2) {
        System.out.println("Player2 wins!");
      } else if (determineWinner() == 3) {
        System.out.println("Game is a tie!");
      }
    }
  }

  private boolean allEqual(int x, int y, int z) {
    return x == z && y == z;
  }

  public int determineWinner() {

    for (int i = 0; i < 3; i++) {
      // Check straight lines
      if (allEqual(chessBoard[i][0], chessBoard[i][1], chessBoard[i][2])) {
        return chessBoard[i][0];
      }
      if (allEqual(chessBoard[0][i], chessBoard[1][i], chessBoard[2][i])) {
        return chessBoard[0][i];
      }

    }
    // Check diagnoses
    if (allEqual(chessBoard[0][0], chessBoard[1][1], chessBoard[2][2])
        || allEqual(chessBoard[2][0], chessBoard[1][1], chessBoard[0][2])) {
      return chessBoard[1][1];
    }

    boolean notFinished = false;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (chessBoard[i][j] == 0) {
          notFinished = true;
        }
      }
    }
    // 3 is tie
    if (!notFinished) {
      return 3;
    }

    return 0;
  }
}
