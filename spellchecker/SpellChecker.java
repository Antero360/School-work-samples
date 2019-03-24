/*
 * Name:        Nevarez-Lira, Antero (nevarez)
 * Date:        March 19, 2019
 * Course:      CSCI 340
 * Instructor:  Dr. Hansen
 * Description: 
 *              This program creates a rudimentary spellchecker which is implemented
 *              by using a Trie. The Trie is implemented using an array of TrieNodes.
 *              Program prompts the user for a file name, searches for the file,
 *              opens the file, and searches each line for any spelling errors.
 *              
 * Bugs:        None.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SpellChecker
{
    private Lexicon dictionary;
    private String alphabetString;
    
    /*
     * main: Creates a spellchecker object, prompts user for a file to work with.
     * Opens file and calls the spellchecker.CheckFile(file) to check the file.
     */
    public static void main(String[] args) throws IOException
    {
        try
        {
            SpellChecker checker = new SpellChecker();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a file you would like to work with: ");
            String targetFile = scanner.nextLine();
            checker.CheckFile(targetFile);
        }
        catch(Exception e)
        {
            System.out.println("Error! File cannot be found.");
        }
    }
    
    /*
     * SpellChecker(): Contructor, takes no parameters. Initializes alphabetString
     * as a string containing all the letters of the alphabet. Initializes
     * the lexicon from the appropriate lexicon file.
     */
    public SpellChecker() throws IOException
    {
        alphabetString = "abcdefghijklmnopqrstuvwxyz";
        try
        {
            dictionary = new Lexicon("enable1augmented.txt");
        }
        catch(Exception e)
        {
            System.out.println("Error! File 'enable1augmented.txt' cannot be found.");
        }
    }
    
    /*
     * CheckFile(String): Takes 1 parameter, a file name and calls the Inspect(file)
     * method to check the file for spelling errors. Returns nothing.
     */
    public void CheckFile(String fileName) throws IOException
    {
        try
        {
            InspectFile(fileName);
        }
        catch(Exception e)
        {
            System.out.println("Error! File cannot be found.");
        }
    }
    
    /*
     * InspectFile(String): Takes 1 parameter, a file name, opens up the file,
     * and reads in the file line by line. Strips current line of all punctuations
     * then checks to see if the current line is a multi-word line or a single-word
     * line. If it contains just one word, current line, along with current line number
     * is passed to SearchDictionary(int,string) for error checking. If it has
     * more than one word, current line is split on each space and each word, along 
     * with current line number, is passed on to SearchDictionary(int,string) for
     * error checking. Closes file afterwards. Returns nothing.
     */    
    private void InspectFile(String fileName) throws IOException
    {
        int lineCounter = 1;
        Scanner file = new Scanner(new File(fileName));
        while (file.hasNextLine())
        {
            String currentLine = file.nextLine().toLowerCase().replaceAll("[^a-zA-Z\\s]", "").trim();
            if (currentLine.isEmpty() == false)
            {
                if (currentLine.contains(" "))
                {
                    String[] words = currentLine.split(" ");
                    for (String word : words)
                    {
                        SearchDictionary(lineCounter,word);
                    }
                }
                else
                {
                    SearchDictionary(lineCounter,currentLine);
                }
            }
            lineCounter++;
        }
        file.close();
    }
    
    /*
     * SearchDictionary(int,string): Takes 2 parameters, a line number, and a 
     * word. Checks to see if word is contained within dictionary(lexicon). If
     * it is not within the dictionary, calls PrintSuggestions(line,word) to 
     * print all possible suggested words. Returns nothing.
     */
    private void SearchDictionary(int lineNumber, String word)
    {
        if (dictionary.ContainsWord(word) == false)
            PrintSuggestions(lineNumber,word);
    }
    
    /*
     * PrintSuggestions(int,string): Takes 2 parameters, a line number, and a word.
     * Passes both parameters to GetSuggestions(line number, word) to obtain 
     * suggested words and prints everything in a nice format. Returns nothing.
     * Line #: 'Word' is not spelled correctly!
     * Suggestions:
     * [suggested words]
     */
    private void PrintSuggestions(int lineNumber, String word)
    {
        System.out.println(String.format("Line %1$s: \"%2$s\" is not spelled correctly!", lineNumber, word));
        System.out.println("Suggestions:");
        ArrayList<String> suggestionList = GetSuggestions(word);
        Collections.sort(suggestionList);
        String suggestions = String.join(",", suggestionList);
        System.out.println(String.format("[%1$s]",suggestions));
        System.out.println();        
    }
    
    /*
     * GetSuggestions(string): Takes 1 parameter, a word and calls 5 different
     * methods to generate suggestions: BruteForceLetterInsert(word),
     * BruteForceLetterDelete(word), BruteForceLetterReplace(word),
     * BruteForceLetterSwap(word), and BruteForceWordSplit(word). Goes through
     * each generated list, and adds each suggestion to a master list, provided
     * that the suggestion is not already in the master list.
     * Returns master list of suggestions.
     */
    private ArrayList<String> GetSuggestions(String word)
    {
        ArrayList<String> suggestions = new ArrayList<String>();
        
        ArrayList<String> letterInsert = BruteForceLetterInsert(word);
        for(String suggestion:letterInsert)
        {
            if(suggestions.contains(suggestion) == false)
                suggestions.add(suggestion);
        }
        
        ArrayList<String> letterDelete = BruteForceLetterDelete(word);
        for(String suggestion:letterDelete)
        {
            if(suggestions.contains(suggestion) == false)
                suggestions.add(suggestion);
        }
        
        ArrayList<String> letterReplace = BruteForceLetterReplace(word);
        for(String suggestion:letterReplace)
        {
            if(suggestions.contains(suggestion) == false)
                suggestions.add(suggestion);
        }
        
        ArrayList<String> letterSwap = BruteForceLetterSwap(word);
        for(String suggestion:letterSwap)
        {
            if(suggestions.contains(suggestion) == false)
                suggestions.add(suggestion);
        }
        
        ArrayList<String> wordSplit = BruteForceWordSplit(word);
        for(String suggestion:wordSplit)
        {
            if(suggestions.contains(suggestion) == false)
                suggestions.add(suggestion);
        }
        
        return suggestions;
    }
    
    /*
     * BruteForceLetterInsert(string): Takes 1 parameter, a word. Creates a 
     * mutable version of the word, and inserts every letter of the alphabet
     * at each position in the string. After each insertion, checks to see if
     * newly created string is a word contained within the dictionary, and if
     * so, adds it to a list of suggestions. Afterwards, removes the inserted
     * letter, thereby resetting itself back to the original word.
     * Returns the list of suggestions.
     */
    private ArrayList<String> BruteForceLetterInsert(String word)
    {
        StringBuilder mutableWord = new StringBuilder(word);
        ArrayList<String> suggestions = new ArrayList<String>();
        for(int x = 0; x < mutableWord.length(); x++)
        {
            int letterIndex = 0;
            while(letterIndex < alphabetString.length())
            {
                mutableWord.insert(x,alphabetString.charAt(letterIndex));
                if(dictionary.ContainsWord(mutableWord.toString()) == true)
                    if(suggestions.contains(mutableWord.toString()) == false)
                        suggestions.add(mutableWord.toString());
                mutableWord.deleteCharAt(x);
                letterIndex++;
            }
        }
        return suggestions;
    }
    
    /*
     * BruteForceLetterDelete(string): Takes 1 parameter, a word. Creates a 
     * mutable version of the word, iterates through the entire word and 
     * deletes each letter. After each deletion, checks to see if newly created
     * string is a word contained within the dictionary, and if so, adds it to
     * a list of suggestions. Afterwards, adds the deleted letter back, thereby
     * resetting itself back to the orginal word. Returns the list of suggestions.
     */
    private ArrayList<String> BruteForceLetterDelete(String word)
    {
        StringBuilder mutableWord = new StringBuilder(word);
        ArrayList<String> suggestions = new ArrayList<String>();
        for(int x = 0; x < mutableWord.length(); x++)
        {
            char temp = mutableWord.charAt(x);
            mutableWord.deleteCharAt(x);
            if(dictionary.ContainsWord(mutableWord.toString()) == true)
                if(suggestions.contains(mutableWord.toString()) == false)
                    suggestions.add(mutableWord.toString());
            mutableWord.insert(x, temp);
        }
        return suggestions;
    }
    
    /*
     * BruteForceLetterReplace(string): Takes 1 parameter, a word. Creates a 
     * mutable version of the word, iterates through the entire word and 
     * replaces each letter with each letter of the alphabet. After each
     * replacement, checks to see if newly created string is a word contained
     * within the dictionary, and if so, adds it to a list of suggestions.
     * Afterwards, adds the deleted letter back, thereby resetting itself back
     * to the orginal word. Returns the list of suggestions.
     */
    private ArrayList<String> BruteForceLetterReplace(String word)
    {
        StringBuilder mutableWord = new StringBuilder(word);
        ArrayList<String> suggestions = new ArrayList<String>();
        for(int x = 0; x < mutableWord.length(); x++)
        {
            int letterIndex = 0;
            while(letterIndex < alphabetString.length())
            {
                char alphabetLetter = alphabetString.charAt(letterIndex);
                char temp = mutableWord.charAt(x);
                if(alphabetLetter != temp)
                    mutableWord.setCharAt(x, alphabetLetter);
                
                if(dictionary.ContainsWord(mutableWord.toString()) == true)
                    if(suggestions.contains(mutableWord.toString()) == false)
                        suggestions.add(mutableWord.toString());
                mutableWord.setCharAt(x, temp);
                letterIndex++;
            }
        }
        return suggestions;
    }
    
    /*
     * BruteForceLetterSwap(string): Takes 1 parameter, a word. Creates a 
     * mutable version of the word, iterates through the entire word and 
     * swaps each adjacent letter. After each swap, checks to see if newly
     * created string is a word contained within the dictionary, and if so, adds
     * it to a list of suggestions. Afterwards, swaps the letter back,
     * thereby resetting itself back to the orginal word. 
     */
    private ArrayList<String> BruteForceLetterSwap(String word)
    {
        StringBuilder mutableWord = new StringBuilder(word);
        ArrayList<String> suggestions = new ArrayList<String>();
        for(int x = 0; x < mutableWord.length() - 1; x++)
        {
            char temp1 = mutableWord.charAt(x);
            char temp2 = mutableWord.charAt(x+1);
            mutableWord.setCharAt(x, temp2);
            mutableWord.setCharAt((x+1), temp1);
            if(dictionary.ContainsWord(mutableWord.toString()) == true)
                if(suggestions.contains(mutableWord.toString()) == false)
                    suggestions.add(mutableWord.toString());
            mutableWord.setCharAt(x, temp1);
            mutableWord.setCharAt((x+1), temp2);
        }        
        return suggestions;
    }
    
    /*
     * BruteForceWordSplit(string): Takes 1 parameter, a word. Creates a 
     * mutable version of the word, checks to see if the length of the string
     * is greater than or equal to 2. If so, goes through the entire length of 
     * the string - 1, and adds a space after each character. Splits the word on
     * that space, and checks to see if the created pair of strings are words
     * contained within the dictionary and only if they are, the words are added
     * to a list of suggestions. Afterwards, adds the deleted letter back, thereby
     * resetting itself back to the orginal word. Returns the list of suggestions.
     */
    private ArrayList<String> BruteForceWordSplit(String word)
    {
        ArrayList<String> suggestions = new ArrayList<String>();
        if(word.length() >= 2)
        {
            StringBuilder mutableWord = new StringBuilder(word);
            for(int x = 1; x < mutableWord.length()-1; x++)
            {
                mutableWord.insert(x," ");
                String[] wordPair = mutableWord.toString().split(" ");
                if( (dictionary.ContainsWord(wordPair[0])) && (dictionary.ContainsWord(wordPair[1])))
                    suggestions.add(mutableWord.toString());
                mutableWord.deleteCharAt(x);
            }
        }        
        return suggestions;
    }
    
    /*
     * Lexicon class. Implements the Trie class.
     */
    static class Lexicon
    {
        private Trie lexicon;
        
        /*
         * Lexicon(string): Constructor, takes 1 parameter, a file name. 
         * Initializes lexicon variable as a Trie, and calls the BuildFromFile(file)
         * to build the lexicon.
         */
        public Lexicon(String fileName) throws IOException
        {
            lexicon = new Trie();
            BuildFromFile(fileName);
        }
        
        /*
         * BuildFromFile(string): Takes 1 parameter, a file name. Opens file
         * and reads in each line and calls the trie.Insert(string) to add every
         * word in the line to the lexicon. Closes file afterwards.
         * Returns nothing.
         */
        private void BuildFromFile(String fileName) throws IOException
        {
            Scanner file = new Scanner(new File(fileName));
            while (file.hasNextLine())
            {
                lexicon.Insert(file.nextLine());
            }
            file.close();
        }
        
        /*
         * ContainsWord(string): Takes 1 parameter, a word. Calls trie.Search(word)
         * to search the trie for the word. Returns true/false value of Search()
         */
        public boolean ContainsWord(String word)
        {
            return lexicon.Search(word);
        }
        
        /*
         * The Trie class. Implements the TrieNode class.
         */
        private class Trie
        {
            private TrieNode root;
            
            /*
             * Trie(): Constructor, takes no parameters. Initializes the root
             * node as a TrieNode.
             */
            public Trie()
            {
                root = new TrieNode();
            }
            
            /*
             * Insert(string): Takes 1 parameter, a word. Iterates through the 
             * word and inserts each letter of the word into the trie tree as a
             * TrieNode. Returns nothing.
             */
            public void Insert(String word)
            {
                TrieNode node = root;
                for(int letterIndex = 0; letterIndex < word.length(); letterIndex++)
                {
                    char letter = word.charAt(letterIndex);
                    int nodeIndex = letter - 'a';
                    if(node.IsNodeEmpty(nodeIndex) == true)
                    {
                        TrieNode placeholderNode = new TrieNode();
                        node.Insert(nodeIndex, placeholderNode);
                        node = placeholderNode;
                    }
                    else
                        node = node.GetNode(nodeIndex);
                }
                node.SetIsAWord(true);
            }
            
            /*
             * Search(string): Takes 1 parameter, a target word to search for.
             * Calls the SearchTrie(target) method to traverse through the trie
             * and locate target word. Returns false if SearchTrie(target)
             * returns as null, and returns true if and only if SearchTrie(target)
             * returns a TrieNode node and that node is does in fact contain a 
             * word.
             */
            public boolean Search(String target)
            {
                boolean isFound = false;
                TrieNode node = SearchTrie(target);
                if(node == null)
                {
                    isFound = false;
                }
                else
                {
                    if(node.IsAWord())
                        isFound = true;
                }
                return isFound;
            }
            
            /*
             * SearchTrie(string): Takes 1 parameter, a target word to search for.
             * Iterates through the entire length of the word and seaches the trie
             * for each letter in the word. If the word is found, it returns the
             * TrieNode node where it is found, otherwise, returns null if 
             * the word cannot be found, or if the node is the root.
             */
            private TrieNode SearchTrie(String target)
            {
                TrieNode node = root;
                for(int targetIndex = 0; targetIndex < target.length();targetIndex++)
                {
                    char letter = target.charAt(targetIndex);
                    int nodeIndex = letter - 'a';
                    if(node.IsNodeEmpty(nodeIndex) == false)
                        node = node.GetNode(nodeIndex);
                    else
                        return null;
                }
                
                if(node == root)
                    return null;
                return node;
            }
            
            /*
             * TrieNode class. Implemented using an array.
             */
            private class TrieNode
            {
                private TrieNode[] childNodes;
                private boolean isAword;
                
                /*
                 * TrieNode(): Constructor, takes no parameters. Initializes a 
                 * TrieNode node with an array for a its child TrieNodes. The size
                 * of each array is set to 26, representing each of the letters
                 * of the alphabet.
                 */
                public TrieNode()
                {
                    childNodes = new TrieNode[26];
                }
                
                /*
                 * SetIsAWord(boolean): Takes 1 parameter, a boolean True/False value
                 * which is used to set the isWord variable if a word is a word.
                 */
                public void SetIsAWord(boolean isWord)
                {
                    isAword = isWord;
                }
                
                /*
                 * IsAWord(): Takes no parameter. returns the value of isAword
                 * variable, which represents that a word is contained within the
                 * trie.
                 */
                public boolean IsAWord()
                {
                    return isAword;
                }
                
                /*
                 * Insert(int,TrieNode): Takes 2 parameters, an integer to represent
                 * a position in the array implementation of the Trie, and a TrieNode
                 * which contains the data to be inserted. Inserts node at position
                 * given.
                 */
                public void Insert(int index,TrieNode data)
                {
                    childNodes[index] = data;
                }
                
                /*
                 * GetNode(int): Takes 1 parameter, an integer representing a position
                 * and retrieves the data node contained at given position.
                 */
                public TrieNode GetNode(int index)
                {
                    return childNodes[index];
                }
                
                /*
                 * IsNodeEmpty(int): Takes 1 parameter, an integer representing a
                 * position in the array implementation of the Trie. Checks to see
                 * if given position is empty. Return true if so, otherwise return
                 * false.
                 */
                public boolean IsNodeEmpty(int index)
                {
                    if(childNodes[index] == null)
                        return true;
                    else
                        return false;
                }
            }
        }
    }   
}