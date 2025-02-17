/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.resiliency.mpi;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.messaging.config.BaseMessagingConfigurator;
import com.liferay.portal.kernel.messaging.config.MessagingConfigurator;
import com.liferay.portal.kernel.messaging.config.MessagingConfiguratorRegistry;
import com.liferay.portal.kernel.nio.intraband.DatagramReceiveHandler;
import com.liferay.portal.kernel.nio.intraband.Intraband;
import com.liferay.portal.kernel.nio.intraband.SystemDataType;
import com.liferay.portal.kernel.nio.intraband.blocking.ExecutorIntraband;
import com.liferay.portal.kernel.nio.intraband.nonblocking.SelectorIntraband;
import com.liferay.portal.kernel.nio.intraband.rpc.BootstrapRPCDatagramReceiveHandler;
import com.liferay.portal.kernel.nio.intraband.test.MockIntraband;
import com.liferay.portal.kernel.nio.intraband.welder.socket.SocketWelder;
import com.liferay.portal.kernel.resiliency.spi.MockSPI;
import com.liferay.portal.kernel.resiliency.spi.MockSPIProvider;
import com.liferay.portal.kernel.resiliency.spi.SPI;
import com.liferay.portal.kernel.resiliency.spi.SPIConfiguration;
import com.liferay.portal.kernel.resiliency.spi.SPIRegistryUtil;
import com.liferay.portal.kernel.resiliency.spi.provider.SPIProvider;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.resiliency.spi.SPIRegistryImpl;

import java.io.IOException;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
@NewEnv(type = NewEnv.Type.CLASSLOADER)
public class MPIHelperUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, NewEnvTestRule.INSTANCE);

	@Before
	public void setUp() {
		Map<String, Object> properties = HashMapBuilder.<String, Object>put(
			PropsKeys.INTRABAND_IMPL, ExecutorIntraband.class.getName()
		).put(
			PropsKeys.INTRABAND_TIMEOUT_DEFAULT, "10000"
		).put(
			PropsKeys.INTRABAND_WELDER_IMPL, SocketWelder.class.getName()
		).build();

		PropsTestUtil.setProps(properties);

		SPIRegistryUtil spiRegistryUtil = new SPIRegistryUtil();

		spiRegistryUtil.setSPIRegistry(
			new SPIRegistryImpl() {

				@Override
				public void registerSPI(SPI spi) {
				}

				@Override
				public void unregisterSPI(SPI spi) {
				}

			});
	}

	@After
	public void tearDown() {
		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					MPIHelperUtil.class.getName(), Level.OFF)) {

			MPIHelperUtil.shutdown();
		}
		catch (Throwable t) {
		}
	}

	@Test
	public void testClassInitializationFailed() {
		PropsUtil.setProps(null);

		System.setProperty(PropsKeys.INTRABAND_IMPL, "NoSuchClass");

		try {
			MPIHelperUtil.getMPI();

			Assert.fail();
		}
		catch (ExceptionInInitializerError eiie) {
			Throwable throwable = eiie.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals(
				"Unable to instantiate NoSuchClass", throwable.getMessage());
		}
		finally {
			System.clearProperty(PropsKeys.INTRABAND_IMPL);
		}
	}

	@Test
	public void testClassInitializationOnMPI() throws Exception {
		PropsUtil.setProps(null);

		MPI mpiImpl = _getMPIImpl();

		Assert.assertNotNull(mpiImpl);
		Assert.assertTrue(mpiImpl.isAlive());

		MPI mpi = MPIHelperUtil.getMPI();

		Assert.assertSame(mpi, UnicastRemoteObject.toStub(mpiImpl));
		Assert.assertTrue(mpi.isAlive());

		Intraband intraband = MPIHelperUtil.getIntraband();

		Assert.assertSame(ExecutorIntraband.class, intraband.getClass());

		DatagramReceiveHandler[] datagramReceiveHandlers =
			intraband.getDatagramReceiveHandlers();

		Assert.assertSame(
			BootstrapRPCDatagramReceiveHandler.class,
			datagramReceiveHandlers[SystemDataType.RPC.getValue()].getClass());
	}

	@Test
	public void testClassInitializationOnSPI() throws Exception {
		PropsUtil.setProps(null);

		System.setProperty(
			PropsKeys.INTRABAND_IMPL, SelectorIntraband.class.getName());
		System.setProperty(PropsKeys.INTRABAND_TIMEOUT_DEFAULT, "10000");
		System.setProperty(
			PropsKeys.INTRABAND_WELDER_IMPL, SocketWelder.class.getName());

		MPI mpiImpl = _getMPIImpl();

		Assert.assertNotNull(mpiImpl);
		Assert.assertTrue(mpiImpl.isAlive());

		MPI mpi = MPIHelperUtil.getMPI();

		Assert.assertSame(mpi, UnicastRemoteObject.toStub(mpiImpl));
		Assert.assertTrue(mpi.isAlive());

		Intraband intraband = MPIHelperUtil.getIntraband();

		Assert.assertSame(SelectorIntraband.class, intraband.getClass());

		DatagramReceiveHandler[] datagramReceiveHandlers =
			intraband.getDatagramReceiveHandlers();

		Assert.assertSame(
			BootstrapRPCDatagramReceiveHandler.class,
			datagramReceiveHandlers[SystemDataType.RPC.getValue()].getClass());
	}

	@NewEnv(type = NewEnv.Type.NONE)
	@Test
	public void testConstructor() {
		new MPIHelperUtil();
	}

	@Test
	public void testShutdownFailWithLog() throws NoSuchObjectException {
		UnicastRemoteObject.unexportObject(_getMPIImpl(), true);

		final IOException ioException = new IOException();

		ReflectionTestUtil.setFieldValue(
			MPIHelperUtil.class, "_intraband",
			new MockIntraband() {

				@Override
				public void close() throws IOException {
					throw ioException;
				}

			});

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					MPIHelperUtil.class.getName(), Level.WARNING)) {

			MPIHelperUtil.shutdown();

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 2, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Unable to unexport " + _getMPIImpl(), logRecord.getMessage());

			Throwable throwable = logRecord.getThrown();

			Assert.assertSame(
				NoSuchObjectException.class, throwable.getClass());

			logRecord = logRecords.get(1);

			Assert.assertEquals(
				"Unable to close intraband", logRecord.getMessage());
			Assert.assertSame(ioException, logRecord.getThrown());
		}
	}

	@Test
	public void testShutdownFailWithoutLog() throws NoSuchObjectException {
		UnicastRemoteObject.unexportObject(_getMPIImpl(), true);

		final IOException ioException = new IOException();

		ReflectionTestUtil.setFieldValue(
			MPIHelperUtil.class, "_intraband",
			new MockIntraband() {

				@Override
				public void close() throws IOException {
					throw ioException;
				}

			});

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					MPIHelperUtil.class.getName(), Level.OFF)) {

			MPIHelperUtil.shutdown();

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
		}
	}

	@Test
	public void testShutdownSuccess() {
		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					MPIHelperUtil.class.getName(), Level.ALL)) {

			MPIHelperUtil.shutdown();

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
		}
	}

	@Test
	public void testSPIProviderRegistration() throws RemoteException {

		// Register SPI provider, null name

		MockSPIProvider mockSPIProvider1 = new MockSPIProvider(null);

		try {
			MPIHelperUtil.registerSPIProvider(mockSPIProvider1);

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
		}

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					MPIHelperUtil.class.getName(), Level.INFO)) {

			// Register SPI provider, with log

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			String name1 = "spiProvider1";

			mockSPIProvider1 = new MockSPIProvider(name1);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider1));

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				"Registered SPI provider " + mockSPIProvider1,
				logRecord1.getMessage());

			// Register SPI provider, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			String name2 = "spiProvider2";

			MockSPIProvider mockSPIProvider2 = new MockSPIProvider(name2);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider2));

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Register SPI provider, duplicate name, with log

			logRecords = captureHandler.resetLogLevel(Level.WARNING);

			MockSPIProvider mockSPIProvider3 = new MockSPIProvider(name1);

			Assert.assertFalse(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider3));

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Not registering SPI provider ", mockSPIProvider3,
					" because it duplicates ", mockSPIProvider1),
				logRecord1.getMessage());

			// Register SPI provider, duplicate name, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			mockSPIProvider3 = new MockSPIProvider(name1);

			Assert.assertFalse(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider3));

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Get SPI provider

			String name3 = "spiProvider3";

			Assert.assertSame(
				mockSPIProvider1, MPIHelperUtil.getSPIProvider(name1));
			Assert.assertSame(
				mockSPIProvider2, MPIHelperUtil.getSPIProvider(name2));
			Assert.assertNull(MPIHelperUtil.getSPIProvider(name3));

			List<SPIProvider> spiProviders = MPIHelperUtil.getSPIProviders();

			Assert.assertEquals(
				spiProviders.toString(), 2, spiProviders.size());
			Assert.assertTrue(
				spiProviders.toString(),
				spiProviders.contains(mockSPIProvider1));
			Assert.assertTrue(
				spiProviders.toString(),
				spiProviders.contains(mockSPIProvider2));

			// Unregister SPI provider, null name

			mockSPIProvider3 = new MockSPIProvider(null);

			try {
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider3);

				Assert.fail();
			}
			catch (NullPointerException nullPointerException) {
			}

			// Unregister SPI provider, nonexistent name, with log

			logRecords = captureHandler.resetLogLevel(Level.WARNING);

			mockSPIProvider3 = new MockSPIProvider(name3);

			Assert.assertFalse(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider3));

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				"Not unregistering unregistered SPI provider " +
					mockSPIProvider3,
				logRecord1.getMessage());

			// Unregister SPI provider, nonexistent name, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			mockSPIProvider3 = new MockSPIProvider(name3);

			Assert.assertFalse(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider3));

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister SPI provider, mismatch instance, with log

			logRecords = captureHandler.resetLogLevel(Level.INFO);

			Assert.assertFalse(
				MPIHelperUtil.unregisterSPIProvider(
					new MockSPIProvider(name2)));
			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				"Not unregistering unregistered SPI provider " + name2,
				logRecord1.getMessage());

			// Unregister SPI provider, mismatch instance, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			Assert.assertFalse(
				MPIHelperUtil.unregisterSPIProvider(
					new MockSPIProvider(name2)));
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister SPI provider, concurrent remove failure, with log

			logRecords = captureHandler.resetLogLevel(Level.INFO);

			ConcurrentMap<String, Object> oldSPIProviderContainers =
				ReflectionTestUtil.getFieldValue(
					MPIHelperUtil.class, "_spiProviderContainers");

			try {
				ReflectionTestUtil.setFieldValue(
					MPIHelperUtil.class, "_spiProviderContainers",
					new ConcurrentHashMap<String, Object>(
						oldSPIProviderContainers) {

						@Override
						public boolean remove(Object key, Object value) {
							return false;
						}

					});

				Assert.assertFalse(
					MPIHelperUtil.unregisterSPIProvider(mockSPIProvider2));
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					MPIHelperUtil.class, "_spiProviderContainers",
					oldSPIProviderContainers);
			}

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				"Not unregistering unregistered SPI provider " + name2,
				logRecord1.getMessage());

			// Unregister SPI provider, concurrent remove failure, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			oldSPIProviderContainers = ReflectionTestUtil.getFieldValue(
				MPIHelperUtil.class, "_spiProviderContainers");

			try {
				ReflectionTestUtil.setFieldValue(
					MPIHelperUtil.class, "_spiProviderContainers",
					new ConcurrentHashMap<String, Object>(
						oldSPIProviderContainers) {

						@Override
						public boolean remove(Object key, Object value) {
							return false;
						}

					});

				Assert.assertFalse(
					MPIHelperUtil.unregisterSPIProvider(mockSPIProvider2));
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					MPIHelperUtil.class, "_spiProviderContainers",
					oldSPIProviderContainers);
			}

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister SPI provider, with no SPI, with log

			logRecords = captureHandler.resetLogLevel(Level.INFO);

			Assert.assertTrue(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider2));
			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				"Unregistered SPI provider " + mockSPIProvider2,
				logRecord1.getMessage());

			// Unregister SPI provider, with no SPI, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			Assert.assertTrue(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider1));
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister SPI provider, with SPI, fail on stop, with log

			logRecords = captureHandler.resetLogLevel(Level.SEVERE);

			mockSPIProvider1 = new MockSPIProvider(name1);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider1));

			mockSPIProvider2 = new MockSPIProvider(name2);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider2));

			MockSPI mockSPI1 = new MockSPI();

			mockSPI1.failOnStop = true;
			mockSPI1.spiProviderName = name1;

			MPIHelperUtilTestUtil.directResigterSPI("spi1", mockSPI1);

			MockSPI mockSPI2 = new MockSPI();

			mockSPI2.failOnStop = true;
			mockSPI2.spiProviderName = name2;

			MPIHelperUtilTestUtil.directResigterSPI("spi2", mockSPI2);

			Assert.assertTrue(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider1));
			Assert.assertFalse(mockSPI1.destroyed);
			Assert.assertFalse(mockSPI1.stopped);
			Assert.assertFalse(mockSPI2.destroyed);
			Assert.assertFalse(mockSPI2.stopped);
			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to unregister SPI ", mockSPI1,
					" while unregistering SPI provider ", mockSPIProvider1),
				logRecord1.getMessage());

			Throwable throwable = logRecord1.getThrown();

			Assert.assertSame(RemoteException.class, throwable.getClass());

			// Unregister SPI provider, with SPI, fail on destroy, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			mockSPI1.failOnDestroy = true;
			mockSPI1.failOnStop = false;

			mockSPI2.failOnDestroy = true;
			mockSPI2.failOnStop = false;

			Assert.assertTrue(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider2));
			Assert.assertFalse(mockSPI1.destroyed);
			Assert.assertFalse(mockSPI1.stopped);
			Assert.assertFalse(mockSPI2.destroyed);
			Assert.assertTrue(mockSPI2.stopped);
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister SPI provider, with SPI, fail on catch, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			mockSPIProvider1 = new MockSPIProvider(name1);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider1));

			final RuntimeException runtimeException1 = new RuntimeException();

			mockSPI1 = new MockSPI() {

				@Override
				public String toString() {
					throw runtimeException1;
				}

			};

			mockSPI1.failOnDestroy = true;
			mockSPI1.failOnStop = false;
			mockSPI1.spiProviderName = name1;

			MPIHelperUtilTestUtil.directResigterSPI(name1, mockSPI1);

			try {
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider1);

				Assert.fail();
			}
			catch (RuntimeException runtimeException2) {
				Assert.assertSame(runtimeException1, runtimeException2);
			}

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister SPI provider, with SPI, success, with log

			mockSPIProvider1 = new MockSPIProvider(name1);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider1));

			mockSPIProvider2 = new MockSPIProvider(name2);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider2));

			mockSPI1 = new MockSPI();

			mockSPI1.failOnDestroy = false;
			mockSPI1.spiProviderName = name1;

			MPIHelperUtilTestUtil.directResigterSPI("spi1", mockSPI1);

			mockSPI2 = new MockSPI();

			mockSPI2.failOnDestroy = false;
			mockSPI2.spiProviderName = name2;

			MPIHelperUtilTestUtil.directResigterSPI("spi2", mockSPI2);

			logRecords = captureHandler.resetLogLevel(Level.INFO);

			Assert.assertTrue(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider1));
			Assert.assertTrue(mockSPI1.destroyed);
			Assert.assertTrue(mockSPI1.stopped);
			Assert.assertFalse(mockSPI2.destroyed);
			Assert.assertFalse(mockSPI2.stopped);
			Assert.assertEquals(logRecords.toString(), 2, logRecords.size());

			logRecord1 = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unregistered SPI ", mockSPI1,
					" while unregistering SPI provider ", mockSPIProvider1),
				logRecord1.getMessage());

			LogRecord logRecord2 = logRecords.get(1);

			Assert.assertEquals(
				"Unregistered SPI provider " + mockSPIProvider1,
				logRecord2.getMessage());

			// Unregister SPI provider, with SPI, success, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			Assert.assertTrue(
				MPIHelperUtil.unregisterSPIProvider(mockSPIProvider2));
			Assert.assertTrue(mockSPI1.destroyed);
			Assert.assertTrue(mockSPI1.stopped);
			Assert.assertTrue(mockSPI2.destroyed);
			Assert.assertTrue(mockSPI2.stopped);
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
		}
	}

	@Test
	public void testSPIRegistration() {
		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					MPIHelperUtil.class.getName(), Level.WARNING)) {

			// Mismatch MPI, with log

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			MockSPI mockSPI1 = new MockSPI();

			mockSPI1.mpi = new MockMPI();

			Assert.assertFalse(MPIHelperUtil.registerSPI(mockSPI1));

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Not registering SPI ", mockSPI1, " with foreign MPI ",
					mockSPI1.mpi, " versus ", MPIHelperUtil.getMPI()),
				logRecord.getMessage());

			// Mismatch MPI, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			Assert.assertFalse(MPIHelperUtil.registerSPI(mockSPI1));
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Null SPI provider name

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiProviderName = null;

			try {
				MPIHelperUtil.registerSPI(mockSPI1);

				Assert.fail();
			}
			catch (NullPointerException nullPointerException) {
			}

			// No such SPI provider, with log

			logRecords = captureHandler.resetLogLevel(Level.WARNING);

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiProviderName = "name1";

			Assert.assertFalse(MPIHelperUtil.registerSPI(mockSPI1));

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Not registering SPI ", mockSPI1,
					" with unknown SPI provider ", mockSPI1.spiProviderName),
				logRecord.getMessage());

			// No such SPI provider, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiProviderName = "name1";

			Assert.assertFalse(MPIHelperUtil.registerSPI(mockSPI1));

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Successful register, with log

			String name = "name1";

			MockSPIProvider mockSPIProvider = new MockSPIProvider(name);

			Assert.assertTrue(
				MPIHelperUtil.registerSPIProvider(mockSPIProvider));

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiConfiguration = new SPIConfiguration(
				"testId1", "", 8081, "", new String[0],
				new String[] {"servletContextName1"}, null);
			mockSPI1.spiProviderName = name;

			logRecords = captureHandler.resetLogLevel(Level.INFO);

			Assert.assertTrue(MPIHelperUtil.registerSPI(mockSPI1));
			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Registered SPI " + mockSPI1, logRecord.getMessage());

			// Successful register, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			MessagingConfigurator messagingConfigurator =
				new BaseMessagingConfigurator() {

					@Override
					public void connect() {
					}

					@Override
					public void disconnect() {
					}

					@Override
					protected ClassLoader getOperatingClassLoader() {
						return null;
					}

				};

			MessagingConfiguratorRegistry.registerMessagingConfigurator(
				"servletContextName2", messagingConfigurator);

			MockSPI mockSPI2 = new MockSPI();

			mockSPI2.mpi = MPIHelperUtil.getMPI();
			mockSPI2.spiConfiguration = new SPIConfiguration(
				"testId2", "", 8082, "", new String[0],
				new String[] {"servletContextName2"}, null);
			mockSPI2.spiProviderName = name;

			Assert.assertTrue(MPIHelperUtil.registerSPI(mockSPI2));

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Duplicate register, with log

			logRecords = captureHandler.resetLogLevel(Level.WARNING);

			Assert.assertFalse(MPIHelperUtil.registerSPI(mockSPI1));
			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Not registering SPI ", mockSPI1, " because it duplicates ",
					mockSPI1),
				logRecord.getMessage());

			// Duplicate register, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			Assert.assertFalse(MPIHelperUtil.registerSPI(mockSPI2));
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Bad SPI impl

			mockSPI1 = new MockSPI();

			mockSPI1.failOnGetConfiguration = true;
			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiProviderName = name;

			try {
				MPIHelperUtil.registerSPI(mockSPI1);

				Assert.fail();
			}
			catch (RuntimeException runtimeException) {
				Throwable throwable = runtimeException.getCause();

				Assert.assertSame(RemoteException.class, throwable.getClass());
			}

			// Get SPI, no such SPI provider

			Assert.assertNull(MPIHelperUtil.getSPI("name2", "testId1"));

			// Get SPI, exists

			Assert.assertNotNull(MPIHelperUtil.getSPI(name, "testId1"));

			// Get SPI, does not exist

			Assert.assertNull(MPIHelperUtil.getSPI(name, "testId3"));

			// Get SPIs

			logRecords = captureHandler.resetLogLevel(Level.SEVERE);

			mockSPI2.failOnIsAlive = true;

			List<SPI> spis = MPIHelperUtil.getSPIs();

			Assert.assertEquals(spis.toString(), 1, spis.size());

			mockSPI1 = (MockSPI)spis.get(0);

			Assert.assertEquals(name, mockSPI1.spiProviderName);

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Throwable throwable = logRecord.getThrown();

			Assert.assertSame(RemoteException.class, throwable.getClass());

			// Get SPIs by SPI provider, exists

			mockSPI2 = new MockSPI();

			mockSPI2.mpi = MPIHelperUtil.getMPI();
			mockSPI2.spiConfiguration = new SPIConfiguration(
				"testId2", "", 8082, "", new String[0], new String[0], null);
			mockSPI2.spiProviderName = name;

			Assert.assertTrue(MPIHelperUtil.registerSPI(mockSPI2));

			mockSPI2.failOnIsAlive = true;

			spis = MPIHelperUtil.getSPIs(name);

			Assert.assertEquals(spis.toString(), 1, spis.size());

			mockSPI1 = (MockSPI)spis.get(0);

			Assert.assertEquals(name, mockSPI1.spiProviderName);

			// Get SPIs by SPI provider, does not exist

			spis = MPIHelperUtil.getSPIs("name2");

			Assert.assertTrue(spis.toString(), spis.isEmpty());

			// Unregister thread local shortcut, with log

			mockSPI1 = new MockSPI();

			mockSPI1.spiConfiguration = new SPIConfiguration(
				null, null, 0, null, null, new String[0], null);

			ThreadLocal<SPI> unregisteringSPIThreadLocal =
				ReflectionTestUtil.getFieldValue(
					MPIHelperUtil.class, "_unregisteringSPIThreadLocal");

			unregisteringSPIThreadLocal.set(mockSPI1);

			try {
				Assert.assertTrue(MPIHelperUtil.unregisterSPI(mockSPI1));
			}
			finally {
				unregisteringSPIThreadLocal.remove();
			}

			// Unregister MPI mismatch, with log

			logRecords = captureHandler.resetLogLevel(Level.WARNING);

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = new MockMPI();

			Assert.assertFalse(MPIHelperUtil.unregisterSPI(mockSPI1));

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Not unregistering SPI ", mockSPI1, " with foreign MPI ",
					mockSPI1.mpi, " versus ", MPIHelperUtil.getMPI()),
				logRecord.getMessage());

			// Unregister MPI mismatch, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			Assert.assertFalse(MPIHelperUtil.unregisterSPI(mockSPI1));
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister no such SPI provider, with log

			logRecords = captureHandler.resetLogLevel(Level.WARNING);

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiProviderName = "name2";

			Assert.assertFalse(MPIHelperUtil.unregisterSPI(mockSPI1));

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Not unregistering SPI ", mockSPI1,
					" with unknown SPI provider ", mockSPI1.spiProviderName),
				logRecord.getMessage());

			// Unregister no such SPI provider, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiProviderName = "name2";

			Assert.assertFalse(MPIHelperUtil.unregisterSPI(mockSPI1));

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister no such SPI, with log

			logRecords = captureHandler.resetLogLevel(Level.WARNING);

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiConfiguration = new SPIConfiguration(
				"testId3", "", 8083, "", new String[0], new String[0], null);
			mockSPI1.spiProviderName = name;

			Assert.assertFalse(MPIHelperUtil.unregisterSPI(mockSPI1));

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Not unregistering unregistered SPI " + mockSPI1,
				logRecord.getMessage());

			// Unregister no such SPI, without log

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			mockSPI1 = new MockSPI();

			mockSPI1.mpi = MPIHelperUtil.getMPI();
			mockSPI1.spiConfiguration = new SPIConfiguration(
				"testId3", "", 8083, "", new String[0], new String[0], null);
			mockSPI1.spiProviderName = name;

			Assert.assertFalse(MPIHelperUtil.unregisterSPI(mockSPI1));

			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister success, with log

			mockSPI1 = (MockSPI)MPIHelperUtil.getSPI(name, "testId1");

			logRecords = captureHandler.resetLogLevel(Level.INFO);

			Assert.assertTrue(MPIHelperUtil.unregisterSPI(mockSPI1));
			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Unregistered SPI " + mockSPI1, logRecord.getMessage());

			// Unregister success, without log

			Assert.assertTrue(MPIHelperUtil.registerSPI(mockSPI1));

			logRecords = captureHandler.resetLogLevel(Level.OFF);

			Assert.assertTrue(MPIHelperUtil.unregisterSPI(mockSPI1));
			Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());

			// Unregister fail on getting configuration

			mockSPI1.failOnGetConfiguration = true;

			try {
				MPIHelperUtil.unregisterSPI(mockSPI1);

				Assert.fail();
			}
			catch (RuntimeException runtimeException) {
				throwable = runtimeException.getCause();

				Assert.assertSame(RemoteException.class, throwable.getClass());
			}
		}
	}

	private static MPI _getMPIImpl() {
		MPI mpiImpl = ReflectionTestUtil.getFieldValue(
			MPIHelperUtil.class, "_mpiImpl");

		Assert.assertNotNull(mpiImpl);

		return mpiImpl;
	}

	private static class MockMPI implements MPI {

		@Override
		public boolean isAlive() {
			return true;
		}

	}

}