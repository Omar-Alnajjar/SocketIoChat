package com.omi.socketiochat.main_activity.model;



import com.omi.socketiochat.objects.Message;
import com.omi.socketiochat.repository.dp.InfoLocalStorage;
import com.omi.socketiochat.repository.http.ChatSocketService;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public class MainRepositoryImpl implements MainRepository {

    private ChatSocketService chatSocketService;
    private InfoLocalStorage infoLocalStorage;

    public MainRepositoryImpl(ChatSocketService chatSocketService, InfoLocalStorage infoLocalStorage) {
        this.chatSocketService = chatSocketService;
        this.infoLocalStorage = infoLocalStorage;
    }


    @Override
    public Maybe<List<Message>> getResultsFromMemory(String lastId) {
        return infoLocalStorage.getInfo();
    }

    @Override
    public Maybe<List<Message>> getResultsFromNetwork(String lastId) {
        return Maybe.empty();
    }

    @Override
    public Observable<List<Message>> getResults(String lastId) {
        return getResultsFromMemory(lastId).toObservable();
    }

    @Override
    public Observable<Message> saveMessageToDB(Message message) {
        return infoLocalStorage.saveMessage(message);
    }

    @Override
    public Completable connectToServer(String mUsername) {
        return chatSocketService.connectToServer(mUsername);
    }

    @Override
    public Completable disconnectFromServer() {
        return chatSocketService.disconnectFromServer();
    }

    @Override
    public Observable<Message> newMessage(Message message) {
        return chatSocketService.newMessage(message);
    }

    @Override
    public Observable<Message> uploadImage(Message message) {
        return chatSocketService.uploadImage(message);
    }

    @Override
    public Completable typing(Message message) {
        return chatSocketService.typing(message);
    }

    @Override
    public Completable stopTyping(Message message) {
        return chatSocketService.stopTyping(message);
    }

    @Override
    public Observable<Message> newMessageCallback() {
        return chatSocketService.newMessageCallback();
    }

    @Override
    public Observable<Message> typingCallback() {
        return chatSocketService.typingCallback();
    }

    @Override
    public Observable<Message> stopTypingCallback() {
        return chatSocketService.stopTypingCallback();
    }

    @Override
    public Observable<Message> userJoinedCallback() {
        return chatSocketService.userJoinedCallback();
    }

    @Override
    public Observable<Message> userLeftCallback() {
        return chatSocketService.userLeftCallback();
    }

    @Override
    public Observable<Message> newMessageImageCallback() {
        return chatSocketService.newMessageImageCallback();
    }

    @Override
    public boolean isConnected() {
        return chatSocketService.isConnected();
    }
}
