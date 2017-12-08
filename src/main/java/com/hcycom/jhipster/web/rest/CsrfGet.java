package com.hcycom.jhipster.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/h2-console")
@Api(tags = { "CSRF管理" }, description = "CSRF获取接口")
public class CsrfGet {

	@RequestMapping(value = "/csrf", method = RequestMethod.GET)
	@Timed
	@ApiOperation(value = "获取CSRF", notes = "获取CSRF")
	public ResponseEntity<Map<String, Object>> csrf(HttpServletResponse response,HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
//		String csrf=HttpClientTool.GetCSRF();
//		Cookie cookie = new Cookie("XSRF-TOKEN", csrf);
//		cookie.setPath("/");
//		response.addCookie(cookie);
		CookieCsrfTokenRepository repository=new CookieCsrfTokenRepository();
		CsrfToken token =repository.generateToken(request);
		repository.saveToken(token, request, response);
		map.put("CsrfToken", token);
		map.put("code", 1);

		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
}
