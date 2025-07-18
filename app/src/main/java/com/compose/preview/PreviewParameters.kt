package com.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.web.data.Music

class PreviewMusic : PreviewParameterProvider<Music> {
    override val values: Sequence<Music>
        get() = listOf(
            Music(
                "MusicName---------------------------------------------------------------------------------------------------------",
                "singer----------------------------------------------------------------------------------------------------------------------------",
                "path"
            ).apply {
                duration = (60 * 3 + 20) * 1000
            }
        ).asSequence()

}