package com.omi.socketiochat.main_activity;

import android.content.Context;

import com.omi.socketiochat.main_activity.model.MainModel;
import com.omi.socketiochat.main_activity.model.MainRepository;
import com.omi.socketiochat.main_activity.model.MainRepositoryImpl;
import com.omi.socketiochat.main_activity.presenter.MainPresenter;
import com.omi.socketiochat.repository.dp.InfoLocalStorage;
import com.omi.socketiochat.repository.http.ChatSocketService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    @Provides
    public MainActivityMVP.Presenter provideMainActivityPresenter(MainActivityMVP.Model MainModel, Context context) {
        return new MainPresenter(MainModel, context);
    }

    @Provides
    public MainActivityMVP.Model provideMainActivityModel(MainRepository repository) {
        return new MainModel(repository);
    }

    @Singleton
    @Provides
    public MainRepository provideRepo(ChatSocketService chatSocketService, InfoLocalStorage infoLocalStorage) {
        return new MainRepositoryImpl(chatSocketService, infoLocalStorage);
    }


}
