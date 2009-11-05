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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistField;

import fede.workspace.model.manager.properties.impl.ic.IC_ForBrowserOrCombo;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Convert;

/**
 * Display a browser field which has a text field and a button "...".
 * 
 * @author chomats
 * 
 */
public class DBrowserUI extends DAbstractField implements
		IContentProposalListener {
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
			IControlContentAdapter contentAdapter = new ProposerTextContentAdapter(
					this, _swtuiplatform, proposer);

			_contentAssistField = new ContentAssistField(container, style,
					new IControlCreator() {
						public Control createControl(Composite controlParent,
								int style) {
							return new Text(controlParent, style);
						}
					}, contentAdapter, proposer.getContentProposalProvider(),
					proposer.getCommandId(), proposer
							.getAutoActivationCharacters());
			_contentAssistField.getContentAssistCommandAdapter()
					.setProposalAcceptanceStyle(
							proposer.getProposalAcceptanceStyle());
			_textControl = (Text) _contentAssistField.getControl();
			swtControl = _contentAssistField.getLayoutControl();
			_contentAssistField.getContentAssistCommandAdapter()
					.addContentProposalListener(this);
		} else {
			swtControl = _textControl = new Text(container, style);
		}
		_textControl.setData(UIField.CADSE_MODEL_KEY, _field);
		// swtControl.setData(CADSE_MODEL_KEY, this);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan - 1;
		swtControl.setLayoutData(gd);

		if (getInteractionController().hasDeleteFunction()) {
			_textControl.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 8 || e.keyCode == 127) {
						deleteValue();
					}
				}

				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}

			});
		}
		_textControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!_sendNotification) {
					return;
				}
				_currentValueTextToSend = _textControl.getText();
				sendModificationIfNeed(_currentValueTextToSend, false);

			}
		});

		_textControl.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == '\u001b') { // Escape character
					cancelEditor();
				} else if (e.character == '\n' || e.character == '\r') {
					sendModificationIfNeed(_currentValueTextToSend, true);
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		_textControl.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				if (_currentValueTextToSend != null)
					sendModificationIfNeed(_currentValueTextToSend, true);
			}
		});

		_buttonBrowser = new Button(container, SWT.PUSH);
		_buttonBrowser.setText(UIField.SELECT_BUTTON);
		_buttonBrowser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(
					@SuppressWarnings("unused") SelectionEvent e) {
				handleSelect();
			}
		});
		_buttonBrowser.setData(UIField.CADSE_MODEL_KEY, this);

		setEditable(_field.isEditable());
	}

	protected void cancelEditor() {
		// TODO Auto-generated method stub

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
		return getInteractionController().fromString(value2);
	}

	protected IFieldContenProposalProvider getContentAssistant() {
		RuningInteractionController uc = getInteractionController();
		if (uc instanceof IFieldContenProposalProvider) {
			return (IFieldContenProposalProvider) uc;
		}
		return null;
	}

	public IC_ForBrowserOrCombo getInteractionController() {
		return (IC_ForBrowserOrCombo) _ic;
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
		Object ret = getInteractionController().selectOrCreateValue(
				this._buttonBrowser.getShell());

		if (ret != null) {
			setVisualValue(ret, false);
			_swtuiplatform.broadcastValueChanged(_page, _field,
					getVisualValue());
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

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		_value = visualValue;
		if (_textControl == null || _textControl.isDisposed())
			return;

		final String valueText = toString(_value);
		if (valueText.equals(_currentValueText)) {
			return;
		}
		_currentValueText = valueText;
		_sendNotification = sendNotification;
		try {
			_textControl.setText(valueText);
			_textControl.setSelection(valueText.length(), valueText.length());
		} finally {
			_sendNotification = true;
		}
	}

	public String toString(Object object) {
		String ret;
		try {
			ret = getInteractionController().toString(object);
			if (ret == null) {
				ret = "";
			}
		} catch (Throwable e) {
			ret = "<invalid value>";
			_swtuiplatform.setMessage("Internal error "
					+ e.getClass().getCanonicalName() + ": " + e.getMessage(),
					IPageController.ERROR);
			WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID,
					"Internal error in DBrowwserUI.toString", e));
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

			Object newValue = proposer
					.getValueFromProposal((Proposal) proposal);
			// setVisualValue(newValue);
			if (newValue != null) {
				_value = newValue;
				_swtuiplatform.broadcastValueChanged(_page, _field, newValue);

			} else {
				_swtuiplatform.broadcastValueDeleted(_page, _field, _value);
				_value = null;
			}

		}

	}

	protected synchronized void sendModificationIfNeed(String value,
			boolean send) {
		if (!_field.isEditable()) {
			return;
		}

		Object goodValue = fromString(value);
		if (Convert.equals(goodValue, _value)) {
			return;
		}

		if (send) {
			// true, if error
			if (!_swtuiplatform.broadcastValueChanged(_page, _field, goodValue)) {
				_currentValueText = value;
				_value = goodValue;
			}
		} else {
			_swtuiplatform.setMessage(null, IPageController.ERROR);

			// validate value and if it's ok, test other fields
			// true if error
			if (!_swtuiplatform.validateValueChanged(_field, goodValue)) {
				_swtuiplatform.validateFields(_field, _page);
			}
		}
	}
}