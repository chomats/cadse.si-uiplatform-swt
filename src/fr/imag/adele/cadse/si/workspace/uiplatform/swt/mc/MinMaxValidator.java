package fr.imag.adele.cadse.si.workspace.uiplatform.swt.mc;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.ui.AbstractUIRunningValidator;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Convert;

public class MinMaxValidator extends AbstractUIRunningValidator {
	IAttributeType<Integer> minAttribute = CadseGCST.LINK_at_MIN_;
	IAttributeType<Integer> maxAttribute = CadseGCST.LINK_at_MAX_;
	
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
				Integer i = (Integer) attRef.convertTo(value);
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
	
}
