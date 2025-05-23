package chess;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    public static final int PORT = 3030;
    public static final ArrayList<Game> games = new ArrayList<>();
    private static ClientConnection queuePlayer = null;

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            System.out.println("Server started at port " + PORT);

            while(true) {
                Socket socket = ss.accept();
                System.out.println("Accepted new connection: " + socket);

                if(queuePlayer == null) {
                    games.add(new Game());
                    queuePlayer = new ClientConnection(socket, games.getLast(), Color.WHITE);
                    games.getLast().setWhite(queuePlayer);
                    queuePlayer.start();
                } else {
                    ClientConnection otherPlayer = new ClientConnection(socket, games.get(games.size() - 1), Color.BLACK);
                    otherPlayer.start();
                    games.getLast().setBlack(otherPlayer);
                    games.getLast().start();


                    // Notifica entrambi i giocatori che il gioco è pronto
                    queuePlayer.sendReadyMessage();
                    otherPlayer.sendReadyMessage();


                    queuePlayer = null;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}