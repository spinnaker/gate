/*
 * Copyright 2020 OpsMx, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.controllers

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.stream.Collectors

import com.netflix.spinnaker.gate.config.ServiceConfiguration
import com.netflix.spinnaker.gate.services.internal.OpsmxOesService
import com.netflix.spinnaker.security.AuthenticatedRequest

import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import okio.BufferedSink
import okio.Okio
import okio.Source
import retrofit.client.Response

@RequestMapping("/oes")
@RestController
@Slf4j
@ConditionalOnExpression('${services.opsmx.enabled:false}')
class OpsmxOesController {

  @Autowired
  OpsmxOesService opsmxOesService

  @Autowired
  ServiceConfiguration serviceConfiguration

  @Autowired
  OkHttpClient okHttpClient

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.GET)
  Object getOesResponse(@PathVariable("type") String type,
                        @PathVariable("source") String source,
                        @RequestParam(value = "isTreeView", required = false) boolean isTreeView,
                        @RequestParam(value = "isLatest", required = false) boolean isLatest,
                        @RequestParam(value = "applicationName", required = false) String applicationName,
                        @RequestParam(value = "chartId", required = false) Integer chartId,
                        @RequestParam(value = "imageSource", required = false) String imageSource,
                        @RequestParam(value = "accountName", required = false) String accountName,
                        @RequestParam(value = "startTime", required = false) String startTime,
                        @RequestParam(value = "endTime", required = false) String endTime) {

    return opsmxOesService.getOesResponse(type, source, isTreeView, isLatest,
            applicationName, chartId, imageSource, accountName, startTime, endTime)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getOesResponse4(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1) {

    return opsmxOesService.getOesResponse4(type, source, source1)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getOesResponse5(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2) {

    return opsmxOesService.getOesResponse5(type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getOesResponse6(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2,
                         @PathVariable("source3") String source3,
                         @RequestParam(value = "noOfDays", required = false) Integer noOfDays) {

    return opsmxOesService.getOesResponse6(type, source, source1, source2, source3, noOfDays)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.DELETE)
  Object deleteOesResponse(@PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @RequestParam(value = "accountName", required = false) String accountName) {

    return opsmxOesService.deleteOesResponse(type, source, accountName)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deleteOesResponse4(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1) {

    return opsmxOesService.deleteOesResponse4(type, source, source1)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.DELETE)
  Object deleteOesResponse5(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1,
                            @PathVariable("source2") String source2) {

    return opsmxOesService.deleteOesResponse5(type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.DELETE)
  Object deleteOesResponse6(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1,
                            @PathVariable("source2") String source2,
                            @PathVariable("source3") String source3) {

    return opsmxOesService.deleteOesResponse6(type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Add or Update dynamic account configured in Spinnaker", response = String.class )
  @RequestMapping(value = "/accountsConfig/addOrUpdateDynamicAccount", method = RequestMethod.POST)
  String addOrUpdateAccount(@RequestParam MultipartFile files, @RequestParam Map<String, String> postData) {
	String filename = files ? files.getOriginalFilename() : ''
	return addOrUpdateDynamicAccount(files, postData.get("postData"))
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.POST)
  Object postOesResponse(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @RequestParam(value = "isTreeView", required = false) boolean isTreeView,
                         @RequestParam(value = "isLatest", required = false) boolean isLatest,
                         @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse(type, source, isTreeView, isLatest, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postOesResponse4(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse4(type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postOesResponse5(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse5(type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postOesResponse6(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                          @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse6(type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.PUT)
  Object updateOesResponse(@PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @RequestBody Object data) {

    return opsmxOesService.updateOesResponse(type, source, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updateOesResponse4(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1,
                            @RequestBody Object data) {

    return opsmxOesService.updateOesResponse4(type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.PUT)
  Object updateOesResponse5(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1,
                            @PathVariable("source2") String source2,
                            @RequestBody Object data) {

    return opsmxOesService.updateOesResponse5(type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.PUT)
  Object updateOesResponse6(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1,
                            @PathVariable("source2") String source2,
                            @PathVariable("source3") String source3,
                            @RequestBody Object data) {

    return opsmxOesService.updateOesResponse6(type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Add or Update dynamic account configured in Spinnaker", response = String.class )
  @RequestMapping(value = "/accountsConfig/cloudProviders/addOrUpdateDynamicAccount", method = RequestMethod.POST)
  String addOrUpdateCloudProver(@RequestParam MultipartFile files, @RequestParam Map<String, String> postData) {
    String filename = files ? files.getOriginalFilename() : ''
    return addOrUpdateCloudProverAccount(files, postData.get("postData"))
  }

  @ApiOperation(value = "Add or Update Spinnaker")
  @RequestMapping(value = "/accountsConfig/spinnakerX509", method = RequestMethod.POST)
  Object addOrUpdateSpinnaker(@RequestParam MultipartFile files, @RequestParam Map<String, String> postData) {
    return addOrUpdateSpinnaker(files, postData.get("postData"))
  }

  @ApiOperation(value = "Add or Update spinnaker cloudprovider account configured in Spinnaker", response = String.class )
  @RequestMapping(value = "/accountsConfig/spinnaker/addOrUpdateCloudProviderAccount", method = RequestMethod.POST)
  String addOrUpdateSpinnakerCloudProver(@RequestParam MultipartFile files, @RequestParam Map<String, String> postData) {
    String filename = files ? files.getOriginalFilename() : ''
    return addOrUpdateSpinnakerCloudProverAccount(files, postData.get("postData"))
  }

  private String addOrUpdateSpinnakerCloudProverAccount(MultipartFile files, String data) {
    Map<String, Optional<String>> authenticationHeaders = AuthenticatedRequest.getAuthenticationHeaders();
    Map headersMap = new HashMap()
    authenticationHeaders.each { key, val ->
      if(val.isPresent())
        headersMap.putAt(key,val.get())
      else
        headersMap.putAt(key,"")
    }
    AuthenticatedRequest.propagate {
      def request = new Request.Builder()
        .url(serviceConfiguration.getServiceEndpoint("opsmx").url +"/oes/accountsConfig/spinnaker/addOrUpdateCloudProviderAccount")
        .headers(Headers.of(headersMap))
        .post(uploadFileOkHttp(data,files))
        .build()

      def response = okHttpClient.newCall(request).execute()
      return response.body()?.string() ?: "Unknown reason: " + response.code()
    }.call() as String
  }

  Object addOrUpdateSpinnaker(MultipartFile files, String data) {
    Map<String, Optional<String>> authenticationHeaders = AuthenticatedRequest.getAuthenticationHeaders();
    Map headersMap = new HashMap()
    authenticationHeaders.each { key, val ->
      if(val.isPresent())
        headersMap.putAt(key,val.get())
      else
        headersMap.putAt(key,"")
    }
    AuthenticatedRequest.propagate {
      def request = new Request.Builder()
        .url(serviceConfiguration.getServiceEndpoint("opsmx").url +"/oes/accountsConfig/spinnakerX509")
        .headers(Headers.of(headersMap))
        .post(uploadFileOkHttp(data,files))
        .build()

      def response = okHttpClient.newCall(request).execute()
      return response.body()?.string() ?: "Unknown reason: " + response.code()
    }.call() as Object
  }

  private String addOrUpdateCloudProverAccount(MultipartFile files, String data) {
    Map<String, Optional<String>> authenticationHeaders = AuthenticatedRequest.getAuthenticationHeaders();
    Map headersMap = new HashMap()
    authenticationHeaders.each { key, val ->
      if(val.isPresent())
        headersMap.putAt(key,val.get())
      else
        headersMap.putAt(key,"")
    }
    AuthenticatedRequest.propagate {
      def request = new Request.Builder()
        .url(serviceConfiguration.getServiceEndpoint("opsmx").url +"/oes/accountsConfig/cloudProviders/addOrUpdateDynamicAccount")
        .headers(Headers.of(headersMap))
        .post(uploadFileOkHttp(data,files))
        .build()

      def response = okHttpClient.newCall(request).execute()
      return response.body()?.string() ?: "Unknown reason: " + response.code()
    }.call() as String
  }

  private String addOrUpdateDynamicAccount(MultipartFile files, String data) {

	  Map<String, Optional<String>> authenticationHeaders = AuthenticatedRequest.getAuthenticationHeaders();
	  Map headersMap = new HashMap()
	  authenticationHeaders.each { key, val ->
		  if(val.isPresent())
		   headersMap.putAt(key,val.get())
		  else
		   headersMap.putAt(key,"")
	  }
	  AuthenticatedRequest.propagate {
		  def request = new Request.Builder()
			.url(serviceConfiguration.getServiceEndpoint("opsmx").url +"/oes/accountsConfig/addOrUpdateDynamicAccount")
			.headers(Headers.of(headersMap))
			.post(uploadFileOkHttp(data,files))
			.build()

		  def response = okHttpClient.newCall(request).execute()
		  return response.body()?.string() ?: "Unknown reason: " + response.code()
		}.call() as String
  }

  private okhttp3.RequestBody uploadFileOkHttp(String data, MultipartFile multiPartfile) throws IOException {

	  String fileName = multiPartfile.getOriginalFilename();
	  MultipartBody.Builder builder = new MultipartBody.Builder();
	  builder.setType(MultipartBody.FORM);
	  builder.addFormDataPart("files", fileName, new okhttp3.RequestBody() {
		  @Override
		  public MediaType contentType() {
			  return MediaType.parse("application/octet-stream");
		  }

		  @Override
		  public void writeTo(BufferedSink sink) throws IOException {
			  try {
				  Source source = Okio.source(multiPartfile.getInputStream());
				  Buffer buf = new Buffer();

				  long totalRead = 0;
				  long totalSize = multiPartfile.getSize();
				  long remaining = totalSize;

				  for (long readCount; (readCount = source.read(buf, 32000)) != -1;) {

					  totalRead += readCount;
					  remaining -= readCount;

					  sink.write(buf, readCount);
					  sink.flush();

				  }
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		  }
	  });
	  builder.addFormDataPart("postData", null, okhttp3.RequestBody.create(MediaType.parse("text/plain"), data));
	  return builder.build();
  }

  @ApiOperation(value = "download the manifest file")
  @GetMapping(value = "/accountsConfig/cloudProviders/manifestfile/{agentName}", produces = "application/octet-stream")
  @ResponseBody Object getDownloadManifestFile(@PathVariable("agentName") String agentName){

	Response response = opsmxOesService.manifestDownloadFile(agentName)
	InputStream inputStream = response.getBody().in()
	try {
	  byte [] manifestFile = IOUtils.toByteArray(inputStream)
	  HttpHeaders headers = new HttpHeaders()
	  headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
	  return ResponseEntity.ok().headers(headers).body(manifestFile)
	} finally{
	  if (inputStream!=null){
		inputStream.close()
	  }
	}
  }

}
