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
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForList;

/**
 * value <li>List&lt;Object&gt;<br>
 * </li>
 * 
 * @author chomats
 * 
 */
public class DTreeFiltered2UI<IC extends IC_ForList> extends DAbstractField<IC> {

	List<Object> fElements;

	private FilteredTree fFilteredTree;

	@Override
	public Object getVisualValue() {
		return fElements;
	}

	@Override
	public void createControl(Composite container, int hspan) {

		GridData gd;
		fFilteredTree = new FilteredTree(container, SWT.BORDER
				| SWT.SINGLE | SWT.V_SCROLL, new PatternFilter());

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.verticalSpan = 2;
		gd.horizontalSpan = hspan - 1;
		fFilteredTree.setLayoutData(gd);
		fFilteredTree.getViewer().setContentProvider(_ic.getContentProvider());
		fFilteredTree.setData(UIField.CADSE_MODEL_KEY, _field);

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
		fFilteredTree.getViewer().setInput(fElements.toArray());

	}

	@Override
	public void setEnabled(boolean v) {
		fFilteredTree.setEnabled(v);
	}

	@Override
	public void setEditable(boolean v) {
		fFilteredTree.setEnabled(v);
	}

	@Override
	public void setVisible(boolean v) {
		super.setVisible(v);
		fFilteredTree.setVisible(v);
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getMainControl() {
		return fFilteredTree;
	}

	@Override
	public Object[] getSelectedObjects() {
		return ((StructuredSelection) fFilteredTree.getViewer().getSelection())
				.toArray();
	}
}
