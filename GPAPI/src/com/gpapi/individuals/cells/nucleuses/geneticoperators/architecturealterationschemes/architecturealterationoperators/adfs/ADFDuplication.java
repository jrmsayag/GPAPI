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
 * ADFDuplication.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adfs;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADF;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADFProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADFDuplication implements ArchitectureAlterationOperatorInterface {
	
	private static final long serialVersionUID = 3195633330208156613L;
	
	private final int maxAdfs;
	
	
	
	public ADFDuplication(int maxAdfs){
		this.maxAdfs = maxAdfs;
	}
	public ADFDuplication(){
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADF> adfs = nucleus.getAdfs();
		if(adfs.isEmpty() || adfs.size() >= maxAdfs)
			return false;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		ADF originalAdf = adfs.get(generator.nextInt(adfs.size()));
		ADF duplicateAdf = new ADF(
				originalAdf.getId() + 1, 
				nucleus.getGeneticOperator().replicateSubtree(originalAdf.getRoot()), 
				originalAdf.getArgsTypes());
		
		for(AbstractNode node : nucleus.getAllNodes()){
			if(node instanceof ADFProxy){
				ADFProxy adfProxy = (ADFProxy) node;
				if(adfProxy.getId() == originalAdf.getId() && generator.nextBoolean())
					adfProxy.setAdf(duplicateAdf);
			}
		}
		
		for(int i = duplicateAdf.getId(); i < adfs.size(); i++)
			adfs.get(i).setId(i+1);
		adfs.add(duplicateAdf.getId(), duplicateAdf);
		
		return true;
	}
	
	@Override
	public final ADFDuplication copy() {
		return new ADFDuplication(maxAdfs);
	}
}
