package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.ApiConstants;
import com.tomato.bookstore.constant.RoleConstants;
import com.tomato.bookstore.dto.AdvertisementDTO;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.AdvertisementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 广告控制器
 *
 * <p>该类包含广告相关的接口，包括获取广告列表、创建广告、更新广告、删除广告等操作。
 */
@RestController
@RequestMapping(ApiConstants.ADVERTISEMENT_BASE_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdvertisementController {
  private final AdvertisementService advertisementService;

  /**
   * 获取所有广告
   *
   * @return 广告列表
   */
  @GetMapping
  public ApiResponse<List<AdvertisementDTO>> getAllAdvertisements() {
    log.info("获取所有广告列表");
    List<AdvertisementDTO> advertisements = advertisementService.getAllAdvertisements();
    return ApiResponse.success(advertisements);
  }

  /**
   * 获取指定 ID 的广告
   *
   * @param id 广告 ID
   * @return 广告信息
   */
  @GetMapping(ApiConstants.ADVERTISEMENT_DETAIL)
  public ApiResponse<AdvertisementDTO> getAdvertisementById(@PathVariable Long id) {
    log.info("获取广告详情 id={}", id);
    AdvertisementDTO advertisement = advertisementService.getAdvertisementById(id);
    return ApiResponse.success(advertisement);
  }

  /**
   * 创建广告（仅管理员）
   *
   * @param advertisementDTO 广告信息
   * @param userPrincipal 当前用户
   * @return 创建成功的广告
   */
  @PostMapping
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<AdvertisementDTO> createAdvertisement(
      @RequestBody @Valid AdvertisementDTO advertisementDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员「{}」创建广告：{}", userPrincipal.getUsername(), advertisementDTO.getTitle());
    AdvertisementDTO createdAdvertisement =
        advertisementService.createAdvertisement(advertisementDTO);
    return ApiResponse.success(createdAdvertisement);
  }

  /**
   * 更新广告信息（仅管理员）
   *
   * @param advertisementDTO 广告信息
   * @param userPrincipal 当前用户
   * @return 更新后的广告
   */
  @PutMapping
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<AdvertisementDTO> updateAdvertisement(
      @RequestBody @Valid AdvertisementDTO advertisementDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员「{}」更新广告 id={}", userPrincipal.getUsername(), advertisementDTO.getId());
    AdvertisementDTO updatedAdvertisement =
        advertisementService.updateAdvertisement(advertisementDTO);
    return ApiResponse.success(updatedAdvertisement);
  }

  /**
   * 删除广告（仅管理员）
   *
   * @param id 广告 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping(ApiConstants.ADVERTISEMENT_DETAIL)
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<String> deleteAdvertisement(
      @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员「{}」删除广告 id={}", userPrincipal.getUsername(), id);
    advertisementService.deleteAdvertisement(id);
    return ApiResponse.success("删除成功");
  }
}
