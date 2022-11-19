package application;

import application.controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Waiting for players...");
        List<Socket> players = new ArrayList<>();

        while(players.size() < 2) {
            Socket socket = serverSocket.accept();
            players.add(socket);
        }
        ChessService chessService1 = new ChessService(players.get(0), players.get(1));
        ChessService chessService2 = new ChessService(players.get(1), players.get(0));
        Thread thread1 = new Thread(chessService1);
        Thread thread2 = new Thread(chessService2);
        thread1.start();
        thread2.start();
    }
}

