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

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.IFieldContenProposalProvider;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.ui.Proposal;

final public class IC_Min extends ICRunningField implements	IFieldContenProposalProvider, IContentProposalProvider {

	public char[] getAutoActivationCharacters() {
		IAttributeType<?> attRef = getUIField().getAttributeDefinition();
		if (attRef != null && attRef.canBeUndefined()) {
			return new char[] { '0', '1', 'n' };
		}
		return new char[] { '0', '1' };
	}

	public String getCommandId() {
		return ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
	}

	public IContentProposalProvider getContentProposalProvider() {
		return this;
	}

	public int getProposalAcceptanceStyle() {
		return ContentProposalAdapter.PROPOSAL_REPLACE;
	}

	public Object setControlContents(String newValue) {
		return newValue;
	}

	public Object getValueFromProposal(Proposal proposal) {
		return proposal.getContent();
	}

	public IContentProposal[] getProposals(String contents, int position) {
		IAttributeType<?> attRef = getUIField().getAttributeDefinition();
		Proposal proposal_value_0 = new Proposal("0", "0", "optional value", 1);
		Proposal proposal_value_1 = new Proposal("1", "1", "mandatory value", 1);
		if (attRef != null && attRef.canBeUndefined()) {
			Proposal proposal_value_null = new Proposal("null", "null", "null value", 4);
			if (position == 1 && contents.length() >= 1 && (contents.charAt(0) == 'n' || contents.charAt(0) == 'N')) {
				return new IContentProposal[] { proposal_value_null };
			}
			return new IContentProposal[] { proposal_value_null, proposal_value_0, proposal_value_1 };
		}
		return new IContentProposal[] { proposal_value_0, proposal_value_1 };
	}
}
