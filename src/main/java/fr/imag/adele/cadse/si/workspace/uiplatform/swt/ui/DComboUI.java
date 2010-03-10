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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForBrowserOrCombo;

/**
 * attributes : -ui controller : IInteractiveComboBoxController - combo-values : String[] si
 * IC_StaticArrayOfObjectForBrowser_Combo comme interactive controller ///- enable-editor : Boolean, default :
 * Boolean.FALSE value - Object or String
 */

public class DComboUI<IC extends IC_ForBrowserOrCombo> extends DAbstractField<IC> {

	protected CCombo attributWidget;
	Object[] values;

	@Override
	public void createControl(Composite ocontainer, int hspan) {
		GridData gd;
		Composite container = ocontainer;
		attributWidget = new CCombo(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		attributWidget.setLayoutData(gd);
		attributWidget.setData(UIField.CADSE_MODEL_KEY, this);

		attributWidget.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
			}

		});
		attributWidget.setEditable(_field.isEditable());

		values = _ic.getValues();
		String[] valuesString = new String[values.length];
		for (int i = 0; i < valuesString.length; i++) {
			valuesString[i] = _ic.toString(values[i]);
		}
		attributWidget.setItems(valuesString);

	}

	@Override
	public Object getVisualValue() {
		int index = attributWidget.getSelectionIndex();

		if (index == -1) {
			return attributWidget.getText();
		}
		return values[index];
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			return;
		}

		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(visualValue)) {
				attributWidget.select(i);
				return;
			}
		}
		if (visualValue instanceof String) {
			attributWidget.setText((String) visualValue);
		}
		else if (values.length > 0) {
			attributWidget.select(0);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		attributWidget.setEnabled(enabled);
	}

	@Override
	public void setEditable(boolean v) {
		attributWidget.setEditable(v);
	}

	@Override
	public void setVisible(boolean v) {
		attributWidget.setVisible(v);
	}

	public ItemType getType() {
		return CadseGCST.DCOMBO;
	}

	@Override
	public Control getMainControl() {
		return this.attributWidget;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { getVisualValue() };
	}

}