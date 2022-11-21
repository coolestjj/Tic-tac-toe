package application.controller;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
      Pane root = fxmlLoader.load();
      primaryStage.setTitle("Tic Tac Toe");
      primaryStage.setScene(new Scene(root));
      primaryStage.setResizable(false);
      primaryStage.show();

      new Thread() {
        @Override
        public void run() {
          try {
            Socket testSocket = new Socket("localhost", 1236);
            Scanner in = new Scanner(testSocket.getInputStream());
            PrintWriter out = new PrintWriter(testSocket.getOutputStream());
            while (true) {
              if (!in.hasNext()) {
                sleep(300);
                if (!in.hasNext()) {
                  System.err.println("Server down!");
                  break;
                }
              }
              String msg = in.next();
//                            System.out.println("receive " + msg);
              out.println(msg);
              out.flush();
//                            System.out.println("send " + msg);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }.start();

      primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
          try {
            Socket closeSocket = new Socket("localhost", 1235);
            PrintWriter out = new PrintWriter(closeSocket.getOutputStream());
            out.println("Close");
            out.flush();
            System.out.println("Exit");
            System.exit(0);
          } catch (Exception ignored) {
          }
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
