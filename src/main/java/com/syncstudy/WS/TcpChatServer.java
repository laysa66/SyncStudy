// java
package com.syncstudy.WS;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TcpChatServer with extra logging to help debug missing-broadcast issues.
 * The `main` is only a convenience for running the server as a separate process
 * during development. Remove it if you start the server from your app instead.
 */
public class TcpChatServer {
    private final int port;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private final Gson gson = new Gson();

    public TcpChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("TcpChatServer listening on " + port);
            while (true) {
                Socket socket = server.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler, "TcpChatServer-ClientHandler").start();
                System.out.println("Client connected: " + socket.getRemoteSocketAddress() + " (clients=" + clients.size() + ")");
            }
        }
    }

    private int clientCount() {
        return clients.size();
    }

    private void broadcast(String json) {
        System.out.println("Broadcasting to " + clientCount() + " clients: " + (json.length() > 200 ? json.substring(0, 200) + "..." : json));
        for (ClientHandler ch : clients) {
            ch.safeSend(json);
        }
    }

    private void onClientMessage(String json, ClientHandler from) {
        try {
            Object envelope = gson.fromJson(json, Object.class);
            System.out.println("Received envelope from " + from.remoteAddr() + ": " + gson.toJson(envelope));
        } catch (Exception ex) {
            System.out.println("Received raw from " + from.remoteAddr() + ": " + json);
        }
        broadcast(json);
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final BufferedReader in;
        private final BufferedWriter writer;
        private volatile boolean open = true;

        ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        }

        @Override
        public void run() {
            try {
                String line;
                while (open && (line = in.readLine()) != null) {
                    onClientMessage(line, this);
                }
            } catch (IOException e) {
                System.err.println("Client read error (" + remoteAddr() + "): " + e.getMessage());
            } finally {
                close();
            }
        }

        synchronized void safeSend(String json) {
            if (!open) return;
            try {
                writer.write(json);
                writer.write("\n");
                writer.flush();
            } catch (IOException e) {
                System.err.println("Failed to send to " + remoteAddr() + ", removing: " + e.getMessage());
                close();
            }
        }

        String remoteAddr() {
            return socket.getRemoteSocketAddress() != null ? socket.getRemoteSocketAddress().toString() : "unknown";
        }

        void close() {
            if (!open) return;
            open = false;
            clients.remove(this);
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.out.println("Client disconnected: " + remoteAddr() + " (clients=" + clientCount() + ")");
        }
    }

    // quick manual run for development/testing; safe to keep or remove as you prefer
    public static void main(String[] args) throws IOException {
        int port = 9000;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        new TcpChatServer(port).start();
    }
}