package com.netflix.spinnaker.gate.swagger

import com.netflix.spinnaker.gate.Main
import com.netflix.spinnaker.gate.security.GateSystemTest
import com.netflix.spinnaker.gate.security.YamlFileApplicationContextInitializer
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.ws.rs.core.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Slf4j
@GateSystemTest
@ContextConfiguration(
  classes = [Main],
  initializers = YamlFileApplicationContextInitializer
)
class GenerateSwagger extends Specification {

  @Autowired
  WebApplicationContext wac

  MockMvc mockMvc

  def setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build()
  }

  def "generate and write swagger spec to file"() {
    given:
    Boolean written = false

    when:
    mockMvc.perform(get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
      .andDo({ result ->
      log.info('Generating swagger spec and writing to "swagger.json".')
      FileUtils.writeStringToFile(new File('swagger.json'), result.getResponse().getContentAsString())
      written = true
    })

    then:
    written
  }
}
