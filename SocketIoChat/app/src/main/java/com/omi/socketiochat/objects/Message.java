package com.omi.socketiochat.objects;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Message  extends RealmObject {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    public static final int STATUS_SENT = 0;
    public static final int STATUS_RECEIVED = 2;

    @PrimaryKey
    private String mId;
    private int mType;
    private String mMessage;
    private String mUsername;
    private int mStatus;

    public Message() {}

    public int getType() {
        return mType;
    };

    public String getmId() {
        return mId;
    }

    public String getMessage() {
        return mMessage;
    };

    public String getUsername() {
        return mUsername;
    };

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public static class Builder {
        private final int mType;
        private String mId;
        private String mUsername;
        private String mMessage;
        private int mStatus;

        public Builder(int type) {
            mType = type;
        }

        public Builder id(String id) {
            mId = id;
            return this;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder status(int status) {
            mStatus = status;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mId = mId;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            return message;
        }
    }


    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            if(mId != null)
            jsonObject.put("id", mId);

            jsonObject.put("type", mType);
            if(mUsername != null)
            jsonObject.put("username", mUsername);
            if(mMessage != null)
            jsonObject.put("message", mMessage);

            jsonObject.put("status", mStatus);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
