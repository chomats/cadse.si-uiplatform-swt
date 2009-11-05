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
package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import fede.workspace.model.manager.properties.impl.ic.IC_ContextMenu;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;

public abstract class DAbstractField<IC extends RuningInteractionController> extends UIRunningField<IC>  {

	protected Label		_labelWidget;
	private MenuManager	_menumanager	= null;
	

	
	protected void createContextMenu(Control parent) {
		if (_ic instanceof IC_ContextMenu) {
			_menumanager = new MenuManager();
			final IC_ContextMenu contextMenu = ((IC_ContextMenu) _ic);
			_menumanager.setRemoveAllWhenShown(contextMenu.hasRemoveAllWhenShown());
			_menumanager.createContextMenu(parent);
			_menumanager.addMenuListener(new IMenuListener2() {

				public void menuAboutToHide(IMenuManager manager) {
					contextMenu.menuAboutToHide(getSelectedObjects(), manager);
				}

				public void menuAboutToShow(IMenuManager manager) {
					contextMenu.menuAboutToShow(getSelectedObjects(), manager);
				}

			});
			getMainControl().setMenu(_menumanager.getMenu());
		}
	}

	public void dispose() {
		if (_menumanager != null) {
			_menumanager.dispose();
		}
		_menumanager = null;
		_labelWidget = null;
	}

	public abstract Control getMainControl();

	public abstract Object[] getSelectedObjects();

	public void setEditable(boolean v) {
		if (this._labelWidget != null) {
			this._labelWidget.setEnabled(v);
		}
	}

	public void setVisible(boolean v) {
		if (_labelWidget != null) {
			_labelWidget.setVisible(v);
		}
	}

	public void setEnabled(boolean v) {
		if (this._labelWidget != null) {
			this._labelWidget.setEnabled(v);
		}
	}

	public void setLabel(String label) {
		if (_labelWidget != null) {
			_labelWidget.setText(label);
			_labelWidget.redraw();
		}
	}

	public void setLabelWidget(Label labelWidget) {
		this._labelWidget = labelWidget;
	}

	/**
	 * return a desciption of this field
	 */
	@Override
	public String toString() {
		IPage p = getPage();
		StringBuilder sb = new StringBuilder();
		sb.append(_field);

		if (p != null) {
			sb.append(" in page ").append(p.getTitle());
		}

		return sb.toString();
	}

	@Override
	public void updateValue() {
		Display d = Display.getCurrent();
		if (d == null) {
			d = Display.getDefault();
		}
		d.asyncExec(new Runnable() {
			public void run() {
				setVisualValue(_swtuiplatform.getValueForVisual(_field), false);
			}
		});
	}



	public Object getVisualValue() {
		return null;
	}

}
