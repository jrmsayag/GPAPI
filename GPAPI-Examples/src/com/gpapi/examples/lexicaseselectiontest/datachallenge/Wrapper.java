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
 * Wrapper.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.lexicaseselectiontest.datachallenge;

import java.util.List;

import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class Wrapper {
	public static final double predictProba(EvolvedIndividual individual, Sample sample){
		List<AbstractType> results = individual.executeUnconditionallySameArgs(sample.argsValues);
		
		double sumPredictors = 0.0;
		for(int i = 0; i < results.size(); i++){
			if(results.get(i) != null) {
				double value = ((RealValue) results.get(i)).getValue();
				sumPredictors += Math.max(1e-15, Math.min(1-1e-15, value));
			} else
				sumPredictors += 0.5;
		}
		
		return sumPredictors / results.size();
	}
}
