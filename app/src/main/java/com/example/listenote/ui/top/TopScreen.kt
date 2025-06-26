package com.example.listenote.ui.top

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun TopScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: TopViewModel = viewModel()
    val createdNotebookId by viewModel.createdNotebookId.collectAsState()

    // ファイルピッカーのランチャーを準備
    val launcher = rememberLauncherForActivityResult(
        // 何をするかを設定
        contract = ActivityResultContracts.OpenDocument(),
        // contractが終わったら何をするかを設定
        // 末尾ラムダでも書けるが、こっちのほうがわかりやすいため名前付き引数で書く
        onResult = { uri: Uri? ->
            uri?.let {
                // 永続的なアクセス許可を取得
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
                viewModel.createNotebookFromUri(it)
            }
        }
    )

    // ノートブックが作成されたら画面遷移するようLaunchedEffectを宣言
    LaunchedEffect(
        // createdNotebookId が変更されたら(=ノートブックが作成されたら) blockが実行される
        key1 = createdNotebookId,
        block = {
            createdNotebookId?.let { id ->
                navController.navigate("notebook/$id")
                viewModel.onNavigationComplete()
            }
        }
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = 200.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                // ファイルピッカーを起動
                launcher.launch(arrayOf("audio/*"))
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp)
        ) {
            Text(text = "新しいノートを作成")
        }

        Button(
            onClick = { navController.navigate("notebook_list") },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp)
        ) {
            Text(text = "ノート一覧")
        }
    }
}