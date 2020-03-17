/**
 * @(#)MyInstrumentation.java Created by zhongya.yan on 2020/3/16   11:38
 * <p>
 * Copyrights (C) 2020保留所有权利
 */

package org.agent.demo.transformer;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * (类型功能说明描述)
 *
 * <p>
 * 修改历史:                                 <br>
 * 修改日期           修改人员       版本       修改内容<br>
 * -------------------------------------------------<br>
 * 2020/3/16 11:38   zhongya.yan     1.0       初始化创建<br>
 * </p>
 *
 * @author zhongya.yan
 * @version 1.0
 * @since JDK1.7
 */
public class MyTransformer implements ClassFileTransformer {
    public final static String NAME = "org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry";
    public final static String HELLO = "com.example.demo.DemoController";

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String name = className.replaceAll("/", ".");
        if (name.equalsIgnoreCase(NAME) || name.equals(HELLO)) {
            ClassPool pool = ClassPool.getDefault();
            try {
                pool.appendClassPath(new LoaderClassPath(loader));
                CtClass ctClass = pool.get(name);
                if (ctClass.isFrozen()){
                    ctClass.defrost();
                }
                for (CtMethod method : ctClass.getMethods()) {
                    method.getModifiers();
                    if (method.getName().equals("getMappingsByUrl")) {
                        method.insertBefore("System.out.println($0.mappingLookup);");
                    }
                    if (method.getName().equals("hello")) {
                        method.insertBefore("System.out.println(\"hello invoke\");");
                    }
                }
                ctClass.writeFile("D:\\MyConfiguration\\zhongya.yan\\Desktop\\a.class");
                return ctClass.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//
        return null;
    }
}
