/**
 * @(#)AgentMain.java Created by zhongya.yan on 2020/3/16   10:21
 * <p>
 * Copyrights (C) 2020保留所有权利
 */

package org.agent.demo;

import org.agent.demo.transformer.MyTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * (类型功能说明描述)
 *
 * <p>
 * 修改历史:                                 <br>
 * 修改日期           修改人员       版本       修改内容<br>
 * -------------------------------------------------<br>
 * 2020/3/16 10:21   zhongya.yan     1.0       初始化创建<br>
 * </p>
 *
 * @author zhongya.yan
 * @version 1.0
 * @since JDK1.7
 */
public class AgentMain {

    public static void agentmain(String arg, Instrumentation instrumentation) {
        instrumentation.addTransformer(new MyTransformer(), true);
        Class[] classes = instrumentation.getAllLoadedClasses();
        for (Class aClass : classes) {
            if (aClass.getName().equals(MyTransformer.NAME)||aClass.getName().equals(MyTransformer.HELLO)){
                try {
                    instrumentation.retransformClasses(aClass);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
