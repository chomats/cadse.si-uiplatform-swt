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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import fede.workspace.model.manager.properties.impl.ParentPartGetAndSet;
import fede.workspace.model.manager.properties.impl.ic.ICRunningField;
import fede.workspace.model.manager.properties.impl.ic.IC_DefaultForList;
import fede.workspace.model.manager.properties.impl.ic.IC_FileResourceForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_FolderResource_ForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_IconResourceForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_PartParentForBrowser_Combo;
import fede.workspace.model.manager.properties.impl.ic.IC_TreeModel;
import fede.workspace.model.manager.properties.impl.mc.MC_DefaultForList;
import fede.workspace.model.manager.properties.impl.mc.MC_DisplayNameItemProperty;
import fede.workspace.model.manager.properties.impl.mc.MC_IDItemProperty;
import fede.workspace.model.manager.properties.impl.mc.MC_ShortNameItemProperty;
import fede.workspace.model.manager.properties.impl.mc.StringToOneResourceModelController;
import fede.workspace.model.manager.properties.impl.mc.StringToResourceListModelController;
import fede.workspace.model.manager.properties.impl.mc.StringToResourceSimpleModelController;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.DerivedLink;
import fr.imag.adele.cadse.core.DerivedLinkDescription;
import fr.imag.adele.cadse.core.EventFilter;
import fr.imag.adele.cadse.core.GroupType;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemDescription;
import fr.imag.adele.cadse.core.ItemDescriptionRef;
import fr.imag.adele.cadse.core.ItemFilter;
import fr.imag.adele.cadse.core.ItemState;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.attribute.BooleanAttributeType;
import fr.imag.adele.cadse.core.attribute.CheckStatus;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.attribute.IntegerAttributeType;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.attribute.AttributeType;
import fr.imag.adele.cadse.core.impl.internal.ui.PagesImpl;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.CreationAction;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.impl.ui.PageImpl;
import fr.imag.adele.cadse.core.impl.ui.mc.LinkModelController;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransactionListener;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.HierarchyPage;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIValidator;
import fr.imag.adele.cadse.core.ui.view.FilterContext;
import fr.imag.adele.cadse.core.util.ArraysUtil;
import fr.imag.adele.cadse.core.util.IErrorCollector;
import fr.imag.adele.cadse.core.util.OrderWay;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.FieldsCore;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;
import fr.imag.adele.cadse.core.impl.ui.UIFieldImpl;
/**
 */

public class SWTUIPlatform implements IPageController {

	private final class FictifAttribute<T> extends AttributeType implements IAttributeType<T>{
		private IAttributeType<?>[] _children;

		private FictifAttribute(CompactUUID id, String name, int flag, IAttributeType<?>[] chrildren) {
			super(id, name, flag);
			_children = chrildren;
		}

		@Override
		public ItemType getType() {
			return null;
		}

		@Override
		public UIField generateDefaultField() {
			return new UIFieldImpl(CadseGCST.DISPLAY, newID());
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
	private Map<IAttributeType<?>, UIValidator[]> _listen = new HashMap<IAttributeType<?>, UIValidator[]>();
	private Map<UIField, UIRunningField> runningField = new HashMap<UIField, UIRunningField>();
	private Composite parent;

	private Map<String, Object> _vars = new HashMap<String, Object>();
	private List<RemoveListener> _removeListener;
	private WizardDialog dialog;

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
	}

	public Pages getPages() {
		return pages;
	}

	public WizardDialog createCreationWizard(Shell parentShell)
			throws CadseException {
		parent = parentShell;
		dialog = new WizardDialog(parentShell, new WizardController(this));
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
	
	public void open(Shell parentShell, IPage page, IActionPage dialogAction, boolean showDetail) {
		init();
		parent = parentShell;
		pages = new PagesImpl(false, dialogAction, null, new IPage[] { page},  Collections.EMPTY_LIST);
		if (showDetail) {
			dialog = new DetailWizardDialog(parentShell, new WizardController(this));
			
		} else {
			dialog = new WizardDialog(parentShell, new WizardController(this));
		}
		dialog.setPageSize(800, 500);
		dialog.open();
	}

	public Composite createPage(IPage page, Composite parentPage) {
		Composite container = getToolkit().createComposite(parentPage);
		return createFieldsControl(page, null, container, getFields(page), null);

	}

	public UIField[] getFields(IPage page) {
		List<UIField> fields = new ArrayList<UIField>();
		if (page instanceof HierarchyPage) {

		} else {
			IAttributeType<?>[] attrs = page.getAttributes();
			for (IAttributeType<?> at : attrs) {
				UIField f = pages.getUIField(at);
				if (f != null)
					fields.add(f);
			}
		}
		return fields.toArray(new UIField[fields.size()]);
	}

	public void createChildrenControl(UIRunningField<?> ui,
			Composite container, GridLayout layout) {
		createFieldsControl(ui._page, ui, container, ui._field.getChildren(),
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

		UIRunningField<?>[] _children = new UIRunningField<?>[fields.length];
		int i = 0;
		for (UIField mf : fields) {
			try {
				_children[i++] = createControl(page, mf, container, maxHspan);
			} catch (Throwable e) {
				log("", e);
			}
		}
		if (ui != null)
			ui._children = _children;
		return container;
	}

	private void init(UIField mf) {
		// TODO Auto-generated method stub

	}

	public void setPages(Pages pages) {
		this.pages = pages;
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
		for (IPage page : pages.getPages()) {
			// Reset visual value. and set UI_running at true
			resetVisualValue(page);
		}

		setMessage(null, IPageController.ERROR);
		validateFields(null, null);

		return container;
	}

	public boolean validateFields(UIField fieldsController, IPage page) {
		return false;
	}

	protected void initAfterUI(IPage page) {
		// TODO Auto-generated method stub

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

	protected void resetVisualValue(IPage page) {
		// TODO Auto-generated method stub

	}

	protected void init() throws CadseException {
		if (!isModification() && copy == null) {
			copy = CadseCore.getLogicalWorkspace().createTransaction();
		}
		action = pages.getAction();
		if (action != null) {
			action.init(this);
		}
		for (UIValidator v : pages.getUIValidators()) {
			IAttributeType<?>[] attr = v.getListenAttributeType();
			if (attr != null) {
				for (IAttributeType<?> a : attr) {
					addListener(a, v);
				}
			}
			v.init(this);
		}
	}

	public void addListener(IAttributeType<?> attr, UIValidator v) {
		UIValidator[] l = _listen.get(attr);
		l = ArraysUtil.add(UIValidator.class, l, v);
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
			error = field.getModelController().validSubValueAdded(this, field,
					added);
			if (error) {
				return true;
			}
			UIValidator[] listeners = this._listen.get(field
					.getAttributeDefinition());
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					error = listeners[i].validSubValueAdded(this, field, added);
					if (error) {
						return true;
					}
				}
			}
			field.getModelController().notifieSubValueAdded(this, field, added);
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].notifieSubValueAdded(this, field, added);
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

	public boolean broadcastSubValueRemoved(IPage page, UIField field,
			Object removed) {
		try {
			boolean error;
			error = field.getModelController().validSubValueRemoved(this,
					field, removed);
			if (error) {
				return true;
			}
			UIValidator[] listeners = this._listen.get(field
					.getAttributeDefinition());
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					error = listeners[i].validSubValueRemoved(this, field,
							removed);
					if (error) {
						return true;
					}
				}
			}
			field.getModelController().notifieSubValueRemoved(this, field,
					removed);
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].notifieSubValueRemoved(this, field,
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

	// @Override
	// public void thisFieldHasChanged(UIField uiField) {
	// UIRunningField rf = runningField.get(uiField);
	// while (rf != null) {
	// rf.updateValue();
	// rf = rf._next;
	// }
	// }

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
			boolean error = field.getModelController().validValueDeleted(this,
					field, oldvalue);
			if (error) {
				return true;
			}

			UIValidator[] listeners = this._listen.get(field
					.getAttributeDefinition());
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].validValueDeleted(this, field, oldvalue);
					} catch (Throwable e) {
						e.printStackTrace();
						listeners[i].incrementError();
					}
				}
			}
			field.getModelController().notifieValueDeleted(this, field,
					oldvalue);
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].notifieValueDeleted(this, field, oldvalue);
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
					if (newType == IPageController.ERROR) {
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
		field.getModelController()
				.notifieValueChanged(this, field, visualValue);
		UIValidator[] listeners = this._listen.get(field
				.getAttributeDefinition());
		if (listeners != null) {
			for (int i = 0; i < listeners.length; i++) {
				try {
					listeners[i].notifieValueChanged(this, field, visualValue);
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
		error = field.getModelController().validValueChanged(this, field,
				visualValue);
		if (error) {
			return true;
		}
		UIValidator[] listeners = this._listen.get(field
				.getAttributeDefinition());
		if (listeners != null) {
			for (UIValidator v : listeners) {
				try {
					error = v.validValueChanged(this, field, visualValue);
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
		IModelController getFct = field.getModelController();
		if (getFct == null) {
			return null;
		}
		Object abstratcObject = getFct.getValue(this);
		if (abstratcObject == null) {
			Object defaultValue = getFct.defaultValue();
			if (defaultValue != null) {
				getFct.notifieValueChanged(this, field, defaultValue);
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
		IModelController getFct = field.getModelController();
		if (getFct != null) {
			error = getFct.validValue(this, field, visualValue);
		}
		if (error) {
			return true;
		}

		UIValidator[] listeners = this._listen.get(field
				.getAttributeDefinition());
		if (listeners != null) {
			for (UIValidator v : listeners) {
				try {
					error = v.validValue(this, field, visualValue);
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
	private IActionPage action;
	private FedeFormToolkit _toolkit;
	private IPageSite _pageSite;
	private IModelController _defaultModelController;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.Pages#doFinish(java.lang.Object)
	 */
	public boolean doFinish(Object monitor) throws Exception {
		action.doFinish(this, monitor);
		copy.commit();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.Pages#doCancel(java.lang.Object)
	 */
	public void doCancel(Object monitor) {
		action.doCancel(this, monitor);
		copy.rollback();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.Pages#getItem()
	 */
	public Item getItem() {
		String typeid = this.action.getTypeId();
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
		String typeid = this.action.getTypeId();
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
	
	static public Pages createWizard(AbstractActionPage action, IPage... pages) {

		return new PagesImpl(false, action, pages);
	}

	static public DBrowserUI createOneFolderOrFileField(String key, String label, String title, String message,
			boolean selectFolder, String filter, int kindroot) {

		IModelController mc = new StringToOneResourceModelController();
		IC_FileResourceForBrowser_Combo_List ic = new IC_FileResourceForBrowser_Combo_List(title, message, kindroot,
				filter, selectFolder);
		DBrowserUI ui = new DBrowserUI(key, label, EPosLabel.top, mc, ic, SWT.BORDER | SWT.SINGLE);
		return ui;

	}

	static public DListUI createFolderField(String key, String label, String title, String message, int kindroot) {

		IC_FolderResource_ForBrowser_Combo_List ic = new IC_FolderResource_ForBrowser_Combo_List(title, message,
				kindroot);
		StringToResourceListModelController mc = new StringToResourceListModelController();
		DListUI ui = new DListUI(key, label, EPosLabel.top, mc, ic, true, true);
		return ui;

	}

	static public DTextUI createShortNameField() {
		return new DTextUI(CadseGCST.ITEM_at_NAME, "name:", EPosLabel.left, new MC_ShortNameItemProperty(),
				null);

	}

	static public DTextUI createShortNameField_Noborder() {
		return new DTextUI(CadseGCST.ITEM_at_NAME, "name:", EPosLabel.left, new MC_ShortNameItemProperty(),
				null, SWT.SINGLE, 1, null);
	}

	static public DTextUI createUniqueNameField() {
		return createShortNameField_Noborder();

	}

	static public DTextUI createDisplayNameField() {
		return new DTextUI(CadseGCST.ITEM_at_DISPLAY_NAME, "display name:", EPosLabel.left,
				new MC_DisplayNameItemProperty(), null, SWT.SINGLE, 1, null);
	}

	static public DTextUI createIDField() {
		return new DTextUI(CadseGCST.ITEM_at_DISPLAY_NAME, "#ID:", EPosLabel.left, new MC_IDItemProperty(),
				null, SWT.SINGLE, 1, null);
	}

	// TODO
	// static public IFieldDescription createLinkCheckDependencyField(String
	// linkName, String... linksTransitives) {
	// return createFD(linkName,
	// IFieldDescription.LABEL,"",
	// IFieldDescription.POS_LABEL, EPosLabel.none,
	// IFieldDescription.VALUE_CONTROLLER,new DefaultValueControler(),
	// IFieldDescription.FIELD_UI_CONTROLLER, new LinkViewerController(),
	// LinkViewerController.LINKS_TRANSITIVES, linksTransitives);
	// }
	public DCheckedListUI createCheckBoxList(String key, String label, IC_ForCheckedViewer ic,
			IModelController mc) {
		return new DCheckedListUI(key, label, label == null ? EPosLabel.none : EPosLabel.top, mc, ic);
	}

	public DCheckBoxUI createCheckBox(String key, String label) {
		return createCheckBox(key, label, null);
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

	public DCheckBoxUI createCheckBox(IPage page, BooleanAttributeType key, String label, RuningInteractionController ic) {
		return initDefaultRunningField(page, key, null, ic, new DCheckBoxUI());
	}

	private <IC extends RuningInteractionController, T extends UIRunningField<IC>> T initDefaultRunningField(IPage page, IAttributeType<?> key,
			IModelController defaultMC, IC ic,  T ret) {
		ret._field = createDefaultField(key, defaultMC);
		ret._ic = ic;
		ret._page = page;
		ret._swtuiplatform = this;
		this.runningField.put(ret._field, ret);
		this.pages.setUIField(key, ret._field);
		return ret;
	}

	private UIField createDefaultField(IAttributeType<?> key, IModelController defaultMC) {
		UIField defaultField = key.generateDefaultField();
		if (defaultField.getModelController() == null) {
			defaultField.setModelController(defaultMC == null ? getDefaultModelController() : defaultMC);
		}
		
		return defaultField;
	}

	public IModelController getDefaultModelController() {
		return _defaultModelController;
	}

	public DBrowserUI createLinkDependencyField(LinkType key, String label, IC_LinkForBrowser_Combo_List ic,
			boolean mandatory, String msg) {
		return createLinkDependencyField(key, label, label == null ? EPosLabel.none : EPosLabel.top, ic, mandatory, msg);

	}

	public DBrowserUI<IC_LinkForBrowser_Combo_List> createLinkDependencyField(IPage page, LinkType key, EPosLabel poslabel,
			IC_LinkForBrowser_Combo_List ic, boolean mandatory, String msg) {
		//return new DBrowserUI(key.getName(), label, poslabel, mc, ic, SWT.BORDER | SWT.SINGLE);

		DBrowserUI<IC_LinkForBrowser_Combo_List> rf = initDefaultRunningField(page, key,  new LinkModelController(mandatory, msg), ic, new DBrowserUI<IC_LinkForBrowser_Combo_List>());
		if (poslabel != null)
			rf._field.setPositionLabel(poslabel);
		rf._field.setStyle(SWT.BORDER | SWT.SINGLE);
		return rf;
	}

	static public DBrowserUI createSelectContainmentItemField(String label, String selectTitle, String selectMessage) {
		return new DBrowserUI("", label, EPosLabel.left, new ParentPartGetAndSet(), new IC_PartParentForBrowser_Combo(
				selectTitle, selectMessage), SWT.BORDER | SWT.SINGLE);
	}

	public <IC extends RuningInteractionController> DTextUI<IC> createIntField(IPage page, IntegerAttributeType key, IC ic) {
		return initDefaultRunningField(page, key,  null, ic, new DTextUI<IC>());
	}

	public static DTextUI createIntField( key, String label, IModelController mc) {
		return new DTextUI(key, label, EPosLabel.left, mc, null);
	}

	public static DBrowserUI createBrowserIconField(String key, String label, EPosLabel poslabel) {
		return new DBrowserUI(key, label, poslabel, new StringToResourceSimpleModelController(),
				new IC_IconResourceForBrowser_Combo_List(), SWT.BORDER | SWT.SINGLE);
	}

	public static DBrowserUI createBrowserField(String key, String label, EPosLabel poslabel,
			IInteractionControllerForBrowserOrCombo ic, IModelController mc) {
		return new DBrowserUI(key, label, poslabel, mc, ic, SWT.BORDER | SWT.SINGLE);
	}

	public static DComboUI createComboBox(String key, String label, EPosLabel poslabel,
			IInteractionControllerForBrowserOrCombo ic, IModelController mc, boolean edit) {
		if (mc == null) {
			mc = new MC_AttributesItem();
		}
		return new DComboUI(key, label, poslabel, mc, ic, edit);
	}

	public static Pages createDefaultNameWizard(String title, String description, CreationAction action) {
		return createWizard(action, FieldsCore.createPage("page1", title, description, 2, FieldsCore
				.createShortNameField()));
	}

	public static DTextUI createTextField(String key, String label) {
		return createTextField(key, label, 1, null, null, null);
	}

	public static DTextUI createTextField(String key, String label, int vspan) {
		return createTextField(key, label, vspan, null, null, null);
	}

	public DTextUI createTextField(String key, String label, String tooltip) {
		return createTextField(key, label, 1, tooltip, null, null);
	}

	public DTextUI createTextField(String key, String label, IModelController mc) {
		return createTextField(key, label, 1, null, null, mc);
	}

	public DTextUI createTextField(String key, String label, RuningInteractionController uc) {
		return createTextField(key, label, 1, null, uc, null);
	}

	public DTextUI createTextField(String key, String label, int vspan, String tooltip,
			RuningInteractionController ic, IModelController mc) {
		if (mc == null) {
			mc = new MC_AttributesItem();
		}
		
		return new DTextUI(key, label, EPosLabel.left, mc, ic, 0, vspan, tooltip);
	}

	UIField createField(IAttributeType<?> att) {
		new UIfield
	}
	public static DListUI createList_ListOfString(String key, String label, String title, String message,
			boolean allowDuplicate, int min, int max) {
		return createList(key, label, new MC_DefaultForList(min, max), new IC_DefaultForList(title,
				message, allowDuplicate));
	}

	public static DListUI createList_ListOfString(String key, String label, MC_DefaultForList mc,
			IC_DefaultForList ic) {
		return createList(key, label, mc, ic);
	}

	public static DListUI createList(String key, String label, IModelController mc, IInteractionControllerForList ic,
			Object... objects) {
		return new DListUI(key, label, EPosLabel.top, mc, ic, true, true);

	}

	public IAttributeType<?> createFictifAttributte(String name, IAttributeType<?> ...attributeTypes) {
		return new FictifAttribute(newID(), name, 0, attributeTypes);
	}

	public <IC extends IC_TreeModel> DTreeModelUI<IC> createTreeModelUI(IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel none, IModelController mc,
			IC ic, boolean checkBox) {
		DTreeModelUI<IC> ret = initDefaultRunningField(page, attributte, mc, ic, new DTreeModelUI<IC>());
		ret._useCheckBox = checkBox;
		return ret;
	}

	public <IC extends ICRunningField> DTextUI<IC> createTextUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel none, IModelController mc,
			IC ic, int vspan, boolean multiLine, boolean noBorder, boolean wrapLine, boolean hscroll, boolean vscroll) {
		DTextUI<IC> ret = initDefaultRunningField(page, attributte, mc, ic, new DTextUI<IC>());
		ret._field.setType(CadseGCST.DTEXT);
		ret._field.setFlag(Item.UI_TEXT_MULTI_LINE, multiLine);
		ret._field.setFlag(Item.UI_NO_BORDER, noBorder);
		ret._field.setFlag(Item.UI_TEXT_WRAP_LINE, wrapLine);
		ret._field.setAttribute(CadseGCST.DTEXT_at_MULTI_LINE_, multiLine);
		ret._field.setAttribute(CadseGCST.DTEXT_at_NO_BORDER_, noBorder);
		ret._field.setAttribute(CadseGCST.DTEXT_at_WRAP_LINE_, wrapLine);
		ret._field.setFlag(Item.UI_HSCROLL, hscroll);
		ret._field.setFlag(Item.UI_VSCROLL, vscroll);
		ret._vspan = vspan;
		
		return ret;
	}

	public <IC extends ICRunningField> DSashFormUI<IC> createDSashFormUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel none, IModelController mc,
			IC ic, UIRunningField<?> child1, UIRunningField<?> child2) {
		DSashFormUI<IC> ret = initDefaultRunningField(page, attributte, mc, ic, new DSashFormUI<IC>());
		ret._children = new UIRunningField<?>[] { child1, child2 };
		return ret;
	}

	public <IC extends ICRunningField> DGridUI<IC> createDGridUI(
			IPage page,
			IAttributeType<?> attributte, String label,
			EPosLabel none, IModelController mc,
			IC ic, UIRunningField<?>... children) {
		DGridUI<IC> ret = initDefaultRunningField(page, attributte, mc, ic, new DGridUI<IC>());
		ret._children = children;
		return ret;
	}

	public IPage createPageDescription(String title, String label) {
		return new PageImpl("p1", label, title, title, true);
	}

	

}