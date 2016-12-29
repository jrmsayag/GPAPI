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
 * EvolvedIndividualBuilder.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.Cell;
import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
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
import com.gpapi.individuals.cells.nucleuses.nodes.Constant;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class EvolvedIndividualBuilder implements Serializable {
	
	private static final long serialVersionUID = 4294228995886326936L;
	
	private final GeneticOperatorInterface geneticOperator;
	public final GeneticOperatorInterface getGeneticOperator() {
		return geneticOperator;
	}
	
	private int minInitRpbsSize = 5;
	public final int getMinInitRpbsSize() {
		return minInitRpbsSize;
	}
	public final EvolvedIndividualBuilder setMinInitRpbsSize(int minInitRpbsSize) {
		this.minInitRpbsSize = minInitRpbsSize;
		maxInitRpbsSize = Math.max(minInitRpbsSize, maxInitRpbsSize);
		return this;
	}
	
	private int maxInitRpbsSize = 15;
	public final int getMaxInitRpbsSize() {
		return maxInitRpbsSize;
	}
	public final EvolvedIndividualBuilder setMaxInitRpbsSize(int maxInitRpbsSize) {
		this.maxInitRpbsSize = maxInitRpbsSize;
		minInitRpbsSize = Math.min(maxInitRpbsSize, minInitRpbsSize);
		return this;
	}

	public final int getInitRpbsSize() {
		return ThreadLocalRandom.current().nextInt(minInitRpbsSize, maxInitRpbsSize+1);
	}
	public final EvolvedIndividualBuilder setInitRpbsSize(int initRpbsSize) {
		this.minInitRpbsSize = initRpbsSize;
		this.maxInitRpbsSize = initRpbsSize;
		return this;
	}
	
	private List<AbstractType> rpbsTypes = Arrays.asList(RealValue.create());
	public final List<AbstractType> getRpbsTypes() {
		return rpbsTypes;
	}
	public final EvolvedIndividualBuilder setRpbsTypes(List<AbstractType> rpbsTypes) {
		this.rpbsTypes = rpbsTypes;
		return this;
	}
	
	private List<List<AbstractType>> rpbsArgsTypes = Arrays.asList(Collections.emptyList());
	public final List<List<AbstractType>> getRpbsArgsTypes() {
		return rpbsArgsTypes;
	}
	public final EvolvedIndividualBuilder setRpbsArgsTypes(List<List<AbstractType>> rpbsArgsTypes) {
		this.rpbsArgsTypes = rpbsArgsTypes;
		return this;
	}

	private List<List<String>> rpbsArgsNames = Arrays.asList(Collections.emptyList());
	public final List<List<String>> getRpbsArgsNames() {
		return rpbsArgsNames;
	}
	public final EvolvedIndividualBuilder setRpbsArgsNames(List<List<String>> rpbsArgsNames) {
		this.rpbsArgsNames = rpbsArgsNames;
		return this;
	}
	
	private int minInitAdlsSize = 5;
	public final int getMinInitAdlsSize() {
		return minInitAdlsSize;
	}
	public final EvolvedIndividualBuilder setMinInitAdlsSize(int minInitAdlsSize) {
		this.minInitAdlsSize = minInitAdlsSize;
		maxInitAdlsSize = Math.max(minInitAdlsSize, maxInitAdlsSize);
		return this;
	}
	
	private int maxInitAdlsSize = 15;
	public final int getMaxInitAdlsSize() {
		return maxInitAdlsSize;
	}
	public final EvolvedIndividualBuilder setMaxInitAdlsSize(int maxInitAdlsSize) {
		this.maxInitAdlsSize = maxInitAdlsSize;
		minInitAdlsSize = Math.min(maxInitAdlsSize, minInitAdlsSize);
		return this;
	}

	public final int getInitAdlsSize() {
		return ThreadLocalRandom.current().nextInt(minInitAdlsSize, maxInitAdlsSize+1);
	}
	public final EvolvedIndividualBuilder setInitAdlsSize(int initAdlsSize) {
		this.minInitAdlsSize = initAdlsSize;
		this.maxInitAdlsSize = initAdlsSize;
		return this;
	}
	
	private List<AbstractType> adlsInitializationTypes = Collections.emptyList();
	public final List<AbstractType> getAdlsInitializationTypes() {
		return adlsInitializationTypes;
	}
	public final EvolvedIndividualBuilder setAdlsInitializationTypes(List<AbstractType> adlsInitializationTypes) {
		this.adlsInitializationTypes = adlsInitializationTypes;
		return this;
	}
	
	private List<AbstractType> adlsBodyTypes = Collections.emptyList();
	public final List<AbstractType> getAdlsBodyTypes() {
		return adlsBodyTypes;
	}
	public final EvolvedIndividualBuilder setAdlsBodyTypes(List<AbstractType> adlsBodyTypes) {
		this.adlsBodyTypes = adlsBodyTypes;
		return this;
	}
	
	private List<List<AbstractType>> adlsArgsTypes = Collections.emptyList();
	public final List<List<AbstractType>> getAdlsArgsTypes() {
		return adlsArgsTypes;
	}
	public final EvolvedIndividualBuilder setAdlsArgsTypes(List<List<AbstractType>> adlsArgsTypes) {
		this.adlsArgsTypes = adlsArgsTypes;
		return this;
	}
	
	private List<Integer> adlsMaxCalls = Collections.emptyList();
	public final List<Integer> getAdlsMaxCalls() {
		return adlsMaxCalls;
	}
	public final EvolvedIndividualBuilder setAdlsMaxCalls(List<Integer> adlsMaxCalls) {
		this.adlsMaxCalls = adlsMaxCalls;
		return this;
	}
	
	private List<Integer> adlsMaxIterations = Collections.emptyList();
	public final List<Integer> getAdlsMaxIterations() {
		return adlsMaxIterations;
	}
	public final EvolvedIndividualBuilder setAdlsMaxIterations(List<Integer> adlsMaxIterations) {
		this.adlsMaxIterations = adlsMaxIterations;
		return this;
	}
	
	private boolean hierarchicalAdls = false;
	public final boolean isHierarchicalAdls() {
		return hierarchicalAdls;
	}
	public final EvolvedIndividualBuilder setHierarchicalAdls(boolean hierarchicalAdls) {
		this.hierarchicalAdls = hierarchicalAdls;
		return this;
	}
	
	private int minInitAdrsSize = 5;
	public final int getMinInitAdrsSize() {
		return minInitAdrsSize;
	}
	public final EvolvedIndividualBuilder setMinInitAdrsSize(int minInitAdrsSize) {
		this.minInitAdrsSize = minInitAdrsSize;
		maxInitAdrsSize = Math.max(minInitAdrsSize, maxInitAdrsSize);
		return this;
	}
	
	private int maxInitAdrsSize = 15;
	public final int getMaxInitAdrsSize() {
		return maxInitAdrsSize;
	}
	public final EvolvedIndividualBuilder setMaxInitAdrsSize(int maxInitAdrsSize) {
		this.maxInitAdrsSize = maxInitAdrsSize;
		minInitAdrsSize = Math.min(maxInitAdrsSize, minInitAdrsSize);
		return this;
	}

	public final int getInitAdrsSize() {
		return ThreadLocalRandom.current().nextInt(minInitAdrsSize, maxInitAdrsSize+1);
	}
	public final EvolvedIndividualBuilder setInitAdrsSize(int initAdrsSize) {
		this.minInitAdrsSize = initAdrsSize;
		this.maxInitAdrsSize = initAdrsSize;
		return this;
	}
	
	private List<AbstractType> adrsTypes = Collections.emptyList();
	public final List<AbstractType> getAdrsTypes() {
		return adrsTypes;
	}
	public final EvolvedIndividualBuilder setAdrsTypes(List<AbstractType> adrsTypes) {
		this.adrsTypes = adrsTypes;
		return this;
	}
	
	private List<List<AbstractType>> adrsArgsTypes = Collections.emptyList();
	public final List<List<AbstractType>> getAdrsArgsTypes() {
		return adrsArgsTypes;
	}
	public final EvolvedIndividualBuilder setAdrsArgsTypes(List<List<AbstractType>> adrsArgsTypes) {
		this.adrsArgsTypes = adrsArgsTypes;
		return this;
	}
	
	private List<Integer> adrsMaxCalls = Collections.emptyList();
	public final List<Integer> getAdrsMaxCalls() {
		return adrsMaxCalls;
	}
	public final EvolvedIndividualBuilder setAdrsMaxCalls(List<Integer> adrsMaxCalls) {
		this.adrsMaxCalls = adrsMaxCalls;
		return this;
	}
	
	private List<Integer> adrsMaxRecursionDepths = Collections.emptyList();
	public final List<Integer> getAdrsMaxRecursionDepths() {
		return adrsMaxRecursionDepths;
	}
	public final EvolvedIndividualBuilder setAdrsMaxRecursionDepths(List<Integer> adrsMaxRecursionDepths) {
		this.adrsMaxRecursionDepths = adrsMaxRecursionDepths;
		return this;
	}
	
	private boolean hierarchicalAdrs = false;
	public final boolean isHierarchicalAdrs() {
		return hierarchicalAdrs;
	}
	public final EvolvedIndividualBuilder setHierarchicalAdrs(boolean hierarchicalAdrs) {
		this.hierarchicalAdrs = hierarchicalAdrs;
		return this;
	}

	private int minInitAdisSize = 5;
	public final int getMinInitAdisSize() {
		return minInitAdisSize;
	}
	public final EvolvedIndividualBuilder setMinInitAdisSize(int minInitAdisSize) {
		this.minInitAdisSize = minInitAdisSize;
		maxInitAdisSize = Math.max(minInitAdisSize, maxInitAdisSize);
		return this;
	}
	
	private int maxInitAdisSize = 15;
	public final int getMaxInitAdisSize() {
		return maxInitAdisSize;
	}
	public final EvolvedIndividualBuilder setMaxInitAdisSize(int maxInitAdisSize) {
		this.maxInitAdisSize = maxInitAdisSize;
		minInitAdisSize = Math.min(maxInitAdisSize, minInitAdisSize);
		return this;
	}

	public final int getInitAdisSize() {
		return ThreadLocalRandom.current().nextInt(minInitAdisSize, maxInitAdisSize+1);
	}
	public final EvolvedIndividualBuilder setInitAdisSize(int initAdisSize) {
		this.minInitAdisSize = initAdisSize;
		this.maxInitAdisSize = initAdisSize;
		return this;
	}
	
	private List<AbstractType> adisTypes = Collections.emptyList();
	public final List<AbstractType> getAdisTypes() {
		return adisTypes;
	}
	public final EvolvedIndividualBuilder setAdisTypes(List<AbstractType> adisTypes) {
		this.adisTypes = adisTypes;
		return this;
	}
	
	private List<List<AbstractType>> adisExternalArgsTypes = Collections.emptyList();
	public final List<List<AbstractType>> getAdisExternalArgsTypes() {
		return adisExternalArgsTypes;
	}
	public final EvolvedIndividualBuilder setAdisExternalArgsTypes(List<List<AbstractType>> adisExternalArgsTypes) {
		this.adisExternalArgsTypes = adisExternalArgsTypes;
		return this;
	}
	
	private List<Integer> adisCollectionIds = Collections.emptyList();
	public final List<Integer> getAdisCollectionIds() {
		return adisCollectionIds;
	}
	public final EvolvedIndividualBuilder setAdisCollectionIds(List<Integer> adisCollectionIds) {
		this.adisCollectionIds = adisCollectionIds;
		return this;
	}
	
	private List<List<AbstractType>> adisFieldsTypes = Collections.emptyList();
	public final List<List<AbstractType>> getAdisFieldsTypes() {
		return adisFieldsTypes;
	}
	public final EvolvedIndividualBuilder setAdisFieldsTypes(List<List<AbstractType>> adisFieldsTypes) {
		this.adisFieldsTypes = adisFieldsTypes;
		return this;
	}
	
	private List<List<String>> adisFieldsNames = Collections.emptyList();
	public final List<List<String>> getAdisFieldsNames() {
		return adisFieldsNames;
	}
	public final EvolvedIndividualBuilder setAdisFieldsNames(List<List<String>> adisFieldsNames) {
		this.adisFieldsNames = adisFieldsNames;
		return this;
	}
	
	private List<Integer> adisMaxCalls = Collections.emptyList();
	public final List<Integer> getAdisMaxCalls() {
		return adisMaxCalls;
	}
	public final EvolvedIndividualBuilder setAdisMaxCalls(List<Integer> adisMaxCalls) {
		this.adisMaxCalls = adisMaxCalls;
		return this;
	}
	
	private boolean hierarchicalAdis = false;
	public final boolean isHierarchicalAdis() {
		return hierarchicalAdis;
	}
	public final EvolvedIndividualBuilder setHierarchicalAdis(boolean hierarchicalAdis) {
		this.hierarchicalAdis = hierarchicalAdis;
		return this;
	}
	
	private int minInitAdfsSize = 5;
	public final int getMinInitAdfsSize() {
		return minInitAdfsSize;
	}
	public final EvolvedIndividualBuilder setMinInitAdfsSize(int minInitAdfsSize) {
		this.minInitAdfsSize = minInitAdfsSize;
		maxInitAdfsSize = Math.max(minInitAdfsSize, maxInitAdfsSize);
		return this;
	}
	
	private int maxInitAdfsSize = 15;
	public final int getMaxInitAdfsSize() {
		return maxInitAdfsSize;
	}
	public final EvolvedIndividualBuilder setMaxInitAdfsSize(int maxInitAdfsSize) {
		this.maxInitAdfsSize = maxInitAdfsSize;
		minInitAdfsSize = Math.min(maxInitAdfsSize, minInitAdfsSize);
		return this;
	}

	public final int getInitAdfsSize() {
		return ThreadLocalRandom.current().nextInt(minInitAdfsSize, maxInitAdfsSize+1);
	}
	public final EvolvedIndividualBuilder setInitAdfsSize(int initAdfsSize) {
		this.minInitAdfsSize = initAdfsSize;
		this.maxInitAdfsSize = initAdfsSize;
		return this;
	}
	
	private List<AbstractType> adfsTypes = Collections.emptyList();
	public final List<AbstractType> getAdfsTypes() {
		return adfsTypes;
	}
	public final EvolvedIndividualBuilder setAdfsTypes(List<AbstractType> adfsTypes) {
		this.adfsTypes = adfsTypes;
		return this;
	}
	
	private List<List<AbstractType>> adfsArgsTypes = Collections.emptyList();
	public final List<List<AbstractType>> getAdfsArgsTypes() {
		return adfsArgsTypes;
	}
	public final EvolvedIndividualBuilder setAdfsArgsTypes(List<List<AbstractType>> adfsArgsTypes) {
		this.adfsArgsTypes = adfsArgsTypes;
		return this;
	}
	
	private boolean hierarchicalAdfs = false;
	public final boolean isHierarchicalAdfs() {
		return hierarchicalAdfs;
	}
	public final EvolvedIndividualBuilder setHierarchicalAdfs(boolean hierarchicalAdfs) {
		this.hierarchicalAdfs = hierarchicalAdfs;
		return this;
	}
	
	private List<ADS.Type> adssStorageTypes = Collections.emptyList();
	public final List<ADS.Type> getAdssStorageTypes() {
		return adssStorageTypes;
	}
	public final EvolvedIndividualBuilder setAdssStorageTypes(List<ADS.Type> adssStorageTypes) {
		this.adssStorageTypes = adssStorageTypes;
		return this;
	}
	
	private List<AbstractType> adssDataTypes = Collections.emptyList();
	public final List<AbstractType> getAdssDataTypes() {
		return adssDataTypes;
	}
	public final EvolvedIndividualBuilder setAdssDataTypes(List<AbstractType> adssDataTypes) {
		this.adssDataTypes = adssDataTypes;
		return this;
	}
	
	private List<Integer> adssMaxSizes = Collections.emptyList();
	public final List<Integer> getAdssMaxSizes() {
		return adssMaxSizes;
	}
	public final EvolvedIndividualBuilder setAdssMaxSizes(List<Integer> adssMaxSizes) {
		this.adssMaxSizes = adssMaxSizes;
		return this;
	}
	
	
	
	private EvolvedIndividualBuilder(GeneticOperatorInterface geneticOperator){
		if(geneticOperator == null)
			throw new NullPointerException("geneticOperator can't be null!");
		else
			this.geneticOperator = geneticOperator;
	}
	public static final EvolvedIndividualBuilder create(GeneticOperatorInterface geneticOperator){
		return new EvolvedIndividualBuilder(geneticOperator);
	}
	
	
	
	private static final Nucleus newNucleus(
			GeneticOperatorInterface geneticOperator, 
			int initRpbsSize,
			List<AbstractType> rpbsTypes,
			List<List<AbstractType>> rpbsArgsTypes, 
			List<List<String>> rpbsArgsNames, 
			int initAdlsSize, 
			List<AbstractType> adlsInitializationTypes, 
			List<AbstractType> adlsBodyTypes, 
			List<List<AbstractType>> adlsArgsTypes, 
			List<Integer> adlsMaxCalls, 
			List<Integer> adlsMaxIterations, 
			boolean hierarchicalAdls, 
			int initAdrsSize, 
			List<AbstractType> adrsTypes, 
			List<List<AbstractType>> adrsArgsTypes, 
			List<Integer> adrsMaxCalls, 
			List<Integer> adrsMaxRecursionDepths, 
			boolean hierarchicalAdrs, 
			int initAdisSize, 
			List<AbstractType> adisTypes, 
			List<List<AbstractType>> adisExternalArgsTypes, 
			List<Integer> adisCollectionIds, 
			List<List<AbstractType>> adisFieldsTypes, 
			List<List<String>> adisFieldsNames, 
			List<Integer> adisMaxCalls, 
			boolean hierarchicalAdis, 
			int initAdfsSize, 
			List<AbstractType> adfsTypes,
			List<List<AbstractType>> adfsArgsTypes,
			boolean hierarchicalAdfs,
			List<ADS.Type> adssStorageTypes, 
			List<AbstractType> adssDataTypes, 
			List<Integer> adssMaxSizes){
		List<ADSProxy> adssProxies = new ArrayList<ADSProxy>(adssStorageTypes.size());
		List<ADS> newAdss = createNewAdss(
				adssStorageTypes, 
				adssDataTypes, 
				adssProxies, 
				adssMaxSizes);
		
		List<ADFProxy> adfsProxies = new ArrayList<ADFProxy>(adfsTypes.size());
		List<ADF> newAdfs = createNewAdfs(
				geneticOperator, 
				adfsTypes, 
				adfsArgsTypes, 
				adfsProxies, 
				adssProxies, 
				initAdfsSize, 
				hierarchicalAdfs);
		
		List<ADIProxy> adisProxies = new ArrayList<ADIProxy>(adisTypes.size());
		List<ADI> newAdis = createNewAdis(
				geneticOperator, 
				adisTypes, 
				adisExternalArgsTypes, 
				adisCollectionIds, 
				adisFieldsTypes, 
				adisFieldsNames, 
				adisMaxCalls,
				adisProxies, 
				adfsProxies, 
				adssProxies, 
				initAdisSize, 
				hierarchicalAdis);
		
		List<ADRProxy> adrsProxies = new ArrayList<ADRProxy>(adrsTypes.size());
		List<ADR> newAdrs = createNewAdrs(
				geneticOperator, 
				adrsTypes, 
				adrsArgsTypes, 
				adrsMaxCalls, 
				adrsMaxRecursionDepths, 
				adrsProxies, 
				adfsProxies, 
				adssProxies, 
				initAdrsSize, 
				hierarchicalAdrs);
		
		List<ADLProxy> adlsProxies = new ArrayList<ADLProxy>(adlsBodyTypes.size());
		List<ADL> newAdls = createNewAdls(
				geneticOperator, 
				adlsInitializationTypes, 
				adlsBodyTypes, 
				adlsArgsTypes, 
				adlsMaxCalls, 
				adlsMaxIterations, 
				adlsProxies, 
				adfsProxies, 
				adssProxies, 
				initAdlsSize, 
				hierarchicalAdls);
		
		List<RPB> newRpbs = createNewRpbs(
				geneticOperator, 
				rpbsTypes,
				rpbsArgsTypes,
				rpbsArgsNames, 
				adlsProxies, 
				adrsProxies, 
				adisProxies, 
				adfsProxies, 
				adssProxies, 
				initRpbsSize);
		
		return new Nucleus(
				geneticOperator, 
				newRpbs, 
				newAdls,
				hierarchicalAdls, 
				newAdrs, 
				hierarchicalAdrs, 
				newAdis, 
				hierarchicalAdis, 
				newAdfs, 
				hierarchicalAdfs, 
				newAdss);
	}
	private static final List<ADS> createNewAdss(
			List<ADS.Type> adssStorageTypes, 
			List<AbstractType> adssDataTypes,
			List<ADSProxy> adssProxies, 
			List<Integer> adssMaxSizes){
		List<ADS> newAdss = new ArrayList<ADS>(adssStorageTypes.size());
		
		for(int i = 0; i < adssStorageTypes.size(); i++){
			ADS ads = new ADS(
					i, 
					adssStorageTypes.get(i), 
					adssDataTypes.get(i), 
					adssMaxSizes.get(i));
			
			newAdss.add(ads);
			adssProxies.add(new ADSProxy(ads, true));
			adssProxies.add(new ADSProxy(ads, false));
		}
		
		return newAdss;
	}
	private static final List<ADF> createNewAdfs(
			GeneticOperatorInterface geneticOperator, 
			List<AbstractType> adfsTypes, 
			List<List<AbstractType>> adfsArgsTypes, 
			List<ADFProxy> adfsProxies, 
			List<ADSProxy> adssProxies, 
			int initAdfsSize, 
			boolean hierarchicalAdfs){
		List<ADF> newAdfs = new ArrayList<ADF>(adfsTypes.size());
		
		List<ADFProxy> effectiveAdfProxies = hierarchicalAdfs ? adfsProxies : Collections.emptyList();
		for(int i = 0; i < adfsTypes.size(); i++){
			List<List<AbstractProxyNode>> localNodes = geneticOperator.buildLocalNodes(
					adssProxies, 
					effectiveAdfProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					Collections.emptyList(), 
					adfsArgsTypes.get(i));
			
			geneticOperator.setLocalExternalNodes(localNodes.get(0));
			geneticOperator.setLocalInternalNodes(localNodes.get(1));
			
			ADF adf = new ADF(
					i, 
					geneticOperator.generateTree(adfsTypes.get(i), initAdfsSize), 
					adfsArgsTypes.get(i));
			
			newAdfs.add(adf);
			adfsProxies.add(new ADFProxy(adf));
		}
		
		return newAdfs;
	}
	private static final List<ADI> createNewAdis(
			GeneticOperatorInterface geneticOperator, 
			List<AbstractType> adisTypes, 
			List<List<AbstractType>> adisExternalArgsTypes, 
			List<Integer> adisCollectionIds, 
			List<List<AbstractType>> adisFieldsTypes, 
			List<List<String>> adisFieldsNames, 
			List<Integer> adisMaxCalls, 
			List<ADIProxy> adisProxies, 
			List<ADFProxy> adfsProxies, 
			List<ADSProxy> adssProxies, 
			int initAdisSize, 
			boolean hierarchicalAdis){
		List<ADI> newAdis = new ArrayList<ADI>(adisTypes.size());
		
		List<ADIProxy> effectiveAdisProxies = hierarchicalAdis ? adisProxies : Collections.emptyList();
		for(int i = 0; i < adisTypes.size(); i++){
			List<List<AbstractProxyNode>> localNodes = geneticOperator.buildLocalNodes(
					adssProxies, 
					adfsProxies, 
					effectiveAdisProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					ADI.argsTypesFor(adisExternalArgsTypes.get(i), adisFieldsTypes.get(i)));
			
			geneticOperator.setLocalExternalNodes(localNodes.get(0));
			geneticOperator.setLocalInternalNodes(localNodes.get(1));
			
			ADI adi = new ADI(
					i, 
					adisTypes.get(i), 
					geneticOperator.generateTree(adisTypes.get(i), initAdisSize), 
					adisExternalArgsTypes.get(i), 
					adisCollectionIds.get(i), 
					adisFieldsTypes.get(adisCollectionIds.get(i)), 
					adisFieldsNames.get(adisCollectionIds.get(i)), 
					adisMaxCalls.get(i));
			
			newAdis.add(adi);
			adisProxies.add(new ADIProxy(adi));
		}
		
		return newAdis;
	}
	private static final List<ADR> createNewAdrs(
			GeneticOperatorInterface geneticOperator, 
			List<AbstractType> adrsTypes, 
			List<List<AbstractType>> adrsArgsTypes, 
			List<Integer> adrsMaxCalls, 
			List<Integer> adrsMaxRecursionDepths, 
			List<ADRProxy> adrsProxies, 
			List<ADFProxy> adfsProxies, 
			List<ADSProxy> adssProxies, 
			int initAdrsSize, 
			boolean hierarchicalAdrs){
		List<ADR> newAdrs = new ArrayList<ADR>(adrsTypes.size());
		
		List<ADRProxy> effectiveAdrsProxies = hierarchicalAdrs ? adrsProxies : Collections.emptyList();
		for(int i = 0; i < adrsTypes.size(); i++){
			List<List<AbstractProxyNode>> localNodes = geneticOperator.buildLocalNodes(
					adssProxies, 
					adfsProxies, 
					Collections.emptyList(), 
					effectiveAdrsProxies, 
					Collections.emptyList(), 
					adrsArgsTypes.get(i));
			
			geneticOperator.setLocalExternalNodes(localNodes.get(0));
			geneticOperator.setLocalInternalNodes(localNodes.get(1));
			
			AbstractNode conditionRoot = geneticOperator.generateTree(ADR.getConditionReturnType(), initAdrsSize);
			AbstractNode groundRoot = geneticOperator.generateTree(adrsTypes.get(i), initAdrsSize);
			
			List<List<AbstractProxyNode>> bodyLocalNodes = geneticOperator.buildLocalNodes(
					Collections.emptyList(), 
					Collections.emptyList(), 
					Collections.emptyList(), 
					Arrays.asList(
							new ADRProxy(new ADR(
									i, 
									new Constant(ADR.getConditionReturnType()),
									new Constant(adrsTypes.get(i)), 
									new Constant(adrsTypes.get(i)), 
									adrsArgsTypes.get(i), 
									0, 
									0))), 
					Collections.emptyList(), 
					Collections.emptyList());
			
			localNodes.get(0).addAll(bodyLocalNodes.get(0));
			localNodes.get(1).addAll(bodyLocalNodes.get(1));
			
			geneticOperator.setLocalExternalNodes(localNodes.get(0));
			geneticOperator.setLocalInternalNodes(localNodes.get(1));
			
			AbstractNode bodyRoot = geneticOperator.generateTree(adrsTypes.get(i), initAdrsSize);
			
			ADR adr = new ADR(
					i, 
					conditionRoot, 
					bodyRoot, 
					groundRoot, 
					adrsArgsTypes.get(i), 
					adrsMaxCalls.get(i), 
					adrsMaxRecursionDepths.get(i));
			
			newAdrs.add(adr);
			adrsProxies.add(new ADRProxy(adr));
		}
		
		return newAdrs;
	}
	private static final List<ADL> createNewAdls(
			GeneticOperatorInterface geneticOperator, 
			List<AbstractType> adlsInitializationTypes, 
			List<AbstractType> adlsBodyTypes, 
			List<List<AbstractType>> adlsArgsTypes, 
			List<Integer> adlsMaxCalls, 
			List<Integer> adlsMaxIterations, 
			List<ADLProxy> adlsProxies, 
			List<ADFProxy> adfsProxies, 
			List<ADSProxy> adssProxies, 
			int initAdlsSize, 
			boolean hierarchicalAdls){
		List<ADL> newAdls = new ArrayList<ADL>(adlsBodyTypes.size());
		
		List<ADLProxy> effectiveAdlsProxies = hierarchicalAdls ? adlsProxies : Collections.emptyList();
		for(int i = 0; i < adlsBodyTypes.size(); i++){
			List<List<AbstractProxyNode>> localNodes = geneticOperator.buildLocalNodes(
					adssProxies, 
					adfsProxies, 
					Collections.emptyList(), 
					Collections.emptyList(), 
					effectiveAdlsProxies, 
					ADL.argsTypesFor(adlsArgsTypes.get(i)));
			
			geneticOperator.setLocalExternalNodes(localNodes.get(0));
			geneticOperator.setLocalInternalNodes(localNodes.get(1));
			
			ADL adl = new ADL(
					i, 
					geneticOperator.generateTree(adlsInitializationTypes.get(i), initAdlsSize), 
					geneticOperator.generateTree(ADL.getConditionReturnType(), initAdlsSize), 
					geneticOperator.generateTree(adlsBodyTypes.get(i), initAdlsSize), 
					adlsArgsTypes.get(i), 
					adlsMaxCalls.get(i), 
					adlsMaxIterations.get(i));
			
			newAdls.add(adl);
			adlsProxies.add(new ADLProxy(adl));
		}
		
		return newAdls;
	}
	private static final List<RPB> createNewRpbs(
			GeneticOperatorInterface geneticOperator,
			List<AbstractType> rpbsTypes, 
			List<List<AbstractType>> rpbsArgsTypes, 
			List<List<String>> rpbsArgsNames, 
			List<ADLProxy> adlsProxies, 
			List<ADRProxy> adrsProxies, 
			List<ADIProxy> adisProxies, 
			List<ADFProxy> adfsProxies, 
			List<ADSProxy> adssProxies, 
			int initRpbsSize){
		List<RPB> newRpbs = new ArrayList<RPB>(rpbsTypes.size());
		
		for(int i = 0; i < rpbsTypes.size(); i++){
			List<List<AbstractProxyNode>> localNodes = geneticOperator.buildLocalNodes(
					adssProxies, 
					adfsProxies, 
					adisProxies, 
					adrsProxies, 
					adlsProxies, 
					rpbsArgsTypes.get(i));
			
			geneticOperator.setLocalExternalNodes(localNodes.get(0));
			geneticOperator.setLocalInternalNodes(localNodes.get(1));
			
			newRpbs.add(new RPB(
					i, 
					geneticOperator.generateTree(rpbsTypes.get(i), initRpbsSize), 
					rpbsArgsTypes.get(i),
					rpbsArgsNames.get(i)));
		}
		
		return newRpbs;
	}
	
	
	
	public final EvolvedIndividual build(){
		return new EvolvedIndividual(this, new Cell(newNucleus(
				getGeneticOperator().copy(), 
				getInitRpbsSize(),
				getRpbsTypes(),
				getRpbsArgsTypes(), 
				getRpbsArgsNames(), 
				getInitAdlsSize(), 
				getAdlsInitializationTypes(), 
				getAdlsBodyTypes(), 
				getAdlsArgsTypes(), 
				getAdlsMaxCalls(), 
				getAdlsMaxIterations(), 
				isHierarchicalAdls(), 
				getInitAdrsSize(), 
				getAdrsTypes(), 
				getAdrsArgsTypes(), 
				getAdrsMaxCalls(), 
				getAdrsMaxRecursionDepths(), 
				isHierarchicalAdrs(), 
				getInitAdisSize(), 
				getAdisTypes(), 
				getAdisExternalArgsTypes(), 
				getAdisCollectionIds(), 
				getAdisFieldsTypes(), 
				getAdisFieldsNames(), 
				getAdisMaxCalls(), 
				isHierarchicalAdis(), 
				getInitAdfsSize(), 
				getAdfsTypes(),
				getAdfsArgsTypes(),
				isHierarchicalAdfs(),
				getAdssStorageTypes(), 
				getAdssDataTypes(), 
				getAdssMaxSizes())));
	}
}
