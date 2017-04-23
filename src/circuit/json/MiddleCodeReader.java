/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package circuit.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import Controller.Genetic;
import circuit.entities.CircuitModel;
import circuit.entities.Input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author pedro
 */
public class MiddleCodeReader {

	public static String readJSON(File file) {
		String json = "";

		try {
			Scanner reader = new Scanner(new FileReader(file));

			while (reader.hasNext()) {
				String line = reader.nextLine();
				json += line;
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		return json;
	}

	public static void main(String args[]) {
		JFileChooser file = new JFileChooser();
		//file.showOpenDialog(null);
		
		File selectedFile = new File("/home/beckman/Documentos/maioria.mdl");
		System.out.println(selectedFile.getPath());
		//File selectedFile = "/root/maioria.mdl";
		// int truthTable[][] = { { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 }, { 1, 1,
		// 1 } };
		// List<Input> inputs = new ArrayList<>();
		// inputs.add(new Input("a"));
		// inputs.add(new Input("b"));

		String json = MiddleCodeReader.readJSON(selectedFile);
		Gson gson = new Gson();
		CircuitModel circuit = gson.fromJson(json, CircuitModel.class);

		int truthTable[][] = TruthTableCreator.truthTable(circuit.getOutputs().get(0), circuit.getInputs());
		/*
		 * for(int row = 0; row< truthTable.length;row++){ System.out.println();
		 * for(int column = 0; column<truthTable[0].length;column++){
		 * System.out.print(" "+ truthTable[row][column]); } }
		 */
		System.out.println("truthtble pronta");
		
		//Genetic genetic = new Genetic(250	, truthTable, 50, 5, 50, circuit.getInputs());
		Genetic genetic = new Genetic(20,truthTable, 1, 1, 10, circuit.getInputs());
		//Genetic genetic = new Genetic(5000, truthTable, 5, 1, 80, 20,circuit.getInputs());
		
		
	}
}
