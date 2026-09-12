package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

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

    private TextView currentUserIdText;

    private RecyclerView chatListRecyclerView;

    private ImageButton btnHome;
    private ImageButton btnCreate;
    private ImageButton btnMessage;
    private ImageButton btnSearch;
    private ImageButton btnProfile;

    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // ==================================================
    // DATA
    // ==================================================

    private final List<ChatItem> chatList =
            new ArrayList<>();

    private ChatListAdapter chatListAdapter;

    // ==================================================
    // LISTENER
    // ==================================================

    private ListenerRegistration chatsListener;

    // ==================================================
    // CACHE
    // ==================================================

    private final Map<String, UserCache> userCache =
            new HashMap<>();

    // ==================================================
    // CREATE
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
        // ROOT INSETS
        // ==================================================

        View root =
                findViewById(
                        R.id.chatListRoot
                );

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
        // VIEWS
        // ==================================================

        searchUserEditText =
                findViewById(
                        R.id.searchUserEditText
                );

        searchUserButton =
                findViewById(
                        R.id.searchUserButton
                );

        currentUserIdText =
                findViewById(
                        R.id.currentUserIdText
                );

        chatListRecyclerView =
                findViewById(
                        R.id.chatListRecyclerView
                );

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

        // ==================================================
        // ADAPTER
        // ==================================================

        chatListAdapter =
                new ChatListAdapter(
                        chatList,
                        this::openChat
                );

        chatListRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );

        chatListRecyclerView.setAdapter(
                chatListAdapter
        );

        chatListRecyclerView.setHasFixedSize(
                true
        );

        chatListRecyclerView.setItemViewCacheSize(
                10
        );

        // ==================================================
        // SEARCH
        // ==================================================

        setupSearch();

        // ==================================================
        // NAVIGATION
        // ==================================================

        setupBottomNavigation();

        // ==================================================
        // CURRENT USER
        // ==================================================

        loadCurrentUser();

        // ==================================================
        // CHATS
        // ==================================================

        startChatsListener();
    }

    // ==================================================
    // CURRENT USER
    // ==================================================

    private void loadCurrentUser() {

        if (
                currentUserIdText == null
        ) {
            return;
        }

        if (
                mAuth.getCurrentUser() == null
        ) {

            currentUserIdText.setText(
                    "User"
            );

            return;
        }

        String currentUid =
                mAuth.getCurrentUser()
                        .getUid();

        db.collection("users")
                .document(currentUid)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (
                                    !document.exists()
                            ) {

                                currentUserIdText.setText(
                                        "User"
                                );

                                return;
                            }

                            String name =
                                    firstNonEmpty(
                                            document.getString("name"),
                                            document.getString("fullName"),
                                            document.getString("displayName")
                                    );

                            String userId =
                                    firstNonEmpty(
                                            document.getString("userId"),
                                            document.getString("username"),
                                            document.getString("uniqueUserId")
                                    );

                            if (
                                    name != null
                            ) {

                                currentUserIdText.setText(
                                        name
                                );

                            } else if (
                                    userId != null
                            ) {

                                currentUserIdText.setText(
                                        userId
                                );

                            } else {

                                currentUserIdText.setText(
                                        "User"
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            currentUserIdText.setText(
                                    "User"
                            );
                        }
                );
    }

    // ==================================================
    // CHAT LISTENER
    // ==================================================

    private void startChatsListener() {

        if (
                mAuth.getCurrentUser() == null
        ) {
            return;
        }

        // Prevent duplicate listener
        stopChatsListener();

        final String currentUid =
                mAuth.getCurrentUser()
                        .getUid();

        chatsListener =
                db.collection("chats")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            isFinishing() ||
                                                    isDestroyed()
                                    ) {
                                        return;
                                    }

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

                                    if (
                                            snapshot == null
                                    ) {
                                        return;
                                    }

                                    processChatSnapshot(
                                            snapshot.getDocuments(),
                                            currentUid
                                    );
                                }
                        );
    }

    // ==================================================
    // STOP LISTENER
    // ==================================================

    private void stopChatsListener() {

        if (
                chatsListener != null
        ) {

            chatsListener.remove();

            chatsListener =
                    null;
        }
    }

    // ==================================================
    // PROCESS CHATS
    // ==================================================

    private void processChatSnapshot(
            List<DocumentSnapshot> documents,
            String currentUid
    ) {

        final Map<String, ChatItem> uniqueChats =
                new HashMap<>();

        final List<String> userIdsToLoad =
                new ArrayList<>();

        // ==================================================
        // READ ONLY CHAT SUMMARY
        // ==================================================

        for (
                DocumentSnapshot document :
                documents
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

            // ------------------------------------------
            // SUMMARY
            // ------------------------------------------

            String lastMessage =
                    document.getString(
                            "lastMessage"
                    );

            if (
                    lastMessage == null ||
                            lastMessage.trim().isEmpty()
            ) {

                lastMessage =
                        "No messages yet";
            }

            Long timeValue =
                    document.getLong(
                            "lastMessageTime"
                    );

            long lastMessageTime =
                    timeValue != null
                            ? timeValue
                            : 0L;

            int unreadCount =
                    getUnreadCount(
                            document,
                            currentUid
                    );

            // ------------------------------------------
            // CREATE TEMP ITEM
            // ------------------------------------------

            ChatItem item =
                    new ChatItem(
                            otherUserId,
                            "",
                            "",
                            "",
                            lastMessage,
                            lastMessageTime,
                            unreadCount
                    );

            uniqueChats.put(
                    otherUserId,
                    item
            );

            if (
                    !userIdsToLoad.contains(
                            otherUserId
                    )
            ) {

                userIdsToLoad.add(
                        otherUserId
                );
            }
        }

        // ==================================================
        // NO CHATS
        // ==================================================

        if (
                userIdsToLoad.isEmpty()
        ) {

            chatList.clear();

            chatListAdapter.updateList(
                    new ArrayList<>()
            );

            return;
        }

        // ==================================================
        // LOAD USER DATA
        // ==================================================

        final int[] remaining =
                {
                        userIdsToLoad.size()
                };

        for (
                String userId :
                userIdsToLoad
        ) {

            loadUserData(
                    userId,
                    uniqueChats,
                    remaining
            );
        }
    }

    // ==================================================
    // LOAD USER DATA
    // ==================================================

    private void loadUserData(
            String userId,
            Map<String, ChatItem> uniqueChats,
            int[] remaining
    ) {

        // ==================================================
        // CACHE
        // ==================================================

        UserCache cached =
                userCache.get(
                        userId
                );

        if (
                cached != null
        ) {

            applyUserCache(
                    userId,
                    cached,
                    uniqueChats
            );

            finishUserLoad(
                    remaining,
                    uniqueChats
            );

            return;
        }

        // ==================================================
        // FIRESTORE
        // ==================================================

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (
                                    document.exists()
                            ) {

                                String username =
                                        firstNonEmpty(
                                                document.getString("userId"),
                                                document.getString("username"),
                                                document.getString("uniqueUserId")
                                        );

                                String name =
                                        firstNonEmpty(
                                                document.getString("name"),
                                                document.getString("fullName"),
                                                document.getString("displayName")
                                        );

                                String profileImage =
                                        firstNonEmpty(
                                                document.getString("profileImage"),
                                                document.getString("profileImageUrl"),
                                                document.getString("photoURL"),
                                                document.getString("photoUrl")
                                        );

                                if (
                                        username == null
                                ) {
                                    username =
                                            "Unknown User";
                                }

                                if (
                                        name == null
                                ) {
                                    name =
                                            username;
                                }

                                if (
                                        profileImage == null
                                ) {
                                    profileImage =
                                            "";
                                }

                                UserCache cache =
                                        new UserCache(
                                                username,
                                                name,
                                                profileImage
                                        );

                                userCache.put(
                                        userId,
                                        cache
                                );

                                applyUserCache(
                                        userId,
                                        cache,
                                        uniqueChats
                                );

                            } else {

                                applyDefaultUser(
                                        userId,
                                        uniqueChats
                                );
                            }

                            finishUserLoad(
                                    remaining,
                                    uniqueChats
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            applyDefaultUser(
                                    userId,
                                    uniqueChats
                            );

                            finishUserLoad(
                                    remaining,
                                    uniqueChats
                            );
                        }
                );
    }

    // ==================================================
    // APPLY CACHE
    // ==================================================

    private void applyUserCache(
            String userId,
            UserCache cache,
            Map<String, ChatItem> uniqueChats
    ) {

        ChatItem item =
                uniqueChats.get(
                        userId
                );

        if (
                item == null ||
                        cache == null
        ) {
            return;
        }

        item.setUsername(
                cache.username
        );

        item.setName(
                cache.name
        );

        item.setProfileImage(
                cache.profileImage
        );
    }

    // ==================================================
    // DEFAULT USER
    // ==================================================

    private void applyDefaultUser(
            String userId,
            Map<String, ChatItem> uniqueChats
    ) {

        ChatItem item =
                uniqueChats.get(
                        userId
                );

        if (
                item == null
        ) {
            return;
        }

        item.setUsername(
                "Unknown User"
        );

        item.setName(
                "Unknown User"
        );

        item.setProfileImage(
                ""
        );
    }

    // ==================================================
    // FINISH USER LOAD
    // ==================================================

    private void finishUserLoad(
            int[] remaining,
            Map<String, ChatItem> uniqueChats
    ) {

        remaining[0]--;

        if (
                remaining[0] > 0
        ) {
            return;
        }

        chatList.clear();

        chatList.addAll(
                uniqueChats.values()
        );

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

        chatListAdapter.updateList(
                new ArrayList<>(
                        chatList
                )
        );
    }

    // ==================================================
    // OTHER USER
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

        // ==================================================
        // NORMAL
        // ==================================================

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

        // ==================================================
        // FALLBACK
        // ==================================================

        if (
                chatId != null &&
                        !chatId.isEmpty()
        ) {

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
        }

        return null;
    }

    // ==================================================
    // UNREAD
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

        return Math.max(
                unread.intValue(),
                0
        );
    }

    // ==================================================
    // SEARCH
    // ==================================================

    private void setupSearch() {

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
    }

    // ==================================================
    // SEARCH USER
    // ==================================================

    private void searchUser() {

        if (
                mAuth.getCurrentUser() == null
        ) {

            searchUserEditText.setError(
                    "Please login first"
            );

            return;
        }

        String searchText =
                searchUserEditText
                        .getText()
                        .toString()
                        .trim();

        if (
                searchText.isEmpty()
        ) {

            searchUserEditText.setError(
                    "Enter User ID"
            );

            searchUserEditText.requestFocus();

            return;
        }

        String searchKey =
                searchText.toLowerCase(
                        java.util.Locale.getDefault()
                );

        db.collection("usernames")
                .document(searchKey)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (
                                    !document.exists()
                            ) {

                                searchUserEditText.setError(
                                        "User ID not found"
                                );

                                searchUserEditText.requestFocus();

                                return;
                            }

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
    // OPEN CHAT
    // ==================================================

    private void openChat(
            ChatItem chatItem
    ) {

        if (
                chatItem == null
        ) {
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

        startActivity(
                intent
        );
    }

    // ==================================================
    // OPEN CHAT FROM SEARCH
    // ==================================================

    private void openChatFromUser(
            User user
    ) {

        if (
                user == null
        ) {
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

        startActivity(
                intent
        );
    }

    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private void setupBottomNavigation() {

        if (btnHome != null) {

            btnHome.setOnClickListener(
                    v -> openActivity(
                            homepage.class
                    )
            );
        }

        if (btnCreate != null) {

            btnCreate.setOnClickListener(
                    v -> openActivity(
                            frontpage.class
                    )
            );
        }

        if (btnMessage != null) {

            btnMessage.setOnClickListener(
                    v -> {
                        // Already here.
                    }
            );
        }

        if (btnSearch != null) {

            btnSearch.setOnClickListener(
                    v -> openActivity(
                            Search.class
                    )
            );
        }

        if (btnProfile != null) {

            btnProfile.setOnClickListener(
                    v -> openActivity(
                            profilepage.class
                    )
            );
        }
    }

    // ==================================================
    // OPEN ACTIVITY
    // ==================================================

    private void openActivity(
            Class<?> target
    ) {

        Intent intent =
                new Intent(
                        ChatListActivity.this,
                        target
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
    // RESUME
    // ==================================================

    @Override
    protected void onResume() {

        super.onResume();

        loadCurrentUser();

        // Do NOT attach another listener here.
        // Existing listener remains active.
        if (
                chatsListener == null
        ) {
            startChatsListener();
        }
    }

    // ==================================================
    // STOP
    // ==================================================

    @Override
    protected void onStop() {

        super.onStop();

        stopChatsListener();
    }

    // ==================================================
    // DESTROY
    // ==================================================

    @Override
    protected void onDestroy() {

        stopChatsListener();

        super.onDestroy();
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
    // FIRST NON-EMPTY
    // ==================================================

    private String firstNonEmpty(
            String... values
    ) {

        if (
                values == null
        ) {
            return null;
        }

        for (
                String value :
                values
        ) {

            if (
                    value != null &&
                            !value.trim().isEmpty()
            ) {

                return value.trim();
            }
        }

        return null;
    }

    // ==================================================
    // USER CACHE
    // ==================================================

    private static class UserCache {

        String username;
        String name;
        String profileImage;

        UserCache(
                String username,
                String name,
                String profileImage
        ) {

            this.username =
                    username;

            this.name =
                    name;

            this.profileImage =
                    profileImage;
        }
    }
}