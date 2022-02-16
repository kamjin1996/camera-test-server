package com.kam.camera.tester.service

import com.kam.camera.tester.bean.SocketRequest
import com.kam.camera.tester.bean.SocketRequestType
import com.kam.camera.tester.bean.SocketResponseType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

var log: Logger = LoggerFactory.getLogger(SocketRequestType::class.java)

fun SocketRequestType.handle(request: SocketRequest) {
    val ws = UidServersHolder.webSocketMap[request.uid]
    if (ws == null) {
        log.error("uid: ${request.uid} ws server not init")
        return
    }

    val imageHandle = UidServersHolder.imageHandleMap[request.uid] ?: ImageHandleServer(ws)
    when (this) {
        SocketRequestType.ImageBase64 -> imageHandle.savePhoto(request)
        SocketRequestType.ImageClear -> imageHandle.clearSavedPhoto(request)
    }
}

fun SocketResponseType.response(data: Any, vararg uid: String?) {
    uid.forEach { UidServersHolder.webSocketMap[it]?.sendMessageJson(this, data) }
}