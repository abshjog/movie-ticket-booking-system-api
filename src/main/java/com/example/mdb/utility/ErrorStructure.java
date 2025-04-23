package com.example.mdb.utility;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorStructure<T> {

    private int errorCode;
    private String message;
    private T data;
}
