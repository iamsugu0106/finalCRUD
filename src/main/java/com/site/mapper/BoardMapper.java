package com.site.mapper;

import com.site.domain.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Board 테이블에 접근하기 위한 MyBatis 매퍼 인터페이스
 * 이 인터페이스에 선언된 메서드들은
 * 'resources/mappers/BoardMapper/xml'에 정의된 SQL쿼리와 매핑
 * @Mapper : Spring이 이 인터페이스를  Mybatis 매퍼로 인식하고 구현체를 자동으로 생성
 */
@Mapper
public interface BoardMapper {

    /**
     * 모든 게시글 또는 게시글 검색 결과 조회
     * @param searchType : 검색 타입(title, content, writer)
     * @param keyword : 검색 키워드
     * @return : 게시글 목록
     */

    List<Board> findAll(String searchType, String keyword);


    Board findById(long bno);

    void addContent(Board board);

    void contentModify(Board board);

    void contentDelete(long bno);

}
