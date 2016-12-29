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
 * GenerationSnapshot.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class GenerationSnapshot implements Serializable {

	private static final long serialVersionUID = 8438586890469019209L;
	
	private final int generation;
	public final int getGeneration(){
		return generation;
	}
	
	private final ArrayList<Population> populations;
	public final List<Population> getPopulations() {
		return Collections.unmodifiableList(populations);
	}
	
	
	
	public GenerationSnapshot(int generation, List<Population> populations){
		if(generation < 0)
			throw new IllegalArgumentException("Generation must equal or be greater than zero!");
		else if(populations == null)
			throw new NullPointerException("The populations must be specified!");
		else if(populations.isEmpty())
			throw new IllegalArgumentException("There must be at least one population!");
		
		this.generation = generation;
		this.populations = new ArrayList<Population>(populations);
	}
}
