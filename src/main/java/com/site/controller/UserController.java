package com.site.controller;

import com.site.domain.User;
import com.site.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
회원 관련 웹 요청 처리 컨트롤러
@Controller : 이 클래스가 Spring MVC의 컨트롤러임을 나타냄
@RequestMapping("/users") : UserController의 모든 메서드는 '/users'로 시작하는 URL에 매핑
                          예: /users/signup, /users/login, /users/logout 등
@RequiredArgsConstructor : 'final'이 붙은 객체 의존성 주입(DI -> IoC)
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원 가입 폼(View)를 보여주는 메서드
    @GetMapping("/signup") // 가입 페이지 보여주기
    public String signUpForm() {
        return "users/signupForm";
    }

    // 회원 가입 처리 메서드
    /*
    HTTP POST 요청으로 '/user/signup' URL에 접근시 호출
    폼에서 전송된 파라미터(id, password, name, email)가 자동으로 바인딩된 User 객체 생성
    (단, 테이블의 컬럼명과 객체의 필드명이 일치해야 함)
     */
    @PostMapping("/signup") // 실제 가입 처리하기
    public String signUp(User user, RedirectAttributes rttr){
        try {
            // 1. 회원 가입 비지니스 로직 시도(예외 발생 가능)
            userService.signUp(user);
            // 2. 회원 가입 성공시 msg를 loginFrom 화면에 전달
            rttr.addFlashAttribute("msg", "회원가입이 완료되었습니다.");
            // 3. 회원 가입 성공 시, 로그인 페이지로 이동
            return "redirect:/users/login";
        }catch (IllegalArgumentException e){
            // 4. 회원가입 실패시(중복 아이디) 서비스에서 발생시킨 에러 메시지 화면에 전달.
            rttr.addFlashAttribute("msg", e.getMessage());
            // 5. 회원 가입 폼으로 재이동
            return "redirect:/users/signup";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "users/loginForm";
    }

    @PostMapping("/login")
    public String login(String id, String password, HttpServletRequest request, Model model) {
        // 1. 먼저 입력한 아이디로 사용자가 존재하는지 확인
        User existingUser = userService.findById(id);

        if (existingUser == null) {
            // 아이디가 DB에 없는 경우
            model.addAttribute("msg", "존재하지 않는 아이디입니다.");
            return "users/loginForm";
        }

        // 2. 아이디가 존재한다면, 비밀번호까지 맞는지 확인
        User loginUser = userService.login(id, password);

        if (loginUser != null) {
            // 로그인 성공 : 세션 생성 및 사용자 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("user", loginUser);
            return "redirect:/";
        } else {
            // 아이디는 있지만 비밀번호가 틀린 경우
            model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
            return "users/loginForm";
        }
    }

    // 로그아웃 처리 메서드
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        // 기존에 세션이 존재하지 않으면 null 반환
        HttpSession session = request.getSession(false);
        // 세션이 존재하면 무효화
        if(session != null){
            session.invalidate(); // 세션 무효화
        }
        return "redirect:/";
    }

    // 회원 탈퇴 처리 메서드
    @GetMapping("/remove")
    public String remove(HttpSession session) {
        // 1. 세션에서 로그인된 사용자 정보 가져오기
        User user = (User) session.getAttribute("user");

        if (user != null) {
            // 2. 회원 정보 존재시 서비스의 remove() 메서드 호출하여 DB에서 삭제
            userService.remove(user.getId());

            // 3. 탈퇴했으므로 로그인 만료(세션 정보 삭제:로그아웃)
            session.invalidate(); // 세션 무효화
        }

        return "redirect:/"; // 메인 페이지로 이동
    }

    // 회원 정보 수정 폼(View)를 보여주는 메서드
    @GetMapping("/modify")
    public String modifyForm(HttpSession session, Model model) {
        // 현재 로그인 상태 여부 판별
        User user = (User) session.getAttribute("user");
        // 로그인 상태가 아닌경우 로그인 페이지로 이동
        if (user == null) return "redirect:/users/login";

        // 최신 정보를 모델에 저장하여 전달
        model.addAttribute("user", user);
        return "users/modifyForm";
    }

    // 2. 수정 처리
    @PostMapping("/modify")
    public String modify(User user, HttpSession session, RedirectAttributes rttr) {
        // DB 업데이트 실행 (비밀번호, 이메일 반영)
        userService.modify(user);

        // 세션 정보 업데이트 (수정된 이름이나 이메일 적용)
        // 지속적으로 필요한 내용을 저장할 때 사용
        session.setAttribute("user", user);

        // addFlashAttribute : 일회성으로 필요한 내용을 전달할 때 사용
        rttr.addFlashAttribute("msg", "정보 수정이 완료되었습니다.");
        return "redirect:/";
    }

}
