package com.linbit.linstor.api.protobuf;

import com.linbit.PlatformStlt;
import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.extproc.ExtCmdFactoryStlt;
import com.linbit.linstor.api.prop.WhitelistProps;
import com.linbit.linstor.api.prop.WhitelistPropsReconfigurator;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.backupshipping.BackupShippingL2LService;
import com.linbit.linstor.backupshipping.BackupShippingMgr;
import com.linbit.linstor.backupshipping.BackupShippingS3Service;
import com.linbit.linstor.core.ApplicationLifecycleManager;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.core.StltExternalFileHandler;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.UpdateMonitorImpl;
import com.linbit.linstor.core.apicallhandler.StltApiCallHandler;
import com.linbit.linstor.core.apicallhandler.StltApiCallHandlerUtils;
import com.linbit.linstor.core.apicallhandler.StltExtToolsChecker;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.migration.StltMigrationHandler;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.event.EventBroker;
import com.linbit.linstor.event.WatchStoreImpl;
import com.linbit.linstor.event.common.ConnectionStateEvent;
import com.linbit.linstor.event.common.DonePercentageEvent;
import com.linbit.linstor.event.common.ReplicationStateEvent;
import com.linbit.linstor.event.common.ResourceStateEvent;
import com.linbit.linstor.event.common.VolumeDiskStateEvent;
import com.linbit.linstor.event.serializer.EventSerializer;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventPublisher;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.layer.storage.DeviceProviderMapper;
import com.linbit.linstor.layer.storage.diskless.DisklessProvider;
import com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider;
import com.linbit.linstor.layer.storage.ebs.EbsTargetProvider;
import com.linbit.linstor.layer.storage.exos.ExosProvider;
import com.linbit.linstor.layer.storage.file.FileProvider;
import com.linbit.linstor.layer.storage.file.FileThinProvider;
import com.linbit.linstor.layer.storage.lvm.LvmProvider;
import com.linbit.linstor.layer.storage.lvm.LvmThinProvider;
import com.linbit.linstor.layer.storage.spdk.SpdkLocalProvider;
import com.linbit.linstor.layer.storage.spdk.SpdkRemoteProvider;
import com.linbit.linstor.layer.storage.storagespaces.StorageSpacesProvider;
import com.linbit.linstor.layer.storage.storagespaces.StorageSpacesThinProvider;
import com.linbit.linstor.layer.storage.zfs.ZfsProvider;
import com.linbit.linstor.layer.storage.zfs.ZfsThinProvider;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.ShutdownProtHolder;
import com.linbit.linstor.storage.StorageException;
import com.linbit.linstor.timer.CoreTimerImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ApplyDeletedStorPoolDiffblueTest {
    /**
     * Test {@link ApplyDeletedStorPool#execute(InputStream)}.
     * <p>
     * Method under test: {@link ApplyDeletedStorPool#execute(InputStream)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testExecute() throws StorageException, IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StltConfig stltCfgRef = new StltConfig();
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

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        UpdateMonitorImpl updateMonitorRef = new UpdateMonitorImpl(reconfigurationLockRef2, nodesMapLockRef2,
                rscDfnMapLockRef2, new ReentrantReadWriteLock(true));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        ShutdownProtHolder shutdownProtHolderRef = new ShutdownProtHolder();
        ApplicationLifecycleManager applicationLifecycleManagerRef = new ApplicationLifecycleManager(null,
                errorReporterRef3, shutdownProtHolderRef, new ReentrantReadWriteLock(true));

        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef2 = new StltConfig();
        StltExtToolsChecker stltExtToolsCheckerRef = new StltExtToolsChecker(errorReporterRef4, drbdVersionCheckRef,
                extCmdFactoryRef, stltCfgRef2, new DrbdEventService(null, new DrbdStateTracker(), null, null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        ReadOnlyPropsImpl satellitePropsRef = ReadOnlyPropsImpl.emptyRoProps();
        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        WatchStoreImpl watchStoreRef = new WatchStoreImpl();
        HashMap<String, EventSerializer> eventSerializersRef = new HashMap<>();
        EventBroker eventBrokerRef = new EventBroker(errorReporterRef5, commonSerializerRef2, watchStoreRef,
                eventSerializersRef, new HashMap<>());

        CoreTimerImpl timerRef = new CoreTimerImpl();
        StderrErrorReporter errLogRef = new StderrErrorReporter("Module Name");
        WhitelistProps whitelistPropsRef = new WhitelistProps(null);
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        WhitelistPropsReconfigurator whiteListPropsReconfiguratorRef = new WhitelistPropsReconfigurator(timerRef, errLogRef,
                whitelistPropsRef, reconfigurationLockRef4, new DrbdVersion(new CoreTimerImpl(), null));

        DrbdStateTracker drbdStateTrackerRef = new DrbdStateTracker();
        DrbdEventService drbdEventServiceRef = new DrbdEventService(null, new DrbdStateTracker(), null, null);

        ResourceStateEvent resourceStateEventRef = new ResourceStateEvent(null);
        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(null);
        ReplicationStateEvent replicationStateEventRef = new ReplicationStateEvent(null);
        DonePercentageEvent donePercentageEventRef = new DonePercentageEvent(null);
        DrbdEventPublisher drbdEventPublisherRef = new DrbdEventPublisher(drbdEventServiceRef, resourceStateEventRef,
                volumeDiskStateEventRef, replicationStateEventRef, donePercentageEventRef, new ConnectionStateEvent(null));

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
        DeviceProviderMapper deviceProviderMapperRef = new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef,
                zfsProviderRef, zfsThinProviderRef, disklessProviderRef, fileProviderRef, fileThinProviderRef,
                spdkLocalProviderRef, spdkRemoteProviderRef, exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef,
                storageSpacesProviderRef, new StorageSpacesThinProvider(null));

        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(null, null, null, null, null, null,
                new StltSecurityObjects(), null, null, null, null);

        BackupShippingMgr backupShippingMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(null, null, null, null, null, new StltSecurityObjects(), null, null, null, null));

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(null, null, null, null,
                new StltConfig());

        StltApiCallHandlerUtils stltApiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef6, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null, new DeviceProviderMapper(null, null,
                null, null, new DisklessProvider(), null, null, null, null, null, null, null, null, null),
                null);

        ExtCmdFactoryStlt extCmdFactoryStltRef = new ExtCmdFactoryStlt(new CoreTimerImpl(), null, null);

        PlatformStlt platformStltRef = new PlatformStlt(extCmdFactoryStltRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef4, rscDfnMapLockRef4, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ApplyDeletedStorPool applyDeletedStorPool = new ApplyDeletedStorPool(new StltApiCallHandler(errorReporterRef, null,
                stltCfgRef, controllerPeerConnectorRef, updateMonitorRef, null, applicationLifecycleManagerRef, null, null,
                null, null, null, null, null, stltExtToolsCheckerRef, interComSerializerRef, reconfigurationLockRef3,
                nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef2, extFileMapLockRef, remoteMapLockRef,
                satellitePropsRef, null, null, null, null, stltSecObjRef, null, eventBrokerRef, whiteListPropsReconfiguratorRef,
                null, drbdStateTrackerRef, drbdEventPublisherRef, deviceProviderMapperRef, backupShippingMgrRef,
                stltApiCallHandlerUtilsRef, platformStltRef, new StltMigrationHandler(null, errorReporterRef7,
                controllerPeerConnectorRef2, extCmdFactoryRef2, new HashMap<>())));

        // Act
        applyDeletedStorPool.execute(new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Test {@link ApplyDeletedStorPool#ApplyDeletedStorPool(StltApiCallHandler)}.
     * <p>
     * Method under test: {@link ApplyDeletedStorPool#ApplyDeletedStorPool(StltApiCallHandler)}
     */
    @Test
    public void testNewApplyDeletedStorPool() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.api.protobuf.ApplyDeletedStorPool.<init>(StltApiCallHandler).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        StltApiCallHandler apiCallHandlerRef = null;

        // Act
        ApplyDeletedStorPool actualApplyDeletedStorPool = new ApplyDeletedStorPool(apiCallHandlerRef);

        // Assert
        // TODO: Add assertions on result
        assertNotNull(actualApplyDeletedStorPool);
    }
}
