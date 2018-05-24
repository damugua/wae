package cn.finder.wae.global.api.response;import java.util.List;import cn.finder.httpcommons.attri.JsonArrayAttribute;import cn.finder.httpcommons.attri.JsonArrayItemAttribute;import cn.finder.httpcommons.response.ApiResponse;import cn.finder.wae.global.api.domain.Media;	/***	* 资源字段描述 	*/public class MediaDescInfoResponse extends ApiResponse{		private List<Media> entities;	@JsonArrayAttribute(name="entities")	@JsonArrayItemAttribute(clazzType=Media.class)	public void setEntities(List<Media> entities)	{		this.entities = entities;	}	public	List<Media> getEntities()	{		return entities;	}		/***	 * 获取媒体信息	 * @return	 */	public Media getEntity(){		if(entities!=null && entities.size()>0)			return entities.get(0);		return null;	}}