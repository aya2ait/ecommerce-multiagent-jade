package agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Iterator;

public class SnifferAgent extends Agent {
    @Override
    protected void setup() {
        // Afficher un message pour indiquer que l'agent de sniffer est prêt
        System.out.println("Sniffer Agent prêt à capturer les messages.");

        // Ajout d'un comportement pour écouter tous les messages
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Créer un MessageTemplate pour capter tous les messages de type ACLMessage
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.UNKNOWN);
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    // Afficher le contenu du message dans la console
                    System.out.println("Message capturé : ");
                    System.out.println("De: " + msg.getSender().getLocalName()); // Utilisation de getSender().getLocalName()
                    Iterator<AID> receivers = msg.getAllReceiver();
                    if (receivers.hasNext()) {
                        System.out.println("Vers: " + receivers.next().getLocalName());
                    } else {
                        System.out.println("Aucun destinataire dans le message.");
                    }

                    System.out.println("Contenu: " + msg.getContent());
                    System.out.println("Performative: " + msg.getPerformative());

                    // Si tu veux voir les autres détails du message, tu peux les afficher ici
                } else {
                    block(); // Bloque le comportement si aucun message n'est reçu
                }
            }
        });
    }
}

