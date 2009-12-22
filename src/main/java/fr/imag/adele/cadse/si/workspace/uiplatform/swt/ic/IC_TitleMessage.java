package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import fr.imag.adele.cadse.core.ui.RuningInteractionController;

public interface IC_TitleMessage extends RuningInteractionController {

	/**
	 * 
	 * @return return the title of the window
	 */
	String getTitle();

	String getMessage();
}
