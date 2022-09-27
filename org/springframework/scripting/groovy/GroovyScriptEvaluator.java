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

package org.springframework.scripting.groovy;

import java.io.IOException;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * Groovy-based implementation of Spring's {@link ScriptEvaluator} strategy interface.
 *
 * <p>
 *  基于Groovy的Spring的{@link ScriptEvaluator}策略接口的实现
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.0
 * @see GroovyShell#evaluate(String, String)
 */
public class GroovyScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware {

	private ClassLoader classLoader;

	private CompilerConfiguration compilerConfiguration = new CompilerConfiguration();


	/**
	 * Construct a new GroovyScriptEvaluator.
	 * <p>
	 *  构造一个新的GroovyScriptEvaluator
	 * 
	 */
	public GroovyScriptEvaluator() {
	}

	/**
	 * Construct a new GroovyScriptEvaluator.
	 * <p>
	 *  构造一个新的GroovyScriptEvaluator
	 * 
	 * 
	 * @param classLoader the ClassLoader to use as a parent for the {@link GroovyShell}
	 */
	public GroovyScriptEvaluator(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	/**
	 * Set a custom compiler configuration for this evaluator.
	 * <p>
	 *  为此评估器设置自定义编译器配置
	 * 
	 * 
	 * @since 4.3.3
	 * @see #setCompilationCustomizers
	 */
	public void setCompilerConfiguration(CompilerConfiguration compilerConfiguration) {
		this.compilerConfiguration =
				(compilerConfiguration != null ? compilerConfiguration : new CompilerConfiguration());
	}

	/**
	 * Return this evaluator's compiler configuration (never {@code null}).
	 * <p>
	 * 返回此评估器的编译器配置(从不{@code null})
	 * 
	 * 
	 * @since 4.3.3
	 * @see #setCompilerConfiguration
	 */
	public CompilerConfiguration getCompilerConfiguration() {
		return this.compilerConfiguration;
	}

	/**
	 * Set one or more customizers to be applied to this evaluator's compiler configuration.
	 * <p>Note that this modifies the shared compiler configuration held by this evaluator.
	 * <p>
	 *  设置一个或多个定制程序以应用于此评估程序的编译器配置<p>请注意,这将修改此评估者持有的共享编译器配置
	 * 
	 * @since 4.3.3
	 * @see #setCompilerConfiguration
	 */
	public void setCompilationCustomizers(CompilationCustomizer... compilationCustomizers) {
		this.compilerConfiguration.addCompilationCustomizers(compilationCustomizers);
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	@Override
	public Object evaluate(ScriptSource script) {
		return evaluate(script, null);
	}

	@Override
	public Object evaluate(ScriptSource script, Map<String, Object> arguments) {
		GroovyShell groovyShell = new GroovyShell(
				this.classLoader, new Binding(arguments), this.compilerConfiguration);
		try {
			String filename = (script instanceof ResourceScriptSource ?
					((ResourceScriptSource) script).getResource().getFilename() : null);
			if (filename != null) {
				return groovyShell.evaluate(script.getScriptAsString(), filename);
			}
			else {
				return groovyShell.evaluate(script.getScriptAsString());
			}
		}
		catch (IOException ex) {
			throw new ScriptCompilationException(script, "Cannot access Groovy script", ex);
		}
		catch (GroovyRuntimeException ex) {
			throw new ScriptCompilationException(script, ex);
		}
	}

}