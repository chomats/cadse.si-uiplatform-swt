package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;

public class IC_WithDialogAction extends ICRunningField {

	public String _title;
	public String _message;

	public IC_WithDialogAction() {
	}

	public IC_WithDialogAction(String title2, String message2) {
		_title = title2;
		_message = message2;
	}

	@Override
	public void init() throws CadseException {
		if (_ic != null) {
			_title = _ic.getAttribute(CadseGCST.IC_WITH_TITLE_FOR_DIALOG_at_SELECT_TITLE_);
			_message = _ic.getAttribute(CadseGCST.IC_WITH_TITLE_FOR_DIALOG_at_SELECT_MESSAGE_);
		}
		if (_title == null || _title.length() == 0) {
			_title = "Add";
		}
	}

	final public String getMessage() {
		return _message;
	}

	final public String getTitle() {
		return _title;
	}

}
