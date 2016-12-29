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
 * GeneralPurposeOperatorInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.generalpurposeoperators;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;



/**
 * 
 * Classes implementing this interface define a general-purpose operator that is to
 * be applied to some of the populations at each generation. By general-purpose, we 
 * mean that this operator is to perform everything that could be necessary but is
 * not included in the breeding operator.
 * 
 * @author jeremy
 *
 */
public interface GeneralPurposeOperatorInterface {
	
	/**
	 * Defines the tasks that are to be performed by this operator.
	 * <p>
	 * Implementations are allowed to modify populations in such a way that some individuals 
	 * might need to be submitted to the fitness function before being submitted to the breeding 
	 * operator. This information can be simply passed to the algorithm by not setting, or clearing, 
	 * the fitness type of these individuals. This should be the case of virtually every generic 
	 * operator that moves individuals from a population to another when the fitness function is not 
	 * a local one (hence the fitnessFunctionIsLocal parameter), since in these cases the fitness of 
	 * each individual depends on the other individuals in the population.
	 * 
	 * @param populations
	 * 			A non-null, possibly empty, list containing the populations to which the
	 * 			operator is to be applied. Implementations should consider this  list of 
	 * 			population only as an unmodifiable view of the algorithm's internal one.
	 * @param generation
	 * 			The current generation. As defined in the AbstractAlgorithm class, the 
	 * 			generation count always starts at one.
	 * @param fitnessFunctionIsLocal
	 * 			A boolean indicating if the fitness function used by the algorithm to
	 * 			evaluate individuals' fitness is a local one (i.e. each individual is
	 * 			submitted independently to the fitness function) or a global one (i.e.
	 * 			each population is submitted as a whole and the fitness of their individuals
	 * 			can be inter-dependent).
	 * @param executor
	 * 			An non-null reference to an executor service which can be used in order 
	 * 			to parallelize the computations. If it is used, implementations must 
	 * 			ensure that the tasks submitted all are finished or cancelled before 
	 * 			returning.
	 * @throws InterruptedException
	 * 			Thrown if the thread from which this method is called is interrupted and the
	 * 			implementing class decides to throw the exception. This should be the case if
	 * 			the operator's computation is heavy, in order not to introduce a delay before 
	 * 			the application can be effectively stopped.
	 * @throws ExecutionException 
	 * 			Can be thrown if one of the tasks submitted to the given executor terminated
	 * 			by throwing an exception.
	 */
	public void perform(List<Population> populations, int generation, boolean fitnessFunctionIsLocal, ExecutorService executor) throws InterruptedException, ExecutionException;

}
