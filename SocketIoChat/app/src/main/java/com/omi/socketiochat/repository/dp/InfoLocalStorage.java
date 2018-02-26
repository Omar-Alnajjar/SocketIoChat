package com.omi.socketiochat.repository.dp;



import com.omi.socketiochat.objects.Message;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface InfoLocalStorage {

    Maybe<List<Message>> getInfo();
    Observable<Message> saveMessage(Message message);

}
