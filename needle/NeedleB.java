/*
 * Name:        Nevarez-Lira, Antero (nevarez)
 * Date:        February 10, 2014
 * Course:      CSCI 340
 * Instructor:  Sarto
 * Description: 
 *              Searches a given file for the binary representation of a needle
 *              "0000 0000". Outputs accordingly.
 * Bugs:        None.
 */
package needle;
import java.util.*;
import java.io.*;
public class NeedleB
{
    public static void main(String[] args)
    {
        //iterates through every file name given to the array, and passes it to
        //the search method. Prints out file name and the two characters whose
        //bits create the needle "0000 0000"
        for(int x =0; x< args.length; x++)
        {
            System.out.println(binaryNeedleSearch(args[x]));
        }
    }
    /*
     * Takes one param, the name of the file to be searched.  Opens a file stream
     * to open up the file, iterates through every line in the file searching
     * for the 8bit binary needle("0000 0000"). If found, outputs file name and 
     * the 2 characters whose bits create the needle. If file does not contain 
     * the needle, outputs "fileName: NOT FOUND". Throws an exception if user 
     * inputs wrong file type.
     */
    private static String binaryNeedleSearch(String fileName)
    {
        //holds the 8-bit binary needle as a string of 0s
        String needle = "00000000";
        //stores the BitSet of the current line being read
        BitSet currentLineBitSet;
        //stores the current line being read
        String currentLine = "";
        //stores the bytes of the current line being read
        byte[] currentLineInBytes;
        //stores the BitSet of the current line as a string of 0s and 1s
        String binary = "";
        //keeps track of whether or not the needle was found in file
        boolean needleFound = false;
        //stores message to be printed to user
        String outputMessage = "";
        try
        {
            //opens file stream to open up file
            Scanner file = new Scanner(new File(fileName));
            //iterates through the file
            while(file.hasNext() && needleFound != true)
            {
                //sets currentLine to the current line being read
                currentLine = file.nextLine();
                //converts and stores the current line as an array of bytes
                currentLineInBytes = currentLine.getBytes();
                //stores the bytes in the array as a BitSet
                currentLineBitSet = fromByteArray(currentLineInBytes);
                //iterates through the BitSet
                for(int y = 0; y < currentLineBitSet.length(); y++)
                {
                    if(currentLineBitSet.get(y) == true)
                    {
                        binary += 1;
                    }
                    else
                    {
                        binary += 0;
                    }
                }
               
                
                if(binary.contains(needle))
                {
                    needleFound = true;
                    System.out.println(binary.indexOf(needle));
                    outputMessage = fileName + ": " + currentLine.substring((binary.indexOf(needle) / 8), (binary.indexOf(needle) / 8) + 2);
                }
                else if (file.hasNext() == false && needleFound != true)
                {
                    outputMessage = fileName + ": NOT FOUND";
                }
            }
        }
        catch(IOException error)
        {
            error = new IOException("File not found!");
        }
        return outputMessage;
    }
    /*
     * Takes one param, an array of bytes.  takes every element in the array
     * and converts into a BitSet.
     */
    public static BitSet fromByteArray(byte[] bytes)
    {
	BitSet bits = new BitSet();
	for (int i = 0; i < bytes.length * 8; i++)
        {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0)
            {
                bits.set(i);
            }
	}
	return bits;
    }
}