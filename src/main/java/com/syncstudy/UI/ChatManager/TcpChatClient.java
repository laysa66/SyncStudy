// java
package com.syncstudy.UI.ChatManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syncstudy.BL.ChatManager.Message;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Minimal TCP client for JavaFX. Uses custom Gson with LocalDateTime adapter to avoid reflection/module issues.
 */
public class TcpChatClient {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Gson gson;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public TcpChatClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void connect(ChatController controller) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        running.set(true);

        Thread reader = new Thread(() -> {
            try {
                String line;
                while (running.get() && (line = in.readLine()) != null) {
                    final String json = line;
                    try {
                        EventEnvelope env = gson.fromJson(json, EventEnvelope.class);
                        Platform.runLater(() -> controller.handleRemoteEnvelope(env));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }, "TcpChatClient-Reader");
        reader.setDaemon(true);
        reader.start();
    }

    public void sendEvent(EventEnvelope envelope) {
        if (out != null) {
            out.println(gson.toJson(envelope));
        }
    }

    public void disconnect() {
        running.set(false);
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }

    // Simple envelope used by server/client
    public static class EventEnvelope {
        public String type; // "new", "edit", "delete"
        public Message message;
        public Long id;

        public EventEnvelope() {}

        public EventEnvelope(String type, Message message, Long id) {
            this.type = type;
            this.message = message;
            this.id = id;
        }
    }
}