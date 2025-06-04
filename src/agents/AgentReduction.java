package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentReduction extends Agent {
    @Override
    protected void setup() {
        System.out.println("Agent Réduction démarré");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String contenu = msg.getContent(); // Exemple: "TOTAL|520.0"
                    if (contenu.startsWith("TOTAL|")) {
                        double montant = Double.parseDouble(contenu.split("\\|")[1]);

                        double nouveauMontant = montant;
                        if (montant >= 500) {
                            nouveauMontant = montant * 0.95; // -5%
                        }

                        ACLMessage reponse = new ACLMessage(ACLMessage.INFORM);
                        reponse.addReceiver(msg.getSender());
                        reponse.setContent("TOTAL_REDUIT|" + nouveauMontant);
                        send(reponse);

                        System.out.println("Montant original: " + montant + ", après réduction: " + nouveauMontant);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
