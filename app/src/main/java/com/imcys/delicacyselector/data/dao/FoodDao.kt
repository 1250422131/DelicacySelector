package com.imcys.delicacyselector.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.imcys.delicacyselector.model.FoodInfo
@Dao
interface FoodDao {

    @Insert
    suspend fun insert(foodDao: FoodInfo)

    @Delete
    suspend fun delete(foodDao: FoodInfo)


    @Query("SELECT * from ds_food WHERE  id =:id  LIMIT 1")
    suspend fun getByIdQuery(id: Int): FoodInfo

    @Query("SELECT * from ds_food ORDER BY id DESC")
    suspend fun selectListById(): MutableList<FoodInfo>

}