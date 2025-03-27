package com.linbit.linstor.core.devmgr;

import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import org.junit.Ignore;
import org.junit.Test;

public class DevMgrModuleDiffblueTest {
    /**
     * Test {@link DevMgrModule#configure()}.
     * <p>
     * Method under test: {@link DevMgrModule#configure()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testConfigure() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalStateException: The binder can only be used inside configure()
        //       at com.google.common.base.Preconditions.checkState(Preconditions.java:512)
        //       at com.google.inject.AbstractModule.binder(AbstractModule.java:77)
        //       at com.google.inject.AbstractModule.bind(AbstractModule.java:98)
        //       at com.linbit.linstor.core.devmgr.DevMgrModule.configure(DevMgrModule.java:29)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        (new DevMgrModule()).configure();
    }

    /**
     * Test {@link DevMgrModule#deviceManagerContext(AccessContext)}.
     * <p>
     * Method under test: {@link DevMgrModule#deviceManagerContext(AccessContext)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testDeviceManagerContext() throws AccessDeniedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.security.AccessContext.clone()" because "systemCtx" is null
        //       at com.linbit.linstor.core.devmgr.DevMgrModule.deviceManagerContext(DevMgrModule.java:46)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        (new DevMgrModule()).deviceManagerContext(null);
    }
}
