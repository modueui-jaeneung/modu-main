package com.modu.ClientViewServer;

import com.modu.ClientViewServer.Posts.PostDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    // CICD Test

    @GetMapping("/loginReceive")
    public String loginResponse(HttpServletRequest request, RedirectAttributes re) {

        String tokenValue = request.getParameter("tokenValue");
        log.info("tokenValue={}", tokenValue);
        if (tokenValue != null) {
            re.addFlashAttribute("access_token", tokenValue);
        }
        log.info("redirect to GET /");
        return "redirect:/";
    }

    @GetMapping("/")
    public String index(@ModelAttribute("access_token") String token, Model model) {
        model.addAttribute("access_token", token);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication instanceof JwtAuthenticationToken) {
//            model.addAttribute("isAuthenticated", 1);
//        } else {
//            model.addAttribute("isAuthenticated", 0);
//        }
        log.info("view index page");
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
