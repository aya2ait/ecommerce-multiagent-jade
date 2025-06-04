package models;

public class Produit {
    private String nom;
    private String categorie;
    private String couleurs;


    private double prix;
    private String taille ;

    private String imageUrl;
    private int stock;



    // Constructeur
    public Produit(String nom,String categorie,String couleurs, double prix,String taille, String imageUrl, int stock) {
        this.nom = nom;
        this.categorie = categorie;
        this.couleurs = couleurs;
        this.prix = prix;
        this.taille = taille;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    // Getters
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;

    }


    public double getPrix() {
        return prix;
    }


    public String getCategorie() {
        return categorie;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStock() {
        return stock;
    }

    // Setter pour le stock (nécessaire pour la modification)
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setTaille(String taille) {
        this.taille = taille;
    }
    public String getTaille() {
        return taille;
    }

    // Méthode pour réduire le stock
    public boolean reduireStock(int quantite) {
        if (stock >= quantite) {
            stock -= quantite;
            return true;
        }
        return false;  // Retourne false si le stock est insuffisant
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nom: ").append(nom).append("\n");
        sb.append("Catégorie: ").append(categorie).append("\n");
        sb.append("Couleurs: ").append(couleurs).append("\n");
        sb.append("Prix: ").append(String.format("%.2f", prix)).append(" MAD\n");
        sb.append("Taille: ").append(taille).append("\n");
        sb.append("Image: ").append(imageUrl != null ? imageUrl : "").append("\n");
        sb.append("Stock: ").append(stock);
        if (stock > 0) {
            sb.append(" (Disponible)");
        } else {
            sb.append(" (Épuisé)");
        }
        sb.append("\n");
        return sb.toString();
    }
}