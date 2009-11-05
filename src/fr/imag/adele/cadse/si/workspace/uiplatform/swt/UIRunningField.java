package fr.imag.adele.cadse.si.workspace.uiplatform.swt;

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

import org.eclipse.swt.widgets.Composite;

import fede.workspace.model.manager.properties.impl.ic.ICRunningField;
import fede.workspace.model.manager.properties.impl.ui.SWTUIPlatform;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 * Represente a graphic field which display a value of an attribute definition
 * 
 * Represente un champ graphique, il y a un identifiant (key). Il est �
 * l'intérieure d'une page, il peut avoir un label et la position de celui-ci
 * par rapport à lui (posLabel) Il a un model controller (mc) et peut avoir un
 * interaction controller dans le but de spécialiser le comportement du field
 * 
 * Il est possible de lui associé des validator (synchrone) et des listeners
 * (synchrone) Il peut recevoir des attributs supplémentaires...
 * 
 * @author chomats
 */
public abstract class UIRunningField<IC extends RuningInteractionController> {
	public UIField _field = null;

	/** The getPage(). */
	public IPage _page;
	/** The global controller. */
	public SWTUIPlatform _swtuiplatform;

	public UIRunningField<?> _next;

	public UIRunningField<?>[] _children;
	public IC _ic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getPosLabel()
	 */
	final public EPosLabel getPosLabel() {
		return _field.getPosLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getLabel()
	 */
	final public String getLabel() {
		return _field.getLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getPage()
	 */
	public IPage getPage() {
		return _page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.IPageObject#getPageController()
	 */
	public IPageController getPageController() {
		return _swtuiplatform;
	}

	/**
	 * Sets the page.
	 * 
	 * @param p
	 *            the new page
	 */
	public void init(IPage p, Pages pages) {
		this._page = p;
		if (hasChildren()) {
			for (UIRunningField c : getChildren()) {
				c.init(p, pages);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getModelController()
	 */
	public IModelController getModelController() {
		return _field.getModelController();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getVisualValue()
	 */
	public abstract Object getVisualValue();

	public void setVisualValue(Object visualValue) {
		setVisualValue(visualValue, true);
	}

	protected void setVisualValue(Object visualValue, boolean notifie) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setValue(java.lang.Object)
	 */
	public void setValue(Object visualValue) {
		setVisualValue(visualValue);
		getModelController().notifieValueChanged(_swtuiplatform, _field,
				visualValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#thisFieldHasChanged()
	 */
	public void thisFieldHasChanged() {
		_swtuiplatform.broadcastThisFieldHasChanged(_field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#forceChange()
	 */
	public void forceChange() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setEnabled(boolean)
	 */
	public abstract void setEnabled(boolean v);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setVisible(boolean)
	 */
	public void setVisible(boolean v) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.imag.adele.cadse.core.ui.UIField#createControl(fr.imag.adele.cadse
	 * .core.ui.IPageController, fr.imag.adele.cadse.core.ui.IFedeFormToolkit,
	 * java.lang.Object, int)
	 */
	public abstract void createControl(Composite container, int hspan);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#init()
	 */
	public void init() throws CadseException {
		if (getModelController() != null) {
			getModelController().init(_swtuiplatform);
		}

		if (_ic != null) {
			_ic.init();
		}

		if (hasChildren()) {
			for (UIRunningField<?> c : getChildren()) {
				c.init();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#initAfterUI()
	 */
	public void initAfterUI() {
		try {
			if (_field.isHidden()) {
				setVisible(false);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (getModelController() != null) {
			getModelController().initAfterUI(_swtuiplatform);
		}

		if (_ic != null) {
			_ic.initAfterUI();
		}

		if (hasChildren()) {
			for (UIRunningField<?> c : getChildren()) {
				c.initAfterUI();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#updateValue()
	 */
	public abstract void updateValue();

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#resetVisualValue()
	 */
	public void resetVisualValue() {
		if (!_field.isEditable()) {
			setEditable(false);
		}

		setVisualValue(_swtuiplatform.getValueForVisual(_field), false);

		if (hasChildren()) {
			for (UIRunningField c : getChildren()) {
				c.resetVisualValue();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getContext()
	 */
	public Object getContext() {
		return _swtuiplatform.getItem(_field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#hasChildren()
	 */
	public boolean hasChildren() {
		return _children != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getChildren()
	 */

	public UIRunningField<?>[] getChildren() {
		return _children;
	}

	public UIField getUIField() {
		return _field;
	}

}
