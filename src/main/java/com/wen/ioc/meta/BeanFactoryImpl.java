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

import com.wen.ioc.annotation.AutoWired;
import com.wen.ioc.annotation.Bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author awlwen
 * @since 2018/7/6
 */
public class BeanFactoryImpl implements BeanFactory {
    private Map beanMap = new HashMap();

    @Override
    public Map newInstance(List<Class<?>> configClass) {
        configClass.forEach(item -> {
            Method[] methods = item.getMethods();
            for (Method method : methods) {
                if(method.getAnnotation(Bean.class) != null){
                    try {
                        Object object = item.newInstance();
                        Parameter[] parameters = method.getParameters();
                        Object[] objects = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i ++) {
                            Parameter parameter = parameters[i];
                            AutoWired autoWired = parameter.getAnnotation(AutoWired.class);
                            Object diObject = beanMap.get(autoWired.name());
                            objects[i] = diObject;
                        }
                        Object returnType = method.invoke(object, objects);
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
        return beanMap;
    }
}
