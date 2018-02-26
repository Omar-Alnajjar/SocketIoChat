package com.omi.socketiochat.main_activity.presenter;



import com.omi.socketiochat.main_activity.MainActivityMVP;
import com.omi.socketiochat.objects.Message;

import org.reactivestreams.Subscription;

import java.util.List;

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

    public MainPresenter(MainActivityMVP.Model model) {
        this.model = model;
        disposables = new CompositeDisposable();
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
                            model.saveData(messages);
                        }
                    }
                }));
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
                        newMessageCallback();
                        userJoinedCallback();
                        userLeftCallback();
                        typingCallback();
                        stopTypingCallback();
                    }
                }));
    }

    @Override
    public void disconnectFromServer() {
        model.disconnectFromServer();
    }

    @Override
    public void newMessage(Message message) {
        model.newMessage(message);
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
}
