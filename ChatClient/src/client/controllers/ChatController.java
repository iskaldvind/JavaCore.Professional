package client.controllers;

import client.NetworkClient;
import client.models.Network;
import clientserver.Command;
import clientserver.Message;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChatController {

    @FXML
    public ListView<String> usersList;

    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatHistory;
    @FXML
    private TextField textField;
    @FXML
    private TextField usernameTitle;

    private Network network;
    private String selectedRecipient;
    private File historyFile;

    private String userName = "";
    private final static String HISTORIES_PATH = "ChatClient/history";


    public void setLabel(String usernameTitle) {
        this.userName = usernameTitle;
        this.usernameTitle.setText(usernameTitle);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void initialize() {
//        usersList.setItems(FXCollections.observableArrayList(NetworkClient.USERS_TEST_DATA));
        sendButton.setOnAction(event -> ChatController.this.sendMessage());
        textField.setOnAction(event -> ChatController.this.sendMessage());
        usernameTitle.setOnAction(event -> ChatController.this.changeName());


        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus();
                if (! cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                } else {
                    selectionModel.clearSelection();
                    selectedRecipient = null;
                }
            });
            return cell ;
        });

    }

    private void sendMessage() {
        String message = textField.getText();

        if(message.matches("\\s*")) {
            return;
        }

        appendMessage(new Message(userName, message));
        textField.clear();

        try {
            if (selectedRecipient != null) {
                network.sendPrivateMessage(message, selectedRecipient);
            }
            else {
                network.sendMessage(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
            NetworkClient.showErrorMessage("Ошибка подключения", "Ошибка при отправке сообщения", e.getMessage());
        }

    }

    private void changeName() {
        String newName = usernameTitle.getText();

        if(newName.matches("\\s*")) {
            usernameTitle.setText(newName);
            return;
        }

        network.sendUpdateCommand(newName);
    }

    public void appendMessage(Message message) {
        chatHistory.appendText(message.getDate());
        chatHistory.appendText(System.lineSeparator());
        String sender = message.getSender().equals(userName) ? "Я: " : message.getSender() + ": ";
        chatHistory.appendText(sender + message.getText());
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
    }

    public void setUsernameTitle(String username) {

    }

    public void updateUsers(List<String> users) {
        usersList.setItems(FXCollections.observableArrayList(users));
    }

    public void connectHistory() {
        historyFile = new File(HISTORIES_PATH + "/history_" + network.getLogin() + ".txt");
        try {
            historyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeHistory(Message message) {
        try(BufferedOutputStream fileOS = new BufferedOutputStream(new FileOutputStream(historyFile.getPath(), true))) {
            String line = message.getTimestamp() + "@@@" + message.getDate() + "@@@" + message.getSender() + "@@@" + message.getText() + "\n";
            byte[] bytes = line.getBytes();
            fileOS.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getHistory() {
        byte[] buff = new byte[1024];
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedInputStream fileIS = new BufferedInputStream(new FileInputStream(historyFile.getPath()))) {
            int countRead;
            while ((countRead = fileIS.read(buff)) > 0) {
                stringBuilder.append(new String(buff, 0, countRead));
            }

            String log = stringBuilder.toString();
            if (log.length() > 0) {
                String[] codedMessages = log.split("\n");
                final int LAST_MESSAGES = 100;
                for (int i = Math.max(0, codedMessages.length - LAST_MESSAGES); i < codedMessages.length; i++) {
                    String[] messageParts = codedMessages[i].split("@@@", 4);
                    Message message = new Message(messageParts[2], Long.parseLong(messageParts[0]), messageParts[1], messageParts[3]);
                    appendMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}