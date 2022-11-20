package application.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChessService implements Runnable{
    public ChessGame chessGame;
    private final Socket mySocket;
    private final Socket enemySocket;
    private Scanner in;
    private PrintWriter out;

    public ChessService (Socket mySocket, Socket enemySocket, ChessGame chessGame) {
        this.mySocket = mySocket;
        this.enemySocket = enemySocket;
        this.chessGame = chessGame;
    }

    public void doService() throws IOException {

        while (true) {
            if (!in.hasNext()) return;
            // receiveMsg = "1,0,2" ==> player2 moves at 1,0
            String receiveMsg = in.next();
            chessGame.move(receiveMsg);

            System.out.println("Player" + receiveMsg.charAt(4) +  " moves at " + receiveMsg.substring(0, 3));
            String sendMsg = receiveMsg;
            out.println(sendMsg);
            out.flush();

        }
    }


    @Override
    public void run() {


//        new Thread() {
//            @Override
//            public void run() {
//                ServerSocket serverSocket;
//                Socket heartBeatSocket = null;
//                try {
//                    serverSocket = new ServerSocket(1235);
//                    heartBeatSocket = serverSocket.accept();
//                    System.out.println("Heart beat socket!");
//                } catch (IOException exception) {
//                    exception.printStackTrace();
//                }
//
//                Scanner in = null;
//                PrintWriter out = null;
//                try {
//                    try {
//                        in = new Scanner(heartBeatSocket.getInputStream());
//                        out = new PrintWriter(heartBeatSocket.getOutputStream());
//                    } finally {
//
//                    }
//                } catch (IOException ioException) {
//                    ioException.printStackTrace();
//                }
//
//                out.println("heartBeat");
//                out.flush();
//                while(true) {
//                    if (!in.hasNext()) {
//                        return;
//                    }
//                    String heartBeat = in.next();
//                    System.out.println("receive " + heartBeat);
//                    out.println(heartBeat);
//                    out.flush();
//                }
//
//            }
//
//        }.start();


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
