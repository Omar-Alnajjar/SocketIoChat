package com.omi.socketiochat.main_activity.presenter;



import android.content.Context;
import android.os.Environment;

import com.omi.socketiochat.compresser.Compressor;
import com.omi.socketiochat.main_activity.MainActivityMVP;
import com.omi.socketiochat.objects.Message;

import org.reactivestreams.Subscription;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainActivityMVP.Presenter {

    private MainActivityMVP.View view;
    private Subscription subscription = null;
    private MainActivityMVP.Model model;
    private CompositeDisposable disposables;
    private List<Message> messages;
    private Context context;

    public MainPresenter(MainActivityMVP.Model model, Context context) {
        this.model = model;
        disposables = new CompositeDisposable();
        this.context = context;
    }

    @Override
    public void loadData(String lastId) {
        disposables.add(model.loadData(lastId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<List<Message>>() {



                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(List<Message> messages) {
                        if (view != null && messages != null && messages.size() > 0)  {
                            view.updateData(messages);
                            MainPresenter.this.messages = messages;
                            if(isConnected()){
                                resendUnReceived();
                            }
                            //model.saveMessage(messages);
                        }
                    }
                }));
    }

    private void resendUnReceived() {
        if(messages != null) {
            for (Message message : messages) {
                if (message.getUsername().equals(view.getUserName()) && message.getmStatus() == Message.STATUS_SENT) {
                    if(message.getType() == Message.TYPE_MESSAGE) {
                        newMessage(message);
                    }else if(message.getType() == Message.TYPE_MESSAGE_IMAGE) {
                        uploadImage(message);
                    }
                }
            }
        }
    }

    @Override
    public void connectToServer(String mUsername) {
        disposables.add(model.connectToServer(mUsername).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableCompletableObserver() {



                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {
                        initCallbacks();
                        resendUnReceived();
                    }
                }));
    }

    private void initCallbacks() {
        newMessageCallback();
        userJoinedCallback();
        userLeftCallback();
        typingCallback();
        stopTypingCallback();
        newMessageImageCallback();
    }

    @Override
    public void disconnectFromServer() {
        model.disconnectFromServer();
    }

    @Override
    public void newMessage(Message message) {
        model.saveMessage(message);

        disposables.add(model.newMessage(message).timeout(10, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if(isConnected()){
                            newMessage(message);
                        }

                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            model.saveMessage(message);
                        }
                    }
                }));
    }

    @Override
    public void typing(Message message) {
        model.typing(message);
    }

    @Override
    public void stopTyping(Message message) {
        model.stopTyping(message);
    }

    @Override
    public void uploadImage(Message message) {
        model.saveMessage(message);
        disposables.add(model.uploadImage(message).timeout(30, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {
                        if (view != null && message != null)  {
                            view.showSnackbar("Completed!");
                        }
                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            view.showSnackbar(message.getUploadPercent()+"");
                        }
                        if(message.getmStatus() == Message.STATUS_RECEIVED){
                            model.saveMessage(message);
                        }
                    }
                }));
    }

    @Override
    public void newMessageCallback() {
        disposables.add(model.newMessageCallback().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            view.removeTyping(message.getUsername());
                            view.addMessage(message);
                            if(message.getType() == Message.TYPE_MESSAGE || message.getType() == Message.TYPE_MESSAGE_IMAGE) {
                                message.setmStatus(Message.STATUS_RECEIVED);
                                model.saveMessage(message);
                            }
                        }
                    }
                }));
    }

    @Override
    public void typingCallback() {
        disposables.add(model.typingCallback().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            view.addTyping(message.getUsername());
                        }
                    }
                }));
    }

    @Override
    public void stopTypingCallback() {
        disposables.add(model.stopTypingCallback().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            view.removeTyping(message.getUsername());
                        }
                    }
                }));
    }

    @Override
    public void userJoinedCallback() {
        disposables.add(model.userJoinedCallback().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            view.addLog(message);
                        }
                    }
                }));
    }

    @Override
    public void userLeftCallback() {
        disposables.add(model.userLeftCallback().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            view.addLog(message);
                            view.removeTyping(message.getUsername());
                        }
                    }
                }));
    }

    @Override
    public void newMessageImageCallback() {
        disposables.add(model.newMessageImageCallback().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Message>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (view != null) {
                            view.showSnackbar("Network error");
                        }

                    }

                    @Override
                    public void onComplete() {

                    }


                    @Override
                    public void onNext(Message message) {
                        if (view != null && message != null)  {
                            view.removeTyping(message.getUsername());
                            view.addMessage(message);
                            if(message.getType() == Message.TYPE_MESSAGE || message.getType() == Message.TYPE_MESSAGE_IMAGE) {
                                message.setmStatus(Message.STATUS_RECEIVED);
                                model.saveMessage(message);
                            }
                        }
                    }
                }));
    }

    @Override
    public void rxUnsubscribe() {
        disposables.dispose();
    }

    @Override
    public boolean isConnected() {
        return model.isConnected();
    }

    @Override
    public void setView(MainActivityMVP.View view) {

        this.view = view;

    }

    @Override
    public void compressImages(File imageFile) {
        try {
            disposables.add(new Compressor(context)
                    .setMaxHeight(900)
                    .setMaxWidth(900)
                    .setRadius(25)
                    .compressToFile(imageFile).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                    .subscribeWith(new DisposableObserver<File[]>() {



                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (view != null) {
                                view.showSnackbar("Error compressing images");
                            }
                        }

                        @Override
                        public void onComplete() {

                        }


                        @Override
                        public void onNext(File[] files) {
                            if(files.length > 0){
                                view.addImageMessage(files[0]);
                            }
                        }
                    }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
