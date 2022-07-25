package com.example.socialmediagamer.utils

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.UsersProvider

class ViewedMessageHelper {

    fun updateOnline(status:Boolean,context: Context){
        val usersProvider = UsersProvider()
        val authProvider =AuthProvider()
        if (authProvider.getUid() != null){
            usersProvider.updateOnline(authProvider.getUid().toString(),status)
        }
        else if (status){
            usersProvider.updateOnline(authProvider.getUid().toString(),status)
        }
    }


    fun isApplicationSentToBackground(context: Context):Boolean{
        val activityManager : ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks : List<ActivityManager.RunningTaskInfo> = activityManager.getRunningTasks(1)
        if (!tasks.isEmpty()){
            val topActivity: ComponentName? = tasks.get(0).topActivity
            if (!topActivity?.packageName.equals(context.packageName)){
                return true
            }
        }
        return false
    }

}