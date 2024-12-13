package com.tunemate.be.global.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OkResponse<T> {
    private final Boolean ok;
    private final T data;
}
