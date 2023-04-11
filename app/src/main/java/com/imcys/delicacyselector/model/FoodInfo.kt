package com.imcys.delicacyselector.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ds_food")
data class FoodInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var foodName: String = "",

    //添加日期
    @ColumnInfo(name = "add_date")
    var addTime: Long = System.currentTimeMillis(),
) {
    constructor() : this(0, "", System.currentTimeMillis())
}
