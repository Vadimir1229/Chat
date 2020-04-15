package JavaRush.JavaMultithreading.BigProjects.com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String readString;
        try {
            readString = reader.readLine();
        } catch (IOException e) {
            writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            readString = readString();
        }
        return readString;
    }

    public static int readInt() {
        int readInt;
        try {
            readInt = Integer.parseInt(readString());
        } catch (NumberFormatException e) {
            writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            readInt = readInt();
        }
        return readInt;
    }
}
