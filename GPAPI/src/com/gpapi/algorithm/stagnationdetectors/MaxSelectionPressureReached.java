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
 * MaxSelectionPressureReached.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.stagnationdetectors;

import java.util.HashSet;
import java.util.List;
import java.util.Observable;

import com.gpapi.GenerationSnapshot;
import com.gpapi.Population;
import com.gpapi.algorithm.breedingoperators.OffspringSelection;



/**
 * 
 * A stagnation detector according to which a population is stagnating when
 * the max selection pressure in OffspringSelection has been reached N times
 * in a row. This detector can therefore be used only when an OffspringSelection 
 * instance is used as the breeding operator of the algorithm.
 * <p>
 * Note : Despite this observer will receive a GenerationSnapshot object through
 * its update() method every generation, the refreshPeriod parameter permits to
 * take these messages into account (and therefore to update the list of stagnating 
 * populations) only periodically.
 * 
 * @author jeremy
 *
 */

public final class MaxSelectionPressureReached implements StagnationDetectorInterface {
	
	private final int n;
	
	private final int refreshPeriod;
	
	private final OffspringSelection selectionPressureGauge;
	
	private final HashSet<Population> allStagnatingPopulations = new HashSet<Population>();
	
	
	
	public MaxSelectionPressureReached(int n, int refreshPeriod, OffspringSelection selectionPressureGauge){
		if(n < 1)
			throw new IllegalArgumentException("n must be strictly positive!");
		else if(refreshPeriod < 1)
			throw new IllegalArgumentException("refreshPeriod must be strictly positive!");
		else if(selectionPressureGauge == null)
			throw new NullPointerException("selectionPressureGauge can't be null!");
		
		this.n = n;
		this.refreshPeriod = refreshPeriod;
		this.selectionPressureGauge = selectionPressureGauge;
	}
	public MaxSelectionPressureReached(int n, OffspringSelection selectionPressureGauge){
		this(n, 1, selectionPressureGauge);
	}
	
	
	@Override
	public final HashSet<Population> getStagnatingPopulationsAmong(List<Population> populations) {
		HashSet<Population> stagnatingPopulations = new HashSet<Population>();
		for(Population population : populations){
			if(allStagnatingPopulations.contains(population))
				stagnatingPopulations.add(population);
		}
		return stagnatingPopulations;
	}
	
	
	@Override
	public final void update(Observable observable, Object arg) {
		if(arg instanceof GenerationSnapshot){
			GenerationSnapshot snapshot = (GenerationSnapshot) arg;
			if((snapshot.getGeneration()-1) % refreshPeriod == 0)
				updateCurrentlyStagnatingPopulations(snapshot.getPopulations(), snapshot.getGeneration());
		}
	}
	private final void updateCurrentlyStagnatingPopulations(List<Population> populations, int generation){
		allStagnatingPopulations.clear();
		for(Population population : populations){
			int count = 0;
			while(selectionPressureGauge.wasMaxPressureReachedFor(population, generation - (1 + count)) && count < n)
				count++;
			if(count >= n)
				allStagnatingPopulations.add(population);
		}
	}
}
