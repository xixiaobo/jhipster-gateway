package com.hcycom.jhipster.web.rest.tool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class HttpClientTool {
	private final static Logger log = LoggerFactory.getLogger(HttpClientTool.class);
	
	public static Map<String, Object> login(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod("http://localhost:8080/auth/login");
		String username = params.get("username");
		String password = params.get("password");
        boolean rememberMe = Boolean.valueOf(params.get("rememberMe"));
		if (username.trim().equals("") || password.trim().equals("")) {
			System.out.println("账号密码为空！");
			throw new Exception("账号密码为空！");
		}
		String data = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"rememberMe\":\"" + rememberMe + "\"}";

		method.setRequestBody(data);
		String XSRF = GetCSRF();
		// 设置请求的编码方式
		method.addRequestHeader("Content-Type", "application/json");
		method.addRequestHeader("Authorization",
				"Basic d2ViYXBwOm15LXNlY3JldC10b2tlbi10by1jaGFuZ2UtaW4tcHJvZHVjdGlvbg==");
		method.setRequestHeader("cookie", "XSRF-TOKEN=" + XSRF);
		method.setRequestHeader("X-XSRF-TOKEN", XSRF);
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		int statusCode = client.executeMethod(method);
		
		log.info("uaa认证登录服务器状态："+method.getStatusLine());
		
		//处理json串  
        String json = method.getResponseBodyAsString();  
        JSONObject jsonStr = JSONObject.fromObject(json);
        
        map=jsonStr;
        map.put("statusCode",statusCode+"");
		
		// 释放连接
		method.releaseConnection();
		return map;
	}

	public static String GetCSRF() throws Exception {

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("http://localhost:8080/h2-console/csrf");
		// 设置请求的编码方式
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + "utf-8");
		method.addRequestHeader("Accept", "application/json");
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		int statusCode = client.executeMethod(method);

		Cookie[] cookies = client.getState().getCookies();
		String XSRF = null;
		for (Cookie c : cookies) {
			System.out.println("cookies = " + c.toString());
			String[] s = c.toString().split("=");
			if (s[0].equals("XSRF-TOKEN")) {
				XSRF = s[1];
			}
		}

		log.info("XSRF获取完毕："+XSRF);
//		 if (statusCode != HttpStatus.SC_OK) {// 打印服务器返回的状态
//		System.out.println("Method failed: " + method.getStatusLine());
//		 }
//		 释放连接
		method.releaseConnection();
		return XSRF;
	}
}
