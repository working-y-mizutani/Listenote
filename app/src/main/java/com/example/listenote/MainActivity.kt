package com.example.listenote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.listenote.ui.theme.ListenoteTheme
import com.example.listenote.ui.top.TopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListenoteTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "top", // 開始画面
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Top画面のルート
                        composable("top") {
                            TopView(navController = navController)
                        }
                        // メモ編集画面のルート
                        composable(
                            "memo_edit/{notebookId}",
                            arguments = listOf(navArgument("notebookId") {
                                type = NavType.LongType
                            })
                        ) { backStackEntry ->
                            val notebookId = backStackEntry.arguments?.getLong("notebookId")
                            // TODO: MemoEditScreenにIDを渡して表示する
                            if (notebookId != null) {
                                // MemoEditScreen(notebookId = notebookId)
                                Text("メモ編集画面 (ID: $notebookId)") // 仮表示
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopView(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: TopViewModel = viewModel()
    val createdNotebookId by viewModel.createdNotebookId.collectAsState()

    // ファイルピッカーのランチャーを準備
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                // 永続的なアクセス許可を取得
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
                // ViewModelのメソッドを呼び出し
                viewModel.createNotebookFromUri(it)
            }
        }
    )

    // ノートブックが作成されたら画面遷移
    LaunchedEffect(createdNotebookId) {
        createdNotebookId?.let { id ->
            navController.navigate("memo_edit/$id")
            viewModel.onNavigationComplete()
        }
    }

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
            onClick = { /* TODO: ノート一覧画面へ遷移 */ },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp)
        ) {
            Text(text = "メモ一覧")
        }
    }
}