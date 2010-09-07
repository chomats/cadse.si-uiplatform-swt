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

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.ui.AbstractUIRunningValidator;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Convert;

public class MinMaxValidator extends AbstractUIRunningValidator {
	IAttributeType<Integer> minAttribute = CadseGCST.LINK_TYPE_at_MIN_;
	IAttributeType<Integer> maxAttribute = CadseGCST.LINK_TYPE_at_MAX_;
	
	@Override
	public boolean validValue(UIField field, Object value) {
		try {
			IAttributeType<?> attRef = field.getAttributeDefinition();
			if (attRef == minAttribute) {
				int min = 1;
				if (attRef != null) {
					Integer i = (Integer) attRef.convertTo(value);
					if (i == null) {
						if (attRef.cannotBeUndefined()) {
							_uiPlatform.setMessageError("The field '" + field.getLabel() + "' must be defined");
							return true;
						}
						return false;
					}
					min = i.intValue();
				} else {
					if (value == null || "".equals(value) || "null".equals(value)) {
						return false;
					}
	
					min = Integer.parseInt((String) value);
				}
				if (min <= -1) {
					_uiPlatform.setMessageError("The field '" + field.getLabel() + "' must be > -1");
					return true;
				}
				int max = getMax();
				if (max != -1 && max < min) {
					_uiPlatform.setMessageError("The field '" + field.getLabel() + "' must be less or equal at max value (" + max
							+ ")");
					return true;
				}
			} else if (attRef == maxAttribute) {
				int max = 0;
				Integer i = (Integer) getMax2();
				if (i == null) {
					if (attRef.cannotBeUndefined()) {
						_uiPlatform.setMessageError("The field '" + attRef.getName() + "' must be defined");
						return true;
					}
					return false;
				}
				max = i.intValue();
				
				if (max <= 0 && max !=-1) {
					_uiPlatform.setMessageError("The field '" + field.getName() + "' must be > 0");
					return true;
				}
				int min = getMin();
				if (max != -1 && max < min) {
					_uiPlatform.setMessageError("The field '" + field.getName()
							+ "' must be upper or equal at min value (" + min + ")");
					return true;
				}
			}
		} catch (NumberFormatException e) {
			_uiPlatform.setMessageError(e.getMessage());
			return true;
		}

		return false;
	}
	
	public int getMin() {
		UIField minfield = _uiPlatform.getField(minAttribute);
		Integer value = minAttribute.convertTo(_uiPlatform.getModelValue(minfield));
		if (value == null) {
			return 0;
		}
		return ((Integer) value).intValue();
	}


	public int getMax() {
		UIField maxField = _uiPlatform.getField(maxAttribute);
		Integer maxStr = maxAttribute.convertTo(_uiPlatform.getModelValue(maxField));
		if (maxStr != null) {
			try {
				return Convert.toInt(maxStr, null);
			} catch (NumberFormatException e) {

			}
		}
		return -1;
	}
	
	public Integer getMax2() {
		UIField maxField = _uiPlatform.getField(maxAttribute);
		Integer maxStr = maxAttribute.convertTo(_uiPlatform.getModelValue(maxField));
		
		return maxStr;
	}
	
}
