package com.linbit.linstor.core.apicallhandler;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.pojo.EbsRemotePojo;
import com.linbit.linstor.api.pojo.S3RemotePojo;
import com.linbit.linstor.api.pojo.StltRemotePojo;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.RemoteMap;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.objects.EbsRemoteSatelliteFactory;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.S3RemoteSatelliteFactory;
import com.linbit.linstor.core.objects.StltRemoteSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.dbdrivers.SatelliteEbsRemoteDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteS3RemoteDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class StltRemoteApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltRemoteApiCallHandler#applyChangesS3(S3RemotePojo)}.
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyChangesS3(S3RemotePojo)}
     */
    @Test
    public void testApplyChangesS3() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltRemoteApiCallHandler.apiCtx
        //     StltRemoteApiCallHandler.ctrlPeerConnector
        //     StltRemoteApiCallHandler.deviceManager
        //     StltRemoteApiCallHandler.ebsRemoteFactory
        //     StltRemoteApiCallHandler.errorReporter
        //     StltRemoteApiCallHandler.remoteMap
        //     StltRemoteApiCallHandler.s3remoteFactory
        //     StltRemoteApiCallHandler.stltRemoteFactory
        //     StltRemoteApiCallHandler.transMgrProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef,
                new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3, new TransactionObjectFactory(null), null),
                null, null);
        UUID uuidRef = UUID.randomUUID();
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltRemoteApiCallHandler
                .applyChangesS3(new S3RemotePojo(uuidRef, "Remote Name Ref", 1L, "https://config.us-east-2.amazonaws.com",
                        "s3://bucket-name/object-key", "us-east-2", accessKeyRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyChangesS3(S3RemotePojo)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyChangesS3(S3RemotePojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyChangesS3_givenReentrantReadWriteLockWithTrue() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteSatelliteFactory stltRemoteFactoryRef = new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3,
                new TransactionObjectFactory(null), null);

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef4,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef, stltRemoteFactoryRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef,
                        storPoolDfnMapLockRef, errorReporterRef2, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))),
                null);
        UUID uuidRef = UUID.randomUUID();
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltRemoteApiCallHandler
                .applyChangesS3(new S3RemotePojo(uuidRef, "Remote Name Ref", 1L, "https://config.us-east-2.amazonaws.com",
                        "s3://bucket-name/object-key", "us-east-2", accessKeyRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyChangesEbs(EbsRemotePojo)}.
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyChangesEbs(EbsRemotePojo)}
     */
    @Test
    public void testApplyChangesEbs() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltRemoteApiCallHandler.apiCtx
        //     StltRemoteApiCallHandler.ctrlPeerConnector
        //     StltRemoteApiCallHandler.deviceManager
        //     StltRemoteApiCallHandler.ebsRemoteFactory
        //     StltRemoteApiCallHandler.errorReporter
        //     StltRemoteApiCallHandler.remoteMap
        //     StltRemoteApiCallHandler.s3remoteFactory
        //     StltRemoteApiCallHandler.stltRemoteFactory
        //     StltRemoteApiCallHandler.transMgrProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef,
                new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3, new TransactionObjectFactory(null), null),
                null, null);
        UUID uuidRef = UUID.randomUUID();
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltRemoteApiCallHandler
                .applyChangesEbs(new EbsRemotePojo(uuidRef, "Remote Name Ref", 1L, "https://example.org/example",
                        "Availability Zone Ref", "us-east-2", accessKeyRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyChangesEbs(EbsRemotePojo)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyChangesEbs(EbsRemotePojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyChangesEbs_givenReentrantReadWriteLockWithTrue() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteSatelliteFactory stltRemoteFactoryRef = new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3,
                new TransactionObjectFactory(null), null);

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef4,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef, stltRemoteFactoryRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef,
                        storPoolDfnMapLockRef, errorReporterRef2, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))),
                null);
        UUID uuidRef = UUID.randomUUID();
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltRemoteApiCallHandler
                .applyChangesEbs(new EbsRemotePojo(uuidRef, "Remote Name Ref", 1L, "https://example.org/example",
                        "Availability Zone Ref", "us-east-2", accessKeyRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyDeletedRemote(String)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyDeletedRemote(String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyDeletedRemote_givenReentrantReadWriteLockWithTrue() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteSatelliteFactory stltRemoteFactoryRef = new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3,
                new TransactionObjectFactory(null), null);

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef4,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();

        // Act
        (new StltRemoteApiCallHandler(errorReporterRef, null, null, null, s3remoteFactoryRef, ebsRemoteFactoryRef,
                stltRemoteFactoryRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef,
                        storPoolDfnMapLockRef, errorReporterRef2, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))),
                null)).applyDeletedRemote("Remote Name Str Ref");
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyChangesStlt(StltRemotePojo)}.
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyChangesStlt(StltRemotePojo)}
     */
    @Test
    public void testApplyChangesStlt() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltRemoteApiCallHandler.apiCtx
        //     StltRemoteApiCallHandler.ctrlPeerConnector
        //     StltRemoteApiCallHandler.deviceManager
        //     StltRemoteApiCallHandler.ebsRemoteFactory
        //     StltRemoteApiCallHandler.errorReporter
        //     StltRemoteApiCallHandler.remoteMap
        //     StltRemoteApiCallHandler.s3remoteFactory
        //     StltRemoteApiCallHandler.stltRemoteFactory
        //     StltRemoteApiCallHandler.transMgrProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef,
                new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3, new TransactionObjectFactory(null), null),
                null, null);
        UUID uuidRef = UUID.randomUUID();

        // Act
        stltRemoteApiCallHandler
                .applyChangesStlt(new StltRemotePojo(uuidRef, "Remote Name Ref", 1L, "Ip Ret", new HashMap<>(), true, 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyChangesStlt(StltRemotePojo)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyChangesStlt(StltRemotePojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyChangesStlt_givenReentrantReadWriteLockWithTrue() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteSatelliteFactory stltRemoteFactoryRef = new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3,
                new TransactionObjectFactory(null), null);

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef4,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef, stltRemoteFactoryRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef,
                        storPoolDfnMapLockRef, errorReporterRef2, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))),
                null);
        UUID uuidRef = UUID.randomUUID();

        // Act
        stltRemoteApiCallHandler
                .applyChangesStlt(new StltRemotePojo(uuidRef, "Remote Name Ref", 1L, "Ip Ret", new HashMap<>(), true, 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyDeletedStltRemote(StltRemotePojo)}.
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyDeletedStltRemote(StltRemotePojo)}
     */
    @Test
    public void testApplyDeletedStltRemote() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltRemoteApiCallHandler.apiCtx
        //     StltRemoteApiCallHandler.ctrlPeerConnector
        //     StltRemoteApiCallHandler.deviceManager
        //     StltRemoteApiCallHandler.ebsRemoteFactory
        //     StltRemoteApiCallHandler.errorReporter
        //     StltRemoteApiCallHandler.remoteMap
        //     StltRemoteApiCallHandler.s3remoteFactory
        //     StltRemoteApiCallHandler.stltRemoteFactory
        //     StltRemoteApiCallHandler.transMgrProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef,
                new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3, new TransactionObjectFactory(null), null),
                null, null);
        UUID uuidRef = UUID.randomUUID();

        // Act
        stltRemoteApiCallHandler.applyDeletedStltRemote(
                new StltRemotePojo(uuidRef, "Remote Name Ref", 1L, "Ip Ret", new HashMap<>(), true, 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#applyDeletedStltRemote(StltRemotePojo)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#applyDeletedStltRemote(StltRemotePojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyDeletedStltRemote_givenReentrantReadWriteLockWithTrue() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        S3RemoteSatelliteFactory s3remoteFactoryRef = new S3RemoteSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        SatelliteEbsRemoteDriver driverRef2 = new SatelliteEbsRemoteDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = new EbsRemoteSatelliteFactory(null, driverRef2,
                objectProtectionFactoryRef2, new TransactionObjectFactory(null), null);

        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        StltRemoteSatelliteFactory stltRemoteFactoryRef = new StltRemoteSatelliteFactory(null, objectProtectionFactoryRef3,
                new TransactionObjectFactory(null), null);

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef4,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        StltRemoteApiCallHandler stltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, null, null, null,
                s3remoteFactoryRef, ebsRemoteFactoryRef, stltRemoteFactoryRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef,
                        storPoolDfnMapLockRef, errorReporterRef2, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))),
                null);
        UUID uuidRef = UUID.randomUUID();

        // Act
        stltRemoteApiCallHandler.applyDeletedStltRemote(
                new StltRemotePojo(uuidRef, "Remote Name Ref", 1L, "Ip Ret", new HashMap<>(), true, 1L, 1L));
    }

    /**
     * Test {@link StltRemoteApiCallHandler#StltRemoteApiCallHandler(ErrorReporter, AccessContext, DeviceManager, RemoteMap, S3RemoteSatelliteFactory, EbsRemoteSatelliteFactory, StltRemoteSatelliteFactory, ControllerPeerConnectorImpl, Provider)}.
     * <p>
     * Method under test: {@link StltRemoteApiCallHandler#StltRemoteApiCallHandler(ErrorReporter, AccessContext, DeviceManager, RemoteMap, S3RemoteSatelliteFactory, EbsRemoteSatelliteFactory, StltRemoteSatelliteFactory, ControllerPeerConnectorImpl, Provider)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewStltRemoteApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltRemoteApiCallHandler.<init>(ErrorReporter, AccessContext, DeviceManager, RemoteMap, S3RemoteSatelliteFactory, EbsRemoteSatelliteFactory, StltRemoteSatelliteFactory, ControllerPeerConnectorImpl, Provider).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ErrorReporter errorReporterRef = null;
        AccessContext apiCtxRef = null;
        DeviceManager deviceManagerRef = null;
        RemoteMap remoteMapRef = null;
        S3RemoteSatelliteFactory s3remoteFactoryRef = null;
        EbsRemoteSatelliteFactory ebsRemoteFactoryRef = null;
        StltRemoteSatelliteFactory stltRemoteFactoryRef = null;
        ControllerPeerConnectorImpl ctrlPeerConnectorRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;

        // Act
        StltRemoteApiCallHandler actualStltRemoteApiCallHandler = new StltRemoteApiCallHandler(errorReporterRef, apiCtxRef,
                deviceManagerRef, remoteMapRef, s3remoteFactoryRef, ebsRemoteFactoryRef, stltRemoteFactoryRef,
                ctrlPeerConnectorRef, transMgrProviderRef);

        // Assert
        // TODO: Add assertions on result
    }
}
