package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import java.awt.*;

public class AgentPaiement extends Agent {

    @Override
    protected void setup() {
        System.out.println("AgentPaiement prêt.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String contenu = msg.getContent();
                    // Debug pour vérifier la réception du message de paiement
                    System.out.println("Message reçu dans AgentPaiement: " + contenu);

                    if (contenu.startsWith("PAIEMENT|")) {
                        String montantStr = contenu.split("\\|")[1];
                        double montant = Double.parseDouble(montantStr);
                        System.out.println("Montant reçu pour paiement : " + montant);

                        ACLMessage msgUtilisateur = new ACLMessage(ACLMessage.INFORM);
                        msgUtilisateur.addReceiver(new jade.core.AID("utilisateur", jade.core.AID.ISLOCALNAME));
                        msgUtilisateur.setContent("REDUCTION_APPLIQUEE:" + montant);
                        send(msgUtilisateur);

                        System.out.println("Message REDUCTION_APPLIQUEE envoyé à l'AgentUtilisateur.");

                    }
                } else {
                    block();
                }
            }
        });

    }


}
