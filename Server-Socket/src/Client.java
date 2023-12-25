import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Введите ваше имя:");
            String userName = userInput.readLine();
            output.println(userName);

            Thread readThread = new Thread(() -> {
                String message;
                try {
                    while ((message = serverInput.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка при чтении сообщения: " + e.getMessage());
                }
            });
            readThread.start();

            String userMessage;
            while (true) {
                userMessage = userInput.readLine();
                output.println(userMessage);
            }
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}