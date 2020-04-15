package JavaRush.JavaMultithreading.BigProjects.com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
        for (Connection connection:
             connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Сообщение не отправлено.");
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())) {
            ConsoleHelper.writeMessage("Сервер запущен...");
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Ошибка. Закрытие сокета сервера.");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            if (socket != null && socket.getRemoteSocketAddress() != null) {
                ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом: " +
                        socket.getRemoteSocketAddress()
                );
            }
            String userName = null;
            try (Connection connection = new Connection(socket)){
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом.");
            } finally {
                if (userName != null && !userName.isEmpty()) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                }
                ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто.");
            }
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                MessageType type = answer.getType();
                String name = answer.getData();

                if (type == MessageType.USER_NAME) {
                    if (!name.isEmpty()) {
                        if (!connectionMap.containsKey(name)) {
                            connectionMap.put(name, connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return name;
                        }
                    }
                }
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pair:
                 connectionMap.entrySet()) {
                String name = pair.getKey();
                if (!name.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String textMessage = userName + ": " + message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, textMessage));
                } else {
                    ConsoleHelper.writeMessage("Произошла ошибка при отправке сообщения.");
                }
            }
        }
    }
}