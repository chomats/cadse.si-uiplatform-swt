package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;

public class DGridUI extends DAbstractField {

	Composite composite;
	boolean makeColumnsEqualWidth = false;

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		int style = SWT.NULL;
		if (!_field.getFlag(Item.UI_NO_BORDER)) {
			style |= SWT.BORDER;
		}
		composite = _swtuiplatform.getToolkit().createComposite(container,
				style);
		composite.setData(UIField.CADSE_MODEL_KEY, _field);
		composite.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout(0, makeColumnsEqualWidth);

		_swtuiplatform.createChildrenControl(this, composite, gridLayout);
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setEditable(boolean v) {
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
	}

	public ItemType getType() {
		return null;
	}

	public void setMakeColumnsEqualWidth(boolean makeColumnsEqualWidth) {
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
	}

	@Override
	public Control getMainControl() {
		return this.composite;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
