package com.example.crisistech;

import android.graphics.Insets;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_IS_GROUP_OWNER = "extra_is_group_owner";
    public static final String EXTRA_HOST_ADDRESS = "extra_host_address";

    private static final int PORT = 8888;

    private ListView messagesListView;
    private EditText messageInput;
    private Button sendButton;

    private final ArrayList<String> messages = new ArrayList<>();
    private ArrayAdapter<String> messagesAdapter;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile Socket socket;
    private volatile PrintWriter writer;
    private volatile boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        View mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (view, insets) -> {
            // Explicitly use the AndroidX Insets type to bypass import conflicts
            androidx.core.graphics.Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int bottomPadding = Math.max(imeInsets.bottom, systemBars.bottom);

            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    bottomPadding
            );

            return insets;
        });

        messagesListView = findViewById(R.id.chat_messages_list);
        messageInput = findViewById(R.id.chat_message_input);
        sendButton = findViewById(R.id.chat_send_button);

        messagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messagesListView.setAdapter(messagesAdapter);

        boolean isGroupOwner = getIntent().getBooleanExtra(EXTRA_IS_GROUP_OWNER, false);
        String hostAddress = getIntent().getStringExtra(EXTRA_HOST_ADDRESS);

        sendButton.setOnClickListener(v -> sendMessage());

        if (isGroupOwner) {
            addSystemMessage("Waiting for peer to connect...");
            executor.execute(this::runAsServer);
        } else {
            if (TextUtils.isEmpty(hostAddress)) {
                Toast.makeText(this, "No host address available", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            addSystemMessage("Connecting to host...");
            executor.execute(() -> runAsClient(hostAddress));
        }
    }

    private void runAsServer() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT));
            Socket client = serverSocket.accept();
            onSocketReady(client);
        } catch (IOException e) {
            postError("Server error: " + e.getMessage());
        }
    }

    private void runAsClient(String hostAddress) {
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(hostAddress, PORT), 15000);
            onSocketReady(clientSocket);
        } catch (IOException e) {
            postError("Connect error: " + e.getMessage());
        }
    }

    private void onSocketReady(Socket connectedSocket) {
        this.socket = connectedSocket;
        try {
            OutputStream out = connectedSocket.getOutputStream();
            writer = new PrintWriter(out, true);

            runOnUiThread(() -> addSystemMessage("Connected."));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));
            String line;
            while (running && (line = reader.readLine()) != null) {
                final String received = line;
                runOnUiThread(() -> addMessage("Peer: " + received));
            }
        } catch (IOException e) {
            postError("Connection lost: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        if (writer == null) {
            Toast.makeText(this, "Not connected yet", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            writer.println(text);
        });

        addMessage("Me: " + text);
        messageInput.setText("");
    }

    private void addMessage(String text) {
        messages.add(text);
        messagesAdapter.notifyDataSetChanged();
        messagesListView.smoothScrollToPosition(messages.size() - 1);
    }

    private void addSystemMessage(String text) {
        addMessage("[System] " + text);
    }

    private void postError(String text) {
        runOnUiThread(() -> {
            addSystemMessage(text);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        executor.shutdownNow();
    }
}