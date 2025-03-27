package com.linbit.extproc;

import static org.junit.Assert.assertTrue;

import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.timer.CoreTimerImpl;
import org.junit.Test;

public class ExtCmdFactoryStltDiffblueTest {
    /**
     * Test {@link ExtCmdFactoryStlt#create()}.
     * <ul>
     *   <li>Then return SaveWithoutSharedLocks.</li>
     * </ul>
     * <p>
     * Method under test: {@link ExtCmdFactoryStlt#create()}
     */
    @Test
    public void testCreate_thenReturnSaveWithoutSharedLocks() {
        // Arrange
        CoreTimerImpl timerRef = new CoreTimerImpl();

        // Act and Assert
        assertTrue((new ExtCmdFactoryStlt(timerRef, new StderrErrorReporter("Module Name"), null)).create()
                .isSaveWithoutSharedLocks());
    }
}
