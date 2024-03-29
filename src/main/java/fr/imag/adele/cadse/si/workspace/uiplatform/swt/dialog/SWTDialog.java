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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.dialog;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.WizardController;

public abstract class SWTDialog {

	protected IPage			_page;
	protected SWTUIPlatform	_swtuiPlatforms;
	private int				_pageWidth	= 800;
	private int				_pageHeight	= 500;

	/**
	 * Create the dialog structure... DSashFormUI DGrillUI FieldExtends DGrillUI
	 * fieldTWVersion fieldDescription
	 * 
	 * @param ret
	 * @generated
	 */
	public SWTDialog(SWTUIPlatform swtuiPlatforms, String title, String label) {
		_swtuiPlatforms = swtuiPlatforms;
		_page = swtuiPlatforms.createPageDescription(title, label);
	}

	public void open(final Shell shell) {
		/**
		 * Create a new display wen call getDefault(). Worksbench is not
		 * started. This method is called by federation in start level.
		 * 
		 */
		Display d = PlatformUI.getWorkbench().getDisplay();

		d.syncExec(new Runnable() {
			public void run() {
				try {
					_swtuiPlatforms.open(shell, _page, getFinishAction(), _pageWidth, _pageHeight, false);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void open(final Shell shell, final WizardController wc) {
		Display d = PlatformUI.getWorkbench().getDisplay();

		d.syncExec(new Runnable() {
			public void run() {
				try {
					_swtuiPlatforms.open(shell, getFinishAction(), _pageWidth, _pageHeight, false, wc);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Sets the size of all pages. The given size takes precedence over computed
	 * sizes.
	 * 
	 * @param width
	 *            the page width
	 * @param height
	 *            the page height
	 * @see #setPageSize(Point)
	 */
	public void setPageSize(int width, int height) {
		_pageWidth = width;
		_pageHeight = height;
	}

	protected void addLast(UIRunningField<?>... runningFields) {
		IAttributeType<?>[] attr = new IAttributeType[runningFields.length];
		for (int i = 0; i < attr.length; i++) {
			attr[i] = runningFields[i].getAttributeDefinition();
		}
		_page.addLast(attr);
	}

	protected abstract IActionPage getFinishAction();
	
	public SWTUIPlatform getSWTUIPlatform() {
		return _swtuiPlatforms;
	}
}
