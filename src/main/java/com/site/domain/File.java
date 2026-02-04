package com.site.domain;

import lombok.Data;

// 업로드 된 파일 정보를 담는 도메인 클래스
@Data
public class File {
    // 파일 고유 id
    private long id;
    // 파일이 첨부된 게시글의 ID(FK)
    private long board_id;
    // 사용자가 업로드한 파일의 원본 이름(ex> dog.png)
    private String original_name;
    // 서버에 저장시 중복을 피하기 위해 생성된 고유한 파일 이름
    // ex) a1b2c3d4-e5f6-7890-1234-567890abcdef.png
    private String stored_name;
    // 서버에 파일이 실제로 저장된 전체 경로
    private String file_path;

}
