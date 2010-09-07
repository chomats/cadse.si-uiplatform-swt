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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.test.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.dialog.SWTDialog;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.ICRunningField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_ForChooseFile;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic.IC_TreeCheckedUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DCheckedTreeUI;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.DChooseFileUI;

/**
 * The Class ImportCadsePagesAction.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class RunTestActionPage extends SWTDialog {

	public RunTestActionPage(SWTUIPlatform swtuiPlatforms, String title, String label) {
		super(swtuiPlatforms, title, label);
		addLast(createDirectoryField(), createListField());
	}

	/**
	 * The Class CadseViewerFilter.
	 */
	class CadseViewerFilter extends ViewerFilter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
		 * .Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IContainer) {
				return true;
			}
			return false;
		}

	}

	/** The select jar. */
	IPath				directoryPath;

	/** The file. */
	File				directoryFile;

	List<File>			selectedTest;

	/** The cadse viewer filter. */
	public ViewerFilter	cadseViewerFilter	= new CadseViewerFilter();

	/**
	 * The Class MC_Import.
	 */
	class MC_Directory extends AbstractModelController {

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IModelController#getValue()
		 */
		@Override
		public Object getValue() {
			return directoryPath;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fr.imag.adele.cadse.core.ui.IEventListener#notifieValueChanged(fr
		 * .imag.adele.cadse.core.ui.UIField, java.lang.Object)
		 */
		@Override
		public void notifieValueChanged(UIField field, Object value) {
			directoryPath = (IPath) value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fr.imag.adele.cadse.core.ui.AbstractModelController#notifieSubValueAdded
		 * (fr.imag.adele.cadse.core.ui.UIField, java.lang.Object)
		 */
		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seefr.imag.adele.cadse.core.ui.AbstractModelController#
		 * notifieSubValueRemoved(fr.imag.adele.cadse.core.ui.UIField,
		 * java.lang.Object)
		 */
		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fr.imag.adele.cadse.core.ui.AbstractModelController#notifieValueDeleted
		 * (fr.imag.adele.cadse.core.ui.UIField, java.lang.Object)
		 */
		@Override
		public void notifieValueDeleted(UIField field, Object oldvalue) {
			directoryPath = null;
		}

		/**
		 * Valid value changed.
		 * 
		 * @param value
		 *            the value
		 * 
		 * @return the string
		 */
		@Override
		public boolean validValueChanged(UIField field, Object value) {
			directoryFile = getFile((IPath) value);
			if (directoryFile == null || !directoryFile.exists() || !directoryFile.isDirectory()) {
				_uiPlatform.setMessageError("Select a directory");
				return true;
			}
			return false;
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class MC_ListTest extends AbstractModelController {

		@Override
		public Object getValue() {
			return selectedTest.toArray(new File[selectedTest.size()]);
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
		}

		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
			if (added instanceof File) {
				selectedTest.add((File) added);
			}
		}

		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
			if (removed instanceof File) {
				selectedTest.remove(removed);
			}
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class IC_ListTest extends ICRunningField implements IC_TreeCheckedUI {

		public String canObjectDeselected(Object obj) {
			File f = (File) obj;
			return null;
		}

		public String canObjectSelected(Object obj) {
			File f = (File) obj;
			if (isTest(f)) {
				return null;
			}
			return null;
		}

		public Object[] getChildren(Object obj) {
			File f = (File) obj;
			if (isTest(f)) {
				return null;
			}
			List<File> ret = new ArrayList<File>();
			File[] children = f.listFiles();
			if (children == null) {
				return null;
			}
			for (File ch : children) {
				if (ch.isDirectory()) {
					ret.add(ch);
				}
			}
			return ret.toArray(new File[ret.size()]);
		}

		public Object getParent(Object obj) {
			File f = (File) obj;
			return f.getParentFile();
		}

		public void edit(Object obj) {
			File f = (File) obj;
		}

		public Object[] getSources() {
			return new Object[] { directoryFile };
		}

		public void select(Object obj) {
			File f = (File) obj;
		}

		public Image toImageFromObject(Object obj) {
			return null;
		}

		public String toStringFromObject(Object obj) {
			File f = (File) obj;
			return f.getName();
		}

	}

	/**
	 * The Class IC_Import.
	 */
	class IC_Directory extends IC_ForChooseFile {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fede.workspace.model.manager.properties.impl.ic.IC_ForChooseFile#
		 * getViewerFilter()
		 */
		@Override
		protected ViewerFilter getViewerFilter() {
			return cadseViewerFilter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fede.workspace.model.manager.properties.impl.ic.IC_ForChooseFile#
		 * getKind()
		 */
		@Override
		public int getKind() {
			return FOLDER_EXT;
		}
	}

	/**
	 * Creates the directory field.
	 * 
	 * @return the d choose file ui
	 */
	public DChooseFileUI<IC_Directory> createDirectoryField() {
		return _swtuiPlatforms.createDChooseFileUI(_page, "selectJar", "Directory:", EPosLabel.left,
				new MC_Directory(), new IC_Directory(), "Select a directory where tests are stored");
	}

	public boolean isTest(File f) {
		return false;
		// return View.getInstance().getTestService().isTest(f);
	}

	/**
	 * Creates the import field.
	 * 
	 * @return the d choose file ui
	 */
	public DCheckedTreeUI<IC_ListTest> createListField() {
		return _swtuiPlatforms.createDCheckedTreeUI(_page, "name", "Test:", EPosLabel.top, new MC_ListTest(),
				new IC_ListTest(), false, false);
	}

	/**
	 * Gets the select jar.
	 * 
	 * @return the select jar
	 */
	public IPath getDirectoryPath() {
		return directoryPath;
	}

	/**
	 * Sets the select jar.
	 * 
	 * @param selectJar
	 *            the new select jar
	 */
	public void setDirectoryPath(IPath directoryPath) {
		this.directoryPath = directoryPath;
	}

	class MyAction extends AbstractActionPage {
		@Override
		public void doFinish(UIPlatform uiPlatform, Object monitor) throws Exception {
			// TODO Auto-generated method stub
			super.doFinish(uiPlatform, monitor);
			IProgressMonitor pmo = (IProgressMonitor) monitor;
			// View.getInstance().getTestService().executeTest(testDirectory);
		}
	}

	/**
	 * Gets the file.
	 * 
	 * @param path
	 *            a path
	 * 
	 * @return the file associated with this path
	 */
	private File getFile(IPath path) {
		if (path == null) {
			return null;
		}
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (r != null) {
			return r.getLocation().toFile();
		}
		return path.toFile();
	}

	@Override
	protected IActionPage getFinishAction() {
		return new MyAction();
	}

}
