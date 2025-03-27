package com.linbit.linstor.clone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import com.linbit.InvalidNameException;
import com.linbit.ServiceName;
import com.linbit.SystemServiceStartException;
import com.linbit.ValueOutOfRangeException;
import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.clone.CloneService.CloneInfo;
import com.linbit.linstor.clone.CloneService.CloneInfo.CloneStatus;
import com.linbit.linstor.core.ControllerPeerConnectorImpl;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.core.apicallhandler.StltExtToolsChecker;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.core.devmgr.DeviceHandler;
import com.linbit.linstor.core.devmgr.DeviceHandler.CloneStrategy;
import com.linbit.linstor.core.identifier.ResourceName;
import com.linbit.linstor.core.identifier.VolumeNumber;
import com.linbit.linstor.core.objects.FreeSpaceMgrSatelliteFactory;
import com.linbit.linstor.core.objects.NodeSatelliteFactory;
import com.linbit.linstor.core.objects.Resource;
import com.linbit.linstor.core.objects.StorPoolSatelliteFactory;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerBCacheVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerResourceIdDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerStorageRscDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteLayerStorageVlmDbDriver;
import com.linbit.linstor.dbdrivers.SatelliteNodeDriver;
import com.linbit.linstor.dbdrivers.SatellitePropDriver;
import com.linbit.linstor.dbdrivers.SatelliteStorPoolDriver;
import com.linbit.linstor.event.GenericEvent;
import com.linbit.linstor.event.common.VolumeDiskStateEvent;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.PropsContainerFactory;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.ObjectProtectionFactory;
import com.linbit.linstor.security.SatelliteSecObjProtAclDbDriver;
import com.linbit.linstor.security.SatelliteSecObjProtDbDriver;
import com.linbit.linstor.storage.StorageException;
import com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData;
import com.linbit.linstor.storage.data.adapter.bcache.BCacheVlmData;
import com.linbit.linstor.storage.data.adapter.cache.CacheRscData;
import com.linbit.linstor.storage.data.provider.StorageRscData;
import com.linbit.linstor.storage.data.provider.zfs.ZfsData;
import com.linbit.linstor.storage.interfaces.categories.resource.AbsRscLayerObject;
import com.linbit.linstor.storage.interfaces.categories.resource.VlmProviderObject;
import com.linbit.linstor.storage.kinds.DeviceProviderKind;
import com.linbit.linstor.timer.CoreTimerImpl;
import com.linbit.linstor.transaction.TransactionObjectFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Ignore;
import org.junit.Test;

public class CloneServiceDiffblueTest {
    /**
     * Test CloneInfo {@link CloneInfo#equals(Object)}.
     * <ul>
     *   <li>When other is different.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneInfo#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoEquals_whenOtherIsDifferent() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.dbdrivers.interfaces.LayerCacheRscDatabaseDriver.getIdDriver()" because "cacheRscDbDriverRef" is null
        //       at com.linbit.linstor.storage.data.adapter.cache.CacheRscData.<init>(CacheRscData.java:54)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        CacheRscData<Resource> parentRef = new CacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref", null, null,
                new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, parentRef, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef3 = new HashSet<>();
        CacheRscData<Resource> parentRef2 = new CacheRscData<>(1, null, null, childrenRef3, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef4 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, parentRef2, childrenRef4, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        CloneInfo cloneInfo = new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE);
        HashSet<AbsRscLayerObject<Resource>> childrenRef5 = new HashSet<>();
        CacheRscData<Resource> parentRef3 = new CacheRscData<>(1, null, null, childrenRef5, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef6 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef3 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef3 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef3 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef3 = new BCacheRscData<>(1, null, parentRef3, childrenRef6, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef3, bcacheVlmDbDriverRef3, vlmProviderObjectsRef3, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver3 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef2 = new BCacheVlmData<>(null, rscDataRef3, null, bcacheVlmdbDriver3,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef7 = new HashSet<>();
        CacheRscData<Resource> parentRef4 = new CacheRscData<>(1, null, null, childrenRef7, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef8 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef4 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef4 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef4 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef4 = new BCacheRscData<>(1, null, parentRef4, childrenRef8, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef4, bcacheVlmDbDriverRef4, vlmProviderObjectsRef4, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver4 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        cloneInfo.equals(new CloneInfo(srcVlmDataRef2,
                new BCacheVlmData<>(null, rscDataRef4, null, bcacheVlmdbDriver4, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE));
    }

    /**
     * Test CloneInfo {@link CloneInfo#equals(Object)}.
     * <ul>
     *   <li>When other is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneInfo#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoEquals_whenOtherIsNull() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        CacheRscData<Resource> parentRef = new CacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref", null, null,
                new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, parentRef, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef3 = new HashSet<>();
        CacheRscData<Resource> parentRef2 = new CacheRscData<>(1, null, null, childrenRef3, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef4 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, parentRef2, childrenRef4, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        (new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE)).equals(null);
    }

    /**
     * Test CloneInfo {@link CloneInfo#equals(Object)}.
     * <ul>
     *   <li>When other is wrong type.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneInfo#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoEquals_whenOtherIsWrongType() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        CacheRscData<Resource> parentRef = new CacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref", null, null,
                new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, parentRef, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef3 = new HashSet<>();
        CacheRscData<Resource> parentRef2 = new CacheRscData<>(1, null, null, childrenRef3, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef4 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, parentRef2, childrenRef4, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        (new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE)).equals("Different type to CloneInfo");
    }

    /**
     * Test CloneInfo {@link CloneInfo#getVlmNr()}.
     * <p>
     * Method under test: {@link CloneInfo#getVlmNr()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoGetVlmNr() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.base/java.util.Objects.requireNonNull(Objects.java:209)
        //       at com.linbit.linstor.storage.data.AbsRscData.<init>(AbsRscData.java:79)
        //       at com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData.<init>(BCacheRscData.java:48)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, null, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        (new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE)).getVlmNr();
    }

    /**
     * Test CloneInfo {@link CloneInfo#setCloneStatus(boolean)}.
     * <ul>
     *   <li>When {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneInfo#setCloneStatus(boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoSetCloneStatus_whenTrue() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.base/java.util.Objects.requireNonNull(Objects.java:209)
        //       at com.linbit.linstor.storage.data.AbsRscData.<init>(AbsRscData.java:79)
        //       at com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData.<init>(BCacheRscData.java:48)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, null, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        (new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE)).setCloneStatus(true);
    }

    /**
     * Test {@link CloneService#shutdown()}.
     * <p>
     * Method under test: {@link CloneService#shutdown()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testShutdown() {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        // Act
        (new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef, volumeDiskStateEventRef,
                extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null)).shutdown();
    }

    /**
     * Test {@link CloneService#shutdown()}.
     * <p>
     * Method under test: {@link CloneService#shutdown()}
     */
    @Test
    public void testShutdown2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        // Act
        (new CloneService(errorReporterRef, null, ctrlStltSerializerRef, volumeDiskStateEventRef, extCmdFactoryRef,
                new ReentrantReadWriteLock(true), null, null)).shutdown();
    }

    /**
     * Test {@link CloneService#awaitShutdown(long)}.
     * <p>
     * Method under test: {@link CloneService#awaitShutdown(long)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testAwaitShutdown() throws InterruptedException {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        // Act
        (new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef, volumeDiskStateEventRef,
                extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null)).awaitShutdown(10L);
    }

    /**
     * Test {@link CloneService#awaitShutdown(long)}.
     * <p>
     * Method under test: {@link CloneService#awaitShutdown(long)}
     */
    @Test
    public void testAwaitShutdown2() throws InterruptedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        // Act
        (new CloneService(errorReporterRef, null, ctrlStltSerializerRef, volumeDiskStateEventRef, extCmdFactoryRef,
                new ReentrantReadWriteLock(true), null, null)).awaitShutdown(10L);
    }

    /**
     * Test CloneInfo {@link CloneInfo#equals(Object)}.
     * <ul>
     *   <li>When other is same.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneInfo#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoEquals_whenOtherIsSame() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        CacheRscData<Resource> parentRef = new CacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref", null, null,
                new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, parentRef, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef3 = new HashSet<>();
        CacheRscData<Resource> parentRef2 = new CacheRscData<>(1, null, null, childrenRef3, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef4 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, parentRef2, childrenRef4, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        (new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE)).equals(new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE));
    }

    /**
     * Test CloneInfo {@link CloneInfo#getKind()}.
     * <p>
     * Method under test: {@link CloneInfo#getKind()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoGetKind() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.base/java.util.Objects.requireNonNull(Objects.java:209)
        //       at com.linbit.linstor.storage.data.AbsRscData.<init>(AbsRscData.java:79)
        //       at com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData.<init>(BCacheRscData.java:48)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, null, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        (new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE)).getKind();
    }

    /**
     * Test CloneInfo getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link CloneInfo#setCloneDaemon(CloneDaemon)}
     *   <li>{@link CloneInfo#getCloneDaemon()}
     *   <li>{@link CloneInfo#getCloneStrategy()}
     *   <li>{@link CloneInfo#getDstVlmData()}
     *   <li>{@link CloneInfo#getResourceName()}
     *   <li>{@link CloneInfo#getSrcVlmData()}
     *   <li>{@link CloneInfo#getStatus()}
     *   <li>{@link CloneInfo#getSuffix()}
     *   <li>{@link CloneInfo#toString()}
     * </ul>
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.clone.CloneService$CloneInfo.setCloneDaemon(CloneDaemon).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.dbdrivers.interfaces.LayerCacheRscDatabaseDriver.getIdDriver()" because "cacheRscDbDriverRef" is null
        //       at com.linbit.linstor.storage.data.adapter.cache.CacheRscData.<init>(CacheRscData.java:54)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        CloneInfo cloneInfo = null;
        CloneDaemon cloneDaemonRef = null;

        // Act
        cloneInfo.setCloneDaemon(cloneDaemonRef);
        CloneDaemon actualCloneDaemon = cloneInfo.getCloneDaemon();
        CloneStrategy actualCloneStrategy = cloneInfo.getCloneStrategy();
        VlmProviderObject<Resource> actualDstVlmData = cloneInfo.getDstVlmData();
        ResourceName actualResourceName = cloneInfo.getResourceName();
        VlmProviderObject<Resource> actualSrcVlmData = cloneInfo.getSrcVlmData();
        CloneStatus actualStatus = cloneInfo.getStatus();
        String actualSuffix = cloneInfo.getSuffix();
        String actualToStringResult = cloneInfo.toString();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link CloneService#CloneService(ErrorReporter, ControllerPeerConnector, CtrlStltSerializer, VolumeDiskStateEvent, ExtCmdFactory, ReadWriteLock, Provider, AccessContext)}.
     * <ul>
     *   <li>Then return not Started.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneService#CloneService(ErrorReporter, ControllerPeerConnector, CtrlStltSerializer, VolumeDiskStateEvent, ExtCmdFactory, ReadWriteLock, Provider, AccessContext)}
     */
    @Test
    public void testNewCloneService_thenReturnNotStarted() {
        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        // Act
        CloneService actualCloneService = new CloneService(errorReporterRef, null, ctrlStltSerializerRef,
                volumeDiskStateEventRef, extCmdFactoryRef, new ReentrantReadWriteLock(true), null, null);

        // Assert
        assertFalse(actualCloneService.isStarted());
        ServiceName serviceName = actualCloneService.SERVICE_NAME;
        assertEquals(serviceName, actualCloneService.getInstanceName());
        assertEquals(CloneService.SERVICE_INFO, actualCloneService.getServiceInfo());
        assertSame(serviceName, actualCloneService.getServiceName());
    }

    /**
     * Test CloneInfo {@link CloneInfo#compareTo(CloneInfo)} with {@code CloneInfo}.
     * <p>
     * Method under test: {@link CloneInfo#compareTo(CloneInfo)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoCompareToWithCloneInfo() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.base/java.util.Objects.requireNonNull(Objects.java:209)
        //       at com.linbit.linstor.storage.data.AbsRscData.<init>(AbsRscData.java:79)
        //       at com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData.<init>(BCacheRscData.java:48)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, null, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        CloneInfo cloneInfo = new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE);
        HashSet<AbsRscLayerObject<Resource>> childrenRef3 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef3 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef3 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef3 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef3 = new BCacheRscData<>(1, null, null, childrenRef3, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef3, bcacheVlmDbDriverRef3, vlmProviderObjectsRef3, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver3 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef2 = new BCacheVlmData<>(null, rscDataRef3, null, bcacheVlmdbDriver3,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef4 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef4 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef4 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef4 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef4 = new BCacheRscData<>(1, null, null, childrenRef4, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef4, bcacheVlmDbDriverRef4, vlmProviderObjectsRef4, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver4 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        cloneInfo.compareTo(new CloneInfo(srcVlmDataRef2,
                new BCacheVlmData<>(null, rscDataRef4, null, bcacheVlmdbDriver4, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE));
    }

    /**
     * Test CloneInfo {@link CloneInfo#CloneInfo(VlmProviderObject, VlmProviderObject, CloneStrategy)}.
     * <p>
     * Method under test: {@link CloneInfo#CloneInfo(VlmProviderObject, VlmProviderObject, DeviceHandler.CloneStrategy)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoNewCloneInfo() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.dbdrivers.interfaces.LayerCacheRscDatabaseDriver.getIdDriver()" because "cacheRscDbDriverRef" is null
        //       at com.linbit.linstor.storage.data.adapter.cache.CacheRscData.<init>(CacheRscData.java:54)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        CacheRscData<Resource> parentRef = new CacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref", null, null,
                new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, parentRef, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef3 = new HashSet<>();
        CacheRscData<Resource> parentRef2 = new CacheRscData<>(1, null, null, childrenRef3, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef4 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, parentRef2, childrenRef4, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE);

    }

    /**
     * Test CloneInfo {@link CloneInfo#setCloneStatus(boolean)}.
     * <ul>
     *   <li>When {@code false}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneInfo#setCloneStatus(boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCloneInfoSetCloneStatus_whenFalse() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.base/java.util.Objects.requireNonNull(Objects.java:209)
        //       at com.linbit.linstor.storage.data.AbsRscData.<init>(AbsRscData.java:79)
        //       at com.linbit.linstor.storage.data.adapter.bcache.BCacheRscData.<init>(BCacheRscData.java:48)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmDataRef = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, null, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        (new CloneInfo(srcVlmDataRef,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE)).setCloneStatus(false);
    }

    /**
     * Test {@link CloneService#doZFSCloneCleanup(ZfsData, String, boolean)}.
     * <p>
     * Method under test: {@link CloneService#doZFSCloneCleanup(ZfsData, String, boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testDoZFSCloneCleanup() throws StorageException {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        CloneService cloneService = new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef,
                volumeDiskStateEventRef, extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null);
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> parentRef = new BCacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        HashMap<VolumeNumber, VlmProviderObject<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        SatelliteLayerStorageRscDbDriver dbDriverRef3 = new SatelliteLayerStorageRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerStorageVlmDbDriver dbVlmDriverRef = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        StorageRscData<Resource> rscDataRef = new StorageRscData<>(1, parentRef, null, "Rsc Name Suffix Ref",
                vlmProviderObjectsRef2, dbDriverRef3, dbVlmDriverRef, new TransactionObjectFactory(null), null);

        SatelliteLayerStorageVlmDbDriver dbDriverRef4 = new SatelliteLayerStorageVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        cloneService.doZFSCloneCleanup(new ZfsData<>(null, rscDataRef, DeviceProviderKind.DISKLESS, null, dbDriverRef4,
                new TransactionObjectFactory(null), null), "Clone Name", true);
    }

    /**
     * Test getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link CloneService#getInstanceName()}
     *   <li>{@link CloneService#getServiceInfo()}
     *   <li>{@link CloneService#getServiceName()}
     *   <li>{@link CloneService#isStarted()}
     *   <li>{@link CloneService#setServiceInstanceName(ServiceName)}
     *   <li>{@link CloneService#start()}
     * </ul>
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGettersAndSetters() throws SystemServiceStartException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.clone.CloneService.setServiceInstanceName(ServiceName).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        CloneService cloneService = null;

        // Act
        ServiceName actualInstanceName = cloneService.getInstanceName();
        String actualServiceInfo = cloneService.getServiceInfo();
        ServiceName actualServiceName = cloneService.getServiceName();
        boolean actualIsStartedResult = cloneService.isStarted();
        ServiceName instanceNameRef = null;
        cloneService.setServiceInstanceName(instanceNameRef);
        cloneService.start();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link CloneService#isRunning(ResourceName, VolumeNumber, String)}.
     * <p>
     * Method under test: {@link CloneService#isRunning(ResourceName, VolumeNumber, String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testIsRunning() throws InvalidNameException, ValueOutOfRangeException {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        CloneService cloneService = new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef,
                volumeDiskStateEventRef, extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null);
        ResourceName rscName = new ResourceName("Res Name");

        // Act
        cloneService.isRunning(rscName, new VolumeNumber(10), "Suffix");
    }

    /**
     * Test {@link CloneService#startClone(VlmProviderObject, VlmProviderObject, CloneStrategy)}.
     * <p>
     * Method under test: {@link CloneService#startClone(VlmProviderObject, VlmProviderObject, CloneStrategy)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStartClone() throws StorageException {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        CloneService cloneService = new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef,
                volumeDiskStateEventRef, extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null);
        HashSet<AbsRscLayerObject<Resource>> childrenRef = new HashSet<>();
        CacheRscData<Resource> parentRef = new CacheRscData<>(1, null, null, childrenRef, "Rsc Name Suffix Ref", null, null,
                new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef2 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef = new HashMap<>();
        BCacheRscData<Resource> rscDataRef = new BCacheRscData<>(1, null, parentRef, childrenRef2, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef, bcacheVlmDbDriverRef, vlmProviderObjectsRef, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        BCacheVlmData<Resource> srcVlmData = new BCacheVlmData<>(null, rscDataRef, null, bcacheVlmdbDriver,
                new TransactionObjectFactory(null), null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef3 = new HashSet<>();
        CacheRscData<Resource> parentRef2 = new CacheRscData<>(1, null, null, childrenRef3, "Rsc Name Suffix Ref", null,
                null, new HashMap<>(), null, null);

        HashSet<AbsRscLayerObject<Resource>> childrenRef4 = new HashSet<>();
        SatelliteLayerBCacheRscDbDriver bcacheRscDbDriverRef2 = new SatelliteLayerBCacheRscDbDriver(
                new SatelliteLayerResourceIdDriver());
        SatelliteLayerBCacheVlmDbDriver bcacheVlmDbDriverRef2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());
        HashMap<VolumeNumber, BCacheVlmData<Resource>> vlmProviderObjectsRef2 = new HashMap<>();
        BCacheRscData<Resource> rscDataRef2 = new BCacheRscData<>(1, null, parentRef2, childrenRef4, "Rsc Name Suffix Ref",
                bcacheRscDbDriverRef2, bcacheVlmDbDriverRef2, vlmProviderObjectsRef2, new TransactionObjectFactory(null), null);

        SatelliteLayerBCacheVlmDbDriver bcacheVlmdbDriver2 = new SatelliteLayerBCacheVlmDbDriver(
                new SatelliteLayerResourceIdDriver());

        // Act
        cloneService.startClone(srcVlmData,
                new BCacheVlmData<>(null, rscDataRef2, null, bcacheVlmdbDriver2, new TransactionObjectFactory(null), null),
                CloneStrategy.LVM_THIN_CLONE);
    }

    /**
     * Test {@link CloneService#removeClone(ResourceName, VolumeNumber)}.
     * <p>
     * Method under test: {@link CloneService#removeClone(ResourceName, VolumeNumber)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRemoveClone() throws InvalidNameException, ValueOutOfRangeException {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        CloneService cloneService = new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef,
                volumeDiskStateEventRef, extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null);
        ResourceName rscName = new ResourceName("Res Name");

        // Act
        cloneService.removeClone(rscName, new VolumeNumber(10));
    }

    /**
     * Test {@link CloneService#setFailed(ResourceName, VolumeNumber, String)}.
     * <p>
     * Method under test: {@link CloneService#setFailed(ResourceName, VolumeNumber, String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testSetFailed() throws InvalidNameException, ValueOutOfRangeException {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        CloneService cloneService = new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef,
                volumeDiskStateEventRef, extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null);
        ResourceName rscName = new ResourceName("Res Name");

        // Act
        cloneService.setFailed(rscName, new VolumeNumber(10), "Suffix");
    }

    /**
     * Test {@link CloneService#notifyCloneStatus(ResourceName, VolumeNumber, boolean)}.
     * <ul>
     *   <li>When {@code true}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneService#notifyCloneStatus(ResourceName, VolumeNumber, boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNotifyCloneStatus_whenTrue() throws InvalidNameException, ValueOutOfRangeException {
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

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        VolumeDiskStateEvent volumeDiskStateEventRef = new VolumeDiskStateEvent(new GenericEvent<>(null));
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef2 = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        CloneService cloneService = new CloneService(errorReporterRef, controllerPeerConnectorRef, ctrlStltSerializerRef,
                volumeDiskStateEventRef, extCmdFactoryRef2, new ReentrantReadWriteLock(true), null, null);
        ResourceName rscName = new ResourceName("Res Name");

        // Act
        cloneService.notifyCloneStatus(rscName, new VolumeNumber(10), true);
    }
}
