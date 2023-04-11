package com.imcys.delicacyselector

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imcys.delicacyselector.model.FoodInfo
import com.imcys.delicacyselector.model.view.HomeIntent
import com.imcys.delicacyselector.model.view.MainViewModel
import com.imcys.delicacyselector.ui.theme.DelicacySelectorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var mCoroutineScope: CoroutineScope
    private var mainViewModel = MainViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DelicacySelectorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //全局协程
                    mCoroutineScope = rememberCoroutineScope()
                    HomeView()
                }
            }
        }
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeView() {

        //顶部导航高度
        val appBarHeight = remember { mutableStateOf(0) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .statusBarsPadding()
                        .onSizeChanged { appBarHeight.value = it.height },
                    title = {
                        Text(text = "美味选择器")
                    })
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "侧滑按钮")
                        }

                        if (mainViewModel.viewStates.editFoodListState) {
                            IconButton(onClick = {
                                mCoroutineScope.launch {
                                    mainViewModel.sendIntent(HomeIntent.SetEditFoodList(!mainViewModel.viewStates.editFoodListState))
                                }
                            }) {
                                Icon(Icons.Rounded.Close, contentDescription = "编辑")
                            }
                        } else {
                            IconButton(onClick = {
                                mCoroutineScope.launch {
                                    mainViewModel.sendIntent(HomeIntent.SetEditFoodList(!mainViewModel.viewStates.editFoodListState))
                                }
                            }) {
                                Icon(Icons.Rounded.Edit, contentDescription = "编辑")
                            }
                        }

                        if (mainViewModel.viewStates.selectFoodState) {
                            IconButton(onClick = {
                                mCoroutineScope.launch {
                                    //点击切换选中状态
                                    mainViewModel.sendIntent(HomeIntent.SetSelectFoodState(!mainViewModel.viewStates.selectFoodState))
                                    if (mainViewModel.viewStates.selectFoodState) {
                                        mainViewModel.sendIntent(HomeIntent.ExtractFood)
                                    }
                                }
                            }) {
                                Icon(Icons.Rounded.Close, "暂停")
                            }
                        } else {
                            IconButton(onClick = {
                                mCoroutineScope.launch {
                                    //点击切换选中状态
                                    mainViewModel.sendIntent(HomeIntent.SetSelectFoodState(!mainViewModel.viewStates.selectFoodState))
                                    if (mainViewModel.viewStates.selectFoodState) {
                                        mainViewModel.sendIntent(HomeIntent.ExtractFood)
                                    }
                                }
                            }) {
                                Icon(Icons.Rounded.PlayArrow, "开始")
                            }
                        }


                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                mCoroutineScope.launch {
                                    mainViewModel.sendIntent(
                                        HomeIntent.SetAddFoodDialogState(
                                            true
                                        )
                                    )
                                }
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Rounded.Add, "Localized description")
                        }
                    }
                )


            }, content = {

                Box(modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(0, appBarHeight.value)
                        }
                    }
                    .padding(10.dp)) {

                    Column {
                        //设置主要内容
                        HomeContent()
                    }


                }
            }

        )
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun HomeContent() {

        initFoodList()
        //初始化添加食物弹窗
        initAddFoodDialog()

        Column {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "今天吃${mainViewModel.viewStates.selectFoodInfo.foodName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }

            var deleteFood: FoodInfo by remember {
                mutableStateOf(FoodInfo())
            }

            initDeleteFoodDialog(deleteFood)

            LazyVerticalStaggeredGrid(modifier = Modifier.padding(top = 20.dp),
                columns = StaggeredGridCells.Fixed(4),

                // item 和 item 之间的纵向间距
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                content = {


                    itemsIndexed(mainViewModel.viewStates.foodInfoList) { _, item ->
                        if (mainViewModel.viewStates.editFoodListState) {
                            InputChip(
                                selected = false,
                                onClick = {
                                    deleteFood = item
                                    mCoroutineScope.launch {
                                        mainViewModel.sendIntent(
                                            HomeIntent.SetDeleteFoodDialogState(
                                                true
                                            )
                                        )
                                    }
                                },
                                label = { Text(item.foodName) }
                            )
                        } else if (mainViewModel.viewStates.selectFoodInfo.id == item.id) {
                            ElevatedFilterChip(
                                selected = true,
                                onClick = {
                                },
                                label = { Text(item.foodName) },
                            )
                        } else {
                            ElevatedAssistChip(
                                onClick = {},
                                label = { Text(item.foodName) },
                            )
                        }

                    }
                })

        }
    }

    @Composable
    private fun initDeleteFoodDialog(deleteFood: FoodInfo) {
        if (mainViewModel.viewStates.deleteFoodDialogState) {
            AlertDialog(
                onDismissRequest = {
                    mCoroutineScope.launch {
                        mainViewModel.sendIntent(HomeIntent.SetDeleteFoodDialogState(false))
                    }
                },
                title = {
                    Text(text = "删除警告")
                },

                text = {
                    Text(text = "你确定要删除这个食物吗？")
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            mCoroutineScope.launch {
                                mainViewModel.sendIntent(HomeIntent.DeleteFood(deleteFood))
                                mainViewModel.sendIntent(
                                    HomeIntent.SetDeleteFoodDialogState(
                                        false
                                    )
                                )
                            }
                        }
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mCoroutineScope.launch {
                                mainViewModel.sendIntent(
                                    HomeIntent.SetDeleteFoodDialogState(
                                        false
                                    )
                                )
                            }
                        }
                    ) {
                        Text("点错了")
                    }
                }
            )
        }


    }


    @Composable
    private fun initFoodList() {
        LaunchedEffect(Unit) {
            mainViewModel.sendIntent(HomeIntent.GetFoodsInfo)
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun initAddFoodDialog() {
        var foodName by remember { mutableStateOf("") }

        if (mainViewModel.viewStates.addFoodDialog) {
            AlertDialog(
                onDismissRequest = {
                    mCoroutineScope.launch {
                        mainViewModel.sendIntent(HomeIntent.SetAddFoodDialogState(false))
                    }
                },
                title = {
                    Text(text = "添加一个食物")
                },

                text = {

                    TextField(
                        label = {
                            Text("食物名称")
                        },
                        value = foodName,
                        onValueChange = {
                            foodName = it
                        }

                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mCoroutineScope.launch {
                                mainViewModel.sendIntent(HomeIntent.AddFood(FoodInfo(foodName = foodName)))
                                mainViewModel.sendIntent(
                                    HomeIntent.SetAddFoodDialogState(
                                        false
                                    )
                                )
                                foodName = ""
                            }
                        }
                    ) {
                        Text("添加")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mCoroutineScope.launch {
                                mainViewModel.sendIntent(
                                    HomeIntent.SetAddFoodDialogState(
                                        false
                                    )
                                )
                            }
                        }
                    ) {
                        Text("不了吧")
                    }
                }
            )
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        DelicacySelectorTheme {
            mainViewModel = MainViewModel()
            HomeView()
        }
    }
}

