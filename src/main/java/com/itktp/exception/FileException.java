package com.itktp.exception;

/**
 * @author: ktp
 * @date: 2022/3/30
 */

/**
 * 自定义异常信息类
 */
public class FileException extends RuntimeException {

    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}
