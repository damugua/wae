package cn.finder.wae.api.sdk.response;

import java.util.List;

import cn.finder.httpcommons.attri.JsonArrayAttribute;
import cn.finder.httpcommons.attri.JsonArrayItemAttribute;
import cn.finder.httpcommons.response.ApiResponse;
import cn.finder.wae.api.sdk.domain.User;

public class UserLoginResponse extends ApiResponse {

	private List<User> entities;

	public List<User> getEntities() {
		return entities;
	}
	
	@JsonArrayAttribute(name="entities")
	@JsonArrayItemAttribute(clazzType=User.class)
	public void setEntities(List<User> entities) {
		this.entities = entities;
	}
	
	public User getUser(){
		if(this.entities!=null&&entities.size()>0){
			return entities.get(0);
		}
		return null;
	}
}
