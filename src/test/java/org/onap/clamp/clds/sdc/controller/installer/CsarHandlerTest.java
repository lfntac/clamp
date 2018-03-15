/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.clamp.clds.sdc.controller.installer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.att.aft.dme2.internal.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.clamp.clds.exception.sdc.controller.CsarHandlerException;
import org.onap.clamp.clds.exception.sdc.controller.SdcArtifactInstallerException;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.openecomp.sdc.api.notification.IArtifactInfo;
import org.openecomp.sdc.api.notification.INotificationData;
import org.openecomp.sdc.api.results.IDistributionClientDownloadResult;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;

public class CsarHandlerTest {

    private static final String sdcFolder = "/tmp/csar-handler-tests";
    private static final String csarArtifactName = "testArtifact.csar";

    @AfterClass
    public static void removeAllFiles() throws IOException {
        // Do some cleanup
        Path path = Paths.get(sdcFolder + "/test-controller/" + csarArtifactName);
        Files.deleteIfExists(path);
    }

    @Test
    public void testConstructor() throws CsarHandlerException {
        IArtifactInfo serviceArtifact = Mockito.mock(IArtifactInfo.class);
        Mockito.when(serviceArtifact.getArtifactType()).thenReturn(CsarHandler.CSAR_TYPE);
        Mockito.when(serviceArtifact.getArtifactName()).thenReturn(csarArtifactName);
        List<IArtifactInfo> servicesList = new ArrayList<>();
        servicesList.add(serviceArtifact);
        INotificationData notifData = Mockito.mock(INotificationData.class);
        Mockito.when(notifData.getServiceArtifacts()).thenReturn(servicesList);
        CsarHandler csar = new CsarHandler(notifData, "test-controller", sdcFolder);
        assertEquals(sdcFolder + "/test-controller" + "/" + csarArtifactName, csar.getFilePath());
    }

    @Test(expected = CsarHandlerException.class)
    public void testFailingConstructor() throws CsarHandlerException {
        INotificationData notifData = Mockito.mock(INotificationData.class);
        Mockito.when(notifData.getServiceArtifacts()).thenReturn(new ArrayList<>());
        new CsarHandler(notifData, "test-controller", "/tmp/csar-handler-tests");
        fail("Exception should have been raised");
    }

    @Test
    public void testSave()
            throws SdcArtifactInstallerException, SdcToscaParserException, CsarHandlerException, IOException {
        IArtifactInfo serviceArtifact = Mockito.mock(IArtifactInfo.class);
        Mockito.when(serviceArtifact.getArtifactType()).thenReturn(CsarHandler.CSAR_TYPE);
        Mockito.when(serviceArtifact.getArtifactName()).thenReturn(csarArtifactName);
        List<IArtifactInfo> servicesList = new ArrayList<>();
        servicesList.add(serviceArtifact);
        INotificationData notifData = Mockito.mock(INotificationData.class);
        Mockito.when(notifData.getServiceArtifacts()).thenReturn(servicesList);
        CsarHandler csar = new CsarHandler(notifData, "test-controller", "/tmp/csar-handler-tests");
        IDistributionClientDownloadResult resultArtifact = Mockito.mock(IDistributionClientDownloadResult.class);
        Mockito.when(resultArtifact.getArtifactPayload()).thenReturn(
                IOUtils.toByteArray(ResourceFileUtil.getResourceAsStream("example/sdc/service-Simsfoimap0112.csar")));
        csar.save(resultArtifact);
        assertTrue((new File(sdcFolder + "/test-controller/" + csarArtifactName)).exists());
        assertEquals(csarArtifactName, csar.getArtifactElement().getArtifactName());
        assertNotNull(csar.getSdcCsarHelper());
    }
}
