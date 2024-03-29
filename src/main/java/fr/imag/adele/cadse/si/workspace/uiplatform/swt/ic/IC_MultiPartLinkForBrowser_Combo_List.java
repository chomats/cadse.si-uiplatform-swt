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

import java.util.Comparator;

import fede.workspace.tool.view.node.FilteredItemNodeModel;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LinkType;

/**
 * The Class IC_PartLinkForBrowser_Combo_List.
 * 
 * @generated
 */
public class IC_MultiPartLinkForBrowser_Combo_List extends IC_FilteredNodeLinkForBrowser_Combo_List {

	private final class StringComparor implements Comparator<Item> {
		public int compare(Item arg0, Item arg1) {
			return IC_MultiPartLinkForBrowser_Combo_List.this.toString(arg0).compareTo(
					IC_MultiPartLinkForBrowser_Combo_List.this.toString(arg1));
		}
	}

	/** The part link type. */
	LinkType[]	showedLinkType;

	/**
	 * The Constructor.
	 * 
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param linkType
	 *            the link type
	 * @param partLinkType
	 *            the part link type
	 * @param errormessage
	 *            the errormessage
	 * 
	 * @generated
	 */

	protected void createFiltedModel() {
		FilteredItemNodeModel newModel = new FilteredItemNodeModel();
		setModel(newModel);

		for (LinkType lt : showedLinkType) {
			newModel.addItemFromItemTypeEntry(null, lt.getSource(), getComparator());
			newModel.addItemFromLinkTypeEntry(lt.getSource(), lt, getComparator(), true, false);
		}

	}

	/**
	 * Gets the comparator.
	 * 
	 * @return the comparator
	 */
	protected Comparator<Item> getComparator() {
		return new StringComparor();
	}

}
