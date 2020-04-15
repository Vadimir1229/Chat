package JavaRush.JavaMultithreading.BigProjects.com.javarush.task.task30.task3008.client;

import JavaRush.JavaMultithreading.BigProjects.com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    public void run() {

    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void processIncomingMessage(String message) {
            if (message != null) {
                ConsoleHelper.writeMessage(message);
                SimpleDateFormat dateFormat = null;
                if (message.contains(": ")) {
                    String[] textMessage = message.split(": ");
                    if (textMessage.length == 2 && textMessage[1] != null) {
                        String userName = textMessage[0];
                        String userText = textMessage[1];
                        switch (userText) {
                            case "дата":
                                 dateFormat = new SimpleDateFormat("d.MM.YYYY");
                                break;
                            case "день":
                                dateFormat = new SimpleDateFormat("d");
                                break;
                            case "месяц":
                                dateFormat = new SimpleDateFormat("MMMM");
                                break;
                            case "год":
                                dateFormat = new SimpleDateFormat("YYYY");
                                break;
                            case "время":
                                dateFormat = new SimpleDateFormat("H:mm:ss");
                                break;
                            case "час":
                                dateFormat = new SimpleDateFormat("H");
                                break;
                            case "минуты":
                                dateFormat = new SimpleDateFormat("mm");
                                break;
                            case "секунды":
                                dateFormat = new SimpleDateFormat("ss");
                                break;
                        }
                        if (dateFormat != null) {
                            String returnText = String.format("Информация для %s: %s",
                                    userName, dateFormat.format(Calendar.getInstance().getTime()));
                            sendTextMessage(returnText);
                        }
                    }
                }
            }
        }

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            String textGreeting = "Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.";
            sendTextMessage(textGreeting);
            super.clientMainLoop();
        }
    }
}
