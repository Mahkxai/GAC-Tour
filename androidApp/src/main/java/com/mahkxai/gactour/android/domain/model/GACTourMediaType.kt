package com.mahkxai.gactour.android.domain.model

enum class GACTourMediaType {
    IMAGE, VIDEO, AUDIO, TEXT;

    fun collectionName(): String {
        return when(this) {
            IMAGE -> "images"
            VIDEO -> "videos"
            AUDIO -> "audios"
            TEXT -> "texts"
        }
    }

    fun title(): String {
        return when(this) {
            IMAGE -> "Images"
            VIDEO -> "Videos"
            AUDIO -> "Audios"
            TEXT -> "Texts"
        }
    }
}