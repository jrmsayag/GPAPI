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
 * AbstractAlgorithm.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gpapi.ControlView;
import com.gpapi.GenerationSnapshot;
import com.gpapi.GenerationStatistics;
import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.FitnessFunctionInterface;
import com.gpapi.algorithm.generalpurposeoperators.GeneralPurposeOperatorInterface;



public abstract class AbstractAlgorithm extends Observable implements Callable<List<GenerationStatistics>> {
	
	/**
	 * A list of the possible states of the execution of the algorithm.
	 */
	public enum State {
		/**
		 * The algorithm isn't started.
		 */
		Stopped,
		/**
		 * The algorithm is running.
		 */
		Running,
		/**
		 * The algorithm is on pause.
		 */
		Paused,
		/**
		 * The algorithm is on pause after a single step required.
		 */
		StepMode,
		/**
		 * The algorithm is required to execute one step.
		 */
		DoStep
	}
	
	private State currentState;
	/**
	 * Returns the current state of the execution of the algorithm.
	 * 
	 * @return
	 * 			The current state of the execution of the algorithm.
	 */
	public final synchronized State getCurrentState(){
		return currentState;
	}
	/**
	 * Permits to control the execution of the algorithm.
	 * 
	 * @param currentState
	 * 			The required new state.
	 */
	public final synchronized void setCurrentState(State currentState){
		if(currentState != this.currentState){
			this.currentState = currentState;
			
			if(currentState == State.Stopped && mainThread != null)
				mainThread.interrupt();
			
			notifyObservers(currentState);
		}
	}
	
	private final ControlView view = new ControlView(this);
	public final ControlView getView() {
		return view;
	}
	
	private final boolean recordOnlyBestGeneration;
	public final boolean isRecordOnlyBestGeneration() {
		return recordOnlyBestGeneration;
	}

	private final int recordingFrequency;
	public final int getRecordingFrequency() {
		return recordingFrequency;
	}
	
	private final int generations;
	public final int getGenerations() {
		return generations;
	}
	
	private final int nThreads;
	public final int getNThreads() {
		return nThreads;
	}
	
	
	private final List<Population> populations;
	/**
	 * Returns an unmodifiable view of the {@link Population}s currently 
	 * evolved by the algorithm.
	 * 
	 * @return
	 * 			An unmodifiable view of the {@link Population}s currently
	 * 			evolved by the Algorithm.
	 */
	protected final List<Population> getPopulations() {
		return populations;
	}
	
	private final GeneralPurposeOperatorInterface generalPurposeOperator;
	protected final GeneralPurposeOperatorInterface getGeneralPurposeOperator() {
		return generalPurposeOperator;
	}
	
	private final ExecutorService executor;
	protected final ExecutorService getExecutor() {
		return executor;
	}

	private Thread mainThread = null;
	
	
	
	protected AbstractAlgorithm(AbstractAlgorithmBuilder<?> builder){
		addObserver(view);
		setCurrentState(builder.getInitialState());
		
		this.generations = builder.getGenerations();
		this.recordOnlyBestGeneration = builder.isRecordOnlyBestGeneration();
		this.recordingFrequency = builder.getRecordingFrequency();
		this.generalPurposeOperator = builder.getGeneralPurposeOperator();
		this.nThreads = builder.getNThreads();
		
		this.populations = Collections.unmodifiableList(new ArrayList<Population>(builder.getPopulations()));
		this.executor = Executors.newFixedThreadPool(nThreads);
	}
	
	public abstract FitnessFunctionInterface getFitnessFunction();
	
	
	
	@Override
	public final void notifyObservers(){
		setChanged();
		super.notifyObservers();
	}
	@Override
	public final void notifyObservers(Object arg){
		setChanged();
		super.notifyObservers(arg);
	}
	
	
	
	@Override
	/**
	 * The outermost routine for an abstract Genetic Programming algorithm.
	 * <p>
	 * This method performs a blocking call to notifyObservers() every generation, passing 
	 * a GenerationSnapshot object to the observers, that reflects the current state of
	 * the populations.
	 * <p>
	 * This permits applications, by recording themselves as observers of the algorithm, 
	 * to take actions depending on the result of the fitness evaluations, automatically 
	 * blocking the algorithm until the required actions have been performed.
	 * <p>
	 * This mechanism is used by the ControlView class, that displays the current state 
	 * of the algorithm, and can be used as well for dynamically modifying the fitness 
	 * function or the operators based on some arbitrary criterions during the run.
	 * <p>
	 * Examples of such a usage include dynamically changing the weights of fitness cases 
	 * for the lexicase selection strategy, based on their Historically Assessed Hardness 
	 * (cf. <em>Genetic Programming with Historically Assessed Hardness</em> and <em>Assessment 
	 * of Problem Modality by Differential Performance of Lexicase Selection in Genetic 
	 * Programming: A Preliminary Report</em> by Lee Spector).
	 * <p>
	 * In particular, on each generation, the following elements are performed (in the 
	 * following order):
	 * <ul>
	 * 		<li>Call performGeneration() with the current generation passed as
	 * 		parameter. The performGeneration() method is where concrete classes have
	 * 		to implement the algorithms' logic, i.e. evolutionary components and 
	 * 		individuals' evaluation.</li>
	 * 		<li>Create a GenerationSnapshot object for the current generation, and 
	 * 		perform a blocking call to notifyObservers() with the GenerationSnapshot 
	 * 		object as parameter.</li>
	 * 		<li>If the condition below is true :
	 * 			<code>getRecordingFrequency() > 0 && (i-1) % getRecordingFrequency() == 0</code>
	 * 			<ul>
	 * 				<li>Build a GenerationStatistics object for the current generation.</li>
	 * 				<li>If the algorithm is not set to record only the best generation, 
	 * 				append the GenerationStatistics object to an ArrayList of such objects.</li>
	 * 				<li>If the current generation's best individual is better than 
	 * 				the one from the current best overall generation's record, set 
	 * 				the GenerationStatistics object as the new best overall generation's 
	 * 				record.</li>
	 * 			</ul></li>
	 * </ul>
	 * The return value of the method is the ArrayList of GenerationStatistics built
	 * sequentially as described above, with the best overall generation's record
	 * appended at the end.
	 * <p>
	 * Note : The generation count starts at 1.
	 * 
	 * @return
	 * 			The ArrayList of GenerationStatistics built as described above, with 
	 * 			the best overall generation's record appended at the end.
	 * @throws ExecutionException
	 * 			Can be thrown if one of the tasks submitted to the executor terminated
	 * 			by throwing an exception.
	 */
	public final ArrayList<GenerationStatistics> call() throws ExecutionException {
		mainThread = Thread.currentThread();
		
		GenerationStatistics bestOverallStatistics = null;
		ArrayList<GenerationStatistics> results = new ArrayList<GenerationStatistics>();
		
		try {
			for(int i = 1; i <= getGenerations() && !stateCheckingActions(); i++){
				performGeneration(i);
				
				GenerationSnapshot snapshot = new GenerationSnapshot(i, getPopulations());
				notifyObservers(snapshot);
				
				if(getRecordingFrequency() > 0 && (i-1) % getRecordingFrequency() == 0){
					GenerationStatistics currentGenerationStatistics = new GenerationStatistics(i, getPopulations());
					
					if(!isRecordOnlyBestGeneration())
						results.add(currentGenerationStatistics);
					
					if(bestOverallStatistics == null || 
							currentGenerationStatistics.getBestIndividualFor(
									currentGenerationStatistics.getBestPopulation())
							.compareTo(bestOverallStatistics.getBestIndividualFor(
									bestOverallStatistics.getBestPopulation())) < 0)
						bestOverallStatistics = currentGenerationStatistics;
				}
			}
		} catch (InterruptedException e) {
			// Let the algorithm close quietly.
		} finally {
			setCurrentState(State.Stopped);
			Thread.interrupted();
			getExecutor().shutdownNow();
		}
		
		if(bestOverallStatistics != null)
			results.add(bestOverallStatistics);
		
		return results;
	}
	/**
	 * If generation equals one, this method must perform the initial fitness
	 * evaluation of every individual in every population. If generation is
	 * greater than one, it must perform the evolutionary components of the
	 * algorithm for the previous generation, and ensure that the individuals
	 * in the resulting populations all have a fitness set for the current
	 * generation.
	 * <p>
	 * Performing the evolutionary component most generally means applying the 
	 * necessary operators, usually the general-purpose operator and then a 
	 * breeding operator, for transforming the previous generation populations
	 * into the current generation populations.
	 * <p>
	 * In the context of a generational algorithm, ensuring that all individuals
	 * in the resulting populations have a fitness set generally means applying 
	 * the fitness function the the whole populations once again, whereas for a 
	 * steady state algorithm, only the one or the few new individuals must be 
	 * evaluated for the new generation (in particular, the specification for the
	 * SteadyStateBreedingOperatorInterface interface includes this evaluation of
	 * the new individuals in the breeding part of the evolutionary process).
	 * 
	 * @param generation
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected abstract void performGeneration(int generation) throws InterruptedException, ExecutionException;
	
	
	
	/**
	 * Method that blocks as long as the algorithm state is Paused or StepMode, 
	 * returns true if the state is Stopped, and returns false in any other 
	 * case (i.e Running or DoStep).
	 * 
	 * @return
	 * 			True if the state is Stopped, and false if the state is Running or DoStep.
	 * @throws InterruptedException 
	 */
	private final synchronized boolean stateCheckingActions() throws InterruptedException {
		onPauseActions();
		stepModeActions();
		return isStopped();
	}
	/**
	 * Checks if the algorithm is on pause, and if it is the case,
	 * waits for the state to be changed.
	 * 
	 * @throws InterruptedException 
	 */
	private final synchronized void onPauseActions() throws InterruptedException {
		while(true){
			if(currentState != State.Paused)
				return;
			else
				wait(50);
		}
	}
	/**
	 * Checks if one step mode is active, and if it is the case,
	 * waits for another step to be required or for the normal
	 * running state to be set back.
	 * 
	 * @throws InterruptedException 
	 */
	private final synchronized void stepModeActions() throws InterruptedException{
		while(true){
			if(currentState != State.StepMode){
				if(currentState == State.DoStep)
					setCurrentState(State.StepMode);
				return;
			} else
				wait(50);
		}
	}
	/**
	 * Indicates if the execution is (or must be) stopped.
	 * 
	 * @return
	 * 			True if the execution is (or must be) stopped.
	 */
	private final synchronized boolean isStopped(){
		if(currentState == State.Stopped)
			return true;
		else
			return false;
	}
}
