package com.linbit.linstor.core.devmgr;

import com.linbit.InvalidNameException;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo.ReadOnlyNode;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo.ReadOnlyStorPool;
import com.linbit.linstor.core.devmgr.StltReadOnlyInfo.ReadOnlyVlmProviderInfo;
import com.linbit.linstor.core.identifier.NodeName;
import com.linbit.linstor.core.identifier.ResourceName;
import com.linbit.linstor.core.identifier.SharedStorPoolName;
import com.linbit.linstor.core.identifier.SnapshotName;
import com.linbit.linstor.core.identifier.StorPoolName;
import com.linbit.linstor.core.identifier.VolumeNumber;
import com.linbit.linstor.core.objects.Resource;
import com.linbit.linstor.core.objects.SnapshotVolume;
import com.linbit.linstor.core.objects.SnapshotVolume.Key;
import com.linbit.linstor.core.objects.Volume;
import com.linbit.linstor.propscon.ReadOnlyProps;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.storage.interfaces.categories.resource.VlmProviderObject;
import com.linbit.linstor.storage.kinds.DeviceProviderKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class StltReadOnlyInfoDiffblueTest {
    /**
     * Test {@link StltReadOnlyInfo#StltReadOnlyInfo(Collection)}.
     * <ul>
     *   <li>Given {@code null}.</li>
     *   <li>Then return StorPoolReadOnlyInfoList size is one.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltReadOnlyInfo#StltReadOnlyInfo(Collection)}
     */
    @Test
    public void testNewStltReadOnlyInfo_givenNull_thenReturnStorPoolReadOnlyInfoListSizeIsOne() {
        // Arrange
        ArrayList<ReadOnlyStorPool> roStorPoolListRef = new ArrayList<>();
        roStorPoolListRef.add(null);

        // Act and Assert
        assertEquals(1, (new StltReadOnlyInfo(roStorPoolListRef)).getStorPoolReadOnlyInfoList().size());
    }

    /**
     * Test {@link StltReadOnlyInfo#StltReadOnlyInfo(Collection)}.
     * <ul>
     *   <li>When {@link ArrayList#ArrayList()}.</li>
     *   <li>Then return StorPoolReadOnlyInfoList Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltReadOnlyInfo#StltReadOnlyInfo(Collection)}
     */
    @Test
    public void testNewStltReadOnlyInfo_whenArrayList_thenReturnStorPoolReadOnlyInfoListEmpty() {
        // Arrange, Act and Assert
        assertTrue((new StltReadOnlyInfo(new ArrayList<>())).getStorPoolReadOnlyInfoList().isEmpty());
    }

    /**
     * Test {@link StltReadOnlyInfo#StltReadOnlyInfo(Collection)}.
     * <ul>
     *   <li>Given {@code null}.</li>
     *   <li>Then return StorPoolReadOnlyInfoList size is two.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltReadOnlyInfo#StltReadOnlyInfo(Collection)}
     */
    @Test
    public void testNewStltReadOnlyInfo_givenNull_thenReturnStorPoolReadOnlyInfoListSizeIsTwo() {
        // Arrange
        ArrayList<ReadOnlyStorPool> roStorPoolListRef = new ArrayList<>();
        roStorPoolListRef.add(null);
        roStorPoolListRef.add(null);

        // Act and Assert
        assertEquals(2, (new StltReadOnlyInfo(roStorPoolListRef)).getStorPoolReadOnlyInfoList().size());
    }

    /**
     * Test {@link StltReadOnlyInfo#getStorPoolReadOnlyInfoList()}.
     * <p>
     * Method under test: {@link StltReadOnlyInfo#getStorPoolReadOnlyInfoList()}
     */
    @Test
    public void testGetStorPoolReadOnlyInfoList() {
        // Arrange, Act and Assert
        assertTrue((new StltReadOnlyInfo(new ArrayList<>())).getStorPoolReadOnlyInfoList().isEmpty());
    }

    /**
     * Test ReadOnlyNode {@link ReadOnlyNode#equals(Object)}.
     * <ul>
     *   <li>When other is different.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyNode#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyNodeEquals_whenOtherIsDifferent() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        ReadOnlyNode copyFromResult = ReadOnlyNode.copyFrom(null, null);

        // Act
        copyFromResult.equals(ReadOnlyNode.copyFrom(null, null));
    }

    /**
     * Test ReadOnlyNode {@link ReadOnlyNode#equals(Object)}.
     * <ul>
     *   <li>When other is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyNode#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyNodeEquals_whenOtherIsNull() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        ReadOnlyNode.copyFrom(null, null).equals(null);
    }

    /**
     * Test ReadOnlyNode {@link ReadOnlyNode#equals(Object)}.
     * <ul>
     *   <li>When other is wrong type.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyNode#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyNodeEquals_whenOtherIsWrongType() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        ReadOnlyNode.copyFrom(null, null).equals("Different type to ReadOnlyNode");
    }

    /**
     * Test ReadOnlyNode {@link ReadOnlyNode#getReadOnlyProps(AccessContext)}.
     * <ul>
     *   <li>Given copyFrom {@code null} and {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyNode#getReadOnlyProps(AccessContext)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyNodeGetReadOnlyProps_givenCopyFromNullAndNull() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        ReadOnlyNode.copyFrom(null, null).getReadOnlyProps(null);
    }

    /**
     * Test ReadOnlyNode {@link ReadOnlyNode#getReadOnlyProps(AccessContext)}.
     * <ul>
     *   <li>Then return emptyRoProps.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyNode#getReadOnlyProps(AccessContext)}
     */
    @Test
    public void testReadOnlyNodeGetReadOnlyProps_thenReturnEmptyRoProps() throws InvalidNameException {
        // Arrange
        UUID uuidRef = UUID.randomUUID();
        NodeName nameRef = new NodeName("42");
        ReadOnlyPropsImpl propsRef = ReadOnlyPropsImpl.emptyRoProps();

        // Act and Assert
        assertSame(propsRef, (new ReadOnlyNode(uuidRef, nameRef, propsRef)).getReadOnlyProps(null));
    }

    /**
     * Test ReadOnlyNode getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link ReadOnlyNode#ReadOnlyNode(UUID, NodeName, ReadOnlyProps)}
     *   <li>{@link ReadOnlyNode#getName()}
     *   <li>{@link ReadOnlyNode#getUuid()}
     * </ul>
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyNodeGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.<init>(UUID, NodeName, ReadOnlyProps).
        //   The arrange section threw
        //   com.linbit.InvalidNameException: Domain name cannot contain character ' '
        //       at com.linbit.Checks.hostNameCheck(Checks.java:260)
        //       at com.linbit.linstor.core.identifier.NodeName.<init>(NodeName.java:17)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        UUID uuidRef = null;
        NodeName nameRef = null;
        ReadOnlyProps propsRef = null;

        // Act
        ReadOnlyNode actualReadOnlyNode = new ReadOnlyNode(uuidRef, nameRef, propsRef);
        NodeName actualName = actualReadOnlyNode.getName();
        UUID actualUuid = actualReadOnlyNode.getUuid();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#compareTo(ReadOnlyStorPool)} with {@code ReadOnlyStorPool}.
     * <p>
     * Method under test: {@link ReadOnlyStorPool#compareTo(ReadOnlyStorPool)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolCompareToWithReadOnlyStorPool() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        ReadOnlyStorPool copyFromResult = ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null);

        // Act
        copyFromResult.compareTo(ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null));
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#copyFrom(ReadOnlyNode, StorPool, AccessContext)}.
     * <ul>
     *   <li>When copyFrom {@code null} and {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyStorPool#copyFrom(ReadOnlyNode, StorPool, AccessContext)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolCopyFrom_whenCopyFromNullAndNull() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null);
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#copyFrom(ReadOnlyNode, StorPool, AccessContext)}.
     * <ul>
     *   <li>When {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyStorPool#copyFrom(ReadOnlyNode, StorPool, AccessContext)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolCopyFrom_whenNull() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.StorPool.getUuid()" because "storPoolRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyStorPool.copyFrom(StltReadOnlyInfo.java:78)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        ReadOnlyStorPool.copyFrom(null, null, null);
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#equals(Object)}.
     * <ul>
     *   <li>When other is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyStorPool#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolEquals_whenOtherIsNull() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null).equals(null);
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#equals(Object)}.
     * <ul>
     *   <li>When other is same.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyStorPool#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolEquals_whenOtherIsSame() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null)
                .equals(ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null));
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#equals(Object)}.
     * <ul>
     *   <li>When other is wrong type.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyStorPool#equals(Object)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolEquals_whenOtherIsWrongType() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null)
                .equals("Different type to ReadOnlyStorPool");
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#getReadOnlyNode()}.
     * <p>
     * Method under test: {@link ReadOnlyStorPool#getReadOnlyNode()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolGetReadOnlyNode() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null).getReadOnlyNode();
    }

    /**
     * Test ReadOnlyStorPool {@link ReadOnlyStorPool#getReadOnlyProps(AccessContext)}.
     * <p>
     * Method under test: {@link ReadOnlyStorPool#getReadOnlyProps(AccessContext)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolGetReadOnlyProps() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null).getReadOnlyProps(null);
    }

    /**
     * Test ReadOnlyStorPool getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link ReadOnlyStorPool#getDeviceProviderKind()}
     *   <li>{@link ReadOnlyStorPool#getName()}
     *   <li>{@link ReadOnlyStorPool#getReadOnlyVolumes()}
     *   <li>{@link ReadOnlyStorPool#getSharedStorPoolName()}
     *   <li>{@link ReadOnlyStorPool#getUuid()}
     * </ul>
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyStorPoolGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyStorPool.getDeviceProviderKind().
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ReadOnlyStorPool readOnlyStorPool = null;

        // Act
        DeviceProviderKind actualDeviceProviderKind = readOnlyStorPool.getDeviceProviderKind();
        StorPoolName actualName = readOnlyStorPool.getName();
        Collection<ReadOnlyVlmProviderInfo> actualReadOnlyVolumes = readOnlyStorPool.getReadOnlyVolumes();
        SharedStorPoolName actualSharedStorPoolName = readOnlyStorPool.getSharedStorPoolName();
        UUID actualUuid = readOnlyStorPool.getUuid();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test ReadOnlyVlmProviderInfo getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link ReadOnlyVlmProviderInfo#getDevicePath()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getIdentifier()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getOrigAllocatedSize()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getReadOnlyStorPool()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getResourceName()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getRscSuffix()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getSnapName()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getSnapVolumeKey()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getVlmNr()}
     *   <li>{@link ReadOnlyVlmProviderInfo#getVolumeKey()}
     * </ul>
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyVlmProviderInfoGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyVlmProviderInfo.getDevicePath().
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ReadOnlyVlmProviderInfo readOnlyVlmProviderInfo = null;

        // Act
        String actualDevicePath = readOnlyVlmProviderInfo.getDevicePath();
        String actualIdentifier = readOnlyVlmProviderInfo.getIdentifier();
        long actualOrigAllocatedSize = readOnlyVlmProviderInfo.getOrigAllocatedSize();
        ReadOnlyStorPool actualReadOnlyStorPool = readOnlyVlmProviderInfo.getReadOnlyStorPool();
        ResourceName actualResourceName = readOnlyVlmProviderInfo.getResourceName();
        String actualRscSuffix = readOnlyVlmProviderInfo.getRscSuffix();
        SnapshotName actualSnapName = readOnlyVlmProviderInfo.getSnapName();
        Key actualSnapVolumeKey = readOnlyVlmProviderInfo.getSnapVolumeKey();
        VolumeNumber actualVlmNr = readOnlyVlmProviderInfo.getVlmNr();
        Volume.Key actualVolumeKey = readOnlyVlmProviderInfo.getVolumeKey();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test ReadOnlyVlmProviderInfo {@link ReadOnlyVlmProviderInfo#ReadOnlyVlmProviderInfo(VlmProviderObject, ReadOnlyStorPool)}.
     * <p>
     * Method under test: {@link ReadOnlyVlmProviderInfo#ReadOnlyVlmProviderInfo(VlmProviderObject, ReadOnlyStorPool)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyVlmProviderInfoNewReadOnlyVlmProviderInfo() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.objects.Node.getUuid()" because "nodeRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyNode.copyFrom(StltReadOnlyInfo.java:197)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        new ReadOnlyVlmProviderInfo(null, ReadOnlyStorPool.copyFrom(ReadOnlyNode.copyFrom(null, null), null, null));

    }

    /**
     * Test ReadOnlyVlmProviderInfo {@link ReadOnlyVlmProviderInfo#ReadOnlyVlmProviderInfo(VlmProviderObject, ReadOnlyStorPool)}.
     * <ul>
     *   <li>When {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReadOnlyVlmProviderInfo#ReadOnlyVlmProviderInfo(VlmProviderObject, ReadOnlyStorPool)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testReadOnlyVlmProviderInfoNewReadOnlyVlmProviderInfo_whenNull() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.storage.interfaces.categories.resource.VlmProviderObject.getRscLayerObject()" because "vlmProvObjRef" is null
        //       at com.linbit.linstor.core.devmgr.StltReadOnlyInfo$ReadOnlyVlmProviderInfo.<init>(StltReadOnlyInfo.java:281)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        new ReadOnlyVlmProviderInfo(null, null);

    }
}
