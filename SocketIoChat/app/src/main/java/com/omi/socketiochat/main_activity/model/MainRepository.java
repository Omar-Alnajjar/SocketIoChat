package com.omi.socketiochat.main_activity.model;



import com.omi.socketiochat.objects.Message;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface MainRepository {

    Maybe<List<Message>> getResultsFromMemory(String last);

    Observable<List<Message>> getResults(String lastId);


    Observable<Message> saveMessageToDB(Message message);


    Completable connectToServer(String mUsername);
    Completable disconnectFromServer();
    Observable<Message> newMessage(Message message);
    Observable<Message> uploadImage(Message message);
    Completable typing(Message message);
    Completable stopTyping(Message message);




    Observable<Message> newMessageCallback();
    Observable<Message> typingCallback();
    Observable<Message> stopTypingCallback();
    Observable<Message> userJoinedCallback();
    Observable<Message> userLeftCallback();
    Observable<Message> newMessageImageCallback();

    boolean isConnected();

}
