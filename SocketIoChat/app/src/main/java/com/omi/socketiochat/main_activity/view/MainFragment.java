package com.omi.socketiochat.main_activity.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.omi.socketiochat.R;
import com.omi.socketiochat.compresser.PathUtil;
import com.omi.socketiochat.main_activity.MainActivityMVP;
import com.omi.socketiochat.main_activity.adapters.MessageAdapter;
import com.omi.socketiochat.main_activity.utils.UserPref;
import com.omi.socketiochat.objects.Message;
import com.omi.socketiochat.root.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A chat fragment containing messages view and input form.
 */
public class MainFragment extends Fragment implements MainActivityMVP.View {

    private static final int PICK_IMAGE = 100;

    private static final String TAG = "MainFragment";


    private static final int TYPING_TIMER_LENGTH = 600;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private String mUsername;


    @Inject
    MainActivityMVP.Presenter presenter;


    @BindView(R.id.messages)
    RecyclerView mMessagesView;

    @BindView(R.id.message_input)
    EditText mInputMessageView;

    @BindView(R.id.send_button)
    ImageButton sendButton;

    @BindView(R.id.pick_button)
    ImageButton pickButton;

    public MainFragment() {
        super();
    }


    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new MessageAdapter(context, mMessages);
        if (context instanceof Activity){
            //this.listener = (MainActivity) context;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserPref userPref = new UserPref(getActivity());
        if(userPref.getUsername() == null){
            mUsername = "user "+ Calendar.getInstance().getTimeInMillis();
            userPref.setUsername(mUsername);
        }else {
            mUsername = userPref.getUsername();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.disconnectFromServer();
        presenter.rxUnsubscribe();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((App) getActivity().getApplication()).getComponent().inject(this);
        presenter.setView(this);
        presenter.loadData("");

        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == 555 || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!presenter.isConnected()) return;

                if (!mTyping) {
                    mTyping = true;
                    presenter.typing(new Message.Builder(Message.TYPE_LOG).username(mUsername).build());
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });



        addLog(new Message.Builder(Message.TYPE_LOG)
                .message(getResources().getString(R.string.message_welcome)).build());


        presenter.connectToServer(mUsername);
    }

    private void pickImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            if(data != null) {
                try {
                    presenter.compressImages(new File(PathUtil.getPath(getActivity() ,data.getData())));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void addLog(Message message) {
        mMessages.add(message);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    @Override
    public void addMessage(Message message) {
        mMessages.add(message);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    @Override
    public void addImageMessage(Message message) {

    }

    @Override
    public String getUserName() {
        return mUsername;
    }

    @Override
    public void addTyping(String username) {
        if (mMessages.get(mMessages.size()-1).getType() == Message.TYPE_ACTION && mMessages.get(mMessages.size()-1).getUsername().equals(username)) {
            return;
        }
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }
    @Override
    public void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void attemptSend() {
        if (null == mUsername) return;

        mTyping = false;

        String messageText = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");


        Message message = new Message.Builder(Message.TYPE_MESSAGE)
                .id("id"+Calendar.getInstance().getTimeInMillis()).username(mUsername).message(messageText).status(Message.STATUS_SENT).build();
        addMessage(message);

        // perform the sending message attempt.
        presenter.newMessage(message);
        if (!presenter.isConnected()) {
            showSnackbar("No Connection, Your messages will be resend once connection retrieved");
        }
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            presenter.stopTyping(new Message.Builder(Message.TYPE_LOG).username(mUsername).build());
        }
    };

    @Override
    public void updateData(List<Message> messages) {
        for (Message message:messages) {
            addMessage(message);
        }
    }

    @Override
    public void showSnackbar(String error) {
        Snackbar.make(mMessagesView, error, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}

