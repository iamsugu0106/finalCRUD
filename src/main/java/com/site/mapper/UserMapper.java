package com.site.mapper;

import com.site.domain.User;
import org.apache.ibatis.annotations.Mapper;

/*
 * '회원' 데이터에 접근하기 위한 MyBatis 매퍼(Mapper) 인터페이스
 * @Mapper : Spring이 인터페이스를 MyBatis 매퍼로 인식하고 구현체를 자동으로 생성
 */
@Mapper
public interface UserMapper {

    /**
     * 회원 조회
     * XML의 resultType이 User 객체이므로 반환 타입을 User로 일치시킵니다.
     */
    User findById(String id);

    /**
     * 회원 등록
     * XML의 id="save"와 연결됩니다.
     */
    void save(User user);

    /**
     * 회원 정보 수정
     * XML의 id="update"와 연결됩니다.
     */
    void update(User user);

    /**
     * 회원 탈퇴
     * XML의 id="deleteById"와 연결됩니다.
     */
    void deleteById(String id);
}