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

import fede.workspace.model.manager.properties.impl.ic.ICRunningField;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.CadseCore;
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
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.UIRunningField;

/**
 */

public class SWTUIPlatform implements IPageController {

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

		UIRunningField<T> rf = createRunningField(field);
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
		rf.createControl(container, hspan_label);
		return rf;
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

}