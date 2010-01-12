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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.core.util.ArraysUtil;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_TreeCheckedUI;

/**
 * This class handles a tabbed panel containing a list of selected items
 * belonging to the configuration, choosen from the associated domain.
 * 
 * @author vega
 * 
 */
public class DCheckedTreeUI<IC extends IC_TreeCheckedUI> extends DAbstractField<IC> implements SelectionListener,
		TreeListener {

	public boolean					_fillBoth			= false;

	public boolean					_selectDelectButton	= true;

	private Object[]				_sources;
	private Set<Object>				_sources_selected	= new HashSet<Object>();

	private Map<Object, TreeItem[]>	_treeItems;
	private Tree					_treeObjects;

	public void addNode(final Object item) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				addNode_(item);
			};
		});
	}

	public void addNode_(final Object item) {
		if (this._treeObjects.isDisposed()) {
			return;
		}

		Object parentobj = _ic.getParent(item);
		if (parentobj == null) {
			return;
		}
		TreeItem[] ti = _treeItems.get(parentobj);
		if (ti == null) {
			addNode_(parentobj);
			ti = _treeItems.get(parentobj);
			if (ti == null) {
				return;
			}
		}
		for (TreeItem treeItem : ti) {
			if (treeItem.getExpanded()) {
				createTreeItem(item, treeItem);
			}
		}

	}

	/**
	 * 
	 * @param object
	 *            l'object deselectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectDeselected(Object object) {
		return _ic.canObjectDeselected(object);
	}

	/**
	 * 
	 * @param object
	 *            l'object selectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectSelected(Object object) {
		return _ic.canObjectSelected(object);
	}

	@Override
	public void createControl(Composite container, int hspan) {
		GridData gd;

		_treeObjects = new Tree(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		_treeObjects.addSelectionListener(this);
		_treeObjects.addTreeListener(this);
		_treeObjects.setData(UIField.CADSE_MODEL_KEY, _field);

		int fillkind = GridData.FILL_BOTH;
		// /if (fillBoth)
		// fillkind = GridData.FILL_BOTH;

		gd = new GridData(fillkind);
		gd.horizontalSpan = hspan - 1;
		gd.verticalSpan = _field.isEditable() ? 3 : 2;
		gd.minimumHeight = 150;

		if (!_fillBoth) {
			gd.minimumHeight = 150;
			// gd.grabExcessVerticalSpace = true;
		} else {
			gd.grabExcessVerticalSpace = false;
		}
		_treeObjects.setLayoutData(gd);

		setSource(_ic.getSources());

		if (_selectDelectButton) {
			Button selectAll = _swtuiplatform.getToolkit().createButton(container, "Select All", SWT.PUSH);
			gd = new GridData(GridData.CENTER);
			selectAll.setLayoutData(gd);
			selectAll.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					selectAll();
				}
			});
			selectAll.setData(this);

			Button deselectAll = _swtuiplatform.getToolkit().createButton(container, "Deselect All", SWT.PUSH);
			gd = new GridData(GridData.CENTER);
			deselectAll.setLayoutData(gd);
			deselectAll.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					deselectAll();
				}
			});
			deselectAll.setData(this);
		}
		if (_field.isEditable()) {
			Button editButton = _swtuiplatform.getToolkit().createButton(container, "Edit", SWT.PUSH);
			gd = new GridData(GridData.CENTER);
			editButton.setLayoutData(gd);
			editButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					edit();
				}
			});
			editButton.setData(this);
		}

		createContextMenu(_treeObjects);
	}

	private void createItemFromObjects() {
		createItemFromObjects(getSource(), null);

		for (Object obj : new ArrayList<Object>(_sources_selected)) {
			TreeItem[] ti = _treeItems.get(obj);
			if (ti == null) {
				_sources_selected.remove(obj);
				continue;
			}
			for (TreeItem treeItem : ti) {
				treeItem.setChecked(true);
			}

		}
	}

	private void createItemFromObjects(Object[] objects, TreeItem parent) {
		TreeItem ti = null;
		for (Object object : objects) {
			ti = createTreeItem(object, parent);
			Object[] childobjects = getChildren(object);
			if (childobjects != null && childobjects.length != 0) {
				Arrays.sort(childobjects, new Comparator<Object>() {
					public int compare(Object o1, Object o2) {
						return toStringFromObject(o1).compareTo(toStringFromObject(o2));
					}
				});
				createItemFromObjects(childobjects, ti);
			}
		}
		if (ti != null && parent == null && objects.length == 1) {
			TreeItem[] items = ti.getItems();
			if (items != null && items.length > 0) {
				ti.setExpanded(true);
			}
		}
	}

	protected TreeItem createTreeItem(Object obj, TreeItem parent) {
		TreeItem ti;
		if (parent == null) {
			ti = new TreeItem(this._treeObjects, treeItemDefaultStyle());
		} else {
			ti = new TreeItem(parent, treeItemDefaultStyle());
		}
		ti.setText(toStringFromObject(obj));
		ti.setData(obj);
		ti.setImage(toImageFromObject(obj));

		_treeItems.put(obj, ArraysUtil.add(TreeItem.class, _treeItems.get(obj), ti));
		return ti;
	}

	protected void deselectAll() {
		for (TreeItem[] ti : this._treeItems.values()) {
			for (TreeItem treeItem : ti) {
				treeItem.setChecked(false);
			}
		}
		_swtuiplatform.broadcastSubValueRemoved(_page, _field, getVisualValue());
		_sources_selected.clear();
	}

	@Override
	public void dispose() {
		super.dispose();
		_treeItems = null;
		_treeObjects = null;
		_sources_selected.clear();
		_sources = null;
	}

	protected void edit() {
		Object o = null;
		TreeItem[] sel = _treeObjects.getSelection();
		if (sel != null) {
			for (int i = 0; i < sel.length; i++) {
				if (sel[i].getChecked()) {
					o = sel[i].getData();
				}
			}
			if (o != null) {
				_ic.edit(o);
			}
		}

	}

	public Object[] getChildren(Object obj) {
		return _ic.getChildren(obj);
	}

	protected Object[] getDataSelection() {
		TreeItem[] treeitemselection = _treeObjects.getSelection();
		Object[] dataselection = null;
		if (treeitemselection != null) {
			dataselection = new Object[treeitemselection.length];
			for (int i = 0; i < treeitemselection.length; i++) {
				dataselection[i] = treeitemselection[i].getData();
			}
		}
		return dataselection;
	}

	@Override
	public Control getMainControl() {
		return this._treeObjects;
	}

	@Override
	public Object[] getSelectedObjects() {
		return _sources_selected.toArray();
	}

	public Object[] getSource() {
		return _sources;
	}

	public ItemType getType() {
		return CadseGCST.DCHECKED_TREE;
	}

	@Override
	public Object getVisualValue() {
		return this._sources_selected.toArray();
	}

	@Override
	public void setEditable(boolean v) {
		_treeObjects.setEnabled(v);
	}

	@Override
	public void setVisible(boolean v) {
		_treeObjects.setVisible(v);
	}

	protected void objectDeselected(Object removed) {
		_sources_selected.remove(removed);
		_swtuiplatform.broadcastSubValueRemoved(_page, _field, removed);
	}

	protected void objectSelected(Object added) {
		_sources_selected.add(added);
		_swtuiplatform.broadcastSubValueAdded(_page, _field, added);
	}

	public void removeNode(final Object obj) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				removeNode_(obj);
			};
		});
	}

	public void removeNode_(Object obj) {
		final TreeItem[] ti = _treeItems.get(obj);
		if (ti != null) {
			_sources_selected.remove(obj);
			_treeItems.remove(obj);
			for (TreeItem treeItem : ti) {
				treeItem.dispose();
			}
		}
	}

	protected void selectAll() {
		for (TreeItem[] ti : this._treeItems.values()) {
			for (TreeItem treeItem : ti) {
				treeItem.setChecked(true);
			}
		}
		_sources_selected.addAll(Arrays.asList(_sources));
		_swtuiplatform.broadcastSubValueAdded(_page, _field, getVisualValue());
	}

	public void selectObject(final Object obj, final boolean sel) {
		if (_treeItems == null) {
			return;
		}
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				selectObject_(obj, sel);
			};
		});
	}

	public void selectObject_(Object object, boolean sel) {
		TreeItem[] ti = this._treeItems.get(object);
		if (ti == null) {
			return;
		}
		for (TreeItem treeItem : ti) {
			treeItem.setChecked(sel);
		}
		if (sel) {
			_sources_selected.add(object);
		} else {
			_sources_selected.remove(object);
		}
	}

	@Override
	public void setEnabled(boolean v) {
		_treeObjects.setEnabled(v);
	}

	public void setSource(Object[] source) {
		Object[] oldSource = this._sources;
		this._sources = source;

		/*
		 * Skip unnecessary updates due to selection refresh
		 */
		if ((oldSource == null) && (source == null)) {
			return;
		}

		if ((oldSource != null) && (source != null) && (Arrays.asList(oldSource).equals(Arrays.asList(source)))) {
			return;
		}

		/*
		 * Set view input element and initialize checkd state
		 */
		Arrays.sort(_sources, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return toStringFromObject(o1).compareTo(toStringFromObject(o2));
			}
		});

		if (_treeItems == null) {
			_treeItems = new HashMap<Object, TreeItem[]>();
		} else {
			_treeItems.clear();
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				_treeObjects.removeAll();
				createItemFromObjects();
			};
		});

	}

	@Override
	public void setVisualValue(final Object visualValue, boolean sendNotification) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				Object[] selectedObject = (Object[]) visualValue;
				_sources_selected.clear();
				if (selectedObject == null) {
					return;
				}
				for (Object obj : selectedObject) {
					selectObject(obj, true);
				}
			};
		});
	}

	protected Image toImageFromObject(Object obj) {
		return _ic.toImageFromObject(obj);
	}

	protected String toStringFromObject(Object element) {
		return _ic.toStringFromObject(element);
	}

	public void treeCollapsed(TreeEvent e) {

	}

	public void treeExpanded(TreeEvent e) {
		// TODO Auto-generated method stub

	}

	protected int treeItemDefaultStyle() {
		return SWT.NONE;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent event) {
		if (event.detail == SWT.CHECK) {

			TreeItem item = (TreeItem) event.item;
			Object obj = item.getData();
			if (obj != null) {
				if (item.getChecked()) {
					String error = canObjectSelected(obj);
					if (error == null) {
						objectSelected(obj);
					} else {
						item.setChecked(false);
						_swtuiplatform.setMessage(error, UIPlatform.ERROR);
					}
				} else {
					String error = canObjectDeselected(obj);
					if (error == null) {
						objectDeselected(obj);
					} else {
						item.setChecked(true);
						_swtuiplatform.setMessage(error, UIPlatform.ERROR);
					}
				}
			}
		}
		TreeItem item = (TreeItem) event.item;
		if (item != null && item.getChecked()) {
			_ic.select(item.getData());
		}
	}

}