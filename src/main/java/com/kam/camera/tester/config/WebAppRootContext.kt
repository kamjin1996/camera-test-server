package com.kam.camera.tester.config

import org.springframework.boot.web.servlet.ServletContextInitializer
import kotlin.Throws
import javax.servlet.ServletException
import javax.servlet.ServletContext
import org.springframework.web.util.WebAppRootListener
import org.springframework.context.annotation.Configuration

@Configuration
open class WebAppRootContext : ServletContextInitializer {

    @Throws(ServletException::class)
    override fun onStartup(servletContext: ServletContext) {
        servletContext.addListener(WebAppRootListener::class.java)
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", "1024000")
    }
}