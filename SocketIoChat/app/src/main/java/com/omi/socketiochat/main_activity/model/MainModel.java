package com.omi.socketiochat.main_activity.model;



import com.omi.socketiochat.main_activity.MainActivityMVP;
import com.omi.socketiochat.objects.Message;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class MainModel implements MainActivityMVP.Model {

    private MainRepository repository;

    public MainModel(MainRepository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<List<Message>> loadData(String lastId) {
        return repository.getResults(lastId);
    }

    @Override
    public Observable<List<Message>> saveData(List<Message> messages) {
        return repository.saveInfoToDB(messages);
    }

    @Override
    public Completable connectToServer(String mUsername) {
        return repository.connectToServer(mUsername);
    }

    @Override
    public void disconnectFromServer() {
        repository.disconnectFromServer();
    }

    @Override
    public void newMessage(Message message) {
        repository.newMessage(message);
    }

    @Override
    public void typing(Message message) {
        repository.typing(message);
    }

    @Override
    public void stopTyping(Message message) {
        repository.stopTyping(message);
    }



    @Override
    public Observable<Message> newMessageCallback() {
        return repository.newMessageCallback();
    }

    @Override
    public Observable<Message> typingCallback() {
        return repository.typingCallback();
    }

    @Override
    public Observable<Message> stopTypingCallback() {
        return repository.stopTypingCallback();
    }

    @Override
    public Observable<Message> userJoinedCallback() {
        return repository.userJoinedCallback();
    }

    @Override
    public Observable<Message> userLeftCallback() {
        return repository.userLeftCallback();
    }


    @Override
    public boolean isConnected() {
        return repository.isConnected();
    }
}
