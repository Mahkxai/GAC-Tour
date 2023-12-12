package com.mahkxai.gactour

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform