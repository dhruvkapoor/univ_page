package util

import play.api.Play
import scala.collection.mutable.HashMap

case class ConfigNotFoundException(reason: String) extends Exception(reason)

object Configurations {
  val configrationsMap = HashMap[String, String]()
  
  /**
   * Fetches configurations for the current environment - prod, dev, or test.
   * 
   * Configurations for an environment env should be prepended with 'env.', for instance, 'env.config = val'.
   * 
   * Test configurations, if absent, fall back to dev configurations. Any absent configurations for an environment fall back to 'bare' 
   * configurations. That is, if 'env.config' is absent, 'config' will be used. Entirely absent configurations trigger an exception.
   */
  def getConfig(key: String): String = {
    
    if (configrationsMap.contains(key))
      // The configuration has been cached in configrationsMap
      configrationsMap.getOrElse(key, null)
    else {
      // Fetch configuration and add to configurationsMap
      
      if (Play.isProd(Play.current) && hasLiteralConfigurationForEnv(key, "prod"))
        // For prod
        setValForKey(getLiteralConfigurationForEnv(key, "prod"), key)
      else if (Play.isTest(Play.current) && hasLiteralConfigurationForEnv(key, "test"))
        // For test
        setValForKey(getLiteralConfigurationForEnv(key, "test"), key)
      else if ((Play.isDev(Play.current) || Play.isTest(Play.current)) && hasLiteralConfigurationForEnv(key, "dev"))
        // For dev, and fall back for test
        setValForKey(getLiteralConfigurationForEnv(key, "dev"), key)
      else if (hasLiteralConfiguration(key))
        // Fall back
        setValForKey(getLiteralConfiguration(key), key)
      else
        throw new ConfigNotFoundException("Config not found for key " + key)
    }
  }
  
  private def setValForKey(value: String, key: String): String = {
    configrationsMap += key -> value
    value
  }
  
  private def hasLiteralConfigurationForEnv(key: String, env: String): Boolean = hasLiteralConfiguration(qualifyWithEnv(key, env))
  
  private def hasLiteralConfiguration(key: String): Boolean = Play.current.configuration.getString(key) != None
  
  private def getLiteralConfigurationForEnv(key: String, env: String): String = getLiteralConfiguration(qualifyWithEnv(key, env))
  
  private def getLiteralConfiguration(key: String): String = Play.current.configuration.getString(key).getOrElse("")
  
  private def qualifyWithEnv(key: String, env: String): String = env + "." + key
}
