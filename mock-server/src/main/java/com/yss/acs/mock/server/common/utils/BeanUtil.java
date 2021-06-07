package com.yss.acs.mock.server.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Bean工具类
 *
 * @author jiayy
 * @date 2020/7/2
 */
@Component
public class BeanUtil implements ApplicationContextAware {
	
    private static ApplicationContext context;
    
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
    
    public static <T> T getBean(Class<T> clazz) {
    	
        return getBean(clazz, null);
    }
    
    public static <T> T getBean(Class<T> clazz, String beanName) {
		Map<String, T> beanMap = context.getBeansOfType(clazz);
		if(beanMap != null && !beanMap.isEmpty()) {
			if(beanName != null) {
		        return beanMap.get(beanName);
			}
			return (T) beanMap.values().toArray()[0];
		}
		return null;
    }
 
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return clazz.cast(getBean(beanName));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;  
    }  
      
    public static ApplicationContext getApplicationContext() {
        return context;  
    }
}