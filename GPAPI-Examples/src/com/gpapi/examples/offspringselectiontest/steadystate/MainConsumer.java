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


package com.gpapi.examples.offspringselectiontest.steadystate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.gpapi.DataManager;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public class MainConsumer {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InvocationTargetException, InterruptedException {
		EvolvedIndividual individual = DataManager.loadIndividual("symbolicRegression/results/evolvedFunction.ser");
		
		double[][] plotPoints = new double[200][3];
		for(int i = 0; i < 200; i++){
			double currentX = (i / 50.0) - 2.0;
			plotPoints[i][0] = currentX;
			plotPoints[i][1] = FitnessFunction.targetFunction(currentX);
			plotPoints[i][2] = ((RealValue) individual.executeUnconditionally(0, RealValue.create(currentX))).getValue();
		}
		
		
		SwingUtilities.invokeAndWait(() ->
			{
				XYSeries targetFuncSeries = new XYSeries("Target Function");
				XYSeries bestIndivSeries = new XYSeries("Best Individual's phenotype");
				
				XYSeriesCollection phenotypeSeriesCollection = new XYSeriesCollection();
				phenotypeSeriesCollection.addSeries(targetFuncSeries);
				phenotypeSeriesCollection.addSeries(bestIndivSeries);
				
				JFreeChart phenotypeChart = ChartFactory.createXYLineChart(
						null, 
						"x", 
						"f(x)", 
						phenotypeSeriesCollection, 
						PlotOrientation.VERTICAL, 
						true, 
						false, 
						false);
				ChartPanel phenotypePanel = new ChartPanel(phenotypeChart);
				
				
				phenotypeChart.setNotify(false);
				targetFuncSeries.clear();
				bestIndivSeries.clear();
				for(double[] point : plotPoints){
					targetFuncSeries.add(point[0], point[1]);
					bestIndivSeries.add(point[0], point[2]);
				}
				phenotypeChart.setNotify(true);
				
				
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(phenotypePanel);
				mainFrame.setTitle("Symbolic regression result");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
	}

}
