package com.smgamer.ui.models

data class Post(
    val id:String,
    val title:String,
    val description:String,
    val imgList:List<String>,
    val category:String,
)
