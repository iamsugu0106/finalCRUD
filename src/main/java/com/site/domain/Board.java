package com.site.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {
    private long bno;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
