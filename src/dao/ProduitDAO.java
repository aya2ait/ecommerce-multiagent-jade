package dao;

import db.DatabaseConnection;
import models.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    // Rechercher les produits par nom
    public static List<Produit> chercherProduitsParNom(String motCle) {
        List<Produit> produits = new ArrayList<>();

        String sql = "SELECT * FROM produit WHERE nom LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + motCle + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getString("nom"),
                        rs.getString("categorie"),
                        rs.getString("couleurs"),

                        rs.getDouble("prix"),
                        rs.getString("taille"),

                        rs.getString("image_url"),
                        rs.getInt("stock")

                        );
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produits;
    }

    // Méthode pour récupérer tous les produits
    public static List<Produit> getTousLesProduits() {
        return chercherProduitsParNom(""); // vide = tous les produits
    }

    // Méthode pour mettre à jour le stock d'un produit
    public static boolean mettreAJourStock(String nomProduit, int nouveauStock) {
        String sql = "UPDATE produit SET stock = ? WHERE nom = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nouveauStock);
            stmt.setString(2, nomProduit);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}