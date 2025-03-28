package com.linbit.linstor.core.apicallhandler;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.ResourceDefinitionMap;
import com.linbit.linstor.core.DeviceManager;
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
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StltRscDfnApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltRscDfnApiCallHandler#StltRscDfnApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, ResourceDefinitionMap, Provider)}.
     * <p>
     * Method under test: {@link StltRscDfnApiCallHandler#StltRscDfnApiCallHandler(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, ResourceDefinitionMap, Provider)}
     */
    @Test
    public void testNewStltRscDfnApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltRscDfnApiCallHandler.<init>(ErrorReporter, AccessContext, DeviceManager, ControllerPeerConnector, ResourceDefinitionMap, Provider).
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
        ResourceDefinitionMap rscDfnMapRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;

        // Act
        StltRscDfnApiCallHandler actualStltRscDfnApiCallHandler = new StltRscDfnApiCallHandler(errorReporterRef, apiCtxRef,
                deviceManagerRef, controllerPeerConnectorRef, rscDfnMapRef, transMgrProviderRef);

        // Assert
        // TODO: Add assertions on result
        assertNotNull(actualStltRscDfnApiCallHandler);
    }

    /**
     * Test {@link StltRscDfnApiCallHandler#primaryResource(String, UUID)}.
     * <ul>
     *   <li>When {@code Rsc Name Str}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltRscDfnApiCallHandler#primaryResource(String, UUID)}
     */
    @Test
    public void testPrimaryResource_whenRscNameStr() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltRscDfnApiCallHandler.apiCtx
        //     StltRscDfnApiCallHandler.controllerPeerConnector
        //     StltRscDfnApiCallHandler.deviceManager
        //     StltRscDfnApiCallHandler.errorReporter
        //     StltRscDfnApiCallHandler.rscDfnMap
        //     StltRscDfnApiCallHandler.transMgrProvider

        // Arrange
        StltRscDfnApiCallHandler stltRscDfnApiCallHandler = new StltRscDfnApiCallHandler(
                new StderrErrorReporter("Module Name"), null, null, null, null, null);

        // Act
        stltRscDfnApiCallHandler.primaryResource("Rsc Name Str", UUID.randomUUID());


    }
}
