package com.iduyatech.geoentreprise.Entites;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_categorie")
public class ECategorie {
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "_id")
    private String _id;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "create_uid")
    private String create_uid;
    @DatabaseField(columnName = "update_uid")
    private String update_uid;
    @DatabaseField(columnName = "date_create")
    private Long date_create;
    @DatabaseField(columnName = "date_update")
    private Long date_update;
   // @DatabaseField(columnName = "version")
    //private int __v;

    public ECategorie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_uid() {
        return create_uid;
    }

    public void setCreate_uid(String create_uid) {
        this.create_uid = create_uid;
    }

    public String getUpdate_uid() {
        return update_uid;
    }

    public void setUpdate_uid(String update_uid) {
        this.update_uid = update_uid;
    }

    public Long getDate_create() {
        return date_create;
    }

    public void setDate_create(Long date_create) {
        this.date_create = date_create;
    }

    public Long getDate_update() {
        return date_update;
    }

    public void setDate_update(Long date_update) {
        this.date_update = date_update;
    }
/*
    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }*/
}
