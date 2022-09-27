/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.bind.support;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * SPI for resolving custom arguments for a specific handler method parameter.
 * Typically implemented to detect special parameter types, resolving
 * well-known argument values for them.
 *
 * <p>A typical implementation could look like as follows:
 *
 * <pre class="code">
 * public class MySpecialArgumentResolver implements WebArgumentResolver {
 *
 *   public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) {
 *     if (methodParameter.getParameterType().equals(MySpecialArg.class)) {
 *       return new MySpecialArg("myValue");
 *     }
 *     return UNRESOLVED;
 *   }
 * }</pre>
 *
 * <p>
 *  用于解析特定处理程序方法参数的自定义参数的SPI通常用于检测特殊参数类型,为它们解析众所周知的参数值
 * 
 * <p>典型的实现可能如下所示：
 * 
 * <pre class="code">
 *  public class MySpecialArgumentResolver实现WebArgumentResolver {
 * 
 *  public Object resolveArgument(MethodParameter methodParameter,NativeWebRequest webRequest){if(methodParametergetParameterType()equals(MySpecialArgclass)){return new MySpecialArg("myValue"); }
 * 
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter#setCustomArgumentResolvers
 * @see org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter#setCustomArgumentResolvers
 */
public interface WebArgumentResolver {

	/**
	 * Marker to be returned when the resolver does not know how to
	 * handle the given method parameter.
	 * <p>
	 *  return UNRESOLVED; }} </pre>。
	 * 
	 */
	Object UNRESOLVED = new Object();


	/**
	 * Resolve an argument for the given handler method parameter within the given web request.
	 * <p>
	 *  当解析器不知道如何处理给定的方法参数时,返回的标记
	 * 
	 * 
	 * @param methodParameter the handler method parameter to resolve
	 * @param webRequest the current web request, allowing access to the native request as well
	 * @return the argument value, or {@code UNRESOLVED} if not resolvable
	 * @throws Exception in case of resolution failure
	 */
	Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception;

}