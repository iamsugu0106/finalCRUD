package com.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 메인 페이지(홈) 요청을 처리하는 컨트롤러입니다.
 */
@Controller
public class MainController {

    @GetMapping("/") // 브라우저 주소창에 localhost:8080 입력 시 호출됩니다.
    public String index() {
        // templates 폴더 바로 아래에 있는 index.html을 찾아 렌더링합니다.
        return "index";
    }
}