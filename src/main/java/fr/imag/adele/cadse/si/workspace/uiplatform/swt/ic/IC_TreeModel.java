/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;

import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.FilteredItemNode;
import fede.workspace.tool.view.node.FilteredItemNodeModel;
import fede.workspace.tool.view.node.ItemNodeIsSelected;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.eclipse.view.SelfViewContentProvider;
import fr.imag.adele.cadse.eclipse.view.SelfViewLabelProvider;

public class IC_TreeModel extends ICRunningField implements ItemNodeIsSelected {

	protected FilteredItemNodeModel	model;
	protected FilteredItemNode		rootNode	= null;

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param object
	 *            l'object selectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	public boolean hasCheckedBox(Object object) {
		return false;
	}

	/**
	 * 
	 * @param object
	 *            l'object selectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	public String canObjectSelected(Object object) {
		return null;
	}

	/**
	 * 
	 * @param object
	 *            l'object deselectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	public String canObjectDeselected(Object object) {
		return null;
	}

	public ILabelProvider getLabelProvider() {
		return new SelfViewLabelProvider();
	}

	public FilteredItemNode getOrCreateFilteredNode() {
		if (rootNode == null) {
			rootNode = createRootNode();
			rootNode.setIcNodeIsSelected(this);
		}
		return rootNode;
	}

	protected FilteredItemNode createRootNode() {
		return new FilteredItemNode(null, getTreeModel());
	}

	protected FilteredItemNodeModel getTreeModel() {
		return this.model;
	}

	public void setModel(FilteredItemNodeModel model) {
		this.model = model;
	}

	public IContentProvider getContentProvider() {
		return new SelfViewContentProvider();
	}

	public void refreshAll() {
		if (rootNode == null) {
			return;
		}
		rootNode.removeAndClose();
		rootNode.recomputeChildren();
	}

	public void select(Object data) {
		// TODO Auto-generated method stub

	}

	public void treeCollapsed(Object data) {
		if (data instanceof AbstractCadseViewNode) {
			((AbstractCadseViewNode) data).close();
		}
	}

	public int isSelected(IItemNode node) {
		return IItemNode.DESELECTED;
	}

}
