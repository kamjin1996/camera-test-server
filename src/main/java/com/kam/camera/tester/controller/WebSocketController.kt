package com.kam.camera.tester.controller

import com.kam.camera.tester.service.WebSocketServer
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.stereotype.Controller
import java.io.IOException


@Controller
@RequestMapping("/checkcenter")
class WebSocketController {

    @ResponseBody
    @RequestMapping("/socket/push/{uid}")
    fun pushToWeb(@PathVariable uid: String?, message: String?): String {
        try {
            WebSocketServer.sendInfo(message!!, uid)
        } catch (e: IOException) {
            e.printStackTrace()
            return "failure"
        }
        return "success"
    }
}