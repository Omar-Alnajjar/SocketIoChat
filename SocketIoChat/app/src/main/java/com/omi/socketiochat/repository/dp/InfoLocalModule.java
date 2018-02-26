package com.omi.socketiochat.repository.dp;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InfoLocalModule {


    @Singleton
    @Provides
    InfoLocalStorage provideInfoDataSource() {
        return new InfoLocalStorageImpl();
    }

}
