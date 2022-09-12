package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class AuthGlobalFilter implements GlobalFilter {

    @Value("${authUrls.url}")
    private String authUrls;
    @Autowired
    private RedisTemplate redisTemplate;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();

        if (pathMatcher.match("/**/inner/**", path)) {

            ServerHttpResponse response = exchange.getResponse();

            return out(response, ResultCodeEnum.PERMISSION);
        }


        String userId = this.getUserId(request);

        String userTempId = this.getUserTempId(request);

        if ("-1".equals(userId)) {

            ServerHttpResponse response = exchange.getResponse();

            return out(response, ResultCodeEnum.PERMISSION);
        }


        if (pathMatcher.match("/api/**/auth/**", path)) {

            if (StringUtils.isEmpty(userId)) {

                ServerHttpResponse response = exchange.getResponse();

                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }
        }

        String[] split = authUrls.split(",");
        if (split != null && split.length > 0) {
            for (String url : split) {

                if (path.indexOf(url) != -1 && StringUtils.isEmpty(userId)) {

                    ServerHttpResponse response = exchange.getResponse();

                    response.setStatusCode(HttpStatus.SEE_OTHER);

                    response.getHeaders().set(HttpHeaders.LOCATION, "http://passport.gmall.com/login.html?originUrl=" + request.getURI());

                    return response.setComplete();
                }
            }
        }


        if (!StringUtils.isEmpty(userId) || !StringUtils.isEmpty(userTempId)) {
            if (!StringUtils.isEmpty(userId)) {

                request.mutate().header("userId", userId).build();
            }

            if (!StringUtils.isEmpty(userTempId)) {

                request.mutate().header("userTempId", userTempId).build();
            }

            return chain.filter(exchange.mutate().request(request).build());
        }

        return chain.filter(exchange);
    }


    private String getUserTempId(ServerHttpRequest request) {

        String userTempId = "";
        HttpCookie httpCookie = request.getCookies().getFirst("userTempId");
        if (httpCookie != null) {
            userTempId = httpCookie.getValue();
        } else {
            List<String> stringList = request.getHeaders().get("userTempId");
            if (!CollectionUtils.isEmpty(stringList)) {
                userTempId = stringList.get(0);
            }
        }
        return userTempId;
    }


    private String getUserId(ServerHttpRequest request) {

        String token = "";

        HttpCookie httpCookie = request.getCookies().getFirst("token");
        if (httpCookie != null) {
            token = httpCookie.getValue();
        } else {

            List<String> stringList = request.getHeaders().get("token");
            if (!CollectionUtils.isEmpty(stringList)) {
                token = stringList.get(0);
            }
        }


        if (!StringUtils.isEmpty(token)) {

            String userLoginKey = "user:login:" + token;

            String strJson = (String) this.redisTemplate.opsForValue().get(userLoginKey);
            if (!StringUtils.isEmpty(strJson)) {

                JSONObject jsonObject = JSON.parseObject(strJson);

                String ip = (String) jsonObject.get("ip");

                if (IpUtil.getGatwayIpAddress(request).equals(ip)) {

                    String userId = (String) jsonObject.get("userId");
                    return userId;
                } else {
                    return "-1";
                }
            }
        }

        return "";
    }

    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {

        Result result = Result.build(null, resultCodeEnum);

        String str = JSON.toJSONString(result);

        DataBuffer wrap = response.bufferFactory().wrap(str.getBytes());

        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        return response.writeWith(Mono.just(wrap));
    }
}