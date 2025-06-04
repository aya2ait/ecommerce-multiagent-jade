package agents;

import dao.ProduitDAO;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import models.Produit;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AgentCatalogue extends Agent {
    // Liste des produits en mémoire
    private List<Produit> catalogue = new ArrayList<>();
    // Liste des agents utilisateur connectés
    private Map<String, AID> agentsUtilisateur = new HashMap<>();

    @Override
    protected void setup() {
        // Charger tous les produits au démarrage
        catalogue = ProduitDAO.getTousLesProduits();
        System.out.println("Agent Catalogue prêt (base SQL connectée)");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    // Enregistrer l'agent utilisateur s'il n'est pas déjà connu
                    String nomAgent = msg.getSender().getLocalName();
                    if (!agentsUtilisateur.containsKey(nomAgent) && nomAgent.startsWith("utilisateur")) {
                        agentsUtilisateur.put(nomAgent, msg.getSender());
                        System.out.println("Nouvel agent utilisateur enregistré: " + nomAgent);
                    }

                    String contenu = msg.getContent();
                    int performative = msg.getPerformative();

                    // Traiter les demandes de recherche (REQUEST)
                    if (performative == ACLMessage.REQUEST) {
                        if (contenu.startsWith("REDUIRE_STOCK:")) {
                            traiterReductionStock(msg);
                        }
                        else if (contenu.startsWith("RETOUR_STOCK:")) {
                            traiterAjoutStock(msg);
                        }
                        else {
                            traiterRechercheProduits(msg, contenu);
                        }
                    }

                } else {
                    block();
                }
            }

            // Méthode pour traiter les demandes de recherche de produits
            private void traiterRechercheProduits(ACLMessage msg, String recherche) {
                List<Produit> produits;
                if (recherche.isEmpty() || recherche.equals("AFFICHER_CATALOGUE")) {
                    produits = catalogue;
                } else {
                    produits = ProduitDAO.chercherProduitsParNom(recherche);
                }

                StringBuilder sb = new StringBuilder();
                if (produits.isEmpty()) {
                    sb.append("Aucun produit trouvé.");
                } else {
                    for (Produit p : produits) {
                        sb.append(p.toString()).append("\n---\n");
                    }
                }

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(sb.toString());
                send(reply);
            }

            // Méthode pour traiter les demandes de réduction de stock
            private void traiterReductionStock(ACLMessage msg) {
                String contenu = msg.getContent();
                String[] info = contenu.substring(14).split("\\|");
                if (info.length >= 2) {
                    String nomProduit = info[0];
                    int quantite = Integer.parseInt(info[1]);

                    // Chercher le produit dans le catalogue et réduire son stock
                    boolean stockReduit = false;
                    for (Produit produit : catalogue) {
                        if (produit.getNom().equals(nomProduit)) {
                            if (produit.getStock() >= quantite) {
                                int nouveauStock = produit.getStock() - quantite;
                                produit.setStock(nouveauStock);
                                stockReduit = true;

                                // Mise à jour dans la base de données
                                ProduitDAO.mettreAJourStock(nomProduit, nouveauStock);
                            }
                            break;
                        }
                    }

                    // Informer l'expéditeur du résultat
                    ACLMessage reponse = new ACLMessage(ACLMessage.INFORM);
                    reponse.addReceiver(msg.getSender());
                    reponse.setContent(stockReduit ?
                            "STOCK_REDUIT:" + nomProduit :
                            "STOCK_INSUFFISANT:" + nomProduit);
                    send(reponse);

                    // Notifier tous les agents utilisateurs du changement de stock
                    if (stockReduit) {
                        notifierChangementStock(nomProduit);
                    }
                }
            }

            // Méthode pour traiter les demandes d'ajout au stock
            private void traiterAjoutStock(ACLMessage msg) {
                String contenu = msg.getContent();
                String[] info = contenu.substring(13).split("\\|");
                if (info.length >= 2) {
                    String nomProduit = info[0];
                    int quantite = Integer.parseInt(info[1]);

                    // Chercher le produit dans le catalogue et ajouter au stock
                    for (Produit produit : catalogue) {
                        if (produit.getNom().equals(nomProduit)) {
                            int nouveauStock = produit.getStock() + quantite;
                            produit.setStock(nouveauStock);

                            // Mise à jour dans la base de données
                            ProduitDAO.mettreAJourStock(nomProduit, nouveauStock);

                            // Notifier les agents utilisateur du changement de stock
                            notifierChangementStock(nomProduit);
                            break;
                        }
                    }


                }
            }
        });
    }

    // Méthode pour notifier tous les agents utilisateur du changement de stock
    private void notifierChangementStock(String nomProduit) {
        // Trouver le produit mis à jour
        Produit produitMisAJour = null;
        for (Produit p : catalogue) {
            if (p.getNom().equals(nomProduit)) {
                produitMisAJour = p;
                break;
            }
        }

        if (produitMisAJour != null) {
            ACLMessage notification = new ACLMessage(ACLMessage.INFORM);
            // Ajouter tous les agents utilisateur comme destinataires
            for (AID utilisateur : agentsUtilisateur.values()) {
                notification.addReceiver(utilisateur);
            }
            notification.setContent("MAJ_PRODUIT:" + produitMisAJour.toString());
            send(notification);
            System.out.println("Notification envoyée aux utilisateurs pour le produit: " + nomProduit);
        }
    }

    // Méthode pour obtenir la liste des agents utilisateur
    private List<AID> getAgentsUtilisateur() {
        return new ArrayList<>(agentsUtilisateur.values());
    }
}