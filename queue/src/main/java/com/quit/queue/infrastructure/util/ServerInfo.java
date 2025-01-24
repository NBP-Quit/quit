package com.quit.queue.infrastructure.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerInfo {

    @Value("${server.port}")
    private int serverPort;

    public String getServerId() {
        return "server-" + serverPort;
    }
}