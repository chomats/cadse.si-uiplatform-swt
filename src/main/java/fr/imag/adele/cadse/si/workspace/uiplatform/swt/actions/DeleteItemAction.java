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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.actions;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.ISharedImages;

import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.actions.AbstractEclipseMenuAction;
import fede.workspace.tool.view.node.RootNode;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.dialog.ShowDetailWLWCDialog;

/**
 * Pour chaque item s�lectionn�, cette action demande au manager si on peut
 * d�truire un item. Le manager renvoie une message d'error dans ou cela est
 * impossible et l'action affiche l'erreur. Si cela est possible pour le
 * manager, l'action demande � l'utilisateur si c'est ok. Et si oui elle d�truit
 * l'item.
 * 
 * @author chomats
 * 
 */
public class DeleteItemAction extends AbstractEclipseMenuAction {

	private Set<IItemNode>	items;
	IShellProvider			_shell;

	public DeleteItemAction(Set<IItemNode> items, IShellProvider shell) {
		setDescription("Deletes the selected elements");

		ISharedImages workbenchImages = WSPlugin.getDefault().getWorkbench().getSharedImages();
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		this.items = items;
		this._shell = shell;
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		HashMap<Item, String> errors = new HashMap<Item, String>();
		LogicalWorkspaceTransaction copy = CadseCore.getLogicalWorkspace().createTransaction();
		for (IItemNode iiv : items) {
			Item aItem = null;
			if (iiv.getItem() == null) {
				continue;
			}
			aItem = iiv.getItem();
			IItemManager im = WSPlugin.getManager(aItem);
			String error = iiv.getParent() instanceof RootNode ? null : im.canDeleteItem(aItem);
			if (error != null) {
				errors.put(aItem, error);
				continue;
			}
			if (aItem == null) {
				continue;
			}

			final ItemDelta copyItem = copy.getItem(aItem.getId());

			if (copyItem == null) {
				continue;
			}
			copyItem.delete(true);
		}

		ShowDetailWLWCDialog.openDialog(new SWTUIPlatform(), _shell.getShell(), copy, "Deleted items",
				"Items to delete", true);
	}

	@Override
	public String getLabel() {
		if (items.size() == 1) {
			return "Delete " + ((ItemInViewer) items.iterator().next()).getItem().getName();
		}
		return "Delete ...";
	}

}
