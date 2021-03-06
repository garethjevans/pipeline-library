

import org.junit.Before
import org.junit.Test
import groovy.mock.interceptor.StubFor

import io.jenkins.infra.*
import com.lesfurets.jenkins.unit.declarative.*

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class UpdatecliStepTests extends BaseTest {
  static final String scriptName = "vars/updatecli.groovy"
  Map env = [:]

  @Override
  @Before
  void setUp() throws Exception {
    super.setUp()

    // Mock Pipeline method which are not already declared in the parent class
    helper.registerAllowedMethod('libraryResource', [String.class], { '' })
  }

  @Test
  void itRunSuccessfullyWithDefault() throws Exception {
    def script = loadScript(scriptName)

    // when calling with the "updatecli" function with default configuration
    script.call()
    printCallStack()

    // Then we expect a successful build
    assertJobStatusSuccess()

    // With the static files read as expected
    assertTrue(assertMethodCallContainsPattern('libraryResource','io/jenkins/infra/updatecli/pod-template.yml'))

    // And the repository checkouted
    assertTrue(assertMethodCallContainsPattern('checkout', ''))

    // And only the diff command called with default values
    assertTrue(assertMethodCallContainsPattern('sh','updatecli diff --config ./updatecli/updatecli.d --values ./updatecli/values.yaml'))
    assertFalse(assertMethodCallContainsPattern('sh','updatecli apply'))
  }

  @Test
  void itRunSuccessfullyWithCustomAction() throws Exception {
    def script = loadScript(scriptName)

    // when calling with the "updatecli" function with a custom action "eat"
    script.call(action: 'eat')
    printCallStack()

    // Then we expect a successful build
    assertJobStatusSuccess()

    // With the static files read as expected
    assertTrue(assertMethodCallContainsPattern('libraryResource','io/jenkins/infra/updatecli/pod-template.yml'))

    // And the repository checkouted
    assertTrue(assertMethodCallContainsPattern('checkout',''))

    // And only the custom command called with default values
    assertFalse(assertMethodCallContainsPattern('sh','updatecli diff --config ./updatecli/updatecli.d --values ./updatecli/values.yaml'))
    assertTrue(assertMethodCallContainsPattern('sh','updatecli eat --config ./updatecli/updatecli.d --values ./updatecli/values.yaml'))
  }

  @Test
  void itRunSuccessfullyWithCustomConfigAndEmptyValues() throws Exception {
    def script = loadScript(scriptName)

    // when calling with the "updatecli" function with a custom config and an empty values
    script.call(config: './ops/config.yml', values: '')
    printCallStack()

    // Then we expect a successful build
    assertJobStatusSuccess()

    // With the static files read as expected
    assertTrue(assertMethodCallContainsPattern('libraryResource','io/jenkins/infra/updatecli/pod-template.yml'))

    // And the repository checkouted
    assertTrue(assertMethodCallContainsPattern('checkout',''))

    // And only the default command called with custom config and NO values
    assertTrue(assertMethodCallContainsPattern('sh','updatecli diff --config ./ops/config.yml'))
    assertFalse(assertMethodCallContainsPattern('sh','--values'))
  }

  @Test
  void itRunSuccessfullyWithEmptyConfigAndCustomValues() throws Exception {
    def script = loadScript(scriptName)

    // when calling with the "updatecli" function with a custom config and an empty values
    script.call(values: './values.yaml', config: '')
    printCallStack()

    // Then we expect a successful build
    assertJobStatusSuccess()

    // With the static files read as expected
    assertTrue(assertMethodCallContainsPattern('libraryResource','io/jenkins/infra/updatecli/pod-template.yml'))

    // And the repository checkouted
    assertTrue(assertMethodCallContainsPattern('checkout',''))

    // And only the default command called with custom config and NO values
    assertTrue(assertMethodCallContainsPattern('sh','updatecli diff --values ./values.yaml'))
    assertFalse(assertMethodCallContainsPattern('sh','--config'))
  }
}
