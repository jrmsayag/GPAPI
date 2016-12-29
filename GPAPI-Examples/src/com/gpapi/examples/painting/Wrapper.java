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
 * Wrapper.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.painting;

import java.awt.Color;
import java.awt.Graphics;

import com.gpapi.examples.painting.nodes.internal.PaintOval;
import com.gpapi.examples.painting.nodes.internal.PaintPolygon;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.cells.nucleuses.Nucleus;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class Wrapper {
	
	private final Graphics paintBrush;
	private final int width;
	private final int height;
	
	private int paintedShapes = 0;
	public final int getPaintedShapes() {
		return paintedShapes;
	}
	
	
	
	private Wrapper(Graphics paintBrush, int width, int height){
		this.paintBrush = paintBrush;
		this.width = width;
		this.height = height;
	}
	
	public final int paintOval(int x1, int y1, int x2, int y2, int red, int green, int blue, int alpha){
		Color color = new Color(red, green, blue, alpha);
		
		paintBrush.setColor(color);
		paintBrush.fillOval(x1 + width/2, y1 + height/2, x2-x1, y2-y1);
		paintedShapes++;
		
		return 2;
	}
	public final int paintPolygon(int[] xPoints, int[] yPoints, int red, int green, int blue, int alpha){
		Color color = new Color(red, green, blue, alpha);
		
		for(int i = 0; i < xPoints.length; i++)
			xPoints[i] += width/2;
		for(int i = 0; i < yPoints.length; i++)
			yPoints[i] += height/2;
		
		paintBrush.setColor(color);
		paintBrush.fillPolygon(xPoints, yPoints, xPoints.length);
		paintedShapes++;
		
		return xPoints.length;
	}
	
	
	
	public static final int doPainting(EvolvedIndividual painter, Graphics paintBrush){
		Nucleus nucleus = painter.getEggCell().getNucleus();
		GeneticOperator geneticOperator = (GeneticOperator) nucleus.getGeneticOperator();
		
		Wrapper wrapper = new Wrapper(paintBrush, geneticOperator.getWidth(), geneticOperator.getHeight());
		for(AbstractNode node : geneticOperator.getDescendantNodes(nucleus.getRpbs().get(0).getRoot())){
			if(node instanceof PaintOval)
				((PaintOval) node).setWrapper(wrapper);
			else if(node instanceof PaintPolygon)
				((PaintPolygon) node).setWrapper(wrapper);
		}
		
		try{
			painter.executeSameArgs();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
		
		return wrapper.getPaintedShapes();
	}
	
	
	
	public static final void clean(EvolvedIndividual painter){
		Nucleus nucleus = painter.getEggCell().getNucleus();
		for(AbstractNode node : nucleus.getGeneticOperator().getDescendantNodes(nucleus.getRpbs().get(0).getRoot())){
			if(node instanceof PaintOval)
				((PaintOval) node).setWrapper(null);
			else if(node instanceof PaintPolygon)
				((PaintPolygon) node).setWrapper(null);
		}
	}
}
