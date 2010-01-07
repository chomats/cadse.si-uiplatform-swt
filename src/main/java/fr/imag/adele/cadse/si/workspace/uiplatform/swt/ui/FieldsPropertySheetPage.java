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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseView;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService.MyPropertySheetPage;
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
public class FieldsPropertySheetPage extends Page implements IPropertySheetPage, SWTService.MyPropertySheetPage {

	IPropertySheetPage		current;;
	Item					lastItem;
	PageBook				pageBook		= null;
	SWTService _service;
	private MyPropertySheetPage	propertySource;

	public FieldsPropertySheetPage(SWTService swt) {
		_service = swt;
	}

	@Override
	public void createControl(Composite parent) {
		pageBook = new PageBook(parent, 0);
		pageBook.setLayoutData(new GridData(GridData.FILL_BOTH));
	}


	protected Composite createEmptyComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.NO_BACKGROUND);
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
		return pageBook;
	}

	@Override
	public void setFocus() {
		pageBook.setFocus();
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

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		AbstractCadseTreeViewUI view = null;
		if (part instanceof AbstractCadseView) {
			view = ((AbstractCadseView) part).getViewController();
		}
		IItemNode desc = descFormSel(selection);
		try {
			setController(view, desc);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setController(AbstractCadseTreeViewUI view, IItemNode itemNode) throws CadseException, PartInitException {
		if (propertySource != null)
			propertySource.dispose();
		
		propertySource = _service.createPropertySheetPage(view, itemNode);
		propertySource.init(getSite());
		propertySource.createControl(pageBook);
		pageBook.showPage(propertySource.getControl());
		
	}

	@Override
	public void dispose() {
		super.dispose();
		pageBook.dispose();
		if (propertySource != null)
			propertySource.dispose();
	}

}
