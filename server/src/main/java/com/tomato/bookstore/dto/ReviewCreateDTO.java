package com.tomato.bookstore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 创建书评请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDTO {
    @NotNull(message = "评分不能为空")
    @Min(value = 0, message = "评分最低为 0 分")
    @Max(value = 10, message = "评分最高为 10 分")
    private Integer rating;

    private String content;
}
