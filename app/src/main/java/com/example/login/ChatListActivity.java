package com.example.login;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {

    // ==================================================
    // SEARCH
    // ==================================================

    private EditText searchUserEditText;

    private ImageButton searchUserButton;

    // ==================================================
    // CHAT LIST
    // ==================================================

    private RecyclerView chatListRecyclerView;

    private List<ChatItem> chatList;

    private ChatListAdapter chatListAdapter;

    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    // ==================================================
    // CHAT LOAD GENERATION
    // ==================================================

    private int chatLoadGeneration = 0;

    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private ImageButton btnHome;
    private ImageButton btnCreate;
    private ImageButton btnMessage;
    private ImageButton btnSearch;
    private ImageButton btnProfile;

    // ==================================================
    // ROOT / CONTENT
    // ==================================================

    private View chatListRoot;
    private View mainContent;
    private View bottomNavigation;

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

        // ==================================================
        // KEYBOARD BEHAVIOUR
        // ==================================================

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams
                        .SOFT_INPUT_ADJUST_RESIZE
        );

        // ==================================================
        // LAYOUT
        // ==================================================

        setContentView(
                R.layout.activity_chat_list
        );

        // ==================================================
        // FIREBASE
        // ==================================================

        db =
                FirebaseFirestore.getInstance();

        mAuth =
                FirebaseAuth.getInstance();

        // ==================================================
        // FIND VIEWS
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

        // ==================================================
        // ROOT / CONTENT
        // ==================================================

        chatListRoot =
                findViewById(
                        R.id.chatListRoot
                );

        mainContent =
                findViewById(
                        R.id.mainContent
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

        chatListRecyclerView.setClipToPadding(
                false
        );

        // ==================================================
        // LOAD CURRENT USER
        // ==================================================

        loadCurrentUserId();

        // ==================================================
        // SEARCH
        // ==================================================

        setupSearch();

        // ==================================================
        // BOTTOM NAVIGATION
        // ==================================================

        setupBottomNavigation();

        // ==================================================
        // SAFE AREA
        // CAMERA / NOTCH / NAVIGATION BAR
        // ==================================================

        setupWindowInsets();

        // ==================================================
        // LOAD CHATS
        // ==================================================

        loadChats();
    }

    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private void setupBottomNavigation() {

        // ==================================================
        // HOME
        // ==================================================

        btnHome.setOnClickListener(
                v -> {

                    hideKeyboard();

                    Intent intent =
                            new Intent(
                                    ChatListActivity.this,
                                    homepage.class
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
        );

        // ==================================================
        // CREATE
        // ==================================================

        btnCreate.setOnClickListener(
                v -> {

                    hideKeyboard();

                    Intent intent =
                            new Intent(
                                    ChatListActivity.this,
                                    frontpage.class
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
        );

        // ==================================================
        // MESSAGE
        // ==================================================

        btnMessage.setOnClickListener(
                v -> {

                    hideKeyboard();

                    // Already inside ChatListActivity.
                    // Do not create another ChatListActivity.
                    chatListRecyclerView.smoothScrollToPosition(
                            0
                    );
                }
        );

        // ==================================================
        // SEARCH
        // IMPORTANT:
        // Bottom Search opens Search Activity.
        // ==================================================

        btnSearch.setOnClickListener(
                v -> {

                    hideKeyboard();

                    Intent intent =
                            new Intent(
                                    ChatListActivity.this,
                                    Search.class
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
        );

        // ==================================================
        // PROFILE
        // ==================================================

        btnProfile.setOnClickListener(
                v -> {

                    hideKeyboard();

                    Intent intent =
                            new Intent(
                                    ChatListActivity.this,
                                    profilepage.class
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
        );
    }

    // ==================================================
    // WINDOW INSETS
    // ==================================================

    private void setupWindowInsets() {

        if (
                chatListRoot == null ||
                        mainContent == null ||
                        bottomNavigation == null
        ) {

            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                chatListRoot,
                (view, windowInsets) -> {

                    // ==================================================
                    // SYSTEM BARS
                    // ==================================================

                    Insets systemBars =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type
                                            .systemBars()
                            );

                    // ==================================================
                    // DISPLAY CUTOUT
                    // ==================================================

                    Insets cutout =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type
                                            .displayCutout()
                            );

                    // ==================================================
                    // KEYBOARD
                    // ==================================================

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

                    // ==================================================
                    // TOP SAFE AREA
                    // ==================================================

                    int safeTop =
                            Math.max(
                                    systemBars.top,
                                    cutout.top
                            );

                    // ==================================================
                    // BOTTOM SAFE AREA
                    // ==================================================

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
                    // MAIN CONTENT
                    // ==================================================

                    ViewGroup.MarginLayoutParams
                            contentParams =
                            (ViewGroup.MarginLayoutParams)
                                    mainContent.getLayoutParams();

                    contentParams.topMargin =
                            safeTop;

                    mainContent.setLayoutParams(
                            contentParams
                    );

                    // ==================================================
                    // BOTTOM NAVIGATION
                    // ==================================================

                    ViewGroup.MarginLayoutParams
                            bottomParams =
                            (ViewGroup.MarginLayoutParams)
                                    bottomNavigation.getLayoutParams();

                    bottomParams.bottomMargin =
                            safeBottom;

                    bottomNavigation.setLayoutParams(
                            bottomParams
                    );

                    // ==================================================
                    // CHAT LIST PADDING
                    // ==================================================

                    int listBottomPadding =
                            Math.max(
                                    safeBottom,
                                    dp(16)
                            );

                    chatListRecyclerView.setPadding(
                            dp(8),
                            dp(5),
                            dp(8),
                            listBottomPadding
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(
                chatListRoot
        );
    }

    // ==================================================
    // CURRENT USER ID
    // ==================================================

    private void loadCurrentUserId() {

        TextView currentUserIdText =
                findViewById(
                        R.id.currentUserIdText
                );

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

                            String userId =
                                    document.getString(
                                            "userId"
                                    );

                            if (
                                    userId == null ||
                                            userId.isEmpty()
                            ) {

                                userId =
                                        document.getString(
                                                "username"
                                        );
                            }

                            if (
                                    userId == null ||
                                            userId.isEmpty()
                            ) {

                                userId =
                                        "User";
                            }

                            currentUserIdText.setText(
                                    userId
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                currentUserIdText.setText(
                                        "User"
                                )
                );
    }

    // ==================================================
    // SEARCH SETUP
    // ==================================================

    private void setupSearch() {

        searchUserButton.setOnClickListener(
                v -> {

                    hideKeyboard();

                    searchUser();
                }
        );

        searchUserEditText.setOnEditorActionListener(
                (v, actionId, event) -> {

                    hideKeyboard();

                    searchUser();

                    return true;
                }
        );
    }

    // ==================================================
    // LOAD CHATS
    // ==================================================

    private void loadChats() {

        if (
                mAuth.getCurrentUser() == null
        ) {

            return;
        }

        final String currentUid =
                mAuth.getCurrentUser()
                        .getUid();

        db.collection("chats")
                .addSnapshotListener(
                        (snapshot, error) -> {

                            // ==========================================
                            // FIREBASE ERROR
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

                            if (
                                    snapshot == null
                            ) {

                                return;
                            }

                            // ==========================================
                            // NEW GENERATION
                            // ==========================================

                            chatLoadGeneration++;

                            final int thisGeneration =
                                    chatLoadGeneration;

                            // ==========================================
                            // UNIQUE CHAT MAP
                            // ==========================================

                            Map<String, ChatItem> uniqueChats =
                                    new HashMap<>();

                            // ==========================================
                            // COUNT VALID CHATS
                            // ==========================================

                            int validChatCount =
                                    0;

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

                                validChatCount++;
                            }

                            // ==========================================
                            // NO CHATS
                            // ==========================================

                            if (
                                    validChatCount == 0
                            ) {

                                chatList.clear();

                                chatListAdapter.updateList(
                                        chatList
                                );

                                return;
                            }

                            // ==========================================
                            // ASYNC COUNTER
                            // ==========================================

                            final int[] remaining =
                                    {
                                            validChatCount
                                    };

                            // ==========================================
                            // READ CHATS
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

                                // ==========================================
                                // LAST MESSAGE
                                // ==========================================

                                String lastMessage =
                                        document.getString(
                                                "lastMessage"
                                        );

                                // ==========================================
                                // LAST MESSAGE TIME
                                // ==========================================

                                Long timeValue =
                                        document.getLong(
                                                "lastMessageTime"
                                        );

                                long lastMessageTime =
                                        timeValue != null
                                                ? timeValue
                                                : 0;

                                // ==========================================
                                // UNREAD COUNT
                                // ==========================================

                                int unreadCount =
                                        getUnreadCount(
                                                document,
                                                currentUid
                                        );

                                // ==========================================
                                // LOAD USER
                                // ==========================================

                                loadChatUser(
                                        otherUserId,
                                        lastMessage,
                                        lastMessageTime,
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

        // ==================================================
        // NORMAL CASE
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
        // FALLBACK CHAT ID
        // ==================================================

        if (
                chatId != null &&
                        !chatId.isEmpty()
        ) {

            String[] parts =
                    chatId.split("_");

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
    // UNREAD COUNT
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
    // LOAD USER
    // ==================================================

    private void loadChatUser(
            String userId,
            String lastMessage,
            long lastMessageTime,
            int unreadCount,
            Map<String, ChatItem> uniqueChats,
            int thisGeneration,
            int[] remaining
    ) {

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            // ==========================================
                            // IGNORE OLD REQUEST
                            // ==========================================

                            if (
                                    thisGeneration !=
                                            chatLoadGeneration
                            ) {

                                return;
                            }

                            // ==========================================
                            // USER MISSING
                            // ==========================================

                            if (
                                    !document.exists()
                            ) {

                                finishUserLoad(
                                        uniqueChats,
                                        thisGeneration,
                                        remaining
                                );

                                return;
                            }

                            // ==========================================
                            // USERNAME
                            // ==========================================

                            String username =
                                    document.getString(
                                            "userId"
                                    );

                            if (
                                    username == null ||
                                            username.isEmpty()
                            ) {

                                username =
                                        document.getString(
                                                "username"
                                        );
                            }

                            if (
                                    username == null ||
                                            username.isEmpty()
                            ) {

                                username =
                                        "Unknown User";
                            }

                            // ==========================================
                            // CHAT ITEM
                            // ==========================================

                            ChatItem newChat =
                                    new ChatItem(
                                            userId,
                                            username,
                                            lastMessage,
                                            lastMessageTime,
                                            unreadCount
                                    );

                            // ==========================================
                            // DUPLICATE CHECK
                            // ==========================================

                            ChatItem existingChat =
                                    uniqueChats.get(
                                            userId
                                    );

                            if (
                                    existingChat == null
                            ) {

                                uniqueChats.put(
                                        userId,
                                        newChat
                                );

                            } else if (
                                    newChat.getLastMessageTime()
                                            >
                                            existingChat
                                                    .getLastMessageTime()
                            ) {

                                uniqueChats.put(
                                        userId,
                                        newChat
                                );
                            }

                            // ==========================================
                            // FINISH
                            // ==========================================

                            finishUserLoad(
                                    uniqueChats,
                                    thisGeneration,
                                    remaining
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                finishUserLoad(
                                        uniqueChats,
                                        thisGeneration,
                                        remaining
                                )
                );
    }

    // ==================================================
    // FINISH USER LOAD
    // ==================================================

    private void finishUserLoad(
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

        // ==================================================
        // REBUILD LIST
        // ==================================================

        chatList.clear();

        chatList.addAll(
                uniqueChats.values()
        );

        // ==================================================
        // SORT
        // ==================================================

        sortChats();

        // ==================================================
        // UPDATE ADAPTER
        // ==================================================

        chatListAdapter.updateList(
                chatList
        );
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

        // ==================================================
        // EMPTY SEARCH
        // ==================================================

        if (
                searchText.isEmpty()
        ) {

            searchUserEditText.setError(
                    "Enter User ID"
            );

            searchUserEditText.requestFocus();

            return;
        }

        // ==================================================
        // LOGIN CHECK
        // ==================================================

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

        // ==================================================
        // FIND USERNAME
        // ==================================================

        db.collection("usernames")
                .document(searchKey)
                .get()
                .addOnSuccessListener(
                        document -> {

                            // ==================================================
                            // NOT FOUND
                            // ==================================================

                            if (
                                    !document.exists()
                            ) {

                                searchUserEditText.setError(
                                        "User ID not found"
                                );

                                searchUserEditText.requestFocus();

                                return;
                            }

                            // ==================================================
                            // UID
                            // ==================================================

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

                            // ==================================================
                            // USERNAME
                            // ==================================================

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

                            // ==================================================
                            // SELF CHECK
                            // ==================================================

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

                            // ==================================================
                            // OPEN CHAT
                            // ==================================================

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
    // OPEN EXISTING CHAT
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
    // DP
    // ==================================================

    private int dp(
            int value
    ) {

        return (int)
                (
                        value *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                );
    }
}