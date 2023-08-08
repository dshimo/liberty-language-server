package io.openliberty;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.openliberty.tools.langserver.lemminx.util.LibertyUtils;
import io.openliberty.tools.langserver.lemminx.util.XmlReader;

public class XmlReaderTest {
    File resourcesDir = new File("src/test/resources");

    @Test
    public void readEmptyXml() throws IOException {
        File emptyXml = new File(resourcesDir, "empty_server.xml");
        assertFalse(XmlReader.hasServerRoot(emptyXml.getCanonicalPath()));
        assertFalse(LibertyUtils.isServerXMLFile(emptyXml.getCanonicalPath()));
        assertFalse(LibertyUtils.isConfigXMLFile(emptyXml.getCanonicalPath()));
    }

    @Test
    public void readServerXml() throws IOException {
        File sampleServerXml = new File(resourcesDir, "sample/custom_server.xml");
        assertTrue(XmlReader.hasServerRoot(sampleServerXml.getCanonicalPath()));
        assertFalse(LibertyUtils.isServerXMLFile(sampleServerXml.getCanonicalPath()));
        assertFalse(LibertyUtils.isConfigDirFile(sampleServerXml.getCanonicalPath()));
        assertTrue(LibertyUtils.isConfigXMLFile(sampleServerXml.getCanonicalPath()));
    }

    @Test
    public void readLibertyPluginConfigXml() throws IOException {
        File lpcXml = new File(resourcesDir, "sample/liberty-plugin-config.xml");
        assertFalse(XmlReader.hasServerRoot(lpcXml.getCanonicalPath()));
        assertFalse(LibertyUtils.isConfigXMLFile(lpcXml.getCanonicalPath()));

        Set<String> elementNames = new HashSet<String> ();
        elementNames.add("configFile");
        elementNames.add("bootstrapPropertiesFile");
        elementNames.add("serverEnv");

        Map<String, String> values = XmlReader.getElementValues(lpcXml, elementNames);
        assertTrue(values.size() == 2, "Did not find expected number of elements in liberty-plugin-config.xml file. Expected 2, found "+values.size());

        assertTrue(values.containsKey("configFile"), "Expected configFile element not found");
        assertTrue(values.get("configFile").equals("/user/sample-project/src/main/liberty/config/server.xml"), "Expected configFile value not found. Value found: "+values.get("configFile"));

        assertTrue(values.containsKey("bootstrapPropertiesFile"), "Expected bootstrapPropertiesFile element not found");
        assertTrue(values.get("bootstrapPropertiesFile").equals("/user/sample-project/src/main/liberty/config/bootstrap.properties"), "Expected bootstrapPropertiesFile value not found. Value found: "+values.get("configFile"));

        assertFalse(values.containsKey("serverEnv"), "Unexpected serverEnv element found");
    }
}
