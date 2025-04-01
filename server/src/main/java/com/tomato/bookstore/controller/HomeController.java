package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.dto.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
  @GetMapping(ApiConstants.HOME)
  public ApiResponse<Map<String, String>> home() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "番茄商城 API");
    response.put("status", "运行中");
    response.put("version", "1.0.0");
    return ApiResponse.success(response);
  }
}
