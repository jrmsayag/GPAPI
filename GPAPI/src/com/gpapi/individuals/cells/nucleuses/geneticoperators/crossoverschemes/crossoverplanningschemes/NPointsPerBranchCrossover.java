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
 * NPointsPerBranchCrossover.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes;

import java.util.HashMap;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class NPointsPerBranchCrossover implements CrossoverPlanningSchemeInterface {

	private static final long serialVersionUID = -4152699154040509998L;
	
	private final int n;
	public final int getN() {
		return n;
	}
	
	

	public NPointsPerBranchCrossover(int n){
		this.n = n;
	}

	@Override
	public final HashMap<AbstractNode, Integer> planCrossover(Nucleus nucleus, GeneticOperatorInterface geneticOperator) {
		HashMap<AbstractNode,Integer> crossoverPlan = new HashMap<AbstractNode,Integer>();
		for(AbstractModule module : nucleus.getAllModules()){
			for(AbstractNode root : module.getBranchRoots())
				crossoverPlan.put(root, n);
		}
		return crossoverPlan;
	}

	@Override
	public final NPointsPerBranchCrossover copy() {
		return new NPointsPerBranchCrossover(n);
	}
}
