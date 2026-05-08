package app.fitkit.api.util;

import app.fitkit.api.dto.ParsedItem;
import java.util.*;

public class DietNLPAnalyzer {

    // A HashSet is O(1) lookup time. It is infinitely faster than looping through a list.
    // In a real production app, you would load these from a file or database on startup.
    private static final Set<String> FOOD_DICTIONARY = new HashSet<>(Arrays.asList(
            "egg", "eggs", "chicken", "rice", "beef", "steak", "pasta", "bread", 
            "apple", "banana", "oats", "salad", "pizza", "burger", "whey"
    ));

    private static final Set<String> DRINK_DICTIONARY = new HashSet<>(Arrays.asList(
            "milk", "water", "coffee", "tea", "coke", "soda", "juice", "shake", "beer", "wine"
    ));

    public static List<ParsedItem> extractItemsFromNote(String journalNote) {
        if (journalNote == null || journalNote.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Normalize the text: Lowercase everything and remove punctuation using Regex
        String cleanText = journalNote.toLowerCase().replaceAll("[^a-z0-9 ]", "");
        
        // 2. Tokenize: Split the text into an array of words
        String[] words = cleanText.split("\\s+");

        // 3. We use a Map to avoid duplicates (e.g., if they say "chicken and more chicken")
        Map<String, ParsedItem> extractedMap = new HashMap<>();

        for (String word : words) {
            // Ignore words we've already found
            if (extractedMap.containsKey(word)) continue;

            if (FOOD_DICTIONARY.contains(word)) {
                // Initialize with 0 macros. The user will update these later.
                extractedMap.put(word, new ParsedItem(word, "FOOD", 0, 0.0, 0.0, 0.0));
            } else if (DRINK_DICTIONARY.contains(word)) {
                extractedMap.put(word, new ParsedItem(word, "DRINK", 0, 0.0, 0.0, 0.0));
            }
        }

        return new ArrayList<>(extractedMap.values());
    }
}