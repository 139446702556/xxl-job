package com.xxl.job.core.glue;

import com.xxl.job.core.glue.impl.SpringGlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import groovy.lang.GroovyClassLoader;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * glue factory, product class/object by name
 * glue 工厂
 *
 * @author xuxueli 2016-1-2 20:02:27
 */
public class GlueFactory {


	private static GlueFactory glueFactory = new GlueFactory();
	public static GlueFactory getInstance(){
		return glueFactory;
	}
	public static void refreshInstance(int type){
		if (type == 0) {
			glueFactory = new GlueFactory();
		} else if (type == 1) {
			glueFactory = new SpringGlueFactory();
		}
	}


	/**
	 * groovy class loader
	 */
	private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
	private ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

	/**
	 * load new instance, prototype
	 * 加载给定源码，创建新的实例对象，并对其中的field进行注入
	 * @param codeSource
	 * @return
	 * @throws Exception
	 */
	public IJobHandler loadNewInstance(String codeSource) throws Exception{
		if (codeSource!=null && codeSource.trim().length()>0) {
			Class<?> clazz = getCodeSourceClass(codeSource);
			if (clazz != null) {
				Object instance = clazz.newInstance();
				if (instance!=null) {
					if (instance instanceof IJobHandler) {
						//注入指定bean对象到实例化对象中，并返回
						this.injectService(instance);
						return (IJobHandler) instance;
					} else {
						throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
								+ "cannot convert from instance["+ instance.getClass() +"] to IJobHandler");
					}
				}
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
	}
	private Class<?> getCodeSourceClass(String codeSource){
		try {
			// md5
			// 对给定源码进行md5加密（唯一标识）
			byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
			String md5Str = new BigInteger(1, md5).toString(16);
			// 从以加载脚本容器中获取对应的类对象
			Class<?> clazz = CLASS_CACHE.get(md5Str);
			//如果内存中不存在，则对传递来的源码进行解析得到对应类对象，并保存到容器中
			if(clazz == null){
				clazz = groovyClassLoader.parseClass(codeSource);
				CLASS_CACHE.putIfAbsent(md5Str, clazz);
			}
			return clazz;
		} catch (Exception e) {
			return groovyClassLoader.parseClass(codeSource);
		}
	}

	/**
	 * inject service of bean field
	 *
	 * @param instance
	 */
	public void injectService(Object instance) {
		// do something
	}

}
