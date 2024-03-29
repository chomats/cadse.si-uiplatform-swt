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

/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForChooseFile;

/**
 * UI to set the file or folder chooser. TODO : implements delete
 */
public class DChooseFileUI<IC extends IC_ForChooseFile> extends DAbstractField<IC> {

	public String choosemsg;

	private Text textWidget;
	private Button btExternalFileWidget;
	private Button btWorkspaceWidget;
	private Button btExternalFolderWidget;

	protected boolean textWidgetChanged;

	public void setPathText(String text) {
		if (text != null) {
			textWidget.setText(text);
		}
	}

	/**
	 * Gets the source attachment path chosen by the user
	 */
	public IPath getPath() {
		if (textWidget.getText().length() == 0) {
			return null;
		}
		return getFilePath();
	}

	@Override
	public void createControl(Composite parent, int hspan) {

		Composite composite = parent;
		GridData gd = new GridData(GridData.FILL, GridData.CENTER, false, false, hspan - 1, getVSpan());
		gd.grabExcessHorizontalSpace = true;

		textWidget = new Text(composite, SWT.LEFT + SWT.BORDER);
		textWidget.setData(UIField.CADSE_MODEL_KEY, this);
		textWidget.setLayoutData(gd);
		textWidget.addKeyListener(new KeyAdapter() {
			/*
			 * @see KeyListener.keyPressed
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				// If there has been a key pressed then mark as dirty
				textWidgetChanged = true;
			}
		});
		textWidget.setData(UIField.CADSE_MODEL_KEY, this);

		textWidget.addFocusListener(new FocusAdapter() {
			/*
			 * @see FocusListener.focusLost(FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e) {
				// Clear the flag to prevent constant update
				if (textWidgetChanged) {
					textWidgetChanged = false;
					_swtuiplatform.broadcastValueChanged(_page, _field, getFilePath());
				}
			}
		});

		int kind = _ic.getKind();

		if ((kind & (IC_ForChooseFile.FILE_EXT)) != 0) {
			btExternalFileWidget = new Button(composite, SWT.PUSH);
			btExternalFileWidget.setText("External &File...");
			btExternalFileWidget.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
					handleExternalFile();
				}
			});
			btExternalFileWidget.setData(UIField.CADSE_MODEL_KEY, this);
		}
		if ((kind & (IC_ForChooseFile.WORKSPACE)) != 0) {
			btWorkspaceWidget = new Button(composite, SWT.PUSH);
			btWorkspaceWidget.setText("&Workspace...");
			btWorkspaceWidget.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
					handleWorkspace();
				}
			});
			btWorkspaceWidget.setData(UIField.CADSE_MODEL_KEY, this);
		}
		if ((kind & (IC_ForChooseFile.FOLDER_EXT)) != 0) {
			btExternalFolderWidget = new Button(composite, SWT.PUSH);
			btExternalFolderWidget.setText("External F&older...");
			btExternalFolderWidget.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
					handleExternalFolder();
				}
			});
			btExternalFolderWidget.setData(UIField.CADSE_MODEL_KEY, this);
		}
	}

	protected void handleWorkspace() {
		IPath jarFilePath = _ic.selectWorkspaceFile(textWidget.getShell(), choosemsg, getPath());
		if (jarFilePath != null) {
			_swtuiplatform.broadcastValueChanged(_page, _field, jarFilePath);
			textWidget.setText(jarFilePath.toString());
		}
	}

	protected void handleExternalFolder() {
		IPath folderPath = _ic.selectExternalFolder(textWidget.getShell(), choosemsg, getPath());
		if (folderPath != null) {
			_swtuiplatform.broadcastValueChanged(_page, _field, folderPath);
			textWidget.setText(folderPath.toString());
		}
	}

	protected void handleExternalFile() {
		IPath jarFilePath = _ic.selectExternalFile(textWidget.getShell(), choosemsg, getPath());
		if (jarFilePath != null) {
			_swtuiplatform.broadcastValueChanged(_page, _field, jarFilePath);
			textWidget.setText(jarFilePath.toString());
		}
	}

	protected IPath getFilePath() {
		String v = textWidget.getText();
		if (v.length() == 0) {
			return Path.EMPTY;
		}
		return Path.fromOSString(v).makeAbsolute();
	}

	private void setFilePath(IPath path) {
		if (path != null) {
			textWidget.setText(path.toOSString());
		}
	}

	@Override
	public int getVSpan() {
		int v = 0;
		int kind = _ic.getKind();
		if ((kind & (IC_ForChooseFile.FILE_EXT)) != 0) {
			v++;
		}
		if ((kind & (IC_ForChooseFile.WORKSPACE)) != 0) {
			v++;
		}
		if ((kind & (IC_ForChooseFile.FOLDER_EXT)) != 0) {
			v++;
		}
		if (v == 0) {
			v++;
		}
		return v;
	}

	@Override
	public Object getVisualValue() {
		return getFilePath();
	}

	@Override
	public void setEditable(boolean v) {
		this.textWidget.setEditable(v);
		if (btExternalFileWidget != null) {
			btExternalFileWidget.setEnabled(v);
		}
		if (btWorkspaceWidget != null) {
			btWorkspaceWidget.setEnabled(v);
		}
		if (btExternalFolderWidget != null) {
			btExternalFolderWidget.setEnabled(v);
		}
	}

	@Override
	public void setEnabled(boolean v) {
		this.textWidget.setEnabled(v);
		if (btExternalFileWidget != null) {
			btExternalFileWidget.setEnabled(v);
		}
		if (btWorkspaceWidget != null) {
			btWorkspaceWidget.setEnabled(v);
		}
		if (btExternalFolderWidget != null) {
			btExternalFolderWidget.setEnabled(v);
		}
	}

	@Override
	public void setVisible(boolean v) {
		this.textWidget.setVisible(v);
		if (btExternalFileWidget != null) {
			btExternalFileWidget.setVisible(v);
		}
		if (btWorkspaceWidget != null) {
			btWorkspaceWidget.setVisible(v);
		}
		if (btExternalFolderWidget != null) {
			btExternalFolderWidget.setVisible(v);
		}
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		setFilePath((IPath) visualValue);
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getMainControl() {
		return this.textWidget;
	}

	@Override
	public Object[] getSelectedObjects() {
		// TODO Auto-generated method stub
		return null;
	}
}
