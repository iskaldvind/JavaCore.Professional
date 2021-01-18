package chat.handler;

import chat.MyServer;
import chat.auth.AuthService;
import clientserver.Command;
import clientserver.CommandType;
import clientserver.Message;
import clientserver.commands.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;

public class ClientHandler {

    private final MyServer myServer;
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private String login;

    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (EOFException e) {
                try {
                    unsubscribe();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void authentication() throws IOException {

        while (true) {

            Command command = readCommand();
            if (command == null) {
                continue;
            }
            if (command.getType() == CommandType.AUTH) {

                boolean isSuccessAuth = processAuthCommand(command);
                if (isSuccessAuth) {
                    break;
                }

            } else {
                sendMessage(Command.authErrorCommand("Ошибка авторизации"));

            }
        }

    }

    private boolean processAuthCommand(Command command) throws IOException {
        AuthCommandData cmdData = (AuthCommandData) command.getData();
        String login = cmdData.getLogin();
        String password = cmdData.getPassword();

        AuthService authService = myServer.getAuthService();
        this.username = authService.getUsernameByLoginAndPassword(login, password);
        if (username != null) {
            if (myServer.isUsernameBusy(username)) {
                sendMessage(Command.authErrorCommand("Логин уже используется"));
                return false;
            }
            this.login = login;
            sendMessage(Command.authOkCommand(username));
            myServer.subscribe(this);
            String text = String.format("%s присоединился к чату", username);
            Message message = new Message("Server", text);
            myServer.broadcastMessage(null, Command.messageInfoCommand(message, null));
            return true;
        } else {
            sendMessage(Command.authErrorCommand("Логин или пароль не соответствуют действительности"));
            return false;
        }
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Получен неизвестный объект";
            System.err.println(errorMessage);
            e.printStackTrace();
            return null;
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }

            switch (command.getType()) {
                case END: {
                    unsubscribe();
                    return;
                }
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    Message message = data.getMessage();
                    String sender = data.getSender();
                    myServer.broadcastMessage(this, Command.messageInfoCommand(message, sender));
                    break;
                }
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    Message message = data.getMessage();
                    myServer.sendPrivateMessage(recipient, this, Command.messageInfoCommand(message, username));
                    break;
                }
                case UPDATE_USER: {
                    UpdateUserCommandData data = (UpdateUserCommandData) command.getData();
                    String login = data.getLogin();
                    String newName = data.getNewName();
                    myServer.updateUser(login, newName);
                    break;
                }
                default: {
                    String errorMessage = "Неизвестный тип команды" + command.getType();
                    System.err.println(errorMessage);
                    sendMessage(Command.errorCommand(errorMessage));
                }
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogin() {
        return login;
    }

    public void sendMessage(Command command) throws IOException {
        out.writeObject(command);
    }

    private void unsubscribe() throws IOException {
        String text = String.format("%s покинул чат", username);
        Message message = new Message("Server", text);
        myServer.broadcastMessage(null, Command.messageInfoCommand(message, null));
        myServer.unSubscribe(this);
    }
}
