package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChessService implements Runnable{
    private int[][] chessBoard = new int[3][3];
    private Socket mySocket;
    private Socket enemySocket;
    private Scanner in;
    private PrintWriter out;

    public ChessService (Socket mySocket, Socket enemySocket) {
        this.mySocket = mySocket;
        this.enemySocket = enemySocket;
    }

    public void refreshBoard(String coordinates) {
        int x = Integer.parseInt(String.valueOf(coordinates.charAt(0)));
        int y = Integer.parseInt(String.valueOf(coordinates.charAt(2)));
        int identiey = Integer.parseInt(String.valueOf(coordinates.charAt(4)));
        if (chessBoard[x][y] == 0) {
            chessBoard[x][y] = identiey;
        }
    }

    public void doService() throws IOException {
        while (true) {
            if (!in.hasNext()) return;
            String receiveMsg = in.next();
            refreshBoard(receiveMsg);

            String sendMsg = receiveMsg;
//            if (receiveMsg.charAt(4) == '1') {
//                sendMsg = sendMsg.replace(sendMsg.charAt(4)+"","2");
//                System.out.println(sendMsg);
//            }
//            else if (receiveMsg.charAt(4) == '2') {
//                sendMsg = sendMsg.replace(sendMsg.charAt(4)+"","1");
//                System.out.println(sendMsg);
//            }
            out.println(sendMsg);
            out.flush();

        }
    }

    @Override
    public void run() {
        try {
            try {
                in = new Scanner(mySocket.getInputStream());
                out = new PrintWriter(enemySocket.getOutputStream());
                doService();
            } finally {
                mySocket.close();
                enemySocket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

//    public boolean determineWinner(int[][] chessBoard) {
//
//    }
}
