package agents;

import gui.InterfaceUtilisateur;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;

public class AgentUtilisateur extends Agent {
    private InterfaceUtilisateur interfaceUtilisateur;

    @Override
    protected void setup() {
        interfaceUtilisateur = new InterfaceUtilisateur(this);

        // Comportement pour recevoir les messages de recherche de produits
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                // Template pour les messages de type INFORM
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    String contenu = msg.getContent();

                    // Si le message commence par "PANIER:" ou "PANIER_MISE_A_JOUR:", c'est une mise à jour du panier
                    if (contenu.startsWith("PANIER:") || contenu.startsWith("PANIER_MISE_A_JOUR:")) {
                        String contenuPanier = contenu.substring(contenu.indexOf(":") + 1);
                        interfaceUtilisateur.mettreAJourAffichagePanier(contenuPanier);
                    }
                    // Si le message commence par "MAJ_PRODUIT:", c'est une mise à jour de produit
                    else if (contenu.startsWith("MAJ_PRODUIT:")) {
                        String infoProduit = contenu.substring(11);
                        interfaceUtilisateur.mettreAJourProduit(infoProduit);
                    }
                    // Si le message commence par "STOCK_REDUIT:" ou "STOCK_INSUFFISANT:"
                    else if (contenu.startsWith("STOCK_REDUIT:") || contenu.startsWith("STOCK_INSUFFISANT:")) {
                        String nomProduit = contenu.substring(contenu.indexOf(":") + 1);
                        if (contenu.startsWith("STOCK_INSUFFISANT:")) {
                            JOptionPane.showMessageDialog(null,
                                    "Stock insuffisant pour: " + nomProduit,
                                    "Erreur de stock",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }else if (contenu.startsWith("REDUCTION_APPLIQUEE:")) {
                        String montantAvecReduction = contenu.substring(contenu.indexOf(":") + 1);
                        double montant = Double.parseDouble(montantAvecReduction);
                        System.out.println("Appel de afficherFormulairePaiementAvecReduction avec : " + montant);
                        interfaceUtilisateur.afficherFormulairePaiementAvecReduction(montant);
                    } else if (contenu.startsWith("PAS_DE_REDUCTION:")) {
                        // Cas où il n'y a pas de réduction
                        String total = contenu.substring(contenu.indexOf(":") + 1);
                        double totalSansReduction = Double.parseDouble(total);
                        System.out.println("Appel de afficherFormulairePaiement sans réduction avec : " + totalSansReduction);
                        interfaceUtilisateur.afficherFormulairePaiement(totalSansReduction, false);
                    }




                    // Sinon, c'est une réponse de recherche de produits
                    else {
                        interfaceUtilisateur.afficherProduits(contenu);
                    }
                } else {
                    block();
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        // Demander les produits au démarrage
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                // Petit délai pour s'assurer que l'interface est initialisée
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Envoyer une requête vide pour obtenir tous les produits
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new jade.core.AID("catalogue", jade.core.AID.ISLOCALNAME));
                msg.setContent("AFFICHER_CATALOGUE");
                send(msg);

                System.out.println("Demande initiale de tous les produits envoyée");

                // Demander le contenu du panier
                demanderContenuPanier();
            }
        });
    }

    public void envoyerRecherche(String recherche) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new jade.core.AID("catalogue", jade.core.AID.ISLOCALNAME));
        msg.setContent(recherche);
        send(msg);
    }

    public void ajouterAuPanier(String nomProduit, double prix) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new jade.core.AID("panier", jade.core.AID.ISLOCALNAME));
        msg.setContent("AJOUTER:" + nomProduit + "|" + prix);
        send(msg);

        System.out.println("Demande d'ajout au panier: " + nomProduit);
    }

    private void demanderContenuPanier() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new jade.core.AID("panier", jade.core.AID.ISLOCALNAME));
        msg.setContent("AFFICHER_PANIER");
        send(msg);
    }

    public void viderPanier() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new jade.core.AID("panier", jade.core.AID.ISLOCALNAME));
        msg.setContent("VIDER_PANIER");
        send(msg);
    }

    public void passerCommande() {
        System.out.println("Passage à la commande, envoi du message à AgentPanier...");
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("panier", AID.ISLOCALNAME));
        msg.setContent("PASSER_COMMANDE");
        send(msg);
    }



    public void supprimerDuPanier(String nomProduit) {
        // 1. Envoi à AgentPanier pour suppression
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new jade.core.AID("panier", jade.core.AID.ISLOCALNAME));
        msg.setContent("SUPPRIMER:" + nomProduit);
        send(msg);

        // 2. Envoi à AgentCatalogue pour remettre 1 unité en stock
        ACLMessage stockMsg = new ACLMessage(ACLMessage.REQUEST);
        stockMsg.addReceiver(new jade.core.AID("catalogue", jade.core.AID.ISLOCALNAME));
        stockMsg.setContent("RETOUR_STOCK:" + nomProduit + "|1"); // remet 1 en stock
        send(stockMsg);

        System.out.println("Produit supprimé du panier et stock mis à jour pour : " + nomProduit);
    }


    public void informerPaiement(double montant) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("paiement", AID.ISLOCALNAME));
        msg.setContent("PAIEMENT|" + montant);
        send(msg);
    }


}