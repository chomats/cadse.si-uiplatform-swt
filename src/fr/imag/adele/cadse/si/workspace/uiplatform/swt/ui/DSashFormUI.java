package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Assert;

public class DSashFormUI<IC extends RuningInteractionController> extends
		DAbstractField<IC> {

	boolean horizontal = true;
	SashForm sashForm;
	int hone = 50;

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm(container,
				horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
		sashForm.setLayoutData(gridData);
		sashForm.setData(UIField.CADSE_MODEL_KEY, _field);

		GridLayout gridLayout = new GridLayout(hspan, false);
		_swtuiplatform.createChildrenControl(this, sashForm, gridLayout);
		Assert.isNotNull(_children);
		Assert.isTrue(_children.length == 2);
		// must set after create two children
		sashForm.setWeights(new int[] { hone, 100 - hone });
		
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
