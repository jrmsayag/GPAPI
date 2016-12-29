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


package com.gpapi.examples.datachallenge;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

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

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;



public class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private final ArrayList<Sample> trainSamples;
	private final int maxGenomeSize;
	
	private JPanel phenotypeView;
	private JFreeChart rocChart;
	private XYSeries rocSeries;
	
	
	
	public FitnessFunction(List<Sample> trainSamples, int maxGenomeSize){
		this.trainSamples = new ArrayList<Sample>(trainSamples);
		this.maxGenomeSize = maxGenomeSize;
		
		SwingUtilities.invokeLater(() ->
			{
				rocSeries = new XYSeries("ROC Plot points");
				rocChart = ChartFactory.createXYAreaChart(
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
				
				phenotypeView = new JPanel();
				phenotypeView.setLayout(new BorderLayout());
				phenotypeView.add(new ChartPanel(rocChart));
			});
	}
	public FitnessFunction(List<Sample> trainSamples){
		this(trainSamples, Integer.MAX_VALUE);
	}
	
	
	@Override
	public void applyTo(Population population, int generation) {
		for(EvolvedIndividual individual : population){
			individual.setRawFitnessType(RawFitnessType.Penalty);
			if(individual.getEggCell().getNucleus().getTotalSize() <= maxGenomeSize){
				double logLoss = 0.0;
				for(Sample sample : trainSamples){
					byte act = sample.shot_made_flag;
					double pred = Wrapper.predictProba(individual, sample);
					logLoss += act*Math.log(pred) + (1-act)*Math.log(1-pred);
				}
				logLoss *= -1.0 / trainSamples.size();
				individual.setRawFitness(logLoss);
			}
		}
	}

	@Override
	public JPanel getPhenotypeView(EvolvedIndividual individual) {
		double[][] plotPoints = new double[101][2];
		for(int i = 0; i <= 100; i++){
			double threshold = ((double) i) / 100;
			
			int tp = 0;
			int tn = 0;
			int fp = 0;
			int fn = 0;
			for(Sample sample : trainSamples){
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
		
		SwingUtilities.invokeLater(() ->
			{
				rocChart.setNotify(false);
				rocSeries.clear();
				for(double[] point : plotPoints)
					rocSeries.add(point[0], point[1]);
				rocChart.setNotify(true);
			});
		
		return phenotypeView;
	}
}
