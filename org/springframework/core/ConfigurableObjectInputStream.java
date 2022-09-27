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

package org.springframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.springframework.util.ClassUtils;

/**
 * Special ObjectInputStream subclass that resolves class names
 * against a specific ClassLoader. Serves as base class for
 * {@link org.springframework.remoting.rmi.CodebaseAwareObjectInputStream}.
 *
 * <p>
 *  特殊ObjectInputStream子类,用于根据特定的ClassLoader解析类名称作为{@link orgspringframeworkremotingrmiCodebaseAwareObjectInputStream}
 * 的基类。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.5
 */
public class ConfigurableObjectInputStream extends ObjectInputStream {

	private final ClassLoader classLoader;

	private final boolean acceptProxyClasses;


	/**
	 * Create a new ConfigurableObjectInputStream for the given InputStream and ClassLoader.
	 * <p>
	 * 为给定的InputStream和ClassLoader创建一个新的ConfigurableObjectInputStream
	 * 
	 * 
	 * @param in the InputStream to read from
	 * @param classLoader the ClassLoader to use for loading local classes
	 * @see java.io.ObjectInputStream#ObjectInputStream(java.io.InputStream)
	 */
	public ConfigurableObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
		this(in, classLoader, true);
	}

	/**
	 * Create a new ConfigurableObjectInputStream for the given InputStream and ClassLoader.
	 * <p>
	 *  为给定的InputStream和ClassLoader创建一个新的ConfigurableObjectInputStream
	 * 
	 * 
	 * @param in the InputStream to read from
	 * @param classLoader the ClassLoader to use for loading local classes
	 * @param acceptProxyClasses whether to accept deserialization of proxy classes
	 * (may be deactivated as a security measure)
	 * @see java.io.ObjectInputStream#ObjectInputStream(java.io.InputStream)
	 */
	public ConfigurableObjectInputStream(
			InputStream in, ClassLoader classLoader, boolean acceptProxyClasses) throws IOException {

		super(in);
		this.classLoader = classLoader;
		this.acceptProxyClasses = acceptProxyClasses;
	}


	@Override
	protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
		try {
			if (this.classLoader != null) {
				// Use the specified ClassLoader to resolve local classes.
				return ClassUtils.forName(classDesc.getName(), this.classLoader);
			}
			else {
				// Use the default ClassLoader...
				return super.resolveClass(classDesc);
			}
		}
		catch (ClassNotFoundException ex) {
			return resolveFallbackIfPossible(classDesc.getName(), ex);
		}
	}

	@Override
	protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
		if (!this.acceptProxyClasses) {
			throw new NotSerializableException("Not allowed to accept serialized proxy classes");
		}
		if (this.classLoader != null) {
			// Use the specified ClassLoader to resolve local proxy classes.
			Class<?>[] resolvedInterfaces = new Class<?>[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) {
				try {
					resolvedInterfaces[i] = ClassUtils.forName(interfaces[i], this.classLoader);
				}
				catch (ClassNotFoundException ex) {
					resolvedInterfaces[i] = resolveFallbackIfPossible(interfaces[i], ex);
				}
			}
			try {
				return ClassUtils.createCompositeInterface(resolvedInterfaces, this.classLoader);
			}
			catch (IllegalArgumentException ex) {
				throw new ClassNotFoundException(null, ex);
			}
		}
		else {
			// Use ObjectInputStream's default ClassLoader...
			try {
				return super.resolveProxyClass(interfaces);
			}
			catch (ClassNotFoundException ex) {
				Class<?>[] resolvedInterfaces = new Class<?>[interfaces.length];
				for (int i = 0; i < interfaces.length; i++) {
					resolvedInterfaces[i] = resolveFallbackIfPossible(interfaces[i], ex);
				}
				return ClassUtils.createCompositeInterface(resolvedInterfaces, getFallbackClassLoader());
			}
		}
	}


	/**
	 * Resolve the given class name against a fallback class loader.
	 * <p>The default implementation simply rethrows the original exception,
	 * since there is no fallback available.
	 * <p>
	 *  根据后备类加载器解析给定的类名<p>默认实现只是重新抛出原始异常,因为没有可用的备用
	 * 
	 * 
	 * @param className the class name to resolve
	 * @param ex the original exception thrown when attempting to load the class
	 * @return the newly resolved class (never {@code null})
	 */
	protected Class<?> resolveFallbackIfPossible(String className, ClassNotFoundException ex)
			throws IOException, ClassNotFoundException{

		throw ex;
	}

	/**
	 * Return the fallback ClassLoader to use when no ClassLoader was specified
	 * and ObjectInputStream's own default class loader failed.
	 * <p>The default implementation simply returns {@code null}, indicating
	 * that no specific fallback is available.
	 * <p>
	 *  当没有指定ClassLoader并返回ObjectLoader时,返回ClassLoader。
	 * ObjectInputStream自己的默认类加载器失败<p>默认实现只返回{@code null},表示没有特定的后备可用。
	 */
	protected ClassLoader getFallbackClassLoader() throws IOException {
		return null;
	}

}
