package circuit.json;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import circuit.entities.Input;
import circuit.entities.Output;
import util.StringHelper;

public class TruthTableCreator{
	
	public static int[][] truthTable(Output output, List<Input>inputs){
		ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("JavaScript");
        try {
			se.eval("false = 0");
			se.eval("true = 1");
		} catch (ScriptException e1) {
			// TODO Auto-generated catch block
			
		}
		int truthTable[][] = truthTable( inputs.size());
		String ignoreList = "^ + ()=;~";
		int rows = truthTable.length;
		int columns = truthTable[0].length;
		for(int row = 0; row<rows;row++ ){
			String expression = output.getMinimizedExpression();
			for(int column = 0;column<columns-1;column++){
				
		            for (int i = 0; i < expression.length(); i++) {
		                char array[] = expression.toCharArray();
		                boolean ignore = false;
		                String replacebleValue = array[i] + "";
		                i--;
		                do {
		                    i++;

		                    if (!ignoreList.contains(replacebleValue)) {
		                        if ((i - 1) >= 0) {
		                            if (ignoreList.contains(array[i - 1] + "")) {
		                                ignore = true;
		                            }
		                        } else {
		                            ignore = true;
		                        }

		                        if ((i + 1) < array.length) {
		                            if (ignoreList.contains(array[i + 1] + "")) {
		                                ignore = true;
		                            } else {
		                                replacebleValue = replacebleValue + array[i + 1];
		                                ignore = false;
		                            }
		                        } else {
		                            ignore = true;
		                        }

		                    } else {
		                        ignore = true;
		                    }

		                } while (!ignore);
		                if (replacebleValue.equals(inputs.get(column).getName())) {
		                    expression = StringHelper.replaceOnIndex(expression, i-(replacebleValue.length()-1), inputs.get(column).getName() + "_READ_", inputs.get(column).getName());
		                    i += "_READ_".length();
		                }

		            }
		            boolean replace = true;
		            if(truthTable[row][column]!=1)
		            	replace = false;
		            expression = expression.replaceAll(inputs.get(column).getName()+"_READ_", replace+"");
		        
			}
			//System.out.println(expression);
			expression = expression.replaceAll(" \\+ ", "||");
			expression = expression.replaceAll("\\~", "!");
			expression = expression.replaceAll(" ", "&&");
			boolean result = false;
			try {
				result = (Boolean)se.eval(expression);
				//result = Boolean.parseBoolean(evaluation);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(expression);
			//System.out.println(result);
			int out = result?1:0;
			
			
			truthTable[row][inputs.size()] = out;	
		}
		
		return truthTable;
		
		
	}
	
	private static int [][] truthTable(int nInputs) {
		
		int tableSize = (int) Math.pow(2, nInputs);
		int truthTable[][] = new int[tableSize][nInputs + 1];

		for (int column = 0; column < nInputs; column++) {
			//System.out.println();

			int repeat = nInputs - column;
			int pattern = 0;
			
			repeat = (int) Math.pow(2, repeat) / 2;
			int cont = repeat;
			for (int row = 0; row < tableSize; row++) {
				if (cont == 0) {
					cont = repeat;
					pattern = pattern==1?0:1;
				}
				cont--;

				truthTable[row][column] = pattern;
			}
		}
		return truthTable;
	}
	
	
}