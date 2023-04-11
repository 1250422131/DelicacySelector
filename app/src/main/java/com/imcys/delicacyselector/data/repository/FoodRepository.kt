package com.imcys.delicacyselector.data.repository

import com.imcys.delicacyselector.data.dao.FoodDao
import com.imcys.delicacyselector.model.FoodInfo

class FoodRepository(private val dao: FoodDao) {

    suspend fun add(todo: FoodInfo) {
        dao.insert(todo)
    }

    suspend fun delete(todo: FoodInfo) {
        dao.delete(todo)
    }

    suspend fun getList(): MutableList<FoodInfo> {
        return dao.selectListById()
    }


}