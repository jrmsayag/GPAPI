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
 * Cell.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.modules.adis.ADI;
import com.gpapi.individuals.cells.nucleuses.modules.adls.ADL;
import com.gpapi.individuals.cells.nucleuses.modules.adrs.ADR;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class Cell implements Serializable {
	
	private static final long serialVersionUID = 1413680296542085630L;
	
	private final Nucleus nucleus;
	/**
	 * TODO : Description.
	 * 
	 * @return
	 */
	public final Nucleus getNucleus() {
		return nucleus;
	}
	
	
	
	public Cell(Nucleus nucleus){
		this.nucleus = nucleus;
	}
	
	
	/**
	 * TODO : Description.
	 * <p>
	 * Note : Pass an empty list for the argument "collections" in order to automatically
	 * set an empty collection for each ADI, otherwise all ADIs' collections must be 
	 * specified, simply pass an empty list for the ones you want to ignore.
	 * <p>
	 * For example if you want only ADIs whose collection ID is 1 to have a collection set
	 * when calling RPB 2, and the potential ADI collection IDs run from 0 to 2 (i.e. three 
	 * lists of ADIs' fields types and names were given at the EvolvedIndividualBuilder),
	 * proceed with the following call:
	 * <p>
	 * execute(2, someArgsList, Arrays.asList(Collection.emptyList(), someListForADIsWhoseCollectionIDIsOne, Collections.emptyList()))
	 * 
	 * @param rpbIndex
	 * @param args
	 * @param collections
	 * @return
	 * 			The Result object returned by the execution of the given RPB.
	 * @throws Exception
	 * 			If any error occur during the execution of the given RPB.
	 */
	public final Result execute(int rpbIndex, List<AbstractType> args, List<List<List<AbstractType>>> collections) throws Exception {
		init(collections);
		return getNucleus().getRpbs().get(rpbIndex).execute(args);
	}
	/**
	 * TODO : Description.
	 * <p>
	 * Note : Pass an empty list for the argument "collections" in order to automatically
	 * set an empty collection for each ADI, otherwise all ADIs' collections must be 
	 * specified. Simply pass an empty list for the ones you want to ignore.
	 * <p>
	 * For example if you want only ADIs whose collection ID is 1 to have a collection set
	 * when calling RPB 2, and the potential ADI collection IDs run from 0 to 2 (i.e. three 
	 * lists of ADIs' fields types and names were given at the EvolvedIndividualBuilder),
	 * proceed with the following call:
	 * <p>
	 * execute(2, someArgsList, Arrays.asList(Collection.emptyList(), someListForADIsWhoseCollectionIDIsOne, Collections.emptyList()))
	 * 
	 * @param rpbIndex
	 * @param args
	 * @param collections
	 * @return
	 * 			The Result object returned by the execution of the given RPB, or null if 
	 * 			any error occur during the execution of the RPB.
	 */
	public final Result executeUnconditionally(int rpbIndex, List<AbstractType> args, List<List<List<AbstractType>>> collections){
		try{
			return execute(rpbIndex, args, collections);
		} catch(Exception e) {
			return null;
		}
	}
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param collections
	 * @throws NullPointerException
	 * 			If collections is null.
	 */
	private final void init(List<List<List<AbstractType>>> collections){
		if(collections == null)
			throw new NullPointerException("Argument collections can't be null !");
		
		for(ADI adi : getNucleus().getAdis()){
			if(!collections.isEmpty())
				adi.setCollection(collections.get(adi.getCollectionId()));
			else
				adi.setCollection(Collections.emptyList());
			adi.reset();
		}
		
		for(ADR adr : getNucleus().getAdrs())
			adr.reset();
		
		for(ADL adl : getNucleus().getAdls())
			adl.reset();
	}
	
	
	public final Cell copy(){
		return new Cell(getNucleus().copy());
	}
	public final Cell mergeWith(Cell other){
		return new Cell(getNucleus().mergeWith(other.getNucleus()));
	}
}
