package com.linbit.linstor.core.apicallhandler;

import com.google.protobuf.UnknownFieldSet;
import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.ApiConsts;
import com.linbit.linstor.api.ApiConsts.ConnectionStatus;
import com.linbit.linstor.api.pojo.NodePojo;
import com.linbit.linstor.api.pojo.NodePojo.NodeConnPojo;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.NodesMap;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.StltConfigAccessor;
import com.linbit.linstor.core.apis.NetInterfaceApi;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NetInterfaceFactory;
import com.linbit.linstor.core.objects.NodeConnectionFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.dbdrivers.SatelliteNiDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeConDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.proto.apidata.NetInterfaceApiData;
import com.linbit.linstor.proto.common.NetInterfaceOuterClass;
import com.linbit.linstor.proto.common.NetInterfaceOuterClass.NetInterface;
import com.linbit.linstor.proto.common.NetInterfaceOuterClass.NetInterface.Builder;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class StltNodeApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltNodeApiCallHandler#applyDeletedNode(String)}.
     * <p>
     * Method under test: {@link StltNodeApiCallHandler#applyDeletedNode(String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyDeletedNode() {
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

        SatelliteNodeConDfnDriver dbDriverRef3 = new SatelliteNodeConDfnDriver();
        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        NodeConnectionFactory nodeConnectionFactoryRef = new NodeConnectionFactory(dbDriverRef3, propsContainerFactoryRef3,
                new TransactionObjectFactory(null), null);

        SatelliteNiDriver driverRef2 = new SatelliteNiDriver();
        NetInterfaceFactory netInterfaceFactoryRef = new NetInterfaceFactory(driverRef2, new TransactionObjectFactory(null),
                null);

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef4 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef3 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef2,
                storPoolFactoryRef2, propsContainerFactoryRef4, transObjFactoryRef3, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef2, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        // Act
        (new StltNodeApiCallHandler(errorReporterRef, null, null, reconfigurationLockRef, nodesMapLockRef, null,
                nodeFactoryRef, nodeConnectionFactoryRef, netInterfaceFactoryRef, controllerPeerConnectorRef,
                new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps()), null)).applyDeletedNode("Node Name Str");
    }

    /**
     * Test {@link StltNodeApiCallHandler#applyDeletedNode(String)}.
     * <ul>
     *   <li>When {@code Node Name Str}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltNodeApiCallHandler#applyDeletedNode(String)}
     */
    @Test
    public void testApplyDeletedNode_whenNodeNameStr() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltNodeApiCallHandler.apiCtx
        //     StltNodeApiCallHandler.controllerPeerConnector
        //     StltNodeApiCallHandler.deviceManager
        //     StltNodeApiCallHandler.errorReporter
        //     StltNodeApiCallHandler.netInterfaceFactory
        //     StltNodeApiCallHandler.nodeConnectionFactory
        //     StltNodeApiCallHandler.nodeFactory
        //     StltNodeApiCallHandler.nodesMap
        //     StltNodeApiCallHandler.nodesMapLock
        //     StltNodeApiCallHandler.reconfigurationLock
        //     StltNodeApiCallHandler.stltConfigAccessor
        //     StltNodeApiCallHandler.transMgrProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
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

        SatelliteNodeConDfnDriver dbDriverRef3 = new SatelliteNodeConDfnDriver();
        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        NodeConnectionFactory nodeConnectionFactoryRef = new NodeConnectionFactory(dbDriverRef3, propsContainerFactoryRef3,
                new TransactionObjectFactory(null), null);

        SatelliteNiDriver driverRef2 = new SatelliteNiDriver();
        NetInterfaceFactory netInterfaceFactoryRef = new NetInterfaceFactory(driverRef2, new TransactionObjectFactory(null),
                null);

        // Act
        (new StltNodeApiCallHandler(errorReporterRef, null, null, reconfigurationLockRef, nodesMapLockRef, null,
                nodeFactoryRef, nodeConnectionFactoryRef, netInterfaceFactoryRef, null,
                new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps()), null)).applyDeletedNode("Node Name Str");
    }

    /**
     * Test {@link StltNodeApiCallHandler#applyChanges(NodePojo)}.
     * <p>
     * Method under test: {@link StltNodeApiCallHandler#applyChanges(NodePojo)}
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

        SatelliteNodeConDfnDriver dbDriverRef3 = new SatelliteNodeConDfnDriver();
        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        NodeConnectionFactory nodeConnectionFactoryRef = new NodeConnectionFactory(dbDriverRef3, propsContainerFactoryRef3,
                new TransactionObjectFactory(null), null);

        SatelliteNiDriver driverRef2 = new SatelliteNiDriver();
        NetInterfaceFactory netInterfaceFactoryRef = new NetInterfaceFactory(driverRef2, new TransactionObjectFactory(null),
                null);

        ReentrantReadWriteLock reconfigurationLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef2 = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef4 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef5 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef5,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef4 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef3 = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef2 = new NodeSatelliteFactory(dbDriverRef4, objectProtectionFactoryRef2,
                storPoolFactoryRef2, propsContainerFactoryRef4, transObjFactoryRef3, new FreeSpaceMgrSatelliteFactory(null),
                null, null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef2, nodesMapLockRef2, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef2, null,
                nodeFactoryRef2, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StltNodeApiCallHandler stltNodeApiCallHandler = new StltNodeApiCallHandler(errorReporterRef, null, null,
                reconfigurationLockRef, nodesMapLockRef, null, nodeFactoryRef, nodeConnectionFactoryRef, netInterfaceFactoryRef,
                controllerPeerConnectorRef, new StltConfigAccessor(ReadOnlyPropsImpl.emptyRoProps()), null);
        UUID nodeUuidRef = UUID.randomUUID();
        ArrayList<NetInterfaceApi> nodeNetInterfacesRef = new ArrayList<>();
        Builder builder = null;
        UnknownFieldSet unknownFields = UnknownFieldSet.newBuilder().build();
        NetInterface refNetInterface = builder.mergeUnknownFields(unknownFields).build();
        NetInterfaceApiData nodeActiveStltConnRef = new NetInterfaceApiData(refNetInterface);
        ArrayList<NodeConnPojo> nodeConnsRef = new ArrayList<>();
        HashMap<String, String> nodePropsRef = new HashMap<>();
        ArrayList<String> deviceLayerKindNamesRef = new ArrayList<>();
        ArrayList<String> deviceProviderKindNamesRef = new ArrayList<>();
        HashMap<String, List<String>> unsupportedLayersWithReasonsRef = new HashMap<>();

        // Act
        stltNodeApiCallHandler.applyChanges(new NodePojo(nodeUuidRef, "Node Name Ref", "Node Type Ref", 1L,
                nodeNetInterfacesRef, nodeActiveStltConnRef, nodeConnsRef, nodePropsRef, ConnectionStatus.OFFLINE, 1L, 1L,
                deviceLayerKindNamesRef, deviceProviderKindNamesRef, unsupportedLayersWithReasonsRef, new HashMap<>(), 1L, 3L));
    }

    /**
     * Test {@link StltNodeApiCallHandler#canRemoteNodeBeDeleted(AccessContext, Node, Node)}.
     * <p>
     * Method under test: {@link StltNodeApiCallHandler#canRemoteNodeBeDeleted(AccessContext, Node, Node)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCanRemoteNodeBeDeleted() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getResourceCount()" because "remoteNodeRef" is null
        //       at com.linbit.linstor.core.apicallhandler.StltNodeApiCallHandler.canRemoteNodeBeDeleted(StltNodeApiCallHandler.java:298)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        StltNodeApiCallHandler.canRemoteNodeBeDeleted(null, null, null);
    }

    /**
     * Test {@link StltNodeApiCallHandler#deleteRemoteNodeIfNeeded(AccessContext, NodesMap, Node, Node)}.
     * <p>
     * Method under test: {@link StltNodeApiCallHandler#deleteRemoteNodeIfNeeded(AccessContext, NodesMap, Node, Node)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testDeleteRemoteNodeIfNeeded() throws DatabaseException, AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getResourceCount()" because "remoteNodeRef" is null
        //       at com.linbit.linstor.core.apicallhandler.StltNodeApiCallHandler.canRemoteNodeBeDeleted(StltNodeApiCallHandler.java:298)
        //       at com.linbit.linstor.core.apicallhandler.StltNodeApiCallHandler.deleteRemoteNodeIfNeeded(StltNodeApiCallHandler.java:312)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        StltNodeApiCallHandler.deleteRemoteNodeIfNeeded(null, null, null, null);
    }

    /**
     * Test {@link StltNodeApiCallHandler#StltNodeApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ReadWriteLock, ReadWriteLock, NodesMap, NodeSatelliteFactory, NodeConnectionFactory, NetInterfaceFactory, ControllerPeerConnector, StltConfigAccessor, Provider)}.
     * <p>
     * Method under test: {@link StltNodeApiCallHandler#StltNodeApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ReadWriteLock, ReadWriteLock, NodesMap, NodeSatelliteFactory, NodeConnectionFactory, NetInterfaceFactory, ControllerPeerConnector, StltConfigAccessor, Provider)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewStltNodeApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltNodeApiCallHandler.<init>(ErrorReporter, AccessContext, DeviceManager, ReadWriteLock, ReadWriteLock, NodesMap, NodeSatelliteFactory, NodeConnectionFactory, NetInterfaceFactory, ControllerPeerConnector, StltConfigAccessor, Provider).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ErrorReporter errorReporterRef = null;
        AccessContext apiCtxRef = null;
        DeviceManager deviceManagerRef = null;
        ReadWriteLock reconfigurationLockRef = null;
        ReadWriteLock nodesMapLockRef = null;
        NodesMap nodesMapRef = null;
        NodeSatelliteFactory nodeFactoryRef = null;
        NodeConnectionFactory nodeConnectionFactoryRef = null;
        NetInterfaceFactory netInterfaceFactoryRef = null;
        ControllerPeerConnector controllerPeerConnectorRef = null;
        StltConfigAccessor stltConfigAccessorRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;

        // Act
        StltNodeApiCallHandler actualStltNodeApiCallHandler = new StltNodeApiCallHandler(errorReporterRef, apiCtxRef,
                deviceManagerRef, reconfigurationLockRef, nodesMapLockRef, nodesMapRef, nodeFactoryRef,
                nodeConnectionFactoryRef, netInterfaceFactoryRef, controllerPeerConnectorRef, stltConfigAccessorRef,
                transMgrProviderRef);

        // Assert
        // TODO: Add assertions on result
    }
}
