package com.modu.ClientViewServer.Posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.modu.ClientViewServer.utils.UrlUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final RestTemplate restTemplate;

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getPosts(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        String access_token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                access_token = cookie.getValue();
            }
        }

        if (access_token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        log.info("cookie에 access_token 있음.");

        String uriString = UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("127.0.0.1")
                .port(8084)
                .path("/posts")
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of("Bearer " + access_token));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<PostDTO>> response = restTemplate.exchange(uriString, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}")
    public String PostDetail(Model model, @PathVariable("postId") long postId) {
        /* 상세페이지 이동 */
        String uriString = UrlUtils.GetUrlString(
                "http",
                "127.0.0.1",
                8084,
                "/posts/" + postId);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<PostDTO> responsepost = restTemplate.exchange(uriString, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        model.addAttribute("postList", responsepost.getBody());

        return "Posts/postDetail";
    }

    @GetMapping("/posts/insert")
    public String PostInsertPage(Model model) {
        /* 글작성 페이지 이동*/
        PostDTO postdto = new PostDTO();
        model.addAttribute("postdto", postdto);
        return "Posts/postInsert";
    }

    @PostMapping("/posts/insertp")
    public String postInsert(@ModelAttribute PostDTO PostDTO) throws JsonProcessingException {
        /* 게시글 작성*/

        String uriString = UrlUtils.GetUrlString(
                "http",
                "127.0.0.1",
                8084,
                "/posts/insertp");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PostDTO> entity = new HttpEntity<>(PostDTO, headers);

        try {
            ResponseEntity<Void> responsepost = restTemplate.exchange(uriString, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });

            if (responsepost.getStatusCode() == HttpStatus.OK) {
                // 성공적으로 게시글을 작성한 경우
                return "redirect:/";
            } else {
                // 요청이 실패한 경우
                return "redirect:/error1"; // 실패 처리를 어떻게 할지에 따라 수정
            }
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 (예: 400 Bad Request)가 발생한 경우
            return "redirect:/error400"; // 실패 처리를 어떻게 할지에 따라 수정
        } catch (HttpServerErrorException e) {
            // 서버 오류 (예: 500 Internal Server Error)가 발생한 경우
            return "redirect:/error500"; // 실패 처리를 어떻게 할지에 따라 수정
        }
    }

    @GetMapping("/posts/update/{postId}")
    public String PostUpdatePage(Model model, @PathVariable("postId") long postId) {
        /* 글수정 페이지 이동*/

        String uriString = UrlUtils.GetUrlString(
                "http",
                "127.0.0.1",
                8084,
                "/posts/update/" + postId);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<PostDTO> responsepost = restTemplate.exchange(uriString, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        model.addAttribute("postdto", responsepost.getBody());
        return "Posts/postUpdate";
    }

    @PostMapping("/posts/updatep")
    public String postUpdate(@ModelAttribute PostDTO PostDTO) throws JsonProcessingException {
        /* 게시글 수정*/

        String uriString = UrlUtils.GetUrlString(
                "http",
                "127.0.0.1",
                8084,
                "/posts/updatep");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PostDTO> entity = new HttpEntity<>(PostDTO, headers);
        try {
            ResponseEntity<Void> responsepost = restTemplate.exchange(uriString, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });

            if (responsepost.getStatusCode() == HttpStatus.OK) {
                // 성공적으로 게시글을 수정한 경우
                return "redirect:/";
            } else {
                // 요청이 실패한 경우
                return "redirect:/error1"; // 실패 처리를 어떻게 할지에 따라 수정
            }
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 (예: 400 Bad Request)가 발생한 경우
            return "redirect:/error400"; // 실패 처리를 어떻게 할지에 따라 수정
        } catch (HttpServerErrorException e) {
            // 서버 오류 (예: 500 Internal Server Error)가 발생한 경우
            return "redirect:/error500"; // 실패 처리를 어떻게 할지에 따라 수정
        }
    }

    @GetMapping ("/posts/delete/{postId}")
    public String PostDelete(@PathVariable("postId") long postId) {
        /* 글 삭제*/
        String uriString = UrlUtils.GetUrlString(
                "http",
                "127.0.0.1",
                8084,
                "/posts/delete/" + postId);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Void> responsepost = restTemplate.exchange(uriString, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            if (responsepost.getStatusCode() == HttpStatus.OK) {
                // 성공적으로 게시글을 삭제한 경우
                log.info("게시글 삭제성공");
                return "redirect:/";
            } else {
                // 요청이 실패한 경우
                return "redirect:/error1"; // 실패 처리를 어떻게 할지에 따라 수정
            }
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 (예: 400 Bad Request)가 발생한 경우
            return "redirect:/error400"; // 실패 처리를 어떻게 할지에 따라 수정
        } catch (HttpServerErrorException e) {
            // 서버 오류 (예: 500 Internal Server Error)가 발생한 경우
            return "redirect:/error500"; // 실패 처리를 어떻게 할지에 따라 수정
        }
    }

}
