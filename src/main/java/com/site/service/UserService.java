package com.site.service;

import com.site.domain.User;
import com.site.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화)
public class UserService {
    private final UserMapper userMapper;

    public User findById(String id) {
        return userMapper.findById(id);
    }

    // 로그인 로직
    public User login(String id, String password){
        // 1. UserMapper가 Optional이 아닌 일반 User를 반환하므로 바로 받습니다.
        User user = userMapper.findById(id);

        // 2. null 체크와 비밀번호 일치 여부를 확인합니다.
        if(user != null && password.equals(user.getPassword())){
            return user; // 로그인 성공
        }
        return null; // 로그인 실패
    }

    // 회원 가입 로직
    @Transactional // 쓰기 작업이므로 기본 트랜잭션 적용 (읽기 전용 해제)
    public void signUp(User user) {
        // .isPresent() 대신 직접 null 체크를 수행합니다.
        if(userMapper.findById(user.getId()) != null){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        userMapper.save(user); // DB 저장
    }

    // 회원 정보 수정 로직
    @Transactional
    public void modify(User user) {
        userMapper.update(user); // 매퍼에게 정보를 수정하라고 시킴
    }

    // 회원 탈퇴 로직
    @Transactional
    public void remove(String id) {
        userMapper.deleteById(id); // 매퍼에게 정보를 삭제하라고 시킴
    }
}