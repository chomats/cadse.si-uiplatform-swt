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

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;

public class DGridUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	Composite composite;
	boolean makeColumnsEqualWidth = false;
	int columnNb = 0;

	public int getColumnNb() {
		return columnNb;
	}

	public void setColumnNb(int columnNb) {
		this.columnNb = columnNb;
	}

	public boolean isMakeColumnsEqualWidth() {
		return makeColumnsEqualWidth;
	}

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		int style = SWT.NULL;
		if (!_field.getFlag(Item.UI_NO_BORDER)) {
			style |= SWT.BORDER;
		}
		composite = _swtuiplatform.getToolkit().createComposite(container, style);
		composite.setData(UIField.CADSE_MODEL_KEY, this);
		composite.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout(columnNb, makeColumnsEqualWidth);

		_swtuiplatform.createChildrenControl(_page, this, composite, gridLayout);
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
		return this.composite;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
