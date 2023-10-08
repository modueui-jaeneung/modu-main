package com.modu.ClientViewServer.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PemFileController {

    private final FileService fileService;

    @GetMapping("/pemFile")
    public void getPemFile(@RequestParam("file")MultipartFile file) {
        fileService.fileUpload(file);
    }
}
