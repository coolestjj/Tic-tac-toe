package application;

import application.controller.Controller;
import javafx.application.Platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);

        while (true) {
            List<Socket> players = new ArrayList<>();
            while (players.size() < 2) {
                Socket socket = serverSocket.accept();
                players.add(socket);
                if (players.size() < 2) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println("Only 1 player now, waiting...");
                    out.flush();
                }
                else if (players.size() == 2) {
                    PrintWriter out1 = new PrintWriter(players.get(0).getOutputStream());
                    PrintWriter out2 = new PrintWriter(players.get(1).getOutputStream());
                    out1.println("1, 2 players now");
                    out2.println("2, 2 players now");
                    out1.flush();
                    out2.flush();
                }
                System.out.println("player" + players.size() + " connected");
            }
            ChessService chessService1 = new ChessService(players.get(0), players.get(1));
            ChessService chessService2 = new ChessService(players.get(1), players.get(0));
            Thread thread1 = new Thread(chessService1);
            Thread thread2 = new Thread(chessService2);
            thread1.start();
            thread2.start();
        }
    }
}

