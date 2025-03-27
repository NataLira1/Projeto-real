package com.linbit.linstor.core.apicallhandler;

import com.linbit.InvalidNameException;
import com.linbit.PlatformStlt;
import com.linbit.linstor.core.CoreModule;
import com.linbit.linstor.core.CoreModule.ExternalFileMap;
import com.linbit.linstor.core.CoreModule.KeyValueStoreMap;
import com.linbit.linstor.core.CoreModule.NodesMap;
import com.linbit.linstor.core.CoreModule.RemoteMap;
import com.linbit.linstor.core.CoreModule.ResourceDefinitionMap;
import com.linbit.linstor.core.CoreModule.ResourceDefinitionMapExtName;
import com.linbit.linstor.core.CoreModule.ResourceGroupMap;
import com.linbit.linstor.core.CoreModule.ScheduleMap;
import com.linbit.linstor.core.CoreModule.StorPoolDefinitionMap;
import com.linbit.linstor.core.DeviceManager;
import com.linbit.linstor.core.StltExternalFileHandler;
import com.linbit.linstor.core.StltSecurityObjects;
import com.linbit.linstor.core.apicallhandler.response.ApiRcException;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo.ReadOnlyNode;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo.ReadOnlyStorPool;
import com.linbit.linstor.core.identifier.NodeName;
import com.linbit.linstor.core.identifier.ResourceName;
import com.linbit.linstor.core.identifier.SharedStorPoolName;
import com.linbit.linstor.core.identifier.StorPoolName;
import com.linbit.linstor.core.objects.Volume;
import com.linbit.linstor.core.objects.Volume.Key;
import com.linbit.linstor.layer.storage.AbsStorageProvider;
import com.linbit.linstor.layer.storage.AbsStorageProvider.AbsStorageProviderInit;
import com.linbit.linstor.layer.storage.DeviceProviderMapper;
import com.linbit.linstor.layer.storage.diskless.DisklessProvider;
import com.linbit.linstor.layer.storage.ebs.AbsEbsProvider;
import com.linbit.linstor.layer.storage.ebs.AbsEbsProvider.AbsEbsProviderIniit;
import com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider;
import com.linbit.linstor.layer.storage.ebs.EbsTargetProvider;
import com.linbit.linstor.layer.storage.exos.ExosProvider;
import com.linbit.linstor.layer.storage.file.FileProvider;
import com.linbit.linstor.layer.storage.file.FileThinProvider;
import com.linbit.linstor.layer.storage.lvm.LvmProvider;
import com.linbit.linstor.layer.storage.lvm.LvmThinProvider;
import com.linbit.linstor.layer.storage.spdk.SpdkLocalProvider;
import com.linbit.linstor.layer.storage.spdk.SpdkRemoteProvider;
import com.linbit.linstor.layer.storage.spdk.utils.SpdkLocalCommands;
import com.linbit.linstor.layer.storage.spdk.utils.SpdkRemoteCommands;
import com.linbit.linstor.layer.storage.storagespaces.StorageSpacesProvider;
import com.linbit.linstor.layer.storage.storagespaces.StorageSpacesThinProvider;
import com.linbit.linstor.layer.storage.zfs.ZfsProvider;
import com.linbit.linstor.layer.storage.zfs.ZfsThinProvider;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.storage.StorageException;
import com.linbit.linstor.storage.kinds.DeviceProviderKind;
import com.linbit.utils.Either;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;

public class StltApiCallHandlerUtilsDiffblueTest {
    /**
     * Test {@link StltApiCallHandlerUtils#getVlmAllocatedCapacities(Set, Set)}.
     * <p>
     * Method under test: {@link StltApiCallHandlerUtils#getVlmAllocatedCapacities(Set, Set)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetVlmAllocatedCapacities() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        StltApiCallHandlerUtils stltApiCallHandlerUtils = null;
        Set<StorPoolName> storPoolFilter = null;
        Set<ResourceName> resourceFilter = null;

        // Act
        Map<Key, Either<Long, ApiRcException>> actualVlmAllocatedCapacities = stltApiCallHandlerUtils
                .getVlmAllocatedCapacities(storPoolFilter, resourceFilter);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link StltApiCallHandlerUtils#getAllSpaceInfo()}.
     * <p>
     * Method under test: {@link StltApiCallHandlerUtils#getAllSpaceInfo()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetAllSpaceInfo() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read field "errorReporter" because "initRef" is null
        //       at com.linbit.linstor.layer.storage.AbsStorageProvider.<init>(AbsStorageProvider.java:187)
        //       at com.linbit.linstor.layer.storage.ebs.AbsEbsProvider.<init>(AbsEbsProvider.java:135)
        //       at com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider.<init>(EbsInitiatorProvider.java:110)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef2, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsProvider zfsProviderRef = new ZfsProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        DisklessProvider disklessProviderRef = new DisklessProvider();
        AbsStorageProviderInit superInitRef = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileProvider fileProviderRef = new FileProvider(superInitRef, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef2 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(superInitRef2, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef3 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(superInitRef3, new SpdkLocalCommands(null));

        AbsStorageProviderInit superInitRef4 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(superInitRef4,
                new SpdkRemoteCommands(null, null, null));

        ExosProvider exosProviderRef = new ExosProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));

        // Act
        (new StltApiCallHandlerUtils(errorReporterRef, null, null, stltExtFileHandlerRef, null, null, null, null, null,
                null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                                null, null, null, null, null))),
                null)).getAllSpaceInfo();
    }

    /**
     * Test {@link StltApiCallHandlerUtils#getSpaceInfo(boolean)} with {@code thin}.
     * <p>
     * Method under test: {@link StltApiCallHandlerUtils#getSpaceInfo(boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetSpaceInfoWithThin() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read field "errorReporter" because "initRef" is null
        //       at com.linbit.linstor.layer.storage.AbsStorageProvider.<init>(AbsStorageProvider.java:187)
        //       at com.linbit.linstor.layer.storage.ebs.AbsEbsProvider.<init>(AbsEbsProvider.java:135)
        //       at com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider.<init>(EbsInitiatorProvider.java:110)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef2, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsProvider zfsProviderRef = new ZfsProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        DisklessProvider disklessProviderRef = new DisklessProvider();
        AbsStorageProviderInit superInitRef = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileProvider fileProviderRef = new FileProvider(superInitRef, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef2 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(superInitRef2, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef3 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(superInitRef3, new SpdkLocalCommands(null));

        AbsStorageProviderInit superInitRef4 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(superInitRef4,
                new SpdkRemoteCommands(null, null, null));

        ExosProvider exosProviderRef = new ExosProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));

        // Act
        (new StltApiCallHandlerUtils(errorReporterRef, null, null, stltExtFileHandlerRef, null, null, null, null, null,
                null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                                null, null, null, null, null))),
                null)).getSpaceInfo(true);
    }

    /**
     * Test {@link StltApiCallHandlerUtils#getStoragePoolSpaceInfo(StorPoolInfo, boolean)}.
     * <p>
     * Method under test: {@link StltApiCallHandlerUtils#getStoragePoolSpaceInfo(StorPoolInfo, boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetStoragePoolSpaceInfo() throws InvalidNameException, StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef2, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsProvider zfsProviderRef = new ZfsProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        DisklessProvider disklessProviderRef = new DisklessProvider();
        AbsStorageProviderInit superInitRef = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileProvider fileProviderRef = new FileProvider(superInitRef, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef2 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(superInitRef2, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef3 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(superInitRef3, new SpdkLocalCommands(null));

        AbsStorageProviderInit superInitRef4 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(superInitRef4,
                new SpdkRemoteCommands(null, null, null));

        ExosProvider exosProviderRef = new ExosProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        StltApiCallHandlerUtils stltApiCallHandlerUtils = new StltApiCallHandlerUtils(errorReporterRef, null, null,
                stltExtFileHandlerRef, null, null, null, null, null, null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                                null, null, null, null, null))),
                null);
        UUID uuidRef = UUID.randomUUID();
        StorPoolName nameRef = new StorPoolName("Pool Name");
        SharedStorPoolName sharedStorPoolNameRef = SharedStorPoolName.restoreName("Shared Stor Pool Name Str");
        UUID uuidRef2 = UUID.randomUUID();
        NodeName nameRef2 = new NodeName("Node Name");
        ReadOnlyNode nodeRef = new ReadOnlyNode(uuidRef2, nameRef2, ReadOnlyPropsImpl.emptyRoProps());

        ReadOnlyPropsImpl roPropsRef = ReadOnlyPropsImpl.emptyRoProps();

        // Act
        stltApiCallHandlerUtils.getStoragePoolSpaceInfo(new ReadOnlyStorPool(uuidRef, nameRef, sharedStorPoolNameRef,
                DeviceProviderKind.DISKLESS, nodeRef, roPropsRef, new ArrayList<>()), true);
    }

    /**
     * Test {@link StltApiCallHandlerUtils#clearCoreMaps()}.
     * <p>
     * Method under test: {@link StltApiCallHandlerUtils#clearCoreMaps()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testClearCoreMaps() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read field "errorReporter" because "initRef" is null
        //       at com.linbit.linstor.layer.storage.AbsStorageProvider.<init>(AbsStorageProvider.java:187)
        //       at com.linbit.linstor.layer.storage.ebs.AbsEbsProvider.<init>(AbsEbsProvider.java:135)
        //       at com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider.<init>(EbsInitiatorProvider.java:110)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef2, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsProvider zfsProviderRef = new ZfsProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        DisklessProvider disklessProviderRef = new DisklessProvider();
        AbsStorageProviderInit superInitRef = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileProvider fileProviderRef = new FileProvider(superInitRef, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef2 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(superInitRef2, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef3 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(superInitRef3, new SpdkLocalCommands(null));

        AbsStorageProviderInit superInitRef4 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(superInitRef4,
                new SpdkRemoteCommands(null, null, null));

        ExosProvider exosProviderRef = new ExosProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));

        // Act
        (new StltApiCallHandlerUtils(errorReporterRef, null, null, stltExtFileHandlerRef, null, null, null, null, null,
                null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                                null, null, null, null, null))),
                null)).clearCoreMaps();
    }

    /**
     * Test {@link StltApiCallHandlerUtils#clearCaches()}.
     * <p>
     * Method under test: {@link StltApiCallHandlerUtils#clearCaches()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testClearCaches() throws StorageException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read field "errorReporter" because "initRef" is null
        //       at com.linbit.linstor.layer.storage.AbsStorageProvider.<init>(AbsStorageProvider.java:187)
        //       at com.linbit.linstor.layer.storage.ebs.AbsEbsProvider.<init>(AbsEbsProvider.java:135)
        //       at com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider.<init>(EbsInitiatorProvider.java:110)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StltExternalFileHandler stltExtFileHandlerRef = new StltExternalFileHandler(errorReporterRef2, null, null, null,
                new StltConfig());

        LvmProvider lvmProviderRef = new LvmProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        LvmThinProvider lvmThinProviderRef = new LvmThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsProvider zfsProviderRef = new ZfsProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        ZfsThinProvider zfsThinProviderRef = new ZfsThinProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        DisklessProvider disklessProviderRef = new DisklessProvider();
        AbsStorageProviderInit superInitRef = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileProvider fileProviderRef = new FileProvider(superInitRef, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef2 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        FileThinProvider fileThinProviderRef = new FileThinProvider(superInitRef2, new PlatformStlt(null, null));

        AbsStorageProviderInit superInitRef3 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkLocalProvider spdkLocalProviderRef = new SpdkLocalProvider(superInitRef3, new SpdkLocalCommands(null));

        AbsStorageProviderInit superInitRef4 = new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                null, null, null, null, null);

        SpdkRemoteProvider spdkRemoteProviderRef = new SpdkRemoteProvider(superInitRef4,
                new SpdkRemoteCommands(null, null, null));

        ExosProvider exosProviderRef = new ExosProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));
        EbsInitiatorProvider ebsInitProviderRef = new EbsInitiatorProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        EbsTargetProvider ebsTargetProviderRef = new EbsTargetProvider(
                new AbsEbsProviderIniit(null, null, null, new StltSecurityObjects()));
        StorageSpacesProvider storageSpacesProviderRef = new StorageSpacesProvider(
                new AbsStorageProviderInit(null, null, null, null, null, null, null, null, null, null, null, null, null));

        // Act
        (new StltApiCallHandlerUtils(errorReporterRef, null, null, stltExtFileHandlerRef, null, null, null, null, null,
                null, null, null,
                new DeviceProviderMapper(lvmProviderRef, lvmThinProviderRef, zfsProviderRef, zfsThinProviderRef,
                        disklessProviderRef, fileProviderRef, fileThinProviderRef, spdkLocalProviderRef, spdkRemoteProviderRef,
                        exosProviderRef, ebsInitProviderRef, ebsTargetProviderRef, storageSpacesProviderRef,
                        new StorageSpacesThinProvider(new AbsStorageProviderInit(null, null, null, null, null, null, null, null,
                                null, null, null, null, null))),
                null)).clearCaches();
    }

    /**
     * Test {@link StltApiCallHandlerUtils#StltApiCallHandlerUtils(ErrorReporter, AccessContext, ExternalFileMap, StltExternalFileHandler, KeyValueStoreMap, NodesMap, RemoteMap, ResourceDefinitionMap, ResourceDefinitionMapExtName, ResourceGroupMap, ScheduleMap, StorPoolDefinitionMap, DeviceProviderMapper, Provider)}.
     * <p>
     * Method under test: {@link StltApiCallHandlerUtils#StltApiCallHandlerUtils(ErrorReporter, AccessContext, ExternalFileMap, StltExternalFileHandler, KeyValueStoreMap, NodesMap, RemoteMap, ResourceDefinitionMap, ResourceDefinitionMapExtName, ResourceGroupMap, ScheduleMap, StorPoolDefinitionMap, DeviceProviderMapper, Provider)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewStltApiCallHandlerUtils() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.apicallhandler.StltApiCallHandlerUtils.<init>(ErrorReporter, AccessContext, ExternalFileMap, StltExternalFileHandler, KeyValueStoreMap, NodesMap, RemoteMap, ResourceDefinitionMap, ResourceDefinitionMapExtName, ResourceGroupMap, ScheduleMap, StorPoolDefinitionMap, DeviceProviderMapper, Provider).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.extproc.ExtCmdFactory.create()" because "extCmdFactoryRef" is null
        //       at com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider.getEc2InstanceId(EbsInitiatorProvider.java:120)
        //       at com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider.<init>(EbsInitiatorProvider.java:112)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ErrorReporter errorReporterRef = null;
        AccessContext apiCtxRef = null;
        ExternalFileMap extFilesMapRef = null;
        StltExternalFileHandler stltExtFileHandlerRef = null;
        KeyValueStoreMap kvsMapRef = null;
        NodesMap nodesMapRef = null;
        RemoteMap remoteMapRef = null;
        ResourceDefinitionMap rscDfnMapRef = null;
        ResourceDefinitionMapExtName rscDfnExtNameMapRef = null;
        ResourceGroupMap rscGrpMapRef = null;
        ScheduleMap scheduleMapRef = null;
        StorPoolDefinitionMap storPoolDfnMapRef = null;
        DeviceProviderMapper deviceProviderMapperRef = null;
        Provider<DeviceManager> devMgrProviderRef = null;

        // Act
        StltApiCallHandlerUtils actualStltApiCallHandlerUtils = new StltApiCallHandlerUtils(errorReporterRef, apiCtxRef,
                extFilesMapRef, stltExtFileHandlerRef, kvsMapRef, nodesMapRef, remoteMapRef, rscDfnMapRef, rscDfnExtNameMapRef,
                rscGrpMapRef, scheduleMapRef, storPoolDfnMapRef, deviceProviderMapperRef, devMgrProviderRef);

        // Assert
        // TODO: Add assertions on result
    }
}
