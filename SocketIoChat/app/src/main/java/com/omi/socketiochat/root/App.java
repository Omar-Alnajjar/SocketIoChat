package com.omi.socketiochat.root;

import android.app.Application;

import com.omi.socketiochat.main_activity.MainModule;
import com.omi.socketiochat.main_activity.utils.Constants;
import com.omi.socketiochat.repository.dp.InfoLocalModule;
import com.omi.socketiochat.repository.http.ChatSocketModule;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;


public class App extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private ApplicationComponent component;
    private static final int DATABASE_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        setupRealm();

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .chatSocketModule(new ChatSocketModule())
                .infoLocalModule(new InfoLocalModule())
                .mainModule(new MainModule())
                .build();

    }

    public Socket getSocket() {
        return mSocket;
    }

    private void setupRealm() {
        Realm.init(this);

        new RealmConfiguration.Builder()
                .schemaVersion(DATABASE_VERSION)
                .name("info.realm")
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
