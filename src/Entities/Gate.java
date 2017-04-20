package Entities;

import circuit.entities.Input;

public class Gate extends Element {

	public int computeNor() {
		
		int result = this.getInputs().get(0);
		String tempExpression = "~(" + this.getInputName().get(0);
		
		if (this.getInputName().get(0) == null)
			tempExpression = "~(" + this.getInputName().get(0);
		boolean boolCalc = result == 1 ? true : false;

		for (int i = 1; i < this.getSize(); i++) {

			if (this.getInputs().get(i) == 1)
				boolCalc = boolCalc || true;
			else
				boolCalc = boolCalc || false;

		}

		for (int i = 1; i < this.getInputName().size(); i++)
			tempExpression = tempExpression + " + " + this.getInputName().get(i);

		tempExpression = tempExpression + ")";
		setComputedExpression(tempExpression);
		
		if (!boolCalc)
			return 1;
		return 0;
	}

}
