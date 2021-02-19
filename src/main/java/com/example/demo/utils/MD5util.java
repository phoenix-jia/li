package com.example.demo.utils;

import org.springframework.util.DigestUtils;

public class MD5util {
    public static String code(String a){
        return DigestUtils.md5DigestAsHex(a.getBytes());
    }
}
