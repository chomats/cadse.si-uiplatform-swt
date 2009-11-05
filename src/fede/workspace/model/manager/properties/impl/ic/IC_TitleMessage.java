package fede.workspace.model.manager.properties.impl.ic;

import fr.imag.adele.cadse.core.ui.RuningInteractionController;

public interface IC_TitleMessage extends RuningInteractionController {

	/**
	 * 
	 * @return return the title of the window
	 */
	String getTitle();

	String getMessage();
}
