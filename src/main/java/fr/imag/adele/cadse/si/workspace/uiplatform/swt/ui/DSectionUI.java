package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;

public class DSectionUI<IC extends RuningInteractionController> extends DAbstractField<IC> {

	boolean		makeColumnsEqualWidth	= false;
	private Section	_section;

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED;
		if (!_field.getFlag(Item.UI_NO_BORDER)) {
			style |= SWT.BORDER;
		}
		_section = _swtuiplatform.getToolkit().createSection(container, style);
		
		_section.setData(UIField.CADSE_MODEL_KEY, this);
		_section.setLayoutData(gridData);
		_section.setText(getLabel());
		//_section.setExpanded(true);
		
		Composite client = new Composite(_section, 0);

		GridLayout gridLayout = new GridLayout(0, makeColumnsEqualWidth);
		
		_swtuiplatform.getToolkit().adapt(client);
		_swtuiplatform.createChildrenControl(_page, this, client, gridLayout);
		_section.setClient(client);
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
		return this._section;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
