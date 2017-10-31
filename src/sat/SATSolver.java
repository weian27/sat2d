package sat;

import java.util.Iterator;

import immutable.ImList;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
    	
   		System.out.println(formula.getClauses().toString());
		return solve(formula.getClauses(), new Environment());
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
    	
    	// if there are no clauses, the formula is trivially satisfiable
    	if (clauses.isEmpty()) { 
    		return env;
    	}
    	// if there is an empty clause, the clause list is unsatisfiable -- fail and backtrack
    	// (use empty clause to denote a clause evaluated to FALSE based on the variable binding in the environment)
    	for (Clause i : clauses) { 
    		if (i.isEmpty()) {
    			return null;
    		}
    	}
    	// Otherwise, find smallest clause (by number of literals)
    	Iterator<Clause> clausesIterator = clauses.iterator();
        Clause smallestClause = clausesIterator.next(); // to get the first clause in clauses ImList
    	for (Clause i : clauses) {
    		if (i.size() < smallestClause.size()) {
    			smallestClause = i;
    		}
    	}
    	
    	Literal literal1 = smallestClause.chooseLiteral(); // arbitrarily pick a literal from the smallest clause
		ImList<Clause> newClauses;
		Environment newEnv;
		boolean makeTrue; // used to make literal true
		
		// To find literal that makes clause true
		// if statement:   To make literal ~a true, check (~a == a) => wrong
		// else statement: To make literal a true, check (a == a) => correct
		if (literal1 == PosLiteral.make(literal1.getVariable() ) ){
			newEnv = env.putTrue(literal1.getVariable());
			newClauses = substitute(clauses, literal1);
			makeTrue = true;
		}
		else {
			newEnv = env.putFalse(literal1.getVariable());
			newClauses = substitute(clauses, literal1);
			makeTrue = false;
		}
		
		// If the clause has only one literal, bind its variable in the environment so that the clause is satisfied
		if (smallestClause.size() == 1) {
			return solve(newClauses, newEnv);
		}

		// If clause has more than one literals
		// First try setting the literal to TRUE, substitute for it in all the clauses, then solve() recursively.
		// If that fails, then try setting the literal to FALSE, substitute, and solve() recursively.
		else {
			Environment newEnv2 = solve(newClauses, newEnv);
			
			if (newEnv2 == null){
				if (makeTrue == false) {
					newEnv = env.putTrue(literal1.getVariable()); // if variable = false => set to true
				}
				else {
					newEnv = env.putFalse(literal1.getVariable()); // if variable = true => set to false
				}
				newClauses = substitute(clauses, literal1.getNegation()); // make the negation (~literal1) true this time
				return solve(newClauses, newEnv);
			}
			else return newEnv2;
		}

    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
    	
    	return clauses.add(new Clause(l)); // add new clause which consists only of the literal l to the list of clauses given
    }

}
>>>>>>> 6ab6a11dd117592dcb7e1f45aea76380bc26b778
