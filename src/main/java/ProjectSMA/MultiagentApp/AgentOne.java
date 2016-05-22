/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package ProjectSMA.MultiagentApp;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class AgentOne extends Agent {
	private AgentController t1 = null;
	private AgentController del = null;
	private static boolean IAmTheCreator = true;
	private static final String MSG = "SCLAAAAAAAVVV";
	private AID initiator = null;

	@Override
	protected void setup() {
		System.out.println("Hello i am " + getLocalName());
		String AGENT_TWO = getLocalName() + "_two";

		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			initiator = new AID((String) args[0], AID.ISLOCALNAME);
		}

		try {
			// create the agent descrption of itself
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			// register the description with the DF
			DFService.register(this, dfd);
			// System.out.println(getLocalName()+" REGISTERED WITH THE DF");
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		if (IAmTheCreator) {
			IAmTheCreator = false;
			try {
				// create agent t1 on the same container of the creator agent
				AgentContainer container = (AgentContainer) getContainerController(); 
				t1 = container.createNewAgent(AGENT_TWO, "ProjectSMA.MultiagentApp.AgentOne", null);
				t1.start();
				System.out.println(getLocalName() + " created new agent :" + AGENT_TWO);
			} catch (Exception any) {
				any.printStackTrace();
			}

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setContent(MSG);

			msg.addReceiver(new AID(AGENT_TWO, AID.ISLOCALNAME));

			send(msg);
			System.out.println(getLocalName() + " sent message " + MSG + " to " + AGENT_TWO);
			try {
				Thread.sleep(100);
			} 
			catch (InterruptedException ie) {}
			
			this.doDelete();
			
			AgentContainer container = (AgentContainer) getContainerController(); 
			try {
				del = container.getAgent(AGENT_TWO, false);
				del.kill();
			} catch (ControllerException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
			System.out.println(getLocalName() + " DEREGISTERED");
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
