package gui;

import agents.AgentUtilisateur;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class InterfaceUtilisateur {
    private JFrame frame;

    private JTextField champRecherche;
    private JPanel panneauProduits;
    private JPanel panneauPanier;
    private JLabel totalLabel;
    private Map<String, ImageIcon> cacheImages = new HashMap<>();
    private Map<String, Double> prixProduits = new HashMap<>();
    private AgentUtilisateur agent;

    public InterfaceUtilisateur(AgentUtilisateur agent) {
        this.agent = agent;
        initialize();
    }
    // Dans la classe InterfaceUtilisateur, ajoutez cette méthode
    public void mettreAJourProduit(String infoProduit) {
        // Récupérer le nom du produit mis à jour
        String nomProduit = extraireInfo(infoProduit, "Nom: ");

        // Mettre à jour l'affichage des produits si nécessaire
        // Si vous avez déjà un panneau des produits, vous pouvez recréer les composants
        // ou mettre à jour le composant spécifique

        // Si vous avez une méthode pour afficher tous les produits, vous pouvez
        // demander à l'agent de renvoyer tous les produits
        agent.envoyerRecherche("");
    }
    private void initialize() {
        frame = new JFrame("Boutique E-Commerce");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.getContentPane().setBackground(Color.WHITE);

        // Police générale
        Font fontGenerale = new Font("Segoe UI", Font.PLAIN, 14);

        // Panel du haut avec la recherche
        champRecherche = new JTextField(30);
        champRecherche.setFont(fontGenerale);

        JButton boutonRecherche = new JButton("Rechercher");
        boutonRecherche.setFont(fontGenerale);
        boutonRecherche.setBackground(new Color(70, 130, 180));
        boutonRecherche.setForeground(Color.WHITE);
        boutonRecherche.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.add(new JLabel("Recherche : "));
        topPanel.add(champRecherche);
        topPanel.add(boutonRecherche);

        // Panel principal divisé en deux
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);

        // Panel des produits
        panneauProduits = new JPanel();
        panneauProduits.setLayout(new BoxLayout(panneauProduits, BoxLayout.Y_AXIS));
        panneauProduits.setBackground(Color.WHITE);
        JScrollPane scrollProduits = new JScrollPane(panneauProduits);
        scrollProduits.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel du panier
        JPanel panierContainer = new JPanel(new BorderLayout());
        panierContainer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Votre Panier"));
        panierContainer.setBackground(Color.WHITE);

        panneauPanier = new JPanel();
        panneauPanier.setLayout(new BoxLayout(panneauPanier, BoxLayout.Y_AXIS));
        panneauPanier.setBackground(Color.WHITE);
        JScrollPane scrollPanier = new JScrollPane(panneauPanier);
        scrollPanier.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel footer du panier
        JPanel panierFooter = new JPanel(new BorderLayout());
        panierFooter.setBackground(Color.WHITE);

        totalLabel = new JLabel("Total: 0.00 MAD", JLabel.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//        JButton viderPanierBtn = new JButton("Vider le panier");
//        viderPanierBtn.setFont(fontGenerale);
//        viderPanierBtn.setBackground(new Color(220, 20, 60));
//        viderPanierBtn.setForeground(Color.WHITE);
//        viderPanierBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        viderPanierBtn.addActionListener(e -> agent.viderPanier());

        JButton commanderBtn = new JButton("Passer la commande");
        commanderBtn.setFont(fontGenerale);
        commanderBtn.setBackground(new Color(34, 139, 34));
        commanderBtn.setForeground(Color.WHITE);
        commanderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        commanderBtn.addActionListener(e -> {
            // Première action : Passer la commande
            agent.passerCommande();


        });




        JPanel boutonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        boutonsPanel.setBackground(Color.WHITE);
//        boutonsPanel.add(viderPanierBtn);
        boutonsPanel.add(commanderBtn);

        panierFooter.add(totalLabel, BorderLayout.NORTH);
        panierFooter.add(boutonsPanel, BorderLayout.SOUTH);

        panierContainer.add(scrollPanier, BorderLayout.CENTER);
        panierContainer.add(panierFooter, BorderLayout.SOUTH);

        splitPane.setLeftComponent(scrollProduits);
        splitPane.setRightComponent(panierContainer);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);

        boutonRecherche.addActionListener(e -> agent.envoyerRecherche(champRecherche.getText()));

        JLabel labelChargement = new JLabel("Chargement des produits...", JLabel.CENTER);
        labelChargement.setFont(new Font("Arial", Font.ITALIC, 14));
        panneauProduits.add(labelChargement);

        frame.setVisible(true);
    }


    public void afficherProduits(String donnees) {
        SwingUtilities.invokeLater(() -> {
            panneauProduits.removeAll();
            prixProduits.clear();

            Font fontTexte = new Font("Segoe UI", Font.PLAIN, 13);
            Color fondCarte = new Color(250, 250, 250);
            Color vertBouton = new Color(30, 173, 177);

            String[] blocs = donnees.split("---\\n");
            for (String bloc : blocs) {
                if (bloc.trim().isEmpty()) continue;

                JPanel carte = new JPanel();
                carte.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                carte.setBackground(fondCarte);
                carte.setLayout(new BorderLayout(10, 5));
                carte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

                String nom = extraireInfo(bloc, "Nom: ");
                String categorie = extraireInfo(bloc, "Catégorie: ");
                String couleurs = extraireInfo(bloc, "Couleurs: ");
                String prixStr = extraireInfo(bloc, "Prix: ").replace(" MAD", "").replace(",", ".");
                String taille = extraireInfo(bloc, "Taille: ");
                String imageUrl = extraireInfo(bloc, "Image: ");
                String stock = extraireInfo(bloc, "Stock: ");

                double prix = 0.0;
                try {
                    prix = Double.parseDouble(prixStr);
                    prixProduits.put(nom, prix);
                } catch (NumberFormatException e) {
                    System.out.println("Erreur de conversion du prix: " + prixStr);
                }

                JPanel imagePanel = new JPanel(new BorderLayout());
                imagePanel.setPreferredSize(new Dimension(150, 150));
                imagePanel.setBackground(fondCarte);

                JLabel imageLabel = new JLabel("Chargement...", JLabel.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
                chargerImageAsync(imageUrl, imageLabel);

                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(fondCarte);

                JLabel titreLabel = new JLabel(nom);
                titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                titreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel prixLabel = new JLabel(String.format("%.2f MAD", prix));
                prixLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                prixLabel.setForeground(Color.RED);
                prixLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JTextArea detailsArea = new JTextArea();
                detailsArea.setEditable(false);
                detailsArea.setLineWrap(true);
                detailsArea.setWrapStyleWord(true);
                detailsArea.setBackground(fondCarte);
                detailsArea.setFont(fontTexte);
                detailsArea.setText("Catégorie: " + categorie + "\n" +
                        "Taille: " + taille + "\n" +
                        "Couleurs: " + couleurs + "\n" +
                        "Prix: " + prix + "\n" +
                        "Disponibilité: " + stock);
                detailsArea.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailsArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JButton boutonAjouter = new JButton("Ajouter au panier");
                boutonAjouter.setAlignmentX(Component.LEFT_ALIGNMENT);
                boutonAjouter.setFont(fontTexte);
                boutonAjouter.setBackground(vertBouton);
                boutonAjouter.setForeground(Color.WHITE);
                boutonAjouter.setCursor(new Cursor(Cursor.HAND_CURSOR));
                boutonAjouter.setFocusPainted(false);

                boolean enStock = !stock.contains("Épuisé");
                boutonAjouter.setEnabled(enStock);

                final String nomFinal = nom;
                final double prixFinal = prix;
                boutonAjouter.addActionListener(e -> agent.ajouterAuPanier(nomFinal, prixFinal));

                infoPanel.add(titreLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                infoPanel.add(prixLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                infoPanel.add(detailsArea);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                infoPanel.add(boutonAjouter);

                carte.add(imagePanel, BorderLayout.WEST);
                carte.add(infoPanel, BorderLayout.CENTER);

                panneauProduits.add(carte);
                panneauProduits.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (blocs.length <= 1 && (blocs[0].trim().isEmpty() || blocs[0].contains("Aucun produit trouvé"))) {
                JLabel noProductLabel = new JLabel("Aucun produit ne correspond à votre recherche", JLabel.CENTER);
                noProductLabel.setFont(new Font("Arial", Font.BOLD, 14));
                panneauProduits.add(noProductLabel);
            }

            panneauProduits.revalidate();
            panneauProduits.repaint();
        });
    }


    public void mettreAJourAffichagePanier(String contenuPanier) {
        SwingUtilities.invokeLater(() -> {
            panneauPanier.removeAll();

            Font fontTexte = new Font("Segoe UI", Font.PLAIN, 13);
            Font fontPetitGras = new Font("Segoe UI", Font.BOLD, 12);
            Color fondItem = new Color(245, 245, 245);

            if (contenuPanier.equals("TOTAL|0.00")) {
                JLabel panierVide = new JLabel("Votre panier est vide", JLabel.CENTER);
                panierVide.setFont(new Font("Arial", Font.ITALIC, 14));
                panierVide.setAlignmentX(Component.CENTER_ALIGNMENT);
                panneauPanier.add(panierVide);
                totalLabel.setText("Total: 0.00 MAD");
            } else {
                String[] elements = contenuPanier.split(";");
                double total = 0.0;

                for (int i = 0; i < elements.length - 1; i++) {
                    String element = elements[i];
                    String[] infos = element.split("\\|");

                    if (infos.length >= 3) {
                        String nom = infos[0];
                        double prix = Double.parseDouble(infos[1]);
                        int quantite = Integer.parseInt(infos[2]);
                        double prixTotal = prix * quantite;

                        JPanel itemPanel = new JPanel();
                        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
                        itemPanel.setBackground(fondItem);
                        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                                BorderFactory.createEmptyBorder(5, 10, 5, 10)
                        ));
                        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                        JLabel nomLabel = new JLabel(nom + " x" + quantite);
                        nomLabel.setFont(fontTexte);

                        JLabel prixLabel = new JLabel(String.format("%.2f MAD", prixTotal));
                        prixLabel.setFont(fontPetitGras);

                        JButton supprimerBtn = new JButton("\uD83D\uDDD1 ");
                        supprimerBtn.setFont(fontPetitGras);

                        supprimerBtn.setForeground(Color.RED);
                        supprimerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        supprimerBtn.setFocusPainted(false);
                        supprimerBtn.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                        supprimerBtn.setContentAreaFilled(false);

                        final String nomFinal = nom;
                        supprimerBtn.addActionListener(e -> agent.supprimerDuPanier(nomFinal));

                        itemPanel.add(nomLabel);
                        itemPanel.add(Box.createHorizontalGlue());
                        itemPanel.add(prixLabel);
                        itemPanel.add(Box.createRigidArea(new Dimension(5, 0)));
                        itemPanel.add(supprimerBtn);

                        panneauPanier.add(itemPanel);
                        panneauPanier.add(Box.createRigidArea(new Dimension(0, 5)));
                    }
                }

                // Mettre à jour le total
                String totalStr = elements[elements.length - 1].replace("TOTAL|", "").replace(",", ".");
                totalLabel.setText("Total: " + totalStr + " MAD");
            }

            panneauPanier.revalidate();
            panneauPanier.repaint();
        });
    }


    private void chargerImageAsync(String urlStr, JLabel label) {
        // Vérifier si l'URL est valide
        if (urlStr == null || urlStr.trim().isEmpty()) {
            label.setText("Pas d'image");
            return;
        }

        // Vérifier le cache
        if (cacheImages.containsKey(urlStr)) {
            label.setIcon(cacheImages.get(urlStr));
            label.setText("");
            return;
        }

        // Message de chargement
        label.setText("Chargement...");
        label.setIcon(null);

        // Worker pour charger l'image
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    URL url = new URL(urlStr);
                    ImageIcon icon = new ImageIcon(url);

                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE && icon.getIconWidth() > 0) {
                        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                        return new ImageIcon(img);
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        cacheImages.put(urlStr, icon);
                        label.setIcon(icon);
                        label.setText("");
                    } else {
                        label.setText("Image indisponible");
                    }
                } catch (Exception e) {
                    label.setText("Erreur");
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private String extraireInfo(String bloc, String prefixe) {
        String[] lignes = bloc.split("\n");
        for (String ligne : lignes) {
            if (ligne.startsWith(prefixe)) {
                String valeur = ligne.substring(prefixe.length()).trim();
                // Si c'est un prix, extraire seulement la partie numérique
                if (prefixe.equals("Prix: ")) {
                    // Extraire uniquement la partie numérique (avant l'espace)
                    int espaceIndex = valeur.indexOf(" ");
                    if (espaceIndex > 0) {
                        return valeur.substring(0, espaceIndex);
                    }
                }
                return valeur;
            }
        }
        return "";
    }

    private void afficherFormulairePaiement(double total) {
        JDialog dialog = new JDialog(frame, "Paiement sécurisé", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new GridLayout(7, 2, 10, 10)); // ligne en plus pour le total

        // Champs de paiement
        JTextField nomCarte = new JTextField();
        JTextField numeroCarte = new JTextField();
        JTextField expirationCarte = new JTextField();
        JTextField cvvCarte = new JTextField();

        dialog.add(new JLabel("Montant à payer :"));
        JLabel totalLabel = new JLabel(String.format("%.2f MAD", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(Color.BLUE);
        dialog.add(totalLabel);

        dialog.add(new JLabel("Nom sur la carte :"));
        dialog.add(nomCarte);
        dialog.add(new JLabel("Numéro de carte :"));
        dialog.add(numeroCarte);
        dialog.add(new JLabel("Date d'expiration (MM/AA) :"));
        dialog.add(expirationCarte);
        dialog.add(new JLabel("Code CVV :"));
        dialog.add(cvvCarte);

        JButton payerBtn = new JButton("Valider le paiement");
        payerBtn.setBackground(new Color(34, 139, 34));
        payerBtn.setForeground(Color.WHITE);

        JButton annulerBtn = new JButton("Annuler");
        annulerBtn.setBackground(Color.GRAY);
        annulerBtn.setForeground(Color.WHITE);

        dialog.add(annulerBtn);
        dialog.add(payerBtn);

        // Action des boutons
        payerBtn.addActionListener(e -> {
            String nom = nomCarte.getText().trim();
            String numero = numeroCarte.getText().trim();
            String expiration = expirationCarte.getText().trim();
            String cvv = cvvCarte.getText().trim();

            if (nom.isEmpty() || numero.isEmpty() || expiration.isEmpty() || cvv.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Envoie du montant à l'agent de paiement
            agent.informerPaiement(total); // Cette méthode est à créer dans AgentUtilisateur
            JOptionPane.showMessageDialog(dialog, "Paiement réussi ! Merci pour votre commande.");
            dialog.dispose();
            agent.viderPanier(); // tu peux réinitialiser le panier après paiement
        });

        annulerBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    public void afficherFormulairePaiementAvecReduction(double montantReduit) {
        JDialog paiementDialog = new JDialog(frame, "Paiement sécurisé", true);
        paiementDialog.setSize(400, 300);
        paiementDialog.setLocationRelativeTo(frame);
        paiementDialog.setLayout(new GridLayout(7, 2, 10, 10)); // ligne en plus pour le total

        // Champs de paiement
        JTextField nomCarte = new JTextField();
        JTextField numeroCarte = new JTextField();
        JTextField expirationCarte = new JTextField();
        JTextField cvvCarte = new JTextField();

        // Montant à payer
        paiementDialog.add(new JLabel("Total à payer après réduction :"));
        JLabel montantLabel = new JLabel(String.format("%.2f MAD", montantReduit));
        montantLabel.setFont(new Font("Arial", Font.BOLD, 14));
        montantLabel.setForeground(Color.BLUE);
        paiementDialog.add(montantLabel);

        // Champs pour les informations de carte
        paiementDialog.add(new JLabel("Nom sur la carte :"));
        paiementDialog.add(nomCarte);
        paiementDialog.add(new JLabel("Numéro de carte :"));
        paiementDialog.add(numeroCarte);
        paiementDialog.add(new JLabel("Date d'expiration (MM/AA) :"));
        paiementDialog.add(expirationCarte);
        paiementDialog.add(new JLabel("Code CVV :"));
        paiementDialog.add(cvvCarte);

        // Boutons
        JButton boutonPayer = new JButton("Confirmer Paiement");
        boutonPayer.setBackground(new Color(34, 139, 34));
        boutonPayer.setForeground(Color.WHITE);

        JButton annulerBtn = new JButton("Annuler");
        annulerBtn.setBackground(Color.GRAY);
        annulerBtn.setForeground(Color.WHITE);

        paiementDialog.add(annulerBtn);
        paiementDialog.add(boutonPayer);

        // Action des boutons
        boutonPayer.addActionListener(e -> {
            String nom = nomCarte.getText().trim();
            String numero = numeroCarte.getText().trim();
            String expiration = expirationCarte.getText().trim();
            String cvv = cvvCarte.getText().trim();

            if (nom.isEmpty() || numero.isEmpty() || expiration.isEmpty() || cvv.isEmpty()) {
                JOptionPane.showMessageDialog(paiementDialog, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Envoie du montant à l'agent de paiement

            JOptionPane.showMessageDialog(paiementDialog, "Paiement de " + montantReduit + "MAD effectué avec succès !");
            paiementDialog.dispose();
            agent.viderPanier();
        });

        annulerBtn.addActionListener(e -> paiementDialog.dispose());

        paiementDialog.setVisible(true);
    }

    public void afficherFormulairePaiement(double total, boolean avecReduction) {
        if (avecReduction) {
            afficherFormulairePaiementAvecReduction(total);
        } else {
            afficherFormulairePaiement(total);
        }
    }

}