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
package de.mklinger.commons.logging.catalina;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mklinger.commons.logging.Slf4jPrintStream;

/**
 * Catalina lifecycle listener for redirecting stdout and stderr to SLF4j.
 * To be configured in server.xml as follows:
 * <pre>
 * &lt;Server&gt;
 *     ...
 *     &lt;Listener className="de.mklinger.commons.logging.catalina.StdErrOutInitListener"/&gt;
 *     ...
 * &lt;/Server&gt;
 * </pre&gt;
 *
 * Additional settings can be given:
 * <pre>
 * &lt;Server&gt;
 *     ...
 *     &lt;Listener
 *         className="de.mklinger.commons.logging.catalina.StdErrOutInitListener"
 *         stdErrLevel="ERROR"
 *         stdOutLevel="INFO"
 *         stdErrName="myerrorout"
 *         stdOutName="myout"
 *     /&gt;
 *     ...
 * &lt;/Server&gt;
 * </pre>
 *
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class StdErrOutInitListener implements LifecycleListener {
	private static final String STDERR_LEVEL_SYSPROP = "de.mklinger.commons.logging.catalina.stderr.level";
	private static final String STDOUT_LEVEL_SYSPROP = "de.mklinger.commons.logging.catalina.stdout.level";

	private static final String DEFAULT_STDERR_LEVEL = "WARN";
	private static final String DEFAULT_STDOUT_LEVEL = "DEBUG";

	private static final String STDERR_NAME_SYSPROP = "de.mklinger.commons.logging.catalina.stderr.name";
	private static final String STDOUT_NAME_SYSPROP = "de.mklinger.commons.logging.catalina.stdout.name";

	private static final String DEFAULT_STDERR_NAME = "stderr";
	private static final String DEFAULT_STDOUT_NAME = "stdout";

	private static final Logger LOG = LoggerFactory.getLogger(StdErrOutInitListener.class);

	private final AtomicReference<PrintStream> oldStderr = new AtomicReference<>();
	private final AtomicReference<PrintStream> oldStdout = new AtomicReference<>();

	private String stdErrLevel;
	private String stdOutLevel;

	private String stdErrName;
	private String stdOutName;

	@Override
	public void lifecycleEvent(final LifecycleEvent event) {
		if (Lifecycle.PERIODIC_EVENT.equals(event.getType())) {
			return;
		}

		if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
			restoreStdErrOut();
			return;
		}

		if (isRedirected()) {
			return;
		}
		redirectStdErrOut();
	}

	private boolean isRedirected() {
		return oldStdout.get() != null || oldStderr.get() != null;
	}

	private void redirectStdErrOut() {
		if (!oldStdout.compareAndSet(null, System.out)) {
			LOG.error("stdout already replaced");
			return;
		}
		if (!oldStderr.compareAndSet(null, System.err)) {
			LOG.error("stderr already replaced");
			return;
		}
		Slf4jPrintStream newStdOut;
		Slf4jPrintStream newStdErr;
		try {
			newStdOut = new Slf4jPrintStream(getStdOutName(), getStdOutLevel());
			newStdErr = new Slf4jPrintStream(getStdErrName(), getStdErrLevel());
		} catch (final UnsupportedEncodingException e) {
			LOG.error("Error creating stderr/stdout replacements", e);
			return;
		}
		System.setOut(newStdOut);
		//System.out.println("Redirected stdout");
		System.setErr(newStdErr);
		//System.err.println("Redirected stderr");
	}

	private void restoreStdErrOut() {
		final PrintStream stdErrToRestore = oldStderr.getAndSet(null);
		final PrintStream stdOutToRestore = oldStdout.getAndSet(null);
		if (stdErrToRestore != null) {
			//System.err.println("Restoring old stderr");
			System.setErr(stdErrToRestore);
		}
		if (stdOutToRestore != null) {
			//System.out.println("Restoring old stdout");
			System.setOut(stdOutToRestore);
		}
	}

	public void setStdErrLevel(String stdErrLevel) {
		if (isRedirected()) {
			throw new IllegalStateException("Too late for setting stdErrLevel");
		}
		this.stdErrLevel = stdErrLevel;
	}

	private String getStdErrLevel() {
		if (stdErrLevel != null) {
			return stdErrLevel;
		}
		return System.getProperty(STDERR_LEVEL_SYSPROP, DEFAULT_STDERR_LEVEL);
	}

	public void setStdOutLevel(String stdOutLevel) {
		if (isRedirected()) {
			throw new IllegalStateException("Too late for setting stdOutLevel");
		}
		this.stdOutLevel = stdOutLevel;
	}

	private String getStdOutLevel() {
		if (stdOutLevel != null) {
			return stdOutLevel;
		}
		return System.getProperty(STDOUT_LEVEL_SYSPROP, DEFAULT_STDOUT_LEVEL);
	}

	public void setStdErrName(String stdErrName) {
		if (isRedirected()) {
			throw new IllegalStateException("Too late for setting stdErrName");
		}
		this.stdErrName = stdErrName;
	}

	private String getStdErrName() {
		if (stdErrName != null) {
			return stdErrName;
		}
		return System.getProperty(STDERR_NAME_SYSPROP, DEFAULT_STDERR_NAME);
	}

	public void setStdOutName(String stdOutName) {
		if (isRedirected()) {
			throw new IllegalStateException("Too late for setting stdOutName");
		}
		this.stdOutName = stdOutName;
	}

	private String getStdOutName() {
		if (stdOutName != null) {
			return stdOutName;
		}
		return System.getProperty(STDOUT_NAME_SYSPROP, DEFAULT_STDOUT_NAME);
	}
}
