package com.omi.socketiochat.root;



import com.omi.socketiochat.main_activity.MainModule;
import com.omi.socketiochat.main_activity.view.MainFragment;
import com.omi.socketiochat.repository.dp.ChatLocalModule;
import com.omi.socketiochat.repository.http.ChatSocketModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, ChatSocketModule.class ,ChatLocalModule.class ,MainModule.class})
public interface ApplicationComponent {

    void inject(MainFragment target);

}
