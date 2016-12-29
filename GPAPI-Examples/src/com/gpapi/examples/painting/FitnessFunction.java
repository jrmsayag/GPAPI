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
 * FitnessFunction.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;



public class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private final JPanel genotypeView;
	private final JPanel paintedImagePanelContainer;
	
	private final BufferedImage originalImage;
	
	private final int maxGenomeSize;
	private final int maxShapes;
	
	private final Map<Population,BufferedImage> originalImageCopies = Collections.synchronizedMap(new HashMap<Population,BufferedImage>());
	private final Map<Population,BufferedImage> paintingZones = Collections.synchronizedMap(new HashMap<Population,BufferedImage>());
	private final Map<Population,Graphics> paintBrushes = Collections.synchronizedMap(new HashMap<Population,Graphics>());
	
	
	
	public FitnessFunction(BufferedImage originalImage, int maxGenomeSize, int maxShapes){
		this.originalImage = originalImage;
		
		this.maxGenomeSize = maxGenomeSize;
		this.maxShapes = maxShapes;
		
		JPanel originalImagePanel = new JPanel(){
			private static final long serialVersionUID = -6614867930426727995L;
			@Override
			protected final void paintComponent(Graphics g){
				super.paintComponent(g);
				g.drawImage(originalImage, 0, 0, null);
			}
			@Override
		    public final Dimension getPreferredSize() {
		        return new Dimension(originalImage.getWidth(),originalImage.getHeight());
		    }
		};
		JPanel originalImagePanelContainer = new JPanel();
		originalImagePanelContainer.setLayout(new GridBagLayout());
		originalImagePanelContainer.add(originalImagePanel);
		
		JLabel originalImageLabel = new JLabel("Original image");
		JPanel originalImageLabelContainer = new JPanel();
		originalImageLabelContainer.setLayout(new GridBagLayout());
		originalImageLabelContainer.add(originalImageLabel);
		
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.add(originalImagePanelContainer);
		left.add(originalImageLabelContainer);
		
		
		JPanel paintedImagePanel = new JPanel();
		paintedImagePanelContainer = new JPanel();
		paintedImagePanelContainer.setLayout(new GridBagLayout());
		paintedImagePanelContainer.add(paintedImagePanel);
		
		JLabel paintedImageLabel = new JLabel("Painted image");
		JPanel paintedImageLabelContainer = new JPanel();
		paintedImageLabelContainer.setLayout(new GridBagLayout());
		paintedImageLabelContainer.add(paintedImageLabel);
		
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.add(paintedImagePanelContainer);
		right.add(paintedImageLabelContainer);
		
		
		genotypeView = new JPanel();
		genotypeView.setLayout(new BoxLayout(genotypeView, BoxLayout.X_AXIS));
		genotypeView.add(left);
		genotypeView.add(right);
	}
	
	private final void initFor(Population population){
		BufferedImage originalImageCopy = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics graphicsOriginal = originalImageCopy.createGraphics();
			graphicsOriginal.setColor(Color.BLACK);
			graphicsOriginal.fillRect(0, 0, originalImageCopy.getWidth(), originalImageCopy.getHeight());
			graphicsOriginal.drawImage(originalImage, 0, 0, null);
			graphicsOriginal.dispose();
		originalImageCopies.put(population, originalImageCopy);
		
		BufferedImage paintingZone = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics paintBrush = paintingZone.createGraphics();
			paintBrush.setColor(Color.BLACK);
			paintBrush.fillRect(0, 0, paintingZone.getWidth(), paintingZone.getHeight());
		paintingZones.put(population, paintingZone);
		paintBrushes.put(population, paintBrush);
	}
	

	@Override
	public void applyTo(Population population, int generation) {
		if(!originalImageCopies.containsKey(population))
			initFor(population);
		
		BufferedImage originalImage = originalImageCopies.get(population);
		BufferedImage paintingZone = paintingZones.get(population);
		Graphics paintBrush = paintBrushes.get(population);
		
		for(EvolvedIndividual individual : population){
			if(individual.getRawFitnessType() == null){
				individual.setRawFitnessType(RawFitnessType.Penalty);
				if(individual.getEggCell().getNucleus().getTotalSize() <= maxGenomeSize){
					double error = 0;
					
					int paintedShapes = Wrapper.doPainting(individual, paintBrush);
					int excessShapes = Math.max(0, paintedShapes - maxShapes);
					
					// TODO : Try to allow more than maxShapes at the beginning and decrease progressively.
					// TODO : Try to evolve the fitnessFunction (as a function of RGBError and excessShapes).
					
					error = computeErrorBetween(originalImage, paintingZone);
					error *= Math.max(1, excessShapes);
					
					paintBrush.setColor(Color.BLACK);
					paintBrush.fillRect(0, 0, originalImage.getWidth(), originalImage.getHeight());
					
					individual.setRawFitness(error);
				}
			}
		}
		
		// TODO : Try to modulate with execution cost.
	}
	private static final double computeErrorBetween(BufferedImage original, BufferedImage painted){
		int originalRGB[] = new int[3];
		int paintedRGB[] = new int[3];
		
		WritableRaster originalData = original.getRaster();
		WritableRaster paintedData = painted.getRaster();
		
		double error = 0.0;
		for(int i = 0; i < original.getWidth(); i++){
			for(int j = 0; j < original.getHeight(); j++){
				originalRGB = originalData.getPixel(i, j, originalRGB);
				paintedRGB = paintedData.getPixel(i, j, paintedRGB);
				
				int redError = originalRGB[0] - paintedRGB[0];
				int greenError = originalRGB[1] - paintedRGB[1];
				int blueError = originalRGB[2] - paintedRGB[2];
				
				error += (redError*redError + greenError*greenError + blueError*blueError);
			}
		}
		return error;
	}
	
	
	@Override
	public synchronized JPanel getPhenotypeView(EvolvedIndividual individual) {
		BufferedImage paintingZone = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics paintBrush = paintingZone.createGraphics();
			paintBrush.setColor(Color.BLACK);
			paintBrush.fillRect(0, 0, paintingZone.getWidth(), paintingZone.getHeight());
			Wrapper.doPainting(individual, paintBrush);
			paintBrush.dispose();
		
		JPanel paintedImagePanel = new JPanel(){
			private static final long serialVersionUID = -9109844552515352717L;
			@Override
			protected final void paintComponent(Graphics g){
				super.paintComponent(g);
				g.drawImage(paintingZone, 0, 0, null);
			}
			@Override
		    public final Dimension getPreferredSize() {
		        return new Dimension(paintingZone.getWidth(),paintingZone.getHeight());
		    }
		};
		
		SwingUtilities.invokeLater(() ->
			{
				paintedImagePanelContainer.remove(0);
				paintedImagePanelContainer.add(paintedImagePanel, 0);
			});
		
		return genotypeView;
	}

}
