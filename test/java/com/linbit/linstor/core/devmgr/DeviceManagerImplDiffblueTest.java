package com.linbit.linstor.core.devmgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.linbit.InvalidNameException;
import com.linbit.PlatformStlt;
import com.linbit.ServiceName;
import com.linbit.SystemServiceStartException;
import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.extproc.ExtCmdFactoryStlt;
import com.linbit.fsevent.FileSystemWatch;
import com.linbit.linstor.SatelliteDbDriver;
import com.linbit.linstor.api.ApiCallRc;
import com.linbit.linstor.api.BackupToS3;
import com.linbit.linstor.api.DecryptionHelper;
import com.linbit.linstor.api.LinStorScope;
import com.linbit.linstor.api.prop.WhitelistProps;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.backupshipping.BackupShippingL2LService;
import com.linbit.linstor.backupshipping.BackupShippingMgr;
import com.linbit.linstor.backupshipping.BackupShippingS3Service;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.core.StltConfigAccessor;
import com.linbit.linstor.core.StltExternalFileHandler;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.StltUpdateRequester;
import com.linbit.linstor.core.StltUpdateTracker;
import com.linbit.linstor.core.StltUpdateTrackerImpl;
import com.linbit.linstor.core.StltUpdateTrackerImpl.UpdateNotification;
import com.linbit.linstor.core.SysFsHandler;
import com.linbit.linstor.core.UdevHandler;
import com.linbit.linstor.core.UpdateMonitorImpl;
import com.linbit.linstor.core.apicallhandler.StltApiCallHandlerUtils;
import com.linbit.linstor.core.apicallhandler.StltExtToolsChecker;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo.ReadOnlyNode;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo.ReadOnlyStorPool;
import com.linbit.linstor.core.identifier.ExternalFileName;
import com.linbit.linstor.core.identifier.NodeName;
import com.linbit.linstor.core.identifier.RemoteName;
import com.linbit.linstor.core.identifier.ResourceName;
import com.linbit.linstor.core.identifier.SnapshotName;
import com.linbit.linstor.core.identifier.StorPoolName;
import com.linbit.linstor.core.identifier.VolumeNumber;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.Resource;
import com.linbit.linstor.core.objects.Resource.ResourceKey;
import com.linbit.linstor.core.objects.Snapshot;
import com.linbit.linstor.core.objects.SnapshotDefinition;
import com.linbit.linstor.core.objects.SnapshotDefinition.Key;
import com.linbit.linstor.core.objects.SnapshotVolume;
import com.linbit.linstor.core.objects.SnapshotVolumeDefinition;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.event.GenericEvent;
import com.linbit.linstor.event.common.ResourceStateEvent;
import com.linbit.linstor.layer.LayerFactory;
import com.linbit.linstor.layer.LayerSizeHelper;
import com.linbit.linstor.layer.bcache.BCacheLayer;
import com.linbit.linstor.layer.dmsetup.cache.CacheLayer;
import com.linbit.linstor.layer.dmsetup.writecache.WritecacheLayer;
import com.linbit.linstor.layer.drbd.DrbdLayer;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventPublisher;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.layer.drbd.utils.DrbdAdm;
import com.linbit.linstor.layer.drbd.utils.WindowsFirewall;
import com.linbit.linstor.layer.luks.CryptSetupCommands;
import com.linbit.linstor.layer.luks.LuksLayer;
import com.linbit.linstor.layer.nvme.NvmeLayer;
import com.linbit.linstor.layer.nvme.NvmeUtils;
import com.linbit.linstor.layer.storage.AbsStorageProvider;
import com.linbit.linstor.layer.storage.AbsStorageProvider.AbsStorageProviderInit;
import com.linbit.linstor.layer.storage.DeviceProviderMapper;
import com.linbit.linstor.layer.storage.StorageLayer;
import com.linbit.linstor.layer.storage.WipeHandler;
import com.linbit.linstor.layer.storage.diskless.DisklessProvider;
import com.linbit.linstor.layer.storage.ebs.AbsEbsProvider;
import com.linbit.linstor.layer.storage.ebs.AbsEbsProvider.AbsEbsProviderIniit;
import com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider;
import com.linbit.linstor.layer.storage.ebs.EbsTargetProvider;
import com.linbit.linstor.layer.storage.exos.ExosProvider;
import com.linbit.linstor.layer.storage.file.FileProvider;
import com.linbit.linstor.layer.storage.file.FileThinProvider;
import com.linbit.linstor.layer.storage.lvm.LvmProvider;
import com.linbit.linstor.layer.storage.lvm.LvmThinProvider;
import com.linbit.linstor.layer.storage.spdk.SpdkLocalProvider;
import com.linbit.linstor.layer.storage.spdk.SpdkRemoteProvider;
import com.linbit.linstor.layer.storage.spdk.utils.SpdkLocalCommands;
import com.linbit.linstor.layer.storage.spdk.utils.SpdkRemoteCommands;
import com.linbit.linstor.layer.storage.storagespaces.StorageSpacesProvider;
import com.linbit.linstor.layer.storage.storagespaces.StorageSpacesThinProvider;
import com.linbit.linstor.layer.storage.zfs.ZfsProvider;
import com.linbit.linstor.layer.storage.zfs.ZfsThinProvider;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.modularcrypto.JclCryptoProvider;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.snapshotshipping.SnapshotShippingService;
import com.linbit.linstor.storage.StorageException;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.locks.LockGuardFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Ignore;
import org.junit.Test;

public class DeviceManagerImplDiffblueTest {
    /**
     * Test {@link DeviceManagerImpl#setServiceInstanceName(ServiceName)}.
     * <ul>
     *   <li>When {@link SatelliteDbDriver#DFLT_SERVICE_INSTANCE_NAME}.</li>
     * </ul>
     * <p>
     * Method under test: {@link DeviceManagerImpl#setServiceInstanceName(ServiceName)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testSetServiceInstanceName_whenDflt_service_instance_name() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).setServiceInstanceName(SatelliteDbDriver.DFLT_SERVICE_INSTANCE_NAME);
    }

    /**
     * Test {@link DeviceManagerImpl#start()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart() throws SystemServiceStartException, StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).start();
    }

    /**
     * Test {@link DeviceManagerImpl#shutdown()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#shutdown()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testShutdown() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).shutdown();
    }

    /**
     * Test {@link DeviceManagerImpl#awaitShutdown(long)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#awaitShutdown(long)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testAwaitShutdown() throws StorageException, InterruptedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).awaitShutdown(10L);
    }

    /**
     * Test {@link DeviceManagerImpl#drbdStateAvailable()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#drbdStateAvailable()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testDrbdStateAvailable() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).drbdStateAvailable();
    }

    /**
     * Test {@link DeviceManagerImpl#controllerUpdateApplied(Set)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#controllerUpdateApplied(Set)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testControllerUpdateApplied() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Set<ResourceName> rscSet = null;

        // Act
        deviceManagerImpl.controllerUpdateApplied(rscSet);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#nodeUpdateApplied(Set, Set)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#nodeUpdateApplied(Set, Set)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNodeUpdateApplied() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Set<NodeName> nodeSet = null;
        Set<ResourceName> rscSet = null;

        // Act
        deviceManagerImpl.nodeUpdateApplied(nodeSet, rscSet);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#storPoolUpdateApplied(Set, Set, ApiCallRc)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#storPoolUpdateApplied(Set, Set, ApiCallRc)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStorPoolUpdateApplied() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Set<StorPoolName> storPoolSet = null;
        Set<ResourceName> rscSet = null;
        ApiCallRc responses = null;

        // Act
        deviceManagerImpl.storPoolUpdateApplied(storPoolSet, rscSet, responses);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#rscUpdateApplied(Set)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#rscUpdateApplied(Set)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRscUpdateApplied() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Set<ResourceKey> rscKeySet = null;

        // Act
        deviceManagerImpl.rscUpdateApplied(rscKeySet);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#snapshotUpdateApplied(Set)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#snapshotUpdateApplied(Set)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testSnapshotUpdateApplied() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Set<Key> snapshotKeySet = null;

        // Act
        deviceManagerImpl.snapshotUpdateApplied(snapshotKeySet);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#externalFileUpdateApplied(ExternalFileName, NodeName, Set)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#externalFileUpdateApplied(ExternalFileName, NodeName, Set)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testExternalFileUpdateApplied() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        ExternalFileName extFileNameRef = null;
        NodeName nodeNameRef = null;
        Set<ResourceName> rscNameSet = null;

        // Act
        deviceManagerImpl.externalFileUpdateApplied(extFileNameRef, nodeNameRef, rscNameSet);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#remoteUpdateApplied(RemoteName, NodeName)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#remoteUpdateApplied(RemoteName, NodeName)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRemoteUpdateApplied() throws InvalidNameException, StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        DeviceManagerImpl deviceManagerImpl = new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef,
                remoteMapLockRef, stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig());
        RemoteName remoteNameRef = RemoteName.createInternal("Remote Name Ref");

        // Act
        deviceManagerImpl.remoteUpdateApplied(remoteNameRef, new NodeName("Node Name"));
    }

    /**
     * Test {@link DeviceManagerImpl#markResourceForDispatch(ResourceName)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#markResourceForDispatch(ResourceName)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testMarkResourceForDispatch() throws InvalidNameException, StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        DeviceManagerImpl deviceManagerImpl = new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef,
                remoteMapLockRef, stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig());

        // Act
        deviceManagerImpl.markResourceForDispatch(new ResourceName("Res Name"));
    }

    /**
     * Test {@link DeviceManagerImpl#markMultipleResourcesForDispatch(Set)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#markMultipleResourcesForDispatch(Set)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testMarkMultipleResourcesForDispatch() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Set<ResourceName> rscSet = null;

        // Act
        deviceManagerImpl.markMultipleResourcesForDispatch(rscSet);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#forceWakeUpdateNotifications()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#forceWakeUpdateNotifications()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testForceWakeUpdateNotifications() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).forceWakeUpdateNotifications();
    }

    /**
     * Test {@link DeviceManagerImpl#applyChangedNodeProps(Props)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#applyChangedNodeProps(Props)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyChangedNodeProps() throws AccessDeniedException, StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        DeviceManagerImpl deviceManagerImpl = new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef,
                remoteMapLockRef, stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig());

        // Act
        deviceManagerImpl.applyChangedNodeProps(ReadOnlyPropsImpl.emptyRoProps());
    }

    /**
     * Test {@link DeviceManagerImpl#fullSyncApplied(Node)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#fullSyncApplied(Node)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testFullSyncApplied() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).fullSyncApplied(null);
    }

    /**
     * Test getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link DeviceManagerImpl#abortDeviceHandlers()}
     *   <li>{@link DeviceManagerImpl#drbdStateUnavailable()}
     *   <li>{@link DeviceManagerImpl#getInstanceName()}
     *   <li>{@link DeviceManagerImpl#getReadOnlyData()}
     *   <li>{@link DeviceManagerImpl#getServiceInfo()}
     *   <li>{@link DeviceManagerImpl#getServiceName()}
     *   <li>{@link DeviceManagerImpl#getUpdateTracker()}
     * </ul>
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.devmgr.DeviceManagerImpl.abortDeviceHandlers().
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;

        // Act
        deviceManagerImpl.abortDeviceHandlers();
        deviceManagerImpl.drbdStateUnavailable();
        ServiceName actualInstanceName = deviceManagerImpl.getInstanceName();
        StltReadOnlyInfo actualReadOnlyData = deviceManagerImpl.getReadOnlyData();
        String actualServiceInfo = deviceManagerImpl.getServiceInfo();
        ServiceName actualServiceName = deviceManagerImpl.getServiceName();
        StltUpdateTracker actualUpdateTracker = deviceManagerImpl.getUpdateTracker();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#run()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#run()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRun() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).run();
    }

    /**
     * Test {@link DeviceManagerImpl#clearReadOnlyStltInfo()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#clearReadOnlyStltInfo()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testClearReadOnlyStltInfo() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).clearReadOnlyStltInfo();
    }

    /**
     * Test {@link DeviceManagerImpl#sharedStorPoolLocksGranted(List)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#sharedStorPoolLocksGranted(List)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testSharedStorPoolLocksGranted() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        List<String> sharedStorPoolLocksListRef = null;

        // Act
        deviceManagerImpl.sharedStorPoolLocksGranted(sharedStorPoolLocksListRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#controllerConnectionLost()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#controllerConnectionLost()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testControllerConnectionLost() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).controllerConnectionLost();
    }

    /**
     * Test {@link DeviceManagerImpl#hasAllSharedLocksGranted()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#hasAllSharedLocksGranted()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testHasAllSharedLocksGranted() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).hasAllSharedLocksGranted();
    }

    /**
     * Test {@link DeviceManagerImpl#registerSharedExtCmdFactory(ExtCmdFactoryStlt)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#registerSharedExtCmdFactory(ExtCmdFactoryStlt)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRegisterSharedExtCmdFactory() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        DeviceManagerImpl deviceManagerImpl = new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef,
                remoteMapLockRef, stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig());
        CoreTimerImpl timerRef4 = new CoreTimerImpl();

        // Act
        deviceManagerImpl
                .registerSharedExtCmdFactory(new ExtCmdFactoryStlt(timerRef4, new StderrErrorReporter("Module Name"), null));
    }

    /**
     * Test {@link DeviceManagerImpl#getSpaceInfo(StorPoolInfo, boolean)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#getSpaceInfo(StorPoolInfo, boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetSpaceInfo() throws AccessDeniedException, StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        DeviceManagerImpl deviceManagerImpl = new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef,
                remoteMapLockRef, stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig());

        // Act
        deviceManagerImpl.getSpaceInfo(ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null), true);
    }

    /**
     * Test {@link DeviceManagerImpl#isStarted()}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#isStarted()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testIsStarted() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).isStarted();
    }

    /**
     * Test {@link DeviceManagerImpl#DeviceManagerImpl(AccessContext, ErrorReporter, NodesMap, ResourceGroupMap, ResourceDefinitionMap, ExternalFileMap, RemoteMap, ReadWriteLock, ReadWriteLock, ReadWriteLock, ReadWriteLock, ReadWriteLock, ReadWriteLock, StltUpdateRequester, ControllerPeerConnector, CtrlStltSerializer, DrbdEventService, StltApiCallHandlerUtils, LinStorScope, Provider, StltSecurityObjects, Scheduler, UpdateMonitor, ResourceStateEvent, DeviceHandler, DrbdVersion, ExtCmdFactory, SnapshotShippingService, BackupShippingMgr, StltExternalFileHandler, StltConfig)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#DeviceManagerImpl(AccessContext, ErrorReporter, NodesMap, ResourceGroupMap, ResourceDefinitionMap, ExternalFileMap, RemoteMap, ReadWriteLock, ReadWriteLock, ReadWriteLock, ReadWriteLock, ReadWriteLock, ReadWriteLock, StltUpdateRequester, ControllerPeerConnector, CtrlStltSerializer, DrbdEventService, StltApiCallHandlerUtils, LinStorScope, Provider, StltSecurityObjects, Scheduler, UpdateMonitor, ResourceStateEvent, DeviceHandler, DrbdVersion, ExtCmdFactory, SnapshotShippingService, BackupShippingMgr, StltExternalFileHandler, StltConfig)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewDeviceManagerImpl() throws StorageException, IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
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

        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef4, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef3 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef4 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef4,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        SatelliteStorPoolDriver driverRef = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(driverRef, propsContainerFactoryRef2,
                transObjFactoryRef2, null, new FreeSpaceMgrSatelliteFactory(null));

        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef3 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef3, objectProtectionFactoryRef2,
                storPoolFactoryRef2, propsContainerFactoryRef3, transObjFactoryRef3, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdVersion drbdVersionCheckRef2 = new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StltConfig stltCfgRef2 = new StltConfig();
        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef5, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef6, drbdVersionCheckRef2, extCmdFactoryRef2, stltCfgRef2,
                        new DrbdEventService(errorReporterRef7, trackerRef, null, new DrbdVersion(new CoreTimerImpl(), null))));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef2 = new DrbdStateTracker();
        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef8, trackerRef2, null,
                new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef10, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsProvider zfsProviderRef = new ZfsProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        DisklessProvider disklessProviderRef = new DisklessProvider();
        AbsStorageProviderInit superInitRef = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileProvider fileProviderRef = new FileProvider(superInitRef, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef2 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(superInitRef2, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef3 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(superInitRef3, new SpdkLocalCommands(null));

        AbsStorageProviderInit superInitRef4 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(superInitRef4,
                new SpdkRemoteCommands(null, null, null));

        ExosProvider exosProviderRef = new ExosProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef9, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                                null, null, null, null, null))),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef5 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef6 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef6,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef3 = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef4 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef4 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(dbDriverRef5, objectProtectionFactoryRef3,
                storPoolFactoryRef3, propsContainerFactoryRef4, transObjFactoryRef4, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef3 = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef3 = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef12, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(errorReporterRef13, drbdVersionCheckRef3, extCmdFactoryRef3, stltCfgRef3,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        NvmeLayer nvmeLayer = new NvmeLayer(errorReporterRef14, null,
                new NvmeUtils(null, null, ReadOnlyPropsImpl.emptyRoProps(), null, null), null);

        DrbdAdm drbdUtilsRef = new DrbdAdm(null, null, null, null);

        DrbdEventService drbdStateRef = new DrbdEventService(null, new DrbdStateTracker(), null, null);

        DrbdEventPublisher drbdEventPublisherRef = new DrbdEventPublisher(null, null, null, null, null, null);

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        WhitelistProps whiltelistPropsRef = new WhitelistProps(null);
        CtrlSecurityObjects secObjsRef4 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfigAccessor stltCfgAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DrbdVersion drbdVersionRef = new DrbdVersion(new CoreTimerImpl(), null);

        WindowsFirewall windowsFirewallRef = new WindowsFirewall(null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, drbdUtilsRef, drbdStateRef, drbdEventPublisherRef, errorReporterRef15,
                whiltelistPropsRef, interComSerializerRef4, controllerPeerConnectorRef3, null, extCmdFactoryRef4,
                stltCfgAccessorRef, drbdVersionRef, windowsFirewallRef, new PlatformStlt(null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef5 = new StltSecurityObjects();
        StorageLayer storageLayer = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef5, null, secObjsRef5,
                new StderrErrorReporter("Module Name"));

        CryptSetupCommands cryptSetupRef = new CryptSetupCommands(null, null, null);

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        StltExtToolsChecker extToolsCheckerRef = new StltExtToolsChecker(null, null, null, new StltConfig(), null);

        StltSecurityObjects secObjsRef6 = new StltSecurityObjects();
        LuksLayer luksLayer = new LuksLayer(null, cryptSetupRef, extCmdFactoryRef6, null, errorReporterRef16,
                extToolsCheckerRef, secObjsRef6, new DecryptionHelper(new JclCryptoProvider()));

        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(new CoreTimerImpl(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(errorReporterRef17, null, extCmdFactoryRef7, null,
                new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(new CoreTimerImpl(), null);

        CacheLayer cacheLayer = new CacheLayer(errorReporterRef18, null, extCmdFactoryRef8, null,
                new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfigAccessor stltConfAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        WipeHandler wipeHandlerRef = new WipeHandler(null, null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(null, null, null, ReadOnlyPropsImpl.emptyRoProps());

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(errorReporterRef19, null, extCmdFactoryRef9, null, stltConfAccessorRef,
                wipeHandlerRef, sysFsHandlerRef, new FileSystemWatch(null)));

        LvmProvider lvmProviderRef2 = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef2 = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef2 = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef2 = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef2 = new DisklessProvider();
        FileProvider fileProviderRef2 = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef2 = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef2 = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef2 = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef2 = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef2 = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef2 = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef2 = new StorageSpacesProvider(null);
        DeviceProviderMapper deviceProviderMapperRef2 = new DeviceProviderMapper(lvmProviderRef2, lvmThinProviderRef2,
                zfsProviderRef2, zfsThinProviderRef2, disklessProviderRef2, fileProviderRef2, fileThinProviderRef2,
                spdkLocalProviderRef2, spdkRemoteProviderRef2, exosProviderRef2, ebsInitProviderRef2, ebsTargetProviderRef2,
                storageSpacesProviderRef2, new StorageSpacesThinProvider(null));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        StltSecurityObjects secObjsRef7 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef2, extCmdFactoryRef10, null,
                secObjsRef7, new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef11 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef4 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef12 = new ExtCmdFactory(timerRef4, new StderrErrorReporter("Module Name"));

        SysFsHandler sysFsHandlerRef2 = new SysFsHandler(errorReporterRef20, null, extCmdFactoryRef12,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef21 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef5 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef13 = new ExtCmdFactory(timerRef5, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef22 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef4 = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef14 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef4 = new StltConfig();
        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef21, extCmdFactoryRef13,
                new StltExtToolsChecker(errorReporterRef22, drbdVersionCheckRef4, extCmdFactoryRef14, stltCfgRef4,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        CoreTimerImpl timerRef6 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef15 = new ExtCmdFactory(timerRef6, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef23 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef24 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef24, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef15,
                errorReporterRef23, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef8, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef25 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef25, null, null, null,
                new StltConfig());

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef26 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef16 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef9 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef9,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef26,
                extCmdFactoryRef16, controllerPeerConnectorRef5, interComSerializerRef5, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef27 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef17 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef10 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef6 = new ProtoCtrlStltSerializer(null, null, secObjsRef10,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef27, extCmdFactoryRef17, controllerPeerConnectorRef6,
                        interComSerializerRef6, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef28 = new StderrErrorReporter("Module Name");
        NvmeLayer nvmeLayer2 = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer2 = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer2 = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer2 = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer2 = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer2 = new CacheLayer(null, null, null, null, null);

        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef28,
                new LayerFactory(nvmeLayer2, drbdLayer2, storageLayer2, luksLayer2, writecacheLayer2, cacheLayer2,
                        new BCacheLayer(null, null, null, null, null, null, null, null)));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef11, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef11,
                sysFsHandlerRef2, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer3 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef2 = new DrbdVersion(coreTimer3, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef7 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef18 = new ExtCmdFactory(timerRef7, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef8 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef19 = new ExtCmdFactory(timerRef8, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef29 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef12 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef12 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef12 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef8 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef30 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef7 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef8 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef8,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef4 = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef5 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef5 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef5 = new NodeSatelliteFactory(dbDriverRef7, objectProtectionFactoryRef4,
                storPoolFactoryRef4, propsContainerFactoryRef5, transObjFactoryRef5, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        ProtoCommonSerializer commonSerializerRef5 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef31 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef5 = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef20 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef5 = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef7 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef12, nodesMapLockRef12, rscDfnMapLockRef12, storPoolDfnMapLockRef8, errorReporterRef30,
                null, nodeFactoryRef5, null, commonSerializerRef5, null, null,
                new StltExtToolsChecker(errorReporterRef31, drbdVersionCheckRef5, extCmdFactoryRef20, stltCfgRef5,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter5 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef11 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef19,
                errorReporterRef29, controllerPeerConnectorRef7,
                new ProtoCtrlStltSerializer(errReporter5, null, secObjsRef11, ReadOnlyPropsImpl.emptyRoProps()));

        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef2 = new BackupToS3(stltConfigAccessorRef3, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef32 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef9 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef21 = new ExtCmdFactory(timerRef9, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef13 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef13 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef13 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef9 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef33 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef6 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef6 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef8 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef13, nodesMapLockRef13, rscDfnMapLockRef13, storPoolDfnMapLockRef9, errorReporterRef33,
                null, nodeFactoryRef6, null, commonSerializerRef6, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter6 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef12 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef7 = new ProtoCtrlStltSerializer(errReporter6, null, secObjsRef12,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef4 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef4 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef14 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef14 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef14 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef10 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef4 = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef2, errorReporterRef32,
                extCmdFactoryRef21, controllerPeerConnectorRef8, interComSerializerRef7, null, stltSecObjRef4,
                stltConfigAccessorRef4, null, null,
                new LockGuardFactory(reconfigurationLockRef14, nodesMapLockRef14, rscDfnMapLockRef14, storPoolDfnMapLockRef10,
                        ctrlConfigLockRef3, kvsMapLockRef3, rscGrpMapLockRef3, extFileMapLockRef4, remoteMapLockRef4,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef34 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef10 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef22 = new ExtCmdFactory(timerRef10, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef15 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef15 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef15 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef11 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef35 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef7 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef7 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef9 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef15, nodesMapLockRef15, rscDfnMapLockRef15, storPoolDfnMapLockRef11, errorReporterRef35,
                null, nodeFactoryRef7, null, commonSerializerRef7, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter7 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef13 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef8 = new ProtoCtrlStltSerializer(errReporter7, null, secObjsRef13,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef5 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef5 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef16 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef16 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef16 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef12 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef5 = new ReentrantReadWriteLock(true);
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef34, extCmdFactoryRef22, controllerPeerConnectorRef9,
                        interComSerializerRef8, null, stltSecObjRef5, stltConfigAccessorRef5, null, null,
                        new LockGuardFactory(reconfigurationLockRef16, nodesMapLockRef16, rscDfnMapLockRef16,
                                storPoolDfnMapLockRef12, ctrlConfigLockRef4, kvsMapLockRef4, rscGrpMapLockRef4, extFileMapLockRef5,
                                remoteMapLockRef5, new ReentrantReadWriteLock(true))));

        StderrErrorReporter errorReporterRef36 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef36, null, null, null,
                new StltConfig());

        // Act
        new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef, nodesMapLockRef,
                rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef, stltUpdateRequesterRef,
                controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef, apiCallHandlerUtilsRef, deviceMgrScopeRef,
                null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef, deviceHandlerRef, drbdVersionRef2,
                extCmdFactoryRef18, snapshipServiceRef, backupServiceMgrRef, extFileHandlerRef2, new StltConfig());

    }

    /**
     * Test {@link DeviceManagerImpl#notifyResourceDispatchResponse(ResourceName, ApiCallRc)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifyResourceDispatchResponse(ResourceName, ApiCallRc)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyResourceDispatchResponse() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        ResourceName resourceName = null;
        ApiCallRc response = null;

        // Act
        deviceManagerImpl.notifyResourceDispatchResponse(resourceName, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#notifySnapshotDispatchResponse(Key, ApiCallRc)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifySnapshotDispatchResponse(SnapshotDefinition.Key, ApiCallRc)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifySnapshotDispatchResponse() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Key snapDfnKey = null;
        ApiCallRc response = null;

        // Act
        deviceManagerImpl.notifySnapshotDispatchResponse(snapDfnKey, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#notifyResourceApplied(Resource)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifyResourceApplied(Resource)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyResourceApplied() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).notifyResourceApplied(null);
    }

    /**
     * Test {@link DeviceManagerImpl#notifyDrbdVolumeResized(Volume)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifyDrbdVolumeResized(Volume)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyDrbdVolumeResized() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).notifyDrbdVolumeResized(null);
    }

    /**
     * Test {@link DeviceManagerImpl#notifyResourceDeleted(Resource)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifyResourceDeleted(Resource)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyResourceDeleted() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).notifyResourceDeleted(null);
    }

    /**
     * Test {@link DeviceManagerImpl#notifyVolumeDeleted(Volume)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifyVolumeDeleted(Volume)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyVolumeDeleted() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        // Act
        (new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null, reconfigurationLockRef,
                nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef, remoteMapLockRef,
                stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig())).notifyVolumeDeleted(null);
    }

    /**
     * Test {@link DeviceManagerImpl#notifySnapshotDeleted(Snapshot)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifySnapshotDeleted(Snapshot)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifySnapshotDeleted() throws InvalidNameException, DatabaseException, StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        DeviceManagerImpl deviceManagerImpl = new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef,
                remoteMapLockRef, stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig());
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

        // Act
        deviceManagerImpl.notifySnapshotDeleted(
                new Snapshot(objIdRef, snapshotDfnRef, null, 1L, dbDriverRef4, propsConFactory, transObjFactory2, null,
                        snapshotVlmMapRef, Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())));
    }

    /**
     * Test {@link DeviceManagerImpl#notifyFreeSpacesChanged(Map)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifyFreeSpacesChanged(Map)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyFreeSpacesChanged() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(null, null);

        StltUpdateRequester stltUpdateRequesterRef = new StltUpdateRequester(errorReporterRef2, interComSerializerRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2,
                        storPoolDfnMapLockRef2, errorReporterRef3, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(null, null, null, new StltConfig(), null)));

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef2 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef2,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"),
                null);

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef4, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(errorReporterRef5, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdEventService drbdEventRef = new DrbdEventService(errorReporterRef6, trackerRef, null,
                new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef8, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef7, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        LinStorScope deviceMgrScopeRef = new LinStorScope();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef4, nodesMapLockRef4,
                rscDfnMapLockRef4, new ReentrantReadWriteLock(true));

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(new GenericEvent<>(null));
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef10 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, storPoolDfnMapLockRef4, errorReporterRef10, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef3 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        NvmeLayer nvmeLayer = new NvmeLayer(null, null, null, null);

        DrbdLayer drbdLayer = new DrbdLayer(null, null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        StorageLayer storageLayer = new StorageLayer(null, null, null, null, new StltSecurityObjects(), null);

        LuksLayer luksLayer = new LuksLayer(null, null, null, null, null, null, new StltSecurityObjects(), null);

        WritecacheLayer writecacheLayer = new WritecacheLayer(null, null, null, null, null);

        CacheLayer cacheLayer = new CacheLayer(null, null, null, null, null);

        LayerFactory layerFactoryRef = new LayerFactory(nvmeLayer, drbdLayer, storageLayer, luksLayer, writecacheLayer,
                cacheLayer, new BCacheLayer(null, null, null, null, null, null, null, null));

        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(null, null, null, null,
                new DisklessProvider(), null, null, null, null, null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltSecurityObjects secObjsRef4 = new StltSecurityObjects();
        StorageLayer storageLayerRef = new StorageLayer(null, deviceProviderMapperRef, extCmdFactoryRef2, null, secObjsRef4,
                new StderrErrorReporter("Module Name"));

        ResourceStateEvent resourceStateEventRef2 = new ResourceStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef11 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(new CoreTimerImpl(), null);

        SysFsHandler sysFsHandlerRef = new SysFsHandler(errorReporterRef11, null, extCmdFactoryRef4,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef12 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef5 = new ExtCmdFactory(new CoreTimerImpl(), null);

        UdevHandler udevHandlerRef = new UdevHandler(errorReporterRef12, extCmdFactoryRef5,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        ExtCmdFactory extCmdFactoryRef6 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StderrErrorReporter errorReporterRef13 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef5 = new CtrlSecurityObjects();
        SnapshotShippingService snapshotShippingManagerRef = new SnapshotShippingService(null, extCmdFactoryRef6,
                errorReporterRef13, controllerPeerConnectorRef3,
                new ProtoCtrlStltSerializer(null, null, secObjsRef5, ReadOnlyPropsImpl.emptyRoProps()));

        StderrErrorReporter errorReporterRef14 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef = new StltExternalFileHandler(errorReporterRef14, null, null, null,
                new StltConfig());

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingManagerRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef15 = new StderrErrorReporter("Module Name");
        SuspendManager suspendMgrRef = new SuspendManager(null, errorReporterRef15,
                new LayerFactory(null, null, null, null, null, null, null));

        DeviceHandlerImpl deviceHandlerRef = new DeviceHandlerImpl(null, errorReporterRef9, controllerPeerConnectorRef2,
                interComSerializerRef3, null, layerFactoryRef, storageLayerRef, resourceStateEventRef2, extCmdFactoryRef3,
                sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef, extFileHandlerRef, backupShippingManagerRef,
                suspendMgrRef, new LayerSizeHelper(null, new HashMap<>()), null);

        CoreTimerImpl coreTimer2 = new CoreTimerImpl();
        DrbdVersion drbdVersionRef = new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef7 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef8 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef16 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef17 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef4 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef17, null,
                nodeFactoryRef4, null, commonSerializerRef4, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter4 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef6 = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef8,
                errorReporterRef16, controllerPeerConnectorRef4,
                new ProtoCtrlStltSerializer(errReporter4, null, secObjsRef6, ReadOnlyPropsImpl.emptyRoProps()));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef18 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef9 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef8 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef8 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef8, nodesMapLockRef8, rscDfnMapLockRef8, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef7 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef4 = new ProtoCtrlStltSerializer(null, null, secObjsRef7,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef9 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref2 = new BackupShippingS3Service(backupHandlerRef, errorReporterRef18,
                extCmdFactoryRef9, controllerPeerConnectorRef5, interComSerializerRef4, null, stltSecObjRef2,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef9, nodesMapLockRef9, rscDfnMapLockRef9, storPoolDfnMapLockRef6,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef2, remoteMapLockRef2,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef19 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef10 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef10 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef10 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef6 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef10, nodesMapLockRef10, rscDfnMapLockRef10, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef8 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef5 = new ProtoCtrlStltSerializer(null, null, secObjsRef8,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef3 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef11 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef7 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef3 = new ReentrantReadWriteLock();
        BackupShippingMgr backupServiceMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref2,
                new BackupShippingL2LService(errorReporterRef19, extCmdFactoryRef10, controllerPeerConnectorRef6,
                        interComSerializerRef5, null, stltSecObjRef3, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef11, nodesMapLockRef11, rscDfnMapLockRef11,
                                storPoolDfnMapLockRef7, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef3,
                                remoteMapLockRef3, new ReentrantReadWriteLock())));

        StderrErrorReporter errorReporterRef20 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler extFileHandlerRef2 = new StltExternalFileHandler(errorReporterRef20, null, null, null,
                new StltConfig());

        DeviceManagerImpl deviceManagerImpl = new DeviceManagerImpl(null, errorReporterRef, null, null, null, null, null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, extFileMapLockRef,
                remoteMapLockRef, stltUpdateRequesterRef, controllerPeerConnectorRef, interComSerializerRef2, drbdEventRef,
                apiCallHandlerUtilsRef, deviceMgrScopeRef, null, stltSecObjRef, null, updateMonitorRef, resourceStateEventRef,
                deviceHandlerRef, drbdVersionRef, extCmdFactoryRef7, snapshipServiceRef, backupServiceMgrRef,
                extFileHandlerRef2, new StltConfig());

        // Act
        deviceManagerImpl.notifyFreeSpacesChanged(new HashMap<>());
    }

    /**
     * Test {@link DeviceManagerImpl#notifyResourceFailed(Resource, ApiCallRc)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifyResourceFailed(Resource, ApiCallRc)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyResourceFailed() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Resource rsc = null;
        ApiCallRc apiCallRc = null;

        // Act
        deviceManagerImpl.notifyResourceFailed(rsc, apiCallRc);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#notifySnapshotRollbackResult(Resource, ApiCallRc, boolean)}.
     * <p>
     * Method under test: {@link DeviceManagerImpl#notifySnapshotRollbackResult(Resource, ApiCallRc, boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifySnapshotRollbackResult() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        DeviceManagerImpl deviceManagerImpl = null;
        Resource rscRef = null;
        ApiCallRc apiCallRcRef = null;
        boolean successRef = false;

        // Act
        deviceManagerImpl.notifySnapshotRollbackResult(rscRef, apiCallRcRef, successRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceManagerImpl#extractUuids(Map)}.
     * <ul>
     *   <li>Given forty-two.</li>
     *   <li>Then return size is two.</li>
     * </ul>
     * <p>
     * Method under test: {@link DeviceManagerImpl#extractUuids(Map)}
     */
    @Test
    public void testExtractUuids_givenFortyTwo_thenReturnSizeIsTwo() {
        // Arrange
        HashMap<Object, UpdateNotification> map = new HashMap<>();
        UUID uuidRef = UUID.randomUUID();
        map.put(42, new UpdateNotification(uuidRef));
        UUID uuidRef2 = UUID.randomUUID();
        map.put("42", new UpdateNotification(uuidRef2));

        // Act
        Map<Object, UUID> actualExtractUuidsResult = DeviceManagerImpl.extractUuids(map);

        // Assert
        assertEquals(2, actualExtractUuidsResult.size());
        assertSame(uuidRef2, actualExtractUuidsResult.get("42"));
        assertSame(uuidRef, actualExtractUuidsResult.get(42));
    }

    /**
     * Test {@link DeviceManagerImpl#extractUuids(Map)}.
     * <ul>
     *   <li>Then return size is one.</li>
     * </ul>
     * <p>
     * Method under test: {@link DeviceManagerImpl#extractUuids(Map)}
     */
    @Test
    public void testExtractUuids_thenReturnSizeIsOne() {
        // Arrange
        HashMap<Object, UpdateNotification> map = new HashMap<>();
        UUID uuidRef = UUID.randomUUID();
        map.put("42", new UpdateNotification(uuidRef));

        // Act
        Map<Object, UUID> actualExtractUuidsResult = DeviceManagerImpl.extractUuids(map);

        // Assert
        assertEquals(1, actualExtractUuidsResult.size());
        assertSame(uuidRef, actualExtractUuidsResult.get("42"));
    }

    /**
     * Test {@link DeviceManagerImpl#extractUuids(Map)}.
     * <ul>
     *   <li>When {@link HashMap#HashMap()}.</li>
     *   <li>Then return Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link DeviceManagerImpl#extractUuids(Map)}
     */
    @Test
    public void testExtractUuids_whenHashMap_thenReturnEmpty() {
        // Arrange and Act
        Map<Object, UUID> actualExtractUuidsResult = DeviceManagerImpl.extractUuids(new HashMap<>());

        // Assert
        assertTrue(actualExtractUuidsResult.isEmpty());
    }
}
