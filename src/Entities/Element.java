package Entities;

import java.util.ArrayList;
import java.util.List;

public class Element {

	private List<Integer> inputs;
	private List<Integer> outputs;
	private List<String> inputName;
	private int computedValue = -1;
	private int size =0;
	private String computedExpression;

	public String getComputedExpression() {
		return this.computedExpression;
	}

	public void setComputedExpression(String expression) {
		this.computedExpression = expression;
	}

	public int getComputedValue() {
		return this.computedValue;
	}

	public void setComputedValue(int computedValue) {
		this.computedValue = computedValue;
	}

	public void addInput(int input) {
		inputs.add(input);
		size++;
	}
	public void addInputName(String inputName) {
		this.inputName.add(inputName);
	}

	public void addOutput(int output) {
		this.outputs.add(output);
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Element() {
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
		inputName = new ArrayList<>();
	}

	public List<Integer> getOutputs() {
		return this.outputs;
	}

	public List<Integer> getInputs() {
		return this.inputs;
	}

	public List<String> getInputName() {
		return this.inputName;
	}

	public void setInputName(List<String> inputName) {
		this.inputName = inputName;
	}

}
