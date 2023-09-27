package com.modu.ClientViewServer.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RestTemplate restTemplate;

    @GetMapping("/chat")
    public String chatIndex(Model model) {

        String userId = "참석자2";

        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8085")
                .path("/chat/rooms")
                .queryParam("userId", userId)
                .encode()
                .build()
                .toUri();

        ResponseEntity<ChatRoomDto[]> response = restTemplate.getForEntity(uri, ChatRoomDto[].class);

        ChatRoomDto[] rooms = response.getBody();
        model.addAttribute("rooms", rooms);
        for (ChatRoomDto room : rooms) {
            System.out.println("room = " + room.getRoomId());
        }

        return "chat";
    }

    @GetMapping("/chat/{roomId}")
    public String enterRoom(@PathVariable String roomId, Model model) {
        String userId = "참석자2";

        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8085")
                .path("/chat/rooms")
                .queryParam("userId", userId)
                .encode()
                .build()
                .toUri();

        String url = "http://localhost:8085/chat";

        // 채팅방 목록 가져오기
        ResponseEntity<ChatRoomDto[]> responseRooms = restTemplate.getForEntity(uri, ChatRoomDto[].class);

        ChatRoomDto[] rooms = responseRooms.getBody();
        model.addAttribute("rooms", rooms);

        for (ChatRoomDto room : rooms) {
            if (roomId.equals(room.getRoomId())) {

            }
        }

        // RoomID에 해당하는 채팅방 가져오기
//        ChatRoomDto room = restTemplate.getForObject(url + "/rooms/" + roomId, ChatRoomDto.class);
//        model.addAttribute("room", room);
        model.addAttribute("roomId", roomId);

        // 메시지 가져오기
        ChatMessageDto[] messages = getMessage(roomId, userId);
        model.addAttribute("messages", messages);

        // TODO : 실제 사용자 닉네임과 연동 생각하기. 현재는 입장할 때 username이 랜덤으로 생성됨
        model.addAttribute("username", UUID.randomUUID().toString());

        return "room";
    }

    public ChatMessageDto[] getMessage(String roomId, String userId) {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8085")
                .path("/chat/rooms/" + roomId + "/messages")
                .queryParam("userId", userId)
                .encode()
                .build()
                .toUri();

        ResponseEntity<ChatMessageDto[]> response =
                restTemplate.getForEntity(uri, ChatMessageDto[].class);

        return response.getBody();
    }
}
