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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;

public class DTabUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	boolean		makeColumnsEqualWidth	= false;
	private TabFolder	_tabFolder;

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED;
		if (!_field.getFlag(Item.UI_NO_BORDER)) {
			style |= SWT.BORDER;
		}
		_tabFolder = new TabFolder(container, SWT.V_SCROLL + SWT.H_SCROLL);
		
		_tabFolder.setData(UIField.CADSE_MODEL_KEY, this);
		_tabFolder.setLayoutData(gridData);
		//_section.setExpanded(true);
		
		UIField[] fields = _swtuiplatform.getFields(_page, _swtuiplatform.getAttributes(_page, this));
		
		for (UIField f : fields) {
			Composite tabControl = new Composite(_tabFolder, SWT.NONE);
			UIRunningField child = _swtuiplatform.createFieldControl(_page, this, tabControl, f, null);
			if (child == null) continue;
			String title = f.getLabel();
			if (title == null) {
				title = f.getName();
			}
			TabItem ti = new TabItem(_tabFolder, SWT.NONE);
			ti.setText(title);
					
			ti.setControl(tabControl);
		}
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setEditable(boolean v) {
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
	}

	public ItemType getType() {
		return null;
	}

	public void setMakeColumnsEqualWidth(boolean makeColumnsEqualWidth) {
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
	}

	@Override
	public Control getMainControl() {
		return this._tabFolder;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}
	
	@Override
	public void setFocus() {
		if (_children != null && _children.length != 0) {
			_children[0].setFocus();
		}
	}

}
