package com.omi.socketiochat.repository.dp;



import com.omi.socketiochat.objects.Message;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;

public class ChatLocalStorageImpl implements ChatLocalStorage {


    public ChatLocalStorageImpl() {

    }

    @Override
    public Maybe<List<Message>> getMessages() {
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<Message> dataObjects = realm.where(Message.class).findAll();
            if(dataObjects == null){
                return Maybe.empty();
            }else {
                return Maybe.just(realm.copyFromRealm(dataObjects));
            }
        }finally {
            realm.close();
        }
    }

    @Override
    public Observable<Message> saveMessage(final Message message) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(message);
                }
            });
        }finally {
            realm.close();
        }
        return Observable.just(message);
    }
}
