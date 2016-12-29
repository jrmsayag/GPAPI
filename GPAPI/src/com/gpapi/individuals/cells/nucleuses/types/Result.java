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
 * Result.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.types;



public final class Result {
	
	private AbstractType value;
	public final AbstractType getValue() {
		return value;
	}
	public Result setValue(AbstractType value) {
		if(value == null)
			throw new NullPointerException("The value can't be null!");
		else {
			this.value = value;
			return this;
		}
	}
	
	private int cost;
	public final int getCost() {
		return cost;
	}
	public final Result setCost(int cost) {
		this.cost = cost;
		return this;
	}
	
	
	
	public Result(AbstractType value, int cost){
		setValue(value);
		setCost(cost);
	}
	public Result(AbstractType value){
		this(value, 1);
	}
	
	
	public final Result incrementCost(int cost) {
		return setCost(cost + getCost());
	}
}
