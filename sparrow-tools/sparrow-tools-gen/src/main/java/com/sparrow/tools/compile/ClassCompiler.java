/**  
 * Project Name:http-server  
 * File Name:CreateClass.java  
 * Package Name:au.tools.compile  
 * Date:2014-2-18下午5:35:37  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.tools.compile;

import static java.io.File.pathSeparatorChar;
import static java.io.File.separatorChar;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;


import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.tools.utils.FileUtil;
import com.sun.tools.javac.Main;

/**
 * ClassName:CreateClass <br/>
 * Date: 2014-2-18 下午5:35:37 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class ClassCompiler {
	private static final String CP;
	private final File srcFile;
	private final File destFile;
	private final PrintWriter cpErr;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append(SystemConfig.SYS_CLASS_PATH).append(pathSeparatorChar);
		sb.append(SystemConfig.SYS_ROOT_PATH);
		sb.append(pathSeparatorChar).append(SystemConfig.WEB_ROOT).append(
				separatorChar).append("WEB-INF").append(separatorChar).append(
				"classes").append(separatorChar);

		File[] libFiles = LibFileList.getLibFileList();
		for (int i = 0; i < libFiles.length; i++) {
			sb.append(pathSeparatorChar).append(libFiles[i].getAbsolutePath());
		}
		CP = sb.toString();
	}

	public ClassCompiler(File srcFile, File destFile) {
		this(srcFile, destFile, new PrintWriter(System.out));
	}

	public ClassCompiler(File srcFile, File destFile, PrintWriter cpErr) {
		this.srcFile = srcFile;
		this.destFile = destFile;
		this.cpErr = cpErr;
	}

	public void compile() {
		if (this.destFile.exists())
			FileUtil.clearSub(this.destFile);
		else
			this.destFile.mkdirs();
		this.javac(this.srcFile, this.destFile, this.cpErr);
		// this.cpErr.close();
	}

	void javac(File javaFile, File destFile, PrintWriter cpErr) {
		loopFile(javaFile, destFile, cpErr);
	}

	void doJavac1(File javaFile) {
		// 3.取得当前系统的编译器
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		// 4.获取一个文件管理器
		StandardJavaFileManager javaFileManager = javaCompiler
				.getStandardFileManager(null, null, null);
		// 5.文件管理器根与文件连接起来
		Iterable<? extends JavaFileObject> it = javaFileManager
				.getJavaFileObjects(javaFile);
		// 6.创建编译任务
		CompilationTask task = javaCompiler.getTask(null, javaFileManager,
				null, Arrays.asList("-g", "-sourcepath", this.srcFile
						.getAbsolutePath(), "-d", this.destFile
						.getAbsolutePath()), null, it);
		// -g 你可以关闭debug，但单独打开那一个选项。这是paranamer依赖的功能，没办法。
		// 不然编译器会把参数名全部移除掉，自动方法适配就不可能正常运作了。
		// 7.执行编译
		task.call();
		try {
			javaFileManager.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void doJavac(File javaFile) {
		String p = javaFile.getAbsolutePath();
		// "-verbose",
		String args[] = { "-g", "-encoding", SystemConfig.SYS_ENCODING,
				"-source", "1.7", "-target", "1.7", "-cp", CP, "-sourcepath",
				this.srcFile.getAbsolutePath(), "-d",
				this.destFile.getAbsolutePath(), p };
		// 有source 在编译的时候，依赖的其他 java也要编译过去
		int s = Main.compile(args, this.cpErr);
		if (s == 2)
			System.err.println("编译java文件: " + p + " -出错");
		// else
		// System.out.println("编译java文件:" + p + " -成功");
	}

	void loopFile(File srcFile, File destFile, PrintWriter cpErr) {
		loopFile(srcFile, destFile, cpErr, srcFile.getAbsolutePath().length());
	}

	void loopFile(File srcFile, File destFile, PrintWriter cpErr, int preLen) {
		if (srcFile.isDirectory()) {
			File[] files = srcFile.listFiles();
			for (File f : files) {
				loopFile(f, destFile, cpErr, preLen);
			}
		} else if (srcFile.getName().endsWith("java")) {
			String sPath = srcFile.getAbsolutePath();
			if (sPath.length() > preLen) {
				String relative = sPath.substring(preLen + 1);
				String classFile = PathResolver.trimExtension(relative)
						+ ".class";
				// 目标class已经生成
				if (new File(destFile, classFile).exists()) {
					return;
				}
			}
			doJavac(srcFile);
		}
	}

	FilenameFilter javaFilter = new FilenameFilter() {
		@Override
		public boolean accept(File file, String fileName) {
			return fileName.endsWith("java");
		}
	};
}
