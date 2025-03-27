package com.linbit.linstor.core.apicallhandler;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.DecryptionHelper;
import com.linbit.linstor.api.pojo.RscPojo;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.NodesMap;
import com.linbit.linstor.core.CoreModule.ResourceDefinitionMap;
import com.linbit.linstor.core.CoreModule.ResourceGroupMap;
import com.linbit.linstor.core.CoreModule.StorPoolDefinitionMap;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NetInterfaceFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.ResourceConnectionSatelliteFactory;
import com.linbit.linstor.core.objects.ResourceDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.ResourceGroupSatelliteFactory;
import com.linbit.linstor.core.objects.ResourceSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.core.objects.VolumeDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.VolumeFactory;
import com.linbit.linstor.core.objects.VolumeGroupSatelliteFactory;
import com.linbit.linstor.core.objects.merger.StltLayerRscDataMerger;
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
import com.linbit.linstor.dbdrivers.SatelliteNiDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteResConDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteResDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteResDriver;
import com.linbit.linstor.dbdrivers.SatelliteRscGrpDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.dbdrivers.SatelliteVlmGrpDriver;
import com.linbit.linstor.dbdrivers.SatelliteVolDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteVolDriver;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.modularcrypto.JclCryptoProvider;
import com.linbit.linstor.numberpool.SatelliteDynamicNumberPool;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.storage.utils.LayerDataFactory;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class StltRscApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltRscApiCallHandler#applyDeletedRsc(String)}.
     * <p>
     * Method under test: {@link StltRscApiCallHandler#applyDeletedRsc(String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyDeletedRsc() {
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

        SatelliteVolDfnDriver driverRef2 = new SatelliteVolDfnDriver();
        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        VolumeDefinitionSatelliteFactory volumeDefinitionFactoryRef = new VolumeDefinitionSatelliteFactory(driverRef2,
                propsContainerFactoryRef3, transObjFactoryRef2, null, new StltSecurityObjects());

        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef5,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        SatelliteStorPoolDriver driverRef3 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef4 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef3 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(driverRef3, propsContainerFactoryRef4,
                transObjFactoryRef3, null, new FreeSpaceMgrSatelliteFactory(null));

        PropsContainerFactory propsContainerFactoryRef5 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef4 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef3,
                storPoolFactoryRef2, propsContainerFactoryRef5, transObjFactoryRef4, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        SatelliteNiDriver driverRef4 = new SatelliteNiDriver();
        NetInterfaceFactory netInterfaceFactoryRef = new NetInterfaceFactory(driverRef4, new TransactionObjectFactory(null),
                null);

        SatelliteResDriver dbDriverRef6 = new SatelliteResDriver();
        SatelliteSecObjProtDbDriver dbDriverRef7 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef7,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef6 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceSatelliteFactory resourceFactoryRef = new ResourceSatelliteFactory(dbDriverRef6,
                objectProtectionFactoryRef4, propsContainerFactoryRef6, new TransactionObjectFactory(null), null);

        SatelliteStorPoolDfnDriver dbDriverRef8 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef9 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef4 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef5 = new ObjectProtectionFactory(null, dbDriverRef9,
                objProtAclDbDriverRef4, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef7 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef8, objectProtectionFactoryRef5, propsContainerFactoryRef7, new TransactionObjectFactory(null), null,
                null);

        SatelliteStorPoolDriver driverRef5 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef8 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef5 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef3 = new StorPoolSatelliteFactory(driverRef5, propsContainerFactoryRef8,
                transObjFactoryRef5, null, new FreeSpaceMgrSatelliteFactory(null));

        SatelliteVolDriver driverRef6 = new SatelliteVolDriver();
        PropsContainerFactory propsContainerFactoryRef9 = new PropsContainerFactory(new SatellitePropDriver(), null);

        VolumeFactory volumeFactoryRef = new VolumeFactory(driverRef6, propsContainerFactoryRef9,
                new TransactionObjectFactory(null), null);

        StltSecurityObjects stltSecObjsRef = new StltSecurityObjects();
        SatelliteResConDfnDriver dbDriverRef10 = new SatelliteResConDfnDriver();
        PropsContainerFactory propsContainerFactoryRef10 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceConnectionSatelliteFactory resourceConnectionFactoryRef = new ResourceConnectionSatelliteFactory(
                dbDriverRef10, propsContainerFactoryRef10, new TransactionObjectFactory(null), null);

        FreeSpaceMgrSatelliteFactory freeSpaceMgrFactoryRef = new FreeSpaceMgrSatelliteFactory(null);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2, errorReporterRef5, null,
                nodeFactoryRef3, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        SatelliteRscGrpDriver rscGrpDriverRef = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef11 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef6 = new ObjectProtectionFactory(null, dbDriverRef11,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef11 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceGroupSatelliteFactory resourceGroupFactoryRef = new ResourceGroupSatelliteFactory(null, rscGrpDriverRef,
                vlmGrpDriverRef, objectProtectionFactoryRef6, propsContainerFactoryRef11, new TransactionObjectFactory(null),
                null, null);

        SatelliteRscGrpDriver rscGrpDriverRef2 = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef2 = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef12 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef7 = new ObjectProtectionFactory(null, dbDriverRef12,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef12 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StltRscGrpApiCallHelper rscGrpApiCallHelperRef = new StltRscGrpApiCallHelper(errorReporterRef4, null, null,
                controllerPeerConnectorRef2, null, resourceGroupFactoryRef,
                new VolumeGroupSatelliteFactory(null, rscGrpDriverRef2, vlmGrpDriverRef2, objectProtectionFactoryRef7,
                        propsContainerFactoryRef12, new TransactionObjectFactory(null), null, null),
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

        SatelliteStorPoolDfnDriver dbDriverRef13 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef14 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef8 = new ObjectProtectionFactory(null, dbDriverRef14,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef13 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef2 = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef13, objectProtectionFactoryRef8, propsContainerFactoryRef13, new TransactionObjectFactory(null),
                null, null);

        SatelliteStorPoolDriver driverRef7 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef14 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef6 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef4 = new StorPoolSatelliteFactory(driverRef7, propsContainerFactoryRef14,
                transObjFactoryRef6, null, new FreeSpaceMgrSatelliteFactory(null));

        StltLayerRscDataMerger layerRscDataMergerRef = new StltLayerRscDataMerger(null, layerDataFactoryRef, null,
                storPoolDefinitionFactoryRef2, storPoolFactoryRef4, new FreeSpaceMgrSatelliteFactory(null));

        StltSecurityObjects secObjsRef = new StltSecurityObjects();
        DecryptionHelper decryptionHelperRef = new DecryptionHelper(new JclCryptoProvider());
        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef6, null,
                nodeFactoryRef4, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();

        // Act
        (new StltRscApiCallHandler(errorReporterRef, null, null, controllerPeerConnectorRef, null, null, null, null,
                resourceDefinitionFactoryRef, volumeDefinitionFactoryRef, nodeFactoryRef2, netInterfaceFactoryRef,
                resourceFactoryRef, storPoolDefinitionFactoryRef, storPoolFactoryRef3, volumeFactoryRef, null, stltSecObjsRef,
                resourceConnectionFactoryRef, freeSpaceMgrFactoryRef, rscGrpApiCallHelperRef, layerRscDataMergerRef,
                new StltCryptApiCallHelper(null, null, null, secObjsRef, null, decryptionHelperRef, controllerPeerConnectorRef3,
                        errorReporterRef7, new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name")))))
                .applyDeletedRsc("Rsc Name Str");
    }

    /**
     * Test {@link StltRscApiCallHandler#applyChanges(RscPojo)}.
     * <p>
     * Method under test: {@link StltRscApiCallHandler#applyChanges(RscPojo)}
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

        SatelliteVolDfnDriver driverRef2 = new SatelliteVolDfnDriver();
        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        VolumeDefinitionSatelliteFactory volumeDefinitionFactoryRef = new VolumeDefinitionSatelliteFactory(driverRef2,
                propsContainerFactoryRef3, transObjFactoryRef2, null, new StltSecurityObjects());

        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef5,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        SatelliteStorPoolDriver driverRef3 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef4 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef3 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(driverRef3, propsContainerFactoryRef4,
                transObjFactoryRef3, null, new FreeSpaceMgrSatelliteFactory(null));

        PropsContainerFactory propsContainerFactoryRef5 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef4 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef3,
                storPoolFactoryRef2, propsContainerFactoryRef5, transObjFactoryRef4, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        SatelliteNiDriver driverRef4 = new SatelliteNiDriver();
        NetInterfaceFactory netInterfaceFactoryRef = new NetInterfaceFactory(driverRef4, new TransactionObjectFactory(null),
                null);

        SatelliteResDriver dbDriverRef6 = new SatelliteResDriver();
        SatelliteSecObjProtDbDriver dbDriverRef7 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef3 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef4 = new ObjectProtectionFactory(null, dbDriverRef7,
                objProtAclDbDriverRef3, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef6 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceSatelliteFactory resourceFactoryRef = new ResourceSatelliteFactory(dbDriverRef6,
                objectProtectionFactoryRef4, propsContainerFactoryRef6, new TransactionObjectFactory(null), null);

        SatelliteStorPoolDfnDriver dbDriverRef8 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef9 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef4 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef5 = new ObjectProtectionFactory(null, dbDriverRef9,
                objProtAclDbDriverRef4, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef7 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef8, objectProtectionFactoryRef5, propsContainerFactoryRef7, new TransactionObjectFactory(null), null,
                null);

        SatelliteStorPoolDriver driverRef5 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef8 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef5 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef3 = new StorPoolSatelliteFactory(driverRef5, propsContainerFactoryRef8,
                transObjFactoryRef5, null, new FreeSpaceMgrSatelliteFactory(null));

        SatelliteVolDriver driverRef6 = new SatelliteVolDriver();
        PropsContainerFactory propsContainerFactoryRef9 = new PropsContainerFactory(new SatellitePropDriver(), null);

        VolumeFactory volumeFactoryRef = new VolumeFactory(driverRef6, propsContainerFactoryRef9,
                new TransactionObjectFactory(null), null);

        StltSecurityObjects stltSecObjsRef = new StltSecurityObjects();
        SatelliteResConDfnDriver dbDriverRef10 = new SatelliteResConDfnDriver();
        PropsContainerFactory propsContainerFactoryRef10 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceConnectionSatelliteFactory resourceConnectionFactoryRef = new ResourceConnectionSatelliteFactory(
                dbDriverRef10, propsContainerFactoryRef10, new TransactionObjectFactory(null), null);

        FreeSpaceMgrSatelliteFactory freeSpaceMgrFactoryRef = new FreeSpaceMgrSatelliteFactory(null);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef2 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef3 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef2 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef2 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef2, storPoolDfnMapLockRef2, errorReporterRef5, null,
                nodeFactoryRef3, null, commonSerializerRef2, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        SatelliteRscGrpDriver rscGrpDriverRef = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef11 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef6 = new ObjectProtectionFactory(null, dbDriverRef11,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef11 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceGroupSatelliteFactory resourceGroupFactoryRef = new ResourceGroupSatelliteFactory(null, rscGrpDriverRef,
                vlmGrpDriverRef, objectProtectionFactoryRef6, propsContainerFactoryRef11, new TransactionObjectFactory(null),
                null, null);

        SatelliteRscGrpDriver rscGrpDriverRef2 = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef2 = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef12 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef7 = new ObjectProtectionFactory(null, dbDriverRef12,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef12 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StltRscGrpApiCallHelper rscGrpApiCallHelperRef = new StltRscGrpApiCallHelper(errorReporterRef4, null, null,
                controllerPeerConnectorRef2, null, resourceGroupFactoryRef,
                new VolumeGroupSatelliteFactory(null, rscGrpDriverRef2, vlmGrpDriverRef2, objectProtectionFactoryRef7,
                        propsContainerFactoryRef12, new TransactionObjectFactory(null), null, null),
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

        SatelliteStorPoolDfnDriver dbDriverRef13 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef14 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef8 = new ObjectProtectionFactory(null, dbDriverRef14,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        PropsContainerFactory propsContainerFactoryRef13 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef2 = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef13, objectProtectionFactoryRef8, propsContainerFactoryRef13, new TransactionObjectFactory(null),
                null, null);

        SatelliteStorPoolDriver driverRef7 = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef14 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef6 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef4 = new StorPoolSatelliteFactory(driverRef7, propsContainerFactoryRef14,
                transObjFactoryRef6, null, new FreeSpaceMgrSatelliteFactory(null));

        StltLayerRscDataMerger layerRscDataMergerRef = new StltLayerRscDataMerger(null, layerDataFactoryRef, null,
                storPoolDefinitionFactoryRef2, storPoolFactoryRef4, new FreeSpaceMgrSatelliteFactory(null));

        StltSecurityObjects secObjsRef = new StltSecurityObjects();
        DecryptionHelper decryptionHelperRef = new DecryptionHelper(new JclCryptoProvider());
        ReentrantReadWriteLock reconfigurationLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef3 = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef6 = new StderrErrorReporter("Module Name");
        NodeSatelliteFactory nodeFactoryRef4 = new NodeSatelliteFactory(new SatelliteNodeDriver(), null, null, null, null,
                null, null, null);

        ProtoCommonSerializer commonSerializerRef3 = new ProtoCommonSerializer(null, null);

        ControllerPeerConnectorImpl controllerPeerConnectorRef3 = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef3, nodesMapLockRef3, rscDfnMapLockRef3, storPoolDfnMapLockRef3, errorReporterRef6, null,
                nodeFactoryRef4, null, commonSerializerRef3, null, null,
                new StltExtToolsChecker(null, null, null, new StltConfig(), null));

        StderrErrorReporter errorReporterRef7 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        StltRscApiCallHandler stltRscApiCallHandler = new StltRscApiCallHandler(errorReporterRef, null, null,
                controllerPeerConnectorRef, null, null, null, null, resourceDefinitionFactoryRef, volumeDefinitionFactoryRef,
                nodeFactoryRef2, netInterfaceFactoryRef, resourceFactoryRef, storPoolDefinitionFactoryRef, storPoolFactoryRef3,
                volumeFactoryRef, null, stltSecObjsRef, resourceConnectionFactoryRef, freeSpaceMgrFactoryRef,
                rscGrpApiCallHelperRef, layerRscDataMergerRef,
                new StltCryptApiCallHelper(null, null, null, secObjsRef, null, decryptionHelperRef, controllerPeerConnectorRef3,
                        errorReporterRef7, new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"))));

        // Act
        stltRscApiCallHandler.applyChanges(new RscPojo("Rsc Name Ref", "Node Name Ref", 1L, new HashMap<>()));
    }

    /**
     * Test {@link StltRscApiCallHandler#StltRscApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, NodesMap, ResourceGroupMap, ResourceDefinitionMap, StorPoolDefinitionMap, ResourceDefinitionSatelliteFactory, VolumeDefinitionSatelliteFactory, NodeSatelliteFactory, NetInterfaceFactory, ResourceSatelliteFactory, StorPoolDefinitionSatelliteFactory, StorPoolSatelliteFactory, VolumeFactory, Provider, StltSecurityObjects, ResourceConnectionSatelliteFactory, FreeSpaceMgrSatelliteFactory, StltRscGrpApiCallHelper, StltLayerRscDataMerger, StltCryptApiCallHelper)}.
     * <p>
     * Method under test: {@link StltRscApiCallHandler#StltRscApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, NodesMap, ResourceGroupMap, ResourceDefinitionMap, StorPoolDefinitionMap, ResourceDefinitionSatelliteFactory, VolumeDefinitionSatelliteFactory, NodeSatelliteFactory, NetInterfaceFactory, ResourceSatelliteFactory, StorPoolDefinitionSatelliteFactory, StorPoolSatelliteFactory, VolumeFactory, Provider, StltSecurityObjects, ResourceConnectionSatelliteFactory, FreeSpaceMgrSatelliteFactory, StltRscGrpApiCallHelper, StltLayerRscDataMerger, StltCryptApiCallHelper)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewStltRscApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltRscApiCallHandler.<init>(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, NodesMap, ResourceGroupMap, ResourceDefinitionMap, StorPoolDefinitionMap, ResourceDefinitionSatelliteFactory, VolumeDefinitionSatelliteFactory, NodeSatelliteFactory, NetInterfaceFactory, ResourceSatelliteFactory, StorPoolDefinitionSatelliteFactory, StorPoolSatelliteFactory, VolumeFactory, Provider, StltSecurityObjects, ResourceConnectionSatelliteFactory, FreeSpaceMgrSatelliteFactory, StltRscGrpApiCallHelper, StltLayerRscDataMerger, StltCryptApiCallHelper).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ErrorReporter errorReporterRef = null;
        AccessContext apiCtxRef = null;
        DeviceManager deviceManagerRef = null;
        ControllerPeerConnector controllerPeerConnectorRef = null;
        NodesMap nodesMapRef = null;
        ResourceGroupMap rscGrpMapRef = null;
        ResourceDefinitionMap rscDfnMapRef = null;
        StorPoolDefinitionMap storPoolDfnMapRef = null;
        ResourceDefinitionSatelliteFactory resourceDefinitionFactoryRef = null;
        VolumeDefinitionSatelliteFactory volumeDefinitionFactoryRef = null;
        NodeSatelliteFactory nodeFactoryRef = null;
        NetInterfaceFactory netInterfaceFactoryRef = null;
        ResourceSatelliteFactory resourceFactoryRef = null;
        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = null;
        StorPoolSatelliteFactory storPoolFactoryRef = null;
        VolumeFactory volumeFactoryRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;
        StltSecurityObjects stltSecObjsRef = null;
        ResourceConnectionSatelliteFactory resourceConnectionFactoryRef = null;
        FreeSpaceMgrSatelliteFactory freeSpaceMgrFactoryRef = null;
        StltRscGrpApiCallHelper rscGrpApiCallHelperRef = null;
        StltLayerRscDataMerger layerRscDataMergerRef = null;
        StltCryptApiCallHelper cryptHelperRef = null;

        // Act
        StltRscApiCallHandler actualStltRscApiCallHandler = new StltRscApiCallHandler(errorReporterRef, apiCtxRef,
                deviceManagerRef, controllerPeerConnectorRef, nodesMapRef, rscGrpMapRef, rscDfnMapRef, storPoolDfnMapRef,
                resourceDefinitionFactoryRef, volumeDefinitionFactoryRef, nodeFactoryRef, netInterfaceFactoryRef,
                resourceFactoryRef, storPoolDefinitionFactoryRef, storPoolFactoryRef, volumeFactoryRef, transMgrProviderRef,
                stltSecObjsRef, resourceConnectionFactoryRef, freeSpaceMgrFactoryRef, rscGrpApiCallHelperRef,
                layerRscDataMergerRef, cryptHelperRef);

        // Assert
        // TODO: Add assertions on result
    }
}
