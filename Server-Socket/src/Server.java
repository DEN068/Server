import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 9090;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, PrintWriter> clientMap = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter writer;

        private Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(writer);

                String userName = reader.readLine();
                clientMap.put(userName, writer);

                for (PrintWriter writer : clientWriters) {
                    writer.println(userName + " присоединился к чату");
                }

                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.startsWith("@")) {
                        String[] split = message.split(" ", 2);
                        if (split.length > 1) {
                            String recipient = split[0].substring(1);
                            PrintWriter privateWriter = clientMap.get(recipient);
                            if (privateWriter != null) {
                                privateWriter.println(userName + " шепчет вам: " + split[1]);
                            }
                        }
                    } else {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(userName + ": " + message);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } finally {
                if (writer != null) {
                    clientWriters.remove(writer);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Ошибка закрытия сокета: " + e.getMessage());
                }
            }
        }
    }
}