package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class AgentPanier extends Agent {

    // Structure de données pour stocker les produits dans le panier
    // Map: Nom du produit -> [Prix, Quantité]
    private Map<String, Object[]> panier = new HashMap<>();
    private double total = 0.0;

    @Override
    protected void setup() {
        System.out.println("Agent Panier démarré");

        // Comportement pour gérer les demandes d'ajout au panier
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Recevoir uniquement les messages de type REQUEST
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    String contenu = msg.getContent();

                    // Si le message commence par "AJOUTER:", c'est une demande d'ajout au panier
                    if (contenu.startsWith("AJOUTER:")) {
                        String[] infoProduit = contenu.substring(8).split("\\|");
                        if (infoProduit.length >= 2) {
                            String nomProduit = infoProduit[0];
                            double prixProduit = Double.parseDouble(infoProduit[1]);

                            // Ajouter ou mettre à jour le produit dans le panier
                            if (panier.containsKey(nomProduit)) {
                                Object[] infoActuelle = panier.get(nomProduit);
                                int quantiteActuelle = (int) infoActuelle[1];
                                panier.put(nomProduit, new Object[]{prixProduit, quantiteActuelle + 1});
                            } else {
                                panier.put(nomProduit, new Object[]{prixProduit, 1});
                            }

                            // Recalculer le total
                            calculerTotal();

                            // Demander à l'agent Catalogue de réduire le stock
                            ACLMessage msgStock = new ACLMessage(ACLMessage.REQUEST);
                            msgStock.addReceiver(new jade.core.AID("catalogue", jade.core.AID.ISLOCALNAME));
                            msgStock.setContent("REDUIRE_STOCK:" + nomProduit + "|1");  // 1 = quantité
                            send(msgStock);

                            // Envoyer une confirmation à l'utilisateur
                            ACLMessage reponse = new ACLMessage(ACLMessage.INFORM);
                            reponse.addReceiver(msg.getSender());
                            reponse.setContent("PANIER_MISE_A_JOUR:" + genererContenuPanier());
                            send(reponse);

                            System.out.println("Produit ajouté au panier: " + nomProduit);
                        }
                    }
                    // Si le message est "AFFICHER_PANIER", envoyer le contenu du panier
                    else if (contenu.equals("AFFICHER_PANIER")) {
                        ACLMessage reponse = new ACLMessage(ACLMessage.INFORM);
                        reponse.addReceiver(msg.getSender());
                        reponse.setContent("PANIER:" + genererContenuPanier());
                        send(reponse);
                    }
                    // Si le message commence par "SUPPRIMER:", c'est une demande de suppression
                    else if (contenu.startsWith("SUPPRIMER:")) {
                        String nomProduit = contenu.substring(10);
                        if (panier.containsKey(nomProduit)) {
                            int quantite = (int) panier.get(nomProduit)[1];
                            panier.remove(nomProduit);
                            calculerTotal();

                            // Demander au catalogue de remettre en stock


                            // Envoyer une confirmation
                            ACLMessage reponse = new ACLMessage(ACLMessage.INFORM);
                            reponse.addReceiver(msg.getSender());
                            reponse.setContent("PANIER_MISE_A_JOUR:" + genererContenuPanier());
                            send(reponse);

                            System.out.println("Produit supprimé du panier: " + nomProduit);
                        }
                    }
                    // Si le message est "VIDER_PANIER", vider le panier
                    else if (contenu.equals("VIDER_PANIER")) {
                        // Remettre tous les produits en stock avant de vider


                        panier.clear();
                        total = 0.0;

                        // Envoyer une confirmation
                        ACLMessage reponse = new ACLMessage(ACLMessage.INFORM);
                        reponse.addReceiver(msg.getSender());
                        reponse.setContent("PANIER_MISE_A_JOUR:" + genererContenuPanier());
                        send(reponse);

                        System.out.println("Panier vidé");
                    } else if (contenu.equals("PASSER_COMMANDE")) {
                        System.out.println("Passage à la commande détecté dans AgentPanier");

                        // Calculer le total du panier
                        calculerTotal();
                        System.out.println("Total calculé: " + total);

                        // Envoyer le total à l'agent de réduction
                        ACLMessage reduction = new ACLMessage(ACLMessage.REQUEST);
                        reduction.addReceiver(new jade.core.AID("reduction", jade.core.AID.ISLOCALNAME));
                        reduction.setContent("TOTAL|" + total);
                        send(reduction);
                        System.out.println("Message envoyé à AgentReduction, en attente de réponse...");

                        // Attendre la réponse de l'agent de réduction
                        MessageTemplate mtReduction = MessageTemplate.and(
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                MessageTemplate.MatchSender(new jade.core.AID("reduction", jade.core.AID.ISLOCALNAME))
                        );

                        ACLMessage msgReduction = blockingReceive(mtReduction, 5000); // Attente max 5 secondes

                        if (msgReduction != null && msgReduction.getContent().startsWith("TOTAL_REDUIT|")) {
                            double nouveauTotal = Double.parseDouble(msgReduction.getContent().split("\\|")[1]);
                            total = nouveauTotal;
                            System.out.println("Réduction reçue, nouveau total: " + total);

                        } else {
                            System.out.println("Aucune réponse reçue de l'agent de réduction. Utilisation du total sans réduction.");
                        }

                        // Envoyer le paiement à l’agent paiement
                        ACLMessage msgPaiement = new ACLMessage(ACLMessage.INFORM);
                        msgPaiement.addReceiver(new jade.core.AID("paiement", jade.core.AID.ISLOCALNAME));
                        msgPaiement.setContent("PAIEMENT|" + total);
                        send(msgPaiement);

                        System.out.println("Commande passée, montant total envoyé à l'AgentPaiement: " + total);
                    }




                } else {
                    block();
                }
            }
        });


    }


    // Méthode pour calculer le total du panier
    private void calculerTotal() {
        total = 0.0;
        for (Map.Entry<String, Object[]> entry : panier.entrySet()) {
            double prix = (double) entry.getValue()[0];
            int quantite = (int) entry.getValue()[1];
            total += prix * quantite;
        }
    }



    // Méthode pour générer le contenu du panier sous forme de chaîne
    private String genererContenuPanier() {
        StringBuilder sb = new StringBuilder();

        // Format du contenu du panier: NomProduit|Prix|Quantité;NomProduit2|Prix2|Quantité2;...;TOTAL|X.XX
        for (Map.Entry<String, Object[]> entry : panier.entrySet()) {
            String nomProduit = entry.getKey();
            double prix = (double) entry.getValue()[0];
            int quantite = (int) entry.getValue()[1];

            sb.append(nomProduit).append("|")
                    .append(prix).append("|")
                    .append(quantite).append(";");
        }

        // Ajouter le total à la fin
        sb.append("TOTAL|").append(String.format("%.2f", total));

        return sb.toString();
    }

}