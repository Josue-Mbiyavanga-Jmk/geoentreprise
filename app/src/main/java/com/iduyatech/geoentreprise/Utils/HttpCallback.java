package com.iduyatech.geoentreprise.Utils;

import org.json.JSONObject;

/**
 * Created by Kevin Lukanga on 28/05/2018.
 */

public interface HttpCallback {
    void onSuccess(JSONObject response);
    void onError(String message);
}
