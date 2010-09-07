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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;

public class DSectionUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	boolean		makeColumnsEqualWidth	= false;
	private Section	_section;

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED;
		if (!_field.getFlag(Item.UI_NO_BORDER)) {
			style |= SWT.BORDER;
		}
		_section = _swtuiplatform.getToolkit().createSection(container, style);
		
		_section.setData(UIField.CADSE_MODEL_KEY, this);
		_section.setLayoutData(gridData);
		_section.setText(getLabel());
		//_section.setExpanded(true);
		
		Composite client = new Composite(_section, 0);

		GridLayout gridLayout = new GridLayout(0, makeColumnsEqualWidth);
		
		_swtuiplatform.getToolkit().adapt(client);
		_swtuiplatform.createChildrenControl(_page, this, client, gridLayout);
		_section.setClient(client);
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
		return this._section;
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
