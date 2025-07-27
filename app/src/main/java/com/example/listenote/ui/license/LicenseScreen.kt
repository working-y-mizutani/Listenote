package com.example.listenote.ui.license

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LicenseScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState) // これでColumn全体がスクロール可能になる
    ) {
        // ライブラリ名の表示
        Text(
            text = "ComposeReorderable",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        // 著作権情報の表示
        Text(
            text = "Copyright 2022 André Claßen",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Apache License 2.0 の条文
        Text(
            text = """
                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                   http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent(), // .trimIndent() でインデントを綺麗に整形
            fontSize = 12.sp,
            lineHeight = 16.sp // 行間を少し広げて読みやすくする
        )
    }
}