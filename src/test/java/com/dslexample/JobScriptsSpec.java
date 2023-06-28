package com.dslexample;

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

    private JenkinsRule jenkinsRule = new JenkinsRule();
    private File outputDir = new File("./build/debug-xml");

    @Before
    public void setup() throws Exception {
        FileUtils.deleteDirectory(outputDir);
        this.jenkinsRule.before();  // Start the mock Jenkins environment.
    }

    @After
    public void cleanup() throws Exception {
        this.jenkinsRule.after();   // Stop the mock Jenkins environment.
    }
    
     @Test 
     public void testScriptFiles() { 
         for (File file : TestUtil.getJobFiles()) { 
             try {  
                 DslScriptLoader loader = new DslScriptLoader(new JenkinsJobManagement(System.out, Collections.emptyMap(), new File("."))); 

                 String scriptText = Files.readString(file.toPath());
                 GeneratedItems items = loader.runScript(scriptText); 

                 writeItems(items, outputDir);
                 
             } catch (Exception e) { Assert.fail("Failed to process " + file.getName()); } }
      }


   /**
     * Write the config.xml for each generated job and view to the build dir.
     */
   private void writeItems(GeneratedItems items, File outputDir) throws IOException {
    Jenkins jenkinInstance = jenkinsRule.jenkins; // Corrected here

       for (GeneratedJob generatedJob : items.getJobs()) {
           Field field = GeneratedJob.class.getDeclaredField("jobName");
           field.setAccessible(true);  // Make it accessible
           String jobName = (String) field.get(generatedJob);  // Get its value
           Item item=jenkinInstance.getItemByFullName(jobName);

           URL url=new URL(jenkinInstance.getRootUrl()+item.getUrl()+"config.xml");
           String text=new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);

           TestUtil.writeFile(new File(outputDir,"jobs"),jobName,text);
       }
       
       for(GeneratedView generatedView:items.views){
            String viewname=generatedView.name;  
            View view=jenkinInstance.getView(viewname);  

            URL url=new URL(jenkinInstance.getRootUrl()+view.getUrl()+"config.xml");   
            String text=new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);   

            TestUtil.writeFile(new File(outputDir,"views"),viewname,text);     
        }
        
   }
}
