package com.kam.camera.tester.config

import org.springframework.web.socket.server.standard.ServerEndpointExporter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class WebSocketConfig {

    @Bean
    open fun serverEndpointExporter() = ServerEndpointExporter()
}