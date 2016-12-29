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
 * NPointsPerModuleCrossover.java
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



public final class NPointsPerModuleCrossover implements CrossoverPlanningSchemeInterface {

	private static final long serialVersionUID = -5578727840079495615L;
	
	private final int n;
	public final int getN() {
		return n;
	}

	private final boolean sizeProportional;
	public final boolean isSizeProportional() {
		return sizeProportional;
	}
	
	
	
	public NPointsPerModuleCrossover(int n, boolean sizeProportional){
		this.n = n;
		this.sizeProportional = sizeProportional;
	}
	public NPointsPerModuleCrossover(int n){
		this(n, true);
	}

	@Override
	public final HashMap<AbstractNode, Integer> planCrossover(Nucleus nucleus, GeneticOperatorInterface geneticOperator) {
		HashMap<AbstractNode,Integer> crossoverPlan = new HashMap<AbstractNode,Integer>();
		for(AbstractModule module : nucleus.getAllModules()){
			if(!module.getBranchRoots().isEmpty()){
				for(AbstractNode root : module.getBranchRoots())
					crossoverPlan.put(root, 0);
				for(int i = 0; i < n; i++){
					AbstractNode root = geneticOperator.chooseBranchRoot(module, sizeProportional);
					crossoverPlan.put(root, crossoverPlan.get(root)+1);
				}
			}
		}
		return crossoverPlan;
	}

	@Override
	public final NPointsPerModuleCrossover copy() {
		return new NPointsPerModuleCrossover(n, sizeProportional);
	}
}
