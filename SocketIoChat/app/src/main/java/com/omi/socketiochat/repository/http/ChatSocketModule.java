package com.omi.socketiochat.repository.http;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ChatSocketModule {


    @Provides
    @Singleton
    public ChatSocketService provideApiService(Context context) {
        return new ChatSocketServiceImpl(context);
    }

}
