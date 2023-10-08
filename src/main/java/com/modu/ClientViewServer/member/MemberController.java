package com.modu.ClientViewServer.member;

import com.modu.ClientViewServer.HomeController;
import com.modu.ClientViewServer.config.EnvironmentValueConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final RestTemplate restTemplate;
    private final EnvironmentValueConfig environmentValueConfig;
    private static String MEMBER_SERVER_HOST = "localhost";

    @GetMapping("/signup")
    public String signup(Model model) {

        String kakaoRestApiKey = environmentValueConfig.kakaoRestApiKey;
        model.addAttribute("kakaoRestApiKey", kakaoRestApiKey);

        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignUpDto signUpDto) {

        String uriString = UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host(MEMBER_SERVER_HOST)
                .port(8083)
                .path("/members")
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SignUpDto> entity = new HttpEntity<>(signUpDto, headers);

        ResponseEntity<HomeController.Message> response;
        try {
            response = restTemplate.exchange(uriString, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return "index";
            }
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return "member/signup";
        }

        return "member/signup";
    }

    /**
     * 회원정보 수정 페이지 보여주기
     * 인증된 사용자만 접근할 수 있도록 해야 함.
     * 게이트웨이 서버에 access_token 을 헤더에 담아서 GET /members/info 요청함
     */
    @GetMapping("/updateInfo")
    public String updateInfo(HttpServletRequest request, Model model) {


        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String uriString = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("127.0.0.1")
                .port(9001)
                .path("/members/info")
                .build().toUriString();

        try {
            ResponseEntity<HomeController.MemberInfoResponseDto> memberInfoResponse = restTemplate.exchange(uriString, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            if (memberInfoResponse.getStatusCode() != HttpStatus.OK) {
                return "index";
            }

            log.info("memberInfo={}", memberInfoResponse.getBody());
            model.addAttribute("memberInfo", memberInfoResponse.getBody());

            String kakaoRestApiKey = environmentValueConfig.kakaoRestApiKey;
            model.addAttribute("kakaoRestApiKey", kakaoRestApiKey);

            return "member/updateInfo";
        } catch (HttpClientErrorException e) {
            return "index";
        }
    }

    @PostMapping("/updateInfo")
    public String updateInfo(HttpServletRequest request,
                             @RequestParam String email,
                             @RequestParam String nickname,
                             @RequestParam String address,
                             @RequestParam String introduceMyself,
                             @RequestParam String socialType) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("email", List.of(email));
        if (request.getParameterMap().containsKey("password") && request.getParameterMap().containsKey("repeatPassword")) {
            map.put("password", List.of(request.getParameter("password")));
            map.put("repeatPassword", List.of(request.getParameter("repeatPassword")));
        }
        map.put("nickname", List.of(nickname));
        map.put("address", List.of(address));
        map.put("introduceMyself", List.of(introduceMyself));
        map.put("socialType", List.of(socialType));
        String uriString = UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("127.0.0.1")
                .port(9001)
                .path("/members/info")
                .queryParams(map)
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SignUpDto> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<HomeController.Message> response = restTemplate.exchange(uriString, HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
            });
            if (response.getStatusCode() == HttpStatus.OK) {
                return "index";
            }
        } catch (HttpClientErrorException e) {
            return "redirect:/updateInfo";
        }
        return "index";
    }
}