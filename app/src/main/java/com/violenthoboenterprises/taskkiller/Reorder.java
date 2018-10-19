package com.violenthoboenterprises.taskkiller;

import android.database.Cursor;
import android.util.Log;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class Reorder {

    String TAG = "Reorder";

    public Reorder(){

        startReordering();

    }

    private void startReordering() {

        ArrayList<Integer> allIDs = MainActivity.db.getIDs();

        ArrayList<Integer> snoozedIDs = new ArrayList<>();

        ArrayList<Integer> tempList = new ArrayList<>();

        //Saving timestamps into a temporary array
        for (int i = 0; i < MainActivity.taskList.size(); i++) {

            //getting timestamp
            String dbTimestamp = "";
            boolean dbSnooze = false;
            int dbInterval = 0;
            String dbID = "";
            String dbSnoozeStamp = "";
            Cursor dbResult = MainActivity.db.getData(allIDs.get(i));
            while (dbResult.moveToNext()) {
                dbID = dbResult.getString(0);
                dbTimestamp = dbResult.getString(3);
                dbSnooze = dbResult.getInt(10) > 0;
                dbInterval = dbResult.getInt(12);
                dbSnoozeStamp = dbResult.getString(21);
            }
            dbResult.close();

            if(dbSnooze) {
                tempList.add(Integer.parseInt(dbSnoozeStamp));
                snoozedIDs.add(Integer.parseInt(dbID));
            }else{
                tempList.add(Integer.valueOf(dbTimestamp));
            }

        }

        ArrayList<String> whenTaskCreated = new ArrayList<>();

        //Ordering list by time task was created
        for (int i = 0; i < MainActivity.taskList.size(); i++) {
            String created = "";
            Cursor createdResult = MainActivity.db.getData(allIDs.get(i));
            while (createdResult.moveToNext()) {
                created = createdResult.getString(15);
            }
            createdResult.close();
            whenTaskCreated.add(created);
        }

        Collections.sort(whenTaskCreated);
        Collections.reverse(whenTaskCreated);

        ArrayList<String> tempIdsList = new ArrayList<>();
        ArrayList<String> tempTaskList = new ArrayList<>();
        ArrayList<String> tempKilledIdsList = new ArrayList<>();
        ArrayList<String> tempKilledTaskList = new ArrayList<>();

        //getting tasks which have no due date
        for(int i = 0; i < MainActivity.taskList.size(); i++){

            //getting task data
            int dbId = 0;
            String dbTimestamp = "";
            String dbTask = "";
            Boolean dbKilled = false;
            Cursor dbResult = MainActivity.db.getDataByTimestamp(
                    whenTaskCreated.get(i));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTimestamp = dbResult.getString(3);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
            }
            dbResult.close();

            //Filtering out killed tasks
            if((Integer.parseInt(dbTimestamp) == 0) && (!dbKilled)){
                tempIdsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            }else if((Integer.parseInt(dbTimestamp) == 0) && (dbKilled)){
                tempKilledIdsList.add(String.valueOf(dbId));
                tempKilledTaskList.add(dbTask);
            }

        }

        Collections.sort(tempList);

        //Adding due tasks which aren't killed to middle of task list
        for(int i = 0; i < MainActivity.taskList.size(); i++) {

            //getting task data
            int dbId;
            String dbTask;
            boolean dbKilled;
            Cursor dbResult = MainActivity.db.getDataByDueTime(
                    String.valueOf(tempList.get(i)));
            boolean dataExists = false;
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
                if ((tempList.get(i) != 0) && !dbKilled) {
                    tempIdsList.add(String.valueOf(dbId));
                    tempTaskList.add(dbTask);
                }
                dataExists = true;
            }
            if (!dataExists) {
                dbResult = MainActivity.db.getDataBySnoozeTime(String.valueOf(tempList.get(i)));
                while (dbResult.moveToNext()) {
                    dbId = dbResult.getInt(0);
                    dbTask = dbResult.getString(4);
                    dbKilled = dbResult.getInt(6) > 0;
                    if ((tempList.get(i) != 0) && !dbKilled) {
                        tempIdsList.add(String.valueOf(dbId));
                        tempTaskList.add(dbTask);
                    }
                }
            }
            dbResult.close();
        }

        //Adding killed tasks to end of task list
        for(int i = 0; i < tempKilledIdsList.size(); i++){
            tempTaskList.add(tempKilledTaskList.get(i));
            tempIdsList.add(tempKilledIdsList.get(i));
        }

        //Adding killed tasks with due dates to end of task list
        for(int i = 0; i < MainActivity.taskList.size(); i++){

            //getting task data
            int dbId = 0;
            String dbTask = "";
            boolean dbKilled = false;
            Cursor dbResult = MainActivity.db.getDataByDueTime(
                    String.valueOf(tempList.get(i)));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
            }
            dbResult.close();

            if((tempList.get(i) != 0) && dbKilled){
                tempIdsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            }

        }

        for(int i = 0; i < MainActivity.taskList.size(); i++){

            MainActivity.db.updateSortedIndex(String.valueOf(i),
                    Integer.parseInt(tempIdsList.get(i)));

        }

        MainActivity.sortedIDs = tempIdsList;
        MainActivity.taskList = tempTaskList;

    }

}
