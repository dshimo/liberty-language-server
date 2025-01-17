package io.openliberty;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.lsp4j.WorkspaceFolder;
import org.junit.jupiter.api.Test;

import io.openliberty.tools.langserver.lemminx.models.feature.Feature;
import io.openliberty.tools.langserver.lemminx.services.FeatureService;
import io.openliberty.tools.langserver.lemminx.services.LibertyProjectsManager;
import io.openliberty.tools.langserver.lemminx.services.LibertyWorkspace;
import jakarta.xml.bind.JAXBException;

public class LibertyFeatureTest {
    
    @Test
    public void getInstalledFeaturesListTest() throws JAXBException {
        FeatureService fs = FeatureService.getInstance();
        File srcResourcesDir = new File("src/test/resources");
        File featureListFile = new File(srcResourcesDir, "featurelist-ol-23.0.0.1-beta.xml");
        
        // LibertyWorkspace must be initialized
        List<WorkspaceFolder> initList = new ArrayList<WorkspaceFolder>();
        initList.add(new WorkspaceFolder(srcResourcesDir.toURI().toString()));
        LibertyProjectsManager.getInstance().setWorkspaceFolders(initList);
        Collection<LibertyWorkspace> workspaceFolders = LibertyProjectsManager.getInstance().getLibertyWorkspaceFolders();
        assertTrue(workspaceFolders.size() == 1);

        LibertyWorkspace libWorkspace = workspaceFolders.iterator().next();

        List<Feature> installedFeatures = new ArrayList<Feature>();
        installedFeatures = fs.readFeaturesFromFeatureListFile(installedFeatures, libWorkspace, featureListFile);
        
        assertFalse(installedFeatures.isEmpty());
        assertTrue(installedFeatures.equals(libWorkspace.getInstalledFeatureList()));
        // Check that list contains a beta feature
        assertTrue(installedFeatures.removeIf(f -> (f.getName().equals("cdi-4.0"))));
    }
}
