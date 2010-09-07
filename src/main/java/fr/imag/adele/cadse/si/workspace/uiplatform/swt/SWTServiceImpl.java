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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.node.ItemNode;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.view.NewContext;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.dialog.CadseDialog;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.FieldsPropertySheetPage;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.ItemPropertySheetPage;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService;

public class SWTServiceImpl implements SWTService {

	private static SWTService	SINGLETON;

	@Override
	public CadseRuntime[] openDialog(boolean askToErase) {
		return CadseDialog.openDialog(askToErase);
	}

	@Override
	public boolean showCreateWizard(Shell parentShell, Item parent, LinkType lt, ItemType destItemType)
			throws CadseException {
		final IItemManager ip = destItemType.getItemManager();
		if (ip == null) {
			return false;
		}
		if (ip.isAbstract(parent, lt)) {
			return false;
		}
		NewContext nc = new NewContext(parent, lt, destItemType);
		
		
		
		return showCreateWizard(parentShell, nc);
	}
	
	@Override
	public boolean showCreateWizard(Shell parentShell, NewContext nc)
			throws CadseException {
		nc.initTransaction(nc.getDestinationType().getLogicalWorkspace());
		Pages f = nc.getNewItem().getCreationPages(nc);

		if (f != null) {
			SWTUIPlatform swtuiPlatform = new SWTUIPlatform();
			swtuiPlatform.openCreationWizard(parentShell, f);
			return true;
		}

		return false;
	}

	
	
	@Override
	public void showCreateWizardWithError(Shell parentShell, NewContext c)
			throws CadseException {
		try {

			if (showCreateWizard(parentShell, c)) {
				return;
			}
			String message;
			if (c.getPartParent() != null) {
				message = MessageFormat.format(
						"Cannot create an item of type {0} from {1} of type {2} the link {3} : no pages found",
						c.getDestinationType().getName(), c.getPartParent().getName(), c.getPartParent().getType().getName(), c.getPartLinkType().getName());
			} else {
				message = MessageFormat.format("Cannot create an item of type {0} : no pages found", c.getDestinationType()
						.getName());
			}

			MessageDialog.openError(parentShell, "Cannot create wizard  : no pages found", message);
			WSPlugin.log(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, message, null));
		} catch (Throwable e1) {
			e1.printStackTrace();
			String message;
			if (c.getPartParent() != null) {
				message = MessageFormat.format("Cannot create an item of type {0} from {1} of type {2} the link {3}",
						c.getDestinationType().getName(), c.getPartParent().getName(), c.getPartParent().getType().getName(), c.getPartLinkType().getName());
			} else {
				message = MessageFormat.format("Cannot create an item of type {0}", c.getDestinationType().getName());
			}

			MessageDialog.openError(parentShell, "Cannot create wizard", message);
			WSPlugin.log(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, message, e1));
		}
	}

	@Override
	public void showCreateWizardWithError(Shell parentShell, Item parent, LinkType lt, ItemType destItemType)
			throws CadseException {
		try {

			if (showCreateWizard(parentShell, parent, lt, destItemType)) {
				return;
			}
			String message;
			if (parent != null) {
				message = MessageFormat.format(
						"Cannot create an item of type {0} from {1} of type {2} the link {3} : no pages found",
						destItemType.getName(), parent.getName(), parent.getType().getName(), lt.getName());
			} else {
				message = MessageFormat.format("Cannot create an item of type {0} : no pages found", destItemType
						.getName());
			}

			MessageDialog.openError(parentShell, "Cannot create wizard  : no pages found", message);
			WSPlugin.log(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, message, null));
		} catch (Throwable e1) {
			e1.printStackTrace();
			String message;
			if (parent != null) {
				message = MessageFormat.format("Cannot create an item of type {0} from {1} of type {2} the link {3}",
						destItemType.getName(), parent.getName(), parent.getType().getName(), lt.getName());
			} else {
				message = MessageFormat.format("Cannot create an item of type {0}", destItemType.getName());
			}

			MessageDialog.openError(parentShell, "Cannot create wizard", message);
			WSPlugin.log(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, message, e1));
		}
	}

	@Override
	public MyPropertySheetPage createPropertySheetPage(Object view, IItemNode node) {
		return new ItemPropertySheetPage((AbstractCadseTreeViewUI) view, node);
	}

	public static SWTService getInstance() {
		return SINGLETON;
	}
}
