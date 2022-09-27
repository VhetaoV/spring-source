/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.jmx.support;

/**
 * Represents how the measurement values of a {@code ManagedMetric} will change over time.
 * <p>
 *  表示{@code ManagedMetric}的测量值将如何随时间而改变
 * 
 * 
 * @author Jennifer Hickey
 * @since 3.0
 */
public enum MetricType {

	/**
	 * The measurement values may go up or down over time
	 * <p>
	 *  测量值可能随时间上升或下降
	 * 
	 */
	GAUGE,

	/**
	 * The measurement values will always increase
	 * <p>
	 *  测量值将始终增加
	 */
	COUNTER

}