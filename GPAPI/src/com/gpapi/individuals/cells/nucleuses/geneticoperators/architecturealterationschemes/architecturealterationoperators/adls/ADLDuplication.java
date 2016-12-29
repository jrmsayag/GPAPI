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
 * ADLDuplication.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADL;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADLProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADLDuplication implements ArchitectureAlterationOperatorInterface {
	
	private static final long serialVersionUID = 3195633330208156613L;
	
	private final int maxAdls;
	
	
	
	public ADLDuplication(int maxAdfs){
		this.maxAdls = maxAdfs;
	}
	public ADLDuplication(){
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADL> adls = nucleus.getAdls();
		if(adls.isEmpty() || adls.size() >= maxAdls)
			return false;
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADL originalAdl = adls.get(generator.nextInt(adls.size()));
		ADL duplicateAdl = new ADL(
				originalAdl.getId() + 1, 
				geneticOperator.replicateSubtree(originalAdl.getInitializationRoot()), 
				geneticOperator.replicateSubtree(originalAdl.getConditionRoot()), 
				geneticOperator.replicateSubtree(originalAdl.getBodyRoot()), 
				originalAdl.getExternalArgsTypes(), 
				originalAdl.getMaxCalls(), 
				originalAdl.getMaxIterations());
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdls())
			referencingModules.addAll(adls.subList(originalAdl.getId() + 1, adls.size()));
		
		for(AbstractModule module : referencingModules){
			for(AbstractNode node : geneticOperator.getDescendantNodes(module.getBranchRoots())){
				if(node instanceof ADLProxy){
					ADLProxy adlProxy = (ADLProxy) node;
					if(adlProxy.getId() == originalAdl.getId() && generator.nextBoolean())
						adlProxy.setAdl(duplicateAdl);
				}
			}
		}
		
		for(int i = duplicateAdl.getId(); i < adls.size(); i++)
			adls.get(i).setId(i+1);
		adls.add(duplicateAdl.getId(), duplicateAdl);
		
		return true;
	}
	
	@Override
	public final ADLDuplication copy() {
		return new ADLDuplication(maxAdls);
	}
}
