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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.attribute.BooleanAttributeType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.attribute.IntegerAttributeType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.attribute.AttributeType;
import fr.imag.adele.cadse.core.impl.internal.ui.PagesImpl;
import fr.imag.adele.cadse.core.impl.ui.PageImpl;
import fr.imag.adele.cadse.core.impl.ui.UIFieldImpl;
import fr.imag.adele.cadse.core.impl.ui.mc.LinkModelController;
import fr.imag.adele.cadse.core.impl.ui.mc.MC_DefaultForList;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransactionListener;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.HierarchyPage;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.RunningModelController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.core.ui.UIRunningValidator;
import fr.imag.adele.cadse.core.ui.UIValidator;
import fr.imag.adele.cadse.core.ui.view.FilterContext;
import fr.imag.adele.cadse.core.util.ArraysUtil;
import fr.imag.adele.cadse.core.util.CreatedObjectManager;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.ActionController;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.ICRunningField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_DefaultForList;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForBrowserOrCombo;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForChooseFile;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForList;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_LinkForBrowser_Combo_List;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_Tree;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_TreeCheckedUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_TreeModel;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DBrowserUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DButtonUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DCheckBoxUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DCheckedTreeUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DChooseFileUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DComboUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DGridUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DListUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DSashFormUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTextUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTreeModelUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DTreeUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DetailWizardDialog;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.FedeFormToolkit;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.FieldsWizardPage;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.RemoveListener;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.UIWizardDialog;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.WizardController;
/**
 */

public class SWTUIPlatform implements UIPlatform {

	private final class FictifAttribute<T> extends AttributeType implements IAttributeType<T>{
		private IAttributeType<?>[] _children = null;

		private FictifAttribute(CompactUUID id, String name, int flag) {
			super(id, name, flag);
		}

		@Override
		public ItemType getType() {
			return null;
		}

		@Override
		public UIField generateDefaultField() {
			return new UIFieldImpl(CadseGCST.DISPLAY, newID(), this, getName(), EPosLabel.defaultpos, null, null);
		}

		@Override
		public Class<T> getAttributeType() {
			return (Class<T>) Object.class;
		}
		@Override
		public T getDefaultValue() {
			return (T) super.getDefaultValue();
		}
		
		@Override
		public IAttributeType<?>[] getChildren() {
			return _children;		
		}
	}

	private Map<UIField, Label> labels = new HashMap<UIField, Label>();
	private Pages pages;
	private Map<IAttributeType<?>, UIRunningValidator[]> _listen = new HashMap<IAttributeType<?>, UIRunningValidator[]>();
	private Map<UIField, UIRunningField> runningField = new HashMap<UIField, UIRunningField>();
	private Composite parent;

	private Map<String, Object> _vars = new HashMap<String, Object>();
	private List<RemoveListener> _removeListener;
	private WizardDialog dialog;
	private Map<IPage, UIRunningField[]> _runningPage = new HashMap<IPage, UIRunningField[]>();

	/**
	 * Constructor for FieldsWizardPage.
	 * 
	 * @param theCurrentItem
	 */

	public SWTUIPlatform(Pages desc, Composite parent) {
		this.pages = desc;
		this.parent = parent;
	}

	public SWTUIPlatform() {
		pages = new PagesImpl();
	}

	public Pages getPages() {
		return pages;
	}

	public WizardDialog createCreationWizard(Shell parentShell)
			throws CadseException {
		parent = parentShell;
		dialog = new UIWizardDialog(parentShell, new WizardController(this));
		init();
		dialog.addPageChangedListener(new IPageChangedListener() {

			@Override
			public void pageChanged(PageChangedEvent event) {
				// TODO Auto-generated method stub

			}
		});
		dialog.addPageChangingListener(new IPageChangingListener() {

			@Override
			public void handlePageChanging(PageChangingEvent event) {
				// TODO Auto-generated method stub

			}
		});
		return dialog;
	}
	public int open(Shell parentShell, IPage page, IActionPage dialogAction, boolean openDetailDialog) throws CadseException {
		return open(parentShell, page, dialogAction, 800, 500, openDetailDialog);
	}
	public int open(Shell parentShell, IPage page, IActionPage dialogAction, int width, int height, boolean openDetailDialog) throws CadseException {
		init();
		parent = parentShell;
		pages.addPage(page);
		if (dialogAction != null)
			pages.setAction(dialogAction);
		if (openDetailDialog) {
			dialog = new DetailWizardDialog(parentShell, new WizardController(this));
			
		} else {
			dialog = new WizardDialog(parentShell, new WizardController(this));
		}
		dialog.setPageSize(width, height);
		return dialog.open();
	}

	public Composite createPage(IPage page, Composite parentPage) {
		Composite container = getToolkit().createComposite(parentPage);
		Composite ret = createFieldsControl(page, null, container, getFields(page), null);
		initAfterUI(page);
		// Reset visual value. and set UI_running at true
		resetVisualValue(page);
		return ret;

	}

	public UIField[] getFields(IPage page) {
		if (page instanceof HierarchyPage) {
			List<UIField> fields = new ArrayList<UIField>();
			return fields.toArray(new UIField[fields.size()]);
		} else {
			IAttributeType<?>[] attrs = page.getAttributes();
			return getFields(attrs);
		}
	}

	private UIField[] getFields(IAttributeType<?>[] attrs) {
		List<UIField> fields = new ArrayList<UIField>();
		for (IAttributeType<?> at : attrs) {
			if (at == null) throw new NullPointerException();
			UIField f = pages.getUIField(at);
			if (f != null)
				fields.add(f);
		}
		return fields.toArray(new UIField[fields.size()]);
	}

	public void createChildrenControl(UIRunningField<?> ui,
			Composite container, GridLayout layout) {
		createFieldsControl(ui._page, ui, container, getFields(ui._field.getAttributeDefinition().getChildren()),
				layout);
	}

	public Composite createFieldsControl(IPage page, UIRunningField<?> ui,
			Composite container, UIField[] fields, GridLayout layout) {

		if (layout == null) {
			layout = new GridLayout();
			container.setLayoutData(new FormData(300, 300));
		}
		container.setLayout(layout);

		int maxHspan = 0;
		for (UIField mf : fields) {
			init(mf);

			int h = mf.getHSpan();

			if (mf.getPosLabel().equals(EPosLabel.left)) {
				h++;
			}
			if (h > maxHspan) {
				maxHspan = h;
			}
		}
		layout.numColumns = maxHspan;
		layout.verticalSpacing = 5; // 9

		UIRunningField<?>[] children = new UIRunningField<?>[fields.length];
		int i = 0;
		for (UIField mf : fields) {
			try {
				children[i++] = createControl(page, mf, container, maxHspan);
			} catch (Throwable e) {
				log("", e);
			}
		}
		if (ui != null)
			ui._children = children;
		
		add(page, children);
	
		return container;
	}

	private void add(IPage page, UIRunningField<?>[] children) {
		UIRunningField<?>[] uiRunningFields = _runningPage .get(page);
		uiRunningFields = ArraysUtil.addList(UIRunningField.class, uiRunningFields, children);
		_runningPage.put(page, uiRunningFields);
	}

	private void init(UIField mf) {
		// TODO Auto-generated method stub

	}


	/**
	 * @throws CadseException
	 * @see IDialogPage#createControl(Composite)
	 */
	public Composite createControlPage(Composite parentPage)
			throws CadseException {
		this.parent = parentPage;
		init();

		TabFolder container = new TabFolder(parent, SWT.V_SCROLL + SWT.H_SCROLL);
		getToolkit().adapt(container);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		for (IPage mf : pages.getPages()) {
			String title = mf.getLabel();
			if (title == null)
				title = mf.getName();
			TabItem ti = new TabItem(container, SWT.NONE);
			ti.setText(title);
			// TODO set an image...
			ti.setControl(createPage(mf, container));
		}
		for (IPage page : pages.getPages()) {
			initAfterUI(page);
		}
		// Reset visual value.
		for (IPage page : pages.getPages()) {
			resetVisualValue(page);
		}

		setMessage(null, UIPlatform.ERROR);
		validateFields(null, null);

		return container;
	}

	public boolean validateFields(UIField fieldsController, IPage page) {
		return false;
	}
	
	private void initAfterUI(IPage page) {
		UIRunningField[] uiRunningFields = _runningPage.get(page);
		if (uiRunningFields == null) return;
		for (UIRunningField uiRunningField : uiRunningFields) {
			uiRunningField.initAfterUI();
		}
	}

	private void resetVisualValue(IPage page) {
		UIRunningField[] uiRunningFields = _runningPage.get(page);
		if (uiRunningFields == null) return;
		for (UIRunningField uiRunningField : uiRunningFields) {
			uiRunningField.resetVisualValue();
		}
	}
	
	private void clearStatusMessage() {
		if (_pageSite == null)
			throw new NullPointerException();

		IStatusLineManager statusLine = _pageSite.getActionBars()
				.getStatusLineManager();
		if (statusLine != null) {
			statusLine.setErrorMessage(null);
			statusLine.setMessage(null);
		}
	}


	protected void init() throws CadseException {
		if (!isModification() && copy == null) {
			copy = CadseCore.getLogicalWorkspace().createTransaction();
		}
		if (pages.getAction() != null) {
			pages.getAction().init(this);
		}
		if (pages.getUIValidators() != null) {
			for (UIRunningValidator v : pages.getUIValidators()) {
				UIValidator desc = (UIValidator) v.getDescriptor();
				if (desc != null) {
					IAttributeType<?>[] attr = desc.getListenAttributeType();
					if (attr != null) {
						for (IAttributeType<?> a : attr) {
							addListener(a, v);
						}
					}
				}
				v.init(this);
			}
		}
	}

	public void addListener(IAttributeType<?> attr, UIRunningValidator v) {
		UIRunningValidator[] l = _listen.get(attr);
		l = ArraysUtil.add(UIRunningValidator.class, l, v);
		if (l.length == 1)
			_listen.put(attr, l);
	}

	protected <T extends RuningInteractionController> UIRunningField<T> createControl(
			IPage page, UIField field, Composite container, int hspan) {
		if (field.isHidden()) {
			return null;
		}

		int hspan_label = hspan;
		if (field.getPosLabel().equals(EPosLabel.left)
				|| field.getPosLabel().equals(EPosLabel.right)) {
			hspan_label--;
		}
		container = createLabelField(field, container, hspan);

		
		UIRunningField<T> rf = find(page, field);
		if (rf == null) {
			rf = createRunningField(field);
			if (rf == null)
				return null;
			rf._field = field;
			rf._page = page;
			rf._swtuiplatform = this;
			rf._next = runningField.get(field);
			runningField.put(field, rf);
			
			ICRunningField ric = createIC(field.getInteractionControllerBASE());
			if (ric != null) {
				ric._ic = field.getInteractionControllerBASE();
				ric._uiPlatform = this;
				rf._ic = (T) ric;
			}
			
			RunningModelController rmc = createMC(field.getModelController());
			if (rmc == null) {
				rmc = getDefaultModelController();
			}
			rf._mc = rmc;
		}
		
		rf.createControl(container, hspan_label);
		return rf;
	}

	

	private <T extends RuningInteractionController> UIRunningField<T> find(IPage page, UIField field) {
		UIRunningField<?> rf = runningField.get(field);
		while (rf != null) {
			if (rf._page == page)
				return (UIRunningField<T>) rf;
			rf = rf._next;
		}		
		return null;
	}

	protected Composite createLabelField(UIField field, Composite container,
			int hspan) {
		GridData gd;
		if (field.getPosLabel().equals(EPosLabel.left)) {
			Label l = getToolkit().createLabel(container, field.getLabel());
			gd = new GridData();
			gd.verticalSpan = field.getVSpan();
			l.setLayoutData(gd);
			if (!field.isEditable()) {
				l.setEnabled(false);
			}
			labels.put(field, l);
		} else if (field.getPosLabel().equals(EPosLabel.top)) {
			Label l = getToolkit().createLabel(container, field.getLabel());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = hspan;
			l.setLayoutData(gd);
			if (!field.isEditable()) {
				l.setEnabled(false);
			}
			labels.put(field, l);
		} else if (field.getPosLabel().equals(EPosLabel.group)) {
			Group g = getToolkit().createGroup(container, field.getLabel());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = hspan;
			g.setLayoutData(gd);
			container = g;
			container.setLayout(new org.eclipse.swt.layout.GridLayout(hspan,
					false));
		}
		return container;
	}

	public void updateStatus(String message) {
		// setErrorMessage(message);
		// setPageComplete(message == null);
	}

	public boolean broadcastSubValueAdded(IPage page, UIField field,
			Object added) {
		try {
			boolean error;
			error = getModelController(field).validSubValueAdded(field,
					added);
			if (error) {
				return true;
			}
			UIRunningValidator[] listeners = this._listen.get(field
					.getAttributeDefinition());
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					error = listeners[i].validSubValueAdded(field, added);
					if (error) {
						return true;
					}
				}
			}
			getModelController(field).notifieSubValueAdded(field, added);
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].notifieSubValueAdded(field, added);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			return validateFields(field, page);
		} catch (Throwable e) {
			log("Exception raised ! messages = " + e.getMessage(), e);
			setMessageError("Exception raised ! messages = " + e.getMessage());
			return true;
		}
	}

	private RunningModelController getModelController(UIField field) {
		return runningField.get(field)._mc;
	}

	public boolean broadcastSubValueRemoved(IPage page, UIField field,
			Object removed) {
		try {
			boolean error;
			error = getModelController(field).validSubValueRemoved(
					field, removed);
			if (error) {
				return true;
			}
			UIRunningValidator[] listeners = this._listen.get(field
					.getAttributeDefinition());
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					error = listeners[i].validSubValueRemoved( field,
							removed);
					if (error) {
						return true;
					}
				}
			}
			getModelController(field).notifieSubValueRemoved( field,
					removed);
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].notifieSubValueRemoved( field,
								removed);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			return validateFields(field, page);
		} catch (Throwable e) {
			log("Exception raised ! messages = " + e.getMessage(), e);
			setMessageError("Exception raised ! messages = " + e.getMessage());
			return true;
		}
	}

	public void broadcastThisFieldHasChanged(final UIField fd) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				UIRunningField rfields = runningField.get(fd);
				while (rfields != null) {
					rfields.updateValue();
					rfields = rfields._next;
				}
			}
		});

	}

	public boolean broadcastValueDeleted(IPage page, UIField field,
			Object oldvalue) {
		try {
			boolean error = getModelController(field).validValueDeleted(
					field, oldvalue);
			if (error) {
				return true;
			}

			UIRunningValidator[] listeners = this._listen.get(field
					.getAttributeDefinition());
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].validValueDeleted( field, oldvalue);
					} catch (Throwable e) {
						e.printStackTrace();
						listeners[i].incrementError();
					}
				}
			}
			getModelController(field).notifieValueDeleted( field,
					oldvalue);
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].notifieValueDeleted( field, oldvalue);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}

			return validateFields(field, page);
		} catch (Throwable e) {
			log("Exception raised ! messages = " + e.getMessage(), e);
			setMessageError("Exception raised ! messages = " + e.getMessage());
			return true;
		}
	}

	public void dispose() {
	}

	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMessageType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMessage(String newMessage, int newType) {
		if (_pageSite != null) {
			IStatusLineManager statusLine = _pageSite.getActionBars()
					.getStatusLineManager();
			if (statusLine != null) {
				Image newImage = null;
				if (newMessage != null) {
					switch (newType) {
					case IMessageProvider.NONE:
						break;
					case IMessageProvider.INFORMATION:
						newImage = JFaceResources
								.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
						break;
					case IMessageProvider.WARNING:
						newImage = JFaceResources
								.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
						break;
					case IMessageProvider.ERROR:
						newImage = JFaceResources
								.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
						break;
					}
					if (newType == UIPlatform.ERROR) {
						statusLine.setErrorMessage(newImage, newMessage);
					} else {
						statusLine.setMessage(newImage, newMessage);
					}
				} else {
					statusLine.setErrorMessage(null);
					statusLine.setMessage(null);
				}
			}
		}
	}

	@Override
	public void addListener(final Item item, final WorkspaceListener listener,
			int eventFilter) {
		item.addListener(listener, eventFilter);
		_removeListener.add(new RemoveListener() {
			public void dispose() {
				item.removeListener(listener);
			}
		});
	}

	@Override
	public void addLogicalWorkspaceTransactionListener(
			LogicalWorkspaceTransactionListener logicalWorkspaceTransactionListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public LogicalWorkspaceTransaction getCopy() {
		return copy;
	}

	@Override
	public Item getItem(UIField fromField) {
		if (fromField == null)
			return getItem();
		Object v = getVariable(fromField.getId().toString());
		if (v instanceof Item)
			return (Item) v;
		return getItem();
	}

	@Override
	public void resetVisualValue(UIField uiField) {
		UIRunningField rf = runningField.get(uiField);
		while (rf != null) {
			rf.resetVisualValue();
			rf = rf._next;
		}
	}

	@Override
	public void setEnabled(UIField uiField, boolean b) {
		UIRunningField rf = runningField.get(uiField);
		while (rf != null) {
			rf.setEnabled(b);
			rf = rf._next;
		}
	}

	@Override
	public void setMessageError(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTextLabel(UIField uiField, String label) {
		UIRunningField rf = runningField.get(uiField);
		while (rf != null) {
			rf.setLabel(label);
			rf = rf._next;
		}
	}

	@Override
	public void setVariable(String varName, Object value) {
		_vars.put(varName, value);
	}

	@Override
	public void setVisible(UIField uiField, boolean b) {
		UIRunningField rf = runningField.get(uiField);
		while (rf != null) {
			rf.setVisible(b);
			rf = rf._next;
		}
	}

	@Override
	public void setVisualField(IAttributeType<?> attributeDefinition,
			Object visualValue) {
		setVisualValue(attributeDefinition, visualValue, true);
	}

	@Override
	public void setVisualValue(IAttributeType<?> attributeDefinition,
			Object visualValue, boolean b) {
		UIField uiField = pages.getUIField(attributeDefinition);
		if (uiField == null)
			return;
		UIRunningField rf = runningField.get(uiField);
		while (rf != null) {
			rf = rf._next;
		}
	}

	public void sendChangedValue(UIField field, Object visualValue) {
		getModelController(field).notifieValueChanged(field, visualValue);
		UIRunningValidator[] listeners = this._listen.get(field
				.getAttributeDefinition());
		if (listeners != null) {
			for (int i = 0; i < listeners.length; i++) {
				try {
					listeners[i].notifieValueChanged(field, visualValue);
				} catch (Throwable e) {
					e.printStackTrace();
					listeners[i].incrementError();
				}
			}
		}
	}

	/**
	 * Return true if error. Validate this field, if no error, call validator
	 * associated to this field. Other fields are not revalidate to test if
	 * error
	 * 
	 * @param visualValue
	 *            the visual value of this field
	 * @return true if error
	 */

	public boolean validateValueChanged(UIField field, Object visualValue) {
		boolean error;
		error = getModelController(field).validValueChanged(field,
				visualValue);
		if (error) {
			return true;
		}
		UIRunningValidator[] listeners = this._listen.get(field
				.getAttributeDefinition());
		if (listeners != null) {
			for (UIRunningValidator v : listeners) {
				try {
					error = v.validValueChanged(field, visualValue);
					if (error) {
						return true;
					}
				} catch (Throwable e) {
					e.printStackTrace();
					v.incrementError();
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.imag.adele.cadse.core.ui.UIField#broadcastValueChanged(fr.imag.adele
	 * .cadse.core.ui.IPageController, java.lang.Object)
	 */
	public boolean broadcastValueChanged(IPage page, UIField field,
			Object visualValue) {
		try {
			try {
				if (validateValueChanged(field, visualValue)) {
					return true;
				}
				sendChangedValue(field, visualValue);
			} catch (RuntimeException e) {
				log("Exception raised ! messages = " + e.getMessage(), e);
				setMessageError("Exception raised ! messages = "
						+ e.getMessage());
				return true;
			}
			validateFields(field, page);
			return false;
		} catch (RuntimeException e) {
			log("Exception raised ! messages = " + e.getMessage(), e);
			setMessageError("Exception raised ! messages = " + e.getMessage());
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getValueForVisual()
	 */
	public Object getValueForVisual(UIField field) {
		RunningModelController getFct = getModelController(field);
		if (getFct == null) {
			return null;
		}
		Object abstratcObject = getFct.getValue();
		if (abstratcObject == null) {
			Object defaultValue = getFct.defaultValue();
			if (defaultValue != null) {
				getFct.notifieValueChanged(field, defaultValue);
			}
			return defaultValue;
		}
		return abstratcObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#validateField()
	 */
	public boolean validateField(UIField field, Object visualValue) {
		if (isDisposed()) {
			return true;
		}

		boolean error = false;
		RunningModelController getFct = getModelController(field);
		if (getFct != null) {
			error = getFct.validValue(field, visualValue);
		}
		if (error) {
			return true;
		}

		UIRunningValidator[] listeners = this._listen.get(field
				.getAttributeDefinition());
		if (listeners != null) {
			for (UIRunningValidator v : listeners) {
				try {
					error = v.validValue(field, visualValue);
					if (error) {
						return true;
					}
				} catch (Throwable e) {
					e.printStackTrace();
					v.incrementError();
				}
			}
		}

		return false;
	}

	public boolean isDisposed() {
		return false;
	}

	public void log(String msg, Throwable e) {
		Logger.getLogger("uiPlatform").log(Level.SEVERE, msg, e);
	}

	@Override
	public Object getVisualValue(UIField uiField) {
		UIRunningField rf = runningField.get(uiField);
		if (rf != null)
			return rf.getVisualValue();
		return null;
	}

	@Override
	public boolean isModification() {
		return pages.isModificationPages();
	}

	Map<ItemType, Class<?>> itToClassImpl = new HashMap<ItemType, Class<?>>();
	private LogicalWorkspaceTransaction copy;
	private FedeFormToolkit _toolkit;
	private IPageSite _pageSite;
	private RunningModelController _defaultModelController;

	public <T extends RuningInteractionController> UIRunningField<T> createRunningField(
			UIField field) {
		ItemType it = field.getType();
		Class<?> clazz = itToClassImpl.get(it);
		if (clazz == null) {
			return null;
		}
		try {
			return (UIRunningField<T>) clazz.newInstance();
		} catch (SecurityException e) {
			log("Cannot create instance of " + clazz, e);
		} catch (IllegalArgumentException e) {
			log("Cannot create instance of " + clazz, e);
		} catch (InstantiationException e) {
			log("Cannot create instance of " + clazz, e);
		} catch (IllegalAccessException e) {
			log("Cannot create instance of " + clazz, e);
		}
		return null;
	}

	public ICRunningField createIC(Item ic) {
		ItemType it = ic.getType();
		Class<?> clazz = itToClassImpl.get(it);
		if (clazz == null) {
			return null;
		}
		try {
			return (ICRunningField) clazz.newInstance();
		} catch (SecurityException e) {
			log("Cannot create instance of " + clazz, e);
		} catch (IllegalArgumentException e) {
			log("Cannot create instance of " + clazz, e);
		} catch (InstantiationException e) {
			log("Cannot create instance of " + clazz, e);
		} catch (IllegalAccessException e) {
			log("Cannot create instance of " + clazz, e);
		}
		return null;
	}
	
	private RunningModelController createMC(Item modelController) {
		CreatedObjectManager<RunningModelController> manager = CreatedObjectManager.getManager(modelController, RunningModelController.class);
		return manager.create(modelController);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.Pages#doFinish(java.lang.Object)
	 */
	public boolean doFinish(Object monitor) throws Exception {
		pages.getAction().doFinish(this, monitor);
		copy.commit();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.Pages#doCancel(java.lang.Object)
	 */
	public void doCancel(Object monitor) {
		pages.getAction().doCancel(this, monitor);
		copy.rollback();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.Pages#getItem()
	 */
	public Item getItem() {
		String typeid = this.pages.getAction().getTypeId();
		return (Item) getVariable(typeid);
	}

	public Object getVariable(String key) {
		return _vars.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.imag.adele.cadse.core.ui.Pages#setItem(fr.imag.adele.cadse.core.Item)
	 */
	public void setItem(Item item) {
		String typeid = this.pages.getAction().getTypeId();
		setVariable(typeid, item);
	}

	@Override
	public void doNextPageAction(Object monitor, int currentPage)
			throws Exception {

	}

	@Override
	public void doPrevPageAction(Object monitor, int currentPage)
			throws Exception {

	}

	@Override
	public int getNextPageIndex(int currentPage) throws Exception {
		if (pages.getPages().length == currentPage + 1) {
			return -1;
		}
		return currentPage + 1;
	}

	@Override
	public int getPrevPageIndex(int currentPage) throws Exception {
		if (0 == currentPage) {
			return -1;
		}
		return currentPage - 1;
	}

	public IWizardPage[] createWizardPage(WizardController wizardController) {
		for (IPage afd : this.pages.getPages()) {
			try {
				init(afd);
				wizardController.addPage(new FieldsWizardPage(this, afd));
			} catch (CadseException e) {
				log("Cannot create page " + afd.getLabel(), e);
			}
		}
		return wizardController.getPages();
	}

	private void init(IPage afd) {
		// TODO Auto-generated method stub

	}

	public void dispose(IPage page) {
		// TODO Auto-generated method stub

	}

	public FedeFormToolkit getToolkit() {
		if (parent == null && dialog != null)
			parent = dialog.getShell();
		if (parent == null)
			throw new NullPointerException();
		if (_toolkit == null)
			_toolkit = new FedeFormToolkit(parent.getDisplay());
		return _toolkit;
	}

	public void setFilterContext(FilterContext filterContext) {
		// TODO Auto-generated method stub

	}

	public void setSite(IPageSite site) {
		_pageSite = site;
	}
	

	public  <IC extends RuningInteractionController> DCheckBoxUI<IC> createCheckBox(IPage page, BooleanAttributeType key, String label) {
		return createCheckBox(page, key, label, null);
	}

	public IntegerAttributeType createIntegerAttribute(String name, Integer min, Integer max, int defaultValue) {
		return new fr.imag.adele.cadse.core.impl.attribute.IntegerAttributeType(newID(),0, name, min, max,Integer.toString(defaultValue));
	}
	
	public BooleanAttributeType createBooleanAttribute(String name, boolean defaultValue) {
		return new fr.imag.adele.cadse.core.impl.attribute.BooleanAttributeType(newID(),0, name, Boolean.toString(defaultValue));
	}
	
	private CompactUUID newID() {
		return CompactUUID.randomUUID();
	}

	public <IC extends RuningInteractionController> DCheckBoxUI<IC> createCheckBox(IPage page, BooleanAttributeType key, String label, IC ic) {
		return initDefaultRunningField(page, key, label, EPosLabel.defaultpos, null, ic, new DCheckBoxUI<IC>(), CadseGCST.DCHECK_BOX);
	}

	private <IC extends RuningInteractionController, T extends UIRunningField<IC>> T initDefaultRunningField(IPage page, IAttributeType<?> attributte,
			String label, EPosLabel posLabel, 
			RunningModelController defaultMC, IC ic,  T ret, ItemType it, UIRunningField<?> ...children) {
		ret._field = createDefaultField(attributte);
		if (ret._field == null) {
			ret._field = new UIFieldImpl(it, CompactUUID.randomUUID());
			((UIFieldImpl)ret._field)._attributeRef = attributte;
		}
		if (posLabel != null)
			ret._field.setPositionLabel(posLabel);
		if (label != null)
			ret._field.setLabel(label);
		ret._ic = ic;
		ret._page = page;
		ret._swtuiplatform = this;
		ret._mc = defaultMC;
		if (ret._mc == null)
			ret._mc = getDefaultModelController();
		
		this.runningField.put(ret._field, ret);
		this.pages.setUIField(attributte, ret._field);
		if (children.length != 0) {
			ret._children = children;
			UIField[] uiFields = new UIField[children.length];
			((UIFieldImpl)ret._field)._children = uiFields;
			for (int i = 0; i < uiFields.length; i++) {
				uiFields[i] = children[i]._field;
			}
			if (attributte instanceof FictifAttribute<?>) {
				FictifAttribute<?> fa = (FictifAttribute<?>) attributte;
				if (fa._children == null) {
					fa._children = new IAttributeType<?>[children.length];
					for (int i = 0; i < children.length; i++) {
						fa._children[i] = children[i]._field.getAttributeDefinition();
					}
				}				
			}
		}
		return ret;
	}
	

	protected UIField createDefaultField(IAttributeType<?> key) {
		return key.generateDefaultField();
	}

	public RunningModelController getDefaultModelController() {
		return _defaultModelController;
	}

	

	public DBrowserUI<IC_LinkForBrowser_Combo_List> createLinkDependencyField(IPage page, LinkType key, String label, EPosLabel poslabel,
			IC_LinkForBrowser_Combo_List ic, boolean mandatory, String msg) {
		//return new DBrowserUI(key.getName(), label, poslabel, mc, ic, SWT.BORDER | SWT.SINGLE);

		DBrowserUI<IC_LinkForBrowser_Combo_List> rf = initDefaultRunningField(page, key, label, poslabel, new LinkModelController(mandatory, msg), ic, new DBrowserUI<IC_LinkForBrowser_Combo_List>()
				, CadseGCST.DBROWSER);
		
		rf._field.setStyle(SWT.BORDER | SWT.SINGLE);
		return rf;
	}

	

	public <IC extends RuningInteractionController> DTextUI<IC> createIntField(IPage page, IntegerAttributeType key, IC ic) {
		return initDefaultRunningField(page, key,  null, EPosLabel.defaultpos, null, ic, new DTextUI<IC>(), CadseGCST.DTEXT);
	}

	

	public <IC extends ICRunningField> DTextUI<IC> createTextField(IPage page, String key, String label, String tooltip) {
		return createTextField(page, key, label, 1, tooltip, null, null);
	}

	public <IC extends ICRunningField> DTextUI<IC> createTextField(IPage page, String key, String label, RunningModelController mc) {
		return createTextField(page, key, label, 1, null, null, mc);
	}

	public <IC extends ICRunningField> DTextUI<IC> createTextField(IPage page, String key, String label, IC uc) {
		return createTextField(page, key, label, 1, null, uc, null);
	}

	public <IC extends ICRunningField> DTextUI<IC> createTextField(IPage page, String key, String label, int vspan, String tooltip,
			IC ic, RunningModelController mc) {
		DTextUI<IC> ret = createTextUI(page, key, label, EPosLabel.defaultpos, mc, ic, vspan, false, false, false, false, false);
		ret._toolTips = tooltip;
		return ret;
	}

	
	
	

	protected IAttributeType<?> createFictifAttributte(String name) {
		return new FictifAttribute(newID(), name, 0);
	}

	public <IC extends IC_TreeModel> DTreeModelUI<IC> createTreeModelUI(IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean checkBox) {
		return createTreeModelUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, checkBox);
	}
	
	public <IC extends IC_TreeModel> DTreeModelUI<IC> createTreeModelUI(IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean checkBox) {
		DTreeModelUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DTreeModelUI<IC>(), CadseGCST.DTREE);
		ret._useCheckBox = checkBox;
		return ret;
	}
	
	public <IC extends ICRunningField> DTextUI<IC> createTextUI(
			IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, int vspan, boolean multiLine, boolean noBorder, boolean wrapLine, boolean hscroll, boolean vscroll) {
		return createTextUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, vspan, multiLine, noBorder, wrapLine, hscroll, vscroll, null);
	}
	
	public <IC extends ICRunningField> DTextUI<IC> createTextUI(
			IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, int vspan, boolean multiLine, boolean noBorder, boolean wrapLine, boolean hscroll, boolean vscroll, String tooltips) {
		return createTextUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, vspan, multiLine, noBorder, wrapLine, hscroll, vscroll, tooltips);
	}

	public <IC extends ICRunningField> DTextUI<IC> createTextUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, int vspan, boolean multiLine, boolean noBorder, boolean wrapLine, boolean hscroll, boolean vscroll, String tooltips) {
		DTextUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DTextUI<IC>(), CadseGCST.DTEXT);
		ret._field.setType(CadseGCST.DTEXT);
		ret._field.setFlag(Item.UI_TEXT_MULTI_LINE, multiLine);
		ret._field.setFlag(Item.UI_NO_BORDER, noBorder);
		ret._field.setFlag(Item.UI_TEXT_WRAP_LINE, wrapLine);
		try {
			ret._field.setAttribute(CadseGCST.DTEXT_at_MULTI_LINE_, multiLine);
			ret._field.setAttribute(CadseGCST.DTEXT_at_NO_BORDER_, noBorder);
			ret._field.setAttribute(CadseGCST.DTEXT_at_WRAP_LINE_, wrapLine);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ret._field.setFlag(Item.UI_HSCROLL, hscroll);
		ret._field.setFlag(Item.UI_VSCROLL, vscroll);
		ret._vspan = vspan;
		ret._toolTips = tooltips;
		return ret;
	}
	
	public <IC extends ICRunningField> DSashFormUI<IC> createDSashFormUI(
			IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, UIRunningField<?> child1, UIRunningField<?> child2) {
		
		return createDSashFormUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, child1, child2);
	}

	public <IC extends ICRunningField> DSashFormUI<IC> createDSashFormUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, UIRunningField<?> child1, UIRunningField<?> child2) {
		DSashFormUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DSashFormUI<IC>(), CadseGCST.FIELD, child1, child2);
		return ret;
	}

	public <IC extends ICRunningField> DGridUI<IC> createDGridUI(
			IPage page,
			String attributte, String label,
			EPosLabel poslabel, RunningModelController mc,
			IC ic, UIRunningField<?>... children) {
		return createDGridUI(page, createFictifAttributte(attributte), label, poslabel, mc, ic, children);
	}
	
	public <IC extends ICRunningField> DGridUI<IC> createDGridUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, UIRunningField<?>... children) {
		DGridUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DGridUI<IC>(), CadseGCST.FIELD, children);
		return ret;
	}

	

	public IPage createPageDescription(String title, String label) {
		PageImpl pageImpl = new PageImpl("p1", label, title, title, true);
		pages.addPage(pageImpl);
		return pageImpl;
	}
	
	public <IC extends IC_Tree> DTreeUI<IC> createTreeUI(
			IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic) {
		return createTreeUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic);
	}

	public <IC extends IC_Tree> DTreeUI<IC> createTreeUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic) {
		DTreeUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DTreeUI<IC>(), CadseGCST.DTREE);
		return ret;
	}
	
	public <IC extends IC_ForList> DListUI<IC> createDListUI(IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean edit, boolean showfilter, boolean order, boolean update) {
		return createDListUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, edit, showfilter, order, update);
	}

	public DListUI<IC_DefaultForList> createList_ListOfString(IPage page, String key, String label, String title, String message,
			boolean allowDuplicate, int min, int max) {
		return createList(page, key, label, new MC_DefaultForList(min, max), new IC_DefaultForList(title,
				message, allowDuplicate));
	}

	public  <IC extends IC_DefaultForList> DListUI<IC> createList_ListOfString(IPage page, String key, String label, MC_DefaultForList mc,
			IC ic) {
		return createList(page, key, label, mc, ic);
	}

	public  <IC extends IC_ForList> DListUI<IC> createList(IPage page, String key, String label, RunningModelController mc, IC ic,
			Object... objects) {
		DListUI<IC> ret = createDListUI(page, createFictifAttributte(key), label, EPosLabel.defaultpos, mc, ic, false, false, false, false);
		return ret;
	}
	
	public <IC extends IC_ForList> DListUI<IC> createDListUI(IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean edit, boolean showfilter, boolean order, boolean update) {
		DListUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DListUI<IC>(), CadseGCST.DLIST);
		try {
			ret._field.setAttribute(CadseGCST.DLIST_at_ORDER_BUTTON_, order);
			ret._field.setAttribute(CadseGCST.DLIST_at_SHOW_FILTER_, showfilter);
			ret._field.setAttribute(CadseGCST.DLIST_at_EDITABLE_BUTTON_, edit);
			ret._field.setAttribute(CadseGCST.DLIST_at_UPDATE_BUTTON_, update);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public <IC extends ICRunningField> DCheckBoxUI<IC> createCheckBoxUI(IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic) {
		return createCheckBoxUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic);
	}

	public <IC extends ICRunningField> DCheckBoxUI<IC> createCheckBoxUI(IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic) {
		DCheckBoxUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DCheckBoxUI<IC>(), CadseGCST.DCHECK_BOX);
		return ret;
	}

	public void setAction(IActionPage newAction) {
		pages.setAction(newAction);
	}
	
	public void addUIValidator(UIRunningValidator v) {
		pages.addUIValidator(v);
	}
	

	public <IC extends IC_ForChooseFile> DChooseFileUI<IC> createDChooseFileUI(IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, String title) {
		return createDChooseFileUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, title);
	}
	
	public <IC extends IC_ForChooseFile> DChooseFileUI<IC> createDChooseFileUI(IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, String title) {
		DChooseFileUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DChooseFileUI<IC>(), CadseGCST.FIELD);
		ret.choosemsg = title;
		return ret;
	}
	
	public <IC extends IC_TreeCheckedUI> DCheckedTreeUI<IC> createDCheckedTreeUI(IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean selectDelectButton, boolean fillBoth) {
		return createDCheckedTreeUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, selectDelectButton, fillBoth);
	}

	public <IC extends IC_TreeCheckedUI> DCheckedTreeUI<IC> createDCheckedTreeUI(IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean selectDelectButton, boolean fillBoth) {
		DCheckedTreeUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DCheckedTreeUI<IC>(), CadseGCST.DCHECKED_TREE);
		ret._selectDelectButton = selectDelectButton;
		ret._fillBoth = fillBoth;
		return ret;
	}
	
	public  <IC extends IC_ForBrowserOrCombo> DComboUI<IC> createDComboUI(
			IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean edit) {
		return createDComboUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic, edit);
	}
	
	public  <IC extends IC_ForBrowserOrCombo> DComboUI<IC> createDComboUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic, boolean edit) {
		DComboUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DComboUI<IC>(), CadseGCST.DCOMBO);
		ret._field.setEditable(edit);
		return ret;
	}
	
	public <IC extends ActionController> DButtonUI<IC> createDButtonUI(IPage page,
			String attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic) {
		return createDButtonUI(page, createFictifAttributte(attributte), label, posLabel, mc, ic);
	}

	public <IC extends ActionController> DButtonUI<IC> createDButtonUI(IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel posLabel, RunningModelController mc,
			IC ic) {
		DButtonUI<IC> ret = initDefaultRunningField(page, attributte, label, posLabel, mc, ic, new DButtonUI<IC>(), CadseGCST.FIELD);
		return ret;
	}

	public Shell getShell() {
		if (dialog != null)
			return dialog.getShell();
		return null;
	}

	public void setPages(Pages lastItemPages) {
		pages = lastItemPages;
	}
	
	

}