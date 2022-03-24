package com.iduyatech.geoentreprise.Entites;

public class EHelper {
    //classes utilitaires internes
    public static class EntrepriseCategorie{
        private String ref_categorie;
        private long date_create;

        public EntrepriseCategorie() {
        }

        public String getRef_categorie() {
            return ref_categorie;
        }

        public void setRef_categorie(String ref_categorie) {
            this.ref_categorie = ref_categorie;
        }

        public long getDate_create() {
            return date_create;
        }

        public void setDate_create(long date_create) {
            this.date_create = date_create;
        }
    }

    public static class Gps{
        private double lat;
        private double lgt;

        public Gps() {
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLgt() {
            return lgt;
        }

        public void setLgt(double lgt) {
            this.lgt = lgt;
        }
    }

    public static class EntiteAdministrative {
        private String name;
        private String description;

        public EntiteAdministrative() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class ProduitStation {
        private String ref_produit;
        private long date_create;
        private String create_uid;
        private int statut; //si c'est 0, ce n'est plus actif.

        public ProduitStation() {
        }

        public String getRef_produit() {
            return ref_produit;
        }

        public void setRef_produit(String ref_produit) {
            this.ref_produit = ref_produit;
        }

        public long getDate_create() {
            return date_create;
        }

        public void setDate_create(long date_create) {
            this.date_create = date_create;
        }

        public String getCreate_uid() {
            return create_uid;
        }

        public void setCreate_uid(String create_uid) {
            this.create_uid = create_uid;
        }

        public int getStatut() {
            return statut;
        }

        public void setStatut(int statut) {
            this.statut = statut;
        }
    }
}
