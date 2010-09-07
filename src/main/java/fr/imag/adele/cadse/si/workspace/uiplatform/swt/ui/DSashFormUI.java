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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.util.Assert;

public class DSashFormUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	boolean		horizontal	= true;
	SashForm	sashForm;
	int			hone		= 50;

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm(container, horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
		sashForm.setLayoutData(gridData);
		sashForm.setData(UIField.CADSE_MODEL_KEY, this);

		GridLayout gridLayout = new GridLayout(hspan, false);
		_swtuiplatform.createChildrenControl(_page, this, sashForm, gridLayout);
		Assert.isNotNull(_children);
		Assert.isTrue(_children.length == 2);
		// must set after create two children
		sashForm.setWeights(new int[] { hone, 100 - hone });

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

	public void setWeight(int hone) {
		assert hone >= 0 && hone <= 100;
		this.hone = hone;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	@Override
	public Control getMainControl() {
		return this.sashForm;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
