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
package fede.workspace.model.manager.properties.impl.ic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.CheckStatus;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.attribute.ListAttributeType;
import fr.imag.adele.cadse.core.impl.ui.ic.IC_Abstract;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.util.Convert;

/**
 * manage a list of string
 * 
 * @author chomats
 * 
 */
public class IC_DefaultForList extends IC_AbstractForList implements
		IC_ForList, ILabelProvider {

	@Override
	public void init() throws CadseException {
		_allowDuplicate = _ic
				.getAttribute(CadseGCST.IC_STRING_LIST_FOR_LIST_at_ALLOW_DUPLICATE_);

	}

	private boolean _allowDuplicate;

	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public String canAddObject(Object[] object) {
		if (_allowDuplicate) {
			return null;
		}

		List v = (List) _uiPlatform.getVisualValue(getUIField());
		for (int i = 0; i < object.length; i++) {
			if (v.contains(object[i])) {
				return "Cannot add the same value twice";
			}
		}
		return null;
	}

	public Image getImage(Object element) {
		return null;
	}

	public ILabelProvider getLabelProvider() {
		return this;
	}

	public String getText(Object element) {
		return element.toString();
	}

	public ItemType getType() {
		return CadseGCST.IC_STRING_LIST_FOR_LIST;
	}

	@Override
	protected Object[] getValues() {
		return null;
	}

	protected boolean isEnable() {
		return true;
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public Object[] selectOrCreateValues(Shell parentShell) {
		if (!isEnable()) {
			return null;
		}

		ListAttributeType<?> att = (ListAttributeType<?>) getUIField()
				.getAttributeDefinition();
		final IAttributeType<?> subAtt = att == null ? null : att
				.getSubAttributeType();
		InputDialog dialog;
		dialog = new InputDialog(parentShell, title, message,

		"", new IInputValidator() {

			public String isValid(String newText) {
				if (subAtt != null) {
					CheckStatus error = subAtt.check(_uiPlatform
							.getItem(getUIField()), newText);
					if (error != null) {
						return error.getFormatedMessage();
					}
				}
				return null;
			}
		});
		int ret = dialog.open();
		if (ret == Window.OK) {
			final String value = dialog.getValue();
			if (subAtt != null) {
				Object v = subAtt.convertTo(value);
				if (v != null) {
					return new Object[] { v };
				}
			}
			return new Object[] { value };
		}
		return null;
	}

	public Object edit(Shell shell, Object value, int index) {
		return null;
	}

	@Override
	public boolean moveDown(Object[] object) {

		Object v = _uiPlatform.getVisualValue(getUIField());
		if (v instanceof ArrayList) {
			ArrayList array = (ArrayList) v;
			boolean modified = false;
			for (int i = object.length - 1; i >= 0; i--) {
				Object e = object[i];
				int index = array.indexOf(e);
				if (index == -1 | index >= array.size() - 1) {
					continue;
				}
				Object swap = array.get(index + 1);
				array.set(index, swap);
				array.set(index + 1, e);
				modified = true;
			}
			return modified;
		}
		// TODO Auto-generated method stub
		return super.moveDown(object);
	}

	@Override
	public boolean moveUp(Object[] object) {
		Object v = _uiPlatform.getVisualValue(getUIField());
		if (v instanceof ArrayList) {
			ArrayList array = (ArrayList) v;
			boolean modified = false;
			for (int i = 0; i < object.length; i++) {
				Object e = object[i];
				int index = array.indexOf(e);
				if (index == -1 | index == 0) {
					continue;
				}
				Object swap = array.get(index - 1);
				array.set(index, swap);
				array.set(index - 1, e);
				modified = true;
			}
			return modified;
		}
		return super.moveUp(object);
	}

	@Override
	public void dispose() {
	}
}
