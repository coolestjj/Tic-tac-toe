package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    private static String enemyMove = null;

    private static String myMove = null;

    private Socket socket;

    private Scanner in;
    private PrintWriter out;

    public Client (Socket socket) {
        this.socket = socket;
    }

    public void sendMove(int x, int y) throws IOException {
        out = new PrintWriter(socket.getOutputStream());
        myMove = x + "," + y;
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
