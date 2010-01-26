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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.ic;

import java.util.List;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;

public class IC_StaticArrayOfObjectForBrowser_Combo extends IC_AbstractForBrowser_Combo {

	private Object[]	_values;

	public IC_StaticArrayOfObjectForBrowser_Combo() {
	}

	public IC_StaticArrayOfObjectForBrowser_Combo(String title, String message, String[] lastCommitMsgTab) {
		this._title = title;
		this._message = message;
		_values = lastCommitMsgTab;
	}

	
	@Override
	public void init() throws CadseException {
		super.init();
		if (_ic != null) {
			Object values = _ic.getAttribute(CadseGCST.IC_STATIC_ARRAY_OF_OBJECT_FOR_BROWSER_COMBO_at_VALUES_);
			if (values instanceof List) {
				_values = ((List)values).toArray();
			} else if (values instanceof Object[]) {
				_values = (Object[]) values;
			}
		}
	}
	
	public Object[] getValues() {
		return _values;
	}

	@Override
	public String toString(Object value) {
		return value.toString();
	}

	@Override
	protected Object[] getSelectableValues() {
		return _values;
	}

	public ItemType getType() {
		return CadseGCST.IC_STATIC_ARRAY_OF_OBJECT_FOR_BROWSER_COMBO;
	}
}
