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

package org.springframework.web.context.request;

import java.io.Serializable;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Adapter that implements the Servlet HttpSessionBindingListener interface,
 * wrapping a session destruction callback.
 *
 * <p>
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestAttributes#registerDestructionCallback
 * @see ServletRequestAttributes#registerSessionDestructionCallback
 */
@SuppressWarnings("serial")
public class DestructionCallbackBindingListener implements HttpSessionBindingListener, Serializable {

	private final Runnable destructionCallback;


	/**
	 * Create a new DestructionCallbackBindingListener for the given callback.
	 * <p>
	 *  适配器实现Servlet HttpSessionBindingListener接口,打包会话销毁回调
	 * 
	 * 
	 * @param destructionCallback the Runnable to execute when this listener
	 * object gets unbound from the session
	 */
	public DestructionCallbackBindingListener(Runnable destructionCallback) {
		this.destructionCallback = destructionCallback;
	}


	@Override
	public void valueBound(HttpSessionBindingEvent event) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		this.destructionCallback.run();
	}

}
