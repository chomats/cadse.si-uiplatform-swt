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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;

public abstract class IC_AbstractForBrowser_Combo extends ICRunningField
		implements IC_ForBrowserOrCombo {

	@Override
	public void init() throws CadseException {
		title = _ic
				.getAttribute(CadseGCST.IC_ABSTRACT_FOR_BROWSER_COMBO_at_TITLE_);
		message = _ic
				.getAttribute(CadseGCST.IC_ABSTRACT_FOR_BROWSER_COMBO_at_MESSAGE_);
	}

	private String title;
	private String message;

	public Object selectOrCreateValue(Shell parentShell) {
		ListDialog lsd = new ListDialog(parentShell);
		lsd.setInput(sort(getSelectableValues()));
		lsd.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Object[])
					return ((Object[]) inputElement);
				return null;
			}

			public void dispose() {
			}

			public void inputChanged(@SuppressWarnings("unused") Viewer viewer,
					@SuppressWarnings("unused") Object oldInput,
					@SuppressWarnings("unused") Object newInput) {
			}
		});
		lsd.setLabelProvider(getLabelProvider());
		lsd.setMessage(message);
		lsd.setTitle(title);
		lsd.open();
		if (lsd.getReturnCode() == Window.OK) {
			Object[] ret = lsd.getResult();
			if ((ret != null) && (ret.length == 1))
				return createGoodObject(ret[0]);

		}
		return null;
	}

	protected Object[] sort(Object[] selectableValues) {
		Arrays.sort(selectableValues, getComparator());
		return selectableValues;
	}

	protected Comparator<Object> getComparator() {
		return new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				return IC_AbstractForBrowser_Combo.this
						.toString(arg0)
						.compareTo(
								IC_AbstractForBrowser_Combo.this.toString(arg1));
			}
		};
	}

	protected Object createGoodObject(Object object) {
		return object;
	}

	protected abstract Object[] getSelectableValues();

	public String toString(Object value) {
		if (value == null)
			return "<none>";
		return value.toString();
	}

	protected LabelProvider getLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				return IC_AbstractForBrowser_Combo.this.toString(element);
			}

			@Override
			public Image getImage(Object element) {
				return IC_AbstractForBrowser_Combo.this.getImage(element);
			}
		};
	}

	protected Image getImage(Object element) {
		return null;
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

}
