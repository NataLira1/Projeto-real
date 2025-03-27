package com.linbit.linstor.core.apicallhandler.satellite.authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.linbit.linstor.api.ApiCallRcImpl;
import com.linbit.linstor.storage.kinds.ExtToolsInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class AuthenticationResultDiffblueTest {
    /**
     * Test getters and setters.
     * <ul>
     *   <li>When {@link ApiCallRcImpl#ApiCallRcImpl()}.</li>
     *   <li>Then return ExternalToolsInfoList is {@code null}.</li>
     * </ul>
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link AuthenticationResult#AuthenticationResult(ApiCallRcImpl)}
     *   <li>{@link AuthenticationResult#getApiCallRc()}
     *   <li>{@link AuthenticationResult#getExternalToolsInfoList()}
     *   <li>{@link AuthenticationResult#isAuthenticated()}
     * </ul>
     */
    @Test
    public void testGettersAndSetters_whenApiCallRcImpl_thenReturnExternalToolsInfoListIsNull() {
        // Arrange
        ApiCallRcImpl failedApiCallRcImplRef = new ApiCallRcImpl();

        // Act
        AuthenticationResult actualAuthenticationResult = new AuthenticationResult(failedApiCallRcImplRef);
        ApiCallRcImpl actualApiCallRc = actualAuthenticationResult.getApiCallRc();
        Collection<ExtToolsInfo> actualExternalToolsInfoList = actualAuthenticationResult.getExternalToolsInfoList();

        // Assert
        assertNull(actualExternalToolsInfoList);
        assertFalse(actualAuthenticationResult.isAuthenticated());
        assertSame(failedApiCallRcImplRef, actualApiCallRc);
    }

    /**
     * Test getters and setters.
     * <ul>
     *   <li>When {@link ArrayList#ArrayList()}.</li>
     *   <li>Then ExternalToolsInfoList return {@link List}.</li>
     * </ul>
     * <p>
     * Methods under test:
     * <ul>
     *   <li>{@link AuthenticationResult#AuthenticationResult(Collection, ApiCallRcImpl)}
     *   <li>{@link AuthenticationResult#getApiCallRc()}
     *   <li>{@link AuthenticationResult#getExternalToolsInfoList()}
     *   <li>{@link AuthenticationResult#isAuthenticated()}
     * </ul>
     */
    @Test
    public void testGettersAndSetters_whenArrayList_thenExternalToolsInfoListReturnList() {
        // Arrange
        ArrayList<ExtToolsInfo> extToolsInfoListRef = new ArrayList<>();
        ApiCallRcImpl apiCallRcImplRef = new ApiCallRcImpl();

        // Act
        AuthenticationResult actualAuthenticationResult = new AuthenticationResult(extToolsInfoListRef, apiCallRcImplRef);
        ApiCallRcImpl actualApiCallRc = actualAuthenticationResult.getApiCallRc();
        Collection<ExtToolsInfo> actualExternalToolsInfoList = actualAuthenticationResult.getExternalToolsInfoList();

        // Assert
        assertTrue(actualExternalToolsInfoList instanceof List);
        assertTrue(actualAuthenticationResult.isAuthenticated());
        assertSame(apiCallRcImplRef, actualApiCallRc);
        assertSame(extToolsInfoListRef, actualExternalToolsInfoList);
    }
}
