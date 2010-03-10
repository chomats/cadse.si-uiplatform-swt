package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;

public class DGridUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	Composite composite;
	boolean makeColumnsEqualWidth = false;
	int columnNb = 0;

	public int getColumnNb() {
		return columnNb;
	}

	public void setColumnNb(int columnNb) {
		this.columnNb = columnNb;
	}

	public boolean isMakeColumnsEqualWidth() {
		return makeColumnsEqualWidth;
	}

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		int style = SWT.NULL;
		if (!_field.getFlag(Item.UI_NO_BORDER)) {
			style |= SWT.BORDER;
		}
		composite = _swtuiplatform.getToolkit().createComposite(container, style);
		composite.setData(UIField.CADSE_MODEL_KEY, this);
		composite.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout(columnNb, makeColumnsEqualWidth);

		_swtuiplatform.createChildrenControl(_page, this, composite, gridLayout);
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setEditable(boolean v) {
	}

	@Override
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
