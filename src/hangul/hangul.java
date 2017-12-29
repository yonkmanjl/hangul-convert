package hangul;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * hangul.
 *
 * TO DO: vowels, W, SH, Y, ending S
 *
 * @author Jenelle Yonkman | A00930379
 * @version 0.3
 */
public class hangul {
    
    private static char[] initialJaeum = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ','ㅍ', 'ㅎ'};
    private static char[] medialJaeum = {'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ'};
    private static char[] finalJaeum = {'X','ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
    
    private static HashMap<String, Character> simpleVowelMap = new HashMap<String, Character>();
    private static HashMap<String, Character> simpleConsonantMap = new HashMap<String, Character>();
        
    private static String[] vowels = {"AA", "AE", "AH", "AO", "AW", "AW", "AY", "EH", "ER", "EY", "IH", "IY", "OW", "OY", "UH", "UW", "W"};
    private static String hangulWord = "";
    
    
    private static void makeMaps() {
        mapSimpleVowels();
        mapSimpleConsonants();
    }
    
    private static void mapSimpleVowels() {
        simpleVowelMap.put("AA", new Character ('ㅏ'));
        simpleVowelMap.put("AE", new Character ('ㅐ'));
        simpleVowelMap.put("AH", new Character ('ㅓ'));
        simpleVowelMap.put("AO", new Character ('ㅗ'));
        simpleVowelMap.put("EH", new Character ('ㅔ'));
        simpleVowelMap.put("ER", new Character ('ㅓ'));
        simpleVowelMap.put("IH", new Character ('ㅣ'));
        simpleVowelMap.put("IY", new Character ('ㅣ'));
        simpleVowelMap.put("OW", new Character ('ㅗ'));
        simpleVowelMap.put("UH", new Character ('ㅜ'));
        simpleVowelMap.put("UW", new Character ('ㅜ'));
    }
    
    private static void mapSimpleConsonants() {
        simpleConsonantMap.put("B", new Character ('ㅂ'));
        simpleConsonantMap.put("CH", new Character ('ㅊ'));
        simpleConsonantMap.put("D", new Character ('ㄷ'));
        simpleConsonantMap.put("DH", new Character ('ㄷ'));
        simpleConsonantMap.put("F", new Character ('ㅍ'));
        simpleConsonantMap.put("G", new Character ('ㄱ'));
        simpleConsonantMap.put("HH", new Character ('ㅎ'));
        simpleConsonantMap.put("JH", new Character ('ㅈ'));
        simpleConsonantMap.put("K", new Character ('ㅋ'));
        simpleConsonantMap.put("L", new Character ('ㄹ'));
        simpleConsonantMap.put("M", new Character ('ㅁ'));
        simpleConsonantMap.put("N", new Character ('ㄴ'));
        simpleConsonantMap.put("NG", new Character ('ㅇ'));
        simpleConsonantMap.put("P", new Character ('ㅍ'));
        simpleConsonantMap.put("R", new Character ('ㄹ'));
        simpleConsonantMap.put("S", new Character ('ㅅ'));
        simpleConsonantMap.put("T", new Character ('ㅌ'));
        simpleConsonantMap.put("TH", new Character ('ㅌ'));
        simpleConsonantMap.put("V", new Character ('ㅂ'));
        simpleConsonantMap.put("Z", new Character ('ㅈ'));
        simpleConsonantMap.put("ZH", new Character ('ㅈ'));
    }
    

    /**
     * Reads content lines of dictionary into string array, removing comments.
     * @param dictionary
     * @return array of dictionary lines
     * @throws IOException
     */
    private static String[] readFile(Path dictionary) throws IOException {
        List<String> tempList = Files.readAllLines((dictionary), StandardCharsets.UTF_8);
        Iterator<String> commentIterator = tempList.iterator();
        boolean finishedComments = false;
        while (commentIterator.hasNext() && !finishedComments) {
            String commentCheck = commentIterator.next();
            if (commentCheck.startsWith(";;;")) {
                commentIterator.remove();
            } else {
                finishedComments = true;
            }
        }
        return tempList.toArray(new String[tempList.size()]); 
    }
    
    /**
     * Separates the first dictionary word from pronunciation guide on same line.
     * @param dictionaryLine
     * @return dictionary word
     */
    private static String getFirstWord(String dictionaryLine) {
        Pattern multiwords = Pattern.compile(".*\\(\\d\\)$"); //multiple versions of word - ends in (digit)
        String firstWord = dictionaryLine.split(" ")[0]; //get first word
        if (multiwords.matcher(firstWord).matches()) {
            firstWord = firstWord.substring(0, firstWord.length() - 3); //remove digit
        }
        return firstWord;
    }
    
    /**
     * Binary search to return requested word and pronunciation.
     * @param dictionaryLines
     * @param word
     * @return word and pronunciation if found
     */
    private static String searchDictionary(String[] dictionaryLines, String word) {
        boolean found = false;
        int start = 0;
        int end = dictionaryLines.length - 1;
        int mid = 0;
        
        while (!found && start <= end) { //binary search
            mid = (start + end) / 2;
            String dictionaryLine = dictionaryLines[mid];
            String firstWord = getFirstWord(dictionaryLine);
            if (firstWord.equals(word)) {
                found = true;
                return dictionaryLine;
            } else {
                if (word.compareTo(firstWord) < 0) {
                    end = mid - 1;
                } else {
                    start = mid + 1;
                }
            }
        }
        throw new IllegalArgumentException("Word not found");
    }
    
    /**
     * Removes number that indicates stress
     * @param wordParts
     * @return wordParts with stress removed
     */
    private static String[] removeStress(String[] wordParts) {
        Pattern stressPattern = Pattern.compile(".*\\d$");
        for (int i = 1; i < wordParts.length; i++) {
            //remove stress
            if (stressPattern.matcher(wordParts[i]).matches()) {
                wordParts[i] = wordParts[i].substring(0, wordParts[i].length() - 1);
            }
        }
        return wordParts;
    }
    
    /**
     * Matches 
     * @param wordPart
     * @return
     */
    private static int findVowel(String wordPart) {
        if (simpleVowelMap.containsKey(wordPart)) {
            return Arrays.binarySearch(medialJaeum, simpleVowelMap.get(wordPart));
        } else if (wordPart.equals("EY")) {
            
        }
        return 0;
    }
    
    private static int findConsonent(String wordPart, char[] consList) {
        if (simpleConsonantMap.containsKey(wordPart)) {
            return Arrays.binarySearch(consList, simpleConsonantMap.get(wordPart));
        }
            return 0;
    }
    
    private static String assembleWord(String[] wordParts) {
        int next = 0;
        int letter = 1;
        int i = 1;
        while (i < wordParts.length) {
            int vowelIndex = Arrays.binarySearch(vowels, wordParts[i]);
            if (letter == 1) {
                findFirstLetter(vowelIndex, wordParts[i]);
                next = 44032;
                if (vowelIndex >= 0) { //syllable starts with a vowel
                    next += (Arrays.binarySearch(initialJaeum, 'ㅇ') * 588); //first letter ㅇ
                    next += (findVowel(wordParts[i]) * 28);
                    letter = 3;
                } else {  //syllable starts with a consonant
                    int temp = findConsonent(wordParts[i], initialJaeum);
                    if (temp > 18) { // SH sound
                        next += temp;
                        letter = 3;
                    } else {
                        next += (findConsonent(wordParts[i], initialJaeum) * 588);
                        letter = 2;
                        if (i == wordParts.length - 1) {
                            next += (Arrays.binarySearch(medialJaeum, 'ㅡ') * 28);
                        }
                    }
                }                
                i++;
            } else if (letter == 2) {
                if (vowelIndex >= 0) {
                    next += (findVowel(wordParts[i]) * 28);
                    i++;
                    letter = 3;
                } else {
                    next += (Arrays.binarySearch(medialJaeum, 'ㅡ') * 28);
                    letter = 1;
                }
            }
            else if (letter == 3) {
                if (wordParts[i].equals("L") || wordParts[i].equals("M") 
                        || wordParts[i].equals("N") || wordParts[i].equals("NG") 
                        || wordParts[i].equals("R")) {
                    next += findConsonent(wordParts[i], finalJaeum);
                    i++;
                }
                letter = 1;
            }
            if (i >= wordParts.length || letter == 1) {
                hangulWord += (char) (next);
            }

        }
        return hangulWord;

    }
    
    
    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) {
        makeMaps();
        Path dictionary = Paths.get("dictionary.txt");
        Scanner wordScanner = new Scanner(System.in);
        System.out.println("Please enter a single correctly spelled English word.");
        String word = wordScanner.next().toUpperCase();
        String[] dictionaryLines = null;
        try {
            dictionaryLines = readFile(dictionary);
        } catch (IOException e) {
            System.out.println("Dictionary not available.");
            e.printStackTrace();
        }
        String foundLine = searchDictionary(dictionaryLines, word);
        String[] wordParts = foundLine.split("\\s+");
        wordParts = removeStress(wordParts);
        String hangulWord = assembleWord(wordParts);
        System.out.println(hangulWord);
        wordScanner.close();
    }
}
