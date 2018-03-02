package com.omi.socketiochat.repository.dp;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatLocalModule {


    @Singleton
    @Provides
    ChatLocalStorage provideChatDataSource() {
        return new ChatLocalStorageImpl();
    }

}
