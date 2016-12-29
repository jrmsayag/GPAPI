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
 * GenotypeView.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;



public final class GenotypeView extends JPanel {

	private static final long serialVersionUID = -6159669275063491835L;
	
	public static final class CustomVertex {
		private final int id;
		public final int getId() {
			return id;
		}
		private final String name;
		public final String getName() {
			return name;
		}
		public CustomVertex(int id, String name){
			this.id = id;
			this.name = name;
		}
		@Override
		public final String toString(){
			return name;
		}
		@Override
		public final boolean equals(Object other){
			if(other instanceof CustomVertex){
				if(((CustomVertex) other).getId() == getId())
					return true;
			}
			return false;
		}
		@Override
		public final int hashCode(){
			return id;
		}
	}
	public static final class CustomEdge {
		private final int id;
		public final int getId() {
			return id;
		}
		private final String name;
		public final String getName() {
			return name;
		}
		public CustomEdge(int id, String name){
			this.id = id;
			this.name = name;
		}
		@Override
		public final String toString(){
			return name;
		}
		@Override
		public final boolean equals(Object other){
			if(other instanceof CustomEdge){
				if(((CustomEdge) other).getId() == getId())
					return true;
			}
			return false;
		}
		@Override
		public final int hashCode(){
			return id;
		}
	}

	private final DelegateForest<CustomVertex,CustomEdge> graph;
    private final VisualizationViewer<CustomVertex,CustomEdge> vv;
    private final TreeLayout<CustomVertex,CustomEdge> treeLayout;
    private final GraphZoomScrollPane panel;
    
    
    
    public GenotypeView(Nucleus nucleus){
    	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        graph = new DelegateForest<CustomVertex,CustomEdge>(new DirectedOrderedSparseMultigraph<CustomVertex,CustomEdge>());

        createTreesView(nucleus);
        treeLayout = new TreeLayout<CustomVertex,CustomEdge>(graph);
        vv =  new VisualizationViewer<CustomVertex,CustomEdge>(treeLayout);
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<CustomVertex, CustomEdge>());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<CustomVertex>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<CustomEdge>());

        panel = new GraphZoomScrollPane(vv);
        add(panel);
        

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                exportImage();
            }
        });

        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("View controls"));

        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        scaleGrid.add(save);
        controls.add(scaleGrid);

        add(controls);
    }
    
    
    
    /**
     * Exports the visual representation of the individual in an
     * image file, after asking the destination file in a dialog.
     */
    protected final void exportImage(){
    	JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(this);
        File file;
        if(returnVal == JFileChooser.APPROVE_OPTION)
           file = chooser.getSelectedFile();
        else
        	return;
        
        
    	BufferedImage im = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	panel.paint(im.getGraphics());
    	try {
			ImageIO.write(im, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    
    /**
     * Recursively builds the visual representation using the GPNode's
     * createView method.
     * 
     * @param nucleus
     */
    protected final void createTreesView(Nucleus nucleus){
    	CustomVertex root = new CustomVertex(0, "ROOT");
    	
    	graph.addVertex(root);
    	
    	for(int i = 0; i < nucleus.getRpbs().size(); i++){
    		String edgeName = String.format("RPB-%d (%d)", nucleus.getRpbs().get(i).getId(), nucleus.getRpbs().get(i).getArgs().size());
    		nucleus.getRpbs().get(i).getRoot().createView(graph, root.getId(), root.getName(), edgeName);
    	}
    	for(int i = 0; i < nucleus.getAdls().size(); i++){
    		CustomVertex adlRoot = new CustomVertex(graph.getVertexCount(), "ROOT");
    		String edgeName = String.format("ADL-%d (%d)", nucleus.getAdls().get(i).getId(), nucleus.getAdls().get(i).getExternalArgsTypes().size());
    		graph.addEdge(new CustomEdge(graph.getEdgeCount(), edgeName), root, adlRoot);
    		nucleus.getAdls().get(i).getInitializationRoot().createView(graph, adlRoot.getId(), adlRoot.getName(), "INIT");
    		nucleus.getAdls().get(i).getConditionRoot().createView(graph, adlRoot.getId(), adlRoot.getName(), "CONDIT");
    		nucleus.getAdls().get(i).getBodyRoot().createView(graph, adlRoot.getId(), adlRoot.getName(), "BODY");
    	}
    	for(int i = 0; i < nucleus.getAdrs().size(); i++){
    		CustomVertex adrRoot = new CustomVertex(graph.getVertexCount(), "ROOT");
    		String edgeName = String.format("ADR-%d (%d)", nucleus.getAdrs().get(i).getId(), nucleus.getAdrs().get(i).getArgsTypes().size());
    		graph.addEdge(new CustomEdge(graph.getEdgeCount(), edgeName), root, adrRoot);
    		nucleus.getAdrs().get(i).getConditionRoot().createView(graph, adrRoot.getId(), adrRoot.getName(), "CONDIT");
    		nucleus.getAdrs().get(i).getBodyRoot().createView(graph, adrRoot.getId(), adrRoot.getName(), "BODY");
    		nucleus.getAdrs().get(i).getGroundRoot().createView(graph, adrRoot.getId(), adrRoot.getName(), "GROUND");
    	}
    	for(int i = 0; i < nucleus.getAdis().size(); i++){
    		String edgeName = String.format("ADI-%d (%d)", nucleus.getAdis().get(i).getId(), nucleus.getAdis().get(i).getExternalArgsTypes().size());
    		nucleus.getAdis().get(i).getRoot().createView(graph, root.getId(), root.getName(), edgeName);
    	}
    	for(int i = 0; i < nucleus.getAdfs().size(); i++){
    		String edgeName = String.format("ADF-%d (%d)", nucleus.getAdfs().get(i).getId(), nucleus.getAdfs().get(i).getArgs().size());
    		nucleus.getAdfs().get(i).getRoot().createView(graph, root.getId(), root.getName(), edgeName);
    	}
    	for(int i = 0; i < nucleus.getAdss().size(); i++){
    		String edgeName = String.format("ADS-%d (%d)", nucleus.getAdss().get(i).getId(), nucleus.getAdss().get(i).getDimension());
    		String vertexName = nucleus.getAdss().get(i).getStorageType().toString();
    		if(!nucleus.getAdss().get(i).getStorageType().equals(ADS.Type.Variable))
    			vertexName = String.format("%s (%d)", vertexName, nucleus.getAdss().get(i).getMaxSize());
    		graph.addEdge(
    				new CustomEdge(graph.getEdgeCount(), edgeName), 
    				root, 
    				new CustomVertex(graph.getVertexCount(), vertexName));
    	}
    }
}
