/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.messaging.converter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * An exception raised by {@link MessageConverter} implementations.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Mark Fisher
 * @since 4.0
 */
@SuppressWarnings("serial")
public class MessageConversionException extends MessagingException {

	public MessageConversionException(String description) {
		super(description);
	}

	public MessageConversionException(String description, Throwable cause) {
		super(description, cause);
	}

	public MessageConversionException(Message<?> failedMessage, String description) {
		super(failedMessage, description);
	}

	public MessageConversionException(Message<?> failedMessage, String description, Throwable cause) {
		super(failedMessage, description, cause);
	}

}
