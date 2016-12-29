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
 * PaintPolygon.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting.nodes.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gpapi.examples.painting.Wrapper;
import com.gpapi.examples.painting.nodes.types.Color;
import com.gpapi.examples.painting.nodes.types.Vertex;
import com.gpapi.examples.painting.nodes.types.VertexList;
import com.gpapi.examples.painting.nodes.types.Void;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



public final class PaintPolygon extends AbstractNode {
	
	private static final long serialVersionUID = 5894058997758188145L;
	
	private static final String name = "Polygon";

	private static final Void returnType = new Void();
	
	private static final ArrayList<AbstractType> argsTypes = new ArrayList<AbstractType>(Arrays.asList(
			new Void(),
			new Vertex(0),
			new VertexList(),
			new Color(0,0),
			new Void()));
	
	
	private final int maxVertices;
	public final int getMaxVertices() {
		return maxVertices;
	}
	
	private Wrapper wrapper = null;
	public final Wrapper getWrapper() {
		return wrapper;
	}
	public final void setWrapper(Wrapper wrapper) {
		this.wrapper = wrapper;
	}
	


	public PaintPolygon(int maxVertices){
		this.maxVertices = maxVertices;
	}
	
	
	@Override
	public final PaintPolygon mutatedCopy(GeneticOperatorInterface geneticOperator){
		return copy();
	}

	
	@Override
	public final String getName() {
		return name;
	}
	@Override
	public final Void getReturnType() {
		return returnType;
	}
	@Override
	public final ArrayList<AbstractType> getArgsTypes() {
		return argsTypes;
	}
	

	@Override
	public final PaintPolygon copy() {
		return new PaintPolygon(getMaxVertices());
	}
	@Override
	public final Result execute() throws Exception {
		Result arg0Result = getArgs().get(0).execute();
		
		Result arg2Result = getArgs().get(2).execute();
		List<Vertex> vertices = ((VertexList) arg2Result.getValue()).getValue();
		if(vertices.size() >= 3){
			Result arg1Result = getArgs().get(1).execute();
			Vertex initPosition = (Vertex) arg1Result.getValue();
			
			Result arg3Result = getArgs().get(3).execute();
			Color c = (Color) arg3Result.getValue();
			
			int xPoints[] = new int[Math.min(vertices.size(), maxVertices)];
			int yPoints[] = new int[Math.min(vertices.size(), maxVertices)];
			for(int i = 0; i < vertices.size() && i < maxVertices; i++){
				Vertex vertex = vertices.get(i);
				xPoints[i] = initPosition.getX() + vertex.getX();
				yPoints[i] = initPosition.getY() + vertex.getY();
			}
			
			int paintingCost = wrapper.paintPolygon(
					xPoints, 
					yPoints, 
					c.getR(), 
					c.getG(), 
					c.getB(), 
					c.getA());
			
			return getArgs().get(4).execute().incrementCost(
					arg0Result.getCost() + 
					arg1Result.getCost() + 
					arg2Result.getCost() + 
					arg3Result.getCost() + 
					5 +
					paintingCost);
		} else {
			return getArgs().get(4).execute().incrementCost(
					arg0Result.getCost() + 
					arg2Result.getCost() + 
					3);
		}
	}
}
