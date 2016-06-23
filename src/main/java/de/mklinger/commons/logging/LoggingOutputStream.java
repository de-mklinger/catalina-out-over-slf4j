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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class LoggingOutputStream extends ByteArrayOutputStream {
	private final InternalLogger log;
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public LoggingOutputStream(final InternalLogger log) {
		this.log = log;
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		writeLines(false);
	}

	@Override
	public void close() throws IOException {
		super.close();
		writeLines(true);
	}

	private synchronized void writeLines(final boolean writeTrailingData) {
		int idx = findNl(buf, 0, count);
		if (idx == -1) {
			if (writeTrailingData && count > 0) {
				writeLine(buf, 0, count);
				reset();
			}
			return;
		}
		int lastIdx = -1;
		while (true) {
			writeLine(buf, lastIdx + 1, idx - lastIdx - 1);
			lastIdx = idx;
			idx = findNl(buf, idx + 1, count);
			if (idx == -1) {
				break;
			}
		}
		final int trailingDataLength = count - lastIdx - 1;
		if (trailingDataLength > 0) {
			if (writeTrailingData) {
				writeLine(buf, lastIdx + 1, trailingDataLength);
				return;
			}
			final byte[] trailingData = new byte[trailingDataLength];
			System.arraycopy(buf, lastIdx + 1, trailingData, 0, trailingDataLength);
			reset();
			write(trailingData, 0, trailingDataLength); // use this method to avoid IOException
		} else {
			reset();
		}
	}

	private void writeLine(final byte[] buf, final int offset, final int length) {
		final String s = new String(buf, offset, length, UTF_8);
		log.log(s);
	}

	private int findNl(final byte[] bytes, final int offset, final int count) {
		for (int idx = offset; idx < count; idx++) {
			if (bytes[idx] == '\n') {
				return idx;
			}
		}
		return -1;
	}
}
