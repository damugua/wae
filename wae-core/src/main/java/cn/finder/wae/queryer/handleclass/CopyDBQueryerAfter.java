package cn.finder.wae.queryer.handleclass;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;







import cn.finder.ui.webtool.QueryCondition;
import cn.finder.wae.business.domain.TableQueryResult;

import cn.finder.wae.common.thread.AppContent;
import cn.finder.wae.queryer.handleclass.QueryerDBAfterClass;

/**
 * @author: wuhualong
 * @data:2014-4-23下午2:15:59
 * @function:生成设备 槽位  参数验证
 */
public class CopyDBQueryerAfter  extends QueryerDBAfterClass {

	/* (non-Javadoc)
	 * @see cn.finder.wae.queryer.handleclass.QueryerAfterClass#handle(cn.finder.wae.business.domain.TableQueryResult, long, cn.finder.ui.webtool.QueryCondition)
	 */
	@Override
	public TableQueryResult handle(TableQueryResult tableQueryResult,long showTableConfigId,QueryCondition<Object[]> condition){
		super.handle(tableQueryResult, showTableConfigId, condition);
		
		
		
		if(tableQueryResult==null || tableQueryResult.getResultList()==null || tableQueryResult.getResultList().size()==0){
			tableQueryResult.setFields(null);
			return tableQueryResult;
		}
		
		List<Map<String, Object>> resultList= tableQueryResult.getResultList();
		
		HttpServletRequest req = AppContent.getRequest();
		String path = req.getRealPath("/")+"copydb";
		 File file = new File(path);
		 if(!file.exists()){
			 file.mkdir();
		 }
		 resultList.clear();
		 if(file.listFiles()!=null){
			 for(int i =0;i<file.listFiles().length; i++){
				 String name = file.listFiles()[i].getName();
				 long time = file.listFiles()[i].lastModified();
				 Date date = new Date(time);
				 Map<String,Object> map = new HashMap<String,Object>();
				 map.put("名称", name);
				 map.put("创建时间", date);
				 resultList.add(map);
			 }
		 }
		return tableQueryResult;
	}
	
	
	
	 
	
	
}
