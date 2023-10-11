package com.modu.ClientViewServer.chat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RestTemplate restTemplate;
    // @Value("${kubernetes.host}")
    private final String hostUrl = "http://chat-service:8085";

    @GetMapping("/chat")
    public void chatAuth(@RequestParam("access_token") String token, HttpServletResponse response) throws IOException {
        log.info("token={}", token);
        response.setHeader("Authorization", "Bearer " + token);
        response.sendRedirect("/enter-chat");
    }

    @GetMapping("/enter-chat")
    public String chatIndex(Model model, @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("userId by jwt={}", userId);

        log.info("전달된 userId = {}", userId);
        URI uri = UriComponentsBuilder
                .fromUriString(hostUrl)
                .path("/chat/rooms")
                .queryParam("userId", userId)
                .encode()
                .build()
                .toUri();

        ResponseEntity<ChatRoomDto[]> response = restTemplate.getForEntity(uri, ChatRoomDto[].class);

        ChatRoomDto[] rooms = response.getBody();
        model.addAttribute("rooms", rooms);

        return "chat/chat";
    }

    @GetMapping("/chat/{roomId}")
    public String enterRoom(@PathVariable String roomId, Model model, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        // 채팅방 데이터
        ChatRoomDto[] rooms = this.getRooms(userId);
        ChatRoomDto room = this.getRoom(roomId);
        model.addAttribute("rooms", rooms);
        model.addAttribute("room", room);

        // 메시지
        ChatMessageDto[] messages = this.getMessage(roomId, userId);
        model.addAttribute("messages", messages);

        // TODO : 실제 사용자 닉네임과 연동 생각하기. 현재는 입장할 때 username이 랜덤으로 생성됨
        model.addAttribute("username", userId);

        return "chat/room";
    }

    private ChatRoomDto[] getRooms(String userId) {
        URI uri = UriComponentsBuilder
                .fromUriString(hostUrl)
                .path("/chat/rooms")
                .queryParam("userId", userId)
                .encode()
                .build()
                .toUri();

        // 채팅방 목록 가져오기
        ResponseEntity<ChatRoomDto[]> responseRooms = restTemplate.getForEntity(uri, ChatRoomDto[].class);

        ChatRoomDto[] rooms = responseRooms.getBody();
        return rooms;
    }

    private ChatMessageDto[] getMessage(String roomId, String userId) {
        URI uri = UriComponentsBuilder
                .fromUriString(hostUrl)
                .path("/chat/rooms/" + roomId + "/messages")
                .queryParam("userId", userId)
                .encode()
                .build()
                .toUri();

        ResponseEntity<ChatMessageDto[]> response =
                restTemplate.getForEntity(uri, ChatMessageDto[].class);

        return response.getBody();
    }

    private ChatRoomDto getRoom(String roomId) {
        URI uri = UriComponentsBuilder
                .fromUriString(hostUrl)
                .path("/chat/rooms/" + roomId)
                .encode()
                .build()
                .toUri();

        return restTemplate.getForObject(uri, ChatRoomDto.class);
    }
}
