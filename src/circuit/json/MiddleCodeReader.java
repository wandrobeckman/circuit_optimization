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
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;


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
		file.showOpenDialog(null);
		
		File selectedFile = file.getSelectedFile();
		if(selectedFile!=null){
			CircuitModel circuitOut = new CircuitModel();
			String json = MiddleCodeReader.readJSON(selectedFile);
			Gson gson = new Gson();
			CircuitModel circuit = gson.fromJson(json, CircuitModel.class);
			circuitOut.setInputs(circuit.getInputs());
			
			for(int i =0; i< circuit.getOutputs().size();i++){
				int truthTable[][] = TruthTableCreator.truthTable(circuit.getOutputs().get(i), circuit.getInputs());
				Genetic genetic = new Genetic(2000,truthTable, 10, 10, 50, circuit.getInputs());
				//Genetic genetic = new Genetic(2000,truthTable, 10, 10, 50, 100, circuit.getInputs());
				//circuitOut.addOutput(circuit.getOutputs().get(i).getName(), circuit.getOutputs().get(i).getExpression(), genetic.getSurvivor().getOptimumExpression());
				String name = circuit.getOutputs().get(i).getName()+".m";
				JFileChooser directoryChooser = new JFileChooser();
				directoryChooser.setDialogTitle("Salvar arquivo m");
				directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				directoryChooser.showSaveDialog(null);
				File mFile = new File(directoryChooser.getSelectedFile()+File.separator+name);
				System.out.println(mFile.getAbsolutePath());
				try {
					FileWriter fw = new FileWriter(mFile);
					fw.write(genetic.getOutput());
					fw.flush();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
		}
		
		
	}
}
