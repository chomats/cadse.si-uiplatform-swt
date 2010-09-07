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

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;

final class ProposerTextContentAdapter extends TextContentAdapter {
	private final IFieldContenProposalProvider	proposer;
	UIPlatform									globalUIController;
	UIRunningField								controller;

	ProposerTextContentAdapter(UIRunningField controller, UIPlatform globalUIController,
			IFieldContenProposalProvider proposer) {
		this.proposer = proposer;
		this.globalUIController = globalUIController;
		this.controller = controller;

	}

	@Override
	public void setControlContents(Control control, String text, int cursorPosition) {
		super.setControlContents(control, text, cursorPosition);

		// deprecated....
		Object newValue = proposer.setControlContents(text);
		if (newValue == null) {
			return;
		}

		// controller.setVisualValue(newValue);
		globalUIController.broadcastValueChanged(null, controller.getUIField(), newValue);
	}

	@Override
	public void insertControlContents(Control control, String text, int cursorPosition) {
		super.insertControlContents(control, text, cursorPosition);
		Object newValue = proposer.setControlContents(getControlContents(control));
		if (newValue == null) {
			return;
		}
		controller.setVisualValue(newValue);
	}

}
