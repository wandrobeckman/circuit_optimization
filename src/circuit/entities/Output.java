package circuit.entities;

public class Output {
	
	private String expression;
	private String minimizedExpression;
	private String name;
	
	public Output(String name, String expression, String minimizedExpression){
		this.expression = expression;
		this.name = name;
		this.minimizedExpression = minimizedExpression;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getMinimizedExpression() {
		return minimizedExpression;
	}
	public void setMinimizedExpression(String minimizedExpression) {
		this.minimizedExpression = minimizedExpression;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
