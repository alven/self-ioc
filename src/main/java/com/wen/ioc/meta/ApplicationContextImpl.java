/*
 *
 *  * Copyright (c) 2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.wen.ioc.meta;

import com.wen.ioc.annotation.Bean;
import com.wen.ioc.annotation.Config;
import com.wen.ioc.exception.IocException;
import com.wen.ioc.util.ClassUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author awlwen
 * @since 2018/7/6
 */
public class ApplicationContextImpl implements ApplicationContext{
    private Map beanMap = new HashMap();

    public ApplicationContextImpl(String pkgName) throws IocException {
        //get bean Config
        pkgName = pkgName.replaceAll("\\.", File.separator);
        List<Class<?>> classes = ClassUtil.getClasses(pkgName);
        //find config bean
        List<Class<?>> configClass = findConfigBean(classes);

        if(configClass.isEmpty()){
            throw new IocException("can`t find config class");
        }

        configClass.forEach(item -> {
            Method[] methods = item.getMethods();
            for (Method method : methods) {
                if(method.getAnnotation(Bean.class) != null){
                    try {
                        Object object = item.newInstance();
                        Object returnType = method.invoke(object, null);
                        beanMap.put(method.getName(), returnType);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //init bean map
        //initBeanMap(classes);
    }

    private List<Class<?>> findConfigBean(List<Class<?>> classes) {
        List<Class<?>> configClass = new ArrayList<>();
        classes.forEach(item -> {
            if(item.isAnnotationPresent(Config.class)){
                configClass.add(item);
            }
        });
        return configClass;
    }

    private void initBeanMap(List<Class<?>> classes) {
        classes.forEach(item -> beanMap.put(item.getName(), item));
    }

    @Override
    public Object getBeanByName(String beanName) {
        return beanMap.get(beanName);
    }
}
