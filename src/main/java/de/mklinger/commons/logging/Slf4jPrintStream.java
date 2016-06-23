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

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class Slf4jPrintStream extends PrintStream {
	public Slf4jPrintStream(final String name) throws UnsupportedEncodingException {
		super(new Slf4jOutputStream(name, null), true, "UTF-8");
	}

	public Slf4jPrintStream(final String name, final String level) throws UnsupportedEncodingException {
		super(new Slf4jOutputStream(name, level), true, "UTF-8");
	}
}
