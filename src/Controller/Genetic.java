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
	//private int percentTournament;
	private int percentMutation;
	private List<Input> inputs;
	private int percentCrossover;
	private int percentMutGene;

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
			System.out.println("Mutou: "+generation);
			List<Chromosome> children = crossover();
			System.out.println("Cruzou: "+generation);
			this.population.addAll(children);
			
			//this.numPopulation = population.size();
			sortBestChromosome();
			System.out.println("Ordenou: "+generation);
			for(int i= numPopulation; i<this.population.size();i++){
				this.population.remove(i);
			}
			System.out.println("Removeu: "+generation);
		}

		//System.out.println();

		for (int i = 0; i < numPopulation; i++) {
			System.out.println("Chromossome[" + i + "]: " + this.population.get(i).getPenalty() + " Factivel: "
					+ this.population.get(i).isFeasible() + " Fitness "+ this.population.get(i).getFitness());
		}
		
		System.out.println("Expressão ótima: "+ this.population.get(0).getOptimumExpression());
		System.out.println("Matriz de conexão");
		for(int i=0; i<this.population.get(0).getnInputs()+this.population.get(0).getnGates()-1;i++){
			System.out.println();
			for(int j =0; j< this.population.get(0).getnGates(); j++){
				String out = this.population.get(0).getConnectionMatrix()[i][j]!=null?this.population.get(0).getConnectionMatrix()[i][j].getConnection()+"":"X";
				System.out.print(" "+ out);
			}
		}
		System.out.println("\nTabela verdade");		
		System.out.println(this.population.get(0).showTruthTable());

		System.out.println("Expressão ótima: "+ this.population.get(0).getOptimumExpression());
		
		this.population.get(0).fitness(this.truthTableInput);
		System.out.println("\nMatriz de conexão");
		for(int i=0; i<this.population.get(0).getnInputs()+this.population.get(0).getnGates()-1;i++){
			System.out.println();
			for(int j =0; j< this.population.get(0).getnGates(); j++){
				String out = this.population.get(0).getConnectionMatrix()[i][j]!=null?this.population.get(0).getConnectionMatrix()[i][j].getConnection()+"":"X";
				System.out.print(" "+ out);
			}
		}
		System.out.println("\nTabela verdade");		
		System.out.println(this.population.get(0).showTruthTable());
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

	public void sortBestChromosome() {
		 Collections.sort(this.population, new Comparator<Chromosome>() {
		        @Override
		        public int compare(Chromosome lhs, Chromosome rhs) {
		        	    return lhs.getFitness() < rhs.getFitness() ? -1 : lhs.getFitness() > rhs.getFitness() ? 1 : 0;
	                
		        }
		        });
		
	}

	/*
	// *tournament selection
	public Chromosome[] tournament() {

		ArrayList<Chromosome> selects = new ArrayList<>();
		int qtySelect = Math.round(this.numPopulation * this.percentTournament / 100);
		int middle = Math.round(qtySelect / 2);
		String chosenChromosome = "";
		int selected = -1;
		for (int i = 0; i < qtySelect; i++) {
			selected = new Random().nextInt(this.numPopulation);
			if (!chosenChromosome.contains(";" + selected + ";")) {

				selects.add(this.population.get(selected));
				chosenChromosome += ";" + selected + ";";
				// *verify if it is correct

			} else {
				i--;
			}
		}

		Chromosome best1 = selects.get(0);

		for (int i = 1; i < middle; i++) {
			if (best1.getFitness() > selects.get(i).getFitness()) {
				best1 = selects.get(i);
			}
		}

		Chromosome best2 = selects.get(middle);

		for (int i = middle + 1; i < qtySelect; i++) {
			if (best2.getFitness() > selects.get(i).getFitness()) {
				best2 = selects.get(i);
			}
		}

		Chromosome[] vectParents = { best1, best2 };
		return vectParents;
	}
	*/

	public ArrayList<Chromosome> crossover() {

		ArrayList<Chromosome> children = new ArrayList<>();
		int qtyCrossover = Math.round(this.numPopulation * this.percentCrossover / 100);
		int chromosomeSize = this.population.get(0).numCells();
		int sort1, sort2;
		int pontoCorte = new Random().nextInt(chromosomeSize - 2) + 1;
		//System.out.println("Ponto de corte:" + pontoCorte);
		Chromosome child1 = new Chromosome(this.inputs);
		Chromosome child2 = new Chromosome(this.inputs);		
		Gene[] vectAux = new Gene[child1.numCells()];
		Gene[] vectAux2 = new Gene[child2.numCells()];

		if (qtyCrossover % 2 == 1) {
			qtyCrossover++;
			//System.out.println("Cruzamento com " + qtyCrossover + " indivíduos.");
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

	// *
	public void mutation() {

		int qtyMutation = Math.round((this.numPopulation * this.percentMutation) / 100);
		int qtyGene = Math.round((this.population.get(0).numCells() * this.percentMutGene) / 100);
		int position = 0;
		int chosen;
		String mutationPoints = "";

		for (int i = 0; i < qtyMutation; i++) {
			chosen = new Random().nextInt(this.numPopulation);

			for (int j = 0; j < qtyGene; j++) {
				position = new Random().nextInt(this.population.get(chosen).getChromosome().length);
				mutationPoints = mutationPoints + " - " + position;
				Gene vector[] =this.population.get(chosen).getChromosome();  
				vector[position].setConnection(Math.abs(this.population.get(chosen).getChromosome()[j].getConnection() - 1));
				this.population.get(chosen).setChromosome(vector);
			}

			//this.population.get(chosen).setMatrix();
			this.population.get(chosen).fitness(this.truthTableInput);

		}
		
	}

	public static void main(String args[]) {

		int truthTable[][] = { { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 }, { 1, 1, 1 } };
		//int numPopulation, int numInputs, int target[][], int mutationPercentual, int mutGenePercentual,
		//int crossoverPercentual, int numGenerations
		//Genetic genetic = new Genetic(1000, 2, truthTable, 30, 10, 50, 3000);

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
