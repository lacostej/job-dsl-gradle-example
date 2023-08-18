package com.dslexample;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import hudson.model.Item;
import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import jenkins.model.Jenkins;
import org.apache.commons.io.FileUtils;
import org.jvnet.hudson.test.JenkinsRule;
import javaposse.jobdsl.dsl.GeneratedJob;
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

public class JobScriptsSpec {
    @org.junit.Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    private File outputDir = new File("./build/debug-xml");

    @Before
    public void setup() throws Exception {
        FileUtils.deleteDirectory(outputDir);
        try {
            this.jenkinsRule.before();
        } catch (Throwable t) {
            System.out.println("Setup error");
            t.printStackTrace();
        }
    }

    @After
    public void cleanup() {
        try {
            this.jenkinsRule.after();
        } catch (Throwable t) {
            System.out.println("Cleanup error");
            t.printStackTrace();
        }
    }

    @Test
    public void testScriptFiles() {
        DslScriptLoader loader = new DslScriptLoader(new JenkinsJobManagement(System.out, Collections.emptyMap(), new File(".")));

        for (File file: TestUtil.getJobFiles()) {
            try {
                String scriptText = Files.readString(file.toPath());
                GeneratedItems items = loader.runScript(scriptText);
                writeItems(items.getJobs(), outputDir);
            } catch (Exception e) {
                System.out.println("Job file process error: " + file.getName());
                e.printStackTrace();
                Assert.fail("Job file process error: " + file.getName());
            }
        }
    }

    private void writeItems(Set<GeneratedJob> jobs, File outputDir) throws IOException {
        Jenkins jenkinInstance = jenkinsRule.jenkins;
        if(jenkinInstance == null) {
            System.out.println("Jenkins instance is null");
            return;
        }

        for (GeneratedJob generatedJob: jobs) {
            try {
                Item item = jenkinInstance.getItemByFullName(generatedJob.getJobName());
                if(item == null) {
                    System.out.println("Item is null for job: " + generatedJob.getJobName());
                    continue;
                }
                
                URL url = new URL(jenkinInstance.getRootUrl() + item.getUrl() + "config.xml");
                String text = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
                TestUtil.writeFile(new File(outputDir, "jobs"), generatedJob.getJobName(), text);
            } catch(Exception e) {
                System.out.println("Writing job error: " + generatedJob.getJobName());
                e.printStackTrace();
            }
        }
    }
}
