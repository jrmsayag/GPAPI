/* ===============================================================
 * GPAPI : A Genetic-Programming library for the Java(tm) platform
 * ===============================================================
 *
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Project Info:  https://github.com/jrmsayag/gpapi
 * 
 * This file is part of GPAPI.
 *
 * This library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see http://www.gnu.org/licenses/.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------
 * Main.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.gpapi.DataManager;
import com.gpapi.GenerationStatistics;
import com.gpapi.algorithm.GlobalFitnessAlgorithm;
import com.gpapi.algorithm.GlobalFitnessAlgorithmBuilder;
import com.gpapi.algorithm.breedingoperators.ParentSelection;
import com.gpapi.algorithm.generalpurposeoperators.KillOldest;
import com.gpapi.algorithm.naturalselectionstrategies.FitnessProportionate;
import com.gpapi.examples.painting.GeneticOperator.Shapes;
import com.gpapi.examples.painting.nodes.types.Void;
import com.gpapi.individuals.EvolvedIndividualBuilder;



public class Main {

	public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException, ExecutionException {
		
		// Original image
		
		BufferedImage rawImage = ImageIO.read(new File("painting/source/created/cacahuete.jpg"));
		BufferedImage image = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.createGraphics();
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
			graphics.drawImage(rawImage, 0, 0, null);
			graphics.dispose();
		
		
		// Parameters
		
		int nGenerations = 25000;
		
		int nPopulations = 1;
		int populationsSize = 500;
		
		boolean rankBased = true;
		double adjustmentConstant = 1.0;
		int newIndivPerGeneration = 25;
		double crossoverBias = -1.0;
		int maxAge = 10;
		
		int maxGenomeSize = 5000;
		int maxShapes = 50;
		
		int crossoverPoints = 1;
		double pointMutationFrequency = 0.0015;
		
		Shapes potentialShapes = Shapes.Polygons;
		int maxPolygonVertices = 6;
		int initPolygonVertices = 3;
		int alpha = 64;
		int colorIncrement = 30;
		int vertexIncrement = 2 * image.getWidth() / maxShapes;
		
		
		// Launching !
		GeneticOperator geneticOperator = new GeneticOperator(
				potentialShapes, 
				maxPolygonVertices, 
				initPolygonVertices, 
				alpha,
				colorIncrement,
				vertexIncrement, 
				image.getWidth(), 
				image.getHeight(), 
				crossoverPoints,
				pointMutationFrequency);
		GlobalFitnessAlgorithm algorithm = GlobalFitnessAlgorithmBuilder.create(
					new FitnessFunction(image, maxGenomeSize, maxShapes), 
					nGenerations, 
					EvolvedIndividualBuilder.create(geneticOperator).setRpbsTypes(Arrays.asList(new Void())).build(), 
					populationsSize, 
					nPopulations)
				.setBreedingOperator(
						new ParentSelection(
								new FitnessProportionate(
										rankBased, 
										adjustmentConstant),
								newIndivPerGeneration, 
								crossoverBias, 
								-1))
				.setGeneralPurposeOperator(new KillOldest(maxAge))
				.setNThreads(Math.min(6, nPopulations))
				.build();
		algorithm.getView().setMaxGenerationsDisplayed(1000);
		
		SwingUtilities.invokeAndWait(() ->
			{
				JFrame mainFrame = new JFrame();
				mainFrame.setTitle("Painting");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.setContentPane(algorithm.getView().getPanel());
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		List<GenerationStatistics> results = executor.submit(algorithm).get();
			
		
		// Exporting !
		
		GenerationStatistics lastGeneration = results.get(results.size()-1);
		DataManager.saveIndividual(
				lastGeneration.getBestIndividualFor(lastGeneration.getBestPopulation()), 
				"painting/results/created/cacahuete.ser");
	}
}
