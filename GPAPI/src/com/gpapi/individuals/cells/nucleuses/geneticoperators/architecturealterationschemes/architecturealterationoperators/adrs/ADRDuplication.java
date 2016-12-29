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
 * ADRDuplication.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adrs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADR;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADRProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADRDuplication implements ArchitectureAlterationOperatorInterface {
	
	private static final long serialVersionUID = 3195633330208156613L;
	
	private final int maxAdrs;
	
	
	
	public ADRDuplication(int maxAdrs){
		this.maxAdrs = maxAdrs;
	}
	public ADRDuplication(){
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADR> adrs = nucleus.getAdrs();
		if(adrs.isEmpty() || adrs.size() >= maxAdrs)
			return false;
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADR originalAdr = adrs.get(generator.nextInt(adrs.size()));
		ADR duplicateAdr = new ADR(
				originalAdr.getId() + 1, 
				geneticOperator.replicateSubtree(originalAdr.getConditionRoot()), 
				geneticOperator.replicateSubtree(originalAdr.getBodyRoot()), 
				geneticOperator.replicateSubtree(originalAdr.getGroundRoot()), 
				originalAdr.getArgsTypes(), 
				originalAdr.getMaxCalls(), 
				originalAdr.getMaxRecursionDepth());
		
		for(AbstractNode node : geneticOperator.getDescendantNodes(duplicateAdr.getBodyRoot())){
			if(node instanceof ADRProxy){
				ADRProxy adrProxy = (ADRProxy) node;
				if(adrProxy.getId() == originalAdr.getId())
					adrProxy.setAdr(duplicateAdr);
			}
		}
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdrs())
			referencingModules.addAll(adrs.subList(originalAdr.getId() + 1, adrs.size()));
		
		for(AbstractModule module : referencingModules){
			for(AbstractNode node : geneticOperator.getDescendantNodes(module.getBranchRoots())){
				if(node instanceof ADRProxy){
					ADRProxy adrProxy = (ADRProxy) node;
					if(adrProxy.getId() == originalAdr.getId() && generator.nextBoolean())
						adrProxy.setAdr(duplicateAdr);
				}
			}
		}
		
		for(int i = duplicateAdr.getId(); i < adrs.size(); i++)
			adrs.get(i).setId(i+1);
		adrs.add(duplicateAdr.getId(), duplicateAdr);
		
		return true;
	}
	
	@Override
	public final ADRDuplication copy() {
		return new ADRDuplication(maxAdrs);
	}
}
