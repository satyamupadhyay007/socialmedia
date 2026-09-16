package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {

    // ==================================================
    // UI
    // ==================================================

    private EditText searchUserEditText;

    private ImageButton searchUserButton;

    private RecyclerView chatListRecyclerView;


    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private ImageButton btnHome;
    private ImageButton btnCreate;
    private ImageButton btnMessage;
    private ImageButton btnSearch;
    private ImageButton btnProfile;

    private View bottomNavigation;


    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;


    // ==================================================
    // CHAT LIST
    // ==================================================

    private List<ChatItem> chatList;

    private ChatListAdapter chatListAdapter;


    // ==================================================
    // LOAD GENERATION
    // ==================================================

    private int chatLoadGeneration = 0;


    // ==================================================
    // SHARED POST DATA
    // ==================================================

    private boolean sharePost = false;
    private String sharedPostId = "";
    private String sharedPostImage = "";
    private String sharedPostCaption = "";


    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        EdgeToEdge.enable(
                this
        );

        setContentView(
                R.layout.activity_chat_list
        );


        // ==================================================
        // ROOT
        // ==================================================

        View root =
                findViewById(
                        R.id.chatListRoot
                );


        // ==================================================
        // SAFE AREA
        // ==================================================

        if (root != null) {

            ViewCompat.setOnApplyWindowInsetsListener(
                    root,
                    (view, windowInsets) -> {

                        Insets systemBars =
                                windowInsets.getInsets(
                                        WindowInsetsCompat.Type.systemBars()
                                );

                        view.setPadding(
                                0,
                                systemBars.top,
                                0,
                                systemBars.bottom
                        );

                        return windowInsets;
                    }
            );
        }


        // ==================================================
        // FIREBASE
        // ==================================================

        db =
                FirebaseFirestore.getInstance();

        mAuth =
                FirebaseAuth.getInstance();


        // ==================================================
        // RECEIVE SHARED POST
        // ==================================================

        Intent incomingIntent = getIntent();

        sharePost = incomingIntent.getBooleanExtra(
                "sharePost",
                false
        );

        if (sharePost) {

            sharedPostId = incomingIntent.getStringExtra(
                    "sharedPostId"
            );

            sharedPostImage = incomingIntent.getStringExtra(
                    "sharedPostImage"
            );

            sharedPostCaption = incomingIntent.getStringExtra(
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


        // ==================================================
        // XML VIEWS
        // ==================================================

        searchUserEditText =
                findViewById(
                        R.id.searchUserEditText
                );

        searchUserButton =
                findViewById(
                        R.id.searchUserButton
                );

        chatListRecyclerView =
                findViewById(
                        R.id.chatListRecyclerView
                );


        // ==================================================
        // BOTTOM NAVIGATION VIEWS
        // ==================================================

        btnHome =
                findViewById(
                        R.id.btnHome
                );

        btnCreate =
                findViewById(
                        R.id.btnCreate
                );

        btnMessage =
                findViewById(
                        R.id.btnMessage
                );

        btnSearch =
                findViewById(
                        R.id.btnSearch
                );

        btnProfile =
                findViewById(
                        R.id.btnProfile
                );

        bottomNavigation =
                findViewById(
                        R.id.bottomNavigation
                );


        // ==================================================
        // CHAT LIST
        // ==================================================

        chatList =
                new ArrayList<>();


        chatListAdapter =
                new ChatListAdapter(
                        chatList,
                        chatItem ->
                                openChat(
                                        chatItem
                                )
                );


        chatListRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        chatListRecyclerView.setAdapter(
                chatListAdapter
        );


        // ==================================================
        // LOAD CHATS
        // ==================================================

        loadChats();


        // ==================================================
        // SEARCH BUTTON
        // ==================================================

        if (
                searchUserButton != null
        ) {

            searchUserButton.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        searchUser();
                    }
            );
        }


        // ==================================================
        // SEARCH ENTER
        // ==================================================

        if (
                searchUserEditText != null
        ) {

            searchUserEditText.setOnEditorActionListener(
                    (v, actionId, event) -> {

                        hideKeyboard();

                        searchUser();

                        return true;
                    }
            );
        }


        // ==================================================
        // BOTTOM NAVIGATION
        // ==================================================

        setupBottomNavigation();
    }


    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private void setupBottomNavigation() {

        // ==================================================
        // HOME
        // ==================================================

        if (
                btnHome != null
        ) {

            btnHome.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                homepage.class
                        );
                    }
            );
        }


        // ==================================================
        // CREATE
        // ==================================================

        if (
                btnCreate != null
        ) {

            btnCreate.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                frontpage.class
                        );
                    }
            );
        }


        // ==================================================
        // MESSAGE
        // ==================================================

        if (
                btnMessage != null
        ) {

            btnMessage.setOnClickListener(
                    v -> {

                        // Already on Chat List.
                        hideKeyboard();
                    }
            );
        }


        // ==================================================
        // SEARCH
        // ==================================================

        if (
                btnSearch != null
        ) {

            btnSearch.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                Search.class
                        );
                    }
            );
        }


        // ==================================================
        // PROFILE
        // ==================================================

        if (
                btnProfile != null
        ) {

            btnProfile.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                profilepage.class
                        );
                    }
            );
        }
    }


    // ==================================================
    // OPEN ACTIVITY
    // ==================================================

    private void openActivity(
            Class<?> targetActivity
    ) {

        Intent intent =
                new Intent(
                        ChatListActivity.this,
                        targetActivity
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(
                intent
        );

        overridePendingTransition(
                0,
                0
        );
    }


    // ==================================================
    // LOAD CHATS
    // ==================================================

    private void loadChats() {

        if (
                mAuth == null ||
                        mAuth.getCurrentUser() == null
        ) {

            return;
        }


        String currentUid =
                mAuth.getCurrentUser()
                        .getUid();


        // ==================================================
        // NEW GENERATION
        // ==================================================

        chatLoadGeneration++;

        final int thisGeneration =
                chatLoadGeneration;


        db.collection("chats")
                .addSnapshotListener(
                        (snapshot, error) -> {

                            // ==========================================
                            // ERROR
                            // ==========================================

                            if (
                                    error != null
                            ) {

                                Toast.makeText(
                                        ChatListActivity.this,
                                        "Could not load chats",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // ==========================================
                            // NULL SNAPSHOT
                            // ==========================================

                            if (
                                    snapshot == null
                            ) {

                                return;
                            }


                            // ==========================================
                            // IGNORE OLD RESULT
                            // ==========================================

                            if (
                                    thisGeneration !=
                                            chatLoadGeneration
                            ) {

                                return;
                            }


                            // ==========================================
                            // UNIQUE CHATS
                            // ==========================================

                            Map<String, ChatItem> uniqueChats =
                                    new HashMap<>();


                            List<DocumentSnapshot> validChats =
                                    new ArrayList<>();


                            // ==========================================
                            // FIND VALID CHATS
                            // ==========================================

                            for (
                                    DocumentSnapshot document :
                                    snapshot.getDocuments()
                            ) {

                                String chatId =
                                        document.getId();


                                String otherUserId =
                                        getOtherUserId(
                                                document,
                                                currentUid,
                                                chatId
                                        );


                                if (
                                        otherUserId == null ||
                                                otherUserId.isEmpty()
                                ) {

                                    continue;
                                }


                                validChats.add(
                                        document
                                );
                            }


                            // ==========================================
                            // NO CHATS
                            // ==========================================

                            if (
                                    validChats.isEmpty()
                            ) {

                                chatList.clear();

                                chatListAdapter.updateList(
                                        chatList
                                );

                                return;
                            }


                            // ==========================================
                            // REMAINING
                            // ==========================================

                            final int[] remaining =
                                    {
                                            validChats.size()
                                    };


                            // ==========================================
                            // LOAD EVERY CHAT
                            // ==========================================

                            for (
                                    DocumentSnapshot chatDocument :
                                    validChats
                            ) {

                                String chatId =
                                        chatDocument.getId();


                                String otherUserId =
                                        getOtherUserId(
                                                chatDocument,
                                                currentUid,
                                                chatId
                                        );


                                int unreadCount =
                                        getUnreadCount(
                                                chatDocument,
                                                currentUid
                                        );


                                loadChatUser(
                                        chatDocument,
                                        chatId,
                                        otherUserId,
                                        unreadCount,
                                        uniqueChats,
                                        thisGeneration,
                                        remaining
                                );
                            }
                        }
                );
    }


    // ==================================================
    // LOAD CHAT USER
    // ==================================================

    private void loadChatUser(
            DocumentSnapshot chatDocument,
            String chatId,
            String otherUserId,
            int unreadCount,
            Map<String, ChatItem> uniqueChats,
            int thisGeneration,
            int[] remaining
    ) {

        db.collection("users")
                .document(otherUserId)
                .get()
                .addOnSuccessListener(
                        userDocument -> {

                            if (
                                    thisGeneration !=
                                            chatLoadGeneration
                            ) {

                                return;
                            }


                            String username =
                                    "Unknown User";


                            if (
                                    userDocument.exists()
                            ) {

                                String userId =
                                        userDocument.getString(
                                                "userId"
                                        );


                                if (
                                        userId != null &&
                                                !userId.isEmpty()
                                ) {

                                    username =
                                            userId;

                                } else {

                                    String oldUsername =
                                            userDocument.getString(
                                                    "username"
                                            );


                                    if (
                                            oldUsername != null &&
                                                    !oldUsername.isEmpty()
                                    ) {

                                        username =
                                                oldUsername;
                                    }
                                }
                            }


                            // ==========================================
                            // LOAD REAL LATEST VISIBLE MESSAGE
                            // ==========================================

                            loadLatestVisibleMessage(
                                    chatDocument,
                                    chatId,
                                    otherUserId,
                                    username,
                                    unreadCount,
                                    uniqueChats,
                                    thisGeneration,
                                    remaining
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            if (
                                    thisGeneration !=
                                            chatLoadGeneration
                            ) {

                                return;
                            }


                            loadLatestVisibleMessage(
                                    chatDocument,
                                    chatId,
                                    otherUserId,
                                    "Unknown User",
                                    unreadCount,
                                    uniqueChats,
                                    thisGeneration,
                                    remaining
                            );
                        }
                );
    }


    // ==================================================
    // LOAD LATEST VISIBLE MESSAGE
    // ==================================================

    private void loadLatestVisibleMessage(
            DocumentSnapshot chatDocument,
            String chatId,
            String otherUserId,
            String username,
            int unreadCount,
            Map<String, ChatItem> uniqueChats,
            int thisGeneration,
            int[] remaining
    ) {

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy(
                        "timestamp",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(
                        messageSnapshot -> {

                            if (
                                    thisGeneration !=
                                            chatLoadGeneration
                            ) {

                                return;
                            }


                            DocumentSnapshot latestVisible =
                                    null;


                            // ==========================================
                            // FIND NEWEST NON-UNSENT MESSAGE
                            // ==========================================

                            for (
                                    DocumentSnapshot messageDocument :
                                    messageSnapshot.getDocuments()
                            ) {

                                if (
                                        isMessageUnsent(
                                                messageDocument
                                        )
                                ) {

                                    continue;
                                }


                                latestVisible =
                                        messageDocument;

                                break;
                            }


                            String lastMessage =
                                    "No messages yet";


                            long lastMessageTime =
                                    0L;


                            // ==========================================
                            // MESSAGE FOUND
                            // ==========================================

                            if (
                                    latestVisible != null
                            ) {

                                lastMessage =
                                        getMessagePreview(
                                                latestVisible
                                        );


                                Long timestamp =
                                        latestVisible.getLong(
                                                "timestamp"
                                        );


                                if (
                                        timestamp != null
                                ) {

                                    lastMessageTime =
                                            timestamp;
                                }
                            }


                            // ==========================================
                            // CREATE CHAT ITEM
                            // ==========================================

                            ChatItem chatItem =
                                    new ChatItem(
                                            otherUserId,
                                            username,
                                            lastMessage,
                                            lastMessageTime,
                                            unreadCount
                                    );


                            // ==========================================
                            // PREVENT DUPLICATE CHAT
                            // ==========================================

                            ChatItem existing =
                                    uniqueChats.get(
                                            otherUserId
                                    );


                            if (
                                    existing == null
                            ) {

                                uniqueChats.put(
                                        otherUserId,
                                        chatItem
                                );

                            } else if (
                                    chatItem.getLastMessageTime()
                                            >
                                            existing
                                                    .getLastMessageTime()
                            ) {

                                uniqueChats.put(
                                        otherUserId,
                                        chatItem
                                );
                            }


                            finishChatLoad(
                                    uniqueChats,
                                    thisGeneration,
                                    remaining
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            if (
                                    thisGeneration !=
                                            chatLoadGeneration
                            ) {

                                return;
                            }


                            // ==========================================
                            // FALLBACK TO CHAT SUMMARY
                            // ==========================================

                            String lastMessage =
                                    chatDocument.getString(
                                            "lastMessage"
                                    );


                            Long time =
                                    chatDocument.getLong(
                                            "lastMessageTime"
                                    );


                            if (
                                    lastMessage == null ||
                                            lastMessage.isEmpty()
                            ) {

                                lastMessage =
                                        "No messages yet";
                            }


                            long timestamp =
                                    time != null
                                            ? time
                                            : 0L;


                            ChatItem chatItem =
                                    new ChatItem(
                                            otherUserId,
                                            username,
                                            lastMessage,
                                            timestamp,
                                            unreadCount
                                    );


                            ChatItem existing =
                                    uniqueChats.get(
                                            otherUserId
                                    );


                            if (
                                    existing == null ||
                                            chatItem.getLastMessageTime()
                                                    >
                                                    existing
                                                            .getLastMessageTime()
                            ) {

                                uniqueChats.put(
                                        otherUserId,
                                        chatItem
                                );
                            }


                            finishChatLoad(
                                    uniqueChats,
                                    thisGeneration,
                                    remaining
                            );
                        }
                );
    }


    // ==================================================
    // MESSAGE PREVIEW
    // ==================================================

    private String getMessagePreview(
            DocumentSnapshot document
    ) {

        String messageType =
                document.getString(
                        "messageType"
                );


        // ==========================================
        // PHOTO
        // ==========================================

        if (
                "image".equalsIgnoreCase(
                        messageType
                )
        ) {

            return "📷 Photo";
        }


        // ==========================================
        // VIDEO
        // ==========================================

        if (
                "video".equalsIgnoreCase(
                        messageType
                )
        ) {

            return "🎥 Video";
        }


        // ==========================================
        // TEXT
        // ==========================================

        String text =
                document.getString(
                        "message"
                );


        if (
                text == null ||
                        text.trim().isEmpty()
        ) {

            return "Message";
        }


        return text;
    }


    // ==================================================
    // CHECK UNSENT MESSAGE
    // ==================================================

    private boolean isMessageUnsent(
            DocumentSnapshot document
    ) {

        // ==========================================
        // NEW FIELD
        // ==========================================

        Boolean unsent =
                document.getBoolean(
                        "unsent"
                );


        if (
                unsent != null &&
                        unsent
        ) {

            return true;
        }


        // ==========================================
        // OLD FIELD
        // ==========================================

        Boolean oldUnsent =
                document.getBoolean(
                        "isUnsent"
                );


        return oldUnsent != null &&
                oldUnsent;
    }


    // ==================================================
    // FINISH CHAT LOADING
    // ==================================================

    private void finishChatLoad(
            Map<String, ChatItem> uniqueChats,
            int thisGeneration,
            int[] remaining
    ) {

        remaining[0]--;


        if (
                remaining[0] > 0
        ) {

            return;
        }


        if (
                thisGeneration !=
                        chatLoadGeneration
        ) {

            return;
        }


        // ==========================================
        // REBUILD LIST
        // ==========================================

        chatList.clear();

        chatList.addAll(
                uniqueChats.values()
        );


        // ==========================================
        // SORT LATEST FIRST
        // ==========================================

        sortChats();


        // ==========================================
        // UPDATE ADAPTER
        // ==========================================

        chatListAdapter.updateList(
                chatList
        );
    }


    // ==================================================
    // FIND OTHER USER
    // ==================================================

    private String getOtherUserId(
            DocumentSnapshot document,
            String currentUid,
            String chatId
    ) {

        String senderId =
                document.getString(
                        "senderId"
                );


        String receiverId =
                document.getString(
                        "receiverId"
                );


        // ==========================================
        // NORMAL CASE
        // ==========================================

        if (
                senderId != null &&
                        receiverId != null
        ) {

            if (
                    currentUid.equals(
                            senderId
                    )
            ) {

                return receiverId;
            }


            if (
                    currentUid.equals(
                            receiverId
                    )
            ) {

                return senderId;
            }
        }


        // ==========================================
        // CHAT ID FALLBACK
        // ==========================================

        String[] parts =
                chatId.split(
                        "_"
                );


        if (
                parts.length == 2
        ) {

            if (
                    currentUid.equals(
                            parts[0]
                    )
            ) {

                return parts[1];
            }


            if (
                    currentUid.equals(
                            parts[1]
                    )
            ) {

                return parts[0];
            }
        }


        return null;
    }


    // ==================================================
    // GET UNREAD COUNT
    // ==================================================

    private int getUnreadCount(
            DocumentSnapshot document,
            String currentUid
    ) {

        String fieldName =
                "unread_" +
                        currentUid;


        Long unread =
                document.getLong(
                        fieldName
                );


        if (
                unread == null
        ) {

            return 0;
        }


        return unread.intValue();
    }


    // ==================================================
    // SORT CHATS
    // ==================================================

    private void sortChats() {

        Collections.sort(
                chatList,
                new Comparator<ChatItem>() {

                    @Override
                    public int compare(
                            ChatItem first,
                            ChatItem second
                    ) {

                        return Long.compare(
                                second.getLastMessageTime(),
                                first.getLastMessageTime()
                        );
                    }
                }
        );
    }


    // ==================================================
    // SEARCH USER
    // ==================================================

    private void searchUser() {

        String searchText =
                searchUserEditText
                        .getText()
                        .toString()
                        .trim();


        // ==========================================
        // EMPTY
        // ==========================================

        if (
                searchText.isEmpty()
        ) {

            searchUserEditText.setError(
                    "Enter User ID"
            );

            searchUserEditText.requestFocus();

            return;
        }


        // ==========================================
        // LOGIN
        // ==========================================

        if (
                mAuth.getCurrentUser() == null
        ) {

            searchUserEditText.setError(
                    "Please login first"
            );

            return;
        }


        String searchKey =
                searchText.toLowerCase();


        // ==========================================
        // SEARCH USER ID
        // ==========================================

        db.collection("usernames")
                .document(
                        searchKey
                )
                .get()
                .addOnSuccessListener(
                        document -> {

                            // ==========================================
                            // USER NOT FOUND
                            // ==========================================

                            if (
                                    !document.exists()
                            ) {

                                searchUserEditText.setError(
                                        "User ID not found"
                                );

                                searchUserEditText.requestFocus();

                                return;
                            }


                            // ==========================================
                            // UID
                            // ==========================================

                            String uid =
                                    document.getString(
                                            "uid"
                                    );


                            if (
                                    uid == null ||
                                            uid.isEmpty()
                            ) {

                                searchUserEditText.setError(
                                        "User data is incomplete"
                                );

                                return;
                            }


                            // ==========================================
                            // USERNAME
                            // ==========================================

                            String username =
                                    document.getString(
                                            "username"
                                    );


                            if (
                                    username == null ||
                                            username.isEmpty()
                            ) {

                                username =
                                        searchText;
                            }


                            // ==========================================
                            // DON'T CHAT WITH YOURSELF
                            // ==========================================

                            String currentUid =
                                    mAuth.getCurrentUser()
                                            .getUid();


                            if (
                                    uid.equals(
                                            currentUid
                                    )
                            ) {

                                searchUserEditText.setError(
                                        "You cannot chat with yourself"
                                );

                                searchUserEditText.requestFocus();

                                return;
                            }


                            // ==========================================
                            // OPEN CHAT
                            // ==========================================

                            User user =
                                    new User(
                                            uid,
                                            username
                                    );


                            openChatFromUser(
                                    user
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            searchUserEditText.setError(
                                    "Could not search User ID"
                            );

                            searchUserEditText.requestFocus();
                        }
                );
    }


    // ==================================================
    // OPEN CHAT FROM CHAT ITEM
    // ==================================================

    private void openChat(
            ChatItem chatItem
    ) {

        if (chatItem == null) {
            return;
        }

        hideKeyboard();

        Intent intent =
                new Intent(
                        ChatListActivity.this,
                        ChatActivity.class
                );

        intent.putExtra(
                "receiverId",
                chatItem.getUid()
        );

        intent.putExtra(
                "receiverUsername",
                chatItem.getUsername()
        );

        // ==================================================
        // SHARED POST
        // ==================================================

        if (sharePost) {

            intent.putExtra(
                    "sharePost",
                    true
            );

            intent.putExtra(
                    "sharedPostId",
                    sharedPostId
            );

            intent.putExtra(
                    "sharedPostImage",
                    sharedPostImage
            );

            intent.putExtra(
                    "sharedPostCaption",
                    sharedPostCaption
            );
        }

        startActivity(intent);
    }


    // ==================================================
    // OPEN CHAT FROM SEARCH
    // ==================================================

    private void openChatFromUser(
            User user
    ) {

        if (user == null) {
            return;
        }

        hideKeyboard();

        Intent intent =
                new Intent(
                        ChatListActivity.this,
                        ChatActivity.class
                );

        intent.putExtra(
                "receiverId",
                user.getUid()
        );

        intent.putExtra(
                "receiverUsername",
                user.getUsername()
        );

        // ==================================================
        // SHARED POST
        // ==================================================

        if (sharePost) {

            intent.putExtra(
                    "sharePost",
                    true
            );

            intent.putExtra(
                    "sharedPostId",
                    sharedPostId
            );

            intent.putExtra(
                    "sharedPostImage",
                    sharedPostImage
            );

            intent.putExtra(
                    "sharedPostCaption",
                    sharedPostCaption
            );
        }

        startActivity(intent);
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
    // ON RESUME
    // ==================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (
                mAuth != null &&
                        mAuth.getCurrentUser() != null
        ) {

            loadChats();
        }
    }
}
