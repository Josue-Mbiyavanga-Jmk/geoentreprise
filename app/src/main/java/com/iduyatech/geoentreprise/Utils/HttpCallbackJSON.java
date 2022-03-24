package com.iduyatech.geoentreprise.Utils;

import org.json.JSONObject;

/**
 * Created by AdbY on 28/05/2018.
 */

public interface HttpCallbackJSON {
    void onSuccess(JSONObject response);
    void onError(String message);
}
