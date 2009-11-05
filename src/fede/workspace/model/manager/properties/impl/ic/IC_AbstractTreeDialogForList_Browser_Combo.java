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

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.util.Convert;

/** */

public abstract class IC_AbstractTreeDialogForList_Browser_Combo extends
		ICRunningField implements IC_ForList, ISelectionStatusValidator,
		IC_ForBrowserOrCombo {

	private String title;
	private String message;

	@Override
	public void init() throws CadseException {
		title = _ic
				.getAttribute(CadseGCST.IC_ABSTRACT_TREE_DIALOG_FOR_LIST_BROWSER_COMBO_at_TITLE_);
		message = _ic
				.getAttribute(CadseGCST.IC_ABSTRACT_TREE_DIALOG_FOR_LIST_BROWSER_COMBO_at_MESSAGE_);
	}

	public String canAddObject(Object[] object) {
		return null;
	}

	public Object[] transAndAddObject(Object[] object) {
		return object;
	}

	public String canRemoveObject(Object[] object) {
		return null;
	}

	public Object[] removeObject(Object[] object) {
		return object;
	}

	protected Object getInputValues() {
		return null;
	}

	public Object[] selectOrCreateValues(Shell parentShell) {
		return selectOrCreateValues(parentShell, true);
	}

	protected Object createGoodObject(Object object) {
		return object;
	}

	public Object selectOrCreateValue(Shell parentShell) {
		Object[] ret = selectOrCreateValues(parentShell, false);
		if (ret != null && ret.length == 1) {
			return createGoodObject(ret[0]);
		}
		return null;
	}

	protected Object[] selectOrCreateValues(Shell parentShell,
			boolean allowMultipleSelection) {
		ElementTreeSelectionDialog lsd = createTreeDialog(parentShell,
				allowMultipleSelection);
		lsd.open();
		if (lsd.getReturnCode() == Window.OK) {
			return lsd.getResult(); // after call canAddObject and
			// transAndAddObject
		}
		return null;
	}

	protected ElementTreeSelectionDialog createTreeDialog(Shell parentShell,
			boolean allowMultipleSelection) {
		ElementTreeSelectionDialog lsd = newTreeDialog(parentShell);
		ViewerFilter filter = getFilter();
		if (filter != null) {
			lsd.addFilter(filter);
		}
		lsd.setValidator(this);
		lsd.setInput(getInputValues());
		lsd.setAllowMultiple(allowMultipleSelection);
		lsd.setTitle(title);
		lsd.setMessage(message);
		return lsd;
	}

	protected ElementTreeSelectionDialog newTreeDialog(Shell parentShell) {
		ElementTreeSelectionDialog lsd = new ElementTreeSelectionDialog(
				parentShell, getLabelProvider(), getTreeContentProvider());
		return lsd;
	}

	protected abstract ViewerFilter getFilter();

	protected abstract ITreeContentProvider getTreeContentProvider();

	public IContentProvider getContentProvider() {
		return getTreeContentProvider();
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public Object fromString(String value) {
		return null;
	}

	public boolean hasDeleteFunction() {
		return true;
	}

	public boolean moveDown(Object[] object) {
		return false;
	}

	public boolean moveUp(Object[] object) {
		return false;
	}

	public Object edit(Shell shell, Object value, int index) {
		return null;
	}
}