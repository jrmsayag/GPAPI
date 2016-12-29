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
 * Shrink.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.modules.ArgumentProxy;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.nodes.Constant;



/**
 * This mutation replaces a subtree with the value its execute() method returns
 * if the subtree does not contain an ArgumentProxy, or performs a simple
 * copy of the node otherwise.
 * 
 * @author sayag
 *
 */

public final class Shrink implements PointMutationOperatorInterface {

	private static final long serialVersionUID = 5610699983412335242L;

	@Override
	public final AbstractNode mutatedCopy(AbstractNode mutationRoot, GeneticOperatorInterface geneticOperator) {
		for(AbstractNode node : geneticOperator.getDescendantNodes(mutationRoot, true)){
			if(node instanceof ArgumentProxy)
				return mutationRoot.copy();
		}
		
		try{
			return new Constant(mutationRoot.execute().getValue());
		} catch(Exception e){
			return geneticOperator.findExternalNode(mutationRoot.getReturnType());
		}
	}

	@Override
	public final Shrink copy() {
		return new Shrink();
	}
}
