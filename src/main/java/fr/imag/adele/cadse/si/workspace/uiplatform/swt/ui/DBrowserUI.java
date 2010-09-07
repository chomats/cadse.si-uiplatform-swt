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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistField;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.core.util.Convert;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.Activator;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForBrowserOrCombo;

/**
 * Display a browser field which has a text field and a button "...".
 * 
 * @author chomats
 */
public class DBrowserUI<IC extends IC_ForBrowserOrCombo> extends DAbstractField<IC> implements IContentProposalListener {
	private Button _buttonBrowser;
	private ContentAssistField _contentAssistField;
	private Text _textControl;
	private Object _value;

	private String _currentValueTextToSend;

	private String _currentValueText;
	private boolean _sendNotification;

	@Override
	public void createControl(Composite container, int hspan) {

		Control swtControl;
		IFieldContenProposalProvider proposer = getContentAssistant();
		int style = _field.getStyle();
		if (!_field.isEditable()) {
			style |= SWT.READ_ONLY;
		}
		if (_field.isEditable() && proposer != null) {
			IControlContentAdapter contentAdapter = new ProposerTextContentAdapter(this, _swtuiplatform, proposer);

			_contentAssistField = new ContentAssistField(container, style, new IControlCreator() {
				public Control createControl(Composite controlParent, int style) {
					return new Text(controlParent, style);
				}
			}, contentAdapter, proposer.getContentProposalProvider(), proposer.getCommandId(), proposer
					.getAutoActivationCharacters());
			_contentAssistField.getContentAssistCommandAdapter().setProposalAcceptanceStyle(
					proposer.getProposalAcceptanceStyle());
			_textControl = (Text) _contentAssistField.getControl();
			swtControl = _contentAssistField.getLayoutControl();
			_contentAssistField.getContentAssistCommandAdapter().addContentProposalListener(this);
		}
		else {
			swtControl = _textControl = new Text(container, style);
		}
		_textControl.setData(UIField.CADSE_MODEL_KEY, this);
		// swtControl.setData(CADSE_MODEL_KEY, this);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan - 1;
		swtControl.setLayoutData(gd);

		if (_ic == null) {
			throw new CadseIllegalArgumentException("Cannot create ic for {0}({1})", this, getAttributeDefinition());
		}
		_textControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!_sendNotification) {
					return;
				}
				_currentValueTextToSend = _textControl.getText();
				if (_currentValueTextToSend.equals("")) {
					_currentValueTextToSend = null;
				}

				_sendNotification = false;
				sendModificationIfNeed(_currentValueTextToSend,
						(_ic.hasDeleteFunction() && _currentValueTextToSend == null) || _currentValueTextToSend != null);
				if (_currentValueTextToSend == null) {
					setUndefinedValue();
					_textControl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
					_value = null;
					_currentValueText = null;
				}
				else {
					_textControl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				}
				_sendNotification = true;

			}
		});

		_textControl.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == '\u0008' || e.character == '\u007f') { // 
					if (_ic.hasDeleteFunction() || _currentValueTextToSend == null
							|| "".equals(_currentValueTextToSend)) {
						_currentValueTextToSend = null;
						_sendNotification = false;
						sendModificationIfNeed(_currentValueTextToSend, _ic.hasDeleteFunction());
						setUndefinedValue();
						_sendNotification = true;
						_textControl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
						// _textControl.setSelection(0, 0);

						e.doit = false;
					}
					else {
						if (_currentValueTextToSend != null && _currentValueTextToSend.length() == 1) {
							_sendNotification = false;
							_currentValueTextToSend = "";
							_textControl.setText("EMPTY");
							_sendNotification = true;
							_textControl.setForeground(Display.getCurrent().getSystemColor(
									SWT.COLOR_WIDGET_LIGHT_SHADOW));
							_textControl.setSelection(0, 0);
							sendModificationIfNeed(_currentValueTextToSend, false);
							e.doit = false;
						}
					}
				}
				else if (e.keyCode == SWT.ARROW_RIGHT || e.keyCode == SWT.ARROW_LEFT) {
					if (_currentValueTextToSend == null || _currentValueTextToSend.length() == 0) {
						_textControl.setSelection(0, 0);
						e.doit = false;
					}
				}
				else if (e.character == '\u001b') { // Escape character

				}
				else if (e.character == '\n' || e.character == '\r') {
					sendModificationIfNeed(_currentValueTextToSend, true);
				}
				else {
					if (_currentValueTextToSend == null || _currentValueTextToSend.length() == 0) {
						_currentValueTextToSend = Character.toString(e.character);
						_textControl.setText(_currentValueTextToSend);
						_textControl.setSelection(_currentValueTextToSend.length(), _currentValueTextToSend.length());
						_textControl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
						sendModificationIfNeed(_currentValueTextToSend, false);
						e.doit = false;
					}
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		_textControl.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				if (_currentValueTextToSend != null) {
					sendModificationIfNeed(_currentValueTextToSend, true);
				}
			}
		});

		_buttonBrowser = new Button(container, SWT.PUSH);
		_buttonBrowser.setText(UIField.SELECT_BUTTON);
		_buttonBrowser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
				handleSelect();
			}
		});
		_buttonBrowser.setData(UIField.CADSE_MODEL_KEY, this);

		setEditable(_field.isEditable());
	}

	@Override
	public void setFocus() {
		if (_textControl != null && !_textControl.isDisposed()) {
			_textControl.setFocus();
		}
	}

	protected void cancelEditor() {

	}

	protected void deleteValue() {
		if (_value != null) {
			Object oldValue = _value;
			_value = null;
			_swtuiplatform.broadcastValueDeleted(_page, _field, oldValue);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		_textControl = null;
		_buttonBrowser = null;
		_value = null;
		_contentAssistField = null;
		_currentValueText = null;
		_currentValueTextToSend = null;
	}

	protected Object fromString(String value2) {
		return _ic.fromString(value2);
	}

	protected IFieldContenProposalProvider getContentAssistant() {
		RuningInteractionController uc = _ic;
		if (uc instanceof IFieldContenProposalProvider) {
			return (IFieldContenProposalProvider) uc;
		}
		return null;
	}

	@Override
	public Control getMainControl() {
		return this._textControl;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { _value };
	}

	public ItemType getType() {
		return CadseGCST.DBROWSER;
	}

	@Override
	public Object getVisualValue() {
		return _value;
	}

	protected void handleSelect() {
		Object ret = _ic.selectOrCreateValue(this._buttonBrowser.getShell());

		if (ret != null) {
			setVisualValue(ret, false);
			_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
		}
	}

	@Override
	public void setEditable(boolean v) {
		_textControl.setEditable(v);
		_buttonBrowser.setEnabled(v);
	}

	@Override
	public void setVisible(boolean v) {
		super.setVisible(v);
		if (_textControl != null) {
			_textControl.setVisible(v);
		}
		if (_buttonBrowser != null) {
			_buttonBrowser.setVisible(v);
		}
	}

	@Override
	public void setEnabled(boolean v) {
		_buttonBrowser.setEnabled(v);
		_textControl.setEnabled(v);
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		_value = visualValue;
		if (_swtuiplatform.isDisposed() || _textControl == null || _textControl.isDisposed()) {
			return;
		}

		final String localVisualValue = toString(_value);
		if (localVisualValue != null
				&& (localVisualValue.equals(_currentValueTextToSend) || localVisualValue == _currentValueTextToSend)) {
			return;
		}

		_sendNotification = sendNotification;
		try {
			_currentValueTextToSend = _currentValueText = localVisualValue;
			if (_textControl != null && !_textControl.isDisposed()) {
				if (_currentValueText == null) {
					_sendNotification = false;
					setUndefinedValue();
					_textControl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
					_textControl.setSelection(0, 0);
					if (sendNotification) {
						sendModificationIfNeed(_currentValueTextToSend, false);
					}
				}
				else if ("".equals(_currentValueText)) {
					_sendNotification = false;
					_textControl.setText("EMPTY");
					_textControl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
					_textControl.setSelection(0, 0);
					if (sendNotification) {
						sendModificationIfNeed(_currentValueTextToSend, false);
					}
				}
				else {
					_textControl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					_textControl.setText(_currentValueText);
					_textControl.setSelection(_currentValueText.length(), _currentValueText.length());

				}
			}
		}
		finally {
			_sendNotification = true;
		}
	}

	public String toString(Object object) {
		String ret;
		try {
			ret = _ic.toString(object);
		}
		catch (Throwable e) {
			ret = null;
			_swtuiplatform.setMessage("Internal error " + e.getClass().getCanonicalName() + ": " + e.getMessage(),
					UIPlatform.ERROR);
			Activator.getDefault().getLog().log(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Internal error in DBrowwserUI.toString", e));
		}
		return ret;
	}

	public void proposalAccepted(IContentProposal proposal) {
		IFieldContenProposalProvider proposer = getContentAssistant();

		if (proposer != null) {
			if (((Proposal) proposal).getValue() == null) {
				return;
			}
			if (proposer.getProposalAcceptanceStyle() == ContentProposalAdapter.PROPOSAL_INSERT) {
				return;
			}

			Object newValue = proposer.getValueFromProposal((Proposal) proposal);
			// setVisualValue(newValue);
			if (newValue != null) {
				_value = newValue;
				_swtuiplatform.broadcastValueChanged(_page, _field, newValue);

			}
			else {
				_swtuiplatform.broadcastValueDeleted(_page, _field, _value);
				_value = null;
			}

		}

	}

	protected synchronized void sendModificationIfNeed(String value, boolean send) {
		if (!_field.isEditable()) {
			return;
		}

		Object goodValue = fromString(value);
		if (!send && goodValue == null && value != null && !"".equals(value)) {
			return;
		}
		if (Convert.equals(goodValue, _value)) {
			return;
		}

		if (send) {
			// true, if error
			if (!_swtuiplatform.broadcastValueChanged(_page, _field, goodValue)) {
				_currentValueText = value;
				_value = goodValue;
			}
		}
		else {
			_swtuiplatform.setMessage(null, UIPlatform.ERROR);

			// validate value and if it's ok, test other fields
			// true if error
			if (!_swtuiplatform.validateValueChanged(_field, goodValue)) {
				_swtuiplatform.validateFields(_field, _page);
			}
		}
	}

	protected void setUndefinedValue() {
		Object hvalue = getModelController().getHeritableValue();
		if (hvalue == null) {
			_textControl.setText("UNDEFINED");
		}
		else {
			_textControl.setText("UNDEFINED (" + toString(hvalue) + ")");
		}
	}
}
