package com.ly.train.flower.common.util;

import com.google.common.base.Predicate;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceFactory;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;
import java.util.regex.Pattern;

public class EnvBuilder {

  /**
   *
   * @param clz Reflections需要从clz类中获取ClassLoader的路径，再遍历Resources目录
   *            clz填写Resources对应的包下面的Class
   * @throws Exception
   */
  public static void buildEnv(Class clz) throws Exception {
    Predicate<String> filter = new FilterBuilder().include(".*\\.services").include(".*\\.flow");

    Reflections reflections = new Reflections(new ConfigurationBuilder()
            .filterInputsBy(filter)
            .setScanners(new ResourcesScanner())
            .setUrls(ClasspathHelper.forClass(clz)));

    Set<String> servicesFiles = reflections.getResources(Pattern.compile(".*\\.services"));
    for (String path : servicesFiles) {
      ServiceFactory.registerService(FileUtil.readService("/" + path));
    }
    Set<String> flowFiles = reflections.getResources(Pattern.compile(".*\\.flow"));
    for (String path : flowFiles) {
      String flowName = path.substring(0, path.lastIndexOf("."));
      ServiceFlow.buildFlow(flowName, FileUtil.readFlow("/" + path));
    }
  }
}
