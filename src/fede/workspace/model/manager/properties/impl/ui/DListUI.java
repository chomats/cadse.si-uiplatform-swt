/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fede.workspace.model.manager.properties.impl.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import fede.workspace.model.manager.properties.impl.ic.IC_ForList;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 * value
 * <li>List&lt;Object&gt;<br>
 * </li>.
 * 
 * @author chomats
 */
public class DListUI<IC extends IC_ForList> extends DAbstractField<IC> {


	static boolean	_temp;

	private final class MyFilteredTree extends FilteredTree {

		private MyFilteredTree(Composite parent, int style, PatternFilter filter, boolean showfilter) {
			super(parent, style, filter);
		}

		@Override
		protected void createControl(Composite parent, int treeStyle) {
			// IMPORTANT : impossible sinon de changer la valeur de
			// showFilterControls dans un constructeur.
			// car la methode create control est appeler dans le contructreur du
			// parent...
			if (showFilterControls) {
				showFilterControls = _temp;
			}
			super.createControl(parent, treeStyle);
		}

		@Override
		protected Control createTreeControl(Composite parent, int style) {
			final Control ret = super.createTreeControl(parent, style);
			ret.setData(UIField.CADSE_MODEL_KEY, _field);
			return ret;
		}

	}

	/** The edit. */
	private boolean	add_remove	= true;
	private boolean	update;
	private boolean	order;
	private boolean	re_order;
	private boolean	showfilter;


	/** The elements. */
	List<Object>			fElements;

	/** The package table. */
	private FilteredTree	packageTable;

	/** The button add. */
	private Button			buttonAdd;

	/** The button remove. */
	private Button			buttonRemove;
	private Button			buttonUp;
	private Button			buttonDown;
	private Button			buttonReOrder;
	private Button			buttonEdit;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getVisualValue()
	 */
	@Override
	public Object getVisualValue() {
		return fElements;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#createControl(fr.imag.adele.cadse.core.ui.IPageController,
	 *      fr.imag.adele.cadse.core.ui.IFedeFormToolkit, java.lang.Object, int)
	 */
	@Override
	public void createControl(Composite ocontainer, int hspan) {

		order = _field.getAttribute(CadseGCST.DLIST_at_ORDER_BUTTON_);
		showfilter = _field.getAttribute(CadseGCST.DLIST_at_SHOW_FILTER_);
		add_remove = _field.getAttribute(CadseGCST.DLIST_at_EDITABLE_BUTTON_);
		update = _field.getAttribute(CadseGCST.DLIST_at_UPDATE_BUTTON_);
		GridData gd;
		Composite container = (Composite) ocontainer;
		// IMPORTANT : impossible sinon de changer la valeur de
		// showFilterControls dans un constructeur.
		// car la methode create control est appeler dans le contructreur du
		// parent...
		_temp = showfilter;
		packageTable = new MyFilteredTree(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL, createPatternFilter(),
				showfilter);

		if (getPage().isLast(_field.getAttributeDefinition())) {
			gd = new GridData(GridData.FILL_BOTH);
		} else {
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 100;
		}

		int vspan = 0;
		if (_field.isEditable()) {
			if (add_remove) {
				buttonAdd = new Button(container, SWT.PUSH);
				buttonAdd.setText(UIField.ADD_BUTTON);
				buttonAdd.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						handleAdd();
					}
				});
				buttonAdd.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						buttonAdd = null;
					}
				});
				buttonAdd.setData(UIField.CADSE_MODEL_KEY, _field);

				vspan++;

				buttonRemove = new Button(container, SWT.PUSH);
				buttonRemove.setText(UIField.REMOVE_BUTTON);
				buttonRemove.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleRemove((ITreeSelection) sel);
					}
				});
				buttonRemove.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						buttonRemove = null;
					}
				});
				buttonRemove.setData(UIField.CADSE_MODEL_KEY, _field);

				vspan++;

			}
			if (order) {
				buttonUp = new Button(container, SWT.PUSH);
				buttonUp.setText(UIField.UP_BUTTON);
				buttonUp.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleUp((ITreeSelection) sel);
					}
				});
				buttonUp.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						buttonUp = null;
					}
				});
				buttonUp.setData(UIField.CADSE_MODEL_KEY, _field);

				vspan++;

				buttonDown = new Button(container, SWT.PUSH);
				buttonDown.setText(UIField.DOWN_BUTTON);
				buttonDown.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleDown((ITreeSelection) sel);
					}
				});
				buttonDown.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						buttonDown = null;
					}
				});
				buttonDown.setData(UIField.CADSE_MODEL_KEY, _field);

				vspan++;
			}
			if (re_order) {
				buttonReOrder = new Button(container, SWT.PUSH);
				buttonReOrder.setText(UIField.RE_ORDER_BUTTON);
				buttonReOrder.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleReOrder((ITreeSelection) sel);
					}
				});
				buttonReOrder.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						buttonReOrder = null;
					}
				});
				buttonReOrder.setData(UIField.CADSE_MODEL_KEY, _field);
				vspan++;
			}
			if (update) {
				buttonEdit = new Button(container, SWT.PUSH);
				buttonEdit.setText(UIField.EDIT_BUTTON);
				buttonEdit.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleEdit((ITreeSelection) sel);
					}
				});
				buttonEdit.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						buttonEdit = null;
					}
				});
				buttonEdit.setData(UIField.CADSE_MODEL_KEY, _field);
				vspan++;
			}
		}
		if (vspan == 0) {
			vspan = 1;
		}
		gd.verticalSpan = vspan;
		gd.horizontalSpan = (vspan != 1) ? hspan - 1 : hspan; // un place pour
		// les boutons
		// ...
		packageTable.setLayoutData(gd);
		packageTable.getViewer().setLabelProvider(getLabelProvider());
		packageTable.getViewer().setContentProvider(_ic.getContentProvider());
	}

	protected void handleReOrder(ITreeSelection sel) {
		// TODO Auto-generated method stub

	}

	protected void handleEdit(ITreeSelection sel) {
		if (((IStructuredSelection) sel).size() == 0) {
			Object value = ((IStructuredSelection) sel).getFirstElement();

			int index = fElements.indexOf(value);
			if (index == -1) {
				return;
			}
			value = _ic.edit(packageTable.getShell(), value, index);
			if (value != null) {
				fElements.set(index, value);
				updateValue();
				_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
			}
		}

	}

	protected PatternFilter createPatternFilter() {
		return new PatternFilter();
	}

	/**
	 * Handle remove.
	 * 
	 * @param sel
	 *            the sel
	 * @param _swtuiplatform
	 *            the field controller
	 */
	protected void handleRemove(ITreeSelection sel) {
		Object[] obj = sel.toArray();
		if (obj == null) {
			return;
		}
		if (obj.length == 0) {
			return;
		}

		String error = _ic.canRemoveObject(obj);
		if (error != null) {
			_swtuiplatform.setMessage(error, IPageController.ERROR);
			return;
		}
		Object[] removedObj = _ic.removeObject(obj);
		for (Object o : removedObj) {
			fElements.remove(o);
		}
		setVisualValue(fElements);
		_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
	}

	/**
	 * Handle add.
	 * 
	 * @param _swtuiplatform
	 *            the field controller
	 */
	protected void handleAdd() {
		Object[] ret = selectOrCreateValue();
		if (ret != null) {
			String error = _ic.canAddObject(ret);
			if (error != null) {
				_swtuiplatform.setMessage(error, IPageController.ERROR);
				return;
			}
			ret = _ic.transAndAddObject(ret);
			if (fElements == null || (!(fElements instanceof ArrayList))) {
				fElements = new ArrayList<Object>();
			}
			fElements.addAll(Arrays.asList(ret));
			setVisualValue(fElements);
			_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
		}
	}

	protected void handleDown(ITreeSelection sel) {
		Object[] obj = sel.toArray();
		if (obj == null) {
			return;
		}
		if (obj.length == 0) {
			return;
		}
		if (_ic.moveDown(obj)) {
			updateValue();
		}

	}

	@Override
	public void updateValue() {
		ITreeSelection sel = (ITreeSelection) packageTable.getViewer().getSelection();
		super.updateValue();
		packageTable.getViewer().setSelection(sel, true);
	}

	protected void handleUp(ITreeSelection sel) {
		Object[] obj = sel.toArray();
		if (obj == null) {
			return;
		}
		if (obj.length == 0) {
			return;
		}
		if (_ic.moveUp(obj)) {
			updateValue();
		}
	}

	/**
	 * Select or create value.
	 * 
	 * @return the object[]
	 */
	private Object[] selectOrCreateValue() {
		return _ic.selectOrCreateValues(packageTable.getShell());
	}

	/**
	 * Gets the label provider.
	 * 
	 * @return the label provider
	 */
	protected ILabelProvider getLabelProvider() {
		return _ic.getLabelProvider();
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			visualValue = new ArrayList<Object>();
		}
		assert visualValue instanceof List;
		fElements = (List<Object>) visualValue;
		/*
		 * On peur fournir une liste non modifiable comme une liste vide.
		 */
		if (fElements != null && !(fElements instanceof ArrayList)) {
			fElements = new ArrayList<Object>(fElements);
		}
		TreeViewer viewer = packageTable.getViewer();
		if (viewer.getContentProvider() != null) {
			viewer.setInput(fElements);
		} else {

		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean v) {
		// packageTable.setEnabled(v || order);
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
		if (buttonDown != null) {
			buttonDown.setEnabled(v || order);
		}
		if (buttonUp != null) {
			buttonUp.setEnabled(v || order);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setEditable(boolean)
	 */
	@Override
	public void setEditable(boolean v) {
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
		if (buttonDown != null) {
			buttonDown.setEnabled(v);
		}
		if (buttonUp != null) {
			buttonUp.setEnabled(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.model.manager.properties.impl.ui.DAbstractField#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean v) {
		packageTable.setEnabled(v);
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
		if (buttonDown != null) {
			buttonDown.setEnabled(v);
		}
		if (buttonUp != null) {
			buttonUp.setEnabled(v);
		}
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}


	public ItemType getType() {
		return CadseGCST.DLIST;
	}


	@Override
	public Control getMainControl() {
		return this.packageTable;
	}

	@Override
	public Object[] getSelectedObjects() {
		return ((StructuredSelection) this.packageTable.getViewer().getSelection()).toArray();
	}


}
