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
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

    private final MyServer myServer;
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private String login;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }

    public void handle(ExecutorService executorService) throws IOException {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

        executorService.submit(() -> {
            try {
                authentication();
                readMessage();
            } catch (EOFException e) {
                try {
                    unsubscribe();
                } catch (IOException ee) {
                    logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            }
        });
    }

    private void authentication() throws IOException {

        while (true) {

            Command command = readCommand();
            if (command == null) {
                continue;
            }
            if (command.getType() == CommandType.AUTH) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Клиент авторизуется: ");
                stringBuilder.append(((AuthCommandData) command.getData()).getLogin());
                stringBuilder.append(" ");
                stringBuilder.append(((AuthCommandData) command.getData()).getPassword());
                String logMessage = stringBuilder.toString();
                logger.log(Level.INFO, logMessage);
                boolean isSuccessAuth = processAuthCommand(command);
                if (isSuccessAuth) {
                    break;
                }

            } else {
                sendMessage(Command.authErrorCommand("Ошибка авторизации"));
                logger.log(Level.INFO, "Ошибка авторизации клиента");
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
            logger.log(Level.INFO, text);
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
            logger.log(Level.SEVERE, "Получен неизвестный объект\n" + Arrays.toString(e.getStackTrace()));
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
                    logger.log(Level.INFO, "Клиент " + this.getUsername() + " покинул чат");
                    unsubscribe();
                    return;
                }
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    Message message = data.getMessage();
                    String sender = data.getSender();
                    String logMessage = "Клиент " +
                            this.getUsername() +
                            " (всем) " +
                            message.getDate() +
                            ": " +
                            message.getText();
                    logger.log(Level.INFO, logMessage );
                    myServer.broadcastMessage(this, Command.messageInfoCommand(message, sender));
                    break;
                }
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    Message message = data.getMessage();
                    String logMessage = "Клиент " +
                            this.getUsername() +
                            " (" +
                            recipient +
                            ") " +
                            message.getDate() +
                            ": " +
                            message.getText();
                    logger.log(Level.INFO, logMessage );
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
                    logger.log(Level.SEVERE, "Неизвестный тип команды" + command.getType());
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
        myServer.unSubscribe(this);
        myServer.broadcastMessage(null, Command.messageInfoCommand(message, null));
    }
}
