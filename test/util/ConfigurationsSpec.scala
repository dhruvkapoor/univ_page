package util

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication
import play.api.test.WithApplication

@RunWith(classOf[JUnitRunner])
class ConfigurationsSpec extends Specification {

  "Configurations" should {
    
    "throw an exception for a non-existent key" in new WithApplication {
      Configurations getConfig "invalid.key" must throwA[ConfigNotFoundException]
    }
    
    "return the right value for a valid key" in new WithApplication {
      Configurations getConfig "application.langs" must not beNull
    }
    
    "cache a previously accessed key" in new WithApplication {
      Configurations getConfig "application.langs"
      Configurations.configrationsMap.get("application.langs") must not beNone
    }
  }
}
