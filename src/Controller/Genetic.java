package Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import Entities.*;
import circuit.entities.Input;
import circuit.entities.Output;

public class Genetic {

	private List<Chromosome> population;
	private int truthTableInput[][];
	private int numPopulation;
	private int percentMutation;
	private List<Input> inputs;
	private int percentCrossover;
	private int percentMutGene;
	private Chromosome survivor;

	public Genetic(int numPopulation, int target[][], int mutationPercentual, int mutGenePercentual,
			int crossoverPercentual, int numGenerations, List<Input> inputs) {
		this.inputs = inputs;
		this.truthTableInput = target;
		this.numPopulation = numPopulation;
		this.percentMutation = mutationPercentual;
		this.percentMutGene = mutGenePercentual;
		this.percentCrossover = crossoverPercentual;
		population = firstPopulation(numPopulation, inputs);

		for (int generation = 0; generation < numGenerations; generation++) {

			mutation();

			List<Chromosome> children = crossover();

			this.population.addAll(children);

			// this.numPopulation = population.size();
			sortBestChromosome();

			for (int i = numPopulation; i < this.population.size(); i++) {
				this.population.remove(i);
			}

		}

		// System.out.println();

		for (int i = (numPopulation-1); i >= 0 ; i--) {
			System.out.println("Chromossome[" + i + "]: " + this.population.get(i).getPenalty() + " Factivel: "
					+ this.population.get(i).isFeasible() + " Fitness " + this.population.get(i).getFitness());
		}

		System.out.println("Expressão ótima: " + this.population.get(0).getOptimumExpression());
		System.out.println("Matriz de conexão");
		for (int i = 0; i < this.population.get(0).getnInputs() + this.population.get(0).getnGates() - 1; i++) {
			System.out.println();
			for (int j = 0; j < this.population.get(0).getnGates(); j++) {
				String out = this.population.get(0).getConnectionMatrix()[i][j] != null
						? this.population.get(0).getConnectionMatrix()[i][j].getConnection() + "" : "X";
				System.out.print(" " + out);
			}
		}
		System.out.println("\nTabela verdade");
		System.out.println(this.population.get(0).showTruthTable());
	}
	
	public Genetic(int numPopulation, int target[][], int mutationPercentual, int mutGenePercentual,
			int crossoverPercentual, List<Input> inputs) {
		this.inputs = inputs;
		this.truthTableInput = target;
		this.numPopulation = numPopulation;
		this.percentMutation = mutationPercentual;
		this.percentMutGene = mutGenePercentual;
		this.percentCrossover = crossoverPercentual;
		population = firstPopulation(numPopulation, inputs);
		survivor = copyChromssome(population.get(0));
		int generation = 0;
		int stopCondition = 50;
		boolean end = false;
		for (generation = 0; !end ; generation++, stopCondition--) {

			mutation();
			

			List<Chromosome> children = crossover();

			this.population.addAll(children);

			// this.numPopulation = population.size();
			sortBestChromosome();

			for (int i = numPopulation; i < this.population.size(); i++) {
				this.population.remove(i);
			}
			
			if(this.population.get(0).getFitness()< survivor.getFitness()){
				survivor = copyChromssome(this.population.get(0));
				stopCondition = 50;
				
			}
				
			end = stopCondition==0;

		}

		// System.out.println();

		for (int i = numPopulation-1; i > -1; i--) {
			System.out.println("Chromossome[" + i + "]: " + this.population.get(i).getPenalty() + " Factivel: "
					+ this.population.get(i).isFeasible() + " Fitness " + this.population.get(i).getFitness());
		}

		System.out.println("Expressão ótima: " + this.survivor.getOptimumExpression());
		System.out.println("Matriz de conexão");
		for (int i = 0; i < this.survivor.getnInputs() + survivor.getnGates() - 1; i++) {
			System.out.println();
			for (int j = 0; j < this.survivor.getnGates(); j++) {
				String out = this.survivor.getConnectionMatrix()[i][j] != null
						? this.survivor.getConnectionMatrix()[i][j].getConnection() + "" : "X";
				System.out.print(" " + out);
			}
		}
		System.out.println("\nTabela verdade");
		System.out.println(this.survivor.showTruthTable());
		System.out.println("\n Gerações"+ generation);
		System.out.println("Fitness do cromossomo: "+ this.survivor.getPenalty());
	}

	public int getNumPopulation() {
		return numPopulation;
	}

	public void setNumPopulation(int numPopulation) {
		this.numPopulation = numPopulation;
	}

	public int getPercentMutation() {
		return percentMutation;
	}

	public void setPercentMutation(int percentMutation) {
		this.percentMutation = percentMutation;
	}

	public int getPercentCrossover() {
		return percentCrossover;
	}

	public void setPercentCrossover(int percentCrossover) {
		this.percentCrossover = percentCrossover;
	}

	public int getPercentMutGene() {
		return percentMutGene;
	}

	public void setPercentMutGene(int percentMutGene) {
		this.percentMutGene = percentMutGene;
	}

	private ArrayList<Chromosome> firstPopulation(int numPopulation, List<Input> inputs) {

		ArrayList<Chromosome> firstPopulation = new ArrayList<>();

		for (int i = 0; i < numPopulation; i++) {
			Chromosome chromosome = new Chromosome(inputs);
			chromosome.randomInitialize();
			chromosome.fitness(this.truthTableInput);
			firstPopulation.add(chromosome);
		}
		return firstPopulation;
	}

	public Chromosome copyChromssome(Chromosome chromosome){
		Chromosome copyCat = new Chromosome(this.inputs);
		
		int nInputs = this.inputs.size();
		int rows =  nInputs* nInputs;
		int nGates = rows+1;
		rows += nInputs;
		int columns = nGates;
		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				if (row - nInputs < column) {
						
						copyCat.getConnectionMatrix()[row][column].setConnection(chromosome.getConnectionMatrix()[row][column].getConnection());
						copyCat.getChromosome()[index].setConnection(chromosome.getChromosome()[index].getConnection());
						index++;
					}
				}
			}
		copyCat.setFitness(chromosome.getPenalty());
		copyCat.setOptimumExpression(chromosome.getOptimumExpression());
		copyCat.setFeasible(chromosome.isFeasible());
		copyCat.setTruthTableChrom(chromosome.getTruthTableChrom());
		return copyCat;
	}

	public void sortBestChromosome() {
		Collections.sort(this.population, new Comparator<Chromosome>() {
			@Override
			public int compare(Chromosome lhs, Chromosome rhs) {
				return lhs.getFitness() < rhs.getFitness() ? -1 : lhs.getFitness() > rhs.getFitness() ? 1 : 0;

			}
		});

	}

	public ArrayList<Chromosome> crossover() {

		ArrayList<Chromosome> children = new ArrayList<>();
		int qtyCrossover = Math.round(this.numPopulation * this.percentCrossover / 100);
		int chromosomeSize = this.population.get(0).numCells();
		int sort1, sort2;
		int pontoCorte = new Random().nextInt(chromosomeSize - 2) + 1;
		// System.out.println("Ponto de corte:" + pontoCorte);
		Chromosome child1 = new Chromosome(this.inputs);
		Chromosome child2 = new Chromosome(this.inputs);
		Gene[] vectAux = new Gene[child1.numCells()];
		Gene[] vectAux2 = new Gene[child2.numCells()];

		if (qtyCrossover % 2 == 1) {
			qtyCrossover++;
			// System.out.println("Cruzamento com " + qtyCrossover + "
			// indivíduos.");
		}

		for (int i = 0; i < qtyCrossover / 2; i++) {

			do {
				sort1 = new Random().nextInt(this.numPopulation);
				sort2 = new Random().nextInt(this.numPopulation);
			} while (sort1 == sort2);

			for (int j = 0; j <= pontoCorte; j++) {
				vectAux[j] = new Gene(this.population.get(sort1).getChromosome()[j].getConnection());
				vectAux2[j] = new Gene(this.population.get(sort2).getChromosome()[j].getConnection());
			}

			for (int j = pontoCorte + 1; j < chromosomeSize; j++) {
				vectAux[j] = new Gene(this.population.get(sort2).getChromosome()[j].getConnection());
				vectAux2[j] = new Gene(this.population.get(sort1).getChromosome()[j].getConnection());
			}

			child1.setChromosome(vectAux);
			child2.setChromosome(vectAux2);
			child1.setMatrix();
			child2.setMatrix();
			child1.fitness(this.truthTableInput);
			child2.fitness(this.truthTableInput);
			children.add(child1);
			children.add(child2);

		}
		return children;
	}

	public void mutation() {

		int qtyMutation = Math.round((this.numPopulation * this.percentMutation) / 100);
		int qtyGene = (int) Math.ceil((this.population.get(0).numCells() * this.percentMutGene) / 100.00);
		int position = 0;
		int chosen;
		String mutationPoints = "";

		for (int i = 0; i < qtyMutation; i++) {
			chosen = new Random().nextInt(this.numPopulation);

			for (int j = 0; j < qtyGene; j++) {
				position = new Random().nextInt(this.population.get(chosen).getChromosome().length);
				mutationPoints = mutationPoints + " - " + position;
				Gene vector[] = this.population.get(chosen).getChromosome();
				vector[position]
						.setConnection(Math.abs(this.population.get(chosen).getChromosome()[j].getConnection() - 1));
				this.population.get(chosen).setChromosome(vector);
			}

			// this.population.get(chosen).setMatrix();
			this.population.get(chosen).fitness(this.truthTableInput);

		}

	}

	public static void main(String args[]) {

		int truthTable[][] = { { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 }, { 1, 1, 1 } };
		// int numPopulation, int numInputs, int target[][], int
		// mutationPercentual, int mutGenePercentual,
		// int crossoverPercentual, int numGenerations
		// Genetic genetic = new Genetic(1000, 2, truthTable, 30, 10, 50, 3000);

		/*
		 * Chromosome individuos[] = new Chromosome[2]; individuos[0] = new
		 * Chromosome(2); individuos[1] = new Chromosome(2);
		 * System.out.println("Individuo 1"); for(int i=0;
		 * i<individuos[0].getChromosome().length;i++) System.out.print(" "+
		 * individuos[0].getChromosome()[i].getConnection());
		 * System.out.println("\nIndividuo 2"); for(int i=0;
		 * i<individuos[1].getChromosome().length;i++) System.out.print(" "+
		 * individuos[1].getChromosome()[i].getConnection());
		 * 
		 * 
		 * Chromosome filho = genetic.crossover(individuos);
		 * System.out.println(); for(int i=0;
		 * i<filho.getChromosome().length;i++) System.out.print(" "+
		 * filho.getChromosome()[i].getConnection());
		 */
	}

}
