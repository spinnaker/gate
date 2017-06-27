#!/usr/bin/env groovy

@Grapes([
    @Grab(group='com.google.api-client', module='google-api-client', version='1.22.0'),
    @Grab(group = 'org.apache.httpcomponents', module = 'httpclient', version = '4.5.3'),
])

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import groovy.json.JsonOutput
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.apache.http.client.CookieStore

/**
 * This client executes an authenticated (and perhaps, authorized) request against Gate.
 */
class GateClient {
  String credential
  String accessToken
  CookieStore cookieStore = new BasicCookieStore()
  HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build()

  GateClient(String credential) {
    this.credential = credential
  }

  String requestAccessToken() {
    def cred = GoogleCredential.fromStream(new FileInputStream(new File(credential))).createScoped(["profile", "email"])
    cred.refreshToken()
    return cred.accessToken
  }

  String execute(String url) {
    def request = new HttpGet(url)
    if (!accessToken) {
      this.accessToken = requestAccessToken()
    }
    request.setHeader("Authorization", "Bearer ${accessToken}")

    def response = httpClient.execute(request)
    return EntityUtils.toString(response.getEntity())
  }
}


def cli = new CliBuilder(usage: "oauth.groovy --credential /path/to/credential url1 [url2...]")
cli.credential(argName: "pathToCred",
               args: 1,
               required: true,
               "Use this credential when obtaining the access token.")
cli.q(longOpt: "quiet", "Suppress extra readability text")
cli.u(longOpt: "ugly", "Disable pretty printing")


def options = cli.parse(args)
if (!options) {
  return
}

def client = new GateClient(options.credential)
for (String url : options.arguments()) {
  def result = client.execute(url)
  if (!options.q) {
    println "----------"
    println "Requested: ${url}"
    println "----------"
  }

  if (!options.u) {
    result = JsonOutput.prettyPrint(result)
  }
  println result
}
