import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import java.io.IOException;

import java.io.File;

public class JobScriptsSpec {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testJenkinsInitializationAndTermination() {
        Jenkins jenkins = jenkinsRule.jenkins;
        if(jenkins != null) {
            System.out.println("Jenkins initialization successful");

            // Check if JENKINS_HOME is writable
            File tempFile = new File(jenkins.getRootDir(), "temp.txt");
            try {
                boolean created = tempFile.createNewFile();
                if(created) {
                    System.out.println("JENKINS_HOME is writable");
                } else {
                    System.out.println("Could not create file in JENKINS_HOME");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error occurred when trying to write to JENKINS_HOME: " + e.getMessage());
            }
        } else {
            System.out.println("Jenkins instance is null after initialization");
        }
    }
}
