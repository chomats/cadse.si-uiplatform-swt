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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;

import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.node.FilteredItemNode;
import fede.workspace.tool.view.node.FilteredItemNodeModel;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.eclipse.view.SelfViewContentProvider;

/**
 * Model suivant FilteredItemNode qui dis ce que je doit afficher Le link type
 * qui dit la validiter : l'item est une instance du type de la destination
 * 
 * The Class IC_PartLinkForBrowser_Combo_List.
 * 
 * @generated
 */
public class IC_FilteredNodeLinkForBrowser_Combo_List extends IC_LinkForBrowser_Combo_List {

	public IC_FilteredNodeLinkForBrowser_Combo_List(String title, String message) {
		super(title, message);
	}

	public IC_FilteredNodeLinkForBrowser_Combo_List() {
	}

	/** The error message. */
	String							errorMessage;

	private FilteredItemNodeModel	model;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List
	 * #getTreeContentProvider()
	 */
	@Override
	protected ITreeContentProvider getTreeContentProvider() {
		return new SelfViewContentProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List
	 * #getInputValues()
	 */
	@Override
	protected Object getInputValues() {
		return getOrCreateFilteredNode();
	}

	protected FilteredItemNode getOrCreateFilteredNode() {

		FilteredItemNodeModel treeModel = getTreeModel();
		if (treeModel == null) {
			return new FilteredItemNode(null);
		}
		return new FilteredItemNode(null, treeModel);
	}

	protected FilteredItemNodeModel getTreeModel() {
		return this.model;
	}

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setModel(FilteredItemNodeModel model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List
	 * #validate(java.lang.Object[])
	 */
	@Override
	public IStatus validate(Object[] selection) {
		if (selection == null || selection.length != 1) {
			return new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, errorMessage, null);
		}
		Object o = selection[0];
		if (o instanceof Item && ((Item) o).isInstanceOf(getLinkType().getDestination())) {
			return Status.OK_STATUS;
		}

		return new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, errorMessage, null);
	}
}
