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

package org.springframework.core.type;

/**
 * Interface that defines abstract access to the annotations of a specific
 * class, in a form that does not require that class to be loaded yet.
 *
 * <p>
 *  定义对特定类的注释的抽象访问的接口,以不需要加载该类的形式
 * 
 * 
 * @author Juergen Hoeller
 * @author Mark Pollack
 * @author Chris Beams
 * @author Phillip Webb
 * @since 3.0
 * @see StandardMethodMetadata
 * @see AnnotationMetadata#getAnnotatedMethods
 * @see AnnotatedTypeMetadata
 */
public interface MethodMetadata extends AnnotatedTypeMetadata {

	/**
	 * Return the name of the method.
	 * <p>
	 *  返回方法的名称
	 * 
	 */
	String getMethodName();

	/**
	 * Return the fully-qualified name of the class that declares this method.
	 * <p>
	 * 返回声明此方法的类的全限定名称
	 * 
	 */
	String getDeclaringClassName();

	/**
	 * Return the fully-qualified name of this method's declared return type.
	 * <p>
	 *  返回此方法声明的返回类型的全限定名称
	 * 
	 * 
	 * @since 4.2
	 */
	String getReturnTypeName();

	/**
	 * Return whether the underlying method is effectively abstract:
	 * i.e. marked as abstract on a class or declared as a regular,
	 * non-default method in an interface.
	 * <p>
	 *  返回底层方法是否有效抽象：即在类中标记为抽象或在接口中声明为常规非默认方法
	 * 
	 * 
	 * @since 4.2
	 */
	boolean isAbstract();

	/**
	 * Return whether the underlying method is declared as 'static'.
	 * <p>
	 *  返回底层方法是否被声明为"静态"
	 * 
	 */
	boolean isStatic();

	/**
	 * Return whether the underlying method is marked as 'final'.
	 * <p>
	 *  返回底层方法是否被标记为'最终'
	 * 
	 */
	boolean isFinal();

	/**
	 * Return whether the underlying method is overridable,
	 * i.e. not marked as static, final or private.
	 * <p>
	 *  返回底层方法是否可覆盖,即不标记为静态,最终或私有
	 */
	boolean isOverridable();

}
