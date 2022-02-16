package com.kam.camera.tester.service

import com.kam.camera.tester.bean.SocketRequest
import com.kam.camera.tester.bean.SocketRequestType
import com.kam.camera.tester.bean.SocketResponseType
import java.lang.RuntimeException

fun SocketRequestType.handle(request: SocketRequest) {
    val ws =
        WebSocketServer.webSocketMap[request.uid] ?: throw RuntimeException("uid: ${request.uid} ws server not init")
    val imageHandle = ImageHandleServer.imageHandleMap[request.uid] ?: ImageHandleServer(ws)
    when (this) {
        SocketRequestType.ImageBase64 -> imageHandle.savePhoto(request)
        SocketRequestType.ImageClear -> imageHandle.clearSavedPhoto(request)
    }
}

fun SocketResponseType.response(data: Any, vararg uid: String?) {
    uid.forEach { WebSocketServer.webSocketMap[it]?.sendMessageJson(this, data) }
}