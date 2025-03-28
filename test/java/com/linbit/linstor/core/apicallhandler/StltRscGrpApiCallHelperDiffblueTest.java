package com.linbit.linstor.core.apicallhandler;

import com.linbit.InvalidNameException;
import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.pojo.AutoSelectFilterPojo;
import com.linbit.linstor.api.pojo.RscGrpPojo;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.ResourceGroupMap;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.apis.ResourceGroupApi;
import com.linbit.linstor.core.apis.VolumeGroupApi;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.ResourceGroupSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.core.objects.VolumeGroupSatelliteFactory;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteRscGrpDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.dbdrivers.SatelliteVlmGrpDriver;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.storage.kinds.DeviceLayerKind;
import com.linbit.linstor.storage.kinds.DeviceProviderKind;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StltRscGrpApiCallHelperDiffblueTest {
    /**
     * Test {@link StltRscGrpApiCallHelper#mergeResourceGroup(ResourceGroupApi)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltRscGrpApiCallHelper#mergeResourceGroup(ResourceGroupApi)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testMergeResourceGroup_givenReentrantReadWriteLockWithTrue()
            throws InvalidNameException, DatabaseException, AccessDeniedException {
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

        SatelliteRscGrpDriver rscGrpDriverRef = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef3,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        ResourceGroupSatelliteFactory resourceGroupFactoryRef = new ResourceGroupSatelliteFactory(null, rscGrpDriverRef,
                vlmGrpDriverRef, objectProtectionFactoryRef2, propsContainerFactoryRef2, new TransactionObjectFactory(null),
                null, null);

        SatelliteRscGrpDriver rscGrpDriverRef2 = new SatelliteRscGrpDriver();
        SatelliteVlmGrpDriver vlmGrpDriverRef2 = new SatelliteVlmGrpDriver();
        SatelliteSecObjProtDbDriver dbDriverRef4 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef2 = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef3 = new ObjectProtectionFactory(null, dbDriverRef4,
                objProtAclDbDriverRef2, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StltRscGrpApiCallHelper stltRscGrpApiCallHelper = new StltRscGrpApiCallHelper(errorReporterRef, null, null,
                controllerPeerConnectorRef, null, resourceGroupFactoryRef,
                new VolumeGroupSatelliteFactory(null, rscGrpDriverRef2, vlmGrpDriverRef2, objectProtectionFactoryRef3,
                        propsContainerFactoryRef3, new TransactionObjectFactory(null), null, null),
                null);
        UUID uuidRef = UUID.randomUUID();
        HashMap<String, String> rscDfnPropsRef = new HashMap<>();
        ArrayList<VolumeGroupApi> vlmGrpListRef = new ArrayList<>();
        ArrayList<String> nodeNameListRef = new ArrayList<>();
        ArrayList<String> storPoolNameListRef = new ArrayList<>();
        ArrayList<String> storPoolDisklessNameListRef = new ArrayList<>();
        ArrayList<String> doNotPlaceWithRscListRef = new ArrayList<>();
        ArrayList<String> replicasOnSameListRef = new ArrayList<>();
        ArrayList<String> replicasOnDifferentListRef = new ArrayList<>();
        HashMap<String, Integer> xReplicasOnDifferentMapRef = new HashMap<>();
        ArrayList<DeviceLayerKind> layerStackListRef = new ArrayList<>();
        ArrayList<DeviceProviderKind> deviceProviderKindsRef = new ArrayList<>();
        ArrayList<String> skipAlreadyPlacedOnNodeNamesCheckRef = new ArrayList<>();

        // Act
        stltRscGrpApiCallHelper.mergeResourceGroup(
                new RscGrpPojo(uuidRef, "Rsc Grp Name Str Ref", "Description Ref", rscDfnPropsRef, vlmGrpListRef,
                        new AutoSelectFilterPojo(3, 3, nodeNameListRef, storPoolNameListRef, storPoolDisklessNameListRef,
                                doNotPlaceWithRscListRef, ".*", replicasOnSameListRef, replicasOnDifferentListRef,
                                xReplicasOnDifferentMapRef, layerStackListRef, deviceProviderKindsRef, true,
                                skipAlreadyPlacedOnNodeNamesCheckRef, true, "Diskless Type Ref", new HashMap<>()),
                        (short) 1));
    }

    /**
     * Test {@link StltRscGrpApiCallHelper#StltRscGrpApiCallHelper(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, ResourceGroupMap, ResourceGroupSatelliteFactory, VolumeGroupSatelliteFactory, Provider)}.
     * <p>
     * Method under test: {@link StltRscGrpApiCallHelper#StltRscGrpApiCallHelper(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, ResourceGroupMap, ResourceGroupSatelliteFactory, VolumeGroupSatelliteFactory, Provider)}
     */
    @Test
    public void testNewStltRscGrpApiCallHelper() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltRscGrpApiCallHelper.<init>(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, ResourceGroupMap, ResourceGroupSatelliteFactory, VolumeGroupSatelliteFactory, Provider).
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
        ResourceGroupMap rscGrpMapRef = null;
        ResourceGroupSatelliteFactory resourceGroupFactoryRef = null;
        VolumeGroupSatelliteFactory volumeGroupFactoryRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;

        // Act
        StltRscGrpApiCallHelper actualStltRscGrpApiCallHelper = new StltRscGrpApiCallHelper(errorReporterRef, apiCtxRef,
                deviceManagerRef, controllerPeerConnectorRef, rscGrpMapRef, resourceGroupFactoryRef, volumeGroupFactoryRef,
                transMgrProviderRef);

        // Assert
        // TODO: Add assertions on result

        assertNotNull(actualStltRscGrpApiCallHelper);
    }
}
