package com.linbit.linstor.core.devmgr;

import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.ApiCallRcImpl;
import com.linbit.linstor.api.SpaceInfo;
import com.linbit.linstor.api.interfaces.serializer.CtrlStltSerializer;
import com.linbit.linstor.backupshipping.BackupShippingMgr;
import com.linbit.linstor.clone.CloneService;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.StltExternalFileHandler;
import com.linbit.linstor.core.SysFsHandler;
import com.linbit.linstor.core.UdevHandler;
import com.linbit.linstor.core.devmgr.DeviceHandler.CloneStrategy;
import com.linbit.linstor.core.devmgr.exceptions.ResourceException;
import com.linbit.linstor.core.devmgr.exceptions.VolumeException;
import com.linbit.linstor.core.objects.Node;
import com.linbit.linstor.core.objects.Resource;
import com.linbit.linstor.core.objects.Snapshot;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.event.common.ResourceState;
import com.linbit.linstor.event.common.ResourceStateEvent;
import com.linbit.linstor.interfaces.StorPoolInfo;
import com.linbit.linstor.layer.DeviceLayer;
import com.linbit.linstor.layer.DeviceLayer.NotificationListener;
import com.linbit.linstor.layer.LayerFactory;
import com.linbit.linstor.layer.LayerSizeHelper;
import com.linbit.linstor.layer.storage.StorageLayer;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.propscon.Props;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.snapshotshipping.SnapshotShippingService;
import com.linbit.linstor.storage.StorageException;
import com.linbit.linstor.storage.interfaces.categories.resource.AbsRscLayerObject;
import com.linbit.linstor.storage.interfaces.categories.resource.VlmProviderObject;

import java.util.Collection;
import java.util.Set;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class DeviceHandlerImplDiffblueTest {
    /**
     * Test {@link DeviceHandlerImpl#initialize()}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#initialize()}
     */
    @Test
    public void testInitialize() {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;

        // Act
        deviceHandlerImpl.initialize();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#dispatchResources(Collection, Collection)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#dispatchResources(Collection, Collection)}
     */
    @Test
    public void testDispatchResources() {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        Collection<Resource> rscsRef = null;
        Collection<Snapshot> snapsRef = null;

        // Act
        deviceHandlerImpl.dispatchResources(rscsRef, snapsRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#getResource(Resource, String)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#getResource(Resource, String)}
     */
    @Test
    public void testGetResource() throws AccessDeniedException, StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        Resource anyRsc = null;
        String rscName = "";

        // Act
        Resource actualResource = deviceHandlerImpl.getResource(anyRsc, rscName);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#sendResourceCreatedEvent(AbsRscLayerObject, ResourceState)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#sendResourceCreatedEvent(AbsRscLayerObject, ResourceState)}
     */
    @Test
    public void testSendResourceCreatedEvent() {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        AbsRscLayerObject<Resource> layerDataRef = null;
        ResourceState resourceStateRef = null;

        // Act
        deviceHandlerImpl.sendResourceCreatedEvent(layerDataRef, resourceStateRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#sendResourceDeletedEvent(AbsRscLayerObject)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#sendResourceDeletedEvent(AbsRscLayerObject)}
     */
    @Test
    public void testSendResourceDeletedEvent() {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        AbsRscLayerObject<Resource> layerDataRef = null;

        // Act
        deviceHandlerImpl.sendResourceDeletedEvent(layerDataRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#processResource(AbsRscLayerObject, ApiCallRcImpl)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#processResource(AbsRscLayerObject, ApiCallRcImpl)}
     */
    @Test
    public void testProcessResource()
            throws ResourceException, VolumeException, DatabaseException, AccessDeniedException, StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        AbsRscLayerObject<Resource> rscLayerDataRef = null;
        ApiCallRcImpl apiCallRcRef = null;

        // Act
        deviceHandlerImpl.processResource(rscLayerDataRef, apiCallRcRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#DeviceHandlerImpl(AccessContext, ErrorReporter, ControllerPeerConnector, CtrlStltSerializer, Provider, LayerFactory, StorageLayer, ResourceStateEvent, ExtCmdFactory, SysFsHandler, UdevHandler, SnapshotShippingService, StltExternalFileHandler, BackupShippingMgr, SuspendManager, LayerSizeHelper, CloneService)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#DeviceHandlerImpl(AccessContext, ErrorReporter, ControllerPeerConnector, CtrlStltSerializer, Provider, LayerFactory, StorageLayer, ResourceStateEvent, ExtCmdFactory, SysFsHandler, UdevHandler, SnapshotShippingService, StltExternalFileHandler, BackupShippingMgr, SuspendManager, LayerSizeHelper, CloneService)}
     */
    @Test
    @Ignore
    public void testNewDeviceHandlerImpl() {
        // Arrange
        // TODO: Populate arranged inputs
        AccessContext wrkCtxRef = null;
        ErrorReporter errorReporterRef = null;
        ControllerPeerConnector controllerPeerConnectorRef = null;
        CtrlStltSerializer interComSerializerRef = null;
        Provider<NotificationListener> notificationListenerRef = null;
        LayerFactory layerFactoryRef = null;
        StorageLayer storageLayerRef = null;
        ResourceStateEvent resourceStateEventRef = null;
        ExtCmdFactory extCmdFactoryRef = null;
        SysFsHandler sysFsHandlerRef = null;
        UdevHandler udevHandlerRef = null;
        SnapshotShippingService snapshotShippingManagerRef = null;
        StltExternalFileHandler extFileHandlerRef = null;
        BackupShippingMgr backupShippingManagerRef = null;
        SuspendManager suspendMgrRef = null;
        LayerSizeHelper layerSizeHelperRef = null;
        CloneService cloneServiceRef = null;

        // Act
        DeviceHandlerImpl actualDeviceHandlerImpl = new DeviceHandlerImpl(wrkCtxRef, errorReporterRef,
                controllerPeerConnectorRef, interComSerializerRef, notificationListenerRef, layerFactoryRef, storageLayerRef,
                resourceStateEventRef, extCmdFactoryRef, sysFsHandlerRef, udevHandlerRef, snapshotShippingManagerRef,
                extFileHandlerRef, backupShippingManagerRef, suspendMgrRef, layerSizeHelperRef, cloneServiceRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#processSnapshot(AbsRscLayerObject, ApiCallRcImpl)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#processSnapshot(AbsRscLayerObject, ApiCallRcImpl)}
     */
    @Test
    public void testProcessSnapshot()
            throws ResourceException, VolumeException, DatabaseException, AccessDeniedException, StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        AbsRscLayerObject<Snapshot> snapLayerDataRef = null;
        ApiCallRcImpl apiCallRcRef = null;

        // Act
        deviceHandlerImpl.processSnapshot(snapLayerDataRef, apiCallRcRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#fullSyncApplied(Node)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#fullSyncApplied(Node)}
     */
    @Test
    public void testFullSyncApplied() throws StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        Node localNode = null;

        // Act
        deviceHandlerImpl.fullSyncApplied(localNode);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#localNodePropsChanged(Props)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#localNodePropsChanged(Props)}
     */
    @Test
    public void testLocalNodePropsChanged() throws AccessDeniedException, StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        Props newLocalNodeProps = null;

        // Act
        deviceHandlerImpl.localNodePropsChanged(newLocalNodeProps);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#getSpaceInfo(StorPoolInfo, boolean)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#getSpaceInfo(StorPoolInfo, boolean)}
     */
    @Test
    public void testGetSpaceInfo() throws StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        StorPoolInfo storPoolInfo = null;
        boolean update = false;

        // Act
        SpaceInfo actualSpaceInfo = deviceHandlerImpl.getSpaceInfo(storPoolInfo, update);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#getCloneStrategy(VlmProviderObject)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#getCloneStrategy(VlmProviderObject)}
     */
    @Test
    public void testGetCloneStrategy() throws StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        VlmProviderObject<?> vlmProvObj = null;

        // Act
        Set<CloneStrategy> actualCloneStrategy = deviceHandlerImpl.getCloneStrategy(vlmProvObj);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#openForClone(VlmProviderObject, String)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#openForClone(VlmProviderObject, String)}
     */
    @Test
    public void testOpenForClone() throws DatabaseException, AccessDeniedException, StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        VlmProviderObject<?> vlmData = null;
        String targetRscNameRef = "";

        // Act
        deviceHandlerImpl.openForClone(vlmData, targetRscNameRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#closeAfterClone(VlmProviderObject, String)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#closeAfterClone(VlmProviderObject, String)}
     */
    @Test
    public void testCloseAfterClone() throws StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        VlmProviderObject<?> vlmData = null;
        String targetRscNameRef = "";

        // Act
        deviceHandlerImpl.closeAfterClone(vlmData, targetRscNameRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link DeviceHandlerImpl#processAfterClone(VlmProviderObject, VlmProviderObject, String)}.
     * <p>
     * Method under test: {@link DeviceHandlerImpl#processAfterClone(VlmProviderObject, VlmProviderObject, String)}
     */
    @Test
    public void testProcessAfterClone() throws StorageException {
        // Arrange
        // TODO: Populate arranged inputs
        DeviceHandlerImpl deviceHandlerImpl = null;
        VlmProviderObject<?> vlmSrcData = null;
        VlmProviderObject<?> vlmTgtData = null;
        String clonedDevPath = "";

        // Act
        deviceHandlerImpl.processAfterClone(vlmSrcData, vlmTgtData, clonedDevPath);

        // Assert
        // TODO: Add assertions on result
    }
}
