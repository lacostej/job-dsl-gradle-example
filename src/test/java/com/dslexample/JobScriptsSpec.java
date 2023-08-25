import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.jvnet.hudson.test.JenkinsRule;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import hudson.model.Item;
import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import com.dslexample.support.TestUtil;

public class JobScriptsSpec {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    private File outputDir = new File("./output/");

    @Test
    public void testJenkinsInitializationAndTermination() {
        Jenkins jenkins = jenkinsRule.jenkins;
        if(jenkins != null) {
            System.out.println("Jenkins initialization successful");
        } else {
            System.out.println("Jenkins instance is null after initialization");
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
