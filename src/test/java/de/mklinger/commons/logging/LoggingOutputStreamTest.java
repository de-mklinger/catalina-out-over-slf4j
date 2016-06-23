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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class LoggingOutputStreamTest {
	private static class TestLogger implements InternalLogger {
		final List<String> lines = new ArrayList<>();

		@Override
		public void log(final String s) {
			lines.add(s);
		}

		public List<String> getLines() {
			return lines;
		}
	}

	@Test
	public void testWriteOnClose() throws UnsupportedEncodingException, IOException {
		final TestLogger testLogger = new TestLogger();
		final LoggingOutputStream out = new LoggingOutputStream(testLogger);
		out.write("Hello World".getBytes("UTF-8"));
		out.close();
		Assert.assertEquals(1, testLogger.getLines().size());
		Assert.assertEquals("Hello World", testLogger.getLines().get(0));
	}

	@Test
	public void testWriteOnClose2() throws UnsupportedEncodingException, IOException {
		final TestLogger testLogger = new TestLogger();
		final LoggingOutputStream out = new LoggingOutputStream(testLogger);
		out.write("Hello World\nHello Logger".getBytes("UTF-8"));
		out.close();
		Assert.assertEquals(2, testLogger.getLines().size());
		Assert.assertEquals("Hello World", testLogger.getLines().get(0));
		Assert.assertEquals("Hello Logger", testLogger.getLines().get(1));
	}

	@Test
	public void testWriteOnFlushAndClose() throws UnsupportedEncodingException, IOException {
		final TestLogger testLogger = new TestLogger();
		final LoggingOutputStream out = new LoggingOutputStream(testLogger);
		out.write("Hello World\nHello Logger".getBytes("UTF-8"));
		out.flush();
		Assert.assertEquals(1, testLogger.getLines().size());
		Assert.assertEquals("Hello World", testLogger.getLines().get(0));
		out.close();
		Assert.assertEquals(2, testLogger.getLines().size());
		Assert.assertEquals("Hello World", testLogger.getLines().get(0));
		Assert.assertEquals("Hello Logger", testLogger.getLines().get(1));
	}

	@Test
	public void testWriteOnFlush() throws UnsupportedEncodingException, IOException {
		final TestLogger testLogger = new TestLogger();
		final LoggingOutputStream out = new LoggingOutputStream(testLogger);
		out.write("Hello World\nHello Logger\nLine 3\n".getBytes("UTF-8"));
		out.flush();
		Assert.assertEquals(3, testLogger.getLines().size());
		Assert.assertEquals("Hello World", testLogger.getLines().get(0));
		Assert.assertEquals("Hello Logger", testLogger.getLines().get(1));
		Assert.assertEquals("Line 3", testLogger.getLines().get(2));
		out.close();
		Assert.assertEquals(3, testLogger.getLines().size());
		Assert.assertEquals("Hello World", testLogger.getLines().get(0));
		Assert.assertEquals("Hello Logger", testLogger.getLines().get(1));
		Assert.assertEquals("Line 3", testLogger.getLines().get(2));
	}

	@Test
	public void testNoWriteOnFlushButOnClose() throws UnsupportedEncodingException, IOException {
		final TestLogger testLogger = new TestLogger();
		final LoggingOutputStream out = new LoggingOutputStream(testLogger);
		out.write("Hello World Hello Logger".getBytes("UTF-8"));
		out.flush();
		Assert.assertEquals(0, testLogger.getLines().size());
		out.close();
		Assert.assertEquals(1, testLogger.getLines().size());
		Assert.assertEquals("Hello World Hello Logger", testLogger.getLines().get(0));
	}
}
