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
 * VertexList.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting.nodes.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gpapi.examples.painting.nodes.types.Vertex;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



public final class VertexList extends AbstractType {
	
	private static final long serialVersionUID = -7507089474446524317L;
	
	private final ArrayList<Vertex> value = new ArrayList<Vertex>();
	public final ArrayList<Vertex> getValue(){
		return value;
	}
	
	
	
	public VertexList(List<Vertex> vertices){
		for(Vertex v : vertices)
			value.add(v);
	}
	public VertexList(){
		this(Collections.emptyList());
	}
	
	
	@Override
	public final void mutate(){
		// Do nothing
	}
	@Override
	public final VertexList copy() {
		return new VertexList(getValue());
	}
	@Override
	public final VertexList generateNew(){
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
