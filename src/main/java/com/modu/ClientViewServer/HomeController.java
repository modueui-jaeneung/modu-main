package com.modu.ClientViewServer;

import com.modu.ClientViewServer.config.EnvironmentValueConfig;
import com.modu.ClientViewServer.member.SignUpDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

        String tokenValue = request.getParameter("tokenValue");

        if (tokenValue != null) {
            re.addFlashAttribute("access_token", tokenValue);
        }

        return "redirect:/";
    }

    @GetMapping("/")
    public String index(@ModelAttribute("access_token") String token, Model model) {
        model.addAttribute("access_token", token);
//        String uriString = UriComponentsBuilder
//                    .newInstance()
//                    .scheme("http")
//                    .host("127.0.0.1")
//                    .port(8084)
//                    .path("/posts")
//                    .build().toUriString();
//
//            HttpHeaders headers = new HttpHeaders();
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            ResponseEntity<List<PostDTO>> responsepost = restTemplate.exchange(uriString, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
//            });
//
//            model.addAttribute("postList", responsepost.getBody());
        return "index";
    }


    @GetMapping("/bookmark")
    public String bookmark() {
        return "member/bookmark";
    }

    @GetMapping("/applyList")
    public String applyList() {
        return "member/applyList";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfoResponseDto {
        String email;
        String nickname;
        String socialType;
        String address;
        String introduceMyself;
    }
}
