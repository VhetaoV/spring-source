/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;

import org.springframework.core.Ordered;

/**
 * {@link BeanInfoFactory} implementation that evaluates whether bean classes have
 * "non-standard" JavaBeans setter methods and are thus candidates for introspection
 * by Spring's (package-visible) {@code ExtendedBeanInfo} implementation.
 *
 * <p>Ordered at {@link Ordered#LOWEST_PRECEDENCE} to allow other user-defined
 * {@link BeanInfoFactory} types to take precedence.
 *
 * <p>
 * {@link BeanInfoFactory}实现,评估bean类是否具有"非标准"JavaBeans setter方法,因此是Spring(包可见){@code ExtendedBeanInfo}实现
 * 的内省的候选者。
 * 
 *  <p>订购于{@link Ordered#LOWEST_PRECEDENCE}以允许其他用户定义的{@link BeanInfoFactory}类型优先
 * 
 * 
 * @author Chris Beams
 * @since 3.2
 * @see BeanInfoFactory
 * @see CachedIntrospectionResults
 */
public class ExtendedBeanInfoFactory implements BeanInfoFactory, Ordered {

	/**
	 * Return an {@link ExtendedBeanInfo} for the given bean class, if applicable.
	 * <p>
	 *  返回给定bean类的{@link ExtendedBeanInfo}(如果适用)
	 * 
	 */
	@Override
	public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
		return (supports(beanClass) ? new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass)) : null);
	}

	/**
	 * Return whether the given bean class declares or inherits any non-void
	 * returning bean property or indexed property setter methods.
	 * <p>
	 *  返回给定的bean类是否声明或继承任何非void返回的bean属性或索引属性setter方法
	 */
	private boolean supports(Class<?> beanClass) {
		for (Method method : beanClass.getMethods()) {
			if (ExtendedBeanInfo.isCandidateWriteMethod(method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
