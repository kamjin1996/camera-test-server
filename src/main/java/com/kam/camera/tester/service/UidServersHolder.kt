package com.kam.camera.tester.service

import java.util.concurrent.ConcurrentHashMap

class UidServersHolder {
    companion object {

        val webSocketMap = ConcurrentHashMap<String, WebSocketServer>()

        val imageHandleMap = ConcurrentHashMap<String, ImageHandleServer>()

        fun countOnline(): Int {
            return webSocketMap.count()
        }

        fun remove(uid: String) {
            with(uid) {
                webSocketMap.remove(this)
                imageHandleMap.remove(this)
            }
        }

        fun add(uid: String, ws: WebSocketServer) = ws.also { webSocketMap[uid] = it }

        fun add(uid: String, imageHandleServer: ImageHandleServer) = imageHandleServer.also { imageHandleMap[uid] = it }
    }
}