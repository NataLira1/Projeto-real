package com.linbit.linstor.core.apicallhandler;

import com.linbit.InvalidNameException;
import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.BackupToS3;
import com.linbit.linstor.api.DecryptionHelper;
import com.linbit.linstor.api.interfaces.RscLayerDataApi;
import com.linbit.linstor.api.pojo.BCacheRscPojo;
import com.linbit.linstor.api.pojo.SnapshotDfnListItemPojo;
import com.linbit.linstor.api.pojo.SnapshotPojo;
import com.linbit.linstor.api.pojo.SnapshotShippingListItemPojo;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.backupshipping.BackupShippingL2LService;
import com.linbit.linstor.backupshipping.BackupShippingMgr;
import com.linbit.linstor.backupshipping.BackupShippingS3Service;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.ResourceDefinitionMap;
import com.linbit.linstor.core.CoreModule.ResourceGroupMap;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.StltConfigAccessor;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.apis.SnapshotVolumeApi;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.identifier.NodeName;
import com.linbit.linstor.core.identifier.SnapshotName;
import com.linbit.linstor.core.identifier.VolumeNumber;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.ResourceDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.ResourceGroupSatelliteFactory;
import com.linbit.linstor.core.objects.Snapshot;
import com.linbit.linstor.core.objects.SnapshotDefinition;
import com.linbit.linstor.core.objects.SnapshotDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.SnapshotSatelliteFactory;
import com.linbit.linstor.core.objects.SnapshotVolumeDefinition;
import com.linbit.linstor.core.objects.SnapshotVolumeDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.SnapshotVolumeSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.core.objects.VolumeGroupSatelliteFactory;
import com.linbit.linstor.core.objects.merger.StltLayerSnapDataMerger;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerCacheRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerCacheVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerDrbdRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerDrbdRscDfnDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerDrbdVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerDrbdVlmDfnDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerLuksRscDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerLuksVlmDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerNvmeRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerResourceIdDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerStorageRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerStorageVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerWritecacheRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerWritecacheVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteResDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteRscGrpDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotVlmDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteSnapshotVlmDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.dbdrivers.SatelliteVlmGrpDriver;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.modularcrypto.JclCryptoProvider;
import com.linbit.linstor.numberpool.SatelliteDynamicNumberPool;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.snapshotshipping.SnapshotShippingService;
import com.linbit.linstor.storage.utils.LayerDataFactory;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;
import com.linbit.locks.LockGuardFactory;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class StltSnapshotApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltSnapshotApiCallHandler#applyChanges(SnapshotPojo)}.
     * <p>
     * Method under test: {@link StltSnapshotApiCallHandler#applyChanges(SnapshotPojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyChanges() {
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

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        SatelliteResDfnDriver driverRef = new SatelliteResDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceDefinitionSatelliteFactory resourceDefinitionFactoryRef = new ResourceDefinitionSatelliteFactory(driverRef,
                objectProtectionFactoryRef2, propsContainerFactoryRef2, new TransactionObjectFactory(null), null, null);

        SatelliteSnapshotDfnDriver driverRef2 = new SatelliteSnapshotDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef4 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef4,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotDefinitionSatelliteFactory snapshotDefinitionFactoryRef = new SnapshotDefinitionSatelliteFactory(driverRef2,
                objectProtectionFactoryRef3, propsContainerFactoryRef3, new TransactionObjectFactory(null), null);

        SatelliteSnapshotVlmDfnDriver driverRef3 = new SatelliteSnapshotVlmDfnDriver();
        PropsContainerFactory propsContainerFactoryRef4 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotVolumeDefinitionSatelliteFactory snapshotVolumeDefinitionFactoryRef = new SnapshotVolumeDefinitionSatelliteFactory(
                driverRef3, propsContainerFactoryRef4, new TransactionObjectFactory(null), null);

        SatelliteSnapshotDriver driverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotSatelliteFactory snapshotFactoryRef = new SnapshotSatelliteFactory(driverRef4, propsConFactoryRef,
                new TransactionObjectFactory(null), null);

        PropsContainerFactory propsContainerFactoryRef5 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SatelliteSnapshotVlmDriver driverRef5 = new SatelliteSnapshotVlmDriver();
        SnapshotVolumeSatelliteFactory snapshotVolumeFactoryRef = new SnapshotVolumeSatelliteFactory(
                propsContainerFactoryRef5, driverRef5, new TransactionObjectFactory(null), null);

        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2, errorReporterRef5, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        SatelliteRscGrpDriver rscGrpDriverRef = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef6 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceGroupSatelliteFactory resourceGroupFactoryRef = new ResourceGroupSatelliteFactory(null, rscGrpDriverRef,
                vlmGrpDriverRef, objectProtectionFactoryRef4, propsContainerFactoryRef6, new TransactionObjectFactory(null),
                null, null);

        SatelliteRscGrpDriver rscGrpDriverRef2 = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef2 = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef6 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef5 = new ObjectProtectionFactory(null, dbDriverRef6,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef7 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StltRscGrpApiCallHelper stltGrpApiCallHelperRef = new StltRscGrpApiCallHelper(errorReporterRef4, null, null,
                controllerPeerConnectorRef2, null, resourceGroupFactoryRef,
                new VolumeGroupSatelliteFactory(null, rscGrpDriverRef2, vlmGrpDriverRef2, objectProtectionFactoryRef5,
                        propsContainerFactoryRef7, new TransactionObjectFactory(null), null, null),
                null);

        SatelliteLayerResourceIdDriver layerRscIdDatabaseDriverRef = new SatelliteLayerResourceIdDriver();
        SatelliteLayerLuksRscDriver layerLuksRscDbDriverRef = new SatelliteLayerLuksRscDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerLuksVlmDriver layerLuksVlmDbDriverRef = new SatelliteLayerLuksVlmDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerDrbdRscDfnDbDriver layerDrbdRscDfnDbDriverRef = new SatelliteLayerDrbdRscDfnDbDriver();
        SatelliteLayerDrbdVlmDfnDbDriver layerDrbdVlmDfnDbDriverRef = new SatelliteLayerDrbdVlmDfnDbDriver();
        SatelliteLayerDrbdRscDbDriver layerDrbdRscDbDriverRef = new SatelliteLayerDrbdRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerDrbdVlmDbDriver layerDrbdVlmDbDriverRef = new SatelliteLayerDrbdVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageRscDbDriver layerStorRscDbDriverRef = new SatelliteLayerStorageRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageVlmDbDriver layerStorVlmDbDriverRef = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerNvmeRscDbDriver layerNvmeRscDbDriverRef = new SatelliteLayerNvmeRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerWritecacheRscDbDriver layerWritecacheRscDbDriverRef = new SatelliteLayerWritecacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerWritecacheVlmDbDriver layerWritecacheVlmDbDriverRef = new SatelliteLayerWritecacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerCacheRscDbDriver layerCacheRscDbDriverRef = new SatelliteLayerCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerCacheVlmDbDriver layerCacheVlmDbDriverRef = new SatelliteLayerCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheRscDbDriver layerBCacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver layerBCacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteDynamicNumberPool tcpPortPoolRef = new SatelliteDynamicNumberPool();
        SatelliteDynamicNumberPool minorPoolRef = new SatelliteDynamicNumberPool();
        LayerDataFactory layerDataFactoryRef = new LayerDataFactory(layerRscIdDatabaseDriverRef, layerLuksRscDbDriverRef,
                layerLuksVlmDbDriverRef, layerDrbdRscDfnDbDriverRef, layerDrbdVlmDfnDbDriverRef, layerDrbdRscDbDriverRef,
                layerDrbdVlmDbDriverRef, layerStorRscDbDriverRef, layerStorVlmDbDriverRef, layerNvmeRscDbDriverRef,
                layerWritecacheRscDbDriverRef, layerWritecacheVlmDbDriverRef, layerCacheRscDbDriverRef,
                layerCacheVlmDbDriverRef, layerBCacheRscDbDriverRef, layerBCacheVlmDbDriverRef, tcpPortPoolRef, minorPoolRef,
                null, new TransactionObjectFactory(null));

        SatelliteStorPoolDfnDriver dbDriverRef7 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef8 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef6 = new ObjectProtectionFactory(null, dbDriverRef8,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef8 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef7, objectProtectionFactoryRef6, propsContainerFactoryRef8, new TransactionObjectFactory(null), null,
                null);

        SatelliteStorPoolDriver driverRef6 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef9 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(driverRef6, propsContainerFactoryRef9,
                transObjFactoryRef2, null, new FreeSpaceMgrSatelliteFactory(null));

        StltLayerSnapDataMerger layerSnapDataMergerRef = new StltLayerSnapDataMerger(null, layerDataFactoryRef, null,
                storPoolDefinitionFactoryRef, storPoolFactoryRef2, new FreeSpaceMgrSatelliteFactory(null));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(null, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef6,
                extCmdFactoryRef2, controllerPeerConnectorRef3, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef3,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(null, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingMgr backupShippingMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef7, extCmdFactoryRef3, controllerPeerConnectorRef4,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock())));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef9, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        StltSnapshotApiCallHandler stltSnapshotApiCallHandler = new StltSnapshotApiCallHandler(errorReporterRef, null, null,
                null, null, controllerPeerConnectorRef, resourceDefinitionFactoryRef, snapshotDefinitionFactoryRef,
                snapshotVolumeDefinitionFactoryRef, snapshotFactoryRef, snapshotVolumeFactoryRef, stltGrpApiCallHelperRef,
                layerSnapDataMergerRef, null, backupShippingMgrRef,
                new SnapshotShippingService(null, extCmdFactoryRef4, errorReporterRef8, controllerPeerConnectorRef5,
                        new ProtoCtrlStltSerializer(errReporter, null, secObjsRef3, ReadOnlyPropsImpl.emptyRoProps())));
        ArrayList<String> nodeNamesRef = new ArrayList<>();
        SnapshotShippingListItemPojo snaphotDfnRef = new SnapshotShippingListItemPojo(
                new SnapshotDfnListItemPojo(null, nodeNamesRef, new ArrayList<>()), "Source Node Name Ref",
                "Target Node Name Ref", "Shipping Status Ref");

        UUID uuidRef = UUID.randomUUID();
        ArrayList<SnapshotVolumeApi> snapshotVlmsRef = new ArrayList<>();
        ArrayList<RscLayerDataApi> childrenRef = new ArrayList<>();
        BCacheRscPojo layerDataRef = new BCacheRscPojo(childrenRef, "Rsc Name Suffix Ref", new ArrayList<>());

        Date createTimestampRef = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        HashMap<String, String> snapPropsMapRef = new HashMap<>();

        // Act
        stltSnapshotApiCallHandler.applyChanges(new SnapshotPojo(snaphotDfnRef, uuidRef, 1L, true, true, 1L, 1L,
                snapshotVlmsRef, layerDataRef, "Node Name Ref", createTimestampRef, snapPropsMapRef, new HashMap<>()));
    }

    /**
     * Test {@link StltSnapshotApiCallHandler#applyEndedSnapshot(String, String)}.
     * <p>
     * Method under test: {@link StltSnapshotApiCallHandler#applyEndedSnapshot(String, String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyEndedSnapshot() {
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

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        SatelliteResDfnDriver driverRef = new SatelliteResDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceDefinitionSatelliteFactory resourceDefinitionFactoryRef = new ResourceDefinitionSatelliteFactory(driverRef,
                objectProtectionFactoryRef2, propsContainerFactoryRef2, new TransactionObjectFactory(null), null, null);

        SatelliteSnapshotDfnDriver driverRef2 = new SatelliteSnapshotDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef4 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef4,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotDefinitionSatelliteFactory snapshotDefinitionFactoryRef = new SnapshotDefinitionSatelliteFactory(driverRef2,
                objectProtectionFactoryRef3, propsContainerFactoryRef3, new TransactionObjectFactory(null), null);

        SatelliteSnapshotVlmDfnDriver driverRef3 = new SatelliteSnapshotVlmDfnDriver();
        PropsContainerFactory propsContainerFactoryRef4 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotVolumeDefinitionSatelliteFactory snapshotVolumeDefinitionFactoryRef = new SnapshotVolumeDefinitionSatelliteFactory(
                driverRef3, propsContainerFactoryRef4, new TransactionObjectFactory(null), null);

        SatelliteSnapshotDriver driverRef4 = new SatelliteSnapshotDriver();
        PropsContainerFactory propsConFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        SnapshotSatelliteFactory snapshotFactoryRef = new SnapshotSatelliteFactory(driverRef4, propsConFactoryRef,
                new TransactionObjectFactory(null), null);

        PropsContainerFactory propsContainerFactoryRef5 = new PropsContainerFactory(new SatellitePropDriver(), null);

        SatelliteSnapshotVlmDriver driverRef5 = new SatelliteSnapshotVlmDriver();
        SnapshotVolumeSatelliteFactory snapshotVolumeFactoryRef = new SnapshotVolumeSatelliteFactory(
                propsContainerFactoryRef5, driverRef5, new TransactionObjectFactory(null), null);

        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2, errorReporterRef5, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        SatelliteRscGrpDriver rscGrpDriverRef = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef6 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceGroupSatelliteFactory resourceGroupFactoryRef = new ResourceGroupSatelliteFactory(null, rscGrpDriverRef,
                vlmGrpDriverRef, objectProtectionFactoryRef4, propsContainerFactoryRef6, new TransactionObjectFactory(null),
                null, null);

        SatelliteRscGrpDriver rscGrpDriverRef2 = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef2 = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef6 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef5 = new ObjectProtectionFactory(null, dbDriverRef6,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef7 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StltRscGrpApiCallHelper stltGrpApiCallHelperRef = new StltRscGrpApiCallHelper(errorReporterRef4, null, null,
                controllerPeerConnectorRef2, null, resourceGroupFactoryRef,
                new VolumeGroupSatelliteFactory(null, rscGrpDriverRef2, vlmGrpDriverRef2, objectProtectionFactoryRef5,
                        propsContainerFactoryRef7, new TransactionObjectFactory(null), null, null),
                null);

        SatelliteLayerResourceIdDriver layerRscIdDatabaseDriverRef = new SatelliteLayerResourceIdDriver();
        SatelliteLayerLuksRscDriver layerLuksRscDbDriverRef = new SatelliteLayerLuksRscDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerLuksVlmDriver layerLuksVlmDbDriverRef = new SatelliteLayerLuksVlmDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerDrbdRscDfnDbDriver layerDrbdRscDfnDbDriverRef = new SatelliteLayerDrbdRscDfnDbDriver();
        SatelliteLayerDrbdVlmDfnDbDriver layerDrbdVlmDfnDbDriverRef = new SatelliteLayerDrbdVlmDfnDbDriver();
        SatelliteLayerDrbdRscDbDriver layerDrbdRscDbDriverRef = new SatelliteLayerDrbdRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerDrbdVlmDbDriver layerDrbdVlmDbDriverRef = new SatelliteLayerDrbdVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageRscDbDriver layerStorRscDbDriverRef = new SatelliteLayerStorageRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageVlmDbDriver layerStorVlmDbDriverRef = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerNvmeRscDbDriver layerNvmeRscDbDriverRef = new SatelliteLayerNvmeRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerWritecacheRscDbDriver layerWritecacheRscDbDriverRef = new SatelliteLayerWritecacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerWritecacheVlmDbDriver layerWritecacheVlmDbDriverRef = new SatelliteLayerWritecacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerCacheRscDbDriver layerCacheRscDbDriverRef = new SatelliteLayerCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerCacheVlmDbDriver layerCacheVlmDbDriverRef = new SatelliteLayerCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheRscDbDriver layerBCacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver layerBCacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteDynamicNumberPool tcpPortPoolRef = new SatelliteDynamicNumberPool();
        SatelliteDynamicNumberPool minorPoolRef = new SatelliteDynamicNumberPool();
        LayerDataFactory layerDataFactoryRef = new LayerDataFactory(layerRscIdDatabaseDriverRef, layerLuksRscDbDriverRef,
                layerLuksVlmDbDriverRef, layerDrbdRscDfnDbDriverRef, layerDrbdVlmDfnDbDriverRef, layerDrbdRscDbDriverRef,
                layerDrbdVlmDbDriverRef, layerStorRscDbDriverRef, layerStorVlmDbDriverRef, layerNvmeRscDbDriverRef,
                layerWritecacheRscDbDriverRef, layerWritecacheVlmDbDriverRef, layerCacheRscDbDriverRef,
                layerCacheVlmDbDriverRef, layerBCacheRscDbDriverRef, layerBCacheVlmDbDriverRef, tcpPortPoolRef, minorPoolRef,
                null, new TransactionObjectFactory(null));

        SatelliteStorPoolDfnDriver dbDriverRef7 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef8 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef6 = new ObjectProtectionFactory(null, dbDriverRef8,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef8 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef7, objectProtectionFactoryRef6, propsContainerFactoryRef8, new TransactionObjectFactory(null), null,
                null);

        SatelliteStorPoolDriver driverRef6 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef9 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(driverRef6, propsContainerFactoryRef9,
                transObjFactoryRef2, null, new FreeSpaceMgrSatelliteFactory(null));

        StltLayerSnapDataMerger layerSnapDataMergerRef = new StltLayerSnapDataMerger(null, layerDataFactoryRef, null,
                storPoolDefinitionFactoryRef, storPoolFactoryRef2, new FreeSpaceMgrSatelliteFactory(null));

        BackupToS3 backupHandlerRef = new BackupToS3(null, null, null);

        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(null, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock();
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef6,
                extCmdFactoryRef2, controllerPeerConnectorRef3, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef, null, null,
                new LockGuardFactory(reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef3,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock()));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(new CoreTimerImpl(), null);

        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock();
        ControllerPeerConnectorImpl controllerPeerConnectorRef4 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5, new ReentrantReadWriteLock(), null, null, null,
                null, null, null, null, null);

        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(null, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock nodesMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscDfnMapLockRef6 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock();
        BackupShippingMgr backupShippingMgrRef = new BackupShippingMgr(null, null, backupShippingS3Ref,
                new BackupShippingL2LService(errorReporterRef7, extCmdFactoryRef3, controllerPeerConnectorRef4,
                        interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef2, null, null,
                        new LockGuardFactory(reconfigurationLockRef6, nodesMapLockRef6, rscDfnMapLockRef6, storPoolDfnMapLockRef4,
                                ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2, remoteMapLockRef2,
                                new ReentrantReadWriteLock())));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef7 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef9 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef5 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef7, nodesMapLockRef7, rscDfnMapLockRef7, storPoolDfnMapLockRef5, errorReporterRef9, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();

        // Act
        (new StltSnapshotApiCallHandler(errorReporterRef, null, null, null, null, controllerPeerConnectorRef,
                resourceDefinitionFactoryRef, snapshotDefinitionFactoryRef, snapshotVolumeDefinitionFactoryRef,
                snapshotFactoryRef, snapshotVolumeFactoryRef, stltGrpApiCallHelperRef, layerSnapDataMergerRef, null,
                backupShippingMgrRef,
                new SnapshotShippingService(null, extCmdFactoryRef4, errorReporterRef8, controllerPeerConnectorRef5,
                        new ProtoCtrlStltSerializer(errReporter, null, secObjsRef3, ReadOnlyPropsImpl.emptyRoProps()))))
                .applyEndedSnapshot("Rsc Name Str", "Snapshot Name Str");
    }

    /**
     * Test {@link StltSnapshotApiCallHandler#deleteSnapshotsAndCleanup(ResourceDefinitionMap, ResourceGroupMap, SnapshotDefinition, AccessContext, ErrorReporter, SnapshotShippingService, BackupShippingMgr)}.
     * <p>
     * Method under test: {@link StltSnapshotApiCallHandler#deleteSnapshotsAndCleanup(ResourceDefinitionMap, ResourceGroupMap, SnapshotDefinition, AccessContext, ErrorReporter, SnapshotShippingService, BackupShippingMgr)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testDeleteSnapshotsAndCleanup() throws InvalidNameException, DatabaseException, AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.linbit.InvalidNameException: Invalid name: Cannot contain character ' '
        //       at com.linbit.Checks.nameCheck(Checks.java:157)
        //       at com.linbit.linstor.core.identifier.SnapshotName.<init>(SnapshotName.java:30)
        //       at com.linbit.linstor.core.identifier.SnapshotName.<init>(SnapshotName.java:24)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        UUID objIdRef = UUID.randomUUID();
        SnapshotName snapshotNameRef = new SnapshotName("Snapshot Name");
        SatelliteSnapshotDfnDriver dbDriverRef = new SatelliteSnapshotDfnDriver();
        TransactionObjectFactory transObjFactory = new TransactionObjectFactory(null);
        PropsContainerFactory propsContainerFactory = new PropsContainerFactory(new SatellitePropDriver(), null);

        HashMap<VolumeNumber, SnapshotVolumeDefinition> snapshotVlmDfnMapRef = new HashMap<>();
        HashMap<NodeName, Snapshot> snapshotMapRef = new HashMap<>();
        SnapshotDefinition snapDfn = new SnapshotDefinition(objIdRef, null, null, snapshotNameRef, 1L, dbDriverRef,
                transObjFactory, propsContainerFactory, null, snapshotVlmDfnMapRef, snapshotMapRef, new HashMap<>());

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef2 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef3,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef2, objectProtectionFactoryRef,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef3, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef4, drbdVersionCheckRef, extCmdFactoryRef2, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        SnapshotShippingService snapshipServiceRef = new SnapshotShippingService(null, extCmdFactoryRef, errorReporterRef2,
                controllerPeerConnectorRef,
                new ProtoCtrlStltSerializer(errReporter, null, secObjsRef, ReadOnlyPropsImpl.emptyRoProps()));

        StltConfigAccessor stltConfigAccessorRef = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        DecryptionHelper decHelperRef = new DecryptionHelper(new JclCryptoProvider());
        BackupToS3 backupHandlerRef = new BackupToS3(stltConfigAccessorRef, decHelperRef,
                new StderrErrorReporter("Module Name"));

        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef3 = new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2, errorReporterRef6, null,
                nodeFactoryRef2, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter2 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef2 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef = new ProtoCtrlStltSerializer(errReporter2, null, secObjsRef2,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef2 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef = new ReentrantReadWriteLock(true);
        BackupShippingS3Service backupShippingS3Ref = new BackupShippingS3Service(backupHandlerRef, errorReporterRef5,
                extCmdFactoryRef3, controllerPeerConnectorRef2, interComSerializerRef, null, stltSecObjRef,
                stltConfigAccessorRef2, null, null,
                new LockGuardFactory(reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3,
                        ctrlConfigLockRef, kvsMapLockRef, rscGrpMapLockRef, extFileMapLockRef, remoteMapLockRef,
                        new ReentrantReadWriteLock(true)));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef3 = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef4 = new ExtCmdFactory(timerRef3, new StderrErrorReporter("Module Name"));

        ReentrantReadWriteLock reconfigurationLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef4 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef8 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef4, nodesMapLockRef4, rscDfnMapLockRef4, storPoolDfnMapLockRef4, errorReporterRef8, null,
                nodeFactoryRef3, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errReporter3 = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef3 = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer interComSerializerRef2 = new ProtoCtrlStltSerializer(errReporter3, null, secObjsRef3,
                ReadOnlyPropsImpl.emptyRoProps());

        StltSecurityObjects stltSecObjRef2 = new StltSecurityObjects();
        StltConfigAccessor stltConfigAccessorRef3 = new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps());
        ReentrantReadWriteLock reconfigurationLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef5 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock ctrlConfigLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock kvsMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscGrpMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock extFileMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock remoteMapLockRef2 = new ReentrantReadWriteLock(true);

        // Act
        StltSnapshotApiCallHandler.deleteSnapshotsAndCleanup(null, null, snapDfn, null, errorReporterRef,
                snapshipServiceRef,
                new BackupShippingMgr(null, null, backupShippingS3Ref,
                        new BackupShippingL2LService(errorReporterRef7, extCmdFactoryRef4, controllerPeerConnectorRef3,
                                interComSerializerRef2, null, stltSecObjRef2, stltConfigAccessorRef3, null, null,
                                new LockGuardFactory(reconfigurationLockRef5, nodesMapLockRef5, rscDfnMapLockRef5,
                                        storPoolDfnMapLockRef5, ctrlConfigLockRef2, kvsMapLockRef2, rscGrpMapLockRef2, extFileMapLockRef2,
                                        remoteMapLockRef2, new ReentrantReadWriteLock(true)))));
    }

    /**
     * Test {@link StltSnapshotApiCallHandler#StltSnapshotApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ResourceGroupMap, ResourceDefinitionMap, ControllerPeerConnector, ResourceDefinitionSatelliteFactory, SnapshotDefinitionSatelliteFactory, SnapshotVolumeDefinitionSatelliteFactory, SnapshotSatelliteFactory, SnapshotVolumeSatelliteFactory, StltRscGrpApiCallHelper, StltLayerSnapDataMerger, Provider, BackupShippingMgr, SnapshotShippingService)}.
     * <p>
     * Method under test: {@link StltSnapshotApiCallHandler#StltSnapshotApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ResourceGroupMap, ResourceDefinitionMap, ControllerPeerConnector, ResourceDefinitionSatelliteFactory, SnapshotDefinitionSatelliteFactory, SnapshotVolumeDefinitionSatelliteFactory, SnapshotSatelliteFactory, SnapshotVolumeSatelliteFactory, StltRscGrpApiCallHelper, StltLayerSnapDataMerger, Provider, BackupShippingMgr, SnapshotShippingService)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewStltSnapshotApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltSnapshotApiCallHandler.<init>(ErrorReporter, AccessContext, DeviceManager, ResourceGroupMap, ResourceDefinitionMap, ControllerPeerConnector, ResourceDefinitionSatelliteFactory, SnapshotDefinitionSatelliteFactory, SnapshotVolumeDefinitionSatelliteFactory, SnapshotSatelliteFactory, SnapshotVolumeSatelliteFactory, StltRscGrpApiCallHelper, StltLayerSnapDataMerger, Provider, BackupShippingMgr, SnapshotShippingService).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ErrorReporter errorReporterRef = null;
        AccessContext apiCtxRef = null;
        DeviceManager deviceManagerRef = null;
        ResourceGroupMap rscGrpMapRef = null;
        ResourceDefinitionMap rscDfnMapRef = null;
        ControllerPeerConnector controllerPeerConnectorRef = null;
        ResourceDefinitionSatelliteFactory resourceDefinitionFactoryRef = null;
        SnapshotDefinitionSatelliteFactory snapshotDefinitionFactoryRef = null;
        SnapshotVolumeDefinitionSatelliteFactory snapshotVolumeDefinitionFactoryRef = null;
        SnapshotSatelliteFactory snapshotFactoryRef = null;
        SnapshotVolumeSatelliteFactory snapshotVolumeFactoryRef = null;
        StltRscGrpApiCallHelper stltGrpApiCallHelperRef = null;
        StltLayerSnapDataMerger layerSnapDataMergerRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;
        BackupShippingMgr backupShippingMgrRef = null;
        SnapshotShippingService snapShipServiceRef = null;

        // Act
        StltSnapshotApiCallHandler actualStltSnapshotApiCallHandler = new StltSnapshotApiCallHandler(errorReporterRef,
                apiCtxRef, deviceManagerRef, rscGrpMapRef, rscDfnMapRef, controllerPeerConnectorRef,
                resourceDefinitionFactoryRef, snapshotDefinitionFactoryRef, snapshotVolumeDefinitionFactoryRef,
                snapshotFactoryRef, snapshotVolumeFactoryRef, stltGrpApiCallHelperRef, layerSnapDataMergerRef,
                transMgrProviderRef, backupShippingMgrRef, snapShipServiceRef);

        // Assert
        // TODO: Add assertions on result
    }
}
