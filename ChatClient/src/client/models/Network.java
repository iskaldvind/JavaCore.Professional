package client.models;

import client.NetworkClient;
import client.controllers.ChatController;
import clientserver.Command;
import clientserver.Message;
import clientserver.commands.*;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.Date;

public class Network {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;

    private final String host;
    private final int port;

    private ObjectOutputStream dataOutputStream;
    private ObjectInputStream dataInputStream;

    private Socket socket;

    private String username;
    private String newName;
    private String login;
    private ChatController chatController;

    public ObjectOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public ObjectInputStream getDataInputStream() {
        return dataInputStream;
    }

    public Network() {
        this(SERVER_ADDRESS, SERVER_PORT);
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new ObjectInputStream(socket.getInputStream());
            return true;

        } catch (IOException e) {
            System.out.println("Соединение не было установлено!");
            e.printStackTrace();
            return false;
        }

    }

    public void close() {
        try {
            Command endCommand = Command.endCommand();
            dataOutputStream.writeObject(endCommand);
            socket.close();
        } catch (SocketException e) {
            // Ignore
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitMessage(ChatController chatController) {

       Thread thread = new Thread( () -> {
           try {

               chatController.connectHistory();
               chatController.getHistory();

               while (true) {

                   Command command = readCommand();
                   if (command == null) {
                       NetworkClient.showErrorMessage("Error", "Ошибка серверва", "Получена неверная команда");
                       continue;
                   }

                   switch (command.getType()) {
                       case INFO_MESSAGE: {
                           MessageInfoCommandData data = (MessageInfoCommandData) command.getData();
                           Message message = data.getMessage();
                           String sender = data.getSender();
                           Platform.runLater(() -> {
                               chatController.appendMessage(message);
                               chatController.writeHistory(message);
                           });
                           break;
                       }
                       case UPD_ERROR:
                       case ERROR: {
                           ErrorCommandData data = (ErrorCommandData) command.getData();
                           String errorMessage = data.getErrorMessage();
                           Platform.runLater(() -> {
                               NetworkClient.showErrorMessage("Error", "Server error", errorMessage);
                           });
                           break;
                       }
                       case UPDATE_USERS_LIST: {
                           UpdateUsersListCommandData data = (UpdateUsersListCommandData) command.getData();
                           Platform.runLater(() -> chatController.updateUsers(data.getUsers()));
                           break;
                       }
                       case UPD_OK: {
                           this.username = newName;
                           break;
                       }
                       default:
                           Platform.runLater(() -> {
                               NetworkClient.showErrorMessage("Error", "Unknown command from server!", command.getType().toString());
                           });
                   }

               }
           }catch (SocketException e) {
               System.out.println("Соединение потеряно!");
           } catch (IOException e) {
               e.printStackTrace();
               System.out.println("Соединение потеряно!");
           }
       });
        thread.setDaemon(true);
        thread.start();
    }


    public String sendAuthCommand(String login, String password) {
        try {
            Command authCommand = Command.authCommand(login, password);
            dataOutputStream.writeObject(authCommand);

            Command command = readCommand();
            if (command == null) {
                return "Ошибка чтения команды с сервера";
            }

            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    this.username = data.getUsername();
                    this.login = login;

                    return null;
                }

                case AUTH_ERROR:
                case ERROR: {
                    AuthErrorCommandData data = (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }
                default:
                    return "Unknown type of command: " + command.getType();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void sendUpdateCommand(String newName) {
        this.newName = newName;
        try {
            Command updateCommand = Command.updateUserCommand(this.login, newName);
            dataOutputStream.writeObject(updateCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getLogin() { return login; }

    public void sendMessage(String text) throws IOException {
        Message message = new Message(username, text);
        sendMessage(Command.publicMessageCommand(username, message));
        chatController.writeHistory(message);
    }

    public void sendMessage(Command command) throws IOException {
        dataOutputStream.writeObject(command);
    }



    public void sendPrivateMessage(String text, String recipient) throws IOException {
        Message message = new Message(username, text);
        Command command = Command.privateMessageCommand(recipient, message);
        sendMessage(command);
        chatController.writeHistory(message);
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) dataInputStream.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Получен неизвестный объект";
            System.err.println(errorMessage);
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }
}
