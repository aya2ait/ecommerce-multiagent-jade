package main;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        // Démarrer la plateforme JADE
        Runtime rt = Runtime.instance();

        // Créer le conteneur principal avec GUI
        ProfileImpl mainProfile = new ProfileImpl();
        mainProfile.setParameter(Profile.GUI, "true");

        System.out.println("Création du container principal...");
        AgentContainer mainContainer = rt.createMainContainer(mainProfile);

        try {
            // Créer un container pour les agents liés au catalogue
            ProfileImpl catalogueProfile = new ProfileImpl();
            catalogueProfile.setParameter(Profile.MAIN_HOST, "localhost");
            catalogueProfile.setParameter(Profile.CONTAINER_NAME, "Catalogue-Container");
            System.out.println("Création du container pour le catalogue...");
            AgentContainer catalogueContainer = rt.createAgentContainer(catalogueProfile);

            // Créer un container pour les agents utilisateurs
            ProfileImpl userProfile = new ProfileImpl();
            userProfile.setParameter(Profile.MAIN_HOST, "localhost");
            userProfile.setParameter(Profile.CONTAINER_NAME, "User-Container");
            System.out.println("Création du container pour l'utilisateur...");
            AgentContainer userContainer = rt.createAgentContainer(userProfile);

            // Créer un container pour les agents du panier et du paiement
            ProfileImpl tradeProfile = new ProfileImpl();
            tradeProfile.setParameter(Profile.MAIN_HOST, "localhost");
            tradeProfile.setParameter(Profile.CONTAINER_NAME, "Trade-Container");
            System.out.println("Création du container pour transactions...");
            AgentContainer tradeContainer = rt.createAgentContainer(tradeProfile);

            // Attendre que les containers soient bien créés
            Thread.sleep(1000);

            // Créer les agents dans leurs containers respectifs
            AgentController agentCatalogue = catalogueContainer.createNewAgent("catalogue", "agents.AgentCatalogue", null);
            AgentController agentReduction = catalogueContainer.createNewAgent("reduction", "agents.AgentReduction", null);

            AgentController agentUtilisateur = userContainer.createNewAgent("utilisateur", "agents.AgentUtilisateur", null);

            AgentController agentPanier = tradeContainer.createNewAgent("panier", "agents.AgentPanier", null);
            AgentController agentPaiement = tradeContainer.createNewAgent("paiement", "agents.AgentPaiement", null);

            // Attendre que les agents soient bien créés
            Thread.sleep(1000);

            // Créer et démarrer le sniffer dans le container principal pour surveiller tous les agents
            AgentController sniffer = mainContainer.createNewAgent(
                    "sniffer",
                    "jade.tools.sniffer.Sniffer",
                    new Object[]{"utilisateur;catalogue;panier;paiement;reduction"}
            );
            sniffer.start();

            // Attendre que le sniffer démarre complètement
            Thread.sleep(2000);

            // Démarrer les agents
            System.out.println("Démarrage des agents...");
            agentCatalogue.start();
            agentReduction.start();
            agentUtilisateur.start();
            agentPanier.start();
            agentPaiement.start();

            System.out.println("Tous les agents ont été démarrés avec succès sur leurs containers respectifs");

        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}