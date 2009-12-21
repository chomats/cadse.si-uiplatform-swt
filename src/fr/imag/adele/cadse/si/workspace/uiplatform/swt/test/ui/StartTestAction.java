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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.test.ui;

import java.net.URL;

import org.eclipse.jface.window.IShellProvider;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;

public class StartTestAction extends IMenuAction {
	private IShellProvider	shellProvider;

	public StartTestAction(IShellProvider shellProvider) {
		this.shellProvider = shellProvider;
	}

	@Override
	public URL getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Start";
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {

		try {
			StartTestActionPage myaction = new StartTestActionPage(new SWTUIPlatform(), "Record test",
					"Start a new test");
			myaction.setDirectoryPath(null);
			myaction.setPageSize(300, 200);
			myaction.open(shellProvider.getShell());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
