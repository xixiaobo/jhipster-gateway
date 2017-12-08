package com.hcycom.jhipster.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcycom.jhipster.domain.Attribute_values;
import com.hcycom.jhipster.domain.Resource;
import com.hcycom.jhipster.domain.User;
import com.hcycom.jhipster.service.mapper.AttributeMapper;
import com.hcycom.jhipster.service.mapper.Attribute_valuesMapper;
import com.hcycom.jhipster.service.mapper.ResourceMapper;
import com.hcycom.jhipster.service.mapper.RoleMapper;

import net.sf.json.JSONObject;

/**
 * 用户管理服务
 * @author Xi
 *
 */
@Service
public class UserSrevice {

	@Autowired
	private Attribute_valuesMapper attribute_valuesMapper;
	@Autowired
	private AttributeMapper attributeMapper;
	@Autowired
	private ResourceMapper resourceMapper;
	@Autowired
	private RoleMapper roleMapper;
	
	
	public User findeUserByName(String username){
	User user = new User();
		Resource resource = resourceMapper.findResoureBySave_table("user");
		List<Attribute_values> list = attribute_valuesMapper.findUserByName(resource.getResource_name(), username);
		Map map = new HashMap();
		for (Attribute_values attribute_values : list) {
			map.put(attribute_values.getAttribute_key(), attribute_values.getValue());
		}
		JSONObject json = JSONObject.fromObject(map);
		user = (User) JSONObject.toBean(json, User.class);
		Set<String> authorities = new HashSet<>();
		String[] rolesids = user.getRoles().split(",");
		for (String rolesid : rolesids) {
			authorities.add(roleMapper.getUsersAuthority(rolesid).getRole_name());
		}
		user.setAuthorities(authorities);	
		return user;
	}
}
