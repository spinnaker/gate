/*
 * Copyright 2020 Netflix, Inc.
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

package com.netflix.spinnaker.gate.plugins.test

import com.netflix.spinnaker.gate.Main
import com.netflix.spinnaker.gate.plugins.GateApiExtension
import com.netflix.spinnaker.gate.plugins.GatePlugin
import com.netflix.spinnaker.kork.plugins.SpinnakerPluginManager
import com.netflix.spinnaker.kork.plugins.internal.PluginJar
import com.netflix.spinnaker.kork.plugins.tck.PluginsTckFixture
import java.io.File
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.context.annotation.Bean
import com.netflix.spinnaker.kork.jedis.EmbeddedRedis
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory
import redis.clients.jedis.JedisPool

class GatePluginsFixture : PluginsTckFixture, GateTestService() {

  final override val plugins = File("build/plugins")

  final override val enabledPlugin: PluginJar
  final override val disabledPlugin: PluginJar
  final override val versionNotSupportedPlugin: PluginJar

  override val extensionClassNames: MutableList<String> = mutableListOf(
    GateApiExtension::class.java.name
  )

  final override fun buildPlugin(pluginId: String, systemVersionRequirement: String): PluginJar {
    return PluginJar.Builder(plugins.toPath().resolve("$pluginId.jar"), pluginId)
      .pluginClass(GatePlugin::class.java.name)
      .pluginVersion("1.0.0")
      .manifestAttribute("Plugin-Requires", "gate$systemVersionRequirement")
      .extensions(extensionClassNames)
      .build()
  }

  @Autowired
  override lateinit var spinnakerPluginManager: SpinnakerPluginManager

  @Autowired
  lateinit var mockMvc: MockMvc

  init {
    plugins.delete()
    plugins.mkdir()
    enabledPlugin = buildPlugin("com.netflix.gate.enabled.plugin", ">=1.0.0")
    disabledPlugin = buildPlugin("com.netflix.gate.disabled.plugin", ">=1.0.0")
    // Make it very unlikely that the version of gate satisfies this requirement
    versionNotSupportedPlugin = buildPlugin("com.netflix.gate.version.not.supported.plugin", "=0.0.9")
  }
}

@SpringBootTest(classes = [Main::class])
@ContextConfiguration(classes = [PluginTestConfiguration::class])
@TestPropertySource(properties = ["spring.config.location=classpath:gate-plugins-test.yml"])
@AutoConfigureMockMvc
abstract class GateTestService

@TestConfiguration
internal open class PluginTestConfiguration {
  @Bean(destroyMethod = "destroy")
  fun embeddedRedis(): EmbeddedRedis {
    return EmbeddedRedis.embed().also { redis -> redis.jedis.connect() }.also { redis -> redis.jedis.ping() }
  }

  @Bean
  @Primary
  @SpringSessionRedisConnectionFactory
  fun jedisConnectionFactory(embeddedRedis: EmbeddedRedis): JedisConnectionFactory {
    return JedisConnectionFactory(RedisStandaloneConfiguration(embeddedRedis.host, embeddedRedis.port))
  }

  @Bean
  @Primary
  fun jedis(embeddedRedis: EmbeddedRedis): JedisPool {
    return embeddedRedis.getPool();
  }
}
