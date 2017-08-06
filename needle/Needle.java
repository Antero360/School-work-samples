    `/*
 * Name:        Nevarez-Lira, Antero (nevarez)
 * Date:        February 10, 2014
 * Course:      CSCI 340
 * Instructor:  Sarto
 * Description: 
 *              Searches a given file for the word "needle". Outputs accordingly.
 * Bugs:        None.
 */
package needle;
import java.util.*;
import java.io.*;
public class Needle
{

    public static void main(String[] args) throws FileNotFoundException
    {
        //iterates through every file name given to the array, and passes it to
        //the search method. Prints out file name and line number of where 
        //needle was found.
        for (int x = 0; x < args.length; x++)
        {
            System.out.println(needleSearch(args[x]));
        }
    }
    /*
     * Takes one param, the name of the file to be searched.  Opens a file stream
     * to open up the file, iterates through every line in the file searching
     * for the word "needle". If found, outputs file name and line number of
     * where needle was found. If file does not contain the needle, outputs 
     * "fileName: NOT FOUND". Throws an exception if user inputs wrong file type.
     */
    private static String needleSearch(String fileName)
    {
        //keeps track of how many lines have been read
        int lineCounter = 0;
        //stores the current line being read
        String currentLine = "";
        //keeps track of whether or not the needle was found in file
        boolean needleFound = false;
        //stores message to be printed to user
        String outputMessage = "";
        try
        {
            //opens up file stream
            Scanner file = new Scanner(new File(fileName));
            //iterates through file
            while (file.hasNext() && needleFound != true)
            {
                lineCounter++;
                currentLine = file.nextLine().toLowerCase();
                if (currentLine.equals("needle") == true)
                {
                    needleFound = true;
                    outputMessage = fileName + ": " + lineCounter;
                } 
                else if (file.hasNext() == false && needleFound != true)
                {
                    outputMessage = fileName + ": NOT FOUND";
                }
            }
        } 
        catch (IOException error)
        {
            error = new IOException("File not found!");
        }
        return outputMessage;
    }
}
