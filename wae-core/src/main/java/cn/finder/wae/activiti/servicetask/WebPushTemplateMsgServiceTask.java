package cn.finder.wae.activiti.servicetask;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.finder.wae.business.domain.TemplateMsg;
import cn.finder.wae.business.module.common.service.CommonService;
import cn.finder.wae.common.comm.templatemsg.WebTemplateMsgSender;
import cn.finder.wae.common.thread.AppContent;

import common.Logger;
/***
 * 通用的 网页版消息推送-默认通道 default_channel
 * 需要任务参数:
 * commandName: 命令名称cmd_xxx
 * commandValue: json格式{showtableConfigId:1,id:2}
 * templateMsgCode:消息模板编码
 * toUserId: 接收者账户
 * pushChannel:default_channel
 * @author whl
 *
 */
public class WebPushTemplateMsgServiceTask implements JavaDelegate {

	private Logger logger=Logger.getLogger(WebPushTemplateMsgServiceTask.class);
	CommonService commonService= WebApplicationContextUtils.getWebApplicationContext(AppContent.getSession().getServletContext()).getBean("commonService", CommonService.class);
			
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {

		//应该是获取到当前 服务任务中设置的 变量
	    final Map<String,Object> variables= execution.getVariablesLocal();
		
	    
	    Object templateMsgCode=variables.get("templateMsgCode");
	    if(templateMsgCode==null)
	      logger.error("can't find parameter:templateMsgCode");
	    
	    TemplateMsg tm= commonService.findTemplateMsg(templateMsgCode.toString());
	   
	    WebTemplateMsgSender sender=new WebTemplateMsgSender(tm);
	    
	    sender.send(variables);
	    
	    
	}
	
	
}
