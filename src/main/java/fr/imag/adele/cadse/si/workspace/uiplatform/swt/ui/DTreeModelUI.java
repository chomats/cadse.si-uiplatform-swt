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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import fede.workspace.tool.view.node.FilteredItemNode;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_TreeModel;

public class DTreeModelUI<IC extends IC_TreeModel> extends DAbstractField<IC> implements ISelectionChangedListener,
		SelectionListener, ICheckStateListener, TreeListener {

	public boolean _useCheckBox = true;
	public boolean _useColumns = false;
	private Tree _treeControl;
	private CheckboxTreeViewer _treeViewer;
	private FilteredItemNode _rootNode;
	public String[] _columns;

	@Override
	public void dispose() {
		super.dispose();
		_treeControl = null;
		_treeViewer = null;
		_rootNode = null;
	}

	@Override
	public void createControl(Composite container, int hspan) {

		int style = SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL;
		if (_useCheckBox) {
			style |= SWT.CHECK;
		}
		if (_useColumns) {
			style |= SWT.H_SCROLL;
		}
		_treeControl = new Tree(container, style);
		_treeControl.addSelectionListener(this);
		_treeControl.addTreeListener(this);
		_treeControl.setData(UIField.CADSE_MODEL_KEY, this);
		int fillkind = GridData.FILL_BOTH;
		// /if (fillBoth)
		// fillkind = GridData.FILL_BOTH;
		// boolean fillBoth = true;

		GridData gd;
		gd = new GridData(fillkind);
		gd.horizontalSpan = hspan;
		// gd.verticalSpan = 3;
		// gd.minimumHeight = 150;
		// if (!fillBoth) {
		// gd.minimumHeight = 150;
		// // gd.grabExcessVerticalSpace = true;
		// } else
		// gd.grabExcessVerticalSpace = false;
		_treeControl.setLayoutData(gd);

		if (_columns != null) {
			_treeControl.setHeaderVisible(true);
			for (int i = 0; i < _columns.length; i++) {
				TreeColumn column = new TreeColumn(_treeControl, SWT.CENTER);
				column.setText(_columns[i]);
				column.setWidth(200);
			}
		}

		_treeViewer = new CheckboxTreeViewer(_treeControl) {

			@Override
			protected void doUpdateItem(Item item, Object element) {
				super.doUpdateItem(item, element);
				if (item.isDisposed()) {
					return;
				}

				if (item instanceof TreeItem) {
					final TreeItem treeItem = ((TreeItem) item);
					IItemNode node = (IItemNode) treeItem.getData();
					int s = node.isSelected();
					treeItem.setChecked(s == IItemNode.SELECTED);
					treeItem.setGrayed(s == IItemNode.GRAY_SELECTED);
				}
			}

		};
		_treeViewer.addCheckStateListener(this);
		_treeViewer.setUseHashlookup(true);

		_treeViewer.addSelectionChangedListener(this);
		_rootNode = _ic.getOrCreateFilteredNode();
		_rootNode.setTreeViewer(_treeViewer);

		_treeViewer.setContentProvider(_ic.getContentProvider());
		_treeViewer.setLabelProvider(_ic.getLabelProvider());
		_treeViewer.setInput(_rootNode);

		createContextMenu(_treeControl);
	}

	@Override
	public Object getVisualValue() {
		return _rootNode;
	}

	public CheckboxTreeViewer getTreeViewer() {
		return _treeViewer;
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
					}
					else {
						item.setChecked(false);
						_swtuiplatform.setMessage(error, UIPlatform.ERROR);
					}
				}
				else {
					String error = canObjectDeselected(obj);
					if (error == null) {
						objectDeselected(obj);
					}
					else {
						item.setChecked(true);
						_swtuiplatform.setMessage(error, UIPlatform.ERROR);
					}
				}
			}
		}
		TreeItem item = (TreeItem) event.item;
		if (item != null) {
			_ic.select(item.getData());
		}
	}

	/**
	 * @param object
	 *            l'object selectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectSelected(Object object) {
		return _ic.canObjectSelected(object);
	}

	/**
	 * @param object
	 *            l'object deselectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectDeselected(Object object) {
		return _ic.canObjectDeselected(object);
	}

	protected void objectSelected(Object added) {
		_swtuiplatform.broadcastSubValueAdded(_page, _field, added);
	}

	protected void objectDeselected(Object removed) {
		_swtuiplatform.broadcastSubValueRemoved(_page, _field, removed);
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue instanceof FilteredItemNode) {
			_rootNode = (FilteredItemNode) visualValue;
			_rootNode.setTreeViewer(_treeViewer);
			_treeViewer.setInput(_rootNode);
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void checkStateChanged(CheckStateChangedEvent event) {
		event.getElement();
	}

	@Override
	public Object[] getSelectedObjects() {
		return _treeViewer.getCheckedElements();
	}

	public void treeCollapsed(TreeEvent e) {
		if (e.item.getData() != null) {
			_ic.treeCollapsed(e.item.getData());
		}

	}

	public void treeExpanded(TreeEvent e) {
	}

	public void selectNode(IItemNode n) {
		_treeViewer.setChecked(n, true);
	}

	@Override
	public Control getMainControl() {
		return _treeControl;
	}

	public void setExpandedNodes(IItemNode... n) {
		_treeViewer.setExpandedElements(n);
	}

}
