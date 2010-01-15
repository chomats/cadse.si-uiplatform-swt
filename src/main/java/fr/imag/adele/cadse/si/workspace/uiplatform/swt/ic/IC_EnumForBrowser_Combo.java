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

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.EnumAttributeType;

public class IC_EnumForBrowser_Combo extends IC_AbstractForBrowser_Combo {

	private Class<?>	_values;

	
	@Override
	public void init() throws CadseException {
		super.init();
		EnumAttributeType<?> enumAttribute = (EnumAttributeType<?>) getUIField().getAttributeDefinition();
		_values = enumAttribute.getAttributeType();
	}
	
	public Object[] getValues() {
		return _values.getEnumConstants();
	}

	@Override
	public String toString(Object value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	@Override
	protected Object[] getSelectableValues() {
		return _values.getEnumConstants();
	}

	public ItemType getType() {
		return CadseGCST.IC_ENUM_FOR_BROWSER_COMBO;
	}

}
