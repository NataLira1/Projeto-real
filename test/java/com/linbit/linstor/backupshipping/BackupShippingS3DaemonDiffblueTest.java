package com.linbit.linstor.backupshipping;

import com.linbit.linstor.api.BackupToS3;
import com.linbit.linstor.api.DecryptionHelper;
import com.linbit.linstor.core.StltConfigAccessor;
import com.linbit.linstor.core.identifier.RemoteName;
import com.linbit.linstor.core.objects.remotes.S3Remote;
import com.linbit.linstor.dbdrivers.SatelliteS3RemoteDriver;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.modularcrypto.JclCryptoProvider;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

public class BackupShippingS3DaemonDiffblueTest {
    /**
     * Test {@link BackupShippingS3Daemon#BackupShippingS3Daemon(ErrorReporter, ThreadGroup, String, String[], String, S3Remote, BackupToS3, boolean, long, BiConsumer, AccessContext, byte[])}.
     * <ul>
     *   <li>When {@code false}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#BackupShippingS3Daemon(ErrorReporter, ThreadGroup, String, String[], String, S3Remote, BackupToS3, boolean, long, BiConsumer, AccessContext, byte[])}
     */
    @Test
    public void testNewBackupShippingS3Daemon_whenFalse() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingS3Daemon.accCtx
        //     BackupShippingS3Daemon.afterTermination
        //     BackupShippingS3Daemon.afterTerminationSent
        //     BackupShippingS3Daemon.backupHandler
        //     BackupShippingS3Daemon.backupName
        //     BackupShippingS3Daemon.cmdProcess
        //     BackupShippingS3Daemon.cmdThread
        //     BackupShippingS3Daemon.command
        //     BackupShippingS3Daemon.deque
        //     BackupShippingS3Daemon.doneFirst
        //     BackupShippingS3Daemon.errorReporter
        //     BackupShippingS3Daemon.handler
        //     BackupShippingS3Daemon.masterKey
        //     BackupShippingS3Daemon.remote
        //     BackupShippingS3Daemon.restore
        //     BackupShippingS3Daemon.running
        //     BackupShippingS3Daemon.s3Thread
        //     BackupShippingS3Daemon.syncObj
        //     BackupShippingS3Daemon.threadGroup
        //     BackupShippingS3Daemon.uploadId
        //     BackupShippingS3Daemon.volSize
        //     BackupToS3.cache
        //     BackupToS3.decHelper
        //     BackupToS3.errorReporter
        //     BackupToS3.stltConfigAccessor

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, false, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"));

    }

    /**
     * Test {@link BackupShippingS3Daemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"))).start();
    }

    /**
     * Test {@link BackupShippingS3Daemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart2() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(null, threadGroupRef, "Thread Name", new String[]{"Command Ref"}, "Backup Name Ref",
                remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"))).start();
    }

    /**
     * Test {@link BackupShippingS3Daemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart3() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.security.ObjectProtection.requireAccess(com.linbit.linstor.security.AccessContext, com.linbit.linstor.security.AccessType)" because "this.objProt" is null
        //       at com.linbit.linstor.core.objects.remotes.S3Remote.getAccessKey(S3Remote.java:192)
        //       at com.linbit.linstor.api.BackupToS3.getCredentials(BackupToS3.java:476)
        //       at com.linbit.linstor.api.BackupToS3.getS3Client(BackupToS3.java:497)
        //       at com.linbit.linstor.api.BackupToS3.initMultipart(BackupToS3.java:96)
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.start(BackupShippingS3Daemon.java:114)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, false, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"))).start();
    }

    /**
     * Test {@link BackupShippingS3Daemon#start()}.
     * <ul>
     *   <li>Given {@link StderrErrorReporter#StderrErrorReporter(String)} with moduleName is empty string.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart_givenStderrErrorReporterWithModuleNameIsEmptyString() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"))).start();
    }

    /**
     * Test {@link BackupShippingS3Daemon#BackupShippingS3Daemon(ErrorReporter, ThreadGroup, String, String[], String, S3Remote, BackupToS3, boolean, long, BiConsumer, AccessContext, byte[])}.
     * <ul>
     *   <li>When {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#BackupShippingS3Daemon(ErrorReporter, ThreadGroup, String, String[], String, S3Remote, BackupToS3, boolean, long, BiConsumer, AccessContext, byte[])}
     */
    @Test
    public void testNewBackupShippingS3Daemon_whenTrue() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingS3Daemon.accCtx
        //     BackupShippingS3Daemon.afterTermination
        //     BackupShippingS3Daemon.afterTerminationSent
        //     BackupShippingS3Daemon.backupHandler
        //     BackupShippingS3Daemon.backupName
        //     BackupShippingS3Daemon.cmdProcess
        //     BackupShippingS3Daemon.cmdThread
        //     BackupShippingS3Daemon.command
        //     BackupShippingS3Daemon.deque
        //     BackupShippingS3Daemon.doneFirst
        //     BackupShippingS3Daemon.errorReporter
        //     BackupShippingS3Daemon.handler
        //     BackupShippingS3Daemon.masterKey
        //     BackupShippingS3Daemon.remote
        //     BackupShippingS3Daemon.restore
        //     BackupShippingS3Daemon.running
        //     BackupShippingS3Daemon.s3Thread
        //     BackupShippingS3Daemon.syncObj
        //     BackupShippingS3Daemon.threadGroup
        //     BackupShippingS3Daemon.uploadId
        //     BackupShippingS3Daemon.volSize
        //     BackupToS3.cache
        //     BackupToS3.decHelper
        //     BackupToS3.errorReporter
        //     BackupToS3.stltConfigAccessor

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"));

    }

    /**
     * Test {@link BackupShippingS3Daemon#run()}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#run()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRun() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.logging.ErrorReporter.logTrace(String, Object[])" because "this.errorReporter" is null
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.run(BackupShippingS3Daemon.java:236)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(null, threadGroupRef, "Thread Name", new String[]{"Command Ref"}, "Backup Name Ref",
                remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"))).run();
    }

    /**
     * Test {@link BackupShippingS3Daemon#run()}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#run()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRun2() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "java.util.function.BiConsumer.accept(Object, Object)" because "this.afterTermination" is null
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.threadFinished(BackupShippingS3Daemon.java:315)
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.run(BackupShippingS3Daemon.java:298)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"))).run();
    }

    /**
     * Test {@link BackupShippingS3Daemon#shutdown(boolean)}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#shutdown(boolean)}
     */
    @Test
    public void testShutdown() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingS3Daemon.accCtx
        //     BackupShippingS3Daemon.afterTermination
        //     BackupShippingS3Daemon.afterTerminationSent
        //     BackupShippingS3Daemon.backupHandler
        //     BackupShippingS3Daemon.backupName
        //     BackupShippingS3Daemon.cmdProcess
        //     BackupShippingS3Daemon.cmdThread
        //     BackupShippingS3Daemon.command
        //     BackupShippingS3Daemon.deque
        //     BackupShippingS3Daemon.doneFirst
        //     BackupShippingS3Daemon.errorReporter
        //     BackupShippingS3Daemon.handler
        //     BackupShippingS3Daemon.masterKey
        //     BackupShippingS3Daemon.remote
        //     BackupShippingS3Daemon.restore
        //     BackupShippingS3Daemon.running
        //     BackupShippingS3Daemon.s3Thread
        //     BackupShippingS3Daemon.syncObj
        //     BackupShippingS3Daemon.threadGroup
        //     BackupShippingS3Daemon.uploadId
        //     BackupShippingS3Daemon.volSize

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8")))
                .shutdown(true);
    }

    /**
     * Test {@link BackupShippingS3Daemon#shutdown(boolean)}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#shutdown(boolean)}
     */
    @Test
    public void testShutdown2() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingS3Daemon.accCtx
        //     BackupShippingS3Daemon.afterTermination
        //     BackupShippingS3Daemon.afterTerminationSent
        //     BackupShippingS3Daemon.backupHandler
        //     BackupShippingS3Daemon.backupName
        //     BackupShippingS3Daemon.cmdProcess
        //     BackupShippingS3Daemon.cmdThread
        //     BackupShippingS3Daemon.command
        //     BackupShippingS3Daemon.deque
        //     BackupShippingS3Daemon.doneFirst
        //     BackupShippingS3Daemon.errorReporter
        //     BackupShippingS3Daemon.handler
        //     BackupShippingS3Daemon.masterKey
        //     BackupShippingS3Daemon.remote
        //     BackupShippingS3Daemon.restore
        //     BackupShippingS3Daemon.running
        //     BackupShippingS3Daemon.s3Thread
        //     BackupShippingS3Daemon.syncObj
        //     BackupShippingS3Daemon.threadGroup
        //     BackupShippingS3Daemon.uploadId
        //     BackupShippingS3Daemon.volSize

        // Arrange
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(null, threadGroupRef, "Thread Name", new String[]{"Command Ref"}, "Backup Name Ref",
                remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8"))).shutdown(true);
    }

    /**
     * Test {@link BackupShippingS3Daemon#shutdown(boolean)}.
     * <ul>
     *   <li>When {@code false}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#shutdown(boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testShutdown_whenFalse() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "java.util.function.BiConsumer.accept(Object, Object)" because "this.afterTermination" is null
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.threadFinished(BackupShippingS3Daemon.java:315)
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.lambda$shutdown$0(BackupShippingS3Daemon.java:397)
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.shutdown(BackupShippingS3Daemon.java:417)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8")))
                .shutdown(false);
    }

    /**
     * Test {@link BackupShippingS3Daemon#awaitShutdown(long)}.
     * <ul>
     *   <li>When minus one.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#awaitShutdown(long)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testAwaitShutdown_whenMinusOne() throws UnsupportedEncodingException, InterruptedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: timeout value is negative
        //       at java.base/java.lang.Thread.join(Thread.java:1316)
        //       at com.linbit.linstor.backupshipping.BackupShippingS3Daemon.awaitShutdown(BackupShippingS3Daemon.java:426)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8")))
                .awaitShutdown(-1L);
    }

    /**
     * Test {@link BackupShippingS3Daemon#awaitShutdown(long)}.
     * <ul>
     *   <li>When ten.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#awaitShutdown(long)}
     */
    @Test
    public void testAwaitShutdown_whenTen() throws UnsupportedEncodingException, InterruptedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingS3Daemon.accCtx
        //     BackupShippingS3Daemon.afterTermination
        //     BackupShippingS3Daemon.afterTerminationSent
        //     BackupShippingS3Daemon.backupHandler
        //     BackupShippingS3Daemon.backupName
        //     BackupShippingS3Daemon.cmdProcess
        //     BackupShippingS3Daemon.cmdThread
        //     BackupShippingS3Daemon.command
        //     BackupShippingS3Daemon.deque
        //     BackupShippingS3Daemon.doneFirst
        //     BackupShippingS3Daemon.errorReporter
        //     BackupShippingS3Daemon.handler
        //     BackupShippingS3Daemon.masterKey
        //     BackupShippingS3Daemon.remote
        //     BackupShippingS3Daemon.restore
        //     BackupShippingS3Daemon.running
        //     BackupShippingS3Daemon.s3Thread
        //     BackupShippingS3Daemon.syncObj
        //     BackupShippingS3Daemon.threadGroup
        //     BackupShippingS3Daemon.uploadId
        //     BackupShippingS3Daemon.volSize

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8")))
                .awaitShutdown(10L);
    }

    /**
     * Test {@link BackupShippingS3Daemon#setPrepareAbort()}.
     * <p>
     * Method under test: {@link BackupShippingS3Daemon#setPrepareAbort()}
     */
    @Test
    public void testSetPrepareAbort() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingS3Daemon.accCtx
        //     BackupShippingS3Daemon.afterTermination
        //     BackupShippingS3Daemon.afterTerminationSent
        //     BackupShippingS3Daemon.backupHandler
        //     BackupShippingS3Daemon.backupName
        //     BackupShippingS3Daemon.cmdProcess
        //     BackupShippingS3Daemon.cmdThread
        //     BackupShippingS3Daemon.command
        //     BackupShippingS3Daemon.deque
        //     BackupShippingS3Daemon.doneFirst
        //     BackupShippingS3Daemon.errorReporter
        //     BackupShippingS3Daemon.handler
        //     BackupShippingS3Daemon.masterKey
        //     BackupShippingS3Daemon.remote
        //     BackupShippingS3Daemon.restore
        //     BackupShippingS3Daemon.running
        //     BackupShippingS3Daemon.s3Thread
        //     BackupShippingS3Daemon.syncObj
        //     BackupShippingS3Daemon.threadGroup
        //     BackupShippingS3Daemon.uploadId
        //     BackupShippingS3Daemon.volSize

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ThreadGroup threadGroupRef = new ThreadGroup("foo");
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        // Act
        (new BackupShippingS3Daemon(errorReporterRef, threadGroupRef, "Thread Name", new String[]{"Command Ref"},
                "Backup Name Ref", remoteRef, backupHandlerRef, true, 3L, null, null, "AXAXAXAX".getBytes("UTF-8")))
                .setPrepareAbort();
    }
}
