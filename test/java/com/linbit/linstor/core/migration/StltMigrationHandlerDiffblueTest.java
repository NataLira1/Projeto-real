package com.linbit.linstor.core.migration;

import static org.junit.Assert.assertTrue;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.protobuf.FullSync;
import com.linbit.linstor.api.protobuf.FullSync.FullSyncStatus;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.apicallhandler.StltExtToolsChecker;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.migration.StltMigrationHandler.StltMigrationResult;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Ignore;
import org.junit.Test;

public class StltMigrationHandlerDiffblueTest {
    /**
     * Test {@link StltMigrationHandler#StltMigrationHandler(AccessContext, ErrorReporter, ControllerPeerConnector, ExtCmdFactory, Map)}.
     * <p>
     * Method under test: {@link StltMigrationHandler#StltMigrationHandler(AccessContext, ErrorReporter, ControllerPeerConnector, ExtCmdFactory, Map)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewStltMigrationHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.migration.StltMigrationHandler.<init>(AccessContext, ErrorReporter, ControllerPeerConnector, ExtCmdFactory, Map).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        AccessContext accCtxRef = null;
        ErrorReporter errorReporterRef = null;
        ControllerPeerConnector controllerPeerConnectorRef = null;
        ExtCmdFactory extCmdFactoryRef = null;
        Map<SatelliteMigrations, BaseStltMigration> stltMigrationsMapRef = null;

        // Act
        StltMigrationHandler actualStltMigrationHandler = new StltMigrationHandler(accCtxRef, errorReporterRef,
                controllerPeerConnectorRef, extCmdFactoryRef, stltMigrationsMapRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link StltMigrationHandler#migrate()}.
     * <ul>
     *   <li>Given {@link ReentrantReadWriteLock#ReentrantReadWriteLock(boolean)} with {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltMigrationHandler#migrate()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testMigrate_givenReentrantReadWriteLockWithTrue() {
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

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        // Act
        (new StltMigrationHandler(null, errorReporterRef, controllerPeerConnectorRef, extCmdFactoryRef2, new HashMap<>()))
                .migrate();
    }

    /**
     * Test StltMigrationResult {@link StltMigrationResult#StltMigrationResult(FullSyncStatus, Map, Set, Set)}.
     * <p>
     * Method under test: {@link StltMigrationResult#StltMigrationResult(FullSyncStatus, Map, Set, Set)}
     */
    @Test
    public void testStltMigrationResultNewStltMigrationResult() {
        // Arrange
        HashMap<String, String> stltPropsToAddRef = new HashMap<>();
        HashSet<String> stltPropKeysToDeleteRef = new HashSet<>();

        // Act
        StltMigrationResult actualStltMigrationResult = new StltMigrationResult(FullSyncStatus.SUCCESS, stltPropsToAddRef,
                stltPropKeysToDeleteRef, new HashSet<>());

        // Assert
        assertTrue(actualStltMigrationResult.stltPropsToAdd.isEmpty());
        assertTrue(actualStltMigrationResult.stltPropKeysToDelete.isEmpty());
        assertTrue(actualStltMigrationResult.stltPropNamespacesToDelete.isEmpty());
    }
}
