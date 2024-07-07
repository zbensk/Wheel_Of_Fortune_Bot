import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class WOF_Bot {
    // fields
    private String[] words; // each raw word that must be solved
    private ArrayList<String> discoveredLetters; // letters that are in any of the words
    private static ArrayList<String> defaultWordlist; // massive wordlist containing ~370,000 words, this will get trimmed down later to meet each word's specifications

    // constructor
    public WOF_Bot(String input) {
        words = convertToArray(input);
        discoveredLetters = new ArrayList<String>();
        defaultWordlist = new ArrayList<String>();
        loadWordlist("wordlist.txt");

        // this is where the outputs will be generated
        findValidWords(); 
    }

    // methods

    /**
     * Calls various methods to trim down the size of the wordlist for each word, generating the bot's output
     */
    private void findValidWords() {
        // fill up discoveredLetters with everything necessary
        findDiscoveredLetters();
        for (String word : words) {
            // creating the wordlist that will continously get trimmed
            ArrayList<String> filteredWordlist = filterByLength(word.length()); // first filter by length
            
            // now filter down the wordlist by letters
            for (int i = 0; i < word.length(); i++) {
                String let = word.substring(i, i + 1);
                // if letter is not known
                if (let.equals("-")) {
                    filteredWordlist = filterOutLetters(i, filteredWordlist);
                } else {
                    filteredWordlist = filterByLetter(i, let, filteredWordlist);
                }
            }

            // output the information for each word
            System.out.println(generateOutput(word, filteredWordlist));
        }
    }

    /**
     * Trims the massive defaultWordlist into one more manageable based on length
     * @param len the length of the word to filter by
     * @return an ArrayList<String> of only words of len long
     */
    private ArrayList<String> filterByLength(int len) {
        ArrayList<String> filteredWordlist = new ArrayList<String>();
        for (String wordlistWord : defaultWordlist) {
            if (wordlistWord.length() == len) {
                filteredWordlist.add(wordlistWord);
            }
        }
        return filteredWordlist;
    }

    /**
     * Using discoveredLetters, removes all words from the filteredWordlist that contain an already discoveredLetter in the i spot.
     * @param i index to filter with
     * @param wordlist filteredWordlist
     * @return filteredWordlist after bad elements are removed
     */
    private ArrayList<String> filterOutLetters(int i, ArrayList<String> wordlist) {
        for (int idx = wordlist.size() - 1; idx >= 0; idx--) {
            String word = wordlist.get(idx);
            String let = word.substring(i, i + 1);
            for (String letter : discoveredLetters) {
                if (let.equals(letter)) {
                    wordlist.remove(word);
                }
            }
        }
        return wordlist;
    }

    /**
     * Removes all words from filteredWordlist that don't have letter let in index i location
     * @param i index to filter with
     * @param let letter to check
     * @param wordlist filteredWordlist
     * @return filteredWordlist after bad elements are removed
     */
    private ArrayList<String> filterByLetter(int i, String let, ArrayList<String> wordlist) {
        for (int idx = wordlist.size() - 1; idx >= 0; idx--) {
            String word = wordlist.get(idx);
            String letter = word.substring(i, i + 1);
            if (!let.equals(letter)) {
                wordlist.remove(word);
            }
        }
        return wordlist;
    }

    /**
     * Transforms data into readable message, that is displayed differently based on size of wordlist
     * @param word word whose data is being outputted
     * @param wordlist filteredWordlist for that word
     * @return a String message that will be displayed to the user
     */
    private String generateOutput(String word, ArrayList<String> wordlist) {
        String message = word + " : " + wordlist.size() + " possibilities";
        if (wordlist.size() <= 30) {
            message += " : ";
            for (String w : wordlist) {
                message += w + ", ";
            }
        }
        return message;
    }

    /**
     * Iterates through String[] words, filling up discoveredLetters with all letters found (no duplicates allowed)
     */
    private void findDiscoveredLetters() {
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                String letter = word.substring(i, i + 1);
                if (!letter.equals("-") && !inDiscoveredLetters(letter)) {
                    discoveredLetters.add(letter);
                }
            }
        }
    }

    /**
     * @param letter
     * @return true if letter is in ArrayList discoveredLetters
     */
    private boolean inDiscoveredLetters(String letter) {
        for (String let : discoveredLetters) {
            if (let.equals(letter)) { return true; }
        }
        return false;
    }

    /**
     * Separates the raw string by spaces into a String[] containing each separate word
     * @param rawString the initial string sent by the user
     * @return String[] of separated words
     */
    private String[] convertToArray(String rawString) {
        String[] splitByWord = rawString.split(" ");
        return splitByWord;
    }

    /**
     * Transforms text file of a wordlist into ArrayList<String> defaultWordlist with each word as a separate element
     * Error if fileName is not valid
     * @param fileName
     */
    private void loadWordlist(String fileName) {
        File file = new File(fileName);
        try (Scanner scan = new Scanner(file)) {
            while (scan.hasNextLine()) {
                defaultWordlist.add(scan.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // main for testing
    public static void main(String[] args) {
        // Get rawInput from the user
        System.out.println("Enter what you see in the puzzle, with a dash(-) for any unknown letter, and a space to mark a separation between words:");
        Scanner scan = new Scanner(System.in);
        String rawInput = scan.nextLine().toLowerCase();
        scan.close();

        WOF_Bot bot = new WOF_Bot(rawInput);
    }
}