package Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import circuit.entities.Input;

public class Chromosome {

	private Gene connectionMatrix[][];
	private Gene chromosome[];
	private Element circuit[];
	private int truthTableChrom[][];
	private int nInputs;
	private int nGates;
	private int fitness;
	private boolean feasible;
	private List<Input> inputs;
	private String optimumExpression;
	

	public Chromosome(List<Input> inputs) {
		this.nInputs = inputs.size();
		this.inputs = inputs;
		this.nGates = (nInputs * nInputs) + 1;
		this.chromosome = new Gene[numCells()];
		this.connectionMatrix = new Gene[(nInputs * nInputs) + nInputs][nGates];
		randomInitialize();
		truthTableChrom();
		this.feasible = false;
	}
	

	public String getOptimumExpression() {
		return optimumExpression;
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
		return feasible?fitness:Integer.MAX_VALUE;
		
	}
	
	public int getPenalty(){
		return this.fitness;
	}

	// *
	public int[][] getTruthTableChrom() {
		return truthTableChrom;
	}

	// *
	public void setTruthTableChrom(int[][] truthTableChrom) {
		this.truthTableChrom = truthTableChrom;
	}

	public Gene[] getChromosome() {
		return chromosome;
	}

	public void setChromosome(Gene[] chromosome) {
		this.chromosome = chromosome;
	}

	// *
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

	public void truthTableChrom() {

		int tableSize = (int) Math.pow(2, this.nInputs);
		this.truthTableChrom = new int[tableSize][this.nInputs + 1];

		for (int column = 0; column < this.nInputs; column++) {
			//System.out.println();

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

		circuit = new Element[linhas + 1];
		circuit[linhas] = new Gate();
		int gates[] = new int[this.nGates];
		int index = 0;
		for (int coluna = 0; coluna < colunas; coluna++) {
			for (int linha = 0; linha < linhas; linha++) {
				if (connectionMatrix[linha][coluna] != null) {
					if (circuit[linha] == null) {
						if (linha < this.nInputs){
							
							circuit[linha] = new Element();
							circuit[linha].setComputedExpression(this.inputs.get(index).getName());
							index++;
						}
							
						else
							circuit[linha] = new Gate();

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
				//System.out.print("entrada: " + this.truthTableChrom[i][j] + " - ");
				inputList.add(this.truthTableChrom[i][j]);
			}
			this.truthTableChrom[i][wishedOutput] = computeData(inputList);
			//System.out.println("SAIDA: " + this.truthTableChrom[i][wishedOutput] + " ");
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

		for (int output : element.getOutputs()){
			this.circuit[output].addInput(element.getComputedValue());
			this.circuit[output].addInputName(element.getComputedExpression());
		}
			
		// element.getOutputs().clear();
		element.getInputs().clear();
		element.setSize(0);
		element.getInputName().clear();

	}

	// *
	public int feasible(int truthTableIn[][]) {

		int penality = 0;
		int rows = (int) Math.pow(2, this.nInputs);
		int columns = this.nInputs;

		for (int row = 0; row < rows; row++) {
			//for (int column = 0; column < columns; column++) {
				//if (column == columns - 1) {
					if (truthTableIn[row][columns] != this.truthTableChrom[row][columns]) {
						penality++;
					}
				//}
			//}
		}

		if (penality == 0) {
			this.feasible = true;
		}
		return penality;
	}

	// *retorna numero de celulas do cromossomo
	public int numCells() {
		int ng, rectangle, triangle, numCells;
		ng = nGates;
		rectangle = (ng * this.nInputs);
		triangle = (ng * (ng - 1)) / 2;
		numCells = rectangle + triangle;

		return numCells;
	}

	// metodo para redefinicao da matriz
	public void setMatrix() {

		int rows = this.nInputs * this.nInputs;
		rows += this.nInputs;
		int columns = nGates;
		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				if (row - this.nInputs < column) {
					addGene(row, column, this.chromosome[index].getConnection());
					index++;
				}
			}
		}
	}

	public static void main(String[] args) {

		/*
		 * Chromosome teste = new Chromosome(2);
		 * 
		 * teste.addGene(0, 0, 1); teste.addGene(0, 1, 1); teste.addGene(0, 2,
		 * 0); teste.addGene(0, 3, 0); teste.addGene(0, 4, 0);
		 * 
		 * teste.addGene(1, 0, 1); teste.addGene(1, 1, 0); teste.addGene(1, 2,
		 * 1); teste.addGene(1, 3, 0); teste.addGene(1, 4, 0);
		 * 
		 * teste.addGene(2, 1, 1); teste.addGene(2, 2, 1); teste.addGene(2, 3,
		 * 1); teste.addGene(2, 4, 1);
		 * 
		 * teste.addGene(3, 2, 0); teste.addGene(3, 3, 1); teste.addGene(3, 4,
		 * 0);
		 * 
		 * teste.addGene(4, 3, 0); teste.addGene(4, 4, 1);
		 * 
		 * teste.addGene(5, 4, 0);
		 * 
		 * int truthTable[][] = { {0,0,1}, {0,1,1}, {1,0,1}, {1,1,1} };
		 * teste.fitness(truthTable);
		 * System.out.println(teste.showTruthTable());
		 * System.out.println(teste.numCells()); System.out.println("Fitness: "+
		 * teste.getFitness());
		 * 
		 */

		Random gerador = new Random();
		int numero = gerador.nextInt((10 - 2) + 1) + 2;
		System.out.println(numero);

	}

}
