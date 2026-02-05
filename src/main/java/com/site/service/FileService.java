package com.site.service;

import com.site.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileMapper fileMapper;

    // application.properties에 설정한 파일 업로드 디렉토리 경로 주입.
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일을 서버에 물리적으로 저장하고DB에 정보를 기록합니다.
     * @param boardID : 파일이 첨부될 게시글의 PK
     * @param multipartFile : 컨트롤러에서 받은 multipartFile 객체
     * @throws IOException : 파일 저장 중 발생할 수 있는 예외 처리
     */

    @Transactional
    public void saveFile(long boardId, MultipartFile multipartFile) {
        // 1. 사용자가 파일을 첨부하지 않았으면, 메서드를 그냥 종료
        if (multipartFile.isEmpty()) {
            return;
        }

        try {
            // 1. 원본 파일명 추출
            String originalName = multipartFile.getOriginalFilename();

            // 2. 서버 저장용 고유 파일명 생성
            String uuid = UUID.randomUUID().toString();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String storedName = uuid + extension; // ex: a1b2...png

            // [수정 포인트 1] 폴더 경로와 파일 전체 경로를 분리합니다.
            // uploadDir는 "C:/upload/" 같은 폴더 경로여야 합니다.
            String savePath = uploadDir;

            // [수정 포인트 2] 폴더가 없으면 폴더만 생성합니다 (파일 이름 제외)
            File uploadFolder = new File(savePath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs(); // mkdir()보다 mkdirs()가 더 안전합니다 (상위 폴더까지 생성)
            }

            // [수정 포인트 3] 저장할 파일 객체 생성 (폴더 경로 + 파일 이름)
            // File.separator는 운영체제에 맞는 구분자(\ 또는 /)를 넣어줍니다.
            File targetFile = new File(savePath + File.separator + storedName);

            // 4. 파일을 지정된 경로에 물리적으로 저장
            multipartFile.transferTo(targetFile);

            // 5. DB 저장을 위한 객체 생성
            com.site.domain.File fileEntity = new com.site.domain.File();
            fileEntity.setBoard_id(boardId);
            fileEntity.setOriginal_name(originalName);
            fileEntity.setStored_name(storedName);
            fileEntity.setFile_path(savePath + File.separator + storedName); // 전체 경로 저장

            // 6. DB 저장
            fileMapper.save(fileEntity);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 특정 게시글에 첨부된 모든 파일 정보 조회
     * @param bno : 이미지가 저장된 게시글 PK
     * @return 파일 정보 목록
     */
    public List<com.site.domain.File> findFilesByBoardId(long bno) {
        return fileMapper.findFilesByBoardId(bno);
    }


    public com.site.domain.File findById(long fileId) {
        return fileMapper.findById(fileId);
    }

    // 파일 삭제 후 새로운 파일 등록 기능 구현
    @Transactional
    public void deleteFile(long bno) {
        // 1. DB에서 게시글에 첨부된 파일 정보 조회
        List<com.site.domain.File> files = fileMapper.findFilesByBoardId(bno);

        // 2. 파일 목록을 순회하며 서버에 저장된 실제 파일 삭제
        for(com.site.domain.File file : files){
            String filePath = file.getFile_path();

            File deletefile = new File(filePath);

            if(deletefile.exists()) {
                deletefile.delete();
            }

        }

        // 3. DB에서 파일 정보 삭제
//        fileMapper.findFilesByBoardId(bno)
        fileMapper.deleteByBoardId(bno);
    }
}
