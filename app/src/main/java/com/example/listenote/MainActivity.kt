package com.example.listenote

import android.app.Application
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
import com.example.listenote.player.AudioPlayerViewModel
import com.example.listenote.ui.memo_create_edit.MemoCreateEditScreen
import com.example.listenote.ui.memo_create_edit.MemoCreateEditViewModelFactory
import com.example.listenote.ui.notebook.NotebookScreen
import com.example.listenote.ui.notebook.NotebookViewModelFactory
import com.example.listenote.ui.theme.ListenoteTheme
import com.example.listenote.ui.top.TopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListenoteTheme {
                val navController = rememberNavController()
                val audioPlayerViewModel: AudioPlayerViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "top", // 開始画面
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Top画面のルート
                        // composableでNavHostに情報を登録
                        // navController.navigate(routeName)で {} 内の処理を実行
                        composable("top") {
                            TopView(navController = navController)
                        }
                        // メモ編集画面のルート
                        composable(
                            "notebook/{notebookId}",
                            arguments = listOf(navArgument("notebookId") {
                                type = NavType.LongType
                            })
                            // navController.navigate()が呼ばれると、この画面の情報がbackStackに置かれる。
                            // そして、このcomposableブロックが実行される際に、
                            // 引数の backStackEntry から arguments を取り出すことができる。
                        ) { backStackEntry ->
                            val notebookId = backStackEntry.arguments?.getLong("notebookId")
                            if (notebookId != null) {
                                NotebookScreen(
                                    viewModel = viewModel(
                                        factory = NotebookViewModelFactory(
                                            application = LocalContext.current.applicationContext as Application,
                                            notebookId = notebookId
                                        )
                                    ),
                                    navController = navController,
                                    audioPlayerViewModel = audioPlayerViewModel
                                )
                            }
                        }
                        composable(
                            // メモ新規作成の場合 memoIdはないためオプションにする
                            // 既存メモ編集の場合memoIdが渡される
                            route = "memo_create_edit/{notebookId}?memoId={memoId}&timestamp={timestamp}",
                            arguments = listOf(
                                navArgument("notebookId") { type = NavType.LongType },
                                navArgument("memoId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                },
                                navArgument("timestamp") {
                                    type = NavType.LongType
                                    defaultValue = 0L
                                },
                            )
                        ) { backStackEntry ->
                            val notebookId = backStackEntry.arguments?.getLong("notebookId")
                            val memoId = backStackEntry.arguments?.getLong("memoId")
                            val timestamp = backStackEntry.arguments?.getLong("timestamp")
                            if (notebookId != null && memoId != null && timestamp != null) {
                                MemoCreateEditScreen(
                                    navController = navController,
                                    audioPlayerViewModel = audioPlayerViewModel,
                                    viewModel = viewModel(
                                        factory = MemoCreateEditViewModelFactory(
                                            application = LocalContext.current.applicationContext as Application,
                                            notebookId = notebookId,
                                            memoId = memoId,
                                            timestamp = timestamp,
                                        )
                                    )
                                )
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
            onClick = { /* TODO: ノート一覧画面へ遷移 */ },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp)
        ) {
            Text(text = "メモ一覧")
        }
    }
}