package sat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.io.*;
import java.util.regex.Pattern;

/*
import static org.junit.Assert.*;
s
import org.junit.Test;
*/

import sat.SATSolver;
import sat.env.*;
import sat.formula.*;



public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
    	
*/    	
    }
        
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
/* 
    	assertEquals( Bool.FALSE, e.get(na.getVaria ble()));
*/    	
    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    private static Formula cnfReader(String filename) throws HeaderException, 
    														 FileFormatException,
    														 ClauseException {
    	/*
    	 * Takes in a filename (i.e location of .cnf file) and returns a Formula object
    	 */
    	Pattern comment = Pattern.compile("c.*");
    	Pattern header = Pattern.compile("p [a-zA-Z]+ [0-9]+ [0-9]+"); // p FORMAT VARIABLES CLAUSES
    	Pattern clause = Pattern.compile("[[-]\\d? ]*");
    	Pattern blankLine = Pattern.compile("[ ]*");	
    	
    	String line = null;
    	
    	int variableCount = 0;
    	int clauseCount = 0;
    	
    	ArrayList<String> list = new ArrayList<String>();
    	
    	Formula formula = new Formula(); // output
    	
    	// Check for .cnf file extension
    	try {
			// Read file in the default encoding
    		if(!filename.trim().endsWith(".cnf")) {
        		throw new FileFormatException("FileFormatException: File type is not .cnf");
        	}
    		
			FileReader fileReader = new FileReader(filename);
			
			// Wrap the file reader in a BufferedReader
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null) {
				// Trim all trailing and leading white spaces
				String process = line.trim();
				
				////////////////////////////////////
				// Case 0: Blank Lines or Comments//
				////////////////////////////////////
				if(comment.matcher(process).matches() || blankLine.matcher(process).matches()) {
					continue;
				}
				
				////////////////////////////////////
				// Case 1: Header                 //
				////////////////////////////////////
				else if (header.matcher(process).matches()) {
					// Check for duplicates, if variableCount and clauseCount is greater 
					// than 0. It means that the header is duplicated. 
					if(variableCount != 0 || clauseCount != 0) {
						throw new HeaderException("HeaderException: There is more than one header in the file");
					}
					
					// Split the array
					String[] splitArr = process.split(" ");
					
					// Check if it is the right file format
					if(!splitArr[1].toLowerCase().equals("cnf")){
						throw new HeaderException("HeaderException: Wrong file format specified");
					}
					
					// Parse for the variable count
					if(Pattern.matches("[0-9]+", splitArr[2])) {
						variableCount = Integer.parseInt(splitArr[2]);
					} else {
						throw new HeaderException("HeaderException: Errors in variable count");
					}
					
					if(Pattern.matches("[0-9]+", splitArr[3])) {
						clauseCount = Integer.parseInt(splitArr[3]);
					} else {
						throw new HeaderException("HeaderException: Errors in clause count");
					}
				}

				////////////////////////////////////
				// Case 2: Clauses                //
				////////////////////////////////////
				else { 
					if(clause.matcher(process).matches()) {
						String[] splitArr = process.split(" ");
						for (String item : splitArr) {
							list.add(item);
						}
					} else {
						throw new ClauseException("ClauseException: Errors in clause: " + process);
					}
				}
				
				// Close buffer and file
	    		fileReader.close();
	    		bufferedReader.close();
			}
				
			
			
			// Parse the Array List into Clauses
			Clause clauseToAdd = new Clause();
			HashSet<String> variablesToAdd = new HashSet<String>();
			
			for(String item:list) {
				// If reach the end of a line "0" add the clause to a formula
				// Restart new Clause();
				if(item.equals("0")){
					formula = formula.addClause(clauseToAdd);
					clauseToAdd = new Clause();
				} 
				// Handle negative literals
				else if(item.substring(0,1).equals("-")) {
					// Add positive part of the string as a negative literal.
					String itemProc = item.substring(1);
					variablesToAdd.add(itemProc);
					clauseToAdd = clauseToAdd.add(NegLiteral.make(itemProc));
				}
				// Handle positive literals
				else {
					variablesToAdd.add(item);
					clauseToAdd = clauseToAdd.add(PosLiteral.make(item));
				}
			}
			
			// Handle the case where the clause is not terminated at the last line
			if(!clauseToAdd.isEmpty()) {
				formula = formula.addClause(clauseToAdd);
			}
			
			
			// Check if the number clauses are correct
			if(formula.getSize() != clauseCount) {
				throw new ClauseException("ClauseException: The number of clauses found does not equal the number of clauses declared in header.");
			}

			if(variablesToAdd.size() != variableCount){
				throw new ClauseException("ClauseException: The number of variables found does not equal the number of variables declared in header.");
			}
			
			
			return formula;
			
    	}
        	   	
		
		catch(FileNotFoundException ex) {
			System.out.println("FileNotFoundException: Unable to open file '" + filename + "'");
		}
		
		catch(IOException ex) {
			System.out.println("IOException: Error while reading file '" + filename + "'");
		}
    	
    	catch(HeaderException ex) {
    		System.out.println(ex.getMessage());
    	}
    	
    	catch(FileFormatException ex) {
    		System.out.println(ex.getMessage());
    	}
    	
    	catch(ClauseException ex) {
    		System.out.println(ex.getMessage());
    	}
   	   	
    	return null;
    }
    
    public static void main(String args[]) throws HeaderException, 
    											  FileFormatException, 
    											  ClauseException {
    		String input = null;  		
    		System.out.print("Input Filepath: ");
			Scanner s = new Scanner(System.in);
			input = s.nextLine();
			s.close();
			
			Formula f = cnfReader(input);
			
			System.out.println(f);
			
			Environment e = SATSolver.solve(f);
			System.out.println(e);
			System.out.println("END");
			
			
    		
    }
    
    
}

/*
 * Additional Classes for Custom Exceptions
*/

class HeaderException extends Exception{
	private static final long serialVersionUID = 1L;
	public HeaderException(String message) {
		super(message);
	}
}

class FileFormatException extends Exception{
	private static final long serialVersionUID = 2L;
	public FileFormatException(String message) {
		super(message);
	}
}

class ClauseException extends Exception{
	private static final long serialVersionUID = 3L;
	public ClauseException(String message) {
		super(message);
	}
}