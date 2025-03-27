package com.linbit.linstor.backupshipping;

import com.linbit.InvalidNameException;
import com.linbit.ValueOutOfRangeException;
import com.linbit.drbd.DrbdVersion;
import com.linbit.drbd.md.MdException;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.backupshipping.AbsBackupShippingService.ShippingInfo;
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
import com.linbit.linstor.core.objects.remotes.S3Remote;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerResourceIdDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerStorageRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerStorageVlmDbDriver;
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
import com.linbit.linstor.propscon.InvalidKeyException;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData;
import com.linbit.linstor.storage.data.adapter.bcache.BCacheVlmData;
import com.linbit.linstor.storage.data.provider.AbsStorageVlmData;
import com.linbit.linstor.storage.data.provider.StorageRscData;
import com.linbit.linstor.storage.data.provider.diskless.DisklessData;
import com.linbit.linstor.storage.interfaces.categories.resource.AbsRscLayerObject;
import com.linbit.linstor.storage.interfaces.categories.resource.VlmProviderObject;
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

public class BackupShippingL2LServiceDiffblueTest {
    /**
     * Test {@link BackupShippingL2LService#getCommandReceiving(String, AbsRemote, AbsStorageVlmData)}.
     * <p>
     * Method under test: {@link BackupShippingL2LService#getCommandReceiving(String, AbsRemote, AbsStorageVlmData)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetCommandReceiving() throws ValueOutOfRangeException, MdException, DatabaseException,
            AccessDeniedException, UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
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
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingL2LService backupShippingL2LService = new BackupShippingL2LService(errorReporterRef, extCmdFactoryRef,
                controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef, stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        UUID objIdRef2 = UUID.randomUUID();
        UUID objIdRef3 = UUID.randomUUID();
        UUID objIdRef4 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef3 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef4, null, null, null, 1L, dbDriverRef3, null,
                null, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();
        Snapshot snapshotRef = new Snapshot(objIdRef3, snapshotDfnRef, null, 1L, dbDriverRef4, propsConFactory,
                transObjFactory, null, snapshotVlmMapRef,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        UUID objIdRef5 = UUID.randomUUID();
        UUID objIdRef6 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef5 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef2 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef2 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef2 = new SnapshotDefinition(objIdRef6, null, null, null, 1L, dbDriverRef5, null,
                null, null, snapshotVlmDfnMapRef2, snapshotMapRef2, new HashMap<>());

        VolumeNumber vlmNrRef = new VolumeNumber(10);
        SatelliteSnapshotVlmDfnDriver dbDriverRef6 = new SatelliteSnapshotVlmDfnDriver();
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<NodeName, SnapshotVolume> snapshotVlmMapRef2 = new HashMap<>();
        SnapshotVolumeDefinition snapshotVolumeDefinitionRef = new SnapshotVolumeDefinition(objIdRef5, snapshotDfnRef2,
                null, vlmNrRef, 3L, 1L, dbDriverRef6, propsContainerFactory, transObjFactory2, null, snapshotVlmMapRef2,
                new HashMap<>());

        SatelliteSnapshotVlmDriver dbDriverRef7 = new SatelliteSnapshotVlmDriver();
        PropsContainerFactory propsConFactory2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotVolume vlmRef = new SnapshotVolume(objIdRef2, snapshotRef, snapshotVolumeDefinitionRef, dbDriverRef7,
                propsConFactory2, new TransactionObjectFactory(null), null);

        UUID objIdRef7 = UUID.randomUUID();
        SatelliteSnapshotDriver dbDriverRef8 = new SatelliteSnapshotDriver();
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef3 = new HashMap<>();
        Snapshot snapshot = new Snapshot(objIdRef7, null, null, 1L, dbDriverRef8, null, null, null, snapshotVlmMapRef3,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashSet<AbsRscLayerObject<Snapshot>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Snapshot>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Snapshot> parentRef = new BCacheRscData<>(1, snapshot, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        UUID objIdRef8 = UUID.randomUUID();
        UUID objIdRef9 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef9 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef3 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef3 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef3 = new SnapshotDefinition(objIdRef9, null, null, null, 1L, dbDriverRef9, null,
                null, null, snapshotVlmDfnMapRef3, snapshotMapRef3, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef10 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory3 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef4 = new HashMap<>();
        Snapshot snapshot2 = new Snapshot(objIdRef8, snapshotDfnRef3, null, 1L, dbDriverRef10, propsConFactory3,
                transObjFactory3, null, snapshotVlmMapRef4,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashMap<VolumeNumber, VlmProviderObject<Snapshot>> vlmProviderObjectsRef2 = new HashMap<>();
        SatelliteLayerStorageRscDbDriver dbDriverRef11 = new SatelliteLayerStorageRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageVlmDbDriver dbVlmDriverRef = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        StorageRscData<Snapshot> rscDataRef = new StorageRscData<>(1, parentRef, snapshot2, "Rsc Name Suffix Ref",
                vlmProviderObjectsRef2, dbDriverRef11, dbVlmDriverRef, new TransactionObjectFactory(null), null);

        SatelliteLayerStorageVlmDbDriver dbDriverRef12 = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        backupShippingL2LService.getCommandReceiving("Cmd Ref", remoteRef,
                new DisklessData<>(vlmRef, rscDataRef, 3L, null, dbDriverRef12, new TransactionObjectFactory(null), null));
    }

    /**
     * Test {@link BackupShippingL2LService#getCommandSending(String, AbsRemote, AbsStorageVlmData)}.
     * <p>
     * Method under test: {@link BackupShippingL2LService#getCommandSending(String, AbsRemote, AbsStorageVlmData)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetCommandSending() throws ValueOutOfRangeException, MdException, DatabaseException,
            AccessDeniedException, UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
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
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingL2LService backupShippingL2LService = new BackupShippingL2LService(errorReporterRef, extCmdFactoryRef,
                controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef, stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));
        UUID objIdRef = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");
        S3Remote remoteRef = new S3Remote(null, objIdRef, driverRef, remoteNameRef, 1L,
                "https://config.us-east-2.amazonaws.com", "s3://bucket-name/object-key", "us-east-2", accessKeyRef,
                secretKeyRef, new TransactionObjectFactory(null), null);

        UUID objIdRef2 = UUID.randomUUID();
        UUID objIdRef3 = UUID.randomUUID();
        UUID objIdRef4 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef3 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef4, null, null, null, 1L, dbDriverRef3, null,
                null, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();
        Snapshot snapshotRef = new Snapshot(objIdRef3, snapshotDfnRef, null, 1L, dbDriverRef4, propsConFactory,
                transObjFactory, null, snapshotVlmMapRef,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        UUID objIdRef5 = UUID.randomUUID();
        UUID objIdRef6 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef5 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef2 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef2 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef2 = new SnapshotDefinition(objIdRef6, null, null, null, 1L, dbDriverRef5, null,
                null, null, snapshotVlmDfnMapRef2, snapshotMapRef2, new HashMap<>());

        VolumeNumber vlmNrRef = new VolumeNumber(10);
        SatelliteSnapshotVlmDfnDriver dbDriverRef6 = new SatelliteSnapshotVlmDfnDriver();
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<NodeName, SnapshotVolume> snapshotVlmMapRef2 = new HashMap<>();
        SnapshotVolumeDefinition snapshotVolumeDefinitionRef = new SnapshotVolumeDefinition(objIdRef5, snapshotDfnRef2,
                null, vlmNrRef, 3L, 1L, dbDriverRef6, propsContainerFactory, transObjFactory2, null, snapshotVlmMapRef2,
                new HashMap<>());

        SatelliteSnapshotVlmDriver dbDriverRef7 = new SatelliteSnapshotVlmDriver();
        PropsContainerFactory propsConFactory2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotVolume vlmRef = new SnapshotVolume(objIdRef2, snapshotRef, snapshotVolumeDefinitionRef, dbDriverRef7,
                propsConFactory2, new TransactionObjectFactory(null), null);

        UUID objIdRef7 = UUID.randomUUID();
        SatelliteSnapshotDriver dbDriverRef8 = new SatelliteSnapshotDriver();
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef3 = new HashMap<>();
        Snapshot snapshot = new Snapshot(objIdRef7, null, null, 1L, dbDriverRef8, null, null, null, snapshotVlmMapRef3,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashSet<AbsRscLayerObject<Snapshot>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Snapshot>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Snapshot> parentRef = new BCacheRscData<>(1, snapshot, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        UUID objIdRef8 = UUID.randomUUID();
        UUID objIdRef9 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef9 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef3 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef3 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef3 = new SnapshotDefinition(objIdRef9, null, null, null, 1L, dbDriverRef9, null,
                null, null, snapshotVlmDfnMapRef3, snapshotMapRef3, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef10 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory3 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef4 = new HashMap<>();
        Snapshot snapshot2 = new Snapshot(objIdRef8, snapshotDfnRef3, null, 1L, dbDriverRef10, propsConFactory3,
                transObjFactory3, null, snapshotVlmMapRef4,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashMap<VolumeNumber, VlmProviderObject<Snapshot>> vlmProviderObjectsRef2 = new HashMap<>();
        SatelliteLayerStorageRscDbDriver dbDriverRef11 = new SatelliteLayerStorageRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageVlmDbDriver dbVlmDriverRef = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        StorageRscData<Snapshot> rscDataRef = new StorageRscData<>(1, parentRef, snapshot2, "Rsc Name Suffix Ref",
                vlmProviderObjectsRef2, dbDriverRef11, dbVlmDriverRef, new TransactionObjectFactory(null), null);

        SatelliteLayerStorageVlmDbDriver dbDriverRef12 = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        backupShippingL2LService.getCommandSending("Cmd Ref", remoteRef,
                new DisklessData<>(vlmRef, rscDataRef, 3L, null, dbDriverRef12, new TransactionObjectFactory(null), null));
    }

    /**
     * Test {@link BackupShippingL2LService#createDaemon(AbsStorageVlmData, String[], String, AbsRemote, boolean, Integer, BiConsumer)}.
     * <p>
     * Method under test: {@link BackupShippingL2LService#createDaemon(AbsStorageVlmData, String[], String, AbsRemote, boolean, Integer, BiConsumer)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCreateDaemon()
            throws ValueOutOfRangeException, MdException, DatabaseException, UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
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
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingL2LService backupShippingL2LService = new BackupShippingL2LService(errorReporterRef, extCmdFactoryRef,
                controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef, stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        UUID objIdRef3 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef3 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef3, null, null, null, 1L, dbDriverRef3, null,
                null, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();
        Snapshot snapshotRef = new Snapshot(objIdRef2, snapshotDfnRef, null, 1L, dbDriverRef4, propsConFactory,
                transObjFactory, null, snapshotVlmMapRef,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        UUID objIdRef4 = UUID.randomUUID();
        UUID objIdRef5 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef5 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef2 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef2 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef2 = new SnapshotDefinition(objIdRef5, null, null, null, 1L, dbDriverRef5, null,
                null, null, snapshotVlmDfnMapRef2, snapshotMapRef2, new HashMap<>());

        VolumeNumber vlmNrRef = new VolumeNumber(10);
        SatelliteSnapshotVlmDfnDriver dbDriverRef6 = new SatelliteSnapshotVlmDfnDriver();
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<NodeName, SnapshotVolume> snapshotVlmMapRef2 = new HashMap<>();
        SnapshotVolumeDefinition snapshotVolumeDefinitionRef = new SnapshotVolumeDefinition(objIdRef4, snapshotDfnRef2,
                null, vlmNrRef, 3L, 1L, dbDriverRef6, propsContainerFactory, transObjFactory2, null, snapshotVlmMapRef2,
                new HashMap<>());

        SatelliteSnapshotVlmDriver dbDriverRef7 = new SatelliteSnapshotVlmDriver();
        PropsContainerFactory propsConFactory2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotVolume vlmRef = new SnapshotVolume(objIdRef, snapshotRef, snapshotVolumeDefinitionRef, dbDriverRef7,
                propsConFactory2, new TransactionObjectFactory(null), null);

        UUID objIdRef6 = UUID.randomUUID();
        SatelliteSnapshotDriver dbDriverRef8 = new SatelliteSnapshotDriver();
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef3 = new HashMap<>();
        Snapshot snapshot = new Snapshot(objIdRef6, null, null, 1L, dbDriverRef8, null, null, null, snapshotVlmMapRef3,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashSet<AbsRscLayerObject<Snapshot>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Snapshot>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Snapshot> parentRef = new BCacheRscData<>(1, snapshot, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        UUID objIdRef7 = UUID.randomUUID();
        UUID objIdRef8 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef9 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef3 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef3 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef3 = new SnapshotDefinition(objIdRef8, null, null, null, 1L, dbDriverRef9, null,
                null, null, snapshotVlmDfnMapRef3, snapshotMapRef3, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef10 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory3 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef4 = new HashMap<>();
        Snapshot snapshot2 = new Snapshot(objIdRef7, snapshotDfnRef3, null, 1L, dbDriverRef10, propsConFactory3,
                transObjFactory3, null, snapshotVlmMapRef4,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashMap<VolumeNumber, VlmProviderObject<Snapshot>> vlmProviderObjectsRef2 = new HashMap<>();
        SatelliteLayerStorageRscDbDriver dbDriverRef11 = new SatelliteLayerStorageRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageVlmDbDriver dbVlmDriverRef = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        StorageRscData<Snapshot> rscDataRef = new StorageRscData<>(1, parentRef, snapshot2, "Rsc Name Suffix Ref",
                vlmProviderObjectsRef2, dbDriverRef11, dbVlmDriverRef, new TransactionObjectFactory(null), null);

        SatelliteLayerStorageVlmDbDriver dbDriverRef12 = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        DisklessData<Snapshot> snapVlmDataRef = new DisklessData<>(vlmRef, rscDataRef, 3L, null, dbDriverRef12,
                new TransactionObjectFactory(null), null);

        UUID objIdRef9 = UUID.randomUUID();
        SatelliteS3RemoteDriver driverRef = new SatelliteS3RemoteDriver();
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");
        byte[] accessKeyRef = "AXAXAXAX".getBytes("UTF-8");
        byte[] secretKeyRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        backupShippingL2LService.createDaemon(snapVlmDataRef, new String[]{"Full Command Ref"}, "Backup Name Ref",
                new S3Remote(null, objIdRef9, driverRef, remoteNameRef, 1L, "https://config.us-east-2.amazonaws.com",
                        "s3://bucket-name/object-key", "us-east-2", accessKeyRef, secretKeyRef, new TransactionObjectFactory(null),
                        null),
                true, 1, null);
    }

    /**
     * Test {@link BackupShippingL2LService#getBackupNameForRestore(AbsStorageVlmData)}.
     * <p>
     * Method under test: {@link BackupShippingL2LService#getBackupNameForRestore(AbsStorageVlmData)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetBackupNameForRestore()
            throws ValueOutOfRangeException, MdException, DatabaseException, InvalidKeyException, AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
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
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingL2LService backupShippingL2LService = new BackupShippingL2LService(errorReporterRef, extCmdFactoryRef,
                controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef, stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        UUID objIdRef3 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef3 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef3, null, null, null, 1L, dbDriverRef3, null,
                null, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();
        Snapshot snapshotRef = new Snapshot(objIdRef2, snapshotDfnRef, null, 1L, dbDriverRef4, propsConFactory,
                transObjFactory, null, snapshotVlmMapRef,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        UUID objIdRef4 = UUID.randomUUID();
        UUID objIdRef5 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef5 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef2 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef2 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef2 = new SnapshotDefinition(objIdRef5, null, null, null, 1L, dbDriverRef5, null,
                null, null, snapshotVlmDfnMapRef2, snapshotMapRef2, new HashMap<>());

        VolumeNumber vlmNrRef = new VolumeNumber(10);
        SatelliteSnapshotVlmDfnDriver dbDriverRef6 = new SatelliteSnapshotVlmDfnDriver();
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<NodeName, SnapshotVolume> snapshotVlmMapRef2 = new HashMap<>();
        SnapshotVolumeDefinition snapshotVolumeDefinitionRef = new SnapshotVolumeDefinition(objIdRef4, snapshotDfnRef2,
                null, vlmNrRef, 3L, 1L, dbDriverRef6, propsContainerFactory, transObjFactory2, null, snapshotVlmMapRef2,
                new HashMap<>());

        SatelliteSnapshotVlmDriver dbDriverRef7 = new SatelliteSnapshotVlmDriver();
        PropsContainerFactory propsConFactory2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotVolume vlmRef = new SnapshotVolume(objIdRef, snapshotRef, snapshotVolumeDefinitionRef, dbDriverRef7,
                propsConFactory2, new TransactionObjectFactory(null), null);

        UUID objIdRef6 = UUID.randomUUID();
        SatelliteSnapshotDriver dbDriverRef8 = new SatelliteSnapshotDriver();
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef3 = new HashMap<>();
        Snapshot snapshot = new Snapshot(objIdRef6, null, null, 1L, dbDriverRef8, null, null, null, snapshotVlmMapRef3,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashSet<AbsRscLayerObject<Snapshot>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Snapshot>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Snapshot> parentRef = new BCacheRscData<>(1, snapshot, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        UUID objIdRef7 = UUID.randomUUID();
        UUID objIdRef8 = UUID.randomUUID();
        SatelliteSnapshotDfnDriver dbDriverRef9 = new SatelliteSnapshotDfnDriver();
        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef3 = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef3 = new HashMap<>();
        SnapshotDefinition snapshotDfnRef3 = new SnapshotDefinition(objIdRef8, null, null, null, 1L, dbDriverRef9, null,
                null, null, snapshotVlmDfnMapRef3, snapshotMapRef3, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef10 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory3 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef4 = new HashMap<>();
        Snapshot snapshot2 = new Snapshot(objIdRef7, snapshotDfnRef3, null, 1L, dbDriverRef10, propsConFactory3,
                transObjFactory3, null, snapshotVlmMapRef4,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        HashMap<VolumeNumber, VlmProviderObject<Snapshot>> vlmProviderObjectsRef2 = new HashMap<>();
        SatelliteLayerStorageRscDbDriver dbDriverRef11 = new SatelliteLayerStorageRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageVlmDbDriver dbVlmDriverRef = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        StorageRscData<Snapshot> rscDataRef = new StorageRscData<>(1, parentRef, snapshot2, "Rsc Name Suffix Ref",
                vlmProviderObjectsRef2, dbDriverRef11, dbVlmDriverRef, new TransactionObjectFactory(null), null);

        SatelliteLayerStorageVlmDbDriver dbDriverRef12 = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        backupShippingL2LService.getBackupNameForRestore(
                new DisklessData<>(vlmRef, rscDataRef, 3L, null, dbDriverRef12, new TransactionObjectFactory(null), null));
    }

    /**
     * Test {@link BackupShippingL2LService#preCtrlNotifyBackupShipped(boolean, boolean, Snapshot, ShippingInfo)}.
     * <p>
     * Method under test: {@link BackupShippingL2LService#preCtrlNotifyBackupShipped(boolean, boolean, Snapshot, ShippingInfo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testPreCtrlNotifyBackupShipped() throws InvalidNameException, DatabaseException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
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
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingL2LService backupShippingL2LService = new BackupShippingL2LService(errorReporterRef, extCmdFactoryRef,
                controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef, stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        SnapshotName snapshotNameRef = new SnapshotName("Snapshot Name");
        SatelliteSnapshotDfnDriver dbDriverRef3 = new SatelliteSnapshotDfnDriver();
        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef2, null, null, snapshotNameRef, 1L, dbDriverRef3,
                transObjFactory, propsContainerFactory, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();
        Snapshot snapRef = new Snapshot(objIdRef, snapshotDfnRef, null, 1L, dbDriverRef4, propsConFactory, transObjFactory2,
                null, snapshotVlmMapRef, Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        // Act
        backupShippingL2LService.preCtrlNotifyBackupShipped(true, true, snapRef, new ShippingInfo());
    }

    /**
     * Test {@link BackupShippingL2LService#postAllBackupPartsRegistered(Snapshot, ShippingInfo)}.
     * <p>
     * Method under test: {@link BackupShippingL2LService#postAllBackupPartsRegistered(Snapshot, ShippingInfo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testPostAllBackupPartsRegistered() throws InvalidNameException, DatabaseException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
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
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingL2LService backupShippingL2LService = new BackupShippingL2LService(errorReporterRef, extCmdFactoryRef,
                controllerPeerConnectorRef, interComSerializerRef, null, stltSecObjRef, stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));
        UUID objIdRef = UUID.randomUUID();
        UUID objIdRef2 = UUID.randomUUID();
        SnapshotName snapshotNameRef = new SnapshotName("Snapshot Name");
        SatelliteSnapshotDfnDriver dbDriverRef3 = new SatelliteSnapshotDfnDriver();
        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapshotDfnRef = new SnapshotDefinition(objIdRef2, null, null, snapshotNameRef, 1L, dbDriverRef3,
                transObjFactory, propsContainerFactory, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        SatelliteSnapshotDriver dbDriverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactory2 = new TransactionObjectFactory(null);
        HashMap<VolumeNumber, SnapshotVolume> snapshotVlmMapRef = new HashMap<>();
        Snapshot snap = new Snapshot(objIdRef, snapshotDfnRef, null, 1L, dbDriverRef4, propsConFactory, transObjFactory2,
                null, snapshotVlmMapRef, Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        // Act
        backupShippingL2LService.postAllBackupPartsRegistered(snap, new ShippingInfo());
    }

    /**
     * Test {@link BackupShippingL2LService#BackupShippingL2LService(ErrorReporter, ExtCmdFactory, ControllerPeerConnector, CtrlStltSerializer, AccessContext, StltSecurityObjects, StltConfigAccessor, StltConnTracker, RemoteMap, LockGuardFactory)}.
     * <p>
     * Method under test: {@link BackupShippingL2LService#BackupShippingL2LService(ErrorReporter, ExtCmdFactory, ControllerPeerConnector, CtrlStltSerializer, AccessContext, StltSecurityObjects, StltConfigAccessor, StltConnTracker, RemoteMap, LockGuardFactory)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewBackupShippingL2LService() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
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
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        SatelliteStorPoolDriver driverRef = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(driverRef, propsContainerFactoryRef,
                transObjFactoryRef, null, new FreeSpaceMgrSatelliteFactory(null));

        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef2, transObjFactoryRef2, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        StltConfig stltCfgRef = new StltConfig();
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef2, stltCfgRef,
                        new DrbdEventService(errorReporterRef4, trackerRef, null, new DrbdVersion(new CoreTimerImpl(), null))));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);

        // Act
        new BackupShippingL2LService(errorReporterRef, extCmdFactoryRef, controllerPeerConnectorRef, interComSerializerRef,
                null, stltSecObjRef, stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

    }
}
