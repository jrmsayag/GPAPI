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


package com.gpapi.examples.lexicaseselectiontest.datachallenge;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.gpapi.DataManager;
import com.gpapi.individuals.EvolvedIndividual;



public class MainConsumer {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InvocationTargetException, InterruptedException {
		EvolvedIndividual individual = DataManager.loadIndividual("datachallenge/results/evolvedPredictor.ser");
		
		Parser.parse("datachallenge/data.csv");
		List<Sample> testSamples = Parser.getTrainSamples();
		
		double[][] plotPoints = new double[101][2];
		for(int i = 0; i <= 100; i++){
			double threshold = ((double) i) / 100;
			
			int tp = 0;
			int tn = 0;
			int fp = 0;
			int fn = 0;
			for(Sample sample : testSamples){
				boolean pred = Wrapper.predictProba(individual, sample) >= threshold;
				if(pred){
					if(sample.shot_made_flag == 1)
						tp++;
					else
						fp++;
				} else {
					if(sample.shot_made_flag == 1)
						fn++;
					else
						tn++;
				}
			}
			
			plotPoints[i][0] = ((double) fp) / (fp + tn);
			plotPoints[i][1] = ((double) tp) / (tp + fn);
		}
		
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;
		for(Sample sample : testSamples){
			boolean pred = Wrapper.predictProba(individual, sample) >= 0.5;
			if(pred){
				if(sample.shot_made_flag == 1)
					tp++;
				else
					fp++;
			} else {
				if(sample.shot_made_flag == 1)
					fn++;
				else
					tn++;
			}
		}
		
		System.out.println("True positives  :  " + tp);
		System.out.println("True negatives  :  " + tn);
		System.out.println("False positives :  " + fp);
		System.out.println("False negatives :  " + fn);
		System.out.println("=> Success rate :  " + ((double) (tp + tn)) / (tp + fp + tn + fn));
		
		
		SwingUtilities.invokeAndWait(() ->
			{
				
				XYSeries rocSeries = new XYSeries("ROC Plot points");
				JFreeChart rocChart = ChartFactory.createXYAreaChart(
						"ROC Curve", 
						"False positive rate", 
						"True positive rate", 
						new XYSeriesCollection(rocSeries), 
						PlotOrientation.VERTICAL, 
						false, 
						false, 
						false);
				((NumberAxis) ((XYPlot) rocChart.getPlot()).getDomainAxis()).setRange(0.0, 1.0);
				((NumberAxis) ((XYPlot) rocChart.getPlot()).getRangeAxis()).setRange(0.0, 1.0);
				
				JPanel phenotypeView = new JPanel();
				phenotypeView.setLayout(new BorderLayout());
				phenotypeView.add(new ChartPanel(rocChart));
				
				
				rocChart.setNotify(false);
				rocSeries.clear();
				for(double[] point : plotPoints)
					rocSeries.add(point[0], point[1]);
				rocChart.setNotify(true);
				
				
				JFrame mainFrame = new JFrame();
				mainFrame.setContentPane(phenotypeView);
				mainFrame.setTitle("Shots success predictor result");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			});
	}

}
