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


package com.gpapi.examples.lexicaseselectiontest.symbolicregression;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private final ArrayList<Double> xValues = new ArrayList<Double>();
	
	private final int maxBranchDepth;
	
	private XYSeries targetFuncSeries;
	private XYSeries bestIndivSeries;
	
	private JFreeChart phenotypeChart;
	private ChartPanel phenotypePanel;
	
	
	
	public FitnessFunction(int maxBranchDepth){
		this.maxBranchDepth = maxBranchDepth;
		
		for(int i = 0; i <= 20; i++)
			xValues.add(i / 10.0 - 1.0);
		
		SwingUtilities.invokeLater(() ->
			{
				targetFuncSeries = new XYSeries("Target Function");
				bestIndivSeries = new XYSeries("Best Individual's phenotype");
				
				XYSeriesCollection phenotypeSeriesCollection = new XYSeriesCollection();
				phenotypeSeriesCollection.addSeries(targetFuncSeries);
				phenotypeSeriesCollection.addSeries(bestIndivSeries);
				
				phenotypeChart = ChartFactory.createXYLineChart(
						null, 
						"x", 
						"f(x)", 
						phenotypeSeriesCollection, 
						PlotOrientation.VERTICAL, 
						true, 
						false, 
						false);
				phenotypePanel = new ChartPanel(phenotypeChart);
			});
	}
	public FitnessFunction(){
		this(Integer.MAX_VALUE);
	}
	
	
	@Override
	public final void applyTo(Population population, int generation) {
		for(EvolvedIndividual individual : population){
			individual.setRawFitnessType(RawFitnessType.Penalty);
			if(individual.getEggCell().getNucleus().getMaxModuleDepth() <= maxBranchDepth){
				double error = 0.0;
				for(Double currentX : xValues){
					double targetValue = targetFunction(currentX);
					double indivValue = ((RealValue) individual.executeUnconditionally(0, RealValue.create(currentX))).getValue();
					
					double currentError = Math.abs(targetValue - indivValue);
					individual.addFitnessCase(currentX, currentError);
					
					error += currentError;
				}
				individual.setRawFitness(error);
			}
		}
		
		/*if(generation > 250){
			modulateWithExecutionCost(population, 1.0E-10);
			modulateWithGenomeSize(population, 1.0E-15);
		}*/
	}
	public static final double targetFunction(double x){
		return 7.5*x*x*x*x + -3.2*x*x*x + 1.5*x*x + -0.5*x - 1;
	}
	
	
	@Override
	public final JPanel getPhenotypeView(EvolvedIndividual individual) {
		double[][] plotPoints = new double[xValues.size()][3];
		for(int i = 0; i < xValues.size(); i++){
			Double currentX = xValues.get(i);
			plotPoints[i][0] = currentX;
			plotPoints[i][1] = targetFunction(currentX);
			plotPoints[i][2] = ((RealValue) individual.executeUnconditionallySameArgs(RealValue.create(currentX)).get(0)).getValue();
		}
		
		SwingUtilities.invokeLater(() ->
			{
				phenotypeChart.setNotify(false);
				targetFuncSeries.clear();
				bestIndivSeries.clear();
				for(double[] point : plotPoints){
					targetFuncSeries.add(point[0], point[1]);
					bestIndivSeries.add(point[0], point[2]);
				}
				phenotypeChart.setNotify(true);
			});
		
		return phenotypePanel;
	}
}
