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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 * attribut : check-label : string value : Boolean
 * 
 * @author chomats
 */
public class DCheckBoxUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	private Button _control;

	private Boolean _value;

	public Object __getVisualValue() {
		_value = _control.getGrayed() ? null : _control.getSelection() ? Boolean.TRUE : Boolean.FALSE;
		return _value;
	}

	@Override
	public void createControl(Composite container, int hspan) {

		GridData gd;

		String label = getLabel();
		_control = _swtuiplatform.getToolkit().createButton(container, label, SWT.CHECK);

		_control.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if (getAttributeDefinition().canBeUndefined()) {
					if (_value == null) {
						_control.setSelection(true);
						_control.setGrayed(false);
					}
					else if (_value == Boolean.TRUE) {
						_control.setSelection(false);
					}
					else {
						_control.setGrayed(true);
						_control.setSelection(true);
					}
					e.doit = false;
				}
				else {
					_control.setGrayed(false);
				}
				_swtuiplatform.broadcastValueChanged(_page, _field, __getVisualValue());
			}

		});

		if (!_field.isEditable()) {
			_control.setEnabled(false);
		}

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		_control.setLayoutData(gd);
		_control.setData(UIField.CADSE_MODEL_KEY, this);
	}

	@Override
	public void dispose() {
		super.dispose();
		_control = null;
		_value = null;
	}

	@Override
	public Control getMainControl() {
		return _control;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Boolean[] { _value };
	}

	@Override
	public Object getVisualValue() {
		return _value;
	}

	@Override
	public void setEditable(boolean v) {
		_control.setEnabled(v);
	}

	@Override
	public void setVisible(boolean v) {
		_control.setVisible(v);
	}

	@Override
	public void setEnabled(boolean v) {
		_control.setEnabled(v);
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			if (getAttributeDefinition().canBeUndefined()) {
				if (_control != null && !_control.isDisposed()) {
					(_control).setSelection(true);
					(_control).setGrayed(true);
				}
				_value = null;
				return;
			}
			else {
				visualValue = false;
				_swtuiplatform.broadcastValueChanged(_page, _field, false);
			}
		}
		assert visualValue instanceof Boolean;

		if (visualValue.equals(_value)) return;
		
		_value = (Boolean) visualValue;
		if (_control != null && !_control.isDisposed()) {
			(_control).setSelection(_value.booleanValue());
			(_control).setGrayed(false);
		}
	}

}
