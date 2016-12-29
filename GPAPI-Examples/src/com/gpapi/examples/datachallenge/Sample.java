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
 * Sample.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.datachallenge;

import java.util.ArrayList;

import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public class Sample implements Comparable<Sample> {
	
	// IDs
	
	public final String game_date;
	
	public final int shot_id;
	
	
	// Pred
	
	public final byte shot_made_flag;
	
	
	// Variables
	
	private static final ArrayList<Integer> argsIndexes = new ArrayList<Integer>(){
		private static final long serialVersionUID = 1852820661677172201L;
		{
			add(4);
			add(5);
			add(6);
			add(7);
			add(8);
			add(9);
			add(10);
			add(12);
			add(13);
		}
	};
	public static final ArrayList<String> argsNames = new ArrayList<String>(){
		private static final long serialVersionUID = -7320173467915988521L;
		{
			add("lat");
			add("loc_x");
			add("loc_y");
			add("lon");
			add("minutes_remaining");
			add("period");
			add("playoffs");
			add("seconds_remaining");
			add("shot_distance");
		}
	};
	public static final ArrayList<AbstractType> argsTypes = new ArrayList<AbstractType>(){
		private static final long serialVersionUID = -6054881973244491943L;
		{
			add(RealValue.create());
			add(RealValue.create());
			add(RealValue.create());
			add(RealValue.create());
			add(RealValue.create());
			add(RealValue.create());
			add(RealValue.create());
			add(RealValue.create());
			add(RealValue.create());
		}
	};
	public final ArrayList<AbstractType> argsValues = new ArrayList<AbstractType>();
	
	
	
	
	public Sample(String line){
		String[] values = line.split(",");
		
		game_date = values[21];
		
		shot_id = Integer.valueOf(values[24]);
		
		shot_made_flag = values[14].length() > 0 ? Byte.valueOf(values[14]) : -1;
		
		for(Integer argIndex : argsIndexes)
			argsValues.add(RealValue.create(Double.valueOf(values[argIndex])));
	}
	
	
	@Override
	public final int compareTo(Sample other) {
		return game_date.compareTo(other.game_date);
	}
}
