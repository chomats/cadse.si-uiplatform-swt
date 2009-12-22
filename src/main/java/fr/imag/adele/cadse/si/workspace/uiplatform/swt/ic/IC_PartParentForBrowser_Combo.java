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

import java.util.ArrayList;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemState;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.ui.IFieldDescription;

/**
 * String link-browser-item-type, nom du type d'item � selection dans le
 * workspace. suppose que la key est le nom du lien.
 */

public class IC_PartParentForBrowser_Combo extends IC_AbstractForBrowser_Combo implements IC_ForBrowserOrCombo {

	@Override
	public String toString(Object value) {
		if (value == null) {
			return "<none>";
		}
		if (value instanceof Item) {
			return ((Item) value).getName();
		}
		return "???";
	}

	@Override
	protected Object[] getSelectableValues() {
		Item currentItem = _uiPlatform.getItem(getUIField());
		if (currentItem.getState() != ItemState.NOT_IN_WORKSPACE) {
			return new Object[0];
		}
		ArrayList<Item> ret = new ArrayList<Item>();
		for (LinkType lt : currentItem.getType().getIncomingLinkTypes()) {
			if (lt.isPart()) {
				ret.addAll(lt.getSource().getItems());
			}
		}
		return ret.toArray();
	}

	@Override
	protected Object createGoodObject(Object object) {
		Item parentItem = (Item) object;
		Item currentItem = _uiPlatform.getItem(getUIField());
		LinkType ltselect = null;
		for (LinkType lt : currentItem.getType().getIncomingLinkTypes()) {
			if (lt.isPart() && lt.getSource().equals(parentItem.getType())) {
				ltselect = lt;
				break;
			}
		}
		_uiPlatform.setVariable(IFieldDescription.PARENT_CONTEXT, parentItem);
		_uiPlatform.setVariable(IFieldDescription.INCOMING_LINK_TYPE, ltselect);
		return parentItem;
	}

	public Object[] getValues() {
		return getSelectableValues();
	}

	public ItemType getType() {
		return CadseGCST.IC_PART_LINK_FOR_BROWSER_COMBO_LIST;
	}
}