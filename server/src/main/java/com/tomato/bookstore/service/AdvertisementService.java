package com.tomato.bookstore.service;

import com.tomato.bookstore.dto.AdvertisementDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import java.util.List;

/** 广告服务接口 */
public interface AdvertisementService {
  /**
   * 获取所有广告
   *
   * @return 广告列表
   */
  List<AdvertisementDTO> getAllAdvertisements();

  /**
   * 根据 ID 获取广告
   *
   * @param id 广告 ID
   * @return 广告信息
   * @throws ResourceNotFoundException 广告不存在
   */
  AdvertisementDTO getAdvertisementById(Long id) throws ResourceNotFoundException;

  /**
   * 创建广告
   *
   * @param advertisementDTO 广告信息
   * @return 创建后的广告
   * @throws BusinessException 商品不存在
   */
  AdvertisementDTO createAdvertisement(AdvertisementDTO advertisementDTO) throws BusinessException;

  /**
   * 更新广告
   *
   * @param advertisementDTO 广告信息
   * @return 更新后的广告
   * @throws ResourceNotFoundException 广告不存在
   * @throws BusinessException 商品不存在
   */
  AdvertisementDTO updateAdvertisement(AdvertisementDTO advertisementDTO)
      throws ResourceNotFoundException, BusinessException;

  /**
   * 删除广告
   *
   * @param id 广告 ID
   * @throws ResourceNotFoundException 广告不存在
   */
  void deleteAdvertisement(Long id) throws ResourceNotFoundException;
}
