package WordSquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class DictionaryTernary {
    private Node head;

    public DictionaryTernary() {
        head = new Node();

        try {
            /* COULD INCREASE EFFICIENCY by randomizing the word bank
             * before insert.  Ordered input is worst case for tree structures. */

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

    /***
     * A method to find all word bank words that match a given pattern.
     * @param pattern A {@code String} in which {@code .} characters are treated
     *                as wildcards.
     * @return Returns an {@code Iterable} list of word bank words matching the given pattern.
     */
    public Iterable<String> matchPattern(String pattern) {
        HashSet<String> matches = new HashSet<>();
        match(pattern, head, 0, new StringBuilder(), matches);
        return matches;
    }

    private void match(String word, Node nd, int pos, StringBuilder sb, HashSet<String> matches) {
        char c = word.charAt(pos);
        if (c == '.') {
            matchWild(word, nd, pos, sb, matches);
            return;
        }
        if (c == nd.value) {
            sb.append(c);
            if (pos == word.length() - 1) {
                if (nd.valid) matches.add(sb.toString());
            }
            else if (nd.equal != null) match(word, nd.equal, ++pos, sb, matches);
        } else if (c < nd.value) {
            if (nd.smaller != null) match(word, nd.smaller, pos, sb, matches);
        } else { // if (c > nd.value)
            if (nd.bigger != null) match(word, nd.bigger, pos, sb, matches);
        }
    }

    private void matchWild(String word, Node nd, int pos, StringBuilder sb, HashSet<String> matches) {
        if (nd.smaller != null) matchWild(word, nd.smaller, pos, new StringBuilder(sb.toString()), matches);
        if (nd.bigger != null) matchWild(word, nd.bigger, pos, new StringBuilder(sb.toString()), matches);
        sb.append(nd.value);
        if (pos == word.length() - 1) {
            if (nd.valid) matches.add(sb.toString());
        }
        else if (nd.equal != null) match(word, nd.equal, ++pos, sb, matches);
    }

    /*--------------------------------------
    *
    * The following prefix-match methods are not required for operation of WordSquare.
    *
    * --------------------------------------*/

    public Iterable<String> matchPrefix(String prefix) {
        // Get head of subtree for this prefix
        Node nd = find(prefix, head, 0);
        if (nd == null) return null;
        else {
            // Create an iterable set.
            HashSet<String> valid = new HashSet<>();
            // If prefix is a valid word, add it
            if (nd.valid) valid.add(prefix);
            // Fill set with all valid words in prefix subtree
            if (nd.equal != null) fillSet(nd.equal, new StringBuilder(prefix), valid);
            // Return
            if (valid.size() == 0) return null;
            return valid;
        }
    }

    private Node find(String prefix, Node nd, int pos) {
        char c = prefix.charAt(pos);
        if (c == nd.value) {
            if (pos == prefix.length() - 1) return nd;
            else if (nd.equal != null) return find(prefix, nd.equal, ++pos);
            else return null;
        } else if (c < nd.value) {
            if (nd.smaller != null) return find(prefix, nd.smaller, pos);
            else return null;
        } else {
            if (nd.bigger != null) return find(prefix, nd.bigger, pos);
            else return null;
        }
    }

    private void fillSet(Node nd, StringBuilder sb, HashSet<String> valid) {
        if (nd.smaller != null) fillSet(nd.smaller, new StringBuilder(sb.toString()), valid);
        if (nd.bigger != null) fillSet(nd.bigger, new StringBuilder(sb.toString()), valid);
        sb.append(nd.value);
        if (nd.valid) valid.add(sb.toString());
        if (nd.equal != null) fillSet(nd.equal, sb, valid);
    }

    private class Node {
        private boolean valid;
        private char value;
        private Node smaller;
        private Node bigger;
        private Node equal;
    }
}
