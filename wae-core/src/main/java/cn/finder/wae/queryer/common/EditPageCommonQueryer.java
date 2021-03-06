package cn.finder.wae.queryer.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import cn.finder.ui.webtool.QueryCondition;
import cn.finder.wae.business.domain.ShowDataConfig;
import cn.finder.wae.business.domain.ShowTableConfig;
import cn.finder.wae.business.domain.TableQueryResult;
import cn.finder.wae.business.domain.User;
import cn.finder.wae.business.dto.MapParaQueryConditionDto;
import cn.finder.wae.cache.ConstantsCache;
import cn.finder.wae.cache.ArchCache;
import cn.finder.wae.cache.ShowTableConfigCache;
import cn.finder.wae.common.comm.JsonUtil;
import cn.finder.wae.common.constant.Constant;
import cn.finder.wae.common.db.BaseJdbcDaoSupport;
import cn.finder.wae.common.db.DBType;
import cn.finder.wae.common.db.ds.DSManager;


/***
 * 编辑显示页面
 * @author wu hualong
 *
 */
public class EditPageCommonQueryer extends  BaseCommonDBQueryer {

	
	private final static Logger logger = Logger.getLogger(EditPageCommonQueryer.class);
	
	@Override
	public TableQueryResult queryTableQueryResult(long showTableConfigId,
			QueryCondition<Object[]> condition) {
		
		logger.debug("=====go into EditPageCommonQueryer.queryTableQueryResult======");
		
		logger.debug("==showTableConfigId:"+showTableConfigId);
		
		if(condition!=null)
		{
			logger.debug("==Sql:"+condition.getSql());
			logger.debug("==WhereCluster:"+condition.getWhereCluster());
			logger.debug("==WherepParameterValues:"+condition.getWherepParameterValues());
			logger.debug("==pageIndex:"+condition.getPageIndex());
			logger.debug("==pageSize:"+condition.getPageSize());
		}
		
		final TableQueryResult qr =new TableQueryResult();
		final MapParaQueryConditionDto<String, Object> mapCondition=(MapParaQueryConditionDto<String, Object>)condition;
		
		
		//从缓存中获取 ShowTableConfig 配置
		ShowTableConfig showTableConfig = ArchCache.getInstance().getShowTableConfigCache().get(showTableConfigId);
		if(condition.getPageSize()==0){
	    	//默认为数据库设置大小
	    	condition.setPageSize(showTableConfig.getPageSize()==0?10:showTableConfig.getPageSize());
	    }
		
		
	    final List<ShowDataConfig> showDataConfigs =showTableConfig.getShowDataConfigs();
	    
	    StringBuffer sql_sb =new StringBuffer();
	    
	   // StringBuffer sql_cnt_sb = new StringBuffer();
	    
	    
	    // "id 编号,name 名称"
	    StringBuffer fields_sb =new StringBuffer();
	    
	    // where 后面的条件
	    StringBuffer where_sb =new StringBuffer();
	    
	    
	   final  List<String> filedNames=new ArrayList<String>();
	   
	    
	    for(ShowDataConfig df:showDataConfigs)
	    {
	    	filedNames.add(df.getFieldName()+" '"+df.getFieldNameAlias()+"'");
	    }
	    
	    fields_sb.append(StringUtils.join(filedNames, ','));
	    
	    
	    if(!StringUtils.isEmpty(condition.getWhereCluster()))
	    {
	    	 where_sb.append(condition.getWhereCluster());
	    }
	    
	    if(StringUtils.isEmpty(condition.getWhereCluster()))
	    {
	    	 where_sb.append(showTableConfig.getSqlConfig());
	    }
	    
	    //where_sb.append(showTableConfig.getSqlConfig());
	    
	    
	    sql_sb.append("select ").append(fields_sb+" ").append(" from ").append(showTableConfig.getShowTableName()).append(" where 1=1 ");
	    
	    //sql_cnt_sb.append("select ").append(" count(*) cnt ").append(" from ").append(showTableConfig.getShowTableName()).append(" where 1=1 ");
	    //oracle
	    //sql_sb.append(" and ").append(" (rownum >").append((condition.getPageIndex()-1)*condition.getPageSize()).append(" and ").append(" rownum <").append(condition.getPageSize()+") ");
	    
	    
	  
	    
	    if(!StringUtils.isEmpty(where_sb.toString())){
	    	if(where_sb.toString().trim().toLowerCase().startsWith("order by"))
	    	{
	    		sql_sb.append(" ").append(where_sb.toString());
	    		//sql_cnt_sb.append(" ").append(where_sb.toString()); //统计数量不需要排序
	    	}else{
	    		sql_sb.append("and ").append(where_sb.toString());
	    		
	    		String cnt_where =where_sb.toString();
	    	
	    		//需要去掉order by
	    		int order_idx=cnt_where.toLowerCase().indexOf("order");
	    		if(order_idx!=-1)
	    		{
		    		cnt_where= cnt_where.substring(0, order_idx);
		    		
		    		//sql_cnt_sb.append("and ").append(cnt_where);
	    		}
	    		else{
	    		//sql_cnt_sb.append("and ").append(cnt_where);
	    		}
	    	}
	    }
	  //mysql 
	  //  sql_sb.append(" limit ").append((condition.getPageIndex()-1)*condition.getPageSize()).append(",").append(condition.getPageSize());
	    sql_sb =new StringBuffer(wrapperPagerSql(sql_sb.toString(), condition.getPageIndex(), condition.getPageSize()));
	    
	    final Object[] params = condition.getWherepParameterValues();
	   
	    logger.debug("== 查询数据SQL："+sql_sb.toString());
	    
	    
	    List<Map<String, Object>>  dataList=getJdbcTemplate().query(sql_sb.toString(),params,new RowMapper<Map<String,Object>>(){

			@Override
			public Map<String, Object> mapRow(ResultSet rs, int index)
					throws SQLException {
				
				Map<String,Object> item = new HashMap<String, Object>();
				
				/*
				for(String field:filedNames)
				{
					item.put(field, rs.getObject(field));
				}*/
				
				
				
				for(int i=0;i<showDataConfigs.size();i++)
				{
					ShowDataConfig itemDC=showDataConfigs.get(i);
					itemDC.setOldText(null);//要将之前的值清空
					itemDC.setOldValue(null);
					
					Object itemValue = rs.getObject(itemDC.getFieldNameAlias());
					
					if(StringUtils.isEmpty(itemDC.getParentTableName()))
					{
						//如果父表 没有填写，那么直接把值放入
						//item.put(itemDC.getFieldName(), rs.getObject(itemDC.getFieldNameAlias()));
						
						/*if(itemDC.getShowType() == cn.finder.wae.cache.ConstantsCache.ControlType.CONTROLTYPE_PROPERTY_DATA_ENCODING_DECODING_TEXTBOX)
						{
							//解码
							if(itemValue!=null){
								try {
									itemValue = URLDecoder.decode(itemValue.toString(),"utf-8");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
						}*/
						
						item.put(itemDC.getFieldNameAlias(), itemValue);
					}
					else
					{
					/*	if(itemDC.getShowType()==ConstantsCache.ControlType.CONTROLTYPE_POPUP_TABLESELECT){
							//弹出式 选择
							item.put(itemDC.getFieldNameAlias(), rs.getObject(itemDC.getFieldNameAlias()));
						}
						*/
						//加载 父表对应数据
						
						//这里就先做一级 数据显示
						//这里就先做一级 数据显示
						
						
						if(ConstantsCache.isLoadComboxData(itemDC.getShowType()))
//						if(itemDC.getShowType()==ConstantsCache.ControlType.CONTROLTYPE_COMBOX || itemDC.getShowType()==ConstantsCache.ControlType.CONTROLTYPE_CASCADE_COMBOX||itemDC.getShowType()==ConstantsCache.ControlType.CONTROLTYPE_POPUP_TABLESELECT)
						{
							logger.debug("====="+itemDC.getFieldName()+" "+itemDC.getFieldNameAlias());
//							//处理组合框
//							//查找父所有数据
//							Map<String,Object> p_value_map=getJdbcTemplate().queryForMap(itemDC.getParentSelectSql());
//							
//							item.put(itemDC.getFieldName(), p_value_map);
//						}
//						else{
							//根据 表名称获取  showtableConfigId
							ShowTableConfig p_ShowTableConfig=ArchCache.getInstance().getShowTableConfigCache().getShowTableConfig(itemDC.getParentTableName());
							
							MapParaQueryConditionDto<String, Object> p_condition=new MapParaQueryConditionDto<String, Object>();

							p_condition.setPageIndex(1);
							p_condition.setPageSize(1000000000);
							
							p_condition.setSearchObject(new Object[]{false});
							
							Map<String,Object> dataMap = new HashMap<String,Object>();
							dataMap.put("session_user",  mapCondition.getMapParas().get("session_user"));
							p_condition.setMapParas(dataMap);
								
								//将原字段值赋给oldValue字段
								itemDC.setOldValue(rs.getObject(itemDC.getFieldNameAlias()));
								
								

								EditPageCommonQueryer editPageCommonQueryer=new EditPageCommonQueryer();
								editPageCommonQueryer.setDataSource(DSManager.getDataSource(p_ShowTableConfig.getTargetDs()));
								// item.put(itemDC.getFieldNameAlias(), queryParentTableQueryResult(p_ShowTableConfig.getId(),p_condition));
								BaseJdbcDaoSupport baseJdbcDaoSupport = (BaseJdbcDaoSupport)editPageCommonQueryer;
								DataSource ds = DSManager.getDataSource(p_ShowTableConfig.getTargetDs());
								BasicDataSource basicDataSource = (BasicDataSource)ds;
								String driverClassName = basicDataSource.getDriverClassName();
								//判断数据库类型
								if(driverClassName.toLowerCase().indexOf("mysql")!=-1){
									baseJdbcDaoSupport.setDbType(DBType.MySql);
								}else if(driverClassName.toLowerCase().indexOf("sqlserver")!=-1){
									
									baseJdbcDaoSupport.setDbType(DBType.SqlServer);
								}
								else if(driverClassName.toLowerCase().indexOf("oracle")!=-1){
									
									baseJdbcDaoSupport.setDbType(DBType.Oracle);
								}
								
								
								
								
								
								if(itemDC.getShowType()== ConstantsCache.ControlType.CONTROLTYPE_POPUP_TABLESELECT){ //根据条件匹配值
									//只需要设置旧的值
									//String sql ="select "+itemDC.getParentTableKey()+","+itemDC.getParentShowField()+" FROM "+itemDC.getParentTableName()+" where "+itemDC.getParentTableKey()+"=?";
									
									p_condition.setPageSize(1);
									
								   ShowDataConfig p_sdc=ArchCache.getInstance().getShowTableConfigCache().getShowDataConfigForFieldNameAlian(p_ShowTableConfig.getId(), itemDC.getParentTableKey());
								   p_condition.setForwardParams(mapCondition.getForwardParams());
									p_condition.setWhereCluster(p_sdc.getFieldName()+"=?");
									p_condition.setWherepParameterValues(new Object[]{itemValue});
									
									
									//TableQueryResult tableQueryResult = editPageCommonQueryer.queryTableQueryResult(p_ShowTableConfig.getId(),p_condition);
									//下拉列查询表配置
									TableQueryResult tableQueryResult = editPageCommonQueryer.queryTableQueryResultManager(p_ShowTableConfig.getId(),p_condition);
									
									item.put(itemDC.getFieldNameAlias(), tableQueryResult);
									
									try
									{
										itemDC.setOldText(tableQueryResult.getResultList().get(0).get(itemDC.getParentShowField()).toString());
									}
									catch(Exception e){
										
									}
								}else{
									
									TableQueryResult tableQueryResult = editPageCommonQueryer.queryTableQueryResultManager(p_ShowTableConfig.getId(),p_condition);
									
									if(itemDC.getOldValue() != null){
										List<Map<String, Object>>  list = tableQueryResult.getResultList();
										for(Map<String, Object> map:list){//遍历查询到的记录
											String key = itemDC.getParentTableKey();
											if(map.get(key) != null){
												Object value = map.get(key);
												String text = String.valueOf(map.get(itemDC.getParentShowField()));
												if(itemDC.getOldValue().toString().equals(value.toString())){//匹配
													itemDC.setOldText(text);
												}
											}
											
										}
									}
									
									item.put(itemDC.getFieldNameAlias(), tableQueryResult);
								}
//							}
							
						
							
							
							
						}
						
						
					}
					
					
					 //处理 列数字统计
					   //statistics
					
					if(!StringUtils.isEmpty(itemDC.getStatistics()))
					{
						
						StringBuffer statistics_sb = new StringBuffer();
						
						//包含where条件
						statistics_sb.append(itemDC.getStatistics());
						
						String staticsString=getJdbcTemplate().queryForObject(statistics_sb.toString(), params, String.class);
						
						//qr.getStatistics().put(itemDC.getFieldName(), staticsString);
						
						qr.getStatistics().put(itemDC.getFieldNameAlias(), staticsString);
					}
				}
				
				return item;
			}
	    	
	    	
	    });
	    
	    
	  //  logger.debug("== 查询记录数SQL："+sql_cnt_sb.toString());
	    
	 //  long cnt =  getJdbcTemplate().queryForLong(sql_cnt_sb.toString(), params);
	    
	    
	  //  logger.debug("== 总记录数:"+cnt);
	  
	   
	  
	   
	   
	    
	    qr.setResultList(dataList);
		qr.setFields(showDataConfigs);
		qr.setPageIndex(condition.getPageIndex());
		qr.setPageSize(condition.getPageSize());
		qr.setCount(0l);
		
		
		
		//设置grid menus
		//qr.setToolbarGridMenus(ArchCache.getInstance().getShowTableConfigCache().get(showTableConfigId).getToolBarMenus());
		//qr.setToolbarGridMenus(ArchCache.getInstance().getShowTableConfigCache().get(showTableConfigId).getRowFrontMenus());
		//qr.setToolbarGridMenus(ArchCache.getInstance().getShowTableConfigCache().get(showTableConfigId).getRowBackMenus());
		//为了方便 就先直接设置ShowTableConfig到前台，但是数据量有点大
		qr.setShowTableConfig(showTableConfig);
		return qr;
	}

	public void setJDBCDataSource(DataSource dataSource) {
		super.setJdbcDataSource(dataSource);
	}

}
