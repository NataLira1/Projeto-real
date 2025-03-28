package com.linbit.linstor.core.apicallhandler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.DecryptionHelper;
import com.linbit.linstor.api.interfaces.serializer.CtrlStltSerializer;
import com.linbit.linstor.api.pojo.StorPoolPojo;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.core.ControllerPeerConnector;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.StorPoolDefinitionMap;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.StltExternalFileHandler;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.apicallhandler.StltStorPoolApiCallHandler.ChangedData;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolDefinitionSatelliteFactory;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDfnDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
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
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.modularcrypto.JclCryptoProvider;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.storage.StorageException;
import com.linbit.linstor.storage.kinds.DeviceProviderKind;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;
import com.linbit.linstor.transaction.manager.TransactionMgr;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class StltStorPoolApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltStorPoolApiCallHandler#StltStorPoolApiCallHandler(ErrorReporter, AccessContext, DeviceManager, StorPoolDefinitionMap, ControllerPeerConnector, StorPoolDefinitionSatelliteFactory, StorPoolSatelliteFactory, Provider, FreeSpaceMgrSatelliteFactory, StltApiCallHandlerUtils, CtrlStltSerializer, ExtCmdFactory, DecryptionHelper, StltSecurityObjects)}.
     * <p>
     * Method under test: {@link StltStorPoolApiCallHandler#StltStorPoolApiCallHandler(ErrorReporter, AccessContext, DeviceManager, StorPoolDefinitionMap, ControllerPeerConnector, StorPoolDefinitionSatelliteFactory, StorPoolSatelliteFactory, Provider, FreeSpaceMgrSatelliteFactory, StltApiCallHandlerUtils, CtrlStltSerializer, ExtCmdFactory, DecryptionHelper, StltSecurityObjects)}
     */
    @Test
    public void testNewStltStorPoolApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltStorPoolApiCallHandler.<init>(ErrorReporter, AccessContext, DeviceManager, StorPoolDefinitionMap, ControllerPeerConnector, StorPoolDefinitionSatelliteFactory, StorPoolSatelliteFactory, Provider, FreeSpaceMgrSatelliteFactory, StltApiCallHandlerUtils, CtrlStltSerializer, ExtCmdFactory, DecryptionHelper, StltSecurityObjects).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ErrorReporter errorReporterRef = null;
        AccessContext apiCtxRef = null;
        DeviceManager deviceManagerRef = null;
        StorPoolDefinitionMap storPoolDfnMapRef = null;
        ControllerPeerConnector controllerPeerConnectorRef = null;
        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = null;
        StorPoolSatelliteFactory storPoolFactoryRef = null;
        Provider<TransactionMgr> transMgrProviderRef = null;
        FreeSpaceMgrSatelliteFactory freeSpaceMgrFactoryRef = null;
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = null;
        CtrlStltSerializer ctrlStltSerializerRef = null;
        ExtCmdFactory extCmdFactoryRef = null;
        DecryptionHelper decryptionHelperRef = null;
        StltSecurityObjects securityObjectsRef = null;

        // Act
        StltStorPoolApiCallHandler actualStltStorPoolApiCallHandler = new StltStorPoolApiCallHandler(errorReporterRef,
                apiCtxRef, deviceManagerRef, storPoolDfnMapRef, controllerPeerConnectorRef, storPoolDefinitionFactoryRef,
                storPoolFactoryRef, transMgrProviderRef, freeSpaceMgrFactoryRef, apiCallHandlerUtilsRef, ctrlStltSerializerRef,
                extCmdFactoryRef, decryptionHelperRef, securityObjectsRef);

        // Assert
        // TODO: Add assertions on result

        assertNotNull(actualStltStorPoolApiCallHandler);

    }

    /**
     * Test {@link StltStorPoolApiCallHandler#applyDeletedStorPool(String)}.
     * <p>
     * Method under test: {@link StltStorPoolApiCallHandler#applyDeletedStorPool(String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyDeletedStorPool() throws StorageException {
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

        SatelliteStorPoolDfnDriver dbDriverRef3 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef4 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef4,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef3, objectProtectionFactoryRef2, propsContainerFactoryRef2, new TransactionObjectFactory(null), null,
                null);

        SatelliteStorPoolDriver driverRef = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(driverRef, propsContainerFactoryRef3,
                transObjFactoryRef2, null, new FreeSpaceMgrSatelliteFactory(null));

        FreeSpaceMgrSatelliteFactory freeSpaceMgrFactoryRef = new FreeSpaceMgrSatelliteFactory(null);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef5, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef4, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        DecryptionHelper decryptionHelperRef = new DecryptionHelper(new JclCryptoProvider());

        // Act
        (new StltStorPoolApiCallHandler(errorReporterRef, null, null, null, controllerPeerConnectorRef,
                storPoolDefinitionFactoryRef, storPoolFactoryRef2, null, freeSpaceMgrFactoryRef, apiCallHandlerUtilsRef,
                ctrlStltSerializerRef, extCmdFactoryRef2, decryptionHelperRef, new StltSecurityObjects()))
                .applyDeletedStorPool("Stor Pool Name Str");
    }

    /**
     * Test {@link StltStorPoolApiCallHandler#applyChanges(StorPoolPojo)}.
     * <p>
     * Method under test: {@link StltStorPoolApiCallHandler#applyChanges(StorPoolPojo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testApplyChanges() throws StorageException {
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

        SatelliteStorPoolDfnDriver dbDriverRef3 = new SatelliteStorPoolDfnDriver();
        SatelliteSecObjProtDbDriver dbDriverRef4 = new SatelliteSecObjProtDbDriver();
        SatelliteSecObjProtAclDbDriver objProtAclDbDriverRef = new SatelliteSecObjProtAclDbDriver();
        ObjectProtectionFactory objectProtectionFactoryRef2 = new ObjectProtectionFactory(null, dbDriverRef4,
                objProtAclDbDriverRef, null, new TransactionObjectFactory(null));

        PropsContainerFactory propsContainerFactoryRef2 = new PropsContainerFactory(new SatellitePropDriver(), null);

        StorPoolDefinitionSatelliteFactory storPoolDefinitionFactoryRef = new StorPoolDefinitionSatelliteFactory(
                dbDriverRef3, objectProtectionFactoryRef2, propsContainerFactoryRef2, new TransactionObjectFactory(null), null,
                null);

        SatelliteStorPoolDriver driverRef = new SatelliteStorPoolDriver();
        PropsContainerFactory propsContainerFactoryRef3 = new PropsContainerFactory(new SatellitePropDriver(), null);

        TransactionObjectFactory transObjFactoryRef2 = new TransactionObjectFactory(null);
        StorPoolSatelliteFactory storPoolFactoryRef2 = new StorPoolSatelliteFactory(driverRef, propsContainerFactoryRef3,
                transObjFactoryRef2, null, new FreeSpaceMgrSatelliteFactory(null));

        FreeSpaceMgrSatelliteFactory freeSpaceMgrFactoryRef = new FreeSpaceMgrSatelliteFactory(null);
        StderrErrorReporter errorReporterRef4 = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef5 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef5, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(null);
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(null);
        ZfsProvider zfsProviderRef = new ZfsProvider(null);
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(null);
        DisklessProvider disklessProviderRef = new DisklessProvider();
        FileProvider fileProviderRef = new FileProvider(null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(null, null);

        ExosProvider exosProviderRef = new ExosProvider(null);
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(null);
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(null);
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(null);
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef4, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(null)),
                null);

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        DecryptionHelper decryptionHelperRef = new DecryptionHelper(new JclCryptoProvider());
        StltStorPoolApiCallHandler stltStorPoolApiCallHandler = new StltStorPoolApiCallHandler(errorReporterRef, null, null,
                null, controllerPeerConnectorRef, storPoolDefinitionFactoryRef, storPoolFactoryRef2, null,
                freeSpaceMgrFactoryRef, apiCallHandlerUtilsRef, ctrlStltSerializerRef, extCmdFactoryRef2, decryptionHelperRef,
                new StltSecurityObjects());

        // Act
        stltStorPoolApiCallHandler.applyChanges(new StorPoolPojo("Stor Pool Name Ref", DeviceProviderKind.DISKLESS));
    }

    /**
     * Test ChangedData {@link ChangedData#ChangedData(StorPoolDefinition)}.
     * <p>
     * Method under test: {@link ChangedData#ChangedData(StorPoolDefinition)}
     */
    @Test
    public void testChangedDataNewChangedData() {
        // Arrange, Act and Assert
        assertNull((new ChangedData(null)).storPoolDfnToRegister);
    }
}
