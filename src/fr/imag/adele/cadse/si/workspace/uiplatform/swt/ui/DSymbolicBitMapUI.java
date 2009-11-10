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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.attribute.SymbolicBitMapAttributeType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 * attribut : check-label : string value : Boolean
 * 
 * @author chomats
 * 
 */
public class DSymbolicBitMapUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	SymbolicBitMapAttributeType	attributeDefinition;
	String[]					labels;
	Button[]					controls;
	Object						value;
	int							col;
	private Group				g;

	@Override
	public void init() throws CadseException {
		assert _field.getAttributeDefinition() instanceof SymbolicBitMapAttributeType;

		this.attributeDefinition = (SymbolicBitMapAttributeType) _field.getAttributeDefinition();
		this.labels = this.attributeDefinition.getLabels();
		value = attributeDefinition.getDefaultValue();
		col = 3;
	}

	@Override
	public Object getVisualValue() {
		int loc_value = ((Integer) value).intValue();
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				boolean v = controls[i].getSelection();
				int flag = 1 << i;
				if (v != ((loc_value & flag) != 0)) {
					if (v) {
						loc_value |= flag;
					} else {
						loc_value &= ~flag;
					}
				}
				assert v == ((loc_value & flag) != 0);
			}
		}
		return loc_value;
	}

	@Override
	public void createControl(Composite container, int hspan) {

		GridData gd;

		g = _swtuiplatform.getToolkit().createGroup(container, getLabel());
		g.setData(UIField.CADSE_MODEL_KEY, _field);
		g.setLayout(new GridLayout(col, false));
		controls = new Button[labels.length];
		for (int i = 0; i < labels.length; i++) {
			if (labels[i] == null) {
				controls[i] = null;
				continue; // reseved or private position
			}
			controls[i] = _swtuiplatform.getToolkit().createButton(g, labels[i], SWT.CHECK);
			controls[i].setData(i);
			controls[i].setData(UIField.CADSE_MODEL_KEY, _field);
			controls[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
				}

			});
		}
		if (!_field.isEditable()) {
			setEnabled(false);
		}

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			visualValue = attributeDefinition.getDefaultValue();
		}
		assert visualValue instanceof Integer;
		int loc_value = ((Integer) visualValue).intValue();
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setSelection((loc_value & (1 << i)) != 0);
			}
		}
		this.value = visualValue;
	}

	@Override
	public void setEnabled(boolean v) {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setEnabled(v);
			}
		}
	}

	@Override
	public void setEditable(boolean v) {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setEnabled(v);
			}
		}
	}

	@Override
	public void setVisible(boolean v) {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setVisible(v);
			}
		}
	}

	@Override
	public Control getMainControl() {
		return g;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
