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
 * PointTypingCrossover.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.ArchitectureAlterationSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes.CrossoverNodesSelectionSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossoverplanningschemes.CrossoverPlanningSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.MutationSchemeInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractProxyNode;
import com.gpapi.individuals.cells.nucleuses.modules.ArgumentProxy;
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
 * Implements a Point-Typing crossover (cf. <em>Genetic Programming II</em> by John R. Koza).
 * 
 * @author sayag
 *
 */

public final class PointTypingCrossover extends AbstractAchitecturalCrossoverOperator {

	private static final long serialVersionUID = -8397554064314376520L;
	
	
	
	public PointTypingCrossover(
			CrossoverPlanningSchemeInterface crossoverPlanningScheme, 
			CrossoverNodesSelectionSchemeInterface crossoverNodesSelectionScheme, 
			MutationSchemeInterface mutationScheme, 
			ArchitectureAlterationSchemeInterface architectureAlterationScheme, 
			List<AbstractNode> globalExternalNodes,
			List<AbstractNode> globalInternalNodes){
		super(
				crossoverPlanningScheme, 
				crossoverNodesSelectionScheme, 
				mutationScheme, 
				architectureAlterationScheme, 
				globalExternalNodes, 
				globalInternalNodes);
	}
	
	
	@Override
	public final Nucleus mergeNucleuses(Nucleus parent1Nucleus, Nucleus parent2Nucleus) {
		ArrayList<ADSProxy> newAdsProxies = new ArrayList<ADSProxy>(parent1Nucleus.getAdss().size());
		for(int i = 0; i < parent1Nucleus.getAdss().size(); i++){
			newAdsProxies.add(new ADSProxy(parent1Nucleus.getAdss().get(i), true));
			newAdsProxies.add(new ADSProxy(parent1Nucleus.getAdss().get(i), false));
		}
		
		ArrayList<ADFProxy> newAdfsProxies = new ArrayList<ADFProxy>(parent1Nucleus.getAdfs().size());
		HashMap<Integer,List<AbstractProxyNode>> adfsLocalExternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		HashMap<Integer,List<AbstractProxyNode>> adfsLocalInternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		ArrayList<AbstractNode> newAdfsRoots = copyAdfsBranches(
				parent1Nucleus.getAdfs(), 
				adfsLocalExternalNodes, 
				adfsLocalInternalNodes, 
				newAdfsProxies, 
				newAdsProxies, 
				parent1Nucleus.isHierarchicalAdfs());
		
		ArrayList<ADIProxy> newAdisProxies = new ArrayList<ADIProxy>(parent1Nucleus.getAdis().size());
		HashMap<Integer,List<AbstractProxyNode>> adisLocalExternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		HashMap<Integer,List<AbstractProxyNode>> adisLocalInternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		ArrayList<AbstractNode> newAdisRoots = copyAdisBranches(
				parent1Nucleus.getAdis(), 
				adisLocalExternalNodes, 
				adisLocalInternalNodes, 
				newAdisProxies, 
				newAdfsProxies, 
				newAdsProxies, 
				parent1Nucleus.isHierarchicalAdis());
		
		ArrayList<ADRProxy> newAdrsProxies = new ArrayList<ADRProxy>(parent1Nucleus.getAdrs().size());
		HashMap<Integer,ArrayList<List<AbstractProxyNode>>> adrsLocalExternalNodes = new HashMap<Integer,ArrayList<List<AbstractProxyNode>>>();
		HashMap<Integer,ArrayList<List<AbstractProxyNode>>> adrsLocalInternalNodes = new HashMap<Integer,ArrayList<List<AbstractProxyNode>>>();
		ArrayList<ArrayList<AbstractNode>> newAdrsRoots = copyAdrsBranches(
				parent1Nucleus.getAdrs(), 
				adrsLocalExternalNodes, 
				adrsLocalInternalNodes, 
				newAdrsProxies, 
				newAdfsProxies, 
				newAdsProxies, 
				parent1Nucleus.isHierarchicalAdrs());
		
		ArrayList<ADLProxy> newAdlsProxies = new ArrayList<ADLProxy>(parent1Nucleus.getAdls().size());
		HashMap<Integer,List<AbstractProxyNode>> adlsLocalExternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		HashMap<Integer,List<AbstractProxyNode>> adlsLocalInternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		ArrayList<ArrayList<AbstractNode>> newAdlsRoots = copyAdlsBranches(
				parent1Nucleus.getAdls(), 
				adlsLocalExternalNodes, 
				adlsLocalInternalNodes, 
				newAdlsProxies, 
				newAdfsProxies, 
				newAdsProxies, 
				parent1Nucleus.isHierarchicalAdls());
		
		HashMap<Integer,List<AbstractProxyNode>> rpbsLocalExternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		HashMap<Integer,List<AbstractProxyNode>> rpbsLocalInternalNodes = new HashMap<Integer,List<AbstractProxyNode>>();
		ArrayList<AbstractNode> newRpbsRoots = copyRpbsBranches(
				parent1Nucleus.getRpbs(), 
				rpbsLocalExternalNodes, 
				rpbsLocalInternalNodes, 
				newAdlsProxies, 
				newAdrsProxies, 
				newAdisProxies, 
				newAdfsProxies, 
				newAdsProxies);
		
		Map<AbstractNode,Integer> crossoverPlan = getCrossoverPlanningScheme().planCrossover(parent1Nucleus, this);
		
		HashMap<AbstractNode,ArrayList<AbstractProxyNode>> matchingNodesProperties = buildNodesProperties(parent2Nucleus);
		
		ArrayList<ADS> newAdss = new ArrayList<ADS>(parent1Nucleus.getAdss().size());
		for(int i = 0; i < parent1Nucleus.getAdss().size(); i++){
			newAdss.add(
					new ADS(
							parent1Nucleus.getAdss().get(i).getId(), 
							parent1Nucleus.getAdss().get(i).getStorageType(), 
							parent1Nucleus.getAdss().get(i).getReturnType(), 
							parent1Nucleus.getAdss().get(i).getMaxSize()));
		}
		
		ArrayList<ADF> newAdfs = new ArrayList<ADF>(newAdfsRoots.size());
		for(int i = 0; i < newAdfsRoots.size(); i++){
			setLocalExternalNodes(adfsLocalExternalNodes.get(i));
			setLocalInternalNodes(adfsLocalInternalNodes.get(i));
			newAdfs.add(
					new ADF(
							parent1Nucleus.getAdfs().get(i).getId(),
							synthesizeBranch(
									newAdfsRoots.get(i), 
									selectCompatibleNodesInAdfs(i, parent1Nucleus, matchingNodesProperties), 
									crossoverPlan.get(parent1Nucleus.getAdfs().get(i).getRoot())), 
							parent1Nucleus.getAdfs().get(i).getArgsTypes()));
		}
		
		ArrayList<ADI> newAdis = new ArrayList<ADI>(newAdisRoots.size());
		for(int i = 0; i < newAdisRoots.size(); i++){
			setLocalExternalNodes(adisLocalExternalNodes.get(i));
			setLocalInternalNodes(adisLocalInternalNodes.get(i));
			newAdis.add(
					new ADI(
							parent1Nucleus.getAdis().get(i).getId(),
							parent1Nucleus.getAdis().get(i).getDefaultValue(), 
							synthesizeBranch(
									newAdisRoots.get(i), 
									selectCompatibleNodesInAdis(i, parent1Nucleus, matchingNodesProperties), 
									crossoverPlan.get(parent1Nucleus.getAdis().get(i).getRoot())), 
							parent1Nucleus.getAdis().get(i).getExternalArgsTypes(), 
							parent1Nucleus.getAdis().get(i).getCollectionId(), 
							parent1Nucleus.getAdis().get(i).getFieldsTypes(), 
							parent1Nucleus.getAdis().get(i).getFieldsNames(), 
							parent1Nucleus.getAdis().get(i).getMaxCalls()));
		}
		
		ArrayList<ADR> newAdrs = new ArrayList<ADR>(newAdrsRoots.size());
		for(int i = 0; i < newAdrsRoots.size(); i++){
			setLocalExternalNodes(adrsLocalExternalNodes.get(i).get(0));
			setLocalInternalNodes(adrsLocalInternalNodes.get(i).get(0));
			AbstractNode conditionRoot = synthesizeBranch(
					newAdrsRoots.get(i).get(0), 
					selectCompatibleNodesInAdrs(i, parent1Nucleus, matchingNodesProperties, false), 
					crossoverPlan.get(parent1Nucleus.getAdrs().get(i).getConditionRoot()));
			
			setLocalExternalNodes(adrsLocalExternalNodes.get(i).get(1));
			setLocalInternalNodes(adrsLocalInternalNodes.get(i).get(1));
			AbstractNode bodyRoot = synthesizeBranch(
					newAdrsRoots.get(i).get(1), 
					selectCompatibleNodesInAdrs(i, parent1Nucleus, matchingNodesProperties, true), 
					crossoverPlan.get(parent1Nucleus.getAdrs().get(i).getBodyRoot()));
			
			setLocalExternalNodes(adrsLocalExternalNodes.get(i).get(0));
			setLocalInternalNodes(adrsLocalInternalNodes.get(i).get(0));
			AbstractNode groundRoot = synthesizeBranch(
					newAdrsRoots.get(i).get(2), 
					selectCompatibleNodesInAdrs(i, parent1Nucleus, matchingNodesProperties, false), 
					crossoverPlan.get(parent1Nucleus.getAdrs().get(i).getGroundRoot()));
			
			newAdrs.add(
					new ADR(
							parent1Nucleus.getAdrs().get(i).getId(),
							conditionRoot, 
							bodyRoot, 
							groundRoot, 
							parent1Nucleus.getAdrs().get(i).getArgsTypes(), 
							parent1Nucleus.getAdrs().get(i).getMaxCalls(),
							parent1Nucleus.getAdrs().get(i).getMaxRecursionDepth()));
		}
		
		ArrayList<ADL> newAdls = new ArrayList<ADL>(newAdlsRoots.size());
		for(int i = 0; i < newAdlsRoots.size(); i++){
			setLocalExternalNodes(adlsLocalExternalNodes.get(i));
			setLocalInternalNodes(adlsLocalInternalNodes.get(i));
			newAdls.add(
					new ADL(
							parent1Nucleus.getAdls().get(i).getId(),
							synthesizeBranch(
									newAdlsRoots.get(i).get(0), 
									selectCompatibleNodesInAdls(i, parent1Nucleus, matchingNodesProperties), 
									crossoverPlan.get(parent1Nucleus.getAdls().get(i).getInitializationRoot())), 
							synthesizeBranch(
									newAdlsRoots.get(i).get(1), 
									selectCompatibleNodesInAdls(i, parent1Nucleus, matchingNodesProperties), 
									crossoverPlan.get(parent1Nucleus.getAdls().get(i).getConditionRoot())), 
							synthesizeBranch(
									newAdlsRoots.get(i).get(2), 
									selectCompatibleNodesInAdls(i, parent1Nucleus, matchingNodesProperties), 
									crossoverPlan.get(parent1Nucleus.getAdls().get(i).getBodyRoot())), 
							parent1Nucleus.getAdls().get(i).getExternalArgsTypes(), 
							parent1Nucleus.getAdls().get(i).getMaxCalls(),
							parent1Nucleus.getAdls().get(i).getMaxIterations()));
		}
		
		ArrayList<RPB> newRpbs = new ArrayList<RPB>(newRpbsRoots.size());
		for(int i = 0; i < newRpbsRoots.size(); i++){
			setLocalExternalNodes(rpbsLocalExternalNodes.get(i));
			setLocalInternalNodes(rpbsLocalInternalNodes.get(i));
			newRpbs.add(
					new RPB(
							parent1Nucleus.getRpbs().get(i).getId(),
							synthesizeBranch(
									newRpbsRoots.get(i), 
									selectCompatibleNodesInRpbs(i, parent1Nucleus, matchingNodesProperties), 
									crossoverPlan.get(parent1Nucleus.getRpbs().get(i).getRoot())),
							parent1Nucleus.getRpbs().get(i).getArgsTypes(), 
							parent1Nucleus.getRpbs().get(i).getArgsNames()));
		}
		
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
		
		return getArchitectureAlterationScheme().alterArchitecture(newNucleus, this);
	}
	
	
	private final ArrayList<AbstractNode> copyAdfsBranches(
			ArrayList<ADF> adfs, 
			HashMap<Integer,List<AbstractProxyNode>> adfsLocalExternalNodes, 
			HashMap<Integer,List<AbstractProxyNode>> adfsLocalInternalNodes, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			boolean hierarchicalAdfs){
		ArrayList<AbstractNode> newAdfsRoots = new ArrayList<AbstractNode>(adfs.size());
		List<ADFProxy> effectiveAdfsProxies = hierarchicalAdfs ? newAdfsProxies : Collections.emptyList();
		for(int i = 0; i < adfs.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					effectiveAdfsProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					Collections.emptyList(), 
					adfs.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			adfsLocalExternalNodes.put(i, localNodes.get(0));
			adfsLocalInternalNodes.put(i, localNodes.get(1));
			
			newAdfsRoots.add(replicateSubtree(adfs.get(i).getRoot()));
			newAdfsProxies.add(new ADFProxy(adfs.get(i)));
		}
		return newAdfsRoots;
	}
	private final ArrayList<AbstractNode> copyAdisBranches(
			ArrayList<ADI> adis, 
			HashMap<Integer,List<AbstractProxyNode>> adisLocalExternalNodes, 
			HashMap<Integer,List<AbstractProxyNode>> adisLocalInternalNodes, 
			ArrayList<ADIProxy> newAdisProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			boolean hierarchicalAdis){
		ArrayList<AbstractNode> newAdisRoots = new ArrayList<AbstractNode>(adis.size());
		List<ADIProxy> effectiveAdisProxies = hierarchicalAdis ? newAdisProxies : Collections.emptyList();
		for(int i = 0; i < adis.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					newAdfsProxies, 
					effectiveAdisProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					adis.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			adisLocalExternalNodes.put(i, localNodes.get(0));
			adisLocalInternalNodes.put(i, localNodes.get(1));
			
			newAdisRoots.add(replicateSubtree(adis.get(i).getRoot()));
			newAdisProxies.add(new ADIProxy(adis.get(i)));
		}
		return newAdisRoots;
	}
	private final ArrayList<ArrayList<AbstractNode>> copyAdrsBranches(
			ArrayList<ADR> adrs, 
			HashMap<Integer,ArrayList<List<AbstractProxyNode>>> adrsLocalExternalNodes, 
			HashMap<Integer,ArrayList<List<AbstractProxyNode>>> adrsLocalInternalNodes, 
			ArrayList<ADRProxy> newAdrsProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			boolean hierarchicalAdrs){
		ArrayList<ArrayList<AbstractNode>> newAdrsRoots = new ArrayList<ArrayList<AbstractNode>>(adrs.size());
		
		List<ADRProxy> effectiveAdrsProxies = hierarchicalAdrs ? newAdrsProxies : Collections.emptyList();
		for(int i = 0; i < adrs.size(); i++){
			ArrayList<List<AbstractProxyNode>> currentLocalExternalNodes = new ArrayList<List<AbstractProxyNode>>(2);
			ArrayList<List<AbstractProxyNode>> currentLocalInternalNodes = new ArrayList<List<AbstractProxyNode>>(2);
			
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					newAdfsProxies, 
					Collections.emptyList(), 
					effectiveAdrsProxies, 
					Collections.emptyList(), 
					adrs.get(i).getArgsTypes());
			currentLocalExternalNodes.add(localNodes.get(0));
			currentLocalInternalNodes.add(localNodes.get(1));
			
			List<List<AbstractProxyNode>> bodyLocalNodes = buildLocalNodes(
					Collections.emptyList(), 
					Collections.emptyList(), 
					Collections.emptyList(), 
					Arrays.asList(new ADRProxy(adrs.get(i))), 
					Collections.emptyList(), 
					Collections.emptyList());
			bodyLocalNodes.get(0).addAll(localNodes.get(0));
			bodyLocalNodes.get(1).addAll(localNodes.get(1));
			currentLocalExternalNodes.add(bodyLocalNodes.get(0));
			currentLocalInternalNodes.add(bodyLocalNodes.get(1));
			
			
			ArrayList<AbstractNode> currentAdrRoots = new ArrayList<AbstractNode>(3);
			
			setLocalExternalNodes(currentLocalExternalNodes.get(0));
			setLocalInternalNodes(currentLocalInternalNodes.get(0));
			currentAdrRoots.add(replicateSubtree(adrs.get(i).getConditionRoot()));
			
			setLocalExternalNodes(currentLocalExternalNodes.get(1));
			setLocalInternalNodes(currentLocalInternalNodes.get(1));
			currentAdrRoots.add(replicateSubtree(adrs.get(i).getBodyRoot()));
			
			setLocalExternalNodes(currentLocalExternalNodes.get(0));
			setLocalInternalNodes(currentLocalInternalNodes.get(0));
			currentAdrRoots.add(replicateSubtree(adrs.get(i).getGroundRoot()));
			
			
			adrsLocalExternalNodes.put(i, currentLocalExternalNodes);
			adrsLocalInternalNodes.put(i, currentLocalInternalNodes);
			
			newAdrsRoots.add(currentAdrRoots);
			
			newAdrsProxies.add(new ADRProxy(adrs.get(i)));
		}
		
		return newAdrsRoots;
	}
	private final ArrayList<ArrayList<AbstractNode>> copyAdlsBranches(
			ArrayList<ADL> adls, 
			HashMap<Integer,List<AbstractProxyNode>> adlsLocalExternalNodes, 
			HashMap<Integer,List<AbstractProxyNode>> adlsLocalInternalNodes, 
			ArrayList<ADLProxy> newAdlsProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies, 
			boolean hierarchicalAdls){
		ArrayList<ArrayList<AbstractNode>> newAdlsRoots = new ArrayList<ArrayList<AbstractNode>>(adls.size());
		List<ADLProxy> effectiveAdlsProxies = hierarchicalAdls ? newAdlsProxies : Collections.emptyList();
		for(int i = 0; i < adls.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					newAdfsProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					effectiveAdlsProxies, 
					adls.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			adlsLocalExternalNodes.put(i, localNodes.get(0));
			adlsLocalInternalNodes.put(i, localNodes.get(1));
			
			ArrayList<AbstractNode> currentAdlRoots = new ArrayList<AbstractNode>(3);
			currentAdlRoots.add(replicateSubtree(adls.get(i).getInitializationRoot()));
			currentAdlRoots.add(replicateSubtree(adls.get(i).getConditionRoot()));
			currentAdlRoots.add(replicateSubtree(adls.get(i).getBodyRoot()));
			
			newAdlsRoots.add(currentAdlRoots);
			newAdlsProxies.add(new ADLProxy(adls.get(i)));
		}
		return newAdlsRoots;
	}
	private final ArrayList<AbstractNode> copyRpbsBranches(
			ArrayList<RPB> rpbs, 
			HashMap<Integer,List<AbstractProxyNode>> rpbsLocalExternalNodes, 
			HashMap<Integer,List<AbstractProxyNode>> rpbsLocalInternalNodes, 
			ArrayList<ADLProxy> newAdlsProxies, 
			ArrayList<ADRProxy> newAdrsProxies, 
			ArrayList<ADIProxy> newAdisProxies, 
			ArrayList<ADFProxy> newAdfsProxies, 
			ArrayList<ADSProxy> newAdssProxies){
		ArrayList<AbstractNode> newRpbsRoots = new ArrayList<AbstractNode>(rpbs.size());
		for(int i = 0; i < rpbs.size(); i++){
			List<List<AbstractProxyNode>> localNodes = buildLocalNodes(
					newAdssProxies, 
					newAdfsProxies, 
					newAdisProxies, 
					newAdrsProxies, 
					newAdlsProxies, 
					rpbs.get(i).getArgsTypes());
			
			setLocalExternalNodes(localNodes.get(0));
			setLocalInternalNodes(localNodes.get(1));
			
			rpbsLocalExternalNodes.put(i, localNodes.get(0));
			rpbsLocalInternalNodes.put(i, localNodes.get(1));
			
			newRpbsRoots.add(replicateSubtree(rpbs.get(i).getRoot()));
		}
		return newRpbsRoots;
	}
	
	
	private final AbstractNode synthesizeBranch(AbstractNode currentBranchRoot, ArrayList<AbstractNode> compatibleNodesList, int nCrossoverNodes){
		if(nCrossoverNodes > 0){
			List<AbstractNode> branch1CrossoverNodes = getCrossoverNodesSelectionScheme().chooseFirstBranchCrossoverNodes(
					getDescendantNodes(currentBranchRoot), 
					nCrossoverNodes, 
					this);
			
			Map<AbstractNode,AbstractNode> crossoverNodesMap = getCrossoverNodesSelectionScheme().chooseMatchingCrossoverNodes(
					branch1CrossoverNodes, 
					compatibleNodesList, 
					this);
			
			for(Entry<AbstractNode,AbstractNode> crossoverNodesPair : crossoverNodesMap.entrySet()){
				AbstractNode crossoverNode1 = crossoverNodesPair.getKey();
				AbstractNode crossoverNode2 = crossoverNodesPair.getValue();
				
				if(crossoverNode1.getParent() != null)
					crossoverNode1.getParent().replaceArg(crossoverNode1, crossoverNode2);
				else{
					crossoverNode2.setParent(null);
					currentBranchRoot = crossoverNode2;
				}
			}
		}
		return getMutationScheme().mutateBranch(currentBranchRoot, this);
	}
	
	
	private final HashMap<AbstractNode,ArrayList<AbstractProxyNode>> buildNodesProperties(Nucleus nucleus){
		HashMap<AbstractNode,ArrayList<AbstractProxyNode>> nodesProperties = new HashMap<AbstractNode,ArrayList<AbstractProxyNode>>();
		for(AbstractModule module : nucleus.getAllModules()){
			for(AbstractNode root : module.getBranchRoots())
				buildNodeProperties(root, nodesProperties);
		}
		return nodesProperties;
	}
	private final ArrayList<AbstractProxyNode> buildNodeProperties(AbstractNode node, HashMap<AbstractNode,ArrayList<AbstractProxyNode>> nodesProperties){
		ArrayList<AbstractProxyNode> nodeProperties = new ArrayList<AbstractProxyNode>();
		
		HashSet<Integer> adsIds = new HashSet<Integer>();
		HashSet<Integer> adfIds = new HashSet<Integer>();
		HashSet<Integer> adiIds = new HashSet<Integer>();
		HashSet<Integer> adrIds = new HashSet<Integer>();
		HashSet<Integer> adlIds = new HashSet<Integer>();
		HashSet<Integer> argIds = new HashSet<Integer>();
		
		for(AbstractNode arg : node.getArgs()){
			ArrayList<AbstractProxyNode> argProperties = buildNodeProperties(arg, nodesProperties);
			mergeProperties(nodeProperties, argProperties, adsIds, adfIds, adiIds, adrIds, adlIds, argIds);
		}
		
		if(node instanceof AbstractProxyNode){
			AbstractProxyNode castedNode = (AbstractProxyNode) node;
			mergeProperties(nodeProperties, Arrays.asList(castedNode), adsIds, adfIds, adiIds, adrIds, adlIds, argIds);
		}
		
		nodesProperties.put(node, nodeProperties);
		
		return nodeProperties;
	}
	private final void mergeProperties(
			ArrayList<AbstractProxyNode> nodeProperties, 
			List<AbstractProxyNode> otherNodeProperties,
			HashSet<Integer> adsIds, 
			HashSet<Integer> adfIds, 
			HashSet<Integer> adiIds, 
			HashSet<Integer> adrIds, 
			HashSet<Integer> adlIds, 
			HashSet<Integer> argIds){
		for(AbstractProxyNode property : otherNodeProperties){
			if(property instanceof ADSProxy){
				if(!adsIds.contains(property.getId())){
					adsIds.add(property.getId());
					nodeProperties.add(property);
				}
			} else if(property instanceof ADFProxy){
				if(!adfIds.contains(property.getId())){
					adfIds.add(property.getId());
					nodeProperties.add(property);
				}
			} else if(property instanceof ADIProxy){
				if(!adiIds.contains(property.getId())){
					adiIds.add(property.getId());
					nodeProperties.add(property);
				}
			} else if(property instanceof ADRProxy){
				if(!adrIds.contains(property.getId())){
					adrIds.add(property.getId());
					nodeProperties.add(property);
				}
			} else if(property instanceof ADLProxy){
				if(!adlIds.contains(property.getId())){
					adlIds.add(property.getId());
					nodeProperties.add(property);
				}
			} else if(property instanceof ArgumentProxy){
				if(!argIds.contains(property.getId())){
					argIds.add(property.getId());
					nodeProperties.add(property);
				}
			} else
				throw new RuntimeException("Unknown subclass of AbstractProxyNode!");
		}
	}
	
	
	// Potential improvement : if a node is compatible, all its subtree is.
	private final ArrayList<AbstractNode> selectCompatibleNodesInAdfs(
			int branchIndex, 
			Nucleus nucleus, 
			HashMap<AbstractNode,ArrayList<AbstractProxyNode>> nodesProperties){
		ArrayList<AbstractNode> compatibleNodes = new ArrayList<AbstractNode>();
		for(Entry<AbstractNode,ArrayList<AbstractProxyNode>> nodeProperties : nodesProperties.entrySet()){
			boolean isCompatible = true;
			for(AbstractProxyNode property : nodeProperties.getValue()){
				if(property instanceof ADSProxy){
					ADSProxy adsProxy = (ADSProxy) property;
					if(adsProxy.getId() >= nucleus.getAdss().size()){
						isCompatible = false;
						break;
					} else if(!adsProxy.getAds().getReturnType().isTheSameAs(nucleus.getAdss().get(adsProxy.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(adsProxy.getAds().getDimension() != nucleus.getAdss().get(adsProxy.getId()).getDimension()){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADFProxy){
					if(!nucleus.isHierarchicalAdfs()){
						isCompatible = false;
						break;
					} else if(property.getId() >= branchIndex){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdfs().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdfs().get(property.getId()).getArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADIProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ADRProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ADLProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ArgumentProxy){
					if(property.getId() >= nucleus.getAdfs().get(branchIndex).getArgs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdfs().get(branchIndex).getArgsTypes().get(property.getId()))){
						isCompatible = false;
						break;
					}
				} else
					throw new RuntimeException("Unknown subclass of AbstractProxyNode!");
			}
			if(isCompatible)
				compatibleNodes.add(nodeProperties.getKey());
		}
		return compatibleNodes;
	}
	// Potential improvement : if a node is compatible, all its subtree is.
	private final ArrayList<AbstractNode> selectCompatibleNodesInAdis(
			int branchIndex, 
			Nucleus nucleus, 
			HashMap<AbstractNode,ArrayList<AbstractProxyNode>> nodesProperties){
		ArrayList<AbstractNode> compatibleNodes = new ArrayList<AbstractNode>();
		for(Entry<AbstractNode,ArrayList<AbstractProxyNode>> nodeProperties : nodesProperties.entrySet()){
			boolean isCompatible = true;
			for(AbstractProxyNode property : nodeProperties.getValue()){
				if(property instanceof ADSProxy){
					ADSProxy adsProxy = (ADSProxy) property;
					if(adsProxy.getId() >= nucleus.getAdss().size()){
						isCompatible = false;
						break;
					} else if(!adsProxy.getAds().getReturnType().isTheSameAs(nucleus.getAdss().get(adsProxy.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(adsProxy.getAds().getDimension() != nucleus.getAdss().get(adsProxy.getId()).getDimension()){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADFProxy){
					if(property.getId() >= nucleus.getAdfs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdfs().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdfs().get(property.getId()).getArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADIProxy) {
					if(!nucleus.isHierarchicalAdis()){
						isCompatible = false;
						break;
					} else if(property.getId() >= branchIndex){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdis().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdis().get(property.getId()).getExternalArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADRProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ADLProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ArgumentProxy){
					if(property.getId() >= nucleus.getAdis().get(branchIndex).getArgs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdis().get(branchIndex).getArgsTypes().get(property.getId()))){
						isCompatible = false;
						break;
					}
				} else
					throw new RuntimeException("Unknown subclass of AbstractProxyNode!");
			}
			if(isCompatible)
				compatibleNodes.add(nodeProperties.getKey());
		}
		return compatibleNodes;
	}
	// Potential improvement : if a node is compatible, all its subtree is.
	private final ArrayList<AbstractNode> selectCompatibleNodesInAdrs(
			int branchIndex, 
			Nucleus nucleus, 
			HashMap<AbstractNode,ArrayList<AbstractProxyNode>> nodesProperties,
			boolean isBody){
		ArrayList<AbstractNode> compatibleNodes = new ArrayList<AbstractNode>();
		for(Entry<AbstractNode,ArrayList<AbstractProxyNode>> nodeProperties : nodesProperties.entrySet()){
			boolean isCompatible = true;
			for(AbstractProxyNode property : nodeProperties.getValue()){
				if(property instanceof ADSProxy){
					ADSProxy adsProxy = (ADSProxy) property;
					if(adsProxy.getId() >= nucleus.getAdss().size()){
						isCompatible = false;
						break;
					} else if(!adsProxy.getAds().getReturnType().isTheSameAs(nucleus.getAdss().get(adsProxy.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(adsProxy.getAds().getDimension() != nucleus.getAdss().get(adsProxy.getId()).getDimension()){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADFProxy){
					if(property.getId() >= nucleus.getAdfs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdfs().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdfs().get(property.getId()).getArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADIProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ADRProxy) {
					if(property.getId() == branchIndex){
						if(!isBody){
							isCompatible = false;
							break;
						}
					} else {
						if(!nucleus.isHierarchicalAdrs()){
							isCompatible = false;
							break;
						} else if(property.getId() >= branchIndex){
							isCompatible = false;
							break;
						}
					}
					if(!property.getReturnType().isTheSameAs(nucleus.getAdrs().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdrs().get(property.getId()).getArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADLProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ArgumentProxy){
					if(property.getId() >= nucleus.getAdrs().get(branchIndex).getArgs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdrs().get(branchIndex).getArgsTypes().get(property.getId()))){
						isCompatible = false;
						break;
					}
				} else
					throw new RuntimeException("Unknown subclass of AbstractProxyNode!");
			}
			if(isCompatible)
				compatibleNodes.add(nodeProperties.getKey());
		}
		return compatibleNodes;
	}
	// Potential improvement : if a node is compatible, all its subtree is.
	private final ArrayList<AbstractNode> selectCompatibleNodesInAdls(
			int branchIndex, 
			Nucleus nucleus, 
			HashMap<AbstractNode,ArrayList<AbstractProxyNode>> nodesProperties){
		ArrayList<AbstractNode> compatibleNodes = new ArrayList<AbstractNode>();
		for(Entry<AbstractNode,ArrayList<AbstractProxyNode>> nodeProperties : nodesProperties.entrySet()){
			boolean isCompatible = true;
			for(AbstractProxyNode property : nodeProperties.getValue()){
				if(property instanceof ADSProxy){
					ADSProxy adsProxy = (ADSProxy) property;
					if(adsProxy.getId() >= nucleus.getAdss().size()){
						isCompatible = false;
						break;
					} else if(!adsProxy.getAds().getReturnType().isTheSameAs(nucleus.getAdss().get(adsProxy.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(adsProxy.getAds().getDimension() != nucleus.getAdss().get(adsProxy.getId()).getDimension()){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADFProxy){
					if(property.getId() >= nucleus.getAdfs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdfs().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdfs().get(property.getId()).getArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADIProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ADRProxy) {
					isCompatible = false;
					break;
				} else if(property instanceof ADLProxy) {
					if(!nucleus.isHierarchicalAdls()){
						isCompatible = false;
						break;
					} else if(property.getId() >= branchIndex){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdls().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdls().get(property.getId()).getExternalArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ArgumentProxy){
					if(property.getId() >= nucleus.getAdls().get(branchIndex).getArgs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdls().get(branchIndex).getArgsTypes().get(property.getId()))){
						isCompatible = false;
						break;
					}
				} else
					throw new RuntimeException("Unknown subclass of AbstractProxyNode!");
			}
			if(isCompatible)
				compatibleNodes.add(nodeProperties.getKey());
		}
		return compatibleNodes;
	}
	// Potential improvement : if a node is compatible, all its subtree is.
	private final ArrayList<AbstractNode> selectCompatibleNodesInRpbs(
			int branchIndex, 
			Nucleus nucleus, 
			HashMap<AbstractNode,ArrayList<AbstractProxyNode>> nodesProperties){
		ArrayList<AbstractNode> compatibleNodes = new ArrayList<AbstractNode>();
		for(Entry<AbstractNode,ArrayList<AbstractProxyNode>> nodeProperties : nodesProperties.entrySet()){
			boolean isCompatible = true;
			for(AbstractProxyNode property : nodeProperties.getValue()){
				if(property instanceof ADSProxy){
					ADSProxy adsProxy = (ADSProxy) property;
					if(adsProxy.getId() >= nucleus.getAdss().size()){
						isCompatible = false;
						break;
					} else if(!adsProxy.getAds().getReturnType().isTheSameAs(nucleus.getAdss().get(adsProxy.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(adsProxy.getAds().getDimension() != nucleus.getAdss().get(adsProxy.getId()).getDimension()){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADFProxy){
					if(property.getId() >= nucleus.getAdfs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdfs().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdfs().get(property.getId()).getArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADIProxy){
					if(property.getId() >= nucleus.getAdis().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdis().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdis().get(property.getId()).getExternalArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADRProxy){
					if(property.getId() >= nucleus.getAdrs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdrs().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdrs().get(property.getId()).getArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ADLProxy){
					if(property.getId() >= nucleus.getAdls().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getAdls().get(property.getId()).getReturnType())){
						isCompatible = false;
						break;
					} else if(!property.argsTypesMatchRequestedTypes(nucleus.getAdls().get(property.getId()).getExternalArgsTypes())){
						isCompatible = false;
						break;
					}
				} else if(property instanceof ArgumentProxy){
					if(property.getId() >= nucleus.getRpbs().get(branchIndex).getArgs().size()){
						isCompatible = false;
						break;
					} else if(!property.getReturnType().isTheSameAs(nucleus.getRpbs().get(branchIndex).getArgsTypes().get(property.getId()))){
						isCompatible = false;
						break;
					}
				} else
					throw new RuntimeException("Unknown subclass of AbstractProxyNode!");
			}
			if(isCompatible)
				compatibleNodes.add(nodeProperties.getKey());
		}
		return compatibleNodes;
	}
	
	
	@Override
	public final PointTypingCrossover copy() {
		return new PointTypingCrossover(
				getCrossoverPlanningScheme(), 
				getCrossoverNodesSelectionScheme(), 
				getMutationScheme(), 
				getArchitectureAlterationScheme(), 
				getGlobalExternalNodes(),
				getGlobalInternalNodes());
	}
}
