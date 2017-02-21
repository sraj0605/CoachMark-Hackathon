package com.intuit.qbes.mobilescanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intuit.qbes.mobilescanner.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckumar5 on 18/02/17.
 */

public class TaskUtil {

    private static DatabaseHandler db = null;
    public static List<Task> TasksFromJSON(String plJsonStr, long taskType)
    {
        try {
            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            Gson gson = builder.create();
            Task[] objList = gson.fromJson(plJsonStr, Task[].class);
            List<Task> taskLists = new ArrayList<>();
            for (int i = 0; i < objList.length; i++) {

                if(objList[i].getTaskType() == taskType)
                    taskLists.add(objList[i]);
            }
            gson = null;
            builder = null;
            return taskLists;
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }

        return null;
    }

    public static Task TasklistFromJSON(String plJsonStr, long tasktype )
    {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        Task objList = gson.fromJson(plJsonStr, Task.class);

        if(objList != null && objList.getTaskType() != tasktype)
            objList = null;
        gson = null;
        builder = null;

        return objList;
    }

    public static String JSONStringFromTasklist(Task taskList)
    {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        String jsonString = gson.toJson(taskList);

        return jsonString;
    }

    public String JSONStringArrayFromTaskListArray(List<Task> taskLists)
    {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        String jsonString = gson.toJson(taskLists);

        return jsonString;
    }
}
