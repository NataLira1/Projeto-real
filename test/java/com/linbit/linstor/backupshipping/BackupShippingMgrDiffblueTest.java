package com.linbit.linstor.backupshipping;

import com.linbit.InvalidNameException;
import com.linbit.ValueOutOfRangeException;
import com.linbit.drbd.DrbdVersion;
import com.linbit.drbd.md.MdException;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.BackupToS3;
import com.linbit.linstor.api.DecryptionHelper;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.core.StltConfigAccessor;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.apicallhandler.StltExtToolsChecker;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.identifier.NodeName;
import com.linbit.linstor.core.identifier.RemoteName;
import com.linbit.linstor.core.identifier.SnapshotName;
import com.linbit.linstor.core.identifier.VolumeNumber;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.Snapshot;
import com.linbit.linstor.core.objects.SnapshotDefinition;
import com.linbit.linstor.core.objects.SnapshotVolume;
import com.linbit.linstor.core.objects.SnapshotVolumeDefinition;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.core.objects.remotes.AbsRemote;
import com.linbit.linstor.core.objects.remotes.AbsRemote.RemoteType;
import com.linbit.linstor.core.objects.remotes.S3Remote;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerResourceIdDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteS3RemoteDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotVlmDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotVlmDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.modularcrypto.JclCryptoProvider;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.storage.StorageException;
import com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData;
import com.linbit.linstor.storage.data.adapter.bcache.BCacheVlmData;
import com.linbit.linstor.storage.data.adapter.cache.CacheRscData;
import com.linbit.linstor.storage.interfaces.categories.resource.AbsRscLayerObject;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.locks.LockGuardFactory;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Ignore;
import org.junit.Test;

public class BackupShippingMgrDiffblueTest {
    /**
     * Test {@link BackupShippingMgr#getService(AbsRemote)} with {@code remote}.
     * <p>
     * Method under test: {@link BackupShippingMgr#getService(AbsRemote)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetServiceWithRemote() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);
        BackupShippingMgr backupShippingMgr = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))));
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        backupShippingMgr.getService(new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null));
    }

    /**
     * Test {@link BackupShippingMgr#getService(RemoteType)} with {@code remoteType}.
     * <p>
     * Method under test: {@link BackupShippingMgr#getService(AbsRemote.RemoteType)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetServiceWithRemoteType() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);

        // Act
        (new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))))).getService(RemoteType.S3);
    }

    /**
     * Test {@link BackupShippingMgr#getService(VlmProviderObject)} with {@code snapVlmRef}.
     * <p>
     * Method under test: {@link BackupShippingMgr#getService(VlmProviderObject)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetServiceWithSnapVlmRef() throws ValueOutOfRangeException, MdException, DatabaseException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);
        BackupShippingMgr backupShippingMgr = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        UUID objIdRef3 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef3, null, null, null, 1L, dbDriverRef, null, null,
                null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef2 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();
        Snapshot snapshotRef = new Snapshot(objIdRef2, snapshotDfnRef, null, 1L, dbDriverRef2, propsConFactory,
                transObjFactory, null, snapshotVlmMapRef,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        UUID objIdRef4 = UUID.randomUUID();
        UUID objIdRef5 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef3 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef2 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef2 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef2 = new SnapshotDefinition(objIdRef5, null, null, null, 1L, dbDriverRef3, null,
                null, null, snapshotVlmDfnMapRef2, snapshotMapRef2, new HashMap<>());

        VolumeNumber vlmNrRef = new VolumeNumber(10);
        SatelliteSnapshotVlmDfnDriver dbDriverRef4 = new SatelliteSnapshotVlmDfnDriver();
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<NodeName, SnapshotVolume> snapshotVlmMapRef2 = new HashMap<>();
        SnapshotVolumeDefinition snapshotVolumeDefinitionRef = new SnapshotVolumeDefinition(objIdRef4, snapshotDfnRef2,
                null, vlmNrRef, 3L, 1L, dbDriverRef4, propsContainerFactory, transObjFactory2, null, snapshotVlmMapRef2,
                new HashMap<>());

        SatelliteSnapshotVlmDriver dbDriverRef5 = new SatelliteSnapshotVlmDriver();
        PropsContainerFactory propsConFactory2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotVolume vlmRef = new SnapshotVolume(objIdRef, snapshotRef, snapshotVolumeDefinitionRef, dbDriverRef5,
                propsConFactory2, new TransactionObjectFactory(null), null);

        UUID objIdRef6 = UUID.randomUUID();
        UUID objIdRef7 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef6 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef3 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef3 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef3 = new SnapshotDefinition(objIdRef7, null, null, null, 1L, dbDriverRef6, null,
                null, null, snapshotVlmDfnMapRef3, snapshotMapRef3, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef7 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory3 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef3 = new HashMap<>();
        Snapshot snapshot = new Snapshot(objIdRef6, snapshotDfnRef3, null, 1L, dbDriverRef7, propsConFactory3,
                transObjFactory3, null, snapshotVlmMapRef3,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashSet<AbsRscLayerObject<Snapshot>> childrenRef = new HashSet<>();
        CacheRscData<Snapshot> parentRef = new CacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref", null, null,
                new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Snapshot>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Snapshot>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Snapshot> rscDataRef = new BCacheRscData<>(1, snapshot, parentRef, childrenRef2,
                "Rsc Name Suffix Ref", bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef,
                new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        backupShippingMgr.getService(
                new BCacheVlmData<>(vlmRef, rscDataRef, null, bcacheVlmdbDriver, new TransactionObjectFactory(null), null));
    }

    /**
     * Test {@link BackupShippingMgr#getService(Snapshot)} with {@code snapshotRef}.
     * <p>
     * Method under test: {@link BackupShippingMgr#getService(Snapshot)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetServiceWithSnapshotRef() throws InvalidNameException, DatabaseException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);
        BackupShippingMgr backupShippingMgr = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        SnapshotName snapshotNameRef = new SnapshotName("Snapshot Name");
        SatelliteSnapshotDfnDriver dbDriverRef = new SatelliteSnapshotDfnDriver();
        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef2, null, null, snapshotNameRef, 1L, dbDriverRef,
                transObjFactory, propsContainerFactory, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef2 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();

        // Act
        backupShippingMgr.getService(
                new Snapshot(objIdRef, snapshotDfnRef, null, 1L, dbDriverRef2, propsConFactory, transObjFactory2, null,
                        snapshotVlmMapRef, Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())));
    }

    /**
     * Test {@link BackupShippingMgr#allBackupPartsRegistered(Snapshot)}.
     * <p>
     * Method under test: {@link BackupShippingMgr#allBackupPartsRegistered(Snapshot)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testAllBackupPartsRegistered() throws InvalidNameException, DatabaseException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);
        BackupShippingMgr backupShippingMgr = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        SnapshotName snapshotNameRef = new SnapshotName("Snapshot Name");
        SatelliteSnapshotDfnDriver dbDriverRef = new SatelliteSnapshotDfnDriver();
        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef2, null, null, snapshotNameRef, 1L, dbDriverRef,
                transObjFactory, propsContainerFactory, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef2 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();

        // Act
        backupShippingMgr.allBackupPartsRegistered(
                new Snapshot(objIdRef, snapshotDfnRef, null, 1L, dbDriverRef2, propsConFactory, transObjFactory2, null,
                        snapshotVlmMapRef, Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())));
    }

    /**
     * Test {@link BackupShippingMgr#snapshotDeleted(Snapshot)}.
     * <p>
     * Method under test: {@link BackupShippingMgr#snapshotDeleted(Snapshot)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testSnapshotDeleted() throws InvalidNameException, DatabaseException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);
        BackupShippingMgr backupShippingMgr = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        SnapshotName snapshotNameRef = new SnapshotName("Snapshot Name");
        SatelliteSnapshotDfnDriver dbDriverRef = new SatelliteSnapshotDfnDriver();
        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef2, null, null, snapshotNameRef, 1L, dbDriverRef,
                transObjFactory, propsContainerFactory, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef2 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();

        // Act
        backupShippingMgr.snapshotDeleted(
                new Snapshot(objIdRef, snapshotDfnRef, null, 1L, dbDriverRef2, propsConFactory, transObjFactory2, null,
                        snapshotVlmMapRef, Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())));
    }

    /**
     * Test {@link BackupShippingMgr#killAllShipping()}.
     * <p>
     * Method under test: {@link BackupShippingMgr#killAllShipping()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testKillAllShipping() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);

        // Act
        (new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))))).killAllShipping();
    }

    /**
     * Test {@link BackupShippingMgr#removeSnapFromStartedShipments(String, String)}.
     * <p>
     * Method under test: {@link BackupShippingMgr#removeSnapFromStartedShipments(String, String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRemoveSnapFromStartedShipments() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);

        // Act
        (new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))))).removeSnapFromStartedShipments("Rsc Name", "Snap Name");
    }

    /**
     * Test {@link BackupShippingMgr#getAllServices()}.
     * <p>
     * Method under test: {@link BackupShippingMgr#getAllServices()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetAllServices() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);

        // Act
        (new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef3, extCmdFactoryRef2, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))))).getAllServices();
    }

    /**
     * Test {@link BackupShippingMgr#BackupShippingMgr(AccessContext, RemoteMap, BackupShippingS3Service, BackupShippingL2LService)}.
     * <p>
     * Method under test: {@link BackupShippingMgr#BackupShippingMgr(AccessContext, RemoteMap, BackupShippingS3Service, BackupShippingL2LService)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewBackupShippingMgr() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef2, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef,
                extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef3 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef4 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef4,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef3, objectProtectionFactoryRef2,
                storPoolFactoryRef2, propsContainerFactoryRef2, transObjFactoryRef2, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef2 = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef2 = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef5, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef6, drbdVersionCheckRef2, extCmdFactoryRef4, stltCfgRef2,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);

        // Act
        new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef4, extCmdFactoryRef3, controllerPeerConnectorRef2,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                        new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock(true))));

    }
}
