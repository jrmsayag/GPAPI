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
 * ADSDeletion.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.adss;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators.ArchitectureAlterationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class ADSDeletion implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = -7777814582712489448L;

	@Override
	public final boolean architectureAlteration(Nucleus nucleus) {
		List<ADS> adss = nucleus.getAdss();
		if(adss.isEmpty())
			return false;
		
		int deletedAdsId = ThreadLocalRandom.current().nextInt(adss.size());
		adss.remove(deletedAdsId);
		
		GeneticOperatorInterface geneticOperator = nucleus.getGeneticOperator();
		
		for(AbstractModule module : nucleus.getAllModules()){
			boolean consolidate = false;
			for(AbstractNode root : module.getBranchRoots()){
				if(adaptBranch(module, root, deletedAdsId, geneticOperator))
					consolidate = true;
			}
			if(consolidate)
				module.consolidate();
		}
		
		for(int i = deletedAdsId; i < adss.size(); i++)
			adss.get(i).setId(i);
		
		return true;
	}
	
	private final boolean adaptBranch(AbstractModule module, AbstractNode root, int deletedAdsId, GeneticOperatorInterface geneticOperator){
		List<AbstractNode> adsProxiesToBeReplaced = new ArrayList<AbstractNode>();
		List<AbstractNode> potentialReplacingNodes = new ArrayList<AbstractNode>();
		
		for(AbstractNode node : geneticOperator.getDescendantNodes(root)){
			if(node instanceof ADSProxy && ((ADSProxy) node).getId() == deletedAdsId)
				adsProxiesToBeReplaced.add(node);
			else if(node.getArgs().isEmpty())
				potentialReplacingNodes.add(node);
		}
		
		for(AbstractNode adsProxy : adsProxiesToBeReplaced){
			AbstractNode replacingNode = geneticOperator.chooseNodeIn(potentialReplacingNodes, adsProxy.getReturnType());
			
			if(replacingNode != null)
				replacingNode = replacingNode.copy();
			else
				replacingNode = geneticOperator.findExternalNode(adsProxy.getReturnType());
			
			if(adsProxy != root)
				adsProxy.getParent().replaceArg(adsProxy, replacingNode);
			else
				module.replaceBranchRoot(adsProxy, replacingNode);
		}
		
		return !adsProxiesToBeReplaced.isEmpty();
	}
	
	@Override
	public final ADSDeletion copy() {
		return new ADSDeletion();
	}
}
