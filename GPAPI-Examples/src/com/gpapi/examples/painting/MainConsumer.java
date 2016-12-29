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
 * MainConsumer.java
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
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.gpapi.DataManager;
import com.gpapi.individuals.EvolvedIndividual;



public class MainConsumer {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		EvolvedIndividual individual = DataManager.loadIndividual("painting/results/created/cacahuete.ser");
		
		GeneticOperator geneticOperator = (GeneticOperator) individual.getEggCell().getNucleus().getGeneticOperator();
		
		BufferedImage paintingZone = new BufferedImage(geneticOperator.getWidth(), geneticOperator.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics paintBrush = paintingZone.createGraphics();
			paintBrush.setColor(Color.BLACK);
			paintBrush.fillRect(0, 0, paintingZone.getWidth(), paintingZone.getHeight());
			Wrapper.doPainting(individual, paintBrush);
			paintBrush.dispose();
		
		
		SwingUtilities.invokeLater(() ->
			{
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
				
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(paintedImagePanel);
				mainFrame.setTitle("Painting result");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
	}
}
