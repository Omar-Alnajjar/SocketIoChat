package com.omi.socketiochat.main_activity.utils;

import org.json.JSONException;

public interface UploadFileMoreDataReqListener {

	void uploadChunck(int place, int percent);

	void err(JSONException e);

}
