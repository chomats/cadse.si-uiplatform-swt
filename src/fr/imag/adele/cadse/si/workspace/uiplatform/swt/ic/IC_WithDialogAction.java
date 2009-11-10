package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;

public class IC_WithDialogAction extends ICRunningField {

	public String	_title;
	public String	_message;

	public IC_WithDialogAction() {
	}

	public IC_WithDialogAction(String title2, String message2) {
		_title = title2;
		_message = message2;
	}

	@Override
	public void init() throws CadseException {
		if (_ic != null) {
			_title = _ic.getAttribute(CadseGCST.IC_ABSTRACT_TREE_DIALOG_FOR_LIST_BROWSER_COMBO_at_TITLE_);
			_message = _ic.getAttribute(CadseGCST.IC_ABSTRACT_TREE_DIALOG_FOR_LIST_BROWSER_COMBO_at_MESSAGE_);
		}
	}

	final public String getMessage() {
		return _message;
	}

	final public String getTitle() {
		return _title;
	}

}
