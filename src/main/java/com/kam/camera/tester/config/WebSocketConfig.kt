package com.kam.camera.tester.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.socket.server.standard.ServerEndpointExporter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class WebSocketConfig {

    @Value("\${img.saveDir}")
    lateinit var imageSaveDir: String

    @Bean
    open fun serverEndpointExporter() = ServerEndpointExporter()
}