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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.exportimport;

import java.net.URL;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;

/**
 * The Class ExportCadseAction.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class ExportCadseAction extends IMenuAction {

	/** The Constant EXPORT_BINARY_CADSE. */
	public static final String	EXPORT_BINARY_CADSE	= "Export items";

	/** The cadsedef. */
	private Item[]				rootItems;

	/**
	 * Instantiates a new export cadse action.
	 * 
	 * @param cadsedef
	 *            the cadsedef
	 */
	public ExportCadseAction(Item... cadsedef) {
		this.rootItems = cadsedef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getId()
	 */
	@Override
	public String getId() {
		return EXPORT_BINARY_CADSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getImage()
	 */
	@Override
	public String getImage() {
		return CadseGCST.CADSE_DEFINITION.getImage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getLabel()
	 */
	@Override
	public String getLabel() {
		return EXPORT_BINARY_CADSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#run(fr.imag.adele.cadse.core.IItemNode[])
	 */
	@Override
	public void run(IItemNode[] selection) throws CadseException {
		try {

			ExportCadsePagesAction myaction = new ExportCadsePagesAction(new SWTUIPlatform(), "Export cadse",
					"Export cadse");
			myaction.setCadsedef(rootItems);
			myaction.setSelectJar(null);
			myaction.setPageSize(300, 200);
			myaction.open(null);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
