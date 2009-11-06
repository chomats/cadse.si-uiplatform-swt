package fr.imag.adele.cadse.si.workspace.uiplatform.swt;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWizard;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.DefaultItemManager;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.dialog.CadseDialog;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService;

public class SWTServiceImpl implements SWTService {
	
	@Override
	public CadseRuntime[] openDialog(boolean askToErase) {
		return CadseDialog.openDialog(askToErase);
	}

	@Override
	public boolean showCreateWizard(Shell parentShell, Item parent, LinkType lt, ItemType destItemType) throws CadseException {
		final IItemManager ip = destItemType.getItemManager();
		if (ip == null) {
			return false;
		}
		if (ip.isAbstract(parent, lt)) {
			return false;
		}
		Pages f = destItemType.getGoodCreationPage(parent, destItemType, lt);
		
		if (f != null) {
			SWTUIPlatform swtuiPlatform = new SWTUIPlatform();

			swtuiPlatform.setPages(f);
			swtuiPlatform.createCreationWizard(parentShell);
			return true;
		}
		
		return false;
	}

	@Override
	public void showCreateWizardWithError(Shell parentShell, Item parent, LinkType lt, ItemType destItemType) throws CadseException {
		try {
			
			if (showCreateWizard(parentShell, parent, lt, destItemType))
				return;
			String message;
			if (parent != null) {
				message = MessageFormat
						.format(
								"Cannot create an item of type {0} from {1} of type {2} the link {3} : no pages found",
								destItemType.getName(), parent.getName(),
								parent.getType().getName(), lt.getName());
			} else {
				message = MessageFormat
						.format(
								"Cannot create an item of type {0} : no pages found",
								destItemType.getName());
			}

			MessageDialog.openError(parentShell,
					"Cannot create wizard  : no pages found", message);
			WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0,
					message, null));
		} catch (Throwable e1) {
			e1.printStackTrace();
			String message;
			if (parent != null) {
				message = MessageFormat
						.format(
								"Cannot create an item of type {0} from {1} of type {2} the link {3}",
								destItemType.getName(), parent.getName(),
								parent.getType().getName(), lt.getName());
			} else {
				message = MessageFormat.format(
						"Cannot create an item of type {0}", destItemType
								.getName());
			}

			MessageDialog.openError(parentShell,
					"Cannot create wizard", message);
			WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0,
					message, e1));
		}
	}
}
