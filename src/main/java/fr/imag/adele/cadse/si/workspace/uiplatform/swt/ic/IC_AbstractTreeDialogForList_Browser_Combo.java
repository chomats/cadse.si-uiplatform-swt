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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/** */

public abstract class IC_AbstractTreeDialogForList_Browser_Combo extends IC_WithDialogAction implements IC_ForList,
		ISelectionStatusValidator, IC_ForBrowserOrCombo {

	public IC_AbstractTreeDialogForList_Browser_Combo() {
	}

	public IC_AbstractTreeDialogForList_Browser_Combo(String title2, String message2) {
		super(title2, message2);
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

	protected Object[] selectOrCreateValues(Shell parentShell, boolean allowMultipleSelection) {
		ElementTreeSelectionDialog lsd = createTreeDialog(parentShell, allowMultipleSelection);
		lsd.open();
		if (lsd.getReturnCode() == Window.OK) {
			return lsd.getResult(); // after call canAddObject and
			// transAndAddObject
		}
		return null;
	}

	protected ElementTreeSelectionDialog createTreeDialog(Shell parentShell, boolean allowMultipleSelection) {
		ElementTreeSelectionDialog lsd = newTreeDialog(parentShell);
		ViewerFilter filter = getFilter();
		if (filter != null) {
			lsd.addFilter(filter);
		}
		lsd.setValidator(this);
		lsd.setInput(getInputValues());
		lsd.setAllowMultiple(allowMultipleSelection);
		lsd.setTitle(_title);
		lsd.setMessage(_message);
		return lsd;
	}

	protected ElementTreeSelectionDialog newTreeDialog(Shell parentShell) {
		ElementTreeSelectionDialog lsd = new ElementTreeSelectionDialog(parentShell, getLabelProvider(),
				getTreeContentProvider());
		return lsd;
	}

	protected abstract ViewerFilter getFilter();

	protected abstract ITreeContentProvider getTreeContentProvider();

	public IContentProvider getContentProvider() {
		return new ObjectArrayContentProvider();
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
