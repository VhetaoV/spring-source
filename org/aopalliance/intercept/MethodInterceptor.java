/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aopalliance.intercept;

/**
 * Intercepts calls on an interface on its way to the target. These
 * are nested "on top" of the target.
 *
 * <p>The user should implement the {@link #invoke(MethodInvocation)}
 * method to modify the original behavior. E.g. the following class
 * implements a tracing interceptor (traces all the calls on the
 * intercepted method(s)):
 *
 * <pre class=code>
 * class TracingInterceptor implements MethodInterceptor {
 *   Object invoke(MethodInvocation i) throws Throwable {
 *     System.out.println("method "+i.getMethod()+" is called on "+
 *                        i.getThis()+" with args "+i.getArguments());
 *     Object ret=i.proceed();
 *     System.out.println("method "+i.getMethod()+" returns "+ret);
 *     return ret;
 *   }
 * }
 * </pre>
 *
 * <p>
 *  拦截器在到达目标的路上调用接口它们被嵌套在目标的"顶部"上
 * 
 * <p>用户应该实现{@link #invoke(MethodInvocation)}方法来修改原始行为,例如下面的类实现了一个跟踪拦截器(跟踪拦截的方法上的所有调用)：
 * 
 * <pre class=code>
 *  class TracingInterceptor实现MethodInterceptor {Object invoke(MethodInvocation i)throws Throwable {Systemoutprintln("method"+ igetMethod()+"在"+ igetThis()+"上用args"+ igetArguments())调用; Object ret = iproceed(); Systemoutprintln("method"+ igetMethod()+"returns"+ ret);退回}
 * 
 * @author Rod Johnson
 */
public interface MethodInterceptor extends Interceptor {
	
	/**
	 * Implement this method to perform extra treatments before and
	 * after the invocation. Polite implementations would certainly
	 * like to invoke {@link Joinpoint#proceed()}.
	 * <p>
	 * }。
	 * </pre>
	 * 
	 * 
	 * @param invocation the method invocation joinpoint
	 * @return the result of the call to {@link Joinpoint#proceed()};
	 * might be intercepted by the interceptor
	 * @throws Throwable if the interceptors or the target object
	 * throws an exception
	 */
	Object invoke(MethodInvocation invocation) throws Throwable;

}
