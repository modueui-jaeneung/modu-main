package com.modu.ClientViewServer.chat;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRoomList {
    private List<ChatRoomDto> rooms;

    public ChatRoomList() {
        rooms = new ArrayList<>();
    }
}
