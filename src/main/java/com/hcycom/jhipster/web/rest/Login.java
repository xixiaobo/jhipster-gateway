package com.hcycom.jhipster.web.rest;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hcycom.jhipster.domain.User;
import com.hcycom.jhipster.service.UserSrevice;
import com.hcycom.jhipster.web.rest.tool.HttpClientTool;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 跳转登录接口
 * 
 * @author Xi
 *
 */
@RestController
@RequestMapping("/auth")
@Api(tags = { "登录管理" }, description = "登录接口")
public class Login {
	private final Logger log = LoggerFactory.getLogger(Login.class);

	@Autowired
	private UserSrevice userSrevice;

	@RequestMapping(value = "/userlogin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@ApiOperation(value = "登录系统", notes = "根据用户名和密码登录系统，并返回用户信息和token")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> params,
			HttpServletResponse response) throws Exception {
		String username = params.get("username");
		String password = params.get("password");
		boolean rememberMe = Boolean.valueOf(params.get("rememberMe"));

		// 通过httpclient工具实现uaa认证登录并返回oauth2的token
		Map<String, Object> map = HttpClientTool.login(params);

		if (map.get("statusCode").equals("200")) {
			log.info("loginng  user {}", SecurityContextHolder.getContext().getAuthentication().getName());
			String access_token = (String) map.get("access_token");
			String refresh_token = (String) map.get("refresh_token");
			int expires_in = (int) map.get("expires_in");
			// 将token放到cookie中
			Cookie cookie = new Cookie("access_token", access_token);
			cookie.setPath("/");
			cookie.setMaxAge(expires_in);
			Cookie cookie2 = new Cookie("session_token", refresh_token);
			cookie2.setPath("/");
			cookie2.setMaxAge(expires_in);
			response.addCookie(cookie);
			response.addCookie(cookie2);

			User user = userSrevice.findeUserByName(username);
			map.put("data", user);
			map.put("error_code", 0);
			// 获取用户详情

		} else if (map.get("statusCode").equals("500")) {
			log.info("登录失败");
			map.put("error_code", 1);
		} else {

			map.put("error_code", 2);
		}

		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

}
