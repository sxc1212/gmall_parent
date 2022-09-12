package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;


    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {


        String cartKey = getCartKey(userId);

        CartInfo cartInfoExist = (CartInfo) this.redisTemplate.opsForHash().get(cartKey, skuId.toString());

        if (cartInfoExist != null) {

            if (cartInfoExist.getSkuNum() + skuNum > 200) {
                cartInfoExist.setSkuNum(200);
            } else {
                cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);
            }


            cartInfoExist.setSkuPrice(productFeignClient.getSkuPrice(skuId));

            if (cartInfoExist.getIsChecked().intValue() == 0) {
                cartInfoExist.setIsChecked(1);
            }

            cartInfoExist.setUpdateTime(new Date());


        } else {

            cartInfoExist = new CartInfo();

            SkuInfo skuInfo = this.productFeignClient.getSkuInfo(skuId);
            cartInfoExist.setSkuId(skuId);
            cartInfoExist.setSkuNum(skuNum);
            cartInfoExist.setUserId(userId);

            cartInfoExist.setSkuPrice(productFeignClient.getSkuPrice(skuId));

            cartInfoExist.setCartPrice(productFeignClient.getSkuPrice(skuId));
            cartInfoExist.setSkuName(skuInfo.getSkuName());
            cartInfoExist.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfoExist.setCreateTime(new Date());
            cartInfoExist.setUpdateTime(new Date());
        }

        this.redisTemplate.opsForHash().put(cartKey, skuId.toString(), cartInfoExist);

    }

    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {

        List<CartInfo> cartInfoNoLoginList = new ArrayList<>();


        if (!StringUtils.isEmpty(userTempId)) {

            String cartKey = this.getCartKey(userTempId);

            cartInfoNoLoginList = this.redisTemplate.opsForHash().values(cartKey);
            if (StringUtils.isEmpty(userId)) {

                if (!CollectionUtils.isEmpty(cartInfoNoLoginList)) {

                    cartInfoNoLoginList.sort((o1, o2) -> {
                        return DateUtil.truncatedCompareTo(o2.getUpdateTime(), o1.getUpdateTime(), Calendar.SECOND);
                    });
                }

                return cartInfoNoLoginList;
            }

        }


        List<CartInfo> cartInfoLoginList = new ArrayList<>();

        if (!StringUtils.isEmpty(userId)) {

            String cartKey = this.getCartKey(userId);

            BoundHashOperations<String, String, CartInfo> boundHashOperations = this.redisTemplate.boundHashOps(cartKey);


            if (!CollectionUtils.isEmpty(cartInfoNoLoginList)) {

                cartInfoNoLoginList.forEach(cartInfoNoLogin -> {

                    if (boundHashOperations.hasKey(cartInfoNoLogin.getSkuId().toString())) {

                        CartInfo cartInfoLogin = boundHashOperations.get(cartInfoNoLogin.getSkuId().toString());

                        if (cartInfoLogin.getSkuNum() + cartInfoNoLogin.getSkuNum() > 200) {
                            cartInfoLogin.setSkuNum(200);
                        } else {
                            cartInfoLogin.setSkuNum(cartInfoLogin.getSkuNum() + cartInfoNoLogin.getSkuNum());
                        }


                        if (cartInfoNoLogin.getIsChecked().intValue() == 1) {

                            if (cartInfoLogin.getIsChecked().intValue() == 0) {
                                cartInfoLogin.setIsChecked(1);
                            }
                        }


                        cartInfoLogin.setUpdateTime(new Date());


                        this.redisTemplate.boundHashOps(cartKey).put(cartInfoLogin.getSkuId().toString(), cartInfoLogin);

                    } else {

                        if (cartInfoNoLogin.getIsChecked().intValue() == 1) {
                            cartInfoNoLogin.setUserId(userId);
                            cartInfoNoLogin.setCreateTime(new Date());
                            cartInfoNoLogin.setUpdateTime(new Date());
                            this.redisTemplate.opsForHash().put(cartKey, cartInfoNoLogin.getSkuId().toString(), cartInfoNoLogin);
                        }
                    }
                });

                this.redisTemplate.delete(this.getCartKey(userTempId));
            }


            cartInfoLoginList = boundHashOperations.values();
        }

        if (CollectionUtils.isEmpty(cartInfoLoginList)) {
            return new ArrayList<>();
        }

        cartInfoLoginList.sort((o1, o2) -> {
            return DateUtil.truncatedCompareTo(o2.getUpdateTime(), o1.getUpdateTime(), Calendar.SECOND);
        });

        return cartInfoLoginList;


    }

    @Override
    public void checkCart(Long skuId, String userId, Integer isChecked) {

        String cartKey = this.getCartKey(userId);

        CartInfo cartInfo = (CartInfo) this.redisTemplate.opsForHash().get(cartKey, skuId.toString());
        if (cartInfo != null) {

            cartInfo.setIsChecked(isChecked);

            this.redisTemplate.opsForHash().put(cartKey, skuId.toString(), cartInfo);
        }
    }

    @Override
    public void deleteCart(Long skuId, String userId) {

        String cartKey = this.getCartKey(userId);

        this.redisTemplate.opsForHash().delete(cartKey, skuId.toString());
    }

    private String getCartKey(String userId) {
        String cartKey = RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
        return cartKey;
    }
}