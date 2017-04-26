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
	private List<Input> inputs;
	private int truthTableInput[][];
	private int numPopulation;
	private int percentMutation;
	private int percentCrossover;
	private int percentMutGene;
	private Chromosome survivor;
	private String output;
	private List<Chromosome> feasible;
	private List<Chromosome> notFeasible;

	public Chromosome getSurvivor() {
		return survivor;
	}

	public void setSurvivor(Chromosome survivor) {
		this.survivor = survivor;
	}

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
			crossover();
			sortBestChromosome();

			for (int i = numPopulation; i < this.population.size(); i++)
				this.population.remove(i);
			
		}

		for (int i = (numPopulation - 1); i >= 0; i--)
			System.out.println("Chromossome[" + i + "]: " + this.population.get(i).getPenalty() + " Factivel: "
					+ this.population.get(i).isFeasible() + " Fitness " + this.population.get(i).getFitness());

		System.out.println();
		System.out.println("Expressão ótima: " + this.population.get(0).getOptimumExpression());
		System.out.println();
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
		//notFeasible = new ArrayList<Chromosome>();
		//feasible = new ArrayList<Chromosome>();
		population = firstPopulation(numPopulation, inputs);
		survivor = population.get(0);
		int gera = 0;
		output = "";
		output+= "fitness = [";
		while(!this.population.get(numPopulation-1).isFeasible()){
			crossover();
			sortBestChromosome();
			gera++;			
						System.out.println("Evoluindo... ["+ gera+"]");
						System.out.println();
						System.out.println("Melhor dessa rodada: "+ this.population.get(0).getFitness() +" factível "+ this.population.get(0).isFeasible());
						System.out.println();
						System.out.println("Pior dessa rodada "+ this.population.get(numPopulation-1).getFitness() +" factível "+ this.population.get(numPopulation-1).isFeasible());
			
			output = output+this.population.get(0).getFitness()+","+this.population.get(numPopulation-1).getFitness() +";\n";
			
		}
		output+="\n";
		int melhorFitness = this.population.get(0).getFitness();
		while(melhorFitness<this.population.get(numPopulation/2).getFitness()){
			
			gera++;
			crossover();
			sortBestChromosome();			

			if (this.population.get(0).getFitness() < survivor.getFitness()) {
				survivor = this.population.get(0);

			}
			System.out.println("Evoluindo... ["+ gera+"]");
			System.out.println();
			System.out.println("Melhor dessa rodada: "+ this.population.get(0).getFitness() +" factível "+ this.population.get(0).isFeasible());
			System.out.println();
			System.out.println("Pior dessa rodada "+ this.population.get(numPopulation-1).getFitness() +" factível "+ this.population.get(numPopulation-1).isFeasible());

			output = output+this.population.get(0).getFitness()+","+this.population.get(numPopulation-1).getFitness() +";\n";
			
		}
		output+= "]";
		for (int i = numPopulation - 1; i > -1; i--) {
			System.out.println("Chromossome[" + i + "]: " + this.population.get(i).getPenalty() + " Factivel: "
					+ this.population.get(i).isFeasible() + " Fitness " + this.population.get(i).getFitness());
		}
        
		System.out.println();
		System.out.println("Expressão ótima: " + this.survivor.getOptimumExpression());
		System.out.println();
		System.out.println("Matriz de conexão");
		survivor.printConnectionMatrix();
		System.out.println();
		System.out.println("\nTabela verdade");
		System.out.println(this.survivor.showTruthTable());
		System.out.println("\nGerações: " + gera);
		System.out.println("Fitness do cromossomo: " + this.survivor.getFitness());
	}
	
	public String getOutput(){
		return this.output;
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
	
	
	int[] generateTemplate(int nCells, int nInputs){
		int cutPoints = new Random().nextInt((int)Math.pow(2, nInputs)-nInputs)+nInputs;
		int maxSequence = nCells/cutPoints;
		int template[] = new int[nCells];
		int sequence = new Random().nextInt(maxSequence-nInputs)+nInputs;
		int gene = new Random().nextInt(2);
		for(int i=0;i<nCells;i++){
			template[i] = gene;
			sequence--;
			if(sequence==0){
			 sequence = new Random().nextInt(maxSequence-nInputs)+nInputs;
			 gene = Math.abs(gene-1);
			}
		}
		return template;
		
	}


	public void sortBestChromosome() {
		Collections.sort(this.population, new Comparator<Chromosome>() {
			@Override
			public int compare(Chromosome lhs, Chromosome rhs) {
				return lhs.getFitness() < rhs.getFitness() ? -1 : lhs.getFitness() > rhs.getFitness() ? 1 : 0;

			}
		});

	}
	
	private void sorting(List<Chromosome> list){
		for(int i=0;i<population.size();i++){
			for(int j=0; j<population.size()-1;j++){
				if(population.get(j).getFitness()>population.get(j+1).getFitness()){
					Chromosome temp = population.get(j);
					population.set(j, population.get(j+1));
					population.set(j+1, temp);
				}
			}
		}
	}
	
	

	public void crossover() {

		
		int qtyCrossover = Math.round(this.numPopulation * this.percentCrossover / 100);
		int chromosomeSize = this.population.get(0).numCells();
		int sort1, sort2;
		
			
		int worstParentAddrs = -1;
		int worstParentCost = -1;
		
		if (qtyCrossover % 2 == 1)
			qtyCrossover++;
		
		
		for (int i = 0; i < qtyCrossover; i++) {
			Chromosome child1 = new Chromosome(this.inputs);
			int pontoCorte = new Random().nextInt(chromosomeSize - 2) + 1;

			do {
				sort1 = new Random().nextInt(this.numPopulation);
				sort2 = new Random().nextInt(this.numPopulation);
			} while (sort1 == sort2);
			if(this.population.get(sort1).getFitness()> this.population.get(sort2).getFitness()){
				worstParentAddrs = sort1;
				worstParentCost = this.population.get(sort1).getFitness();
			}else{
				worstParentAddrs = sort2;
				worstParentCost = this.population.get(sort2).getFitness();
			}
			for (int j = 0; j <= pontoCorte; j++) {
				int replacement = this.population.get(sort1).getChromosome()[j].getConnection()==0?0:1;
				
				child1.setChromosomeCell(j, replacement);
			}

			for (int j = pontoCorte + 1; j < chromosomeSize; j++) {
				int replacement = this.population.get(sort2).getChromosome()[j].getConnection()==0?0:1;
				child1.setChromosomeCell(j, replacement);
			}
			mutation(child1);
			child1.fitness(this.truthTableInput);
			int childFitness = child1.getFitness();
			if(childFitness<worstParentCost)
				this.population.set(worstParentAddrs, child1);

		}
		
	}
	
	public void templateCrossover(){

		int template[] = generateTemplate(this.population.get(0).getChromosome().length, this.inputs.size());
		int qtyCrossover = Math.round(this.numPopulation * this.percentCrossover / 100);
		int chromosomeSize = this.population.get(0).numCells();
		int sort1 = -1, sort2=-1;
	
		int worstParentAddrs = -1;
		int worstParentCost = -1;
		int sort1Location = -1;
		int sort2Location = -1;
		
		if (qtyCrossover % 2 == 1)
			qtyCrossover++;		
		
		for (int i = 0; i < qtyCrossover; i++) {
			boolean feasible1Chosen = false;
			boolean feasible2Chosen = false;
			boolean notfeasible1Chosen = false;
			boolean notfeasible2Chosen = false;
			Chromosome child1 = new Chromosome(this.inputs);
			if(this.feasible.size()>0){
				sort1 = new Random().nextInt(this.feasible.size());
				feasible1Chosen = true;
				sort1Location = this.population.indexOf(feasible.get(sort1));
			}else{
				sort1 = new Random().nextInt(this.notFeasible.size());
				notfeasible1Chosen = true;
				sort1Location = this.population.indexOf(notFeasible.get(sort1));
			}
			if(this.notFeasible.size()>0){
				sort2 = new Random().nextInt(this.notFeasible.size());
				notfeasible2Chosen = true;
				sort2Location = this.population.indexOf(notFeasible.get(sort2));
			}else{
				feasible2Chosen = true;
				sort2 = new Random().nextInt(this.feasible.size());
				sort2Location = this.population.indexOf(feasible.get(sort2));
			}
			
			if(this.population.get(sort1Location).getFitness()> this.population.get(sort2Location).getFitness()){
				worstParentAddrs = sort1Location;
					
				worstParentCost = this.population.get(sort1Location).getFitness();
			}else{
				worstParentAddrs = sort2Location;
				worstParentCost = this.population.get(sort2Location).getFitness();
			}
			for (int j = 0; j < chromosomeSize; j++) {
				int replacement = template[j]==1? this.population.get(sort1Location).getChromosome()[j].getConnection():
					this.population.get(sort2Location).getChromosome()[j].getConnection();
				
				child1.setChromosomeCell(j, replacement);
			}
			mutation(child1);
			child1.fitness(this.truthTableInput);
			int childFitness = child1.getFitness();
			if(childFitness<worstParentCost){
				if(sort1Location==worstParentAddrs){
					if(feasible1Chosen){
						this.feasible.remove(sort1);
						this.feasible.add(child1);
					}
						
					else{
						this.notFeasible.remove(sort1);
						this.notFeasible.add(child1);
					}
						
				}else{
					if(feasible2Chosen){
						this.feasible.remove(sort2);
						this.feasible.add(child1);
					}
						
					else{
						this.notFeasible.remove(sort2);
						this.notFeasible.add(child1);
					}
						
				}
				this.population.set(worstParentAddrs, child1);
				
			}

		}
	}
	public void mutation(Chromosome chromosome) {

		int qtyGene = (int) Math.ceil((this.population.get(0).numCells() * this.percentMutGene) / 100.00);
		int position = 0;
		String mutationPoints = "";
			

			for (int j = 0; j < qtyGene; j++) {
				position = new Random().nextInt(chromosome.getChromosome().length);
				mutationPoints = mutationPoints + " - " + position;
				chromosome.setChromosomeCell(position, Math.abs(chromosome.getChromosome()[position].getConnection() - 1));				
			}
	}

}
