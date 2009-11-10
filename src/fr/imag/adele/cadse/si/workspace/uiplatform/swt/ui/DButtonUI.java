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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.ActionController;

/**
 * Represents a field with a button.
 */
public class DButtonUI<IC extends ActionController> extends DAbstractField<IC> {

	protected Button	_button;

	@Override
	public void createControl(Composite container, int hspan) {
		Composite composite = container;

		_button = new Button(composite, SWT.PUSH);
		_button.setText(getLabel());
		_button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
				handleSelect();
			}
		});

		setEditable(_field.isEditable());
	}

	@Override
	public void initAfterUI() {
		super.initAfterUI();
	}

	protected void handleSelect() {
		RuningInteractionController ic = _ic;
		if (ic == null) {
			return;
		}

		if (ic instanceof ActionController) {
			ActionController ac = (ActionController) ic;
			ac.callAction();
		}
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		// do nothing
	}

	@Override
	public Control getMainControl() {
		return _button;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}
}
