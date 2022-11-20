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

    public Client (Socket socket) {
        this.socket = socket;
    }

    public void sendMove(int x, int y, int side) throws IOException {

        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            // toDo: 打开四个窗口，关闭前两个后，这边下棋就会到这里出错
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
            if(enemyMove.equals("Another player has left")) {
                System.out.println(enemyMove);
                break;
            }
        }
    }

    @Override
    public void run() {

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
