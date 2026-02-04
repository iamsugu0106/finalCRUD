package com.site.controller;

import com.site.domain.Board;
import com.site.domain.File;
import com.site.domain.User;
import com.site.service.BoardService;
import com.site.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final FileService fileService;


    /**
     * 게시글 전체 목록 및 검색 결과 페이지
     * @param searchType : 검색 타입 (title, content, writer)
     * @param keyword : 검색 키워드
     * @param model : View에 데이터를 전달하기 위한 객체
     * @return "boards/list"
     */

    @GetMapping
    // @RequestParam : 클라이언트가 전달하는 데이터의 name과 변수명이 동일할 경우 자동으로 매핑
    // required = false 속성 : 파라미터로 전달되지 않아도 된다는 의미(즉, 에러방지)
    public String list(@RequestParam(required = false) String searchType,
                       @RequestParam(required = false) String keyword, Model model,
                       HttpServletRequest request) {

        // 전체 게시글 리스트
        List<Board> boards = boardService.findAll(searchType, keyword);
        HttpSession session = request.getSession();
        session.setAttribute("keyword", keyword);
        session.setAttribute("searchType", searchType);
        //model.addAttribute("keyword", keyword);
        model.addAttribute("boards", boards);
        return "boards/list";

    }

    @GetMapping("/{bno}")
    public String detail(@PathVariable long bno, Model model) {
        Board board = boardService.findById(bno);
        model.addAttribute("board", board);

        // FileService를 통해 이 게시글에 첨부된 파일 목록 가져오기
        List<File> attachedFiles = fileService.findFilesByBoardId(bno);
        model.addAttribute("attachedFiles", attachedFiles);
        return "boards/detail";
    }

    @GetMapping("/write")
    public String write() {
        return "boards/write";
    }

    /**
     * 게시글 작성 처리 (+파일 첨부 로직 추가)
     * @param board : 사용자가 입력한 게시글
     * @param session : 로그인한 아이디 필요
     * @param file : 첨부된 파일 데이터 저장
     */

    @PostMapping("/write")
    public String write(HttpServletRequest request, Board board, @RequestParam("file") MultipartFile file) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        board.setWriter(user.getId());
        // 게시글 정보를 먼저 저장 -> bno 생성
        boardService.addContent(board);

        // 파일이 실제로 첨부되어있는지 확인
        if (!file.isEmpty()) {
            // FileService의 saveFile 메서드를 호출하여 파일 저장 로직 실행
            //    이때, 파일이 첨부된 게시글 ID(bno)와 전달받은 파일 데이터를 함께 전달
            fileService.saveFile(board.getBno(), file);
        }
        return "redirect:/boards";
    }

    @GetMapping("/{bno}/contentModify")
    /**
     * @PathVariable : 주소창(URL)에 있는 값을 가져와야 할때 사용(데이터의 위치 즉, 주소)
     * cf) @RequestParam : 데이터의 조건(옵션)
     */
    public String contentModify(@PathVariable long bno, Model model){
        Board board = boardService.findById(bno);
        model.addAttribute("board", board);

        List<File> attachedFiles = fileService.findFilesByBoardId(bno);
        model.addAttribute("attachedFiles", attachedFiles);
        return "boards/boardModifyForm";
    }

    @PostMapping("/{bno}/contentModify")
    public String contentModify(@PathVariable long bno, Board board, @RequestParam("file") MultipartFile file){
        board.setBno(bno);
        boardService.contentModify(board);
        if (!file.isEmpty()) {
            fileService.saveFile(board.getBno(), file);
        }
        return "redirect:/boards";
    }

    @GetMapping("/{bno}/delete")
    public String delete(){
        return "boards/{bno}/delete";
    }

    @PostMapping("/{bno}/delete")
    public String delete(@PathVariable long bno){
        boardService.contentDelete(bno);
        return "redirect:/boards";
    }

}
