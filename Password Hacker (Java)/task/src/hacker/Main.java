package hacker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static String[] readFileAsLines(String filename) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get(filename)));
        if (!data.isEmpty()) {
            return data.split("\n");
        } else {
            return null;
        }
    }

    private static boolean isLower(char current) {
        return 'a' <= current && current <= 'z';
    }
    private static List<Integer> getLetterLocations(String text) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (isLower(current)) indexes.add(i);
        }
        return indexes;
    }

    private static char flipCase(char current) {
        if (isLower(current)) return String.valueOf(current).toUpperCase().charAt(0);
        else return String.valueOf(current).toLowerCase().charAt(0);
    }

    private static String getNextCase(String text, List<Integer> indexes) {
        int currentIndex = indexes.size()-1;
        StringBuilder next = new StringBuilder(text);
        char current = 'a';
        while (isLower(current)) {
            if (currentIndex < 0) return "";
            int index = indexes.get(currentIndex);
            current = flipCase(next.charAt(index));
            next.setCharAt(index, current);
            currentIndex--;
        }
        return next.toString();
    }

    private static List<String> getAllCaseCombinations(String text) {
        text = text.trim();
        List<String> combinations = new ArrayList<>();
        List<Integer> indexes = getLetterLocations(text);
        if (indexes.isEmpty()) {
            combinations.add(text);
            return combinations;
        }
        String current = text;
        while (!"".equals(current)) {
            combinations.add(current);
            current = getNextCase(current, indexes);
        }
        return combinations;
    }

    @Deprecated
    private static char nextChar(char current) {
        if (current == 'z') current = '/';
        current = (char) (current + 1);
        return current;
    }

    @Deprecated
    public static String generateNext(String text) {
        int endIndex = text.length()-1;
        char current = text.charAt(endIndex);
        current = nextChar(current);
        StringBuilder updated = new StringBuilder(text.substring(0, endIndex));
        if (current != ':') updated.append(current);
        else {
            int index = endIndex-1;
            updated.append('a');
            while (true) {
                if (index < 0) {
                    updated.insert(0,'a');
                    break;
                }
                current = updated.charAt(index);
                current = nextChar(current);
                if (current != ':') {
                    updated.setCharAt(index, current);
                    break;
                } else {
                    updated.setCharAt(index, 'a');
                    index--;
                }
            }
        }
        return updated.toString();
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 2) System.out.println("Usage: java hacker.Main IP_address port");
        else {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            String[] passwords = readFileAsLines("passwords.txt");
            try (
                    Socket socket = new Socket(ip, port);
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output  = new DataOutputStream(socket.getOutputStream())
            ) {
                int passwordIndex = 0;
                String sent = null;
                boolean correct = false;
                while (!correct) {
                    if (passwordIndex == passwords.length) break;
                    List<String> guesses = getAllCaseCombinations(passwords[passwordIndex]);
                    for (String guess: guesses) {
                        output.writeUTF(guess);
                        sent = guess;
                        String received = input.readUTF(); // read the reply from the server
                        correct = "Connection success!".equals(received);
                        if (correct) break;
                    }
                    passwordIndex++;
                }
                if (correct) System.out.println(sent);
                else System.out.println("Could not find password");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
