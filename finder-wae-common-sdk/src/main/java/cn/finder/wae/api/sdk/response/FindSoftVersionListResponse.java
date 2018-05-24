package cn.finder.wae.api.sdk.response;import java.util.List;import cn.finder.httpcommons.attri.JsonArrayAttribute;import cn.finder.httpcommons.attri.JsonArrayItemAttribute;import cn.finder.httpcommons.response.ApiResponse;import cn.finder.wae.api.sdk.domain.SoftVersion;	/***	* 获取软件版本信息响应	*/public class FindSoftVersionListResponse extends ApiResponse{	private List<SoftVersion> entities;	@JsonArrayAttribute(name="entities")	@JsonArrayItemAttribute(clazzType=SoftVersion.class)	public	void	setEntities(List<SoftVersion> entities)	{		this.entities = entities;	}	public	List<SoftVersion> getEntities()	{		return entities;	}}