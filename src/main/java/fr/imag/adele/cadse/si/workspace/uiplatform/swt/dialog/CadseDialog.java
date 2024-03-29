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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.dialog;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import fede.workspace.tool.view.node.CategoryNode;
import fede.workspace.tool.view.node.FilteredItemNode;
import fede.workspace.tool.view.node.FilteredItemNodeModel;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.FilteredItemNode.Category;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemShortNameComparator;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.impl.ui.mc.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_TreeModel;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DGridUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DSashFormUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTextUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTreeModelUI;

public class CadseDialog extends SWTDialog {

	int							allreadyselected	= 0;
	Item						selectedItem		= null;
	HashSet<CadseRuntime>		selected			= new HashSet<CadseRuntime>();
	Category					categoryExtendsTo;
	Category					categoryExtendedBy;

	protected DGridUI			fieldsCadse;

	protected DSashFormUI		fieldsShash;
	/**
	 * 
	 */
	protected DTreeModelUI		fieldExtends;

	/**
	 * 
	 */
	protected DTextUI			fieldDescription;

	/**
	 * 
	 */
	protected DTextUI			fieldTWVersion;

	private CadseRuntime[][]	ret;

	public class MyMC_AttributesItem extends MC_AttributesItem {

		@Override
		public Object getValue() {
			if (getItem() == null) {
				return "";
			}
			Object _ret = super.getValue();
			if (_ret == null) {
				return "";
			}
			return _ret;
		}
		
		@Override
		public Item getItem() {
			return selectedItem;
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
			// read only value
		}
		
		@Override
		public boolean validValueChanged(UIField field, Object visualValue) {
			// read only value
			return false;
		}
		
		@Override
		public boolean validValue(UIField field, Object value) {
			// read only value
			return false;
		}
	}

	public class MyActionPage extends AbstractActionPage {

		@Override
		public void doFinish(UIPlatform ui, Object monitor) throws Exception {
			Object[] array = fieldExtends.getSelectedObjects();
			HashSet<CadseRuntime> selection = new HashSet<CadseRuntime>();
			for (int i = 0; i < array.length; i++) {
				IItemNode node = (IItemNode) array[i];
				if (node.getItem() != null && node.getItem().getType() == CadseGCST.CADSE) {
					selection.add((CadseRuntime) node.getItem());
				}
			}
			ret[0] = selection.toArray(new CadseRuntime[selection.size()]);
		}
	}

	public class IC_ItemTypeForTreeUI extends IC_TreeModel {

		@Override
		public ItemType getType() {
			return null;
		}

		@Override
		public void initAfterUI() {
			super.initAfterUI();
			try {
				// corrige un bug
				// les node de premier niveau ne sont pas checked
				FilteredItemNode node = getOrCreateFilteredNode();
				for (IItemNode n : node.getChildren()) {
					if (isSelected(n) == IItemNode.SELECTED) {
						fieldExtends.selectNode(n);
					}
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public String canObjectDeselected(Object object) {
			IItemNode node = (IItemNode) object;
			if (node.getItem() == null || node.getItem().getType() != CadseGCST.CADSE) {
				return "not good item";
			}
			CadseRuntime cadseRuntime = ((CadseRuntime) node.getItem());
			if (cadseRuntime.isExecuted()) {
				return "cadse allready executed";
			}
			if (isNeed(cadseRuntime)) {
				return "is needed";
			}

			return null;
		}

		private boolean isNeed(CadseRuntime cadseRuntime) {
			for (CadseRuntime cr : selected) {
				if (cr == cadseRuntime) {
					continue;
				}
				if (cr.isRequired(cadseRuntime)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String canObjectSelected(Object object) {
			IItemNode node = (IItemNode) object;
			if (node.getItem() == null || node.getItem().getType() != CadseGCST.CADSE) {
				return "not good item";
			}
			if (((CadseRuntime) node.getItem()).getErrors() != null) {
				return "errors in cadse";
			}
			return null;
		}

		/**
		 * Crete the structured model to show All item of type CadseRuntime
		 * Category extends the item destination of link of type
		 * CadseRuntime::extends Category 'extended by' the item source of
		 * incomings link of type CadseRuntime::extends for a node of type
		 * CadseRuntime shows the above category...
		 * 
		 */

		@Override
		protected FilteredItemNodeModel getTreeModel() {
			if (model == null) {
				model = new FilteredItemNodeModel();
				// en premier on rajoute les insances de cadse runtime trier par
				// le nom
				model.addItemFromItemTypeEntry(null, CadseGCST.CADSE, ItemShortNameComparator.INSTANCE);

				// on creer deux categories
				categoryExtendsTo = new FilteredItemNode.Category();
				categoryExtendsTo.name = "extends";

				categoryExtendedBy = new FilteredItemNode.Category();
				categoryExtendedBy.name = "extended by";

				// on lie les deux category à un instance de ce Cadseruntime
				model.addCategories(CadseGCST.CADSE, categoryExtendsTo, categoryExtendedBy);
				model.addItemFromLinkTypeEntry(categoryExtendsTo, CadseGCST.CADSE_lt_EXTENDS,
						ItemShortNameComparator.INSTANCE, false, false);
				model.addItemFromLinkTypeEntry(categoryExtendedBy, CadseGCST.CADSE_lt_EXTENDS,
						ItemShortNameComparator.INSTANCE, false, true);

			}
			return model;
		}

		@Override
		protected FilteredItemNode createRootNode() {
			return new FilteredItemNode(null, getTreeModel()) {
				@Override
				public int isSelected(IItemNode node) {
					return IC_ItemTypeForTreeUI.this.isSelected(node);
				}
			};
		}

		public void edit(Object o) {
			// TODO Auto-generated method stub

		}

		public Object[] getSources() {
			return CadseCore.getLogicalWorkspace().getCadseRuntime();
		}

		@Override
		public void select(Object data) {
			IItemNode node = (IItemNode) data;
			if (node.getItem() == null || node.getItem().getType() != CadseGCST.CADSE) {
				return;
			}
			setSelectedItem(node.getItem());
		}

		@Override
		public int isSelected(IItemNode node) {
			if (node instanceof ItemNode) {
				Item item = (node).getItem();
				if (item != null && item instanceof CadseRuntime) {
					CadseRuntime cadseRuntime = ((CadseRuntime) item);
					return (cadseRuntime.isExecuted() || selected.contains(cadseRuntime)) ? IItemNode.SELECTED
							: IItemNode.DESELECTED;
				}
			}
			if (node instanceof CategoryNode) {
				if (((CategoryNode) node).getCategory() == categoryExtendsTo) {
					if (((CategoryNode) node).getParent() != null) {
						return isSelected(((CategoryNode) node).getParent());
					}
				}
			}
			return IItemNode.DESELECTED;
		}

	}

	public class MC_CDtree extends AbstractModelController {

		public MC_CDtree(Item desc) {
			super(desc);
		}

		@Override
		public Object getValue() {
			ArrayList<CadseRuntime> executedCadse = new ArrayList<CadseRuntime>();
			for (CadseRuntime cadseRuntime : CadseCore.getLogicalWorkspace().getCadseRuntime()) {
				if (cadseRuntime.isExecuted()) {
					executedCadse.add(cadseRuntime);
				}
			}
			allreadyselected = executedCadse.size();

			return executedCadse.toArray(new CadseRuntime[executedCadse.size()]);
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
		}

		@Override
		public void initAfterUI(UIField field) {
			DTreeModelUI<?> runningField = _swtuiPlatforms.getRunningField(field, _page);
			TreeViewer viewer = runningField.getTreeViewer();
			viewer.addFilter(new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof ItemNode) {
						Item item = ((ItemNode)element).getItem();
						return item != null && item.getType() != CadseGCST.CADSE_DEFINITION;
					}
					return true;
				}
			});
		}

		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
			if (added instanceof CategoryNode) {
				for (IItemNode n : ((CategoryNode) added).getChildren()) {
					if (n.getItem() != null && n.getItem().getType() == CadseGCST.CADSE) {
						fieldExtends.selectNode(n);
					}
				}
				return;
			}
			if (added instanceof IItemNode) {
				IItemNode n = (IItemNode) added;
				if (n.getItem() != null && n.getItem().getType() == CadseGCST.CADSE) {
					selected.add((CadseRuntime) n.getItem());
					for (IItemNode nc : n.getChildren()) {
						if (nc instanceof CategoryNode && ((CategoryNode) nc).getCategory() == categoryExtendsTo) {
							for (IItemNode nnc : nc.getChildren()) {
								if (nnc.getItem() != null && n.getItem().getType() == CadseGCST.CADSE) {
									if (selected.add((CadseRuntime) nnc.getItem())) {
										fieldExtends.selectNode(nnc);
										notifieSubValueAdded(field, nnc);
									}
								}
							}
						}
					}
				}
			}
		}

		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
			if (removed instanceof IItemNode) {
				IItemNode n = (IItemNode) removed;
				if (n.getItem() != null && n.getItem().getType() == CadseGCST.CADSE) {
					selected.remove(n.getItem());
				}
			}
		}

	}

	/**
	 * Create the dialog structure... DSashFormUI DGrillUI FieldExtends DGrillUI
	 * fieldTWVersion fieldDescription
	 * 
	 * @param ret
	 * @generated
	 */
	public CadseDialog(SWTUIPlatform swtuiPlatforms, CadseRuntime[][] ret) {
		super(swtuiPlatforms, "Executed CADSEs", "You can execute other CADSEs by validating the checkboxes bellow.");
		this.ret = ret;
		this.fieldExtends = createFieldExtends(true);
		this.fieldDescription = createFieldDescription();
		this.fieldTWVersion = createFieldTWVersion();
		MyMC_AttributesItem defaultMc = new MyMC_AttributesItem();

		DGridUI treeGrild = _swtuiPlatforms.createDGridUI(_page, "#tree", "", EPosLabel.none, defaultMc, null,
				fieldExtends);

		DGridUI crFieldsGrild = _swtuiPlatforms.createDGridUI(_page, "#edit", "", EPosLabel.none, defaultMc, null,
				this.fieldTWVersion, this.fieldDescription);

		this.fieldsShash = _swtuiPlatforms.createDSashFormUI(_page, "#sash", "", EPosLabel.none, defaultMc, null,
				treeGrild, crFieldsGrild);
		fieldsShash.setWeight(60); // 60% , 40%
		// add main field
		addLast(fieldsShash);

		registerListener();
	}

	/**
	 * Register listener or validator if need
	 */
	protected void registerListener() {
		// fieldExtends.addValidateContributor(this);
	}

	/**
	 * When a cadseruntime is selected, set the selected item and reset the
	 * values of fields
	 * 
	 * @param selectedItem
	 */
	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
		this.fieldDescription.resetVisualValue();
		this.fieldTWVersion.resetVisualValue();
	}

	/**
	 * Create a tree field to show CadseModel tree
	 */
	public DTreeModelUI createFieldExtends(boolean checkBox) {
		return _swtuiPlatforms.createTreeModelUI(_page, "#list", "Cadse", EPosLabel.top, new MC_CDtree(null),
				new IC_ItemTypeForTreeUI(), checkBox);
	}

	/**
	 * Create a text field to display the description's CadseRuntime item
	 */
	public DTextUI createFieldDescription() {
		return _swtuiPlatforms.createTextUI(_page, CadseGCST.CADSE_at_DESCRIPTION_, "description", EPosLabel.left,
				new MyMC_AttributesItem(), null, 20, true, false, true, false, true, null);
	}

	/**
	 * Create a text field to display the version's CadseRuntime item
	 */
	public DTextUI createFieldTWVersion() {
		return _swtuiPlatforms.createTextUI(_page, CadseGCST.ITEM_at_TW_VERSION_, "version", EPosLabel.left,
				new MyMC_AttributesItem(), null, 1, false, false, false, false, true, null);
	}

	/**
	 * Open dialog.
	 * 
	 * @param askToErase
	 *            the ask to erase
	 * 
	 * @return true, if successful
	 * 
	 */
	static public CadseRuntime[] openDialog(final boolean askToErase) {
		final CadseRuntime[][] ret = new CadseRuntime[1][];
		ret[0] = null;
		new CadseDialog(new SWTUIPlatform(), ret).open(null, askToErase);
		return ret[0];
	}

	private void open(final Shell shell, final boolean askToErase) {
		/**
		 * Create a new display wen call getDefault(). Worksbench is not
		 * started. This method is called by federation in start level.
		 * 
		 */
		Display d = PlatformUI.getWorkbench().getDisplay();

		d.syncExec(new Runnable() {
			public void run() {
				if (askToErase) {
					boolean wsErase = false;
					wsErase = MessageDialog.openConfirm(new Shell(), "Do you want erase old workspace ?",
							"You a existed cadse workspace with an old model, do you want erase it ?");
					if (!wsErase) {
						ret[0] = null;
						return;
					}
				}
				{
					try {
						_swtuiPlatforms.open(shell, _page, getFinishAction(), false);
						// TODO open commit progression dialog
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected IActionPage getFinishAction() {
		return new MyActionPage();
	}

}
