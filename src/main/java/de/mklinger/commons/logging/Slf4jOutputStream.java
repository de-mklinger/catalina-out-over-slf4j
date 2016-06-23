/*
 * Copyright 2015-present mklinger GmbH - http://www.mklinger.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mklinger.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class Slf4jOutputStream extends LoggingOutputStream {
	private static class InternalLoggerImpl implements InternalLogger {
		private final Logger log;
		private final Level level;

		private InternalLoggerImpl(final String name, final String level) {
			this.log = LoggerFactory.getLogger(name);
			this.level = Level.fromString(level);
		}

		@Override
		public void log(final String s) {
			switch (level) {
				case DEBUG:
					log.debug(s);
					break;
				case ERROR:
					log.error(s);
					break;
				case WARN:
					log.warn(s);
					break;
				case TRACE:
					log.trace(s);
					break;
				default:
					log.info(s);
					break;
			}
		}
	}

	private static enum Level {
		DEBUG,
		ERROR,
		INFO,
		WARN,
		TRACE;

		public static Level fromString(final String s) {
			if (s == null || s.isEmpty()) {
				return INFO;
			}
			try {
				return valueOf(s.toUpperCase());
			} catch (final Exception e) {
				return INFO;
			}
		}
	}

	public Slf4jOutputStream(final String name, final String level) {
		super(new InternalLoggerImpl(name, level));
	}
}
