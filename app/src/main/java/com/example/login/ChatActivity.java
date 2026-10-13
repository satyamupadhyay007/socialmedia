package com.example.login;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    // ==================================================
    // UI
    // ==================================================

    private TextView chatProfile;
    private ImageView imgChatProfile;

    private ImageButton photoButton;
    private ImageButton sendButton;

    private EditText messageEditText;

    private RecyclerView messageRecyclerView;


    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseFirestore db;


    // ==================================================
    // CHAT DATA
    // ==================================================

    private String currentUserId;
    private String receiverId;
    private String receiverUsername;
    private String chatId;


    // ==================================================
    // MESSAGE DATA
    // ==================================================

    private List<Message> messageList;
    private MessageAdapter messageAdapter;


    // ==================================================
    // MEDIA
    // ==================================================

    private Uri selectedMediaUri;
    private String selectedMediaType = "";


    // ==================================================
    // TEMP UPLOAD
    // ==================================================

    private Message uploadingMessage;


    // ==================================================
    // SHARED POST
    // ==================================================

    private boolean sharePost = false;
    private String sharedPostId = "";
    private String sharedPostImage = "";
    private String sharedPostCaption = "";


    // ==================================================
    // CLOUDINARY
    // ==================================================

    private static final String CLOUD_NAME =
            "klhzpjaj";

    private static final String UPLOAD_PRESET =
            "frenzo_media";

    private static boolean cloudinaryInitialized =
            false;


    // ==================================================
    // PHOTO PICKER
    // ==================================================

    private final ActivityResultLauncher<String>
            photoPicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        selectedMediaUri = uri;

                        selectedMediaType = "image";

                        messageEditText.setText("");

                        messageEditText.setHint(
                                "Photo selected • Press Send"
                        );

                        messageEditText.requestFocus();
                    }
            );


    // ==================================================
    // VIDEO PICKER
    // ==================================================

    private final ActivityResultLauncher<String>
            videoPicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        selectedMediaUri = uri;

                        selectedMediaType = "video";

                        messageEditText.setText("");

                        messageEditText.setHint(
                                "Video selected • Press Send"
                        );

                        messageEditText.requestFocus();
                    }
            );


    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_chat);


        // ==================================================
        // FIREBASE
        // ==================================================

        FirebaseAuth mAuth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();


        if (mAuth.getCurrentUser() == null) {

            finish();
            return;
        }


        currentUserId =
                mAuth.getCurrentUser()
                        .getUid();


        // ==================================================
        // RECEIVE USER
        // ==================================================

        receiverId =
                getIntent().getStringExtra(
                        "receiverId"
                );

        receiverUsername =
                getIntent().getStringExtra(
                        "receiverUsername"
                );


        // ==================================================
        // RECEIVE SHARED POST
        // ==================================================

        sharePost = getIntent().getBooleanExtra(
                "sharePost",
                false
        );

        if (sharePost) {

            sharedPostId = getIntent().getStringExtra(
                    "sharedPostId"
            );

            sharedPostImage = getIntent().getStringExtra(
                    "sharedPostImage"
            );

            sharedPostCaption = getIntent().getStringExtra(
                    "sharedPostCaption"
            );

            if (sharedPostId == null) {
                sharedPostId = "";
            }

            if (sharedPostImage == null) {
                sharedPostImage = "";
            }

            if (sharedPostCaption == null) {
                sharedPostCaption = "";
            }
        }


        if (
                receiverId == null ||
                        receiverId.isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "User information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }


        if (
                receiverUsername == null ||
                        receiverUsername.isEmpty()
        ) {

            receiverUsername =
                    "User";
        }


        // ==================================================
        // CHAT ID
        // ==================================================

        chatId =
                createChatId(
                        currentUserId,
                        receiverId
                );


        // ==================================================
        // FIND VIEWS
        // ==================================================

        TextView chatUserName =
                findViewById(
                        R.id.chatUserName
                );

        TextView chatStatus =
                findViewById(
                        R.id.chatStatus
                );

        chatProfile =
                findViewById(
                        R.id.chatProfile
                );

        imgChatProfile =
                findViewById(
                        R.id.imgChatProfile
                );

        ImageButton backButton =
                findViewById(
                        R.id.backButton
                );

        photoButton =
                findViewById(
                        R.id.photoButton
                );

        sendButton =
                findViewById(
                        R.id.sendButton
                );

        messageEditText =
                findViewById(
                        R.id.messageEditText
                );

        // ==================================================
        // SHARED POST UI
        // ==================================================

        if (sharePost && !sharedPostImage.isEmpty()) {

            messageEditText.setText("");

            messageEditText.setHint(
                    "Post selected • Press Send"
            );

            messageEditText.requestFocus();
        }

        messageRecyclerView =
                findViewById(
                        R.id.messageRecyclerView
                );


        // ==================================================
        // HEADER
        // ==================================================

        chatUserName.setText(
                receiverUsername
        );

        chatStatus.setText(
                getString(R.string.chat_handle, receiverUsername)
        );

        loadReceiverProfile();


        // ==================================================
        // MESSAGE LIST
        // ==================================================

        messageList =
                new ArrayList<>();


        messageAdapter =
                new MessageAdapter(
                        messageList,
                        currentUserId,
                        this::showUnsendConfirmation
                );


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        this
                );

        layoutManager.setStackFromEnd(
                true
        );


        messageRecyclerView.setLayoutManager(
                layoutManager
        );

        messageRecyclerView.setAdapter(
                messageAdapter
        );


        // ==================================================
        // CLOUDINARY
        // ==================================================

        initializeCloudinary();


        // ==================================================
        // BACK
        // ==================================================

        backButton.setOnClickListener(
                v -> {

                    hideKeyboard();

                    finish();
                }
        );


        // ==================================================
        // MEDIA
        // ==================================================

        photoButton.setOnClickListener(
                v ->
                        showMediaChooser()
        );


        // ==================================================
        // SEND
        // ==================================================

        sendButton.setOnClickListener(
                v ->
                        sendCurrentContent()
        );


        // ==================================================
        // ENTER
        // ==================================================

        messageEditText.setOnEditorActionListener(
                (v, actionId, event) -> {

                    sendCurrentContent();

                    return true;
                }
        );


        // ==================================================
        // MESSAGE LISTENER
        // ==================================================

        listenForMessages();


        // ==================================================
        // INSETS
        // ==================================================

        setupWindowInsets();

        // Clear unread count when opening chat
        clearUnreadCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearUnreadCount();
    }


    // ==================================================
    // LOAD RECEIVER PROFILE
    // ==================================================

    private void loadReceiverProfile() {

        db.collection("users")
                .document(receiverId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String name = document.getString("name");
                    String profileImage = document.getString("profileImage");

                    if (name != null && !name.trim().isEmpty()) {
                        chatProfile.setText(name.substring(0, 1).toUpperCase());
                    } else {
                        chatProfile.setText(receiverUsername.substring(0, 1).toUpperCase());
                    }

                    if (profileImage != null && !profileImage.isEmpty()) {

                        Glide.with(this)
                                .load(profileImage)
                                .circleCrop()
                                .into(imgChatProfile);

                        chatProfile.setVisibility(View.GONE);

                    } else {

                        imgChatProfile.setImageDrawable(null);
                        chatProfile.setVisibility(View.VISIBLE);
                    }
                });
    }


    // ==================================================
    // WINDOW INSETS
    // ==================================================

    private void setupWindowInsets() {

        View root =
                findViewById(
                        R.id.chatRoot
                );

        View header =
                findViewById(
                        R.id.chatHeader
                );

        View inputContainer =
                findViewById(
                        R.id.messageInputContainer
                );


        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, windowInsets) -> {

                    Insets systemBars =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type
                                            .systemBars()
                            );

                    Insets cutout =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type
                                            .displayCutout()
                            );

                    Insets ime =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type
                                            .ime()
                            );

                    boolean keyboardVisible =
                            windowInsets.isVisible(
                                    WindowInsetsCompat.Type
                                            .ime()
                            );


                    int safeTop =
                            Math.max(
                                    systemBars.top,
                                    cutout.top
                            );


                    int safeBottom;


                    if (keyboardVisible) {

                        safeBottom =
                                Math.max(
                                        ime.bottom,
                                        systemBars.bottom
                                );

                    } else {

                        safeBottom =
                                Math.max(
                                        systemBars.bottom,
                                        cutout.bottom
                                );
                    }


                    // ==================================================
                    // HEADER
                    // ==================================================

                    ViewGroup.LayoutParams
                            rawHeaderParams =
                            header.getLayoutParams();


                    if (
                            rawHeaderParams
                                    instanceof
                                    ViewGroup.MarginLayoutParams
                    ) {

                        ViewGroup.MarginLayoutParams
                                headerParams =
                                (ViewGroup.MarginLayoutParams)
                                        rawHeaderParams;


                        headerParams.topMargin =
                                safeTop;


                        header.setLayoutParams(
                                headerParams
                        );
                    }


                    // ==================================================
                    // INPUT
                    // ==================================================

                    ViewGroup.LayoutParams
                            rawInputParams =
                            inputContainer.getLayoutParams();


                    if (
                            rawInputParams
                                    instanceof
                                    ViewGroup.MarginLayoutParams
                    ) {

                        ViewGroup.MarginLayoutParams
                                inputParams =
                                (ViewGroup.MarginLayoutParams)
                                        rawInputParams;


                        inputParams.bottomMargin =
                                safeBottom;


                        inputContainer.setLayoutParams(
                                inputParams
                        );
                    }


                    return windowInsets;
                }
        );


        ViewCompat.requestApplyInsets(
                root
        );
    }


    // ==================================================
    // CLOUDINARY INIT
    // ==================================================

    private void initializeCloudinary() {

        if (cloudinaryInitialized) {
            return;
        }


        try {

            Map<String, Object> config =
                    new HashMap<>();


            config.put(
                    "cloud_name",
                    CLOUD_NAME
            );


            config.put(
                    "secure",
                    true
            );


            MediaManager.init(
                    getApplicationContext(),
                    config
            );


            cloudinaryInitialized =
                    true;

        } catch (Exception e) {

            cloudinaryInitialized =
                    true;
        }
    }


    // ==================================================
    // MEDIA CHOOSER
    // ==================================================

    private void showMediaChooser() {

        String[] options = {
                "📷  Photo",
                "🎥  Video"
        };


        new AlertDialog.Builder(this)

                .setTitle(
                        "Choose media"
                )

                .setItems(
                        options,
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    photoPicker.launch("image/*");
                                    break;
                                case 1:
                                    videoPicker.launch("video/*");
                                    break;
                            }
                        }
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }


    // ==================================================
    // SEND CURRENT
    // ==================================================

    private void sendCurrentContent() {

        // ==================================================
        // SHARED POST
        // ==================================================

        if (sharePost &&
                sharedPostImage != null &&
                !sharedPostImage.trim().isEmpty()) {

            sendSharedPost();

            return;
        }

        // ==================================================
        // NORMAL PHOTO / VIDEO
        // ==================================================

        if (selectedMediaUri != null) {

            uploadMediaToCloudinary();

            return;
        }

        // ==================================================
        // NORMAL TEXT
        // ==================================================

        sendMessage();
    }


    // ==================================================
    // SEND TEXT
    // ==================================================

    private void sendMessage() {

        String text =
                messageEditText
                        .getText()
                        .toString()
                        .trim();


        if (text.isEmpty()) {

            messageEditText.setError(
                    "Write a message"
            );

            messageEditText.requestFocus();

            return;
        }


        String messageId =
                db.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .document()
                        .getId();


        Message message =
                new Message(
                        messageId,
                        currentUserId,
                        receiverId,
                        text,
                        System.currentTimeMillis(),
                        "sent"
                );


        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(message)

                .addOnSuccessListener(
                        unused -> {

                            updateChatSummary(
                                    message
                            );


                            messageEditText.setText("");


                            messageEditText.setHint(
                                    "Write a message..."
                            );


                            messageEditText.requestFocus();
                        }
                )

                .addOnFailureListener(
                        e -> {
                            String error = (e.getMessage() == null || e.getMessage().isEmpty())
                                    ? "Unknown error" : e.getMessage();

                            Toast.makeText(
                                    this,
                                    "Message could not be sent: " + error,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // ==================================================
    // SEND SHARED POST
    // ==================================================

    private void sendSharedPost() {

        if (sharedPostImage == null ||
                sharedPostImage.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Post image missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String messageId =
                db.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .document()
                        .getId();

        String messageText = (sharedPostCaption == null) ? "" : sharedPostCaption;

        Message sharedMessage =
                new Message(
                        messageId,
                        currentUserId,
                        receiverId,
                        messageText,
                        sharedPostImage,
                        "image",
                        System.currentTimeMillis(),
                        "sent"
                );

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(sharedMessage)
                .addOnSuccessListener(
                        unused -> {

                            updateChatSummary(
                                    sharedMessage
                            );

                            Toast.makeText(
                                    this,
                                    "Post sent ✓",
                                    Toast.LENGTH_SHORT
                            ).show();

                            sharePost = false;
                            sharedPostId = "";
                            sharedPostImage = "";
                            sharedPostCaption = "";

                            messageEditText.setText("");
                            messageEditText.setHint(
                                    "Write a message..."
                            );
                            messageEditText.requestFocus();
                        }
                )
                .addOnFailureListener(
                        e -> {
                            String error = (e.getMessage() == null || e.getMessage().isEmpty())
                                    ? "Unknown error" : e.getMessage();

                            Toast.makeText(
                                    this,
                                    "Post could not be sent: " + error,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // ==================================================
    // SHOW TEMPORARY UPLOAD BUBBLE
    // ==================================================

    private void showUploadingBubble() {

        String tempId =
                "upload_" +
                        System.currentTimeMillis();


        uploadingMessage =
                new Message(
                        tempId,
                        currentUserId,
                        receiverId,
                        "",
                        selectedMediaUri != null
                                ? selectedMediaUri.toString()
                                : "",
                        selectedMediaType,
                        System.currentTimeMillis(),
                        "sending"
                );


        uploadingMessage.setUploading(
                true
        );


        uploadingMessage.setUploadProgress(
                0
        );


        messageList.add(
                uploadingMessage
        );


        messageAdapter.notifyItemInserted(
                messageList.size() - 1
        );


        messageRecyclerView.post(
                () ->
                        messageRecyclerView.scrollToPosition(
                                messageList.size() - 1
                        )
        );
    }


    // ==================================================
    // UPDATE UPLOAD PROGRESS
    // ==================================================

    private void updateUploadingProgress(
            int progress
    ) {

        if (uploadingMessage == null) {
            return;
        }


        uploadingMessage.setUploadProgress(
                progress
        );


        int index =
                messageList.indexOf(
                        uploadingMessage
                );


        if (index != -1) {

            messageAdapter.notifyItemChanged(
                    index
            );
        }
    }


    // ==================================================
    // REMOVE UPLOAD BUBBLE
    // ==================================================

    private void removeUploadingBubble() {

        if (uploadingMessage == null) {
            return;
        }


        int index =
                messageList.indexOf(
                        uploadingMessage
                );


        if (index != -1) {

            messageList.remove(
                    index
            );


            messageAdapter.notifyItemRemoved(
                    index
            );
        }


        uploadingMessage =
                null;
    }


    // ==================================================
    // UPLOAD MEDIA
    // ==================================================

    private void uploadMediaToCloudinary() {

        if (
                selectedMediaUri == null ||
                        selectedMediaType.isEmpty()
        ) {
            return;
        }


        final Uri uploadUri =
                selectedMediaUri;


        final String uploadType =
                selectedMediaType;


        showUploadingBubble();


        sendButton.setEnabled(
                false
        );


        photoButton.setEnabled(
                false
        );


        messageEditText.setEnabled(
                false
        );


        messageEditText.setHint(
                "Media uploading..."
        );


        MediaManager.get()

                .upload(
                        uploadUri
                )

                .unsigned(
                        UPLOAD_PRESET
                )

                .callback(
                        new UploadCallback() {

                            @Override
                            public void onStart(
                                    String requestId
                            ) {
                            }


                            @Override
                            public void onProgress(
                                    String requestId,
                                    long bytes,
                                    long totalBytes
                            ) {

                                if (totalBytes <= 0) {
                                    return;
                                }


                                int percent =
                                        (int)
                                                (
                                                        bytes * 100
                                                                / totalBytes
                                                );


                                runOnUiThread(
                                        () ->
                                                updateUploadingProgress(
                                                        percent
                                                )
                                );
                            }


                            @Override
                            public void onSuccess(
                                    String requestId,
                                    Map resultData
                            ) {
                                Object secureUrlObj = (resultData != null) ? resultData.get("secure_url") : null;
                                final String finalUrl = (secureUrlObj != null) ? secureUrlObj.toString() : "";

                                runOnUiThread(
                                        () -> {

                                            if (
                                                    finalUrl.isEmpty()
                                            ) {

                                                uploadFailed(
                                                        "Media URL not received"
                                                );

                                                return;
                                            }


                                            updateUploadingProgress(
                                                    100
                                            );


                                            saveMediaMessage(
                                                    finalUrl,
                                                    uploadType
                                            );
                                        }
                                );
                            }


                            @Override
                            public void onError(
                                    String requestId,
                                    ErrorInfo error
                            ) {
                                String errorText = (error != null && error.getDescription() != null
                                        && !error.getDescription().isEmpty())
                                        ? error.getDescription() : "Upload failed";

                                runOnUiThread(() -> uploadFailed(errorText));
                            }


                            @Override
                            public void onReschedule(
                                    String requestId,
                                    ErrorInfo error
                            ) {
                            }
                        }
                )

                .dispatch();
    }


    // ==================================================
    // SAVE MEDIA MESSAGE
    // ==================================================

    private void saveMediaMessage(
            String mediaUrl,
            String mediaType
    ) {

        String messageId =
                db.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .document()
                        .getId();


        Message mediaMessage =
                new Message(
                        messageId,
                        currentUserId,
                        receiverId,
                        "",
                        mediaUrl,
                        mediaType,
                        System.currentTimeMillis(),
                        "sent"
                );


        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(mediaMessage)

                .addOnSuccessListener(
                        unused -> {

                            removeUploadingBubble();


                            updateChatSummary(
                                    mediaMessage
                            );


                            selectedMediaUri =
                                    null;


                            selectedMediaType =
                                    "";


                            resetMediaControls();
                        }
                )

                .addOnFailureListener(
                        e -> {

                            removeUploadingBubble();


                            selectedMediaUri =
                                    null;


                            selectedMediaType =
                                    "";


                            resetMediaControls();


                            Toast.makeText(
                                    this,
                                    "Media message could not be saved: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // ==================================================
    // CHAT SUMMARY
    // ==================================================

    private void updateChatSummary(
            Message message
    ) {

        if (
                message == null ||
                        message.isUnsent()
        ) {
            return;
        }


        Map<String, Object> data =
                new HashMap<>();


        data.put(
                "senderId",
                currentUserId
        );


        data.put(
                "receiverId",
                receiverId
        );


        data.put(
                "lastMessageTime",
                message.getTimestamp()
        );


        if (
                "image".equalsIgnoreCase(
                        message.getMessageType()
                )
        ) {

            data.put(
                    "lastMessage",
                    "📷 Photo"
            );

        } else if (
                "video".equalsIgnoreCase(
                        message.getMessageType()
                )
        ) {

            data.put(
                    "lastMessage",
                    "🎥 Video"
            );

        } else {

            data.put(
                    "lastMessage",
                    message.getMessage()
            );
        }


        data.put(
                "unread_" + receiverId,
                FieldValue.increment(1)
        );

        db.collection("chats")
                .document(chatId)
                .set(
                        data,
                        SetOptions.merge()
                );
    }

    private void clearUnreadCount() {
        if (db == null || chatId == null || chatId.isEmpty() || currentUserId == null || currentUserId.isEmpty()) {
            return;
        }
        Map<String, Object> clear = new HashMap<>();
        clear.put("unread_" + currentUserId, 0);
        db.collection("chats")
                .document(chatId)
                .set(clear, SetOptions.merge());
    }


    // ==================================================
    // UNSEND CONFIRMATION
    // ==================================================

    private void showUnsendConfirmation(
            Message message
    ) {

        if (message == null) {
            return;
        }


        if (!Objects.equals(currentUserId, message.getSenderId())) {
            return;
        }


        String title;

        if (
                "image".equalsIgnoreCase(
                        message.getMessageType()
                )
        ) {

            title =
                    "Unsend photo?";

        } else if (
                "video".equalsIgnoreCase(
                        message.getMessageType()
                )
        ) {

            title =
                    "Unsend video?";
        } else {
            title = "Unsend message?";
        }


        new AlertDialog.Builder(this)

                .setTitle(
                        title
                )

                .setMessage(
                        "It will disappear from the chat for both users."
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Unsend",
                        (dialog, which) ->
                                unsendMessage(
                                        message
                                )
                )

                .show();
    }


    // ==================================================
    // UNSEND
    // ==================================================

    private void unsendMessage(
            Message message
    ) {

        if (message == null) {

            Toast.makeText(
                    this,
                    "Message is null",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String messageId =
                message.getMessageId();


        if (
                messageId == null ||
                        messageId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Message ID missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (
                currentUserId == null ||
                        !currentUserId.equals(
                                message.getSenderId()
                        )
        ) {

            Toast.makeText(
                    this,
                    "You can only unsend your own message",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ==================================================
        // UPDATE FIRESTORE
        //
        // IMPORTANT:
        // Field name is now "unsent"
        // ==================================================

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .update(
                        "unsent",
                        true
                )

                .addOnSuccessListener(
                        unused -> Toast.makeText(
                                this,
                                "Unsent successfully",
                                Toast.LENGTH_SHORT
                        ).show()
                )

                .addOnFailureListener(
                        e -> {
                            String errorMessage = (e.getMessage() == null || e.getMessage().isEmpty())
                                    ? "Unknown Firebase error" : e.getMessage();

                            Toast.makeText(
                                    this,
                                    "Unsend failed: " + errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // ==================================================
    // REALTIME MESSAGE LISTENER
    // ==================================================

    private void listenForMessages() {

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy(
                        "timestamp",
                        Query.Direction.ASCENDING
                )

                .addSnapshotListener(
                        (snapshot, error) -> {

                            if (error != null) {

                                Toast.makeText(
                                        this,
                                        "Could not load chat: "
                                                + error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }


                            if (snapshot == null) {
                                return;
                            }


                            messageList.clear();


                            for (
                                    DocumentSnapshot document :
                                    snapshot.getDocuments()
                            ) {

                                Message message =
                                        document.toObject(
                                                Message.class
                                        );


                                if (message == null) {
                                    continue;
                                }


                                // ==================================================
                                // IMPORTANT FIX
                                // Always use Firestore document ID
                                // as message ID.
                                // ==================================================

                                message.setMessageId(
                                        document.getId()
                                );


                                // ==================================================
                                // CHECK NEW UNSENT FIELD
                                // ==================================================

                                Boolean oldUnsent =
                                        document.getBoolean(
                                                "isUnsent"
                                        );

                                boolean unsent =
                                        message.isUnsent() || (oldUnsent != null && oldUnsent);


                                // ==================================================
                                // HIDE UNSENT MESSAGE
                                // ==================================================

                                if (unsent) {

                                    continue;
                                }


                                // ==================================================
                                // MESSAGE RECEIVED FROM OTHER USER
                                // ==================================================

                                if (
                                        currentUserId != null &&
                                                Objects.equals(
                                                        receiverId,
                                                        message.getSenderId()
                                                )
                                ) {

                                    String status =
                                            message.getStatus();


                                    if (
                                            !"seen".equalsIgnoreCase(
                                                    status
                                            )
                                    ) {

                                        markAsSeen(
                                                document.getId()
                                        );


                                        message.setStatus(
                                                "seen"
                                        );
                                    }
                                }


                                messageList.add(
                                        message
                                );
                            }


                            // ==================================================
                            // KEEP UPLOAD BUBBLE
                            // ==================================================

                            if (uploadingMessage != null) {

                                messageList.add(
                                        uploadingMessage
                                );
                            }


                            messageAdapter.notifyDataSetChanged();


                            scrollToLastMessage();
                        }
                );
    }


    // ==================================================
    // MARK AS SEEN
    // ==================================================

    private void markAsSeen(
            String messageId
    ) {

        if (
                messageId == null ||
                        messageId.isEmpty()
        ) {

            return;
        }


        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .update(
                        "status",
                        "seen"
                );
    }


    // ==================================================
    // SCROLL
    // ==================================================

    private void scrollToLastMessage() {

        if (
                messageList == null ||
                        messageList.isEmpty() ||
                        messageRecyclerView == null
        ) {

            return;
        }


        messageRecyclerView.post(
                () ->
                        messageRecyclerView.scrollToPosition(
                                messageList.size() - 1
                        )
        );
    }


    // ==================================================
    // RESET MEDIA CONTROLS
    // ==================================================

    private void resetMediaControls() {

        if (sendButton != null) {

            sendButton.setEnabled(
                    true
            );
        }


        if (photoButton != null) {

            photoButton.setEnabled(
                    true
            );
        }


        if (messageEditText != null) {

            messageEditText.setEnabled(
                    true
            );


            messageEditText.setHint(
                    "Write a message..."
            );


            messageEditText.requestFocus();
        }
    }


    // ==================================================
    // UPLOAD FAILED
    // ==================================================

    private void uploadFailed(
            String error
    ) {

        removeUploadingBubble();


        selectedMediaUri =
                null;


        selectedMediaType =
                "";


        resetMediaControls();


        Toast.makeText(
                this,
                error,
                Toast.LENGTH_LONG
        ).show();
    }


    // ==================================================
    // CREATE CHAT ID
    // ==================================================

    private String createChatId(
            String user1,
            String user2
    ) {

        if (
                user1.compareTo(
                        user2
                ) < 0
        ) {

            return user1 +
                    "_" +
                    user2;
        }


        return user2 +
                "_" +
                user1;
    }


    // ==================================================
    // HIDE KEYBOARD
    // ==================================================

    private void hideKeyboard() {

        InputMethodManager imm =
                (InputMethodManager)
                        getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );


        View currentFocus =
                getCurrentFocus();


        if (
                imm != null &&
                        currentFocus != null
        ) {

            imm.hideSoftInputFromWindow(
                    currentFocus.getWindowToken(),
                    0
            );
        }
    }


    // ==================================================
    // DESTROY
    // ==================================================

    @Override
    protected void onDestroy() {

        selectedMediaUri =
                null;


        selectedMediaType =
                "";


        uploadingMessage =
                null;


        super.onDestroy();
    }
}
