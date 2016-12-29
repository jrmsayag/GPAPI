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
 * BranchTypingCrossover.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.CrossoverNodesSelectionSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.CrossoverPlanningSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.MutationSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.modules.RPB;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADF;
import com.gpapi.individuals.cells.nucleuses.modules.adfs.ADFProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADI;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADIProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADL;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADLProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADR;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADRProxy;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADSProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



/**
 * 
 * Implements a Branch-Typing crossover (cf. <em>Genetic Programming II</em> by John R. Koza).
 * 
 * @author sayag
 *
 */

public final class BranchTypingCrossover extends AbstractCrossoverOperator {
	
	private static final long serialVersionUID = 3426713233928431572L;
	
	
	
	public BranchTypingCrossover(
			CrossoverPlanningSchemeInterface crossoverPlanningScheme, 
			CrossoverNodesSelectionSchemeInterface crossoverNodesSelectionScheme, 
			MutationSchemeInterface mutationScheme, 
			List<AbstractNode> globalExternalNodes,
			List<AbstractNode> globalInternalNodes){
		super(
				crossoverPlanningScheme, 
				crossoverNodesSelectionScheme, 
				mutationScheme, 
				globalExternalNodes, 
				globalInternalNodes);
	}
	
	
	@Override
	public final Nucleus mergeNucleuses(Nucleus parent1Nucleus, Nucleus parent2Nucleus) {
		Map<AbstractNode,Integer> crossoverPlan = getCrossoverPlanningScheme().planCrossover(parent1Nucleus, this);
		
		ArrayList<ADSProxy> newAdssProxies = new ArrayList<ADSProxy>(parent1Nucleus.getAdss().size());
		ArrayList<ADS> newAdss = createAdss(parent1Nucleus.getAdss(), newAdssProxies);
		
		ArrayList<ADFProxy> newAdfsProxies = new ArrayList<ADFProxy>(parent1Nucleus.getAdfs().size());
		ArrayList<ADF> newAdfs = mergeAdfs(
				parent1Nucleus.getAdfs(), 
				parent2Nucleus.getAdfs(), 
				newAdfsProxies, 
				newAdssProxies, 
				crossoverPlan, 
				parent1Nucleus.isHierarchicalAdfs());
		
		ArrayList<ADIProxy> newAdisProxies = new ArrayList<ADIProxy>(parent1Nucleus.getAdis().size());
		ArrayList<ADI> newAdis = mergeAdis(
				parent1Nucleus.getAdis(), 
				parent2Nucleus.getAdis(), 
				newAdisProxies, 
				newAdfsProxies, 
				newAdssProxies, 
				crossoverPlan, 
				parent1Nucleus.isHierarchicalAdis());
		
		ArrayList<ADRProxy> newAdrsProxies = new ArrayList<ADRProxy>(parent1Nucleus.getAdrs().size());
		ArrayList<ADR> newAdrs = mergeAdrs(
				parent1Nucleus.getAdrs(), 
				parent2Nucleus.getAdrs(), 
				newAdrsProxies, 
				newAdfsProxies, 
				newAdssProxies, 
				crossoverPlan, 
				parent1Nucleus.isHierarchicalAdrs());
		
		ArrayList<ADLProxy> newAdlsProxies = new ArrayList<ADLProxy>(parent1Nucleus.getAdls().size());
		ArrayList<ADL> newAdls = mergeAdls(
				parent1Nucleus.getAdls(), 
				parent2Nucleus.getAdls(), 
				newAdlsProxies, 
				newAdfsProxies, 
				newAdssProxies, 
				crossoverPlan, 
				parent1Nucleus.isHierarchicalAdls());
		
		ArrayList<RPB> newRpbs = mergeRpbs(
				parent1Nucleus.getRpbs(), 
				parent2Nucleus.getRpbs(), 
				newAdlsProxies, 
				newAdrsProxies, 
				newAdisProxies, 
				newAdfsProxies, 
				newAdssProxies, 
				crossoverPlan);
		
		Nucleus newNucleus = new Nucleus(
				copy(), 
				newRpbs, 
				newAdls, 
				parent1Nucleus.isHierarchicalAdls(), 
				newAdrs, 
				parent1Nucleus.isHierarchicalAdrs(), 
				newAdis, 
				parent1Nucleus.isHierarchicalAdis(), 
				newAdfs, 
				parent1Nucleus.isHierarchicalAdfs(), 
				newAdss);
		
		return newNucleus;
	}
	private final ArrayList<ADS> createAdss(ArrayList<ADS> parent1Adss, List<ADSProxy> newAdssProxies){
		ArrayList<ADS> newAdss = new ArrayList<ADS>(parent1Adss.size());
		
		for(int i = 0; i < parent1Adss.size(); i++){
			ADS newAds = new ADS(
					parent1Adss.get(i).getId(), 
					parent1Adss.get(i).getStorageType(), 
					parent1Adss.get(i).getReturnType(), 
					parent1Adss.get(i).getMaxSize());
			
			newAdss.add(newAds);
			newAdssProxies.add(new ADSProxy(newAds, true));
			newAdssProxies.add(new ADSProxy(newAds, false));
		}
		
		return newAdss;
	}
	private final ArrayList<ADF> mergeAdfs(
			ArrayList<ADF> parent1Adfs, 
			ArrayList<ADF> parent2Adfs, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			Map<AbstractNode,Integer> crossoverPlan, 
			boolean hierarchicalAdfs){
		ArrayList<ADF> newAdfs = new ArrayList<ADF>(parent1Adfs.size());
		
		List<ADFProxy> effectiveAdfsProxies = hierarchicalAdfs ? newAdfsProxies : Collections.emptyList();
		for(int i = 0; i < parent1Adfs.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					effectiveAdfsProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					Collections.emptyList(), 
					parent1Adfs.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			AbstractNode newRoot = mergeBranches(
					parent1Adfs.get(i).getRoot(), 
					parent2Adfs.get(i).getRoot(),
					crossoverPlan.get(parent1Adfs.get(i).getRoot()));
			ADF newAdf = new ADF(
					parent1Adfs.get(i).getId(), 
					newRoot, 
					parent1Adfs.get(i).getArgsTypes());
			
			newAdfs.add(newAdf);
			newAdfsProxies.add(new ADFProxy(newAdf));
		}
		
		return newAdfs;
	}
	private final ArrayList<ADI> mergeAdis(
			ArrayList<ADI> parent1Adis, 
			ArrayList<ADI> parent2Adis, 
			ArrayList<ADIProxy> newAdisProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			Map<AbstractNode,Integer> crossoverPlan, 
			boolean hierarchicalAdis){
		ArrayList<ADI> newAdis = new ArrayList<ADI>(parent1Adis.size());
		
		List<ADIProxy> effectiveAdisProxies = hierarchicalAdis ? newAdisProxies : Collections.emptyList();
		for(int i = 0; i < parent1Adis.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					newAdfsProxies, 
					effectiveAdisProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					parent1Adis.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			AbstractNode newRoot = mergeBranches(
					parent1Adis.get(i).getRoot(), 
					parent2Adis.get(i).getRoot(),
					crossoverPlan.get(parent1Adis.get(i).getRoot()));
			ADI newAdi = new ADI(
					parent1Adis.get(i).getId(), 
					parent1Adis.get(i).getDefaultValue(), 
					newRoot, 
					parent1Adis.get(i).getExternalArgsTypes(),
					parent1Adis.get(i).getCollectionId(), 
					parent1Adis.get(i).getFieldsTypes(), 
					parent1Adis.get(i).getFieldsNames(), 
					parent1Adis.get(i).getMaxCalls());
			
			newAdis.add(newAdi);
			newAdisProxies.add(new ADIProxy(newAdi));
		}
		
		return newAdis;
	}
	private final ArrayList<ADR> mergeAdrs(
			ArrayList<ADR> parent1Adrs, 
			ArrayList<ADR> parent2Adrs, 
			ArrayList<ADRProxy> newAdrsProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			Map<AbstractNode,Integer> crossoverPlan, 
			boolean hierarchicalAdrs){
		ArrayList<ADR> newAdrs = new ArrayList<ADR>(parent1Adrs.size());
		
		List<ADRProxy> effectiveAdrsProxies = hierarchicalAdrs ? newAdrsProxies : Collections.emptyList();
		for(int i = 0; i < parent1Adrs.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					newAdfsProxies, 
					Collections.emptyList(), 
					effectiveAdrsProxies, 
					Collections.emptyList(), 
					parent1Adrs.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			AbstractNode newConditionRoot = mergeBranches(
					parent1Adrs.get(i).getConditionRoot(), 
					parent2Adrs.get(i).getConditionRoot(),
					crossoverPlan.get(parent1Adrs.get(i).getConditionRoot()));
			AbstractNode newGroundRoot = mergeBranches(
					parent1Adrs.get(i).getGroundRoot(), 
					parent2Adrs.get(i).getGroundRoot(),
					crossoverPlan.get(parent1Adrs.get(i).getGroundRoot()));
			
			List<List<AbstractProxyNode>> bodyLocalNodes = buildLocalNodes(
					Collections.emptyList(), 
					Collections.emptyList(), 
					Collections.emptyList(), 
					Arrays.asList(new ADRProxy(parent1Adrs.get(i))), 
					Collections.emptyList(), 
					Collections.emptyList());
			
			localNodes.get(0).addAll(bodyLocalNodes.get(0));
			localNodes.get(1).addAll(bodyLocalNodes.get(1));
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			AbstractNode newBodyRoot = mergeBranches(
					parent1Adrs.get(i).getBodyRoot(), 
					parent2Adrs.get(i).getBodyRoot(),
					crossoverPlan.get(parent1Adrs.get(i).getBodyRoot()));
			
			ADR newAdr = new ADR(
					parent1Adrs.get(i).getId(), 
					newConditionRoot, 
					newBodyRoot, 
					newGroundRoot, 
					parent1Adrs.get(i).getArgsTypes(),
					parent1Adrs.get(i).getMaxCalls(), 
					parent1Adrs.get(i).getMaxRecursionDepth());
			
			newAdrs.add(newAdr);
			newAdrsProxies.add(new ADRProxy(newAdr));
		}
		
		return newAdrs;
	}
	private final ArrayList<ADL> mergeAdls(
			ArrayList<ADL> parent1Adls, 
			ArrayList<ADL> parent2Adls, 
			ArrayList<ADLProxy> newAdlsProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			Map<AbstractNode,Integer> crossoverPlan, 
			boolean hierarchicalAdls){
		ArrayList<ADL> newAdls = new ArrayList<ADL>(parent1Adls.size());
		
		List<ADLProxy> effectiveAdlsProxies = hierarchicalAdls ? newAdlsProxies : Collections.emptyList();
		for(int i = 0; i < parent1Adls.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					newAdfsProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					effectiveAdlsProxies, 
					parent1Adls.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			AbstractNode newInitializationRoot = mergeBranches(
					parent1Adls.get(i).getInitializationRoot(), 
					parent2Adls.get(i).getInitializationRoot(),
					crossoverPlan.get(parent1Adls.get(i).getInitializationRoot()));
			AbstractNode newConditionRoot = mergeBranches(
					parent1Adls.get(i).getConditionRoot(), 
					parent2Adls.get(i).getConditionRoot(),
					crossoverPlan.get(parent1Adls.get(i).getConditionRoot()));
			AbstractNode newBodyRoot = mergeBranches(
					parent1Adls.get(i).getBodyRoot(), 
					parent2Adls.get(i).getBodyRoot(),
					crossoverPlan.get(parent1Adls.get(i).getBodyRoot()));
			
			ADL newAdl = new ADL(
					parent1Adls.get(i).getId(), 
					newInitializationRoot, 
					newConditionRoot, 
					newBodyRoot, 
					parent1Adls.get(i).getExternalArgsTypes(),
					parent1Adls.get(i).getMaxCalls(), 
					parent1Adls.get(i).getMaxIterations());
			
			newAdls.add(newAdl);
			newAdlsProxies.add(new ADLProxy(newAdl));
		}
		
		return newAdls;
	}
	private final ArrayList<RPB> mergeRpbs(
			ArrayList<RPB> parent1Rpbs, 
			ArrayList<RPB> parent2Rpbs, 
			ArrayList<ADLProxy> newAdlsProxies, 
			ArrayList<ADRProxy> newAdrsProxies, 
			ArrayList<ADIProxy> newAdisProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			Map<AbstractNode,Integer> crossoverPlan){
		List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
				newAdssProxies, 
				newAdfsProxies, 
				newAdisProxies, 
				newAdrsProxies, 
				newAdlsProxies, 
				parent1Rpbs.get(0).getArgsTypes());
		
		setLocalExternalNodes(localNodes.get(0));
		setLocalInternalNodes(localNodes.get(1));
		
		ArrayList<RPB> newRpbs = new ArrayList<RPB>(parent1Rpbs.size());
		
		for(int i = 0; i < parent1Rpbs.size(); i++){
			AbstractNode newRoot = mergeBranches(
					parent1Rpbs.get(i).getRoot(), 
					parent2Rpbs.get(i).getRoot(),
					crossoverPlan.get(parent1Rpbs.get(i).getRoot()));
			newRpbs.add(new RPB(
					parent1Rpbs.get(i).getId(), 
					newRoot, 
					parent1Rpbs.get(i).getArgsTypes(),
					parent1Rpbs.get(i).getArgsNames()));
		}
		
		return newRpbs;
	}
	
	
	private final AbstractNode mergeBranches(AbstractNode branchRoot1, AbstractNode branchRoot2, int nCrossoverNodes){
		AbstractNode newBranchRoot = replicateSubtree(branchRoot1);
		if(nCrossoverNodes > 0){
			List<AbstractNode> branch1CrossoverNodes = getCrossoverNodesSelectionScheme().chooseFirstBranchCrossoverNodes(
					getDescendantNodes(newBranchRoot), 
					nCrossoverNodes, 
					this);
			
			Map<AbstractNode,AbstractNode> crossoverNodesMap = getCrossoverNodesSelectionScheme().chooseMatchingCrossoverNodes(
					branch1CrossoverNodes, 
					getDescendantNodes(branchRoot2), 
					this);
			
			for(Entry<AbstractNode,AbstractNode> crossoverNodesPair : crossoverNodesMap.entrySet()){
				AbstractNode crossoverNode1 = crossoverNodesPair.getKey();
				AbstractNode crossoverNode2 = crossoverNodesPair.getValue();
				
				if(crossoverNode1.getParent() != null)
					crossoverNode1.getParent().replaceArg(crossoverNode1, crossoverNode2);
				else{
					crossoverNode2.setParent(null);
					newBranchRoot = crossoverNode2;
				}
			}
		}
		return getMutationScheme().mutateBranch(newBranchRoot, this);
	}


	@Override
	public final BranchTypingCrossover copy() {
		return new BranchTypingCrossover(
				getCrossoverPlanningScheme(), 
				getCrossoverNodesSelectionScheme(), 
				getMutationScheme(), 
				getGlobalExternalNodes(),
				getGlobalInternalNodes());
	}
}
