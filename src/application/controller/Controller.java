package application.controller;

import application.client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;

    private static int MYSIDE;

    @FXML
    private Pane base_square;

    @FXML
    private Rectangle game_panel;

    private static boolean TURN = false;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client client = null;
        try {
            Socket socket = new Socket("localhost", 1234);
            client = new Client(socket);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Client finalClient = client;
        Thread thread = new Thread(finalClient);
        thread.start();

        //监听线程,接收对方的步子
        new Thread() {
            @Override
            public void run() {

                boolean flag1 = true;
                boolean flag2 = true;

                while (true) {
                    if (determineWinner() != 0) {

                        if (determineWinner() == MYSIDE) {
                            System.out.println("You win!");
                        }
                        else if (determineWinner() == 3 - MYSIDE) {
                            System.out.println("You lose!");
                        }
                        else if (determineWinner() == 3) {
                            System.out.println("Tie!");
                        }
                        break;
                    }

                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String enemyMove = finalClient.getEnemyMove();

                    if (enemyMove != null) {

                        if (enemyMove.equals("Only 1 player now, waiting...") && flag1) {
                            System.out.println(enemyMove);
                            flag1 = false;
                        }
                        else if (enemyMove.contains("2 players now") && flag2) {
                            MYSIDE = Integer.parseInt(String.valueOf(enemyMove.charAt(0)));
                            System.out.println("2 players now, you are player" + MYSIDE);

                            if (MYSIDE == 1) {
                                TURN = true;
                            }
                            else if (MYSIDE == 2) {
                                TURN = false;
                            }

                            flag2 = false;
                        }
                        else if (enemyMove.length() == 5){
                            int enemyX = Integer.parseInt(String.valueOf(enemyMove.charAt(0)));
                            int enemyY = Integer.parseInt(String.valueOf(enemyMove.charAt(2)));
                            Platform.runLater(() -> {
                                  if (enemyRefreshBoard(enemyX, enemyY)) {
                                      TURN = !TURN;
                                  }
                            });
                        }
                    }
                }
            }
        }.start();

        game_panel.setOnMouseClicked(event -> {

            int x = (int) (event.getX() / BOUND);
            int y = (int) (event.getY() / BOUND);

            if (meRefreshBoard(x, y)) {
                TURN = !TURN;
                try {
                    finalClient.sendMove(x, y, MYSIDE);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
        });
    }

    private boolean allEqual(int x, int y, int z) {
        return x == z && y == z;
    }

    private int determineWinner() {

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
        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
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

    private boolean enemyRefreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            if (!TURN) {
                chessBoard[x][y] = 3 - MYSIDE;
                drawChess();
                System.out.println("Opponent moves at " + x + "," + y);
                return true;
            }
        }
        return false;
    }

    private boolean meRefreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            if (TURN) {
                chessBoard[x][y] = MYSIDE;
                drawChess();
                System.out.println("You move at " + x + "," + y);
                return true;
            }
//            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;

        }
        return false;
    }

    private void drawChess() {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle(int i, int j) {
        Circle circle = new Circle();
        base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine(int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        base_square.getChildren().add(line_a);
        base_square.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
        flag[i][j] = true;
    }
}
