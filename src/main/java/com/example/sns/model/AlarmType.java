package com.example.sns.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {
    NEW_COMMENT_ON_POST("new_comment!"),
    NEW_LIKE_ON_POST("new_like")
    ;

    private final String alarmText;
}
