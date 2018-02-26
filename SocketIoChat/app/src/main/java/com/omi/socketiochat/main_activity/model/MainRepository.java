package com.omi.socketiochat.main_activity.model;



import com.omi.socketiochat.objects.Message;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface MainRepository {

    Maybe<List<Message>> getResultsFromMemory(String last);

    Maybe<List<Message>> getResultsFromNetwork(String lastId);

    Observable<List<Message>> getResults(String lastId);


    Observable<List<Message>> saveInfoToDB(List<Message> messages);


    Completable connectToServer(String mUsername);
    Completable disconnectFromServer();
    Completable newMessage(Message message);
    Completable typing(Message message);
    Completable stopTyping(Message message);




    Observable<Message> newMessageCallback();
    Observable<Message> typingCallback();
    Observable<Message> stopTypingCallback();
    Observable<Message> userJoinedCallback();
    Observable<Message> userLeftCallback();

    boolean isConnected();
}
