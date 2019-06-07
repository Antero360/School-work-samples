/*
 * Name:        Nevarez-Lira, Antero (nevarez)
 * Date:        April 25, 2019
 * Course:      CSCI 340
 * Instructor:  Dr. Hansen
 * Description: Program asks user for data file, whose contents is the make-up of a KenKen board in the following format:
                    board size
                    cageConstraint operator [list of board positions that make up the cage, in the format: row column]
                    
                ex:
                    3
                    1 - 0 0 0 1
                    1 - 0 2 1 2
                    4 + 1 0 2 0
                    1 - 1 1 2 1
                    1 # 2 2
                Program parses the data from the file, initializes the board and sets up the constraints. Afterwards, proceeds to find a solution
                to the puzzle, using the depth-first search algorithm. Prints board if a solution was found, otherwise prints a "no solution" message
 *              
 * Bugs:        None.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class KenKen
{
    private Board board;
    
    //KenKen(): default constructor, takes no parameters.
    public KenKen(){}
    
    /*
     * InitializePuzzle(String): takes 1 parameter, a file path. calls auxiliary method,
     * ReadFromFile(String) to open and extract the data from the file.
     */    
    public void InitializePuzzle(String fileName) throws IOException
    {
        try
        {
            ReadFromFile(fileName);
        }
        catch(Exception ex)
        {
            System.out.println("Error! Invalid file path.");
        }
    }
    
    /*
     * ReadFromFile(String): takes 1 parameter, a file path. Opens file and extracts
     * all information and creates an object to hold the appropriate information.
     * Initializes the KenKen board to the proper size, creates a Cage to hold the
     * oonstraint information, 
     */
    private void ReadFromFile(String fileName) throws IOException
    {        
        Scanner fileReader = new Scanner(new File(fileName));
        int lineNumber = 1,boardSize = 0;
        while (fileReader.hasNextLine())
        {
            String currentLine = fileReader.nextLine();
            if(lineNumber == 1)
            {
                boardSize = Integer.parseInt(currentLine);
                board = new Board(boardSize);
            }
            else
            {
                String [] specifications = currentLine.trim().split(" ");
                Cage cage = new Cage();
                cage.SetConstraint(Integer.parseInt(specifications[0]));
                cage.SetModifier(specifications[1].charAt(0));
                for(int index = 2; index < specifications.length - 1; index += 2)
                {
                    BoardPosition position = new BoardPosition();
                    position.SetRow(Integer.parseInt(specifications[index]));
                    position.SetCol(Integer.parseInt(specifications[index + 1]));
                    cage.AddToPerimeter(position);
                }
                board.AddCage(cage);
            }
            lineNumber++;
        }
    }
    
    /*
     * CanBePlaced(int,int,int): takes 3 parameters, row position, column position,
     * and a value. Calls auxiliary methods, IsAllowedRow(int,int,int), IsAllowedColumn(int,int,int)
     * and ConstraintCompliance(int,int,int) to see if the given value can be placed
     * at board location(row,column).
     */
    private boolean CanBePlaced(int row, int col, int value)
    {
        return (IsAllowedRow(row,col,value) && IsAllowedColumn(row,col,value) && ConstraintCompliance(row,col,value));
    }
    
    /*
     * IsAllowedRow(int,int,int): takes 3 parameters, row position, column position,
     * and a value. Checks to see if the value is allowed on the current row.
     * Returns true if the value is not already on the row, returns false if the
     * value is already on the row.
     * 
     */
    private boolean IsAllowedRow(int row, int col, int value)
    {
        boolean isAllowed = true;
        for(int x = 0; x < board.GetSize(); x++)
        {
            if((x != col) && (board.GetValueAt(row, x) == value))
            {
                isAllowed = false;
                break;
            }
        }
        return isAllowed;
    }
    
    /*
     * IsAllowedColumn(int,int,int): takes 3 parameters, a row position, a column position,
     * and a value. Checks to see if the value is allowed on the current column.
     * Returns true if the value is not already on the column, returns false if
     * the value is already on the column.
     */
    private boolean IsAllowedColumn(int row, int col, int value)
    {
        boolean isAllowed = true;
        for(int x = 0; x < board.GetSize(); x++)
        {
            if((x != row)  && (board.GetValueAt(x,col) == value))
            {
                isAllowed = false;
                break;
            }
        }
        return isAllowed;
    }
    
    /*
     * ConstraintCompliance(int,int,int): takes 3 parameters, a row position, a column position,
     * and a value. Checks to see what cage the current position is in. Checks to see
     * if the cage contains any null positions. If the cage does not contain any
     * null positions, checks to see if all the values within each position of the cage
     * comply with the cage constraint. Returns false if they do not comply. If the cage
     * contains any null values, does nothing and returns true.
     * Constraint Rules: x = constraint value
        (x #) # signifies no operation to be performed. cell must contain x. Must be 1*1 cage.
        (x /) cell values must be divided and must equal x. Must be 2*1 cage.
        (x -) cell values must be subtracted and must equal x. Must be 2*1 cage.
        (x +) cell values must be added and must equal x. Must be at least 2*1 cage
        (x *) cell values must be multiplied and must equal x. Must be at least 2*1 cage
     */
    private boolean ConstraintCompliance(int row, int col, int value)
    {
        boolean isGood = true;
        Cage targetCage = new Cage();
        for(Cage cage: board.GetCages())
        {
            ArrayList<BoardPosition> tempList = cage.GetCage();
            int index = 0;
            while(index < tempList.size())
            {
                BoardPosition currentPosition = tempList.get(index);
                if((currentPosition.GetRow() == row) && (currentPosition.GetCol() == col))
                {
                    targetCage = cage;
                    break;
                }
                index++;
            }
            if(targetCage.GetConstraint() != 0)
                break;
        }
        
        int calculatedValue = 0;
        switch(targetCage.GetModifier())
        {
            case '#':
                if(value != targetCage.GetConstraint())
                    isGood = false;
                
                break;
            case '*':                
                if(CheckCageForNulls(targetCage) == false )
                {
                    if(calculatedValue == 0)
                        calculatedValue = 1;
                    
                    for(int index = 0; index < targetCage.GetCage().size(); index++)
                        calculatedValue *= board.GetValueAt(targetCage.GetCage().get(index).GetRow(), targetCage.GetCage().get(index).GetCol());
                    
                    if(calculatedValue != targetCage.GetConstraint())
                        isGood = false;
                }
                break;                
            case '/':                
                if(CheckCageForNulls(targetCage) == false )
                {
                    if((board.GetValueAt(targetCage.GetCage().get(0).GetRow(),targetCage.GetCage().get(0).GetCol()) / board.GetValueAt(targetCage.GetCage().get(1).GetRow(),targetCage.GetCage().get(1).GetCol())) != targetCage.GetConstraint())
                        if((board.GetValueAt(targetCage.GetCage().get(1).GetRow(),targetCage.GetCage().get(1).GetCol()) / board.GetValueAt(targetCage.GetCage().get(0).GetRow(),targetCage.GetCage().get(0).GetCol())) != targetCage.GetConstraint())
                            isGood = false;
                }
                break;
            case '+':
                if(CheckCageForNulls(targetCage) == false )
                {
                    for(int index = 0; index < targetCage.GetCage().size(); index++)
                        calculatedValue += board.GetValueAt(targetCage.GetCage().get(index).GetRow(), targetCage.GetCage().get(index).GetCol());
                    
                    if(calculatedValue != targetCage.GetConstraint())
                        isGood = false;
                }
                break;
            case '-':
                if(CheckCageForNulls(targetCage) == false )
                {
                    if((board.GetValueAt(targetCage.GetCage().get(0).GetRow(),targetCage.GetCage().get(0).GetCol()) - board.GetValueAt(targetCage.GetCage().get(1).GetRow(),targetCage.GetCage().get(1).GetCol())) != targetCage.GetConstraint())
                        if((board.GetValueAt(targetCage.GetCage().get(1).GetRow(),targetCage.GetCage().get(1).GetCol()) - board.GetValueAt(targetCage.GetCage().get(0).GetRow(),targetCage.GetCage().get(0).GetCol())) != targetCage.GetConstraint())
                            isGood = false;
                }
                break;            
        }
        return isGood;
    }
    
    /*
     * CheckCageForNulls(Cage): takes 1 parameter, a cage. Checks to see if there
     * any null cells in the cage. Returns true if so, else returns false.
     */
    private boolean CheckCageForNulls(Cage cage)
    {
        boolean hasNullPositions = false;
        for(int index = 0; index < cage.GetCage().size(); index++)
        {
            if((board.GetValueAt(cage.GetCage().get(index).GetRow(),cage.GetCage().get(index).GetCol()) == 0) )
            {
                hasNullPositions = true;
                break;
            }
        }        
        return hasNullPositions;
    }
    
    /*
     * FindSolution(): takes no parameters. Calls auxiliary method Solve() to find
     * a solution to the current puzzle. Prints out board if there is a solution,
     * else prints a "no solution" message.
     */    
    public void FindSolution()
    {
        if(Solve())
        {
            System.out.println("Solution found!\nPrinting solution...");
            board.PrintBoard();
        }
        else
            System.out.println("No solution was found!");
    }
    
    /*
     * Solve(): takes no parameters. Starts at the upper left corner of the puzzle board.
     * checks to see if current position contains a null value, if so, generates
     * a number from 1 to board size, inclusive, and in numerical sequence. Checks
     * to see if number can be placed in current position. If true, places number on position
     * and checks to see if value complies with cage constraint, if it does not, resets
     * back to null. Calls itself again to move on, otherwise, backtracks to previous position
     * if possible. Returns true if board is filled and all constraints compliances
     * have been met.
     * Uses Depth-first search algorithm with backtracking in order to find a solution.  
     */
    private boolean Solve()
    {
        for(int row = 0; row < board.GetSize(); row++)
        {
            for(int col = 0; col < board.GetSize(); col++)
            {
                if(board.GetValueAt(row, col) == 0)
                {
                    for(int value = 1; value <= board.GetSize(); value++)
                    {
                        if(CanBePlaced(row,col,value))
                        {
                            board.SetValueAt(row, col, value);
                            if(ConstraintCompliance(row,col,value) == false)
                                board.SetValueAt(row, col,0);
                            else
                            {
                                if(Solve())
                                    return true;
                                else
                                    board.SetValueAt(row, col,0);
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    /*
     * Main(): Drives the program. 
     */
    public static void main(String[] args) throws IOException
    {
        System.out.println("Enter a file to work with: ");
        Scanner scan = new Scanner(System.in);
        String file = scan.nextLine();
        KenKen puzzle = new KenKen();
        System.out.println("Initializing KenKen board...\nSetting cages...");
        puzzle.InitializePuzzle(file);
        System.out.println("Searching for solution...");
        puzzle.FindSolution();                
    }
    
    /*
     * Board class: auxiliary class used to represent KenKen board.
     */
    class Board
    {
        private int boardSize;
        private int[][] board;
        private ArrayList<Cage> cages;
        
        /*
         * Board(int): constructor, takes 1 parameter, the size of the board.
         * Initializes a 2-D array to size, used to represent the board.
         * Initializes an empty list to hold all the cages the current puzzle will
         * have. Calls auxiliary method SetDefaultValues to set board cells to
         * default value of 0, or null.
         */
        public Board(int boardSize)
        {
            this.boardSize = boardSize;
            board = new int[boardSize][boardSize];
            cages = new ArrayList<Cage>();
            SetDefaultValues();
        }
        
        // GetCages(): takes no parameters. Returns the list of Cages. 
        public ArrayList<Cage> GetCages()
        {
            return cages;
        }        
        
        /*
         * GetValueAt(int,int): takes 2 parameters, a row position and a column position.
         * Returns the value at board[row][column].
         */
        public int GetValueAt(int row, int col)
        {
            return board[row][col];
        }
        
        //GetSize(): takes no paramters. Returns the size of the board.
        public int GetSize()
        {
            return boardSize;
        }
        
        //AddCage(Cage): takes 1 parameter, a cage. Adds cage to the board's cages list.
        public void AddCage(Cage cage)
        {
            cages.add(cage);
        }
        
        /*
         * SetDefaultValues(): takes no paramters. Sets each cell in the board to 
         * a default value of 0, or null.
         */
        private void SetDefaultValues()
        {
            for(int row = 0; row < boardSize; row++ )
            {
                for(int col = 0; col < boardSize; col++)
                {
                    board[row][col] = 0;
                }
            }
        }
        
        /*
         * SetValueAt(int,int,int): takes 3 parameters, a row position, a column position,
         * and a value. Sets the cell at that position to that value. 
         */
        public void SetValueAt(int row, int col, int value)
        {
            board[row][col] = value;
        }
        
        //PrintBoard(): takes no parameters. Prints the board in a readable format
        public void PrintBoard()
        {
            for(int row = 0; row < boardSize; row++)
            {
                for(int col = 0; col < boardSize; col++)
                {
                    System.out.print(board[row][col] + "\t");
                }
                System.out.println();
            }
        }        
    }
    
    /*
     * BoardPosition class: auxiliary class used to represent a cell of the board.
     */
    protected class BoardPosition
    {
        private int row;
        private int col;
        
        /*
         * BoardPosition(): constructor, takes no parameters. Initializes the
         * variables row, and column to a default value of 0.
         */
        public BoardPosition()
        {
            row = 0;
            col = 0;
        }
        
        //GetRow(): takes no parameters. Returns the row position of the cell.
        public int GetRow()
        {
            return row;
        }
        
        //GetCol(): takes no parameters. Returns the column position of the cell.
        public int GetCol()
        {
            return col;
        }
        
        /*
         * SetRow(int): takes 1 parameter, a row position. Sets this row position
         * to value.
         */
        public void SetRow(int row)
        {
            this.row = row;
        }
        
        /*
         * SetCol(int): takes 1 parameter, a column position. Sets this row position
         * to value.
         */
        public void SetCol(int col)
        {
            this.col = col;
        }
    }
    
    /*
     * Cage class: auxiliary class used to represent a cage of the KenKen board. A cage
     * is to have constraint value, a modifier/operator, and a list of BoardPosition
     */
    protected class Cage
    {
        private char constraintModifier;
        private int constraint;
        private ArrayList<BoardPosition> cagePerimiter;
        
        /*
         * Cage(): constructor, takes no parameters. Initializes the modifier/operator
         * to a space character, or null,sets constraint value to a default of 0, and
         * initializes the list of BoardPosition.
         */
        public Cage()
        {
            constraintModifier = ' ';
            constraint = 0;
            cagePerimiter = new ArrayList<BoardPosition>();
        }
        
        //GetConstraint(): takes no parameters. Returns the constraint value for the cage
        public int GetConstraint()
        {
            return constraint;
        }
        
        //GetModifier(): takes no parameters. Returns the modifier/operator for the constraint
        public char GetModifier()
        {
            return constraintModifier;
        }
        
        //GetCage(): takes no parameters. Returns the list of BoardPosition that make up the cage.
        public ArrayList<BoardPosition> GetCage()
        {
            return cagePerimiter;
        }
        
        //SetConstraint(int): takes 1 parameter, a value. Sets cage constraint to that value.
        public void SetConstraint(int value)
        {
            constraint = value;
        }
        
        //SetModifier(char): takes 1 parameter, a character. Sets constraint modifier to that character.
        public void SetModifier(char rule)
        {
            constraintModifier = rule;
        }
        
        /*
         * AddToPerimiter(BoardPosition): takes 1 parameter, BoardPosition. Adds this position
         * to the list of BoardPosition that make up the Cage
         */
        public void AddToPerimeter(BoardPosition position)
        {
            cagePerimiter.add(position);
        }        
    }    
}