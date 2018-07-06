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

package com.wen.ioc.util;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author awlwen
 * @since 2018/7/6
 */
public class ClassUtil {
    public static final String FILEPROTOCOL = "file";
    public static final String JARPROTOCOL = "jar";
    public static final String FILEDOT = ".";
    public static final String CLASSFILESUFFIX = ".class";

    public static List<Class<?>> getClasses(String pkgName){
        List<Class<?>> returnClass = null;
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pkgName);
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if(FILEPROTOCOL.equals(protocol)){
                    String filePath = url.getPath();
                    returnClass.addAll(getClasses(filePath));
                }else if(JARPROTOCOL.equals(protocol)){
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = connection.getJarFile();
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()){
                        JarEntry jarEntry = jarEntries.nextElement();
                        if(!jarEntry.isDirectory()){
                            String jarName = jarEntry.getName();
                            int lastSlashIndex = jarName.lastIndexOf(CLASSFILESUFFIX);
                            if(lastSlashIndex != -1){
                                jarName = jarName.substring(0, jarName.length() - CLASSFILESUFFIX.length());
                            }
                            jarName = jarName.replaceAll(File.separator, FILEDOT);
                            Class jarClass = Class.forName(jarName);
                            returnClass.add(jarClass);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnClass;
    }

}
