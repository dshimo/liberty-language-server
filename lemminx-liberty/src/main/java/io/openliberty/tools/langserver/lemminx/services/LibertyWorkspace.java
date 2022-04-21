/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package io.openliberty.tools.langserver.lemminx.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.openliberty.tools.langserver.lemminx.models.feature.Feature;
import io.openliberty.tools.langserver.lemminx.util.LibertyConstants;
import io.openliberty.tools.langserver.lemminx.util.LibertyUtils;

public class LibertyWorkspace {

    private String workspaceFolderURI;
    private String libertyVersion;
    private boolean isLibertyInstalled;
    private List<Feature> installedFeatureList;
    private Set<String> configFiles;

    /**
     * Model of a Liberty Workspace. Each workspace indicates the
     * workspaceFolderURI, the Liberty version associated (may be cached), and if an
     * installed Liberty instance has been detected.
     * 
     * @param workspaceFolderURI
     */
    public LibertyWorkspace(String workspaceFolderURI) {
        this.workspaceFolderURI = workspaceFolderURI;
        this.libertyVersion = null;
        this.isLibertyInstalled = false;
        this.installedFeatureList = new ArrayList<Feature>();

        this.configFiles = new HashSet<String>();
        initConfigFileList();
    }

    public String getWorkspaceString() {
        return this.workspaceFolderURI;
    }
    
    public URI getWorkspaceURI() {
        return URI.create(this.workspaceFolderURI);
    }

    public void setLibertyVersion(String libertyVersion) {
        this.libertyVersion = libertyVersion;
    }

    public String getLibertyVersion() {
        return this.libertyVersion;
    }

    public void setLibertyInstalled(boolean isLibertyInstalled) {
        this.isLibertyInstalled = isLibertyInstalled;
    }

    public boolean isLibertyInstalled() {
        return this.isLibertyInstalled;
    }

    public List<Feature> getInstalledFeatureList() {
        return this.installedFeatureList;
    }

    public void setInstalledFeatureList(List<Feature> installedFeatureList) {
        this.installedFeatureList = installedFeatureList;
    }

    public void initConfigFileList() {
        try {
            List<Path> configPathsList = Files.find(Paths.get(getWorkspaceURI()), Integer.MAX_VALUE, (filePath, fileAttributes) -> 
                    LibertyUtils.isServerXMLFile(filePath.toString()) || isConfigDir(filePath, fileAttributes))
                    .collect(Collectors.toList());
            for (Path configPath : configPathsList) {
                if (LibertyUtils.isServerXMLFile(configPath.toString())) {
                    scanXMLforInclude(configPath);
                } else {
                    Files.list(configPath).forEach(path -> configFiles.add(path.toString()));
                }
            }
        } catch (IOException e) {
            // workspace URI does not exist
            e.printStackTrace();
        }
    }

    public void scanXMLforInclude(Path filePath) {
        try {
            String content = new String(Files.readAllBytes(filePath));
            Matcher m = Pattern.compile("<include[^<]+location=[\"\'](.+)[\"\']/>").matcher(content);
            while (m.find()) {
                configFiles.add(filePath.getParent().resolve(m.group(1)).toFile().getCanonicalPath());
            }
        } catch (IOException e) {
            // specified config resources file does not exist
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConfigDir(Path filePath, BasicFileAttributes fileAttributes) {
        if (!fileAttributes.isDirectory()) {
            return false;
        }
        return filePath.endsWith(LibertyConstants.WLP_USER_CONFIG_DIR) || 
                filePath.endsWith(LibertyConstants.SERVER_CONFIG_DROPINS_DEFAULTS) || 
                filePath.endsWith(LibertyConstants.SERVER_CONFIG_DROPINS_OVERRIDES);
    }

    public void addConfigFile(String fileString) {
        configFiles.add(fileString);
    }

    public boolean hasConfigFile(String fileString) {
        try {
            fileString = fileString.startsWith("file:") ? 
                    new File(URI.create(fileString)).getCanonicalPath() : 
                    new File(fileString).getCanonicalPath();
            return this.configFiles.contains(fileString);
        } catch (IOException e) {
            return false;
        }
    }
}
