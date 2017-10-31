package sat;

import java.util.Scanner;

/*
import static org.junit.Assert.*;
s
import org.junit.Test;
*/

import sat.SATSolver;
import sat.env.*;
import sat.formula.*;
import java.io.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();



	
	// TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability
    
	
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
    
    public static void main(String args[]) {
    		String input = null;
    		boolean isFile = false;
    		
    		// To print out one line by one line
    		String line = null;
    		
    		System.out.print("Input Filepath: ");
			Scanner s = new Scanner(System.in);
			input = s.nextLine();
			
    		try {
    			// Read file in the default encoding
    			FileReader fileReader = new FileReader(input);
    			
    			// Wrap the file reader in a BufferedReader
    			BufferedReader bufferedReader = new BufferedReader(fileReader);
    			
    			while((line = bufferedReader.readLine()) != null) {
    				System.out.println(line);
    			}
    		}
    		
    		catch(FileNotFoundException ex) {
    			System.out.println("Unable to open file '" + input + "'");
    		}
    		
    		catch(IOException ex) {
    			System.out.println("IOException while reading file '" + input + "'");
    		}
    }
    
    
}