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

package fr.imag.adele.cadse.si.workspace.uiplatform.swt.exportimport;

import java.net.URL;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;

/**
 * The Class ImportCadseAction.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class ImportCadseAction extends IMenuAction {

	/** The Constant IMPORT_BINARY_CADSE. */
	public static final String	IMPORT_BINARY_CADSE	= "Import cadse";

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getId()
	 */
	@Override
	public String getId() {
		return IMPORT_BINARY_CADSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getImage()
	 */
	@Override
	public URL getImage() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getLabel()
	 */
	@Override
	public String getLabel() {
		return IMPORT_BINARY_CADSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.imag.adele.cadse.core.IMenuAction#run(fr.imag.adele.cadse.core.IItemNode
	 * [])
	 */
	@Override
	public void run(IItemNode[] selection) throws CadseException {
		try {

			ImportCadsePagesAction myaction = new ImportCadsePagesAction(new SWTUIPlatform(), "Import cadse",
					"Import cadse");
			myaction.setPageSize(300, 200);
			myaction.open(null);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
