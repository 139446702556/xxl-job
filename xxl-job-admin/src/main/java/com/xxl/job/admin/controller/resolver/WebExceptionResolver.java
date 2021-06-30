package com.xxl.job.admin.controller.resolver;

import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.admin.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * common exception resolver
 * 公共异常解析器（未捕获异常，api执行异常）
 * @author xuxueli 2016-1-6 19:22:18
 */
@Component
public class WebExceptionResolver implements HandlerExceptionResolver {
	private static transient Logger logger = LoggerFactory.getLogger(WebExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		//如果不是xxljob内置异常，则进行log记录
		if (!(ex instanceof XxlJobException)) {
			logger.error("WebExceptionResolver:{}", ex);
		}

		// if json
		// 判断当前接口返回结果类型是否为json
		boolean isJson = false;
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod)handler;
			ResponseBody responseBody = method.getMethodAnnotation(ResponseBody.class);
			if (responseBody != null) {
				isJson = true;
			}
		}

		// error result
		// 初始化错误异常信息，并以当前接口返回数据类型返回异常信息
		ReturnT<String> errorResult = new ReturnT<String>(ReturnT.FAIL_CODE, ex.toString().replaceAll("\n", "<br/>"));

		// response
		ModelAndView mv = new ModelAndView();
		if (isJson) {
			try {
				response.setContentType("application/json;charset=utf-8");
				response.getWriter().print(JacksonUtil.writeValueAsString(errorResult));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			return mv;
		} else {

			mv.addObject("exceptionMsg", errorResult.getMsg());
			mv.setViewName("/common/common.exception");
			return mv;
		}
	}
	
}