/**
 * @(#)AgentMainAttach.java Created by zhongya.yan on 2020/3/16   10:40
 * <p>
 * Copyrights (C) 2020保留所有权利
 */

package org.agent.demo;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * (类型功能说明描述)
 *
 * <p>
 * 修改历史:                                 <br>
 * 修改日期           修改人员       版本       修改内容<br>
 * -------------------------------------------------<br>
 * 2020/3/16 10:40   zhongya.yan     1.0       初始化创建<br>
 * </p>
 *
 * @author zhongya.yan
 * @version 1.0
 * @since JDK1.7
 */
public class AgentMainAttach {

    public static void main(String[] args) {

        String java_home = System.getProperty("java.home");
        int index = java_home.lastIndexOf("jre");
        java_home = java_home.substring(0, index);

        StringBuilder java_bin = new StringBuilder(java_home);
        java_bin.append("bin/java");

        StringBuilder toolsPath = new StringBuilder("-Xbootclasspath/a:");
        toolsPath.append(java_home);
        toolsPath.append("lib/tools.jar");

        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> commond = new ArrayList<>();
        commond.add(java_bin.toString());
        commond.add(toolsPath.toString());
        commond.add("-jar");
        commond.add("./agent-jar-with-dependencies.jar");
        processBuilder.command(commond);
        try {
            Class.forName("com.sun.tools.attach.VirtualMachine");
            List<VirtualMachineDescriptor> list = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd : list) {
                System.out.println(vmd.displayName() + ": Attach");
                try {
                    VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                    virtualMachine.loadAgent("./agent-jar-with-dependencies.jar ", "cxs");
                    System.out.println("ok");
                    virtualMachine.detach();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            System.out.println("加载失败");
            try {
                Process process = processBuilder.start();
                Thread redirectStdout = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream = process.getInputStream();
                        try {
                            copy(inputStream, System.out);
                        } catch (IOException e) {
                            close(inputStream);
                        }

                    }
                });
                System.in.read();
//                int exitValue = process.exitValue();
//                if (exitValue != 0) {
//                    System.exit(1);
//                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    public static IOException close(InputStream input) {
        return close((Closeable) input);
    }

    public static IOException close(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            return ioe;
        }
        return null;
    }
}
