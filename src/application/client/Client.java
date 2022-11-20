package application.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    private static String enemyMove = null;

    private final Socket socket;

    private Scanner in;
    private PrintWriter out;

    private boolean close = false;

    public Client (Socket socket) {
        this.socket = socket;
    }

    public void sendMove(int x, int y, int side) throws IOException {

        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Server down!");
        }
        String myMove = x + "," + y + "," + side;
        out.println(myMove);
        out.flush();
    }

    public String getEnemyMove() {
        return enemyMove;
    }

    public void doService() {
        while (true) {

            if (!in.hasNext()) return;
            enemyMove = in.nextLine();
        }
    }

//    public Boolean isServerClose(Socket socket){
//        try{
//            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
//            System.out.println("Urgent data sent!!!");
//            return false;
//        }catch(Exception se){
//            System.err.println("WTF?");
//            se.printStackTrace();
//            return true;
//        }
//    }

    @Override
    public void run() {

//        new Thread() {
//            @Override
//            public void run() {
//                Socket heartBeatSocket = null;
//                try {
//                    heartBeatSocket = new Socket("localhost", 1235);
//                } catch (IOException exception) {
//                    exception.printStackTrace();
//                }
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
//                while(true) {
//                    if (!in.hasNext()) {
//                        return;
//                    }
//                    String heartBeat = in.next();
//                    System.out.println("receive " + heartBeat);
//                    out.println(heartBeat);
//                    out.flush();
//                }
//            }
//        }.start();


        try {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream());
                doService();
            } finally {
                socket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
