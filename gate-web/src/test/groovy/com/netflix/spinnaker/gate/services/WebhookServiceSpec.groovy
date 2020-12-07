package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.services.internal.EchoService
import com.netflix.spinnaker.gate.services.internal.OrcaService
import com.netflix.spinnaker.gate.services.internal.OrcaServiceSelector
import com.netflix.spinnaker.kork.common.Header
import com.netflix.spinnaker.security.AuthenticatedRequest
import org.slf4j.MDC
import spock.lang.Specification
import spock.lang.Unroll

class WebhookServiceSpec extends Specification {

  EchoService echoService

  OrcaServiceSelector orcaServiceSelector

  OrcaService orcaService

  WebhookService service

  def setup() {
    echoService = Mock(EchoService)
    orcaServiceSelector = Mock(OrcaServiceSelector)
    orcaService = Mock()
    service = new WebhookService(echoService: echoService, orcaServiceSelector: orcaServiceSelector)
  }

  def cleanup() {
    MDC.clear()
  }

  @Unroll
  def 'should call webhooks endpoint for user (anonymous: #anonymous)'() {

    when:
    MDC.clear()
    MDC.put(Header.USER.header, user)
    Map result = service.webhooks('test', 'test', [:])

    then:
    noExceptionThrown()
    (1..2) * echoService.webhooks(_,_,_) >> { String type, String source, Map m ->
      assert type == 'test'
      assert source == 'test'
      assert m.isEmpty()
      return ['k': 'v']
    }
    result['k'] == 'v'
    AuthenticatedRequest.getSpinnakerUser().isPresent()  == !anonymous
    0 * _

    where:
    user                   ||  anonymous
    null                   ||  true
    "spinnaker-user"       ||  false
  }

  @Unroll
  def 'should call preconfigured webhooks endpoint for user (anonymous: #anonymous)'() {

    when:
    MDC.clear()
    MDC.put(Header.USER.header, user)
    List result = service.preconfiguredWebhooks()

    then:
    noExceptionThrown()
    (1..2) * orcaServiceSelector.select() >> { orcaService }
    (1..2) * orcaService.preconfiguredWebhooks() >> []
    result.isEmpty()
    AuthenticatedRequest.getSpinnakerUser().isPresent()  == !anonymous
    0 * _

    where:
    user                   ||  anonymous
    null                   ||  true
    "spinnaker-user"       ||  false
  }

}
