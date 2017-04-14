package circuit.entities;

import java.util.ArrayList;
import java.util.List;

public class CircuitModel {

	private String circuitName;
	private List<Input> inputs;
	private List<Output> outputs;
	
	public CircuitModel(){
		inputs = new ArrayList<Input>();
		outputs = new ArrayList<Output>();
	}
	
	public List<Input> getInputs() {
		return inputs;
	}
	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}
	public List<Output> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
	}
	public String getName() {
		return circuitName;
	}
	public void setName(String circuitName) {
		this.circuitName = circuitName;
	} 
	
	public void addInput(String name){
		inputs.add(new Input(name));
	}
	
	public void addOutput(String name, String expression, String minimizedExpression){
		outputs.add(new Output( name,  expression,  minimizedExpression));
	}
	
}
