package hacker;

import java.util.ArrayList;
import java.util.List;

public class Guesser {
    private StringBuilder password; // current password
    private int passIndex; // selected letter
    private String[] logins; // list of most common logins
    private int loginIndex; // selected logins

    public Guesser(String[] logins) {
        this.logins = logins;
        this.loginIndex = 0;
        this.password = new StringBuilder("A");
        passIndex = 0;
    }

    public String getPassword() {
        return password.toString();
    }

    // A-Z -> a-z -> 0-9
    private static char nextChar(char current) {
        if (current == 'Z') current = '`';
        if (current == 'z') current = '/';
        current = (char) (current + 1);
        return current;
    }

    public void addNewLetter() {
        password.append('a');
        passIndex++;
    }

    public void incrementCurrentLetter() {
        char current = password.charAt(passIndex);
        password.setCharAt(passIndex, nextChar(current));
    }

    @Deprecated
    private static boolean isLower(char current) {
        return 'a' <= current && current <= 'z';
    }

    @Deprecated
    private List<Integer> getLetterLocations(String text) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (isLower(current)) indexes.add(i);
        }
        return indexes;
    }

    @Deprecated
    private char flipCase(char current) {
        if (isLower(current)) return String.valueOf(current).toUpperCase().charAt(0);
        else return String.valueOf(current).toLowerCase().charAt(0);
    }

    @Deprecated
    private String getNextCase(String text, List<Integer> indexes) {
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

    // Get all possible case combinations from a lowercase string
    // Returns a list with one entry for numeric strings
    @Deprecated
    private List<String> getAllCaseCombinations(String text) {
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

    public String getNextLogin() {
        if (loginIndex == logins.length) return "";
        String login = logins[loginIndex];
        loginIndex++;
        return login.trim();
    }
}
