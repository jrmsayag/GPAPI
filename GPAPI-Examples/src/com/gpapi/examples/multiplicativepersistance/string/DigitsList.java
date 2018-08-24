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
 * DigitsList.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.multiplicativepersistance.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class DigitsList extends AbstractType {
	
	private static final long serialVersionUID = -7507089474446524317L;
	
	private final ArrayList<RealValue> value = new ArrayList<RealValue>();
	public final ArrayList<RealValue> getValue(){
		return value;
	}
	
	
	
	public DigitsList(List<RealValue> digits){
		for(RealValue d : digits)
			value.add(d);
	}
	public DigitsList(){
		this(Collections.emptyList());
	}
	
	
	@Override
	public final void mutate(){
		// Do nothing
	}
	@Override
	public final DigitsList copy() {
		return new DigitsList(getValue());
	}
	@Override
	public final DigitsList generateNew(){
		return copy();
	}
	@Override
	public final String toString(){
		String s = "[";
		if(!getValue().isEmpty()){
			for(int i = 0; i < getValue().size()-1; i++)
				s = s + getValue().get(i).toString() + ",";
			s = s + getValue().get(getValue().size()-1).toString();
		}
		s = s + "]";
		return s;
	}
}
