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
 * ControlView.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.gpapi.algorithm.AbstractAlgorithm;
import com.gpapi.algorithm.AbstractAlgorithm.State;



public final class ControlView implements Observer {

    private boolean enabled = true;
    public final boolean isEnabled() {
		return enabled;
	}
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	private int refreshPeriod = 1;
	public final int getRefreshPeriod() {
		return refreshPeriod;
	}
	public final void setRefreshPeriod(int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	private double minDisplayableFitness = 0.0;
    public final double getMinDisplayableFitness() {
		return minDisplayableFitness;
	}
	public final void setMinDisplayableFitness(double minDisplayableFitness) {
		this.minDisplayableFitness = minDisplayableFitness;
	}
	
	private double maxDisplayableFitness = Double.MAX_VALUE;
	public final double getMaxDisplayableFitness() {
		return maxDisplayableFitness;
	}
	public final void setMaxDisplayableFitness(double maxDisplayableFitness) {
		this.maxDisplayableFitness = maxDisplayableFitness;
	}
	
	private int maxGenerationsDisplayed = Integer.MAX_VALUE;
	public final int getMaxGenerationsDisplayed() {
		return maxGenerationsDisplayed;
	}
	public final void setMaxGenerationsDisplayed(int maxGenerationsDisplayed) {
		this.maxGenerationsDisplayed = maxGenerationsDisplayed;
		SwingUtilities.invokeLater(() ->
			{
				for(Entry<Population,TimeSeries> maxFitnessSeriesEntry : maxFitnessSeries.entrySet())
					maxFitnessSeriesEntry.getValue().setMaximumItemCount(maxGenerationsDisplayed);
				for(Entry<Population,TimeSeries> medianFitnessSeriesEntry : medianFitnessSeries.entrySet())
					medianFitnessSeriesEntry.getValue().setMaximumItemCount(maxGenerationsDisplayed);
				for(Entry<Population,TimeSeries> bestIndividualSizeSeriesEntry : bestIndividualStructuralComplexitySeries.entrySet())
					bestIndividualSizeSeriesEntry.getValue().setMaximumItemCount(maxGenerationsDisplayed);
				for(Entry<Population,TimeSeries> medianSizeSeriesEntry : medianStructuralComplexitySeries.entrySet())
					medianSizeSeriesEntry.getValue().setMaximumItemCount(maxGenerationsDisplayed);
				for(Entry<Population,TimeSeries> bestIndividualCostSeriesEntry : bestIndividualExecutionCostSeries.entrySet())
					bestIndividualCostSeriesEntry.getValue().setMaximumItemCount(maxGenerationsDisplayed);
				for(Entry<Population,TimeSeries> medianCostSeriesEntry : medianExecutionCostSeries.entrySet())
					medianCostSeriesEntry.getValue().setMaximumItemCount(maxGenerationsDisplayed);
			});
	}

	private JPanel panel;
	public final JPanel getPanel(){
		return panel;
	}
	
	private JButton resumeButton;
    private JButton pauseButton;
    private JButton stepButton;
    private JButton stopButton;
    
    private JLabel currentStateText;
    private JLabel bestRawFitnessText;
    private JLabel currentGenerationText;
    
    private JPanel bestIndividualGenotypeTab;
    private JPanel bestIndividualPhenotypeTab;
    
    private final TimeSeriesCollection fitnessSeriesCollection = new TimeSeriesCollection();
    private final HashMap<Population,TimeSeries> maxFitnessSeries = new HashMap<Population,TimeSeries>();
    private final HashMap<Population,TimeSeries> medianFitnessSeries = new HashMap<Population,TimeSeries>();
    
    private final TimeSeriesCollection structuralComplexitySeriesCollection = new TimeSeriesCollection();
    private final HashMap<Population,TimeSeries> bestIndividualStructuralComplexitySeries = new HashMap<Population,TimeSeries>();
    private final HashMap<Population,TimeSeries> medianStructuralComplexitySeries = new HashMap<Population,TimeSeries>();
    
    private final TimeSeriesCollection executionCostSeriesCollection = new TimeSeriesCollection();
    private final HashMap<Population,TimeSeries> bestIndividualExecutionCostSeries = new HashMap<Population,TimeSeries>();
    private final HashMap<Population,TimeSeries> medianExecutionCostSeries = new HashMap<Population,TimeSeries>();
    
	private final AbstractAlgorithm algorithm;
	
	private JPanel currentGenotypeView = null;
	private JPanel currentPhenotypeView = null;
	
	
	
	public ControlView(AbstractAlgorithm algorithm) {
		super();
		
		this.algorithm = algorithm;
		
		
		SwingUtilities.invokeLater(() ->
			{
				// Building the top part.
				
				resumeButton = new JButton("Resume");
				resumeButton.addActionListener(e -> algorithm.setCurrentState(State.Running));
				
				pauseButton = new JButton("Pause");
				pauseButton.addActionListener(e -> algorithm.setCurrentState(State.Paused));
				
				stepButton = new JButton("Step");
				stepButton.addActionListener(e -> algorithm.setCurrentState(State.DoStep));
				
				stopButton = new JButton("Stop");
				stopButton.addActionListener(e -> algorithm.setCurrentState(State.Stopped));
				
				JPanel controlButtonsPanel = new JPanel();
				controlButtonsPanel.setLayout(new BoxLayout(controlButtonsPanel, BoxLayout.LINE_AXIS));
				controlButtonsPanel.add(resumeButton);
				controlButtonsPanel.add(pauseButton);
				controlButtonsPanel.add(stepButton);
				controlButtonsPanel.add(stopButton);
				
				
				currentStateText = new JLabel("Stopped");
				bestRawFitnessText = new JLabel("0");
				currentGenerationText = new JLabel(String.format("0 / %d", algorithm.getGenerations()));
				
				JPanel statePanel = new JPanel();
				statePanel.setLayout(new GridLayout(3, 2, 25, 10));
				statePanel.add(new JLabel("Current state :"));
				statePanel.add(currentStateText);
				statePanel.add(new JLabel("Best raw fitness :"));
				statePanel.add(bestRawFitnessText);
				statePanel.add(new JLabel("Generation :"));
				statePanel.add(currentGenerationText);
				
				
				JPanel simulationControlPanel = new JPanel();
				simulationControlPanel.setBorder(BorderFactory.createTitledBorder("Simulation control"));
				simulationControlPanel.setLayout(new BoxLayout(simulationControlPanel, BoxLayout.LINE_AXIS));
				simulationControlPanel.add(controlButtonsPanel);
				simulationControlPanel.add(new JSeparator());
				simulationControlPanel.add(statePanel);
				
				
				// Building the bottom part.
				
				bestIndividualGenotypeTab = new JPanel();
				bestIndividualGenotypeTab.setLayout(new BorderLayout());
				
				bestIndividualPhenotypeTab = new JPanel();
				bestIndividualPhenotypeTab.setLayout(new BorderLayout());
				
				JFreeChart fitnessChart = ChartFactory.createXYLineChart(
						null, 
						"Generation", 
						"Raw Fitness", 
						fitnessSeriesCollection, 
						PlotOrientation.VERTICAL, 
						true, 
						false, 
						false);
					XYPlot fitnessPlot = (XYPlot) fitnessChart.getPlot();
					((NumberAxis) fitnessPlot.getRangeAxis()).setAutoRangeIncludesZero(false);
					((NumberAxis) fitnessPlot.getDomainAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				JPanel fitnessEvolutionTab = new JPanel();
				fitnessEvolutionTab.setLayout(new BorderLayout());
				fitnessEvolutionTab.add(new ChartPanel(fitnessChart));
				
				JFreeChart structuralComplexityChart = ChartFactory.createXYLineChart(
						null, 
						"Generation", 
						"Structural Complexity", 
						structuralComplexitySeriesCollection, 
						PlotOrientation.VERTICAL, 
						true, 
						false, 
						false);
					XYPlot structuralComplexityPlot = (XYPlot) structuralComplexityChart.getPlot();
					((NumberAxis) structuralComplexityPlot.getRangeAxis()).setAutoRangeIncludesZero(false);
					((NumberAxis) structuralComplexityPlot.getRangeAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
					((NumberAxis) structuralComplexityPlot.getDomainAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				JPanel structuralComplexityEvolutionTab = new JPanel();
				structuralComplexityEvolutionTab.setLayout(new BorderLayout());
				structuralComplexityEvolutionTab.add(new ChartPanel(structuralComplexityChart));
				
				JFreeChart executionCostChart = ChartFactory.createXYLineChart(
						null, 
						"Generation", 
						"Execution Cost", 
						executionCostSeriesCollection, 
						PlotOrientation.VERTICAL, 
						true, 
						false, 
						false);
					XYPlot executionCostPlot = (XYPlot) executionCostChart.getPlot();
					((NumberAxis) executionCostPlot.getRangeAxis()).setAutoRangeIncludesZero(false);
					((NumberAxis) executionCostPlot.getRangeAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
					((NumberAxis) executionCostPlot.getDomainAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				JPanel executionCostEvolutionTab = new JPanel();
				executionCostEvolutionTab.setLayout(new BorderLayout());
				executionCostEvolutionTab.add(new ChartPanel(executionCostChart));
				
				JTabbedPane simultationOutputTabs = new JTabbedPane();
				simultationOutputTabs.setBorder(BorderFactory.createTitledBorder("Simulation output"));
				simultationOutputTabs.addTab("Best individual genotype", bestIndividualGenotypeTab);
				simultationOutputTabs.addTab("Best individual phenotype", bestIndividualPhenotypeTab);
				simultationOutputTabs.addTab("Raw fitness evolution", fitnessEvolutionTab);
				simultationOutputTabs.addTab("Execution cost evolution", executionCostEvolutionTab);
				simultationOutputTabs.addTab("Structural complexity evolution", structuralComplexityEvolutionTab);
				
				
				// Putting it all together.
				
				panel = new JPanel();
				panel.setLayout(new BorderLayout());
				panel.add(
						new JSplitPane(
								JSplitPane.VERTICAL_SPLIT, 
								simulationControlPanel, 
								simultationOutputTabs), 
						BorderLayout.CENTER);
			});
    }
	
    
	@Override
    public final void update(Observable observable, Object arg){
    	if(isEnabled()){
    		if(observable == algorithm){
        		if(arg instanceof State)
        			updateViewForState((State) arg);
        		else if(arg instanceof GenerationSnapshot)
        			addIterationInfo((GenerationSnapshot) arg);
        	}
    	}
    }
    
    private final void updateViewForState(State state){
    	if(state == State.Paused || state == State.StepMode){
    		SwingUtilities.invokeLater(() ->
	    		{
	                resumeButton.setEnabled(true);
	                pauseButton.setEnabled(false);
	                stepButton.setEnabled(true);
	                stopButton.setEnabled(true);
	                currentStateText.setText("Paused");
	    		});
    	} else if(state == State.Running){
    		SwingUtilities.invokeLater(() ->
    			{
    				resumeButton.setEnabled(false);
    				pauseButton.setEnabled(true);
    				stepButton.setEnabled(true);
    				stopButton.setEnabled(true);
    				currentStateText.setText("Running");
    			});
    	} else if(state == State.Stopped){
    		SwingUtilities.invokeLater(() ->
    			{
    		        resumeButton.setEnabled(false);
    		        pauseButton.setEnabled(false);
    		        stepButton.setEnabled(false);
    		        stopButton.setEnabled(false);
    		        currentStateText.setText("Stopped");
    			});
    	}
    }
    private final void addIterationInfo(GenerationSnapshot snapshot){
    	if(getRefreshPeriod() > 0 && (snapshot.getGeneration() - 1) % getRefreshPeriod() == 0){
    		GenerationStatistics statistics = new GenerationStatistics(snapshot.getGeneration(), snapshot.getPopulations());
    		
    		
    		// Textual information
    		
    		int currentGeneration = statistics.getGeneration();
    		int maximumGeneration = algorithm.getGenerations();
    		double bestRawFitness = statistics.getBestIndividualFor(statistics.getBestPopulation()).getRawFitness();
    		SwingUtilities.invokeLater(() -> 
    			{
    				bestRawFitnessText.setText(String.valueOf(bestRawFitness));
    				currentGenerationText.setText(String.format("%d / %d", currentGeneration, maximumGeneration));
    			});
    		
    		
    		// Fitness and Structural complexity curves
    		
    		int i = 0;
    		for(Population population : statistics.getPopulations()){
    			TimeSeries currentMaxFitnessSeries;
    			TimeSeries currentMedianFitnessSeries;
    			TimeSeries currentBestIndividualStructuralComplexitySeries;
    			TimeSeries currentMedianStructuralComplexitySeries;
    			TimeSeries currentBestIndividualExecutionCostSeries;
    			TimeSeries currentMedianExecutionCostSeries;
    			if(!maxFitnessSeries.containsKey(population)){
    				currentMaxFitnessSeries = new TimeSeries(String.format("Population-%d max fitness", i));
    				currentMedianFitnessSeries = new TimeSeries(String.format("Population-%d median fitness", i));
    				currentBestIndividualStructuralComplexitySeries = new TimeSeries(String.format("Population-%d best indiv. struct. complexity", i));
    				currentMedianStructuralComplexitySeries = new TimeSeries(String.format("Population-%d median structural complexity", i));
    				currentBestIndividualExecutionCostSeries = new TimeSeries(String.format("Population-%d best indiv. exec. cost", i));
        			currentMedianExecutionCostSeries = new TimeSeries(String.format("Population-%d median exec. cost", i));
    				maxFitnessSeries.put(population, currentMaxFitnessSeries);
    				medianFitnessSeries.put(population, currentMedianFitnessSeries);
    				bestIndividualStructuralComplexitySeries.put(population, currentBestIndividualStructuralComplexitySeries);
    				medianStructuralComplexitySeries.put(population, currentMedianStructuralComplexitySeries);
    				bestIndividualExecutionCostSeries.put(population, currentBestIndividualExecutionCostSeries);
    				medianExecutionCostSeries.put(population, currentMedianExecutionCostSeries);
    				SwingUtilities.invokeLater(() -> {
    						currentMaxFitnessSeries.setMaximumItemCount(maxGenerationsDisplayed);
    						currentMedianFitnessSeries.setMaximumItemCount(maxGenerationsDisplayed);
    						currentBestIndividualStructuralComplexitySeries.setMaximumItemCount(maxGenerationsDisplayed);
    						currentMedianStructuralComplexitySeries.setMaximumItemCount(maxGenerationsDisplayed);
    						currentBestIndividualExecutionCostSeries.setMaximumItemCount(maxGenerationsDisplayed);
    						currentMedianExecutionCostSeries.setMaximumItemCount(maxGenerationsDisplayed);
    						fitnessSeriesCollection.addSeries(currentMaxFitnessSeries);
    						fitnessSeriesCollection.addSeries(currentMedianFitnessSeries);
    						structuralComplexitySeriesCollection.addSeries(currentBestIndividualStructuralComplexitySeries);
    						structuralComplexitySeriesCollection.addSeries(currentMedianStructuralComplexitySeries);
    						executionCostSeriesCollection.addSeries(currentBestIndividualExecutionCostSeries);
    						executionCostSeriesCollection.addSeries(currentMedianExecutionCostSeries);
    					});
    			} else {
    				currentMaxFitnessSeries = maxFitnessSeries.get(population);
    				currentMedianFitnessSeries = medianFitnessSeries.get(population);
    				currentBestIndividualStructuralComplexitySeries = bestIndividualStructuralComplexitySeries.get(population);
    				currentMedianStructuralComplexitySeries = medianStructuralComplexitySeries.get(population);
    				currentBestIndividualExecutionCostSeries = bestIndividualExecutionCostSeries.get(population);
    				currentMedianExecutionCostSeries = medianExecutionCostSeries.get(population);
    			}
    			
    			double populationBestRawFitness = statistics.getBestIndividualFor(population).getRawFitness();
    			double populationMedianRawFitness = statistics.getMedianFitnessFor(population);
    			int populationBestIndividualStructuralComplexity = statistics.getBestIndividualFor(population).getEggCell().getNucleus().getTotalSize();
    			int populationMedianStructuralComplexity = statistics.getMedianStructuralComplexityFor(population);
    			int populationBestIndividualExecutionCost = statistics.getBestIndividualFor(population).getLastExecutionCost();
    			int populationMedianExecutionCost = statistics.getMedianExecutionCostFor(population);
    			SwingUtilities.invokeLater(() -> {
        				currentMaxFitnessSeries.addOrUpdate(new FixedMillisecond(currentGeneration), cleanFitnessValueFor(populationBestRawFitness));
        				currentMedianFitnessSeries.addOrUpdate(new FixedMillisecond(currentGeneration), cleanFitnessValueFor(populationMedianRawFitness));
        				currentBestIndividualStructuralComplexitySeries.addOrUpdate(new FixedMillisecond(currentGeneration), populationBestIndividualStructuralComplexity);
        				currentMedianStructuralComplexitySeries.addOrUpdate(new FixedMillisecond(currentGeneration), populationMedianStructuralComplexity);
        				currentBestIndividualExecutionCostSeries.addOrUpdate(new FixedMillisecond(currentGeneration), populationBestIndividualExecutionCost);
        				currentMedianExecutionCostSeries.addOrUpdate(new FixedMillisecond(currentGeneration), populationMedianExecutionCost);
        			});
    			
    			i++;
    		}
    		
    		
    		// Genotype and phenotype views
    		
    		JPanel genotypeView = statistics.getBestIndividualFor(statistics.getBestPopulation()).getEggCell().getNucleus().getView();
    		JPanel phenotypeView = algorithm.getFitnessFunction().getPhenotypeView(statistics.getBestIndividualFor(statistics.getBestPopulation()));
    		SwingUtilities.invokeLater(() ->
    			{
    				if(genotypeView != currentGenotypeView){
    					bestIndividualGenotypeTab.removeAll();
    					bestIndividualGenotypeTab.add(genotypeView, BorderLayout.CENTER);
    					bestIndividualGenotypeTab.validate();
    					currentGenotypeView = genotypeView;
    				}
    				
    				if(phenotypeView != currentPhenotypeView){
    					bestIndividualPhenotypeTab.removeAll();
    		    		bestIndividualPhenotypeTab.add(phenotypeView, BorderLayout.CENTER);
    		    		bestIndividualPhenotypeTab.validate();
    					currentPhenotypeView = phenotypeView;
    				}
    			});
    	}
    }
	private final Double cleanFitnessValueFor(double value){
		if(Double.isNaN(value))
			return null;
		else
			return Math.min(maxDisplayableFitness, Math.max(minDisplayableFitness, value));
	}
}
