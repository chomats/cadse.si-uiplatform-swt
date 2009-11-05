package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;

public class DSashFormUI<IC extends RuningInteractionController> extends
		DAbstractField<IC> {

	boolean horizontal = true;
	SashForm sashForm;
	int hone = 50;

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm((Composite) container,
				horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
		sashForm.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout(hspan, false);
		_swtuiplatform.createFieldsControl(_swtuiplatform, sashForm, children,
				hspan, gridLayout);
		sashForm.setWeights(new int[] { hone, 100 - hone });
		sashForm.setData(UIField.CADSE_MODEL_KEY, _field);
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

	public void setWeight(int hone) {
		assert hone >= 0 && hone <= 100;
		this.hone = hone;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	@Override
	public Control getMainControl() {
		return this.sashForm;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
