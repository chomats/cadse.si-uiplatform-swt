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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;

public class IC_WithDialogAction extends ICRunningField {

	public String _title;
	public String _message;

	public IC_WithDialogAction() {
	}

	public IC_WithDialogAction(String title2, String message2) {
		_title = title2;
		_message = message2;
	}

	@Override
	public void init() throws CadseException {
		if (_ic != null) {
			_title = _ic.getAttribute(CadseGCST.IC_WITH_TITLE_FOR_DIALOG_at_SELECT_TITLE_);
			_message = _ic.getAttribute(CadseGCST.IC_WITH_TITLE_FOR_DIALOG_at_SELECT_MESSAGE_);
		}
		if (_title == null || _title.length() == 0) {
			_title = "Add";
		}
	}

	final public String getMessage() {
		return _message;
	}

	final public String getTitle() {
		return _title;
	}

}
