package com.linbit.linstor.core.apicallhandler;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.DecryptionHelper;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.ResourceDefinitionMap;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.modularcrypto.JclCryptoProvider;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StltCryptApiCallHelperDiffblueTest {
    /**
     * Test {@link StltCryptApiCallHelper#decryptVolumesAndDrives(boolean)}.
     * <p>
     * Method under test: {@link StltCryptApiCallHelper#decryptVolumesAndDrives(boolean)}
     */
    @Test
    public void testDecryptVolumesAndDrives() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltCryptApiCallHelper.apiCtx
        //     StltCryptApiCallHelper.controllerPeerConnector
        //     StltCryptApiCallHelper.decryptionHelper
        //     StltCryptApiCallHelper.devMgr
        //     StltCryptApiCallHelper.errorReporter
        //     StltCryptApiCallHelper.extCmdFactory
        //     StltCryptApiCallHelper.rscDfnMap
        //     StltCryptApiCallHelper.secObjs
        //     StltCryptApiCallHelper.transMgrProvider

        // Arrange
        StltSecurityObjects secObjsRef = new StltSecurityObjects();
        DecryptionHelper decryptionHelperRef = new DecryptionHelper(new JclCryptoProvider());
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();

        // Act
        (new StltCryptApiCallHelper(null, null, null, secObjsRef, null, decryptionHelperRef, null, errorReporterRef,
                new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name")))).decryptVolumesAndDrives(true);
    }

    /**
     * Test {@link StltCryptApiCallHelper#decryptVolumesAndDrives(boolean)}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltCryptApiCallHelper#decryptVolumesAndDrives(boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testDecryptVolumesAndDrives_givenReentrantReadWriteLockWithTrue() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StltSecurityObjects secObjsRef = new StltSecurityObjects();
        DecryptionHelper decryptionHelperRef = new DecryptionHelper(new JclCryptoProvider());
        ReentrantReadWriteLock reconfigurationLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock nodesMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock rscDfnMapLockRef = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storPoolDfnMapLockRef = new ReentrantReadWriteLock(true);
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
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

        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(new CoreTimerImpl(), null);

        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(new CoreTimerImpl(), null);

        StltConfig stltCfgRef = new StltConfig();
        ControllerPeerConnectorImpl controllerPeerConnectorRef = new ControllerPeerConnectorImpl(null,
                reconfigurationLockRef, nodesMapLockRef, rscDfnMapLockRef, storPoolDfnMapLockRef, errorReporterRef, null,
                nodeFactoryRef, null, commonSerializerRef, null, null,
                new StltExtToolsChecker(errorReporterRef2, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                        new DrbdEventService(null, new DrbdStateTracker(), null, null)));

        StderrErrorReporter errorReporterRef3 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();

        // Act
        (new StltCryptApiCallHelper(null, null, null, secObjsRef, null, decryptionHelperRef, controllerPeerConnectorRef,
                errorReporterRef3, new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"))))
                .decryptVolumesAndDrives(true);
    }

    /**
     * Test {@link StltCryptApiCallHelper#StltCryptApiCallHelper(ResourceDefinitionMap, AccessContext, Provider, StltSecurityObjects, DeviceManager, DecryptionHelper, ControllerPeerConnector, ErrorReporter, ExtCmdFactory)}.
     * <p>
     * Method under test: {@link StltCryptApiCallHelper#StltCryptApiCallHelper(ResourceDefinitionMap, AccessContext, Provider, StltSecurityObjects, DeviceManager, DecryptionHelper, ControllerPeerConnector, ErrorReporter, ExtCmdFactory)}
     */
    @Test
    public void testNewStltCryptApiCallHelper() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltCryptApiCallHelper.<init>(ResourceDefinitionMap, AccessContext, Provider, StltSecurityObjects, DeviceManager, DecryptionHelper, ControllerPeerConnector, ErrorReporter, ExtCmdFactory).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ResourceDefinitionMap rscDfnMapRef = null;
        AccessContext apiCtxRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;
        StltSecurityObjects secObjsRef = null;
        DeviceManager devMgrRef = null;
        DecryptionHelper decryptionHelperRef = null;
        ControllerPeerConnector controllerPeerConnectorRef = null;
        ErrorReporter errorReporterRef = null;
        ExtCmdFactory extCmdFactoryRef = null;

        // Act
        StltCryptApiCallHelper actualStltCryptApiCallHelper = new StltCryptApiCallHelper(rscDfnMapRef, apiCtxRef,
                transMgrProviderRef, secObjsRef, devMgrRef, decryptionHelperRef, controllerPeerConnectorRef, errorReporterRef,
                extCmdFactoryRef);

        // Assert
        // TODO: Add assertions on result
        assertNotNull(actualStltCryptApiCallHelper);
    }
}
