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
import static org.junit.Assert.assertThrows;

public class ApplyControllerDiffblueTest {
    /**
     * Test {@link ApplyController#ApplyController(StltApiCallHandler)}.
     * <p>
     * Method under test: {@link ApplyController#ApplyController(StltApiCallHandler)}
     */
    @Test
    public void testNewApplyController() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.api.protobuf.ApplyController.<init>(StltApiCallHandler).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        StltApiCallHandler apiCallHandlerRef = null;

        // Act
        ApplyController actualApplyController = new ApplyController(apiCallHandlerRef);

        // Assert
        // TODO: Add assertions on result
        assertNotNull("ApplyController should be instantiated", actualApplyController);
    }

}
