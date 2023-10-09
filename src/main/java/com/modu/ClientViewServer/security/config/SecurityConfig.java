package com.modu.ClientViewServer.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

@EnableWebSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestTemplate restTemplate;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/chat", "/authtest").authenticated()
                        .anyRequest().permitAll());

        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

        http.oauth2ResourceServer((resourceServer) -> resourceServer
                .jwt(Customizer.withDefaults()));

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf().disable();

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/css/**", "/static/images/**", "/static/js/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] publicKeyBytes  = getPublicKey();
        // 굳이 파일을 저장한 다음에 불러와야 하나...?
        // 문자열로 바꾸고 이어서 진행하면 되지 않을까

//        File file = new File("/Users/joseong-gyu/Desktop/modu_servers/modu-main/modu-public.pem");
//        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        String key = new String(publicKeyBytes);
        String publicKeyPEM = key
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace(System.lineSeparator(), "")
                .replace("-----END RSA PUBLIC KEY-----", "");
        byte[] encoded = Base64.decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new X509EncodedKeySpec(encoded);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private byte[] getPublicKey() throws IOException {
        String urlString = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("127.0.0.1")
                .port(9000)
                .path("/publicKey")
                .build().toString();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Resource> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(urlString, HttpMethod.GET, entity, Resource.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            log.info("modu-public.pem 파일 가져오기 실패");
        }
        return responseEntity.getBody().getContentAsByteArray();
    }
}
