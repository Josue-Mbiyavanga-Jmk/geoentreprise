package com.iduyatech.geoentreprise.Utils;

import org.json.JSONObject;

public interface ConnexionCallback {

    void errorJson(JSONObject errorResponsejson);
    void onError(String errorResponse);
}
