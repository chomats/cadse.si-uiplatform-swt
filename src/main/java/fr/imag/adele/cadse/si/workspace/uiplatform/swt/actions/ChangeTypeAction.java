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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import fede.workspace.model.manager.properties.impl.mc.MC_AllItemType;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_AllItemTypeForTreeUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTreeUI;

// TODO is disable.
public class ChangeTypeAction implements IViewActionDelegate {

	private final class ChangeTypeActionPage extends AbstractActionPage {
		private final DTreeUI	treeui;

		private ChangeTypeActionPage(DTreeUI treeui) {
			this.treeui = treeui;
		}

		@Override
		public void doFinish(UIPlatform uiPlatform, Object monitor) throws Exception {
			ChangeTypeAction.this.selectedItemType = null;
			Object[] selectObject = treeui.getSelectedDataObject();
			selectedItemType = (ItemType) selectObject[0];
		}
	}

	private Item								item;
	IItemManager								ip;
	private IViewPart							view;
	protected ItemType							selectedItemType;
	private SWTUIPlatform						_swtuiPlatform;
	private IPage								_page;
	private DTreeUI<IC_AllItemTypeForTreeUI>	_treeUI;
	private ChangeTypeActionPage				_action;

	public ChangeTypeAction() {
		super();
	}

	public void run(IAction action) {
		try {
			createCreateItemWizard();
			if (_swtuiPlatform.open(view.getViewSite().getShell(), _page, _action, false) == Window.OK) {
				item.setType(selectedItemType);
			}
		} catch (Throwable e) {
			MessageDialog.openError(view.getSite().getShell(), "Error when rename the item "
					+ item.getQualifiedDisplayName(), e.getMessage());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// item = null;
		// if (selection != null && selection.isEmpty() == false
		// && selection instanceof IStructuredSelection) {
		// IStructuredSelection ssel = (IStructuredSelection) selection;
		// if (ssel.size() != 1)
		// return;
		// Object obj = ssel.getFirstElement();
		// if (obj instanceof ItemInViewer) {
		// this.iiv = (ItemInViewer) obj;
		// this.item = ((ItemInViewer) obj).getItem();
		// }
		//
		// }
		// if (item == null)
		// action.setEnabled(false);
		// else {
		// ip = WSPlugin.getManager(item.getType());
		// if (ip == null)
		// action.setEnabled(false);
		// else
		// action.setEnabled(true);
		// }
		action.setEnabled(false);
	}

	public void init(IViewPart view1) {
		this.view = view1;
	}

	void createCreateItemWizard() {
		_swtuiPlatform = new SWTUIPlatform();
		_page = _swtuiPlatform.createPageDescription("Select an item type", "Select an item type");

		_treeUI = _swtuiPlatform.createTreeUI(_page, "selector", null, EPosLabel.none, new MC_AllItemType(),
				new IC_AllItemTypeForTreeUI());
		_page.addLast(_treeUI.getAttributeDefinition());
		_action = new ChangeTypeActionPage(_treeUI);
	}

}
