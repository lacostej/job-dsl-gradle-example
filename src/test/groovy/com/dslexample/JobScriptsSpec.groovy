package com.dslexample

import com.dslexample.support.TestUtil
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.GeneratedItems
import javaposse.jobdsl.dsl.GeneratedJob
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification
import spock.lang.Unroll

class JobScriptsSpec extends Specification {

    @Unroll
    void 'test script #file.name'(File file) {
        given:
        JobManagement jm = Mock(JobManagement)

        when:
        GeneratedItems items = new DslScriptLoader(jm).runScript(file.text)
        writeItems(items, new File('output'))

        then:
        noExceptionThrown()

        where:
        file << TestUtil.getJobFiles()
    }

    /**
     * Write the config.xml for each generated job and view to the build dir.
     */
    private void writeItems(GeneratedItems items, File outputDir) {
        items.jobs.each { GeneratedJob generatedJob ->
            String jobName = generatedJob.jobName
            String xml = jm.getConfig(jobName)
            TestUtil.writeFile(new File(outputDir, 'jobs'), jobName, xml)
        }
    }
}
