package me.elmira.simpletwitterclient;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import me.elmira.simpletwitterclient.model.source.TwitterRepository;
import me.elmira.simpletwitterclient.model.source.local.TwitterLocalDataSource;
import me.elmira.simpletwitterclient.model.source.remote.TwitterClient;
import me.elmira.simpletwitterclient.model.source.remote.TwitterRemoteDataSource;

/**
 * Created by elmira on 3/22/17.
 */

public class TwitterClientApp extends Application {

    private static Context context;
    private static TwitterRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        TwitterClientApp.context = this;

        repository = TwitterRepository.getInstance(TwitterLocalDataSource.getInstance(getContentResolver()),
                TwitterRemoteDataSource.getInstance(this.getApplicationContext()));
    }

    public TwitterClient getTwitterClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterClientApp.context);
    }

    public static TwitterRepository getRepository() {
        return repository;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (repository != null) {
            repository.destroy();
        }
    }
}