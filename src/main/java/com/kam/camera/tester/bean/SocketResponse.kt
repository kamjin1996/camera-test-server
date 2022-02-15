package com.kam.camera.tester.bean

class SocketResponse() {

    var type: String? = null
    var data: Any? = null
}

enum class SocketResponseType {
    ImageSavedCount, ConnectSuccess
}