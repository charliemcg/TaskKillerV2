package com.violenthoboenterprises.taskkiller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class Checklist extends MainActivity {

    static InputMethodManager keyboard;
    static EditText checklistEditText;
    public ListAdapter[] checklistAdapter;
    static ListView checklistView;
    public static int checklistSize;
    static ArrayList<ArrayList<String>> checklistList;
    static ArrayList<String> checklist;
    static int activeSubTask;
    static ArrayList<ArrayList<Boolean>> killedSubTasks;
    static ArrayList<Boolean> subTasksKilled;
    static ArrayList<Integer> sortedSubtaskIds;
    static boolean subTaskBeingEdited;
    static boolean goToChecklistAdapter;
    static View checklistRootView;
    private int heightDiff;
    private boolean restoreListView;
    static boolean subTasksClickable;
    private static String TAG;
    static boolean fadeSubTasks;
    static boolean noteExists;
    private Toolbar subTasksToolbar;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);
//        MainActivity.nSharedPreferences = getPreferences(MODE_PRIVATE);
        subTasksToolbar = findViewById(R.id.subTasksToolbar);

        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        checklistEditText = findViewById(R.id.checklistEditText);
        activeSubTask = MainActivity.activeTask;
        checklistView = findViewById(R.id.theChecklist);
        checklistList = new ArrayList<>();
        checklist = new ArrayList<>();
        subTasksKilled = new ArrayList<>();
        sortedSubtaskIds = new ArrayList<>();
        subTaskBeingEdited = false;
        subTasksClickable = false;
        checklistRootView = findViewById(R.id.checklistRoot);
        TAG = "Checklist";
        fadeSubTasks = false;
        noteExists = false;
        inChecklist = true;

        checklistEditText.setBackgroundColor(Color.parseColor(MainActivity.highlight));
        subTasksToolbar.setTitleTextColor(Color.parseColor(highlight));

        //getting task data
        String dbTask = "";
        Cursor dbTaskResult = MainActivity.db.getUniversalData();
        while (dbTaskResult.moveToNext()) {
            dbTask = dbTaskResult.getString(4);
        }
        dbTaskResult.close();
        dbTaskResult = db.getData(Integer.parseInt(dbTask));
        while (dbTaskResult.moveToNext()) {
            dbTask = dbTaskResult.getString(4);
        }

        subTasksToolbar.setTitle(dbTask);

        //Set list view dividers
        String digits = "0123456789ABCDEF";
        int val = 0;
        for (int i = 1; i < highlight.length(); i++) {
            char c = highlight.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        int[] colors = {0, val, val};
        checklistView.setDivider(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors));
        checklistView.setDividerHeight(1);

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            lightDark = dbResult.getInt(3) > 0;
        }
        dbResult.close();

        if(!lightDark){
            checklistView.setBackgroundColor(Color.parseColor("#333333"));
            subTasksToolbar.setBackgroundColor(Color.parseColor("#333333"));
        }else{
            checklistView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            subTasksToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        //Ensure there are array lists available to write data to
//        if(checklistList.size() <= Integer.parseInt(MainActivity.sortedIdsForNote
//                .get(activeSubTask))){
//
//            for(int i = 0; i < (Integer.parseInt(MainActivity.sortedIdsForNote
//                    .get(activeSubTask)) + 1); i++){
//
//                try{
//
//                    checklistList.get(i);
//
//                }catch (IndexOutOfBoundsException e){
//
//                    checklistList.add(i, new ArrayList<String>());
//
//                }
//
//                try{
//
//                    subTasksKilled.get(i);
//
//                }catch (IndexOutOfBoundsException e){
//
//                    subTasksKilled.add(i, new ArrayList<Boolean>());
//
//                }
//
//            }
//
//        }

//        checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklistList
//                    .get(Integer.parseInt(sortedIdsForNote.get(activeSubTask))))};

        ArrayList<Integer> tempSortedIDs = new ArrayList<>();

        for( int i = 0 ; i < taskListSize ; i++ ) {

            Cursor sortedIdsResult = db.getData(i);
            while (sortedIdsResult.moveToNext()) {
                tempSortedIDs.add(sortedIdsResult.getInt(16));
            }
            sortedIdsResult.close();

        }

        Collections.sort(tempSortedIDs);

        for(int i = 0; i < taskListSize; i ++){

            sortedIDs.add(String.valueOf(tempSortedIDs.get(i)));

        }

//        checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklistList
//                .get(Integer.parseInt(sortedIDs.get(activeTask))))};

        checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklist)};

        if(checklist.size() != 0) {

            checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklist)};

            checklistView.setAdapter(checklistAdapter[0]);

        }

        //Make task clickable
        checklistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Log.i(TAG, "I'm in here");

                String killedId = null;
                boolean isKilled = false;
                Cursor dbResult = db.getUniversalData();
                while (dbResult.moveToNext()) {
                    killedId = dbResult.getString(4);
                }
                dbResult.close();
                Cursor dbIdResult = db.getSubtaskData(Integer.parseInt(killedId), sortedSubtaskIds.get(position)/*position*/);
                while(dbIdResult.moveToNext()){
                    isKilled = dbIdResult.getInt(3) > 0;
                }

                if(/*subTasksClickable && */!isKilled){

                }else if(/*subTasksClickable && */isKilled){

//                    Log.i(TAG, "Before: " + Checklist.sortedSubtaskIds);

                    int removeThis = sortedSubtaskIds.get(position);

                    checklist.remove(/*removeThis*//*sortedSubtaskIds.get(position)*/position);

                    subTasksKilled.remove(/*removeThis*//*sortedSubtaskIds.get(position)*/position);

                    MainActivity.db.deleteSubtaskData(killedId, String.valueOf(sortedSubtaskIds.get(position)/*position*/));

                    String checklistId = null;
                    Cursor dbChecklistResult = db.getUniversalData();
                    while (dbChecklistResult.moveToNext()) {
                        checklistId = dbChecklistResult.getString(4);
                    }
                    dbChecklistResult.close();
                    db.updateChecklistSize(checklistId, checklist.size());
                    if(checklist.size() == 0) {
                        db.updateChecklistExist(checklistId, false);
                    }

                    sortedSubtaskIds.remove(/*removeThis*//*sortedSubtaskIds.get(position)*/position);

                    ////////////////////////////////////////////////////////////////////////////////
//                    if(position < checklist.size()) {
//                        for (int i = position; i < checklist.size(); i++) {
//                            Log.i(TAG, "Position: " + i);
////                            String changeId = null;
////                            Cursor dbChangeResult = db.getUniversalData();
////                            while (dbChangeResult.moveToNext()) {
////                                changeId = dbChangeResult.getString(1);
////                            }
////                            dbChangeResult.close();
//                            String anotherId = "";
//                            Cursor dbAnotherIdResult = db.getSubtaskData(Integer.parseInt(killedId), /*position*/i);
//                            while(dbAnotherIdResult.moveToNext()){
//                                anotherId = dbAnotherIdResult.getString(1);
//                            }
//                            dbAnotherIdResult.close();
//                            Log.i(TAG, "ID: " + anotherId);
//                        }
//                    }

                    checklistView.setAdapter(checklistAdapter[0]);

//                    Log.i(TAG, "After: " + Checklist.sortedSubtaskIds);

                }

//                if(subTasksClickable && !subTasksKilled.get(Integer.parseInt(MainActivity
//                        .sortedIdsForNote.get(MainActivity.activeTask))).get(position)){
//
//                    Log.i(TAG, "I'm in here");
//
//                }else if(subTasksClickable && subTasksKilled.get(Integer.parseInt(MainActivity
//                        .sortedIdsForNote.get(MainActivity.activeTask))).get(position)){
//
//                    checklistList.get(Integer.parseInt(MainActivity.sortedIdsForNote
//                            .get(MainActivity.activeTask))).remove(position);
//
//                    subTasksKilled.get(Integer.parseInt(MainActivity.sortedIdsForNote
//                            .get(MainActivity.activeTask))).remove(position);
//
//                    Cursor result = MainActivity.db.getData(Integer.parseInt(MainActivity
//                            .sortedIdsForNote.get(MainActivity.activeTask)));
//                    while(result.moveToNext()){
//                        noteExists = (result.getInt(2) == 1);
//                    }
//                    result.close();
//
//                    if(checklistList.get(Integer.parseInt(MainActivity
//                            .sortedIdsForNote.get(MainActivity.activeTask))).size() == 0){
//                        //setting checklist in database to false
//                        MainActivity.db.updateData(MainActivity.sortedIdsForNote
//                                .get(MainActivity.activeTask), "", false);
//                    }
//
//                    //Updates the view
//                    checklistView.setAdapter(checklistAdapter[0]);
//
//                }

            }

        });

        //Long click allows for editing the sub task name
        checklistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                //Rename subtask
//                if(subTasksClickable && !subTasksKilled.get(Integer.parseInt(MainActivity
//                        .sortedIdsForNote.get(MainActivity.activeTask))).get(position)) {
//
//                    keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//
//                    goToChecklistAdapter = false;
//
//                    fadeSubTasks = true;
//
//                    //Indicates that a task is being edited
//                    subTaskBeingEdited = true;
//
//                    activeSubTask = position;
//
//                    checklistView.setAdapter(checklistAdapter[0]);
//
//                //Reinstate killed subtask
//                }else{
//
//                    //TODO reinstate subtask
//
//                }

                return true;

            }

        });

        checklistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Keyboard is inactive without this line
                checklistEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                //Actions to occur when user submits new sub task
                if (actionId == EditorInfo.IME_ACTION_DONE && !subTaskBeingEdited) {

                    //Getting user data
                    String checklistTaskName = checklistEditText.getText().toString();

                    //Clear text from text box
                    checklistEditText.setText("");

                    //Don't allow blank tasks
                    if(!checklistTaskName.equals("")) {

                        //Adds sub task to list
//                        checklistList.get(Integer.parseInt(sortedIdsForNote.get(activeSubTask)))
//                                .add(checklistList.get(Integer.parseInt(sortedIdsForNote
//                                        .get(activeSubTask))).size(), checklistTaskName);

                        //Marks sub task as incomplete
//                        subTasksKilled.get(Integer.parseInt(sortedIdsForNote.get(activeSubTask)))
//                                .add(subTasksKilled.get(Integer.parseInt(sortedIdsForNote
//                                        .get(activeSubTask))).size(), false);

                        //marking task so that it displays checklist icon
                        db.updateData(MainActivity.sortedIdsForNote.get(activeTask), "", true);

                    }

                    checklist.add(checklistTaskName);
                    subTasksKilled.add(false);

                    //TODO these conditionals may be unnecessary
                    if(checklist.size() != 0) {
                        String id = null;
                        Cursor dbResult = db.getUniversalData();
                        while (dbResult.moveToNext()) {
                            id = dbResult.getString(4);
                        }
                        dbResult.close();
                        db.updateChecklistSize(id, checklist.size());
//                        Log.i(TAG, "size: " + checklist.size());
                        int subtaskId = 0;
                        boolean idIsSet = false;
                        while (!idIsSet) {
                            Cursor dbIdResult = db.getSubtask(Integer.parseInt(id));
                            while(dbIdResult.moveToNext()){
                                if(dbIdResult.getInt(1) == subtaskId) {
                                    subtaskId++;
                                }else{
                                    idIsSet = true;
                                }
                            }
                            if(subtaskId == 0){
                                idIsSet = true;
                            }
                            dbIdResult.close();
//                        if (sortedIDs.contains(String.valueOf(i))) {
//                            i++;
//                        } else {
//                            sortedIDs.add(taskListSize - 1, String.valueOf(i));
//                            db.updateSortedIndex(String.valueOf(taskListSize - 1), i);
//                            idIsSet = true;
//                        }
                        }
//                        db.updateSubtask(id, checklistTaskName);
                        db.insertSubtaskData(Integer.parseInt(id), subtaskId, checklistTaskName);
                        sortedSubtaskIds.add(subtaskId);
                        checklistView.setAdapter(checklistAdapter[0]);
                    }

                    return true;

                //Actions to occur when editing sub tasks
                }else if(actionId == EditorInfo.IME_ACTION_DONE && subTaskBeingEdited){

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    //Getting user data
                    String editedSubTaskString = checklistEditText.getText().toString();

                    //Don't allow blank sub tasks
                    if(!editedSubTaskString.equals("")) {

                        checklistList.get(Integer.parseInt(MainActivity.sortedIdsForNote
                                .get(MainActivity.activeTask))).set(Integer.parseInt(
                                sortedIdsForNote.get(activeSubTask)), editedSubTaskString);

                    }

                    checklistEditText.setText("");

                    checklistView.setAdapter(checklistAdapter[0]);

                    //Marking editing as complete
                    subTaskBeingEdited = false;

                    return true;

                }

                return false;

            }

        });

    }

    //Actions to occur when keyboard is showing
    public void checkIfKeyboardShowing() {

        subTasksClickable = true;

        //TODO check out the hard coded pixels will work on all devices
        checklistRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                heightDiff = checklistRootView.getRootView().getHeight();

                Rect screen = new Rect();

                checklistRootView.getWindowVisibleDisplayFrame(screen);

                //Screen pixel values are used to determine how much of the screen is visible
                heightDiff = checklistRootView.getRootView().getHeight() -
                        (screen.bottom - screen.top);

                //Value of more than 800 seems to indicate that the keyboard is showing
                //in portrait mode
                if ((heightDiff > 800) && (getResources().getConfiguration().orientation == 1)) {

                    if (subTaskBeingEdited) {

                        checklistRootView.setBackgroundColor(Color.parseColor("#888888"));

                    }

                    fadeSubTasks = true;

                    if (goToChecklistAdapter) {

                        if(checklist.size() != 0) {
                            checklistView.setAdapter(checklistAdapter[0]);
                        }

                        goToChecklistAdapter = false;

                    }

//                    if (checklistList.get(Integer.parseInt(MainActivity.sortedIdsForNote
//                            .get(MainActivity.activeTask))).size() == 0) {

//                        checklistRootView.setBackgroundColor(Color.parseColor("#888888"));

//                    }

                    subTasksClickable = false;

                    restoreListView = true;

                    //Similar to above but for landscape mode
                }else if((heightDiff > 73) && (heightDiff < 800) && (getResources()
                        .getConfiguration().orientation == 2)){

                    if (subTaskBeingEdited) {

                        checklistRootView.setBackgroundColor(Color.parseColor("#888888"));

                    }

                    fadeSubTasks = true;

                    if (goToChecklistAdapter) {

                        checklistView.setAdapter(checklistAdapter[0]);

                        goToChecklistAdapter = false;

                    }

                    if (checklistList.get(Integer.parseInt(MainActivity.sortedIdsForNote
                            .get(MainActivity.activeTask))).size() == 0) {

                        checklistRootView.setBackgroundColor(Color.parseColor("#888888"));

                    }

                    subTasksClickable = false;

                    //Keyboard is inactive without this line
                    checklistEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                    goToChecklistAdapter = true;

                }else if(restoreListView){

                    checklistRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    fadeSubTasks = false;

                    subTasksClickable = true;

                    checklistEditText.setText("");

                    subTaskBeingEdited = false;

                    if(checklist.size() != 0) {
                        checklistView.setAdapter(checklistAdapter[0]);
                    }

                    restoreListView = false;

                    goToChecklistAdapter = true;

                }

            }

        });

    }

    @Override
    //Tasks are saved in a manner so that they don't vanish when app closed
    protected void onPause(){

        super.onPause();

//        inChecklist = false;
//
//        //Getting and saving the size of the task array list
//        MainActivity.checklistListSize = checklistList.size();
//        db.updateChecklistListSize(checklistList.size());
//
////        MainActivity.nSharedPreferences.edit().putInt("checklistListSizeKey",
////                MainActivity.checklistListSize).apply();
//
//        for(int i = 0; i < MainActivity.checklistListSize; i++){
//
//            //Getting and saving the size of each array list of sub tasks
//            checklistSize = checklistList.get(i).size();
//
////            MainActivity.nSharedPreferences.edit().putInt("checklistSizeKey" + String.valueOf(i),
////                    checklistSize).apply();
//
//        }
//
//        //Saving each individual sub task
//        for(int i = 0; i < MainActivity.checklistListSize; i++){
//
//            checklistSize = checklistList.get(i).size();
//
//            for(int j = 0; j < checklistSize; j++){
//
////                MainActivity.nSharedPreferences.edit().putString("checklistItemKey"
////                        + String.valueOf(i) + String.valueOf(j),
////                        checklistList.get(i).get(j)).apply();
//
////                MainActivity.nSharedPreferences.edit().putBoolean("subTasksKilledKey"
////                        + String.valueOf(i) + String.valueOf(j),
////                        subTasksKilled.get(i).get(j)).apply();
//
//            }
//
//        }

    }

    @Override
    protected void onResume() {

        super.onResume();

        inChecklist = true;

        getSavedData();

    }

    //Existing tasks are recalled when app opened
    private void getSavedData() {

        //TODO clear lists
        sortedSubtaskIds.clear();
        checklist.clear();
        subTasksKilled.clear();

//        checklistList.get(Integer.parseInt(MainActivity.sortedIdsForNote
//                .get(MainActivity.activeTask))).clear();
//        subTasksKilled.get(Integer.parseInt(MainActivity.sortedIdsForNote
//                .get(MainActivity.activeTask))).clear();

        checkIfKeyboardShowing();

        //Keyboard is inactive without this line
        checklistEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

//        Log.i(TAG, "Checklist before: " + checklist);

        String id = null;
        int subtaskListSize = 0;
        Cursor dbResult = db.getUniversalData();
        while (dbResult.moveToNext()) {
            id = dbResult.getString(4);
        }
        dbResult.close();
        Cursor result = db.getData(Integer.parseInt(id));
        while (result.moveToNext()) {
            subtaskListSize = result.getInt(17);
        }
        result.close();
//        Log.i(TAG, "taskListSize: " + subtaskListSize);
//        for (int i = 0; i < subtaskListSize; i++) {
//            Cursor dbIdResult = db.getSubtaskData(Integer.parseInt(id), i);
            Cursor dbIdResult = db.getSubtask(Integer.parseInt(id));
            while (dbIdResult.moveToNext()) {
//                Log.i(TAG, "i: " + i);
                sortedSubtaskIds.add(dbIdResult.getInt(1));
                checklist.add(dbIdResult.getString(2));
                subTasksKilled.add(dbIdResult.getInt(3) > 0);
//                Log.i(TAG, "Checklist during: " + checklist);
            }
            dbIdResult.close();
//        }
        checklistView.setAdapter(checklistAdapter[0]);

//        Log.i(TAG, "Checklist after: " + checklist);

//        MainActivity.checklistListSize = MainActivity.nSharedPreferences
//                .getInt("checklistListSizeKey", 0);

//        for (int i = 0; i < MainActivity.checklistListSize; i++) {
//
//            checklistSize = MainActivity.nSharedPreferences
//                    .getInt("checklistSizeKey" + String.valueOf(i), 0);
//
//            try {
//
//                checklistList.get(i);
//
//            } catch (IndexOutOfBoundsException e) {
//
//                checklistList.add(new ArrayList<String>());
//
//            }
//
//            try {
//
//                subTasksKilled.get(i);
//
//            } catch (IndexOutOfBoundsException e) {
//
//                subTasksKilled.add(new ArrayList<Boolean>());
//
//            }
//
//            for (int j = 0; j < checklistSize; j++) {
//
//                checklistList.get(i).add(j, MainActivity.nSharedPreferences
//                        .getString("checklistItemKey" + String.valueOf(i)
//                                + String.valueOf(j), ""));
//
//                subTasksKilled.get(i).add(j, MainActivity.nSharedPreferences
//                        .getBoolean("subTasksKilledKey" + String.valueOf(i)
//                                + String.valueOf(j), false));
//
//            }
//
//        }

        //Only show keyboard if there are no existing sub tasks
//        if(checklistList.get(Integer.parseInt(MainActivity.sortedIdsForNote
//                .get(MainActivity.activeTask))).size() != 0) {

//            this.getWindow().setSoftInputMode(WindowManager.LayoutParams
//                    .SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//            subTasksClickable = true;

//        }else{

            subTasksClickable = false;

//        }

    }

    @Override
    //Return to main screen when back pressed
    public void onBackPressed() {

        if(checklistShowing) {

            Intent intent = new Intent();

            intent.setClass(getApplicationContext(), MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            checklistShowing = false;

        }
        super.onBackPressed();

    }

}

