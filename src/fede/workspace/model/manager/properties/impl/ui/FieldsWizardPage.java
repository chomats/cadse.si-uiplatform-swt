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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 */

public class FieldsWizardPage extends WizardPage  {

	IPage			page;
	private boolean	init;
	SWTUIPlatform	 _swtuiPlatform;

	/**
	 * Constructor for FieldsWizardPage.
	 * @param swtuiPlatform 
	 * 
	 * @param theCurrentItem
	 * @throws CadseException
	 */
	public FieldsWizardPage(SWTUIPlatform swtuiPlatform, IPage page) throws CadseException {
		super("wizardPage");
		this.page = page;
		// pageDesc.put("wizard-page",this);
		setTitle(page.getTitle());
		setDescription(page.getDescription());
		setPageComplete(page.isPageComplete());
		_swtuiPlatform = swtuiPlatform;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		this.init = true;
		
		try {
			setControl(_swtuiPlatform.createPage(page, parent));
			_swtuiPlatform.initAfterUI(page);
			// Reset visual value. and set UI_running at true
			_swtuiPlatform.resetVisualValue(page);
			setMessage(null, IPageController.ERROR);
			_swtuiPlatform.validateFields(null, null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.init = false;
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	@Override
	public void setMessage(String newMessage, int newType) {
		super.setMessage(newMessage, newType);
		if (newType == IPageController.ERROR) {
			setPageComplete(newMessage == null);
		}
	}

	public IPage getPageDesc() {
		return this.page;
	}

	public IPage getPage() {
		return this.page;
	}

	@Override
	public void dispose() {
		super.dispose();
		_swtuiPlatform.dispose(page);
	}

}