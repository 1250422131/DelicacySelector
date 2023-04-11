package com.imcys.delicacyselector.model.view

import androidx.lifecycle.viewModelScope
import com.imcys.delicacyselector.app.App
import com.imcys.delicacyselector.data.repository.FoodRepository
import com.imcys.delicacyselector.info.UiIntent
import com.imcys.delicacyselector.info.UiState
import com.imcys.delicacyselector.model.FoodInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


sealed interface HomeIntent : UiIntent {
    /**
     * 获取食物列表
     */
    object GetFoodsInfo : HomeIntent

    /**
     * 添加食品弹窗状态
     * @property boolean Boolean
     * @constructor
     */
    data class SetAddFoodDialogState(val boolean: Boolean = false) : HomeIntent

    /**
     * 添加食物
     * @property foodInfo FoodInfo
     * @constructor
     */
    data class AddFood(val foodInfo: FoodInfo) : HomeIntent

    /**
     * 设置选择食物状态
     * @property boolean Boolean
     * @constructor
     */
    data class SetSelectFoodState(val boolean: Boolean) : HomeIntent

    /**
     * 设置选中的食物
     * @property foodInfo FoodInfo
     * @constructor
     */
    data class SetSelectFood(val foodInfo: FoodInfo) : HomeIntent

    /**
     * 开始抽签
     */
    object ExtractFood : HomeIntent

    data class SetEditFoodList(val boolean: Boolean) : HomeIntent
    data class DeleteFood(val foodInfo: FoodInfo) : HomeIntent
    data class SetDeleteFoodDialogState(val boolean: Boolean) : HomeIntent
}

data class HomeViewState(
    var foodInfoList: MutableList<FoodInfo> = mutableListOf(),
    var addFoodDialog: Boolean = false,
    var selectFoodInfo: FoodInfo = FoodInfo(),
    var selectFoodState: Boolean = false,
    var editFoodListState: Boolean = false,
    var deleteFoodDialogState: Boolean = false,

    ) : UiState


class MainViewModel : ComposeBaseViewModel<HomeViewState, HomeIntent>(HomeViewState()) {


    override fun handleEvent(event: HomeIntent, state: HomeViewState) {
        when (event) {
            is HomeIntent.GetFoodsInfo -> getFoods()
            is HomeIntent.SetAddFoodDialogState -> setAddFoodDialogState(event.boolean)
            is HomeIntent.AddFood -> addFood(event.foodInfo)
            is HomeIntent.SetSelectFoodState -> setSelectFoodState(event.boolean)
            is HomeIntent.SetSelectFood -> setSelectFood(event.foodInfo)
            is HomeIntent.ExtractFood -> extractFood()
            is HomeIntent.SetEditFoodList -> setEditFoodList(event.boolean)
            is HomeIntent.DeleteFood -> deleteFood(event.foodInfo)
            is HomeIntent.SetDeleteFoodDialogState -> setDeleteFoodDialogState(event.boolean)
        }
    }

    private fun setDeleteFoodDialogState(boolean: Boolean) {
        viewStates = viewStates.copy(deleteFoodDialogState = boolean)
    }

    private fun deleteFood(foodInfo: FoodInfo) {
        viewModelScope.launch {
            val foodDao = App.appDatabase.foodDao()
            FoodRepository(foodDao).delete(foodInfo)
            //刷新页面
            getFoods()
        }
    }

    private fun setEditFoodList(boolean: Boolean) {

        viewStates = viewStates.copy(editFoodListState = boolean)

    }

    private fun extractFood() {
        viewModelScope.launch(Dispatchers.Main) {
            repeat(20) {
                delay(130)
                viewStates =
                    viewStates.copy(selectFoodInfo = viewStates.foodInfoList[(0 until viewStates.foodInfoList.size).random()])
            }
            viewStates = viewStates.copy(selectFoodState = false)
        }
    }

    private fun setSelectFood(foodInfo: FoodInfo) {
        viewStates = viewStates.copy(selectFoodInfo = foodInfo)
    }


    private fun addFood(foodInfo: FoodInfo) {

        viewModelScope.launch(Dispatchers.IO) {
            val foodDao = App.appDatabase.foodDao()
            FoodRepository(foodDao).add(foodInfo)
            //刷新页面
            getFoods()
        }

    }

    /**
     * 获取用户数据
     */
    private fun getFoods() {
        viewModelScope.launch(Dispatchers.IO) {
            val foodDao =
                App.appDatabase.foodDao()

            viewStates = viewStates.copy(foodInfoList = FoodRepository(foodDao).getList())
        }

    }

    private fun setAddFoodDialogState(boolean: Boolean) {
        viewStates = viewStates.copy(addFoodDialog = boolean)
    }


    private fun setSelectFoodState(boolean: Boolean) {
        viewStates = viewStates.copy(selectFoodState = boolean)
    }


}