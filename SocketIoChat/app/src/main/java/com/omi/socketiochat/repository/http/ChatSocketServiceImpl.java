package com.omi.socketiochat.repository.http;



import android.content.Context;
import android.util.Log;

import com.omi.socketiochat.R;
import com.omi.socketiochat.main_activity.utils.FileDownloadManager;
import com.omi.socketiochat.main_activity.utils.FileUploadManager;
import com.omi.socketiochat.objects.Message;
import com.omi.socketiochat.root.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatSocketServiceImpl implements ChatSocketService{

    private Socket mSocket;
    private Context context;
    private String mUsername;
    private boolean isConnected;
    private static final String TAG = "MainFragment";

    private Emitter.Listener onConnect;

    private Emitter.Listener onNewMessage;
    private Emitter.Listener onTyping;
    private Emitter.Listener onStopTyping;
    private Emitter.Listener onUserJoined;
    private Emitter.Listener onUserLeft;

    private FileUploadManager mFileUploadManager;
    private FileDownloadManager mFileDownloadManager;


    public ChatSocketServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public Completable connectToServer(String mUsername) {
        this.mUsername = mUsername;

        App app = (App) (context);
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();

        return Completable.create(completableSubscriber -> {
            onConnect = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    isConnected = true;
                    userJoined(new Message.Builder(Message.TYPE_LOG).username(mUsername).build());
                    completableSubscriber.onComplete();
                }
            };
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
        });
    }

    @Override
    public Completable disconnectFromServer() {


        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("chat message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        return Completable.complete();
    }

    @Override
    public Completable connectError() {
        return null;
    }

    @Override
    public Observable<Message> newMessage(Message message) {
        return Observable.create(observableSubscriber -> {
            mSocket.emit("chat message", message.toJsonObject(), new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, (String) args[0]);
                    message.setmStatus(Message.STATUS_RECEIVED);
                    observableSubscriber.onNext(message);
                    observableSubscriber.onComplete();
                }
            });
        });
    }

    @Override
    public Observable<Message> uploadImage(Message message) {
        mFileUploadManager = new FileUploadManager(context);
        mFileUploadManager.prepare(message.getMessage());

        JSONObject res = new JSONObject();
        try {
            res.put("Name", mFileUploadManager.getFileName());
            res.put("Size", mFileUploadManager.getFileSize());
            res.put("username", mUsername);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // This will trigger server 'uploadFileStart' function
        mSocket.emit("uploadFileStart", res);

        return Observable.create(observableSubscriber -> {


            Emitter.Listener onUploadMoreData = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        JSONObject json_data = ((JSONObject)args[0]);
                        int place = json_data.getInt("Place");
                        int percent = json_data.getInt("Percent");

                        message.setUploadPercent(percent);
                        observableSubscriber.onNext(message);
                        // Read the next chunk
                        mFileUploadManager.read();

                        JSONObject res = new JSONObject();

                        res.put("Name", mFileUploadManager.getFileName());
                        res.put("Data", mFileUploadManager.getData());
                        res.put("chunkSize", mFileUploadManager.getBytesRead());

                        // This will trigger server 'uploadFileChuncks' function
                        mSocket.emit("uploadFileChuncks", res);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Emitter.Listener onUploadComplete = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    mFileUploadManager.close();

                    message.setmStatus(Message.STATUS_RECEIVED);
                    observableSubscriber.onNext(message);
                    observableSubscriber.onComplete();
                }
            };

            mSocket.on("uploadFileMoreDataReq", onUploadMoreData);
            mSocket.on("uploadFileCompleteRes", onUploadComplete);
        });
    }

    @Override
    public Completable userJoined(Message message) {
        mSocket.emit("user joined", message.toJsonObject());
        return Completable.complete();
    }

    @Override
    public Completable userLeft(Message message) {
        mSocket.emit("user left", message.toJsonObject());
        return Completable.complete();
    }

    @Override
    public Completable typing(Message message) {
        mSocket.emit("typing", message.toJsonObject());
        return null;
    }

    @Override
    public Completable stopTyping(Message message) {
        mSocket.emit("stop typing", message.toJsonObject());
        return null;
    }

    @Override
    public Observable<Message> newMessageCallback() {
        return Observable.create(observableSubscriber -> {
            onNewMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    String id;
                    String username;
                    String messageText;
                    int status;
                    try {
                        id = data.getString("id");
                        username = data.getString("username");
                        messageText = data.getString("message");
                        status = data.getInt("status");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    Message message = new Message.Builder(Message.TYPE_MESSAGE)
                            .id(id).username(username).message(messageText).status(status).build();

                    observableSubscriber.onNext(message);
                }
            };
            mSocket.on("chat message", onNewMessage);
        });
    }

    @Override
    public Observable<Message> typingCallback() {
        return Observable.create(observableSubscriber -> {
            onTyping = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                            JSONObject data = (JSONObject) args[0];
                            String username;
                            try {
                                username = data.getString("username");
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                                return;
                            }

                            observableSubscriber.onNext(new Message.Builder(Message.TYPE_ACTION).username(username).build());

                }
            };
            mSocket.on("typing", onTyping);
        });
    }

    @Override
    public Observable<Message> stopTypingCallback() {
        return Observable.create(observableSubscriber -> {
            onStopTyping = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                            JSONObject data = (JSONObject) args[0];
                            String username;
                            try {
                                username = data.getString("username");
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                                return;
                            }

                            observableSubscriber.onNext(new Message.Builder(Message.TYPE_ACTION).username(username).build());
                        }
            };
            mSocket.on("stop typing", onStopTyping);
        });
    }

    @Override
    public Observable<Message> userJoinedCallback() {
        return Observable.create(observableSubscriber -> {
            onUserJoined = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {

                            JSONObject data = (JSONObject) args[0];
                            String username;
                            try {
                                username = data.getString("username");
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                                return;
                            }

                            observableSubscriber.onNext(new Message.Builder(Message.TYPE_LOG).username(username).message(context.getResources().getString(R.string.message_user_joined, username)).build());
                        }
            };
            mSocket.on("user joined", onUserJoined);
        });
    }

    @Override
    public Observable<Message> userLeftCallback() {
        return Observable.create(observableSubscriber -> {
            onUserLeft = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                            JSONObject data = (JSONObject) args[0];
                            String username;
                            int numUsers;
                            try {
                                username = data.getString("username");
                                numUsers = data.getInt("numUsers");
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                                return;
                            }
                            observableSubscriber.onNext(new Message.Builder(Message.TYPE_LOG).username(username).message(context.getResources().getString(R.string.message_user_joined, username)).build());

                }
            };
            mSocket.on("user left", onUserLeft);
        });
    }

    @Override
    public Observable<Message> newMessageImageCallback() {

        Emitter.Listener onDownloadStart = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];

                String fileName = null;
                String userName = null;
                try {
                    fileName = data.getString("fileName");
                    userName = data.getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mFileDownloadManager = new FileDownloadManager();
                mFileDownloadManager.setUsername(userName);
                try {
                    mFileDownloadManager.prepare(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Emitter.Listener onDownloadMore = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                String data = (String) args[0];
                try {
                    mFileDownloadManager.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mSocket.on("downloadStart", onDownloadStart);
        mSocket.on("downloadMore", onDownloadMore);

        return Observable.create(observableSubscriber -> {
            Emitter.Listener onDownloadComplete = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    mFileDownloadManager.close();
                    observableSubscriber.onNext(new Message.Builder(Message.TYPE_MESSAGE_IMAGE).id("id"+ Calendar.getInstance().getTimeInMillis())
                    .message(mFileDownloadManager.getFilePath()).username(mFileDownloadManager.getUsername()).status(Message.STATUS_RECEIVED).build());
                    observableSubscriber.onComplete();
                }
            };
            mSocket.on("downloadComplete", onDownloadComplete);
        });

    }

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;
            userLeft(new Message.Builder(Message.TYPE_LOG).username(mUsername).build());
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "Error connecting");
        }
    };

    @Override
    public boolean isConnected() {
        return isConnected && mSocket.connected();
    }
}
