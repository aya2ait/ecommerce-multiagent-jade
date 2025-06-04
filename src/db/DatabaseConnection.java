package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Paramètres de connexion (à adapter)
    private static final String URL = "jdbc:mysql://localhost:3306/ecomerce_sma";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Méthode pour obtenir une connexion
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Testez la connexion (optionnel)
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");
        } catch (SQLException e) {
            System.err.println("Échec de la connexion : " + e.getMessage());
        }
    }
}