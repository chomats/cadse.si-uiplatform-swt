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
package fr.imag.adele.cadse.si.workspace.uiplatform.swt.mc;

import fr.imag.adele.cadse.core.impl.ui.mc.MC_Integer;
import fr.imag.adele.cadse.core.ui.UIField;

final public class MaxModelController extends MC_Integer  {


	public static final String	UNBOUNDED	= "unbounded";
	static final String			MINUS_1		= "-1";

	
	@Override
	protected Object modelToVisual(Object value) {
		if (value == null) {
			return null;
		}
		if (value.equals(MINUS_1) || value.equals(-1)) {
			return UNBOUNDED;
		}
		return value;
	}
	
	@Override
	public Object visualToModel(Object visualValue) {
		if (visualValue != null && visualValue.equals(UNBOUNDED)) {
			visualValue = -1;
		}
		return super.visualToModel(visualValue);
	}
	
	@Override
	public boolean validValueChanged(UIField field, Object value) {
		if (value != null && value.equals(MaxModelController.UNBOUNDED)) {
			return false;
		}
		return super.validValueChanged(field, value);
	}
	
	@Override
	public boolean validValue(UIField field, Object value) {
		if (value.equals(MaxModelController.UNBOUNDED)) {
			return false;
		}
		return super.validValue(field, value);
	}

	@Override
	public Object defaultValue() {
		Object ret = super.defaultValue();
		if (ret != null) {
			if (ret.equals(-1) || ret.equals("-1")) {
				return UNBOUNDED;
			}
			return ret;
		}
		return 1;
	}

	

}
