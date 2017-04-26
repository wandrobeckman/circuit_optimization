package Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import circuit.entities.Input;

public class Chromosome {

	private Gene connectionMatrix[][];
	private Gene chromosome[];
	private Element circuit[];
	private int truthTableChrom[][];
	private int nInputs;
	private int nGates;
	private int fitness;
	private int maxPenalty;
	private boolean feasible;
	private List<Input> inputs;
	private String optimumExpression;
   

	public Chromosome(List<Input> inputs) {
		this.nInputs = inputs.size();
		this.inputs = inputs;
		this.nGates = (nInputs * nInputs) + 1;
		this.chromosome = new Gene[numCells()];
		this.connectionMatrix = new Gene[(nInputs * nInputs) + nInputs][nGates];
		this.maxPenalty = (numCells() + nGates) + (numCells() + nGates) * ((int) Math.pow(2, nInputs));
		randomInitialize();
		truthTableChrom();
		this.feasible = false;
		circuit = new Element[nGates + nInputs];
		for(int i=0; i< nInputs;i++)
			circuit[i] = new Element();
		for(int i= nInputs; i<nGates+nInputs;i++)
			circuit[i] = new Gate();
	}

	public String getOptimumExpression() {
		return optimumExpression;
	}

	public int getMaxPenalty() {
		return this.maxPenalty;
	}

	public void setOptimumExpression(String optimumExpression) {
		this.optimumExpression = optimumExpression;
	}

	public Gene[][] getConnectionMatrix() {
		return connectionMatrix;
	}

	public void addGene(int row, int column, int gene) {
		this.connectionMatrix[row][column] = new Gene(gene);
	}

	public Gene getGene(int row, int column) {
		return this.connectionMatrix[row][column];
	}

	public int getFitness() {
		if (feasible) {
			return fitness;
		} else {
			return this.maxPenalty + fitness;
		}
	}

	public int getPenalty() {
		return this.fitness;
	}

	public int[][] getTruthTableChrom() {
		return truthTableChrom;
	}


	public void setTruthTableChrom(int[][] truthTableChrom) {
		this.truthTableChrom = truthTableChrom;
	}

	public Gene[] getChromosome() {
		return chromosome;
	}

	public void setChromosome(Gene[] chromosome) {
		this.chromosome = chromosome;
	}

	
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public int getnInputs() {
		return nInputs;
	}

	public void setnInputs(int numInputs) {
		this.nInputs = numInputs;
	}

	public int getnGates() {
		return nGates;
	}

	public void setnGates(int numGates) {
		this.nGates = numGates;
	}

	public boolean isFeasible() {
		return feasible;
	}

	public void setFeasible(boolean feasible) {
		this.feasible = feasible;
	}

	// Methods
	public void randomInitialize() {

		int rows = this.nInputs + (nGates - 1);

		int columns = nGates;
		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				if (row - this.nInputs < column) {
					addGene(row, column, new Random().nextInt(2));
					this.chromosome[index] = this.getGene(row, column);
					index++;
				}
			}
		}
	}

	public void setChromosomeCell(int index, int newGene) {
		this.chromosome[index].setConnection(newGene);
	}

	public void truthTableChrom() {

		int tableSize = (int) Math.pow(2, this.nInputs);
		this.truthTableChrom = new int[tableSize][this.nInputs + 1];

		for (int column = 0; column < this.nInputs; column++) {
			

			int repeat = this.nInputs - column;
			int pattern = 0;

			repeat = (int) Math.pow(2, repeat) / 2;
			int cont = repeat;

			for (int row = 0; row < tableSize; row++) {
				if (cont == 0) {
					cont = repeat;
					pattern = Math.abs(pattern - 1);
				}
				cont--;

				this.truthTableChrom[row][column] = pattern;
			}
		}
	}

	public String showTruthTable() {

		String output = "";
		int tamanhoTabela = (int) Math.pow(2, this.nInputs);

		for (int row = 0; row < tamanhoTabela; row++) {
			output += "\n";
			for (int column = 0; column < this.nInputs + 1; column++) {
				output = output + " " + this.truthTableChrom[row][column];
			}
		}
		return output;
	}

	public void fitness(int truthTable[][]) {
		int linhas = connectionMatrix.length;
		int colunas = connectionMatrix[0].length;

		for(int i=0; i< nInputs+nGates;i++){
			circuit[i].setComputedValue(-1);
			circuit[i].getOutputs().clear();
		}
		
		int gates[] = new int[this.nGates];
		int index = 0;
		for (int coluna = 0; coluna < colunas; coluna++) {
			for (int linha = 0; linha < linhas; linha++) {
				if (connectionMatrix[linha][coluna] != null) {
					if (circuit[linha].getComputedExpression() == null) {
						if (linha < this.nInputs) {

							circuit[linha].setComputedExpression(this.inputs.get(index).getName());
							index++;
						}

					}
					if (connectionMatrix[linha][coluna].getConnection() != 0) {
						gates[coluna] += 1;

						circuit[linha].addOutput(coluna + this.nInputs);
					}
				}
			}
		}

		mountTruthTable(this.nInputs);
		int sumPenalties = feasible(truthTable);
		int basicCellsCost = 0;
		for (int i = 0; i < this.nGates; i++)
			if (gates[i] != 0)
				basicCellsCost += (1 + gates[i]);

		int penaltyCost = (nGates + numCells()) * sumPenalties;
		this.fitness = penaltyCost + basicCellsCost;
	}

	private void mountTruthTable(int wishedOutput) {
		int rows = this.truthTableChrom.length;
		for (int i = 0; i < rows; i++) {
			List<Integer> inputList = new ArrayList<>();
			for (int j = 0; j < this.nInputs; j++) {
				inputList.add(this.truthTableChrom[i][j]);
			}
			this.truthTableChrom[i][wishedOutput] = computeData(inputList);
		}
	}

	private int computeData(List<Integer> inputs) {
		int i;
		for (i = 0; i < inputs.size(); i++) {
			this.circuit[i].setComputedValue(inputs.get(i));
			sendNext(this.circuit[i]);
		}
		for (i = i; i < this.circuit.length; i++) {

			sendNext(this.circuit[i]);

			if (circuit[i].getOutputs().isEmpty() && circuit[i].getComputedValue() != -1) {
				this.optimumExpression = circuit[i].getComputedExpression();
				return circuit[i].getComputedValue();
			}
		}

		return 0;
	}

	private void sendNext(Element element) {

		if (element.getClass().equals(Gate.class))
			if (!element.getInputs().isEmpty())
				element.setComputedValue(((Gate) element).computeNor());
			else
				return;

		for (int output : element.getOutputs()) {

			this.circuit[output].addInput(element.getComputedValue());
			this.circuit[output].addInputName(element.getComputedExpression());
		}

		element.getInputs().clear();
		element.setSize(0);
		element.getInputName().clear();

	}

	public void printConnectionVector() {
		for (int i = 0; i < this.chromosome.length; i++) {
			System.out.print(" " + this.chromosome[i].getConnection());
		}
	}

	public void printConnectionMatrix() {
		for (int i = 0; i < this.getnInputs() + this.getnGates() - 1; i++) {
			System.out.println();
			for (int j = 0; j < this.getnGates(); j++) {
				String out = this.getConnectionMatrix()[i][j] != null
						? this.getConnectionMatrix()[i][j].getConnection() + "" : "X";
				System.out.print(" " + out);
			}
		}
	}

	// *
	public int feasible(int truthTableIn[][]) {

		int penality = 0;
		int rows = (int) Math.pow(2, this.nInputs);
		int columns = this.nInputs;

		for (int row = 0; row < rows; row++) {
			if (truthTableIn[row][columns] != this.truthTableChrom[row][columns]) {
				penality++;
			}
		}

		this.feasible = penality == 0;
		return penality;
	}

	public int numCells() {
		int ng, rectangle, triangle, numCells;
		ng = nGates;
		rectangle = (ng * this.nInputs);
		triangle = (ng * (ng - 1)) / 2;
		numCells = rectangle + triangle;

		return numCells;
	}


}
