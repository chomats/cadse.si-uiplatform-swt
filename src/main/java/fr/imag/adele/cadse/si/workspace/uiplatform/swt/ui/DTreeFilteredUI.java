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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForList;

/**
 * value <li>List&lt;Object&gt;<br>
 * </li>
 * 
 * @author chomats
 */
public class DTreeFilteredUI<IC extends IC_ForList> extends DAbstractField<IC> {

	List<Object> fElements;
	private FilteredTree packageTable;
	private Button buttonAdd;
	private Button buttonRemove;

	@Override
	public Object getVisualValue() {
		return fElements;
	}

	@Override
	public void createControl(Composite ocontainer, int hspan) {

		GridData gd;
		Composite container = ocontainer;
		packageTable = new FilteredTree(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL, new PatternFilter());

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.verticalSpan = 2;
		gd.horizontalSpan = hspan - 1;
		packageTable.setLayoutData(gd);
		packageTable.getViewer().setContentProvider(_ic.getContentProvider());
		packageTable.setData(UIField.CADSE_MODEL_KEY, this);

		buttonAdd = new Button(container, SWT.PUSH);
		buttonAdd.setText("Add...");
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
				handleAdd();
			}
		});
		buttonAdd.setData(this);

		buttonRemove = new Button(container, SWT.PUSH);
		buttonRemove.setText("Remove...");
		buttonRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
				ISelection sel = packageTable.getViewer().getSelection();
				if (sel == null) {
					return;
				}
				if (sel instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) sel;
					if (ssel.size() == 0) {
						return;
					}
					handleRemove(ssel.toArray());
				}

			}
		});
		buttonRemove.setData(this);

	}

	protected void handleRemove(Object[] sel) {
		String error = _ic.canRemoveObject(sel);
		if (error != null) {
			_swtuiplatform.setMessage(error, UIPlatform.ERROR);
			return;
		}
		Object[] removedObj = _ic.removeObject(sel);
		for (Object obj : removedObj) {
			fElements.remove(obj);
		}
		setVisualValue(fElements);
		_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
	}

	protected void handleAdd() {
		Object[] ret = _ic.selectOrCreateValues(packageTable.getShell());
		if (ret != null) {
			String error = _ic.canAddObject(ret);
			if (error != null) {
				_swtuiplatform.setMessage(error, UIPlatform.ERROR);
				return;
			}
			ret = _ic.transAndAddObject(ret);
			fElements.addAll(Arrays.asList(ret));
			setVisualValue(fElements);
			_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
		}
	}

	protected ILabelProvider getLabelProvider() {
		return _ic.getLabelProvider();
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			visualValue = new ArrayList<Object>();
		}
		assert visualValue instanceof List;
		fElements = (List<Object>) visualValue;
		packageTable.getViewer().setInput(fElements.toArray());

	}

	@Override
	public void setEnabled(boolean v) {
		packageTable.setEnabled(v);
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
	}

	@Override
	public void setVisible(boolean v) {
		super.setVisible(v);
		packageTable.setVisible(v);
		if (buttonAdd != null) {
			buttonAdd.setVisible(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setVisible(v);
		}
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getMainControl() {
		return this.packageTable;
	}

	@Override
	public Object[] getSelectedObjects() {
		return ((StructuredSelection) this.packageTable.getViewer().getSelection()).toArray();
	}
}
