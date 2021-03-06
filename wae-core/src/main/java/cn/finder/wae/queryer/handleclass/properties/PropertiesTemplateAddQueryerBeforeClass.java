package cn.finder.wae.queryer.handleclass.properties;

import java.util.Map;

import cn.finder.ui.webtool.QueryCondition;
import cn.finder.wae.business.dto.MapParaQueryConditionDto;
import cn.finder.wae.queryer.handleclass.QueryerDBBeforeClass;

/**
 * @author: wuhualong
 * @data:
 * @function: 属性模板添加前置处理
 */
public class PropertiesTemplateAddQueryerBeforeClass extends QueryerDBBeforeClass {

	
	
	@Override
	public void handle(long showTableConfigId,
			QueryCondition<Object[]> condition) {
		
		super.handle(showTableConfigId, condition);

		logger.debug("====================PropertiesTemplateAddQueryerBeforeClass.handle ");
		logger.debug("====================showTableConfigId: "+showTableConfigId);
		
		Map<String, Object> data = ((MapParaQueryConditionDto<String, Object>)condition).getMapParas();
		
		
		String item_fk_key_value= data.get("item_fk_key_value").toString();
		
		data.put("properties_template_type_id", item_fk_key_value);
		
	}
	
	

}
