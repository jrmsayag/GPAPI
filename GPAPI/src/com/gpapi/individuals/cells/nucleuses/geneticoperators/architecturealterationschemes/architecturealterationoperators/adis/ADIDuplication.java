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
 * ADIDuplication.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADI;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADIProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADIDuplication implements ArchitectureAlterationOperatorInterface {
	
	private static final long serialVersionUID = 3195633330208156613L;
	
	private final int maxAdis;
	
	
	
	public ADIDuplication(int maxAdis){
		this.maxAdis = maxAdis;
	}
	public ADIDuplication(){
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADI> adis = nucleus.getAdis();
		if(adis.isEmpty() || adis.size() >= maxAdis)
			return false;
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADI originalAdi = adis.get(generator.nextInt(adis.size()));
		ADI duplicateAdi = new ADI(
				originalAdi.getId() + 1, 
				originalAdi.getDefaultValue(), 
				geneticOperator.replicateSubtree(originalAdi.getRoot()), 
				originalAdi.getExternalArgsTypes(), 
				originalAdi.getCollectionId(), 
				originalAdi.getFieldsTypes(), 
				originalAdi.getFieldsNames(), 
				originalAdi.getMaxCalls());
		
		List<AbstractModule> referencingModules = new ArrayList<AbstractModule>(nucleus.getRpbs());
		if(nucleus.isHierarchicalAdis())
			referencingModules.addAll(adis.subList(originalAdi.getId() + 1, adis.size()));
		
		for(AbstractModule module : referencingModules){
			for(AbstractNode node : geneticOperator.getDescendantNodes(module.getBranchRoots())){
				if(node instanceof ADIProxy){
					ADIProxy adiProxy = (ADIProxy) node;
					if(adiProxy.getId() == originalAdi.getId() && generator.nextBoolean())
						adiProxy.setAdi(duplicateAdi);
				}
			}
		}
		
		for(int i = duplicateAdi.getId(); i < adis.size(); i++)
			adis.get(i).setId(i+1);
		adis.add(duplicateAdi.getId(), duplicateAdi);
		
		return true;
	}
	
	@Override
	public final ADIDuplication copy() {
		return new ADIDuplication(maxAdis);
	}
}
