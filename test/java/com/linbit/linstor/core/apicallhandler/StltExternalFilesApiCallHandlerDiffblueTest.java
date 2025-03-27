package com.linbit.linstor.core.apicallhandler;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.pojo.ExternalFilePojo;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.ExternalFileMap;
import com.linbit.linstor.core.CoreModule.ResourceDefinitionMap;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.objects.ExternalFileSatelliteFactory;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.dbdrivers.SatelliteExternalFileDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
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
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class StltExternalFilesApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltExternalFilesApiCallHandler#applyChanges(ExternalFilePojo)}.
     * <p>
     * Method under test: {@link StltExternalFilesApiCallHandler#applyChanges(ExternalFilePojo)}
     */
    @Test
    public void testApplyChanges() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltExternalFilesApiCallHandler.apiCtx
        //     StltExternalFilesApiCallHandler.ctrlPeerConnector
        //     StltExternalFilesApiCallHandler.deviceManager
        //     StltExternalFilesApiCallHandler.errorReporter
        //     StltExternalFilesApiCallHandler.extFileFactory
        //     StltExternalFilesApiCallHandler.extFileMap
        //     StltExternalFilesApiCallHandler.rscDfnMap
        //     StltExternalFilesApiCallHandler.transMgrProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteExternalFileDriver driverRef = new SatelliteExternalFileDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        StltExternalFilesApiCallHandler stltExternalFilesApiCallHandler = new StltExternalFilesApiCallHandler(
                errorReporterRef, null, null, null, new ExternalFileSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null),
                null, null, null);
        UUID uuidRef = UUID.randomUUID();
        byte[] contentRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltExternalFilesApiCallHandler
                .applyChanges(new ExternalFilePojo(uuidRef, "foo.txt", 1L, contentRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltExternalFilesApiCallHandler#applyChanges(ExternalFilePojo)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltExternalFilesApiCallHandler#applyChanges(ExternalFilePojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyChanges_givenReentrantReadWriteLockWithTrue() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteExternalFileDriver driverRef = new SatelliteExternalFileDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        ExternalFileSatelliteFactory extFileFactoryRef = new ExternalFileSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef2 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef3,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef2, objectProtectionFactoryRef2,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        StltExternalFilesApiCallHandler stltExternalFilesApiCallHandler = new StltExternalFilesApiCallHandler(
                errorReporterRef, null, null, null, extFileFactoryRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef,
                        storPoolDfnMapLockRef, errorReporterRef2, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))),
                null, null);
        UUID uuidRef = UUID.randomUUID();
        byte[] contentRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltExternalFilesApiCallHandler
                .applyChanges(new ExternalFilePojo(uuidRef, "foo.txt", 1L, contentRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltExternalFilesApiCallHandler#applyDeletedExtFile(ExternalFilePojo)}.
     * <p>
     * Method under test: {@link StltExternalFilesApiCallHandler#applyDeletedExtFile(ExternalFilePojo)}
     */
    @Test
    public void testApplyDeletedExtFile() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltExternalFilesApiCallHandler.apiCtx
        //     StltExternalFilesApiCallHandler.ctrlPeerConnector
        //     StltExternalFilesApiCallHandler.deviceManager
        //     StltExternalFilesApiCallHandler.errorReporter
        //     StltExternalFilesApiCallHandler.extFileFactory
        //     StltExternalFilesApiCallHandler.extFileMap
        //     StltExternalFilesApiCallHandler.rscDfnMap
        //     StltExternalFilesApiCallHandler.transMgrProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteExternalFileDriver driverRef = new SatelliteExternalFileDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        StltExternalFilesApiCallHandler stltExternalFilesApiCallHandler = new StltExternalFilesApiCallHandler(
                errorReporterRef, null, null, null, new ExternalFileSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null),
                null, null, null);
        UUID uuidRef = UUID.randomUUID();
        byte[] contentRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltExternalFilesApiCallHandler.applyDeletedExtFile(
                new ExternalFilePojo(uuidRef, "foo.txt", 1L, contentRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltExternalFilesApiCallHandler#applyDeletedExtFile(ExternalFilePojo)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltExternalFilesApiCallHandler#applyDeletedExtFile(ExternalFilePojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyDeletedExtFile_givenReentrantReadWriteLockWithTrue() throws UnsupportedEncodingException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        SatelliteExternalFileDriver driverRef = new SatelliteExternalFileDriver();
        SatelliteSecObjProtDbDriver dbDriverRef = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef = new ObjectProtectionFactory(null, dbDriverRef,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        ExternalFileSatelliteFactory extFileFactoryRef = new ExternalFileSatelliteFactory(null, driverRef,
                objectProtectionFactoryRef, new TransactionObjectFactory(null), null);

        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        SatelliteNodeDriver dbDriverRef2 = new SatelliteNodeDriver();
        SatelliteSecObjProtDbDriver dbDriverRef3 = new SatelliteSecObjProtDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef3,
                new SatelliteSecObjProtAclDbDriver(), null, null);

        StorPoolSatelliteFactory storPoolFactoryRef = new StorPoolSatelliteFactory(new SatelliteStorPoolDriver(), null,
                null, null, null);

        PropsContainerFactory propsContainerFactoryRef = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef = new TransactionObjectFactory(null);
        NodeSatelliteFactory nodeFactoryRef = new NodeSatelliteFactory(dbDriverRef2, objectProtectionFactoryRef2,
                storPoolFactoryRef, propsContainerFactoryRef, transObjFactoryRef, new FreeSpaceMgrSatelliteFactory(null), null,
                null);

        ProtoCommonSerializer commonSerializerRef = new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null);

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        StltExternalFilesApiCallHandler stltExternalFilesApiCallHandler = new StltExternalFilesApiCallHandler(
                errorReporterRef, null, null, null, extFileFactoryRef,
                new ControllerPeerConnectorImpl(null, reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef,
                        storPoolDfnMapLockRef, errorReporterRef2, null, nodeFactoryRef, null, commonSerializerRef, null, null,
                        new StltExtToolsChecker(errorReporterRef3, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                                new DrbdEventService(null, new DrbdStateTracker(), null, null))),
                null, null);
        UUID uuidRef = UUID.randomUUID();
        byte[] contentRef = "AXAXAXAX".getBytes("UTF-8");

        // Act
        stltExternalFilesApiCallHandler.applyDeletedExtFile(
                new ExternalFilePojo(uuidRef, "foo.txt", 1L, contentRef, "AXAXAXAX".getBytes("UTF-8"), 1L, 1L));
    }

    /**
     * Test {@link StltExternalFilesApiCallHandler#StltExternalFilesApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ExternalFileMap, ExternalFileSatelliteFactory, ControllerPeerConnectorImpl, ResourceDefinitionMap, Provider)}.
     * <p>
     * Method under test: {@link StltExternalFilesApiCallHandler#StltExternalFilesApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ExternalFileMap, ExternalFileSatelliteFactory, ControllerPeerConnectorImpl, ResourceDefinitionMap, Provider)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewStltExternalFilesApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltExternalFilesApiCallHandler.<init>(ErrorReporter, AccessContext, DeviceManager, ExternalFileMap, ExternalFileSatelliteFactory, ControllerPeerConnectorImpl, ResourceDefinitionMap, Provider).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ErrorReporter errorReporterRef = null;
        AccessContext apiCtxRef = null;
        DeviceManager deviceManagerRef = null;
        ExternalFileMap extFileMapRef = null;
        ExternalFileSatelliteFactory extFileFactoryRef = null;
        ControllerPeerConnectorImpl ctrlPeerConnectorRef = null;
        ResourceDefinitionMap rscDfnMapRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;

        // Act
        StltExternalFilesApiCallHandler actualStltExternalFilesApiCallHandler = new StltExternalFilesApiCallHandler(
                errorReporterRef, apiCtxRef, deviceManagerRef, extFileMapRef, extFileFactoryRef, ctrlPeerConnectorRef,
                rscDfnMapRef, transMgrProviderRef);

        // Assert
        // TODO: Add assertions on result
    }
}
