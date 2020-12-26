package com.iakuil.em.util;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleUtils {
    private static final String DEFAULT_CLASS_OUTPUT_DIR = "target/classes";

    private ModuleUtils() {
    }

    /**
     * 获取新生成class文件的输出目录
     * <p>默认是原类class输出目录</p>
     */
    public static String getClassOutPutDir(String originalClassName) {
        String outputDir = DEFAULT_CLASS_OUTPUT_DIR;
        File sourceDir = getSourceDir4MultiMoodleProj();
        if (sourceDir != null) { // 如果是多模块的项目
            List<File> sameNameClasses = searchSourceFileByName(originalClassName, sourceDir);
            List<File> samePackageClasses = sameNameClasses.stream().filter(item -> item.getAbsolutePath().contains(originalClassName.replace(".", File.separator))).collect(Collectors.toList());
            if (samePackageClasses.size() == 1) {
                String beanPath = samePackageClasses.get(0).getAbsolutePath();
                outputDir = beanPath.substring(0, beanPath.indexOf("src" + File.separator + "main")) + DEFAULT_CLASS_OUTPUT_DIR;
            } else {
                throw new IllegalStateException("Too many " + originalClassName);
            }
        }

        return outputDir;
    }

    /**
     * 获取源码根目录
     * <p>如果不是多模块项目则直接返回null</p>
     */
    public static File getSourceDir4MultiMoodleProj() {
        // 启动maven编译任务的目录
        File userDirFile = new File(System.getProperty("user.dir"));

        // 获取maven项目子模块
        List<String> modules;
        try {
            FileInputStream fis = new FileInputStream(userDirFile + File.separator + "pom.xml");
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(fis);
            modules = model.getModules();
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException("Occurring an exception during pom parsing!", e);
        }

        // 如果没有子模块直接返回
        if (modules == null) {
            return null;
        }

        // 启动maven编译的模块的POM文件相对于源码根目录的深度
        int maxDeep = 0;
        for (String module : modules) {
            int count = countStr(module, "..");
            maxDeep = Math.max(count, maxDeep);
        }

        File sourceDir = userDirFile;
        for (int i = 0; i < maxDeep; i++) {
            sourceDir = userDirFile.getParentFile();
        }
        return sourceDir;
    }

    public static List<File> searchSourceFileByName(String simpleName, File sourceDir) {
        String[] splited = simpleName.split("\\.");
        return FileUtils.searchFiles(sourceDir, splited[splited.length - 1] + ".java");
    }

    private static int countStr(String str, String targetStr) {
        int count = 0;
        while (str.contains(targetStr)) {
            str = str.substring(str.indexOf(targetStr) + targetStr.length());
            count++;
        }
        return count;
    }
}
