package com.graphicquest.ar.pipvideo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform