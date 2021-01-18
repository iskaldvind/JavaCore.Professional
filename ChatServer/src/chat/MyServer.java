package chat;

import chat.auth.AuthService;
import chat.auth.SQLiteAuthService;
import chat.handler.ClientHandler;
import chat.handler.DBHandler;
import clientserver.Command;
import clientserver.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final DBHandler dbHandler;
    private final List<ClientHandler> clients = new ArrayList<>();

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        dbHandler = DBHandler.getInstance();
        this.authService = new SQLiteAuthService(dbHandler);
        boolean started = this.authService.start();
        if (!started) {
            throw new IOException("AuthService is down!");
        }
    }


    public void start() throws IOException {
        System.out.println("Сервер запущен!");

        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            System.out.println("Ошибка создания нового подключения");
            e.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }

    public synchronized void updateUser(String login, String newName) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getLogin().equals(login)) {
                String oldName = client.getUsername();
                boolean result = dbHandler.changeUserName(login, newName);
                if (result) {
                    client.sendMessage(Command.updateOkCommand());
                    client.setUsername(newName);
                    List<String> usernames = getAllUsernames();
                    broadcastMessage(null, Command.updateUsersListCommand(usernames));
                    String updateMessage = String.format("%s теперь %s", oldName, newName);
                    Message message = new Message("Server", updateMessage);
                    broadcastMessage(null, Command.messageInfoCommand(message, null));
                } else {
                    client.sendMessage(Command.updateErrorCommand("Не удалось обновить"));
                }
                break;
            }
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Ожидание пользователя...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Клиент подключился!");
        processClientConnection(clientSocket);
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isUsernameBusy(String clientUsername) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(clientUsername)) {
               return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
    }

    private List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
    }

    public synchronized void broadcastMessage(ClientHandler sender, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(command);
            }
        }
    }

    public synchronized void sendPrivateMessage(String recipient, ClientHandler sender, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(command);
            }
            break;
        }
    }
}
