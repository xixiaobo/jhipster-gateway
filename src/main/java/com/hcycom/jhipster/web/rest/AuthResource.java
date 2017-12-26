package com.hcycom.jhipster.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hcycom.jhipster.domain.Attribute_values;
import com.hcycom.jhipster.security.oauth2.OAuth2AuthenticationService;
import com.hcycom.jhipster.service.mapper.Attribute_valuesMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Authentication endpoint for web client.
 * Used to authenticate a user using OAuth2 access tokens or log him out.
 *
 * @author markus.oellinger
 */
@RestController
@RequestMapping("/auth")
@Api(tags = { "登录管理" }, description = "登录接口")
public class AuthResource {

    private final Logger log = LoggerFactory.getLogger(AuthResource.class);
    

    @Autowired
    private Attribute_valuesMapper attribute_valuesMapper;
    
    private OAuth2AuthenticationService authenticationService;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResource(OAuth2AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Authenticates a user setting the access and refresh token cookies.
     *
     * @param request  the HttpServletRequest holding - among others - the headers passed from the client.
     * @param response the HttpServletResponse getting the cookies set upon successful authentication.
     * @param params   the login params (username, password, rememberMe).
     * @return the access token of the authenticated user. Will return an error code if it fails to authenticate the user.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType
        .APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "登录系统", notes = "根据用户名和密码登录系统，并返回token")
    public ResponseEntity<OAuth2AccessToken> authenticate(HttpServletRequest request, HttpServletResponse response, @RequestBody
        Map<String, String> params) {
    	String username = params.get("username");
		String password = params.get("password");
		List<Attribute_values> list = attribute_valuesMapper.findUserByName("user", username);
		if(list==null||list.size()==0){
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("msg", "用户不存在");
			map.put("error_code", 1);
	    	return new ResponseEntity(map,HttpStatus.OK);
		}else{
			String DBpassword=null;
			for (Attribute_values attribute_values : list) {
				if (attribute_values.getAttribute_key().equals("password")) {
					DBpassword=attribute_values.getValue();
				}
			}
			if(passwordEncoder.matches(password, DBpassword)){
				return authenticationService.authenticate(request, response, params);
			}else{
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("msg", "密码不正确");
				map.put("error_code", 1);
		    	return new ResponseEntity(map,HttpStatus.OK);
			}
		}
//    	Map<String, Object> map=new HashMap<String, Object>();
//    	return new ResponseEntity(map,HttpStatus.OK);
    }

    /**
     * Logout current user deleting his cookies.
     *
     * @param request  the HttpServletRequest holding - among others - the headers passed from the client.
     * @param response the HttpServletResponse getting the cookies set upon successful authentication.
     * @return an empty response entity.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @Timed
    @ApiOperation(value = "登出系统", notes = "用户登出")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("logging out user {}", SecurityContextHolder.getContext().getAuthentication().getName());
        authenticationService.logout(request, response);
        return ResponseEntity.ok(null);
    }
}
