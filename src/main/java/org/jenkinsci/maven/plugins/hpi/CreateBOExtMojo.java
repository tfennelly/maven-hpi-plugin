/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenkinsci.maven.plugins.hpi;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.io.File;
import java.util.Arrays;

/**
 * Builds a new Blue Ocean extension plugin template.
 */
@Mojo(name="createboext", requiresProject = false)
public class CreateBOExtMojo extends AbstractCreateMojo {
    
    @Parameter(property = "blueOceanVersion")
    String blueOceanVersion;
    
    public void execute() throws MojoExecutionException {
        if(blueOceanVersion == null) {
            try {
                blueOceanVersion = prompter.prompt("Enter the Blue Ocean version");
            } catch (PrompterException e) {
                throw new MojoExecutionException("Failed to create a new Jenkins plugin",e);
            }
        }

        super.execute();
        
        try {
            File outDir = getOutDir();
            
            // copy view resource files. So far maven archetype doesn't seem to be able to handle it.
            FileUtils.deleteDirectory(new File(outDir, "src" + sep + "main" + sep + "java"));
            
            copyResources(Arrays.asList("package.json", "gulpfile.js"), "/archetype-resources/", outDir);

            File jsDir = new File(outDir, "src" + sep + "main" + sep + "js");
            copyResources(Arrays.asList("plugin.js"), "/archetype-resources/src/main/js/", jsDir);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to create a new Jenkins plugin",e);
        }
    }

    @Override
    protected String getDependenciesPOMFrag() {
        return String.format("<dependencies>\n" +
                "    <dependency>\n" +
                "      <groupId>io.jenkins.blueocean</groupId>\n" +
                "      <artifactId>blueocean</artifactId>\n" +
                "      <version>%s</version>\n" +
                "    </dependency>\n" +
                "  </dependencies>\n", blueOceanVersion);
    }
}
