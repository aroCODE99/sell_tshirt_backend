package com.aro.DTOs;

public record SuccessDataResponse<T>(String message, T data, String dataAndTime) {
}
