package com.dslexample;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import hudson.model.Item;
import hudson.model.View;
import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import jenkins.model.Jenkins;
import org.apache.commons.io.FileUtils;
import org.jvnet.hudson.test.JenkinsRule;
import javaposse.jobdsl.dsl.GeneratedJob;
import java.lang.reflect.Field;
import java.util.Set;

import org.junit.Before;
import org.junit.After;
import java.nio.file.Files;
import java.util.Collections;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import com.dslexample.support.TestUtil;
import org.junit.Assert;
import com.dslexample.support.TestUtil;

public class JobScriptsSpec {
    @org.junit.Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    private File outputDir = new File("./build/debug-xml");

    @Before
    public void setup() throws Exception {
        FileUtils.deleteDirectory(outputDir);
    }

    @After
    public void cleanup(){
    }

    
    @Test
    public void testScriptFiles() {
        DslScriptLoader loader = new DslScriptLoader(new JenkinsJobManagement(System.out, Collections.emptyMap(), new File(".")));

        for (File file : TestUtil.getJobFiles()) {
            try {
                String scriptText = Files.readString(file.toPath());

                // Run the script and get generated items.
                GeneratedItems items = loader.runScript(scriptText);

               writeItems(items.getJobs(), outputDir);

            } catch (Exception e) {
               Assert.fail("Failed to process " + file.getName());
            }
        }
    }


   /**
     * Write the config.xml for each generated job and view to the build dir.
     */
     private void writeItems(Set<GeneratedJob> jobs, File outputDir) throws IOException {
       Jenkins jenkinInstance = jenkinsRule.jenkins;

       for (GeneratedJob generatedJob : jobs) {
           Item item=jenkinInstance.getItemByFullName(generatedJob.getJobName());

           URL url=new URL(jenkinInstance.getRootUrl()+item.getUrl()+"config.xml");
           String text=new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);

           TestUtil.writeFile(new File(outputDir,"jobs"),generatedJob.getJobName(),text);
       }
    }
}
