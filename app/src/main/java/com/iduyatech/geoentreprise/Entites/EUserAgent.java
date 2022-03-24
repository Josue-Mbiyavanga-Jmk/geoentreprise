package com.iduyatech.geoentreprise.Entites;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_user_agent")
public class EUserAgent {
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "pseudo")
    private String pseudo;
    @DatabaseField(columnName = "password")
    private String password;
    @DatabaseField(columnName = "profil")
    private String profil;
    @DatabaseField(columnName = "date_update")
    private long date_update;

    public EUserAgent() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfil() {
        return profil;
    }

    public void setProfil(String profil) {
        this.profil = profil;
    }

    public long getDate_update() {
        return date_update;
    }

    public void setDate_update(long date_update) {
        this.date_update = date_update;
    }
}
