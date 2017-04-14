package Entities;

public class Gate extends Element {
	
	
	
	public int computeNor() {
		int result = this.getInputs().get(0);
		String tempExpression = "~("+this.getInputName().get(0);
		if(this.getInputName().get(0)==null)
			tempExpression = "~("+this.getInputName().get(0);
		boolean boolCalc = false;
		if (result == 1)
			boolCalc = true;
		for (int i = 1; i < this.getSize(); i++){
			
			if (this.getInputs().get(i) == 1)
				boolCalc = boolCalc || true;
			else
				boolCalc = boolCalc || false;
			
		}
		
		for(String expression: this.getInputName())
			tempExpression = tempExpression + " + "+ expression;
			
		tempExpression = tempExpression+")";
		setComputedExpression(tempExpression);
		if (!boolCalc)
			return 1;
		return 0;
	}
	
	

}
