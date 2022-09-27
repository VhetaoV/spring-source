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

package org.springframework.aop.target;

/**
 * Statistics for a ThreadLocal TargetSource.
 *
 * <p>
 *  ThreadLocal TargetSource的统计信息
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface ThreadLocalTargetSourceStats {

	/**
	 * Return the number of client invocations.
	 * <p>
	 *  返回客户端调用次数
	 * 
	 */
	int getInvocationCount();

	/**
	 * Return the number of hits that were satisfied by a thread-bound object.
	 * <p>
	 *  返回线程绑定对象所满足的命中数
	 * 
	 */
	int getHitCount();

	/**
	 * Return the number of thread-bound objects created.
	 * <p>
	 *  返回创建的线程绑定对象的数量
	 */
	int getObjectCount();

}
