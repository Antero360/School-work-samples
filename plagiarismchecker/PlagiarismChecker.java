/*
 * Name:        Nevarez-Lira, Antero (nevarez)
 * Date:        May 9, 2019
 * Course:      CSCI 340
 * Instructor:  Dr. Hansen
 * Description: This program checks to see whether or not plagiarism has occured between two files
 *              by comparing the contents of each file using the longest common substring algorithm(lcs).
 *              Program reads in the contents of each file into its own string and finds the
 *              length of the lcs, and finds the plagiarism score. If the score if greater than the 
 *              inputted threshold, program will print out the the file names, and score.
 *              Output format: File1 File2 Score.
 * Bugs:        None.
 */

import java.io.*;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.Formatter;

public class PlagiarismChecker
{
    // Plagiarism(): Default constructor, takes no parameters.
    public PlagiarismChecker()
    {
    }
    
    /*
     * lcsLength(String,String): Takes 2 parameters, 2 string representations of the contents of the files
     * being looked at. Calls auxiliary method CalculateLcsLength(String,String) to return the length
     * of the longest common substring.
     */
    public int lcsLength(String prog1, String prog2)
    {
        return CalculateLcsLength(prog1,prog2);
    }
    
    /*
     * CalculateLcsLength(String,String): Takes 2 parameters, 2 string representations of the contents of the files
     * being looked at (program1,program2). Creates a look-up table, or referenceMatrix, which is used to 
     * store the lengths of the common substrings. Fills referenceMatrix using Bottom-up
     * algorithm.
     * Conditions: if row and col are both 0
     *                 referenceMatrix[row][col] is 0
     *             else if character at position (row - 1) of program1 is equal to character at position (col - 1)
     *                 referenceMatrix[row][col] is (referenceMatrix[row-1][col-1] + 1)
     *             else
     *                 referenceMatrix[row][col] is the max of (referenceMatrix[row-1][col],referenceMatrix[row][col-1])
     * Returns the length of the lcs, which will be located at the lower right corner of the matrix. 
     */
    private int CalculateLcsLength(String prog1, String prog2)
    {
        int[][] referenceMatrix = new int[prog1.length() + 1][prog2.length() + 1];
        for(int x = 0; x <= prog1.length(); x ++)
        {
            for(int y = 0; y <= prog2.length(); y++)
            {
                if(x == 0 || y == 0)
                    referenceMatrix[x][y] = 0;
                else if(prog1.charAt(x-1) == prog2.charAt(y-1))
                    referenceMatrix[x][y] = referenceMatrix[x-1][y-1] + 1;
                else
                    referenceMatrix[x][y] = Math.max(referenceMatrix[x-1][y],referenceMatrix[x][y-1]);
            }
        }
        return referenceMatrix[referenceMatrix.length - 1][referenceMatrix[0].length - 1];
    }
    
    /*
     * plagiarismScore(String,String): Takes 2 parameters, 2 file paths. Calls auxiliary method
     * GetFileContents(String) to get the contents of both files. Calls lcsLength(String,String)
     * to find the length of the longest common substring. Calculates and returns the plagiarism
     * score by multiplying the lcs length by 200, then dividing by the sum of the lengths of
     * the contents of both files.
     */
    public double plagiarismScore(String filename1, String filename2)
    {
        String file1Contents = GetFileContents(filename1);
        String file2Contents = GetFileContents(filename2);
        int substringLength = lcsLength(file1Contents,file2Contents);
        return 200.0 * substringLength / (file1Contents.length() + file2Contents.length());
    }
    
    /*
     * GetFileContents(String): Takes 1 parameter, a file path. Calls auxiliary method ReadFile(String)
     * to obtain and return the contents of the file as a string.
     */
    public String GetFileContents(String filename)
    {
        return ReadFile(filename);
    }
    
    /*
     * ReadFile(String): Takes 1 parameter, a file path. Reads file and saves contents as a string,
     * using the Random Access File functionality. Returns contents of file.
     */
    private String ReadFile(String filename)
    {
        String contents = "";
        try
        {
            RandomAccessFile raf = new RandomAccessFile(new File(filename),"r");
            int index = raf.read();
            while(index != -1)
            {
                contents += (char)index;
                index = raf.read();
            }
        }
        catch(Exception ex)
        {
            System.out.println("Error! Cannot open file.");
        }
        return contents;
    }
    
    /*
     * plagiarismChecker(String[],double): Takes 2 parameters, a list of file paths, and a target, or threshold.
     * Starts at the beginning, checks each file against the remaining on the list. Calculates the score for
     * both files and compares  it ot the threshold. If the score is higher than the threshold,
     * print out both file names, and their score.
     */
    public void plagiarismChecker(String[] filenames, double threshold)
    {
        int currentFileCheck = 0;
        DecimalFormat scoreFormatter = new DecimalFormat("###.##");        
        System.out.format("\n%8s%16s%16s\n","File 1","File 2","Score" );
        while(currentFileCheck < (filenames.length - 1))
        {
            for(int index = currentFileCheck +1; index < filenames.length; index++)
            {
                double score = plagiarismScore(filenames[currentFileCheck],filenames[index]);                
                if(score >= threshold)
                    System.out.format("%8s%16s%16s\n", filenames[currentFileCheck],filenames[index], scoreFormatter.format(score));
            }
            currentFileCheck++;
        }
    }
    
    /*
     * main(): Drives the program 
     */
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("\nEnter the files you would like to check, delimited by a space between files.");
        String input = scan.nextLine();
        String[] files = input.trim().split("\\s+");
        System.out.println("Enter a threshold: ");
        double threshold = Double.parseDouble(scan.nextLine().trim());
        PlagiarismChecker cheatCheck = new PlagiarismChecker();
        cheatCheck.plagiarismChecker(files,threshold);
    }
}