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
 * Argument.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.modules;

import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class Argument extends AbstractProxyNodeSlot {

	private static final long serialVersionUID = -184813017131547520L;
	
	private String manualName = null;
	public final String getManualName() {
		return manualName;
	}
	public final void setManualName(String manualName) {
		this.manualName = manualName;
	}

	private AbstractType value;
	public final AbstractType getValue() {
		return value;
	}
	public final void setValue(AbstractType value) {
		if(!getReturnType().isTheSameAs(value))
			throw new RuntimeException("Trying to set a value of the wrong type!");
		else
			this.value = value;
	}
	
	
	
	public Argument(int id, String manualName, AbstractType type){
		super(id);
		
		if(type == null)
			throw new NullPointerException("A type must be specified!");
		
		this.manualName = manualName;
		this.value = type;
	}
	public Argument(int id, AbstractType type){
		this(id, null, type);
	}
	
	@Override
	public final AbstractType getReturnType() {
		return getValue();
	}
	
	@Override
	public final String getName() {
		if(getManualName() != null)
			return getManualName();
		else
			return "arg" + getId();
	}
}
