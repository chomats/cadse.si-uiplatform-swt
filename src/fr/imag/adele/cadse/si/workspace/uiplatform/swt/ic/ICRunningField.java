package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.ui.ic.IC_Abstract;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;

public class ICRunningField implements RuningInteractionController {
	public Item _ic;
	public UIPlatform _uiPlatform;

	@Override
	public void dispose() {
	}

	@Override
	final public UIField getUIField() {
		return ((IC_Abstract) _ic).getUIField();
	}

	@Override
	public void init() throws CadseException {
	}

	@Override
	public void initAfterUI() {
	}
	
	protected Item getItem() {
		return _uiPlatform.getItem(getUIField());
	}

}
