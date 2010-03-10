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
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_Tree;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only
 * accept file name without the extension OR with the extension that matches the expected one (f).
 */

public class DTreeUI<IC extends IC_Tree> extends DAbstractField<IC> {
	private Object[] _rootNodes;
	private Object[] _selectedObjects;
	private Tree _treeControl;

	@Override
	public void createControl(Composite container, int hspan) {
		_treeControl = new Tree(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 200;
		_treeControl.setLayoutData(gd);
		_treeControl.setData(UIField.CADSE_MODEL_KEY, this);

		_treeControl.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				TreeItem[] sel = _treeControl.getSelection();
				if (sel.length == 1) {
					TreeItem ti = sel[0];
					select(ti);
				}

			}
		});
	}

	protected void createTree(Object[] root2, Object superTreeItem) {
		for (Object obj : root2) {
			TreeItem ti;
			if (superTreeItem instanceof TreeItem) {
				ti = new TreeItem((TreeItem) superTreeItem, SWT.NONE);
			}
			else {
				ti = new TreeItem((Tree) superTreeItem, SWT.NONE);
			}
			ti.setImage(getImage(obj));
			ti.setData(obj);
			ti.setText(getText(obj));
			Object[] children = getChildren(obj);
			if (children != null && children.length > 0) {
				createTree(children, ti);
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		_treeControl = null;
		_rootNodes = null;
		_selectedObjects = null;
	}

	private Object[] getChildren(Object obj) {
		return _ic.getChildren(obj);
	}

	private Image getImage(Object obj) {
		return _ic.getImage(obj);
	}

	@Override
	public Control getMainControl() {
		return _treeControl;
	}

	@Deprecated
	public Object[] getSelectedDataObject() {
		return _selectedObjects;
	}

	@Override
	public Object[] getSelectedObjects() {
		return _selectedObjects;
	}

	/**
	 * Returns the single selected object contained in the passed selectionEvent, or <code>null</code> if the
	 * selectionEvent contains either 0 or 2+ selected objects.
	 */
	protected Object getSingleSelection(IStructuredSelection selection) {
		return selection.size() == 1 ? selection.getFirstElement() : null;
	}

	private String getText(Object obj) {
		return _ic.getText(obj);
	}

	public ItemType getType() {
		return CadseGCST.DTREE;
	}

	@Override
	public Object getVisualValue() {
		return _rootNodes;
	}

	@Override
	public void setEditable(boolean v) {
	}

	@Override
	public void setVisible(boolean v) {
		this._treeControl.setVisible(v);
	}

	void select(TreeItem ti) {
		ti.getParentItem();
		List<Object> selectedData = new ArrayList<Object>();
		while (true) {
			selectedData.add(0, ti.getData());
			ti = ti.getParentItem();
			if (ti == null) {
				break;
			}
		}
		this._selectedObjects = selectedData.toArray();
		_swtuiplatform.broadcastValueChanged(_page, _field, getVisualValue());
	}

	@Override
	public void setEnabled(boolean v) {
		this._treeControl.setEnabled(v);
	}

	@Override
	public void setVisualValue(Object visualValue, boolean sendNotification) {
		_rootNodes = (Object[]) visualValue;
		_treeControl.removeAll();
		createTree(_rootNodes, _treeControl);
	}

}
