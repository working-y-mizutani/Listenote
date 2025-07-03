package com.example.listenote

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.listenote.player.AudioPlayerViewModel
import com.example.listenote.ui.focus_todo.FocusToDoScreen
import com.example.listenote.ui.focus_todo.FocusToDoViewModelFactory
import com.example.listenote.ui.memo_create_edit.MemoCreateEditScreen
import com.example.listenote.ui.memo_create_edit.MemoCreateEditViewModelFactory
import com.example.listenote.ui.notebook.NotebookScreen
import com.example.listenote.ui.notebook.NotebookViewModelFactory
import com.example.listenote.ui.notebook_list.NotebookListScreen
import com.example.listenote.ui.theme.ListenoteTheme
import com.example.listenote.ui.todo_list.ToDoListScreen
import com.example.listenote.ui.todo_list.ToDoListViewModelFactory
import com.example.listenote.ui.top.TopScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListenoteTheme {
                val navController = rememberNavController()
                val audioPlayerViewModel: AudioPlayerViewModel = viewModel()
                ListenoteApp(navController, audioPlayerViewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListenoteApp(
    navController: NavHostController,
    audioPlayerViewModel: AudioPlayerViewModel
) {
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
                TopScreen(navController = navController)
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
            composable(route = "notebook_list") {
                NotebookListScreen(
                    navController = navController,
                )
            }

            composable(
                route = "todo_list/{notebookId}",
                arguments = listOf(navArgument("notebookId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val notebookId = backStackEntry.arguments?.getLong("notebookId")
                if (notebookId != null) {
                    ToDoListScreen(
                        navController = navController,
                        viewModel = viewModel(
                            factory = ToDoListViewModelFactory(
                                application = LocalContext.current.applicationContext as Application,
                                notebookId = notebookId
                            )
                        )
                    )
                }

            }

            composable(
                // ToDoListScreenからnotebookIdを受け取る
                route = "focus_todo_screen/{notebookId}",
                arguments = listOf(navArgument("notebookId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val notebookId = backStackEntry.arguments?.getLong("notebookId")
                if (notebookId != null) {
                    // これから作成するViewModelとScreenを指定
                    FocusToDoScreen(
                        navController = navController,
                        viewModel = viewModel(
                            factory = FocusToDoViewModelFactory(
                                application = LocalContext.current.applicationContext as Application,
                                notebookId = notebookId
                            )
                        )
                    )
                }
            }
        }
    }
}

