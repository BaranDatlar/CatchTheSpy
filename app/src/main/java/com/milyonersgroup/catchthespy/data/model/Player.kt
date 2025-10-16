package com.milyonersgroup.catchthespy.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Player(
    var id: String = "",
    var name: String = "",
    var isHost: Boolean = false,
    var isReady: Boolean = false,
    var isSpy: Boolean = false,
    var word: String = ""
) {
    // No-argument constructor required for Firebase
    constructor() : this("", "", false, false, false, "")
}
