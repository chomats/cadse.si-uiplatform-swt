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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;

public class FieldsPreferencePage extends PropertyPage {

	protected SWTUIPlatform	_swtPlatform	= null;

	public FieldsPreferencePage() {
		this._swtPlatform = new SWTUIPlatform();
	}

	@Override
	protected Control createContents(Composite parent) {

		if (_swtPlatform != null && _swtPlatform.getPages() != null) {
			try {
				return _swtPlatform.createControlPage(parent);
			} catch (CadseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		Text t = new Text(container, SWT.NONE);
		t.setText("No item selected.");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER + GridData.VERTICAL_ALIGN_CENTER);
		t.setLayoutData(gd);
		return container;
	}

	protected void updateStatus(String message) {
		setErrorMessage(message);
	}

	public void setController(Pages pages) {
		_swtPlatform.setPages(pages);
	}

	@Override
	public boolean performOk() {
		try {
			CadseCore.getCadseDomain().beginOperation("WizardController.performFinish");
			_swtPlatform.doFinish(null);
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			CadseCore.getCadseDomain().endOperation();
		}
		return true;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (_swtPlatform != null) {
			_swtPlatform.dispose();
		}
	}
}
