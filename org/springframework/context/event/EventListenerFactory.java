/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.context.event;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationListener;

/**
 * Strategy interface for creating {@link ApplicationListener} for methods
 * annotated with {@link EventListener}.
 *
 * <p>
 *  用于为{@link EventListener}注释的方法创建{@link ApplicationListener}的策略界面
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.2
 */
public interface EventListenerFactory {

	/**
	 * Specify if this factory supports the specified {@link Method}.
	 * <p>
	 *  指定此工厂是否支持指定的{@link方法}
	 * 
	 * 
	 * @param method an {@link EventListener} annotated method
	 * @return {@code true} if this factory supports the specified method
	 */
	boolean supportsMethod(Method method);

	/**
	 * Create an {@link ApplicationListener} for the specified method.
	 * <p>
	 * 为指定的方法创建一个{@link ApplicationListener}
	 * 
	 * @param beanName the name of the bean
	 * @param type the target type of the instance
	 * @param method the {@link EventListener} annotated method
	 * @return an application listener, suitable to invoke the specified method
	 */
	ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method);

}
