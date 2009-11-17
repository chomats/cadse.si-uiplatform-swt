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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;

public class DTextUI<IC extends RuningInteractionController> extends DAbstractField<IC> implements
		IContentProposalListener {

	private ContentAssistCommandAdapter	_contentAssistField;
	private String						_currentValue;
	private String						_currentValueToSend;
	private Text						_textControl;
	public String						_toolTips			= null;
	public int							_vspan				= 1;
	private boolean						_sendNotification	= true;

	public String __getVisualValue() {
		return (_textControl).getText();
	}

	@Override
	public void createControl(Composite ocontainer, int hspan) {

		final IFieldContenProposalProvider proposer = getContentAssistant();
		// Container ocontainer;
		int style = 0;
		style |= (_field.getAttributeWithDefaultValue(CadseGCST.DTEXT_at_MULTI_LINE_,false)) ? SWT.MULTI : SWT.SINGLE;
		style |= (_field.getAttributeWithDefaultValue(CadseGCST.DTEXT_at_WRAP_LINE_, false)) ? SWT.WRAP : 0;
		style |= (_field.getAttributeWithDefaultValue(CadseGCST.DTEXT_at_NO_BORDER_, false)) ? 0 : SWT.BORDER;
		style |= (_field.getFlag(Item.UI_HSCROLL)) ? 0 : SWT.H_SCROLL;
		style |= (_field.getFlag(Item.UI_VSCROLL)) ? 0 : SWT.V_SCROLL;
		if (!_field.isEditable()) {
			style |= SWT.READ_ONLY;
		}

		_textControl = _swtuiplatform.getToolkit().createText(ocontainer, "", style);
		_textControl.setData(UIField.CADSE_MODEL_KEY, _field);
		if (_field.isEditable() && proposer != null) {
			IControlContentAdapter contentAdapter = new ProposerTextContentAdapter(this, _swtuiplatform, proposer);
			_contentAssistField = new ContentAssistCommandAdapter(_textControl, contentAdapter, proposer
					.getContentProposalProvider(), proposer.getCommandId(), proposer.getAutoActivationCharacters(),
					true);
			_contentAssistField.setProposalAcceptanceStyle(proposer.getProposalAcceptanceStyle());
			_contentAssistField.addContentProposalListener(this);
		}

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		gd.verticalSpan = this._vspan;
		if (gd.verticalSpan != 1) {
			gd.verticalAlignment = GridData.FILL;
			gd.grabExcessVerticalSpace = true;
		}
		_textControl.setLayoutData(gd);

		_textControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!_sendNotification) {
					return;
				}
				setTextModified();
			}
		});

		_textControl.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == '\u001b') { // Escape character
					cancelEditor();
				} else if (e.character == '\n' || e.character == '\r') {
					sendModificationIfNeed(_currentValueToSend, true);
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		_textControl.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				sendModificationIfNeed(_currentValueToSend, true);
			}
		});

		// textControl.setEnabled(getLocal(IFieldDescription.ENABLE,true));
		// textControl.setEditable(getLocal(IFieldDescription.EDITABLE,true));

		if (_toolTips != null) {
			_textControl.setToolTipText(_toolTips);
		}
	}

	protected void cancelEditor() {
		_sendNotification = false;
		_textControl.setText(_currentValue);
		_currentValueToSend = _currentValue;
		_textControl.setSelection(_currentValue.length(), _currentValue.length());
		_sendNotification = true;
	}

	@Override
	public void dispose() {
		super.dispose();
		_textControl = null;
		_contentAssistField = null;
		_currentValue = null;
		_currentValueToSend = null;
	}

	protected IFieldContenProposalProvider getContentAssistant() {
		if (_ic instanceof IFieldContenProposalProvider) {
			return (IFieldContenProposalProvider) _ic;
		}
		return null;
	}

	@Override
	public Control getMainControl() {
		return _textControl;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { this._currentValue };
	}

	public ItemType getType() {
		return CadseGCST.DTEXT;
	}

	@Override
	public Object getVisualValue() {
		if (_currentValueToSend == null) {
			_currentValueToSend = __getVisualValue();
		}
		return _currentValueToSend;
	}

	@Override
	public void setEditable(boolean v) {
		if (_textControl == null) {
			return;
		}
		this._textControl.setEditable(v);
		if (this._labelWidget != null) {
			this._labelWidget.setEnabled(v);
		}
	}

	@Override
	public void setVisible(boolean v) {
		this._textControl.setVisible(v);
	}

	protected synchronized void sendModificationIfNeed(String value, boolean send) {
		if (!_field.isEditable()) {
			return;
		}
		if (send) {
			// true, if error
			if (!_swtuiplatform.broadcastValueChanged(_page, _field, value)) {
				_currentValue = value;
			}
		} else {
			_swtuiplatform.setMessage(null, UIPlatform.ERROR);

			// validate value and if it's ok, test other fields
			// true if error
			if (!_swtuiplatform.validateValueChanged(_field, value)) {
				_swtuiplatform.validateFields(_field, _page);
			}
		}
	}

	@Override
	public void setEnabled(boolean v) {
		super.setEnabled(v);
		this._textControl.setEnabled(v);
	}

	protected synchronized void setTextModified() {
		_currentValueToSend = __getVisualValue();
		sendModificationIfNeed(_currentValueToSend, false);
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (_swtuiplatform.isDisposed()) {
			return;
		}
		if (visualValue == null) {
			visualValue = "";
		}
		if (visualValue.equals(_currentValueToSend)) {
			return; // to do nothing;
		}

		_sendNotification = sendNotification;
		try {
			_currentValueToSend = _currentValue = visualValue.toString();
			if (_textControl != null && !_textControl.isDisposed()) {
				_textControl.setText(_currentValue);
			}
		} finally {
			_sendNotification = true;
		}
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
				_currentValue = newValue.toString();
				_swtuiplatform.broadcastValueChanged(_page, _field, newValue);

			} else {
				_swtuiplatform.broadcastValueDeleted(_page, _field, _currentValue);
				_currentValue = null;
			}

		}

	}

}
