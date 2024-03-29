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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.transaction.delta.LinkDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.Activator;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.IFieldContenProposalProvider;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.Proposal;

/**
 * String link-browser-item-type, nom du type d'item � selection dans le
 * workspace. suppose que la key est le nom du lien.
 */

public class IC_LinkForBrowser_Combo_List extends IC_AbstractTreeDialogForList_Browser_Combo implements
		IC_ForBrowserOrCombo, IFieldContenProposalProvider, IContentProposalProvider, IC_ForList {

	LinkType	linkType;
	boolean		deleteExistingLink	= true;

	public IC_LinkForBrowser_Combo_List() {
	}

	public IC_LinkForBrowser_Combo_List(String title, String message) {
		super(title, message);
	}

	@Override
	public void init() throws CadseException {
		super.init();
		linkType = (LinkType) getUIField().getAttributeDefinition();
	}

	public String toString(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Link) {
			Link l = (Link) value;
			if (l.isLinkResolved()) {
				return l.getDestination().getDisplayName();
			}
			return ((Link) value).getDestinationName();
		}

		return ((Item) value).getName();
	}

	@Override
	protected Object createGoodObject(Object object) {
		try {
			return createLink(_uiPlatform.getItem(getUIField()), (Item) object);
		} catch (CadseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Link createLink(Item item, Item dest) throws CadseException {
		LogicalWorkspaceTransaction copy = null;
		if (_uiPlatform.isModification()) {
			copy = getUIField().getLogicalWorkspace().createTransaction();
		} else {
			copy = _uiPlatform.getCopy();
		}

		ItemDelta deltaItem = copy.loadItem(item);
		if (deleteExistingLink) {
			List<Link> result = deltaItem.getOutgoingLinks(getLinkType());
			if (result != null) {
				for (Link l : result) {
					((LinkDelta) l).delete();
				}
			}
		}
		LinkDelta linkDetla = deltaItem.createLink(getLinkType(), dest);

		if (_uiPlatform.isModification()) {
			copy.commit();
			return item.getOutgoingLink(getLinkType(), dest.getId());
		} else {
			return linkDetla;
		}
	}

	public char[] getAutoActivationCharacters() {
		return new char[0];
	}

	public String getCommandId() {
		return ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
	}

	public IContentProposalProvider getContentProposalProvider() {
		return this;
	}

	public IContentProposal[] getProposals(String contents, int position) {
		if (contents != null && contents.length() == 0) {
			contents = null;
		}
		if (contents != null && contents.startsWith("<none>")) {
			contents = contents.substring(6);
		}
		if (contents != null && contents.length() == 0) {
			contents = null;
		}
		if (position == 0) {
			contents = null;
		}
		if (contents != null && position > 0 && position < contents.length()) {
			contents = contents.substring(0, position);
		}
		Object[] items = getValues();
		ArrayList<Proposal> ret = new ArrayList<Proposal>();
		if (items != null) {
			for (Object oitem : items) {
				Item item = (Item) oitem;
				// if (contents != null &&
				// !item.getShortName().startsWith(contents)) {
				// continue;
				// }
				Proposal p = createProposal(item, contents, position, items);
				if (p != null) {
					ret.add(p);
				}
			}
		}

		IContentProposal[] retarray = ret.toArray(new IContentProposal[ret.size()]);
		Arrays.sort(retarray);
		return retarray;
	}

	/**
	 * 
	 * @param item
	 *            a value
	 * @param contents
	 *            the begin text selected or null if empty
	 * @param position
	 *            the current position
	 * @return null or a proposal
	 */
	protected Proposal createProposal(Item item, String contents, int position, Object[] items) {
		if (contents != null && !item.getName().startsWith(contents)) {
			return null;
		}
		return new Proposal(item.getName(), item.getName(), item.getName(), 0, item);
	}

	public int getProposalAcceptanceStyle() {
		return ContentProposalAdapter.PROPOSAL_REPLACE;
	}

	public Object getValueFromProposal(Proposal proposal) {
		Item item = (Item) proposal.getValue();
		Object value = null;
		if (item != null) {
			value = createGoodObject(item);
		}
		return value;
	}

	public Object setControlContents(String newValue) {
		return null;
	}

	protected Item getItemFromShortName(String newValue) {
		Item item = CadseCore.getLogicalWorkspace().getItemByName(getLinkType().getDestination(), newValue);
		return item;
	}

	public Object[] getValues() {
		Collection<Item> values = this.getLinkType().getSelectingDestination(_uiPlatform.getItem(getUIField()));
		return values.toArray();
	}

	@Override
	public Object[] removeObject(Object[] object) {
		for (Object o : object) {
			try {
				removeLink((Link) o);
			} catch (CadseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return object;
	}

	@Override
	public Object[] transAndAddObject(Object[] object) {
		return transforme(object);
	}

	protected Object[] transforme(Object[] ret) {
		if (ret != null) {
			LinkType lt = getLinkTypeFrom();

			Link[] result = new Link[ret.length];
			int i = 0;
			Item theCurrentItem = _uiPlatform.getItem(getUIField());
			for (Object o : ret) {
				Item item1 = (Item) o;
				try {
					result[i++] = theCurrentItem.createLink(lt, item1);
				} catch (CadseException e) {
					e.printStackTrace();
					result[i++] = null;
				}
				;
			}
			return result;
		}
		return null;
	}

	protected LinkType getLinkTypeFrom() {
		return getLinkType();
	}

	public ILabelProvider getLabelProvider() {
		return new LinkLabelProvider();
	}

	protected void removeLink(Link l) throws CadseException {
		if (l != null) {
			l.delete();
		}
	}

	@Override
	protected ViewerFilter getFilter() {
		return null;
	}

	@Override
	public void initAfterUI() {
		super.initAfterUI();
		if (getUIField() != null && getUIField().getAttributeDefinition() != null) {
			linkType = (LinkType) getUIField().getAttributeDefinition();
		}
	}

	public LinkType getLinkType() {
		if (linkType == null) {
			linkType = (LinkType) getUIField().getAttributeDefinition();
		}
		return linkType;
	}

	@Override
	public Object fromString(String value) {

		Link v = (Link) _uiPlatform.getModelValue(getUIField());
		if (value == null || value.equals("<none>")) {
			if (v != null) {
				try {
					v.delete();
				} catch (CadseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		for (Item d : getLinkType().getDestinationType().getItems()) {
			if (d.getName().equals(value)) {
				return createGoodObject(d);
			}
		}
		return null;
	}

	@Override
	protected Object getInputValues() {
		return getValues();
	}

	@Override
	protected ITreeContentProvider getTreeContentProvider() {
		return new ObjectArrayContentProvider();
	}

	public IStatus validate(Object[] selection) {
		Item source = _uiPlatform.getItem(getUIField());

		// load link type
		getLinkType();
		for (Object o : selection) {
			Item dest = (Item) o;
			String msg = canCreateLink(source, dest, linkType);
			if (msg != null) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, msg, null);
			}

			Link l = source.getOutgoingLink(linkType, dest.getId());
			if (l != null) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Allready exists", null);
			}

			LinkType inv_lt = linkType.getInverse();
			if (inv_lt != null) {
				msg = canCreateLink(dest, source, inv_lt);
				if (msg != null) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, msg, null);
				}
			}

		}
		return Status.OK_STATUS;
	}

	private String canCreateLink(Item source, Item dest, LinkType lt) {

		IItemManager im = source.getType().getItemManager();
		String msg = im.canCreateLink(source, dest, lt);
		if (msg != null) {
			return msg;
		}

		if (!source.canCreateLink(lt, dest.getId())) {
			return "cannot create a link from " + source.getDisplayName() + " to " + dest.getDisplayName()
					+ " of type " + lt.getDisplayName();
		}

		if (!deleteExistingLink && lt.getMax() != -1) {
			if (source.getOutgoingItems(lt, false).size() >= lt.getMax()) {
				return "error maximum cardinality is exceed of link type " + lt.getDisplayName();
			}
		}
		return null;
	}

	public ItemType getType() {
		return CadseGCST.IC_LINK_FOR_BROWSER_COMBO_LIST;
	}

	@Override
	public boolean moveDown(Object[] object) {
		List<Object> values = (List<Object>) _uiPlatform.getModelValue(getUIField());
		int end = values.size() - 1;
		for (Object o : object) {
			Link l = (Link) o;
			int index = values.indexOf(o);
			if (index == -1 || index == end) {
				continue;
			}
			Link l2 = (Link) values.get(index + 1);
			try {
				LogicalWorkspaceTransaction t = l.getSource().getLogicalWorkspace().createTransaction();
				t.getLink(l).moveAfter(l2);
				t.commit();
			} catch (CadseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean moveUp(Object[] object) {
		List<Object> values = (List<Object>) _uiPlatform.getModelValue(getUIField());
		for (Object o : object) {
			Link l = (Link) o;
			int index = values.indexOf(o);
			if (index == -1 || index == 0) {
				continue;
			}
			Link l2 = (Link) values.get(index - 1);
			try {
				LogicalWorkspaceTransaction t = l.getSource().getLogicalWorkspace().createTransaction();
				t.getLink(l).moveBefore(l2);
				t.commit();
			} catch (CadseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public void edit(Shell shell, ITreeSelection sel) {
	}
}
