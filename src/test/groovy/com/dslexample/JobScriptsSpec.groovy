package com.dslexample

import spock.lang.Specification
import spock.lang.Unroll

class JobScriptsSpec extends Specification {

    @Unroll
    void 'test script #file.name'(File file) {
        expect:
        System.out.println(file.name)

        where:
        file << new File('./src/jobs').listFiles().findAll { it.name.endsWith('.groovy') }
    }
}
