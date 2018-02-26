package com.omi.socketiochat.repository.dp;



import com.omi.socketiochat.objects.Message;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;

public class InfoLocalStorageImpl implements InfoLocalStorage {


    public InfoLocalStorageImpl() {

    }

    @Override
    public Maybe<List<Message>> getInfo() {
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
    public Observable<List<Message>> saveInfo(final List<Message> messages) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(messages);
                }
            });
        }finally {
            realm.close();
        }
        return Observable.just(messages);
    }
}
