package WordSquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class DictionaryTernary {
    private Node head;

    public DictionaryTernary() {
        head = new Node();
        head.value = 'o';  // Near the center of the alphabet & second most common initial letter.

        try {
            // Open word bank file as a stream.
            InputStreamReader ir = new InputStreamReader(
                    getClass().getResourceAsStream("resources/wordBank.csv"));
            BufferedReader reader = new BufferedReader(ir);

            // Initialize local variable with first line from the stream.
            String workingLine = reader.readLine();

            do {
                workingLine = workingLine.trim().toLowerCase();
                if ((workingLine.length() > 2) &&
                (workingLine.length() < 7) ){
                    insert(workingLine, head, 0);
                }
                workingLine = reader.readLine();
            } while (workingLine != null);

            int x = 0;
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void insert(String word, Node nd, int pos) {
        // Get test char
        char c = word.charAt(pos);
        if (c == nd.value) {
            if (pos == word.length() - 1) {
                nd.valid = true;
                return;
            } else {
                if (nd.equal != null) insert(word, nd.equal, ++pos);
                else {
                    nd.equal = new Node();
                    nd.equal.value = word.charAt(pos + 1);
                    insert(word, nd.equal, ++pos);
                }
            }
        } else if (c < nd.value) {
            if (nd.smaller != null) insert(word, nd.smaller, pos);
            else {
                nd.smaller = new Node();
                nd.smaller.value = c;
                insert(word, nd.smaller, pos);
            }
        } else { // if (c > nd.value)
            if (nd.bigger != null) insert(word, nd.bigger, pos);
            else {
                nd.bigger = new Node();
                nd.bigger.value = c;
                insert(word, nd.bigger, pos);
            }
        }
    }

    public Iterable<String> getAll() {
        HashSet<String> valid = new HashSet<>();
        fillSet(head, new StringBuilder(), valid);
        return valid;
    }

    private void fillSet(Node nd, StringBuilder sb, HashSet<String> valid) {
        if (nd.smaller != null) fillSet(nd.smaller, new StringBuilder(sb.toString()), valid);
        if (nd.bigger != null) fillSet(nd.bigger, new StringBuilder(sb.toString()), valid);
        sb.append(nd.value);
        if (nd.valid) valid.add(sb.toString());
        if (nd.equal != null) fillSet(nd.equal, sb, valid);
    }

    private void find (String word, Node nd, int pos, StringBuilder sb, HashSet<String> matches) {
        char c = word.charAt(pos);
        if (c == '.') {
            findWild(word, nd, pos, sb, matches);
            return;
        }
        if (c == nd.value) {
            sb.append(c);
            if (pos == word.length() - 1) {
                if (nd.valid) matches.add(sb.toString());
            }
            else if (nd.equal != null) find(word, nd.equal, ++pos, sb, matches);
        } else if (c < nd.value) {
            if (nd.smaller != null) find(word, nd.smaller, pos, sb, matches);
        } else { // if (c > nd.value)
            if (nd.bigger != null) find(word, nd.bigger, pos, sb, matches);
        }
    }

    private void findWild (String word, Node nd, int pos, StringBuilder sb, HashSet<String> matches) {
        if (nd.smaller != null) findWild(word, nd.smaller, pos, new StringBuilder(sb.toString()), matches);
        if (nd.bigger != null) findWild(word, nd.bigger, pos, new StringBuilder(sb.toString()), matches);
        sb.append(nd.value);
        if (pos == word.length() - 1) {
            if (nd.valid) matches.add(sb.toString());
        }
        else if (nd.equal != null) find(word, nd.equal, ++pos, sb, matches);
    }

    public HashSet<String> matchWild(String pattern) {
        HashSet<String> matches = new HashSet<>();
        find(pattern, head, 0, new StringBuilder(), matches);
        return matches;
    }



    private class Node {
        private boolean valid;
        private char value;
        private Node smaller;
        private Node bigger;
        private Node equal;
    }
}
