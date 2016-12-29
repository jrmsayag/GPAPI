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
 * Nucleus.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.AbstractModule;
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



public final class Nucleus implements Serializable {
	
	private static final long serialVersionUID = -3431510262902294924L;
	
	
	private transient GenotypeView view = null;
	public final JPanel getView(boolean forceNew) {
		if(forceNew)
			return (view = new GenotypeView(this));
		else
			return view != null ? view : (view = new GenotypeView(this));
	}
	public final JPanel getView(){
		return getView(false);
	}
	
	
	private final GeneticOperatorInterface geneticOperator;
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public final GeneticOperatorInterface getGeneticOperator() {
		return geneticOperator;
	}
	
	
	private final ArrayList<RPB> rpbs;
	/**
	 * TODO : Description.
	 * 
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<RPB> getRpbs() {
		return rpbs;
	}
	
	
	private final ArrayList<ADL> adls;
	/**
	 * TODO : Description.
	 * 
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<ADL> getAdls(){
		return adls;
	}
	
	
	private final boolean hierarchicalAdls;
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public final boolean isHierarchicalAdls() {
		return hierarchicalAdls;
	}


	private final ArrayList<ADR> adrs;
	/**
	 * TODO : Description.
	 * 
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<ADR> getAdrs() {
		return adrs;
	}
	
	
	private final boolean hierarchicalAdrs;
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public final boolean isHierarchicalAdrs() {
		return hierarchicalAdrs;
	}


	private final ArrayList<ADI> adis;
	/**
	 * TODO : Description.
	 * 
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<ADI> getAdis() {
		return adis;
	}
	
	
	private final boolean hierarchicalAdis;
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public final boolean isHierarchicalAdis() {
		return hierarchicalAdis;
	}


	private final ArrayList<ADF> adfs;
	/**
	 * TODO : Description.
	 * 
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<ADF> getAdfs() {
		return adfs;
	}


	private final boolean hierarchicalAdfs;
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public final boolean isHierarchicalAdfs() {
		return hierarchicalAdfs;
	}
	
	
	private final ArrayList<ADS> adss;
	/**
	 * TODO : Description.
	 * 
	 * Note : Be careful, the returned list is the internal one, not just a view or a copy!
	 * 
	 * @return
	 */
	public final ArrayList<ADS> getAdss() {
		return adss;
	}
	


	public Nucleus(
			GeneticOperatorInterface geneticOperator, 
			List<RPB> rpbs, 
			List<ADL> adls, 
			boolean hierarchicalAdls, 
			List<ADR> adrs, 
			boolean hierarchicalAdrs, 
			List<ADI> adis, 
			boolean hierarchicalAdis, 
			List<ADF> adfs, 
			boolean hierarchicalAdfs, 
			List<ADS> adss){
		this.geneticOperator = geneticOperator;
		
		this.rpbs = new ArrayList<RPB>(rpbs);
		
		this.adls = new ArrayList<ADL>(adls);
		this.hierarchicalAdls = hierarchicalAdls;
		
		this.adrs = new ArrayList<ADR>(adrs);
		this.hierarchicalAdrs = hierarchicalAdrs;
		
		this.adis = new ArrayList<ADI>(adis);
		this.hierarchicalAdis = hierarchicalAdis;
		
		this.adfs = new ArrayList<ADF>(adfs);
		this.hierarchicalAdfs = hierarchicalAdfs;
		
		this.adss = new ArrayList<ADS>(adss);
		
		consolidate();
	}
	
	public final void consolidate(){
		for(AbstractNode node : getAllNodes()){
			if(node instanceof ADLProxy){
				ADLProxy adlProxy = (ADLProxy) node;
				adlProxy.setAdl(getAdls().get(adlProxy.getId()));
			} else if(node instanceof ADRProxy){
				ADRProxy adrProxy = (ADRProxy) node;
				adrProxy.setAdr(getAdrs().get(adrProxy.getId()));
			} else if(node instanceof ADIProxy){
				ADIProxy adiProxy = (ADIProxy) node;
				adiProxy.setAdi(getAdis().get(adiProxy.getId()));
			} else if(node instanceof ADFProxy){
				ADFProxy adfProxy = (ADFProxy) node;
				adfProxy.setAdf(getAdfs().get(adfProxy.getId()));
			} else if(node instanceof ADSProxy){
				ADSProxy adsProxy = (ADSProxy) node;
				adsProxy.setAds(getAdss().get(adsProxy.getId()));
			}
		}
	}
	
	
	public final int getMaxModuleSize(){
		int size = 0;
		for(AbstractModule module : getAllModules())
			size = Math.max(size, module.getSize());
		return size;
	}
	public final int getMaxModuleDepth(){
		int depth = 0;
		for(AbstractModule module : getAllModules())
			depth = Math.max(depth, module.getDepth());
		return depth;
	}
	
	public final int getTotalSize(){
		int size = 0;
		for(AbstractModule module : getAllModules())
			size += module.getSize();
		return size;
	}
	
	public final List<AbstractNode> getAllNodes(){
		ArrayList<AbstractNode> allRoots = new ArrayList<AbstractNode>();
		for(AbstractModule module : getAllModules())
			allRoots.addAll(module.getBranchRoots());
		return getGeneticOperator().getDescendantNodes(allRoots);
	}
	
	public final ArrayList<AbstractModule> getAllModules(){
		ArrayList<AbstractModule> allModules = new ArrayList<AbstractModule>(
				getAdss().size() + 
				getAdfs().size() + 
				getAdis().size() + 
				getAdrs().size() + 
				getAdls().size() +
				getRpbs().size());
		
		allModules.addAll(getAdss());
		allModules.addAll(getAdfs());
		allModules.addAll(getAdis());
		allModules.addAll(getAdrs());
		allModules.addAll(getAdls());
		allModules.addAll(getRpbs());
		
		return allModules;
	}
	
	
	public final Nucleus copy(){
		ArrayList<RPB> rpbsCopy = new ArrayList<RPB>(getRpbs().size());
		for(RPB rpb : getRpbs()){
			rpbsCopy.add(new RPB(
					rpb.getId(), 
					getGeneticOperator().replicateSubtree(rpb.getRoot()), 
					rpb.getArgsTypes(), 
					rpb.getArgsNames()));
		}
		
		ArrayList<ADL> adlsCopy = new ArrayList<ADL>(getAdls().size());
		for(ADL adl : getAdls()){
			adlsCopy.add(new ADL(
					adl.getId(), 
					getGeneticOperator().replicateSubtree(adl.getInitializationRoot()), 
					getGeneticOperator().replicateSubtree(adl.getConditionRoot()), 
					getGeneticOperator().replicateSubtree(adl.getBodyRoot()), 
					adl.getExternalArgsTypes(), 
					adl.getMaxCalls(), 
					adl.getMaxIterations()));
		}
		
		ArrayList<ADR> adrsCopy = new ArrayList<ADR>(getAdrs().size());
		for(ADR adr : getAdrs()){
			adrsCopy.add(new ADR(
					adr.getId(), 
					getGeneticOperator().replicateSubtree(adr.getConditionRoot()), 
					getGeneticOperator().replicateSubtree(adr.getBodyRoot()), 
					getGeneticOperator().replicateSubtree(adr.getGroundRoot()), 
					adr.getArgsTypes(), 
					adr.getMaxCalls(), 
					adr.getMaxRecursionDepth()));
		}
		
		ArrayList<ADI> adisCopy = new ArrayList<ADI>(getAdis().size());
		for(ADI adi : getAdis()){
			adisCopy.add(new ADI(
					adi.getId(), 
					adi.getDefaultValue(), 
					getGeneticOperator().replicateSubtree(adi.getRoot()), 
					adi.getExternalArgsTypes(), 
					adi.getCollectionId(), 
					adi.getFieldsTypes(), 
					adi.getFieldsNames(), 
					adi.getMaxCalls()));
		}
		
		ArrayList<ADF> adfsCopy = new ArrayList<ADF>(getAdfs().size());
		for(ADF adf : getAdfs()){
			adfsCopy.add(new ADF(
					adf.getId(), 
					getGeneticOperator().replicateSubtree(adf.getRoot()), 
					adf.getArgsTypes()));
		}
		
		ArrayList<ADS> adssCopy = new ArrayList<ADS>(getAdss().size());
		for(ADS ads : getAdss()){
			adssCopy.add(new ADS(
					ads.getId(), 
					ads.getStorageType(), 
					ads.getReturnType(), 
					ads.getMaxSize()));
		}
		
		return new Nucleus(
				getGeneticOperator().copy(), 
				rpbsCopy, 
				adlsCopy, 
				isHierarchicalAdls(), 
				adrsCopy, 
				isHierarchicalAdrs(), 
				adisCopy, 
				isHierarchicalAdis(), 
				adfsCopy, 
				isHierarchicalAdfs(), 
				adssCopy);
	}
	public final Nucleus mergeWith(Nucleus other){
		return getGeneticOperator().mergeNucleuses(this, other);
	}
}
