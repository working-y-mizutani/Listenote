package com.example.listenote

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.listenote.player.PlayerUI

@Composable
fun MemoEditScreen(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
    ) {
        PlayerUI(
            audioResourceId = R.raw.test_my_song,

            )
    }


}
