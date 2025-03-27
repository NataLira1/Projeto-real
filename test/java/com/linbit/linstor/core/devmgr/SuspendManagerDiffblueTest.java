package com.linbit.linstor.core.devmgr;

import com.linbit.linstor.api.ApiCallRcImpl;
import com.linbit.linstor.core.devmgr.SuspendManager.ExceptionHandler;
import com.linbit.linstor.core.objects.Resource;
import com.linbit.linstor.layer.LayerFactory;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.security.AccessContext;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

public class SuspendManagerDiffblueTest {
    /**
     * Test getters and setters.
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link SuspendManager#SuspendManager(AccessContext, ErrorReporter, LayerFactory)}
     *   <li>{@link SuspendManager#setExceptionHandler(ExceptionHandler)}
     * </ul>
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.core.devmgr.SuspendManager.<init>(AccessContext, ErrorReporter, LayerFactory).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.StltConnTracker.addClosingListener(com.linbit.utils.ExceptionThrowingBiConsumer)" because "stltConnTracker" is null
        //       at com.linbit.linstor.core.ControllerPeerConnectorImpl.<init>(ControllerPeerConnectorImpl.java:98)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        AccessContext wrkCtxRef = null;
        ErrorReporter errorReporterRef = null;
        LayerFactory layerFactoryRef = null;

        // Act
        SuspendManager actualSuspendManager = new SuspendManager(wrkCtxRef, errorReporterRef, layerFactoryRef);
        ExceptionHandler excHandlerRef = null;
        actualSuspendManager.setExceptionHandler(excHandlerRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link SuspendManager#manageSuspendIo(Collection, boolean)} with {@code rscsRef}, {@code resumeOnlyRef}.
     * <p>
     * Method under test: {@link SuspendManager#manageSuspendIo(Collection, boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testManageSuspendIoWithRscsRefResumeOnlyRef() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        SuspendManager suspendManager = null;
        Collection<Resource> rscsRef = null;
        boolean resumeOnlyRef = false;

        // Act
        HashMap<Resource, ApiCallRcImpl> actualManageSuspendIoResult = suspendManager.manageSuspendIo(rscsRef,
                resumeOnlyRef);

        // Assert
        // TODO: Add assertions on result
    }
}
