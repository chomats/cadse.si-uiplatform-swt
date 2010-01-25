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
 */
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.view.FilterContext;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService;

/**
 * The standard implementation of property sheet page which presents a table of
 * property names and values obtained from the current selection in the active
 * workbench part.
 * <p>
 * This page obtains the information about what to properties display from the
 * current selection (which it tracks).
 * </p>
 * <p>
 * The model for this page is a hierarchy of <code>IPropertySheetEntry</code>.
 * The page may be configured with a custom model by setting the root entry.
 * <p>
 * If no root entry is set then a default model is created which uses the
 * <code>IPropertySource</code> interface to obtain the properties of the
 * current slection. This requires that the selected objects provide an
 * <code>IPropertySource</code> adapter (or implement
 * <code>IPropertySource</code> directly). This restiction can be overcome by
 * providing this page with an <code>IPropertySourceProvider</code>. If
 * supplied, this provider will be used by the default model to obtain a
 * property source for the current selection
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @see IPropertySource
 */
public class ItemPropertySheetPage extends Page implements SWTService.MyPropertySheetPage {

	private Pages			lastItemPages;
	private SWTUIPlatform	_swtuiPlatform;

	private IItemNode 		_node;
	private AbstractCadseTreeViewUI	_view;
	private Composite	_control;

	public ItemPropertySheetPage(AbstractCadseTreeViewUI view, IItemNode node) {
		_view = view;
		_node = node;
	}

	@Override
	public void createControl(Composite parent) {
		try {
			_control = createControl2(parent);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (_control == null)
			_control = createEmptyComposite(parent);
	}

	

	protected Composite createEmptyComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.BACKGROUND);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		Label t = new Label(container, SWT.NONE);
		t.setText("No item selected.");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER + GridData.VERTICAL_ALIGN_CENTER);
		t.setLayoutData(gd);
		return container;
	}

	@Override
	public Control getControl() {
		return _control;
	}

	@Override
	public void setFocus() {
		_control.setFocus();
	}

	private LinkType getContainmentLinkTypeParent(Item item) {
		for (Link l : item.getIncomingLinks()) {
			if (l.getLinkType().isPart()) {
				return l.getLinkType();
			}
		}
		return null;
	}

	protected IItemNode descFormSel(ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection sssel = (IStructuredSelection) selection;
		Object element = sssel.getFirstElement();
		if (element == null || !(element instanceof IItemNode)) {
			return null;
		}
		IItemNode iiv = (IItemNode) element;

		return iiv;
	}


	private Composite createControl2(Composite parent) throws CadseException  {
		if (_view == null) {
			return null;
		}
		if (_node == null) {
			return null;
		}
		Item item = _node.getItem();
		if (item == null || !item.isResolved()) {
			return null;
		}

		

		FilterContext fc = new FilterContext(item, null, item.getType(), _view, null, null, _node, null);
		
		lastItemPages = item.getModificationPages(fc);
		_swtuiPlatform = new SWTUIPlatform();
		_swtuiPlatform.setPages(lastItemPages);
		_swtuiPlatform.setParent(parent);
		_swtuiPlatform.setSite(getSite());
		_swtuiPlatform.setItem(item);
		_swtuiPlatform.setVariable(IFieldDescription.PARENT_CONTEXT, item.getPartParent());
		_swtuiPlatform.setVariable(IFieldDescription.INCOMING_LINK_TYPE, getContainmentLinkTypeParent(item));

		_swtuiPlatform.setFilterContext(fc);
		
		return _swtuiPlatform.createControlPage(parent);
	}

	@Override
	public void dispose() {
		super.dispose();
		
		if (_swtuiPlatform != null) {
			_swtuiPlatform.dispose();
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
