package com.violenthoboenterprises.taskkiller;

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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class Checklist extends MainActivity {

    static InputMethodManager keyboard;
    static EditText checklistEditText;
    public ListAdapter[] checklistAdapter;
    static ListView checklistView;
    static ArrayList<String> checklist;
    static ArrayList<Boolean> subTasksKilled;
    static ArrayList<Integer> sortedSubtaskIds;
    ArrayList<Integer> tempSortedIDs;
    static boolean subTaskBeingEdited;
    static boolean goToChecklistAdapter;
    private boolean restoreListView;
    static boolean subTasksClickable;
    static boolean fadeSubTasks;
    static boolean noteExists;
    static View checklistRootView;
    private int heightDiff;
    static int renameMe;
    private static String TAG;
    private Toolbar subTasksToolbar;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);
        overridePendingTransition( R.anim.enter_from_left, R.anim.enter_from_left);
        subTasksToolbar = findViewById(R.id.subTasksToolbar);

        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        checklistEditText = findViewById(R.id.checklistEditText);
        checklistView = findViewById(R.id.theChecklist);
        checklist = new ArrayList<>();
        subTasksKilled = new ArrayList<>();
        sortedSubtaskIds = new ArrayList<>();
        tempSortedIDs = new ArrayList<>();
        subTaskBeingEdited = false;
        subTasksClickable = false;
        checklistRootView = findViewById(R.id.checklistRoot);
        TAG = "Checklist";
        fadeSubTasks = false;
        noteExists = false;
        inChecklist = true;
        renameMe = 0;

        String dbTaskId = "";
        Boolean dbLightDark = false;

        int dbID = 0;
        String dbNote = "";
        String dbTimestamp = "";
        String dbTask = "";
        Boolean dbDue = false;
        Boolean dbKilled = false;
        int dbBroadcast = 0;
        Boolean dbRepeat = false;
        Boolean dbOverdue = false;
        Boolean dbSnooze = false;
        Boolean dbShowOnce = false;
        int dbInterval = 0;
        String dbRepeatInterval = "";
        Boolean dbIgnored = false;
        String dbTimeCreated = "";
        int dbSortedIndex = 0;
        int dbChecklistSize = 0;

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            dbTaskId = dbResult.getString(4);
            dbLightDark = dbResult.getInt(3) > 0;
        }

        //getting subtask data
        dbResult = MainActivity.db.getData(Integer.parseInt(dbTaskId));
        while (dbResult.moveToNext()) {
            dbID = dbResult.getInt(0);
            dbTask = dbResult.getString(4);
            dbSortedIndex = dbResult.getInt(16);
            tempSortedIDs.add(dbSortedIndex);
        }
        dbResult.close();

        final String finalDbTaskId = dbTaskId;
        final int finalDbID = dbID;

        //setting highlight color
        checklistEditText.setBackgroundColor(Color.parseColor(MainActivity.highlight));

        //setting title to task name
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
        checklistView.setDivider(new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, colors));
        checklistView.setDividerHeight(1);

        //setting correct background color
        if(!dbLightDark){
            subTasksToolbar.setTitleTextColor(Color.parseColor("#AAAAAA"));
            checklistView.setBackgroundColor(Color.parseColor("#333333"));
            subTasksToolbar.setBackgroundColor(Color.parseColor("#333333"));
        }else{
            subTasksToolbar.setTitleTextColor(Color.parseColor("#000000"));
            checklistView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            subTasksToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        //sorting temporary IDs
        Collections.sort(tempSortedIDs);

        //populating sortedIds list
        for(int i = 0; i < taskListSize; i ++){

            sortedIDs.add(String.valueOf(tempSortedIDs.get(i)));

        }

        //setting up adapter
        checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklist)};

        if(checklist.size() != 0) {

            checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklist)};

            checklistView.setAdapter(checklistAdapter[0]);

        }

        //actions to occur when user clicks list item
        checklistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Getting id of selected item
                boolean isKilled = false;
                Cursor dbResult = db.getSubtaskData(finalDbID,
                        sortedSubtaskIds.get(position));
                while(dbResult.moveToNext()){
                    isKilled = dbResult.getInt(3) > 0;
                }
                dbResult.close();

                //removes completed task from view
                if(isKilled && subTasksClickable){

                    vibrate.vibrate(50);

                    if(!mute){
                        blip.start();
                    }

                    checklist.remove(position);

                    subTasksKilled.remove(position);

                    MainActivity.db.deleteSubtaskData(String.valueOf(finalDbID),
                            String.valueOf(sortedSubtaskIds.get(position)));

                    db.updateChecklistSize(finalDbTaskId, checklist.size());

                    sortedSubtaskIds.remove(position);

                    checklistView.setAdapter(checklistAdapter[0]);

                }

            }

        });

        //Actions to occur on long click of a list item
        checklistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                Log.i(TAG, "subTasksClickable: " + subTasksClickable);
                Log.i(TAG, "subTasksKilled: " + String.valueOf(subTasksKilled));
                Log.i(TAG, "position: " + position);
                Log.i(TAG, "sortedSubtaskIds: " + sortedSubtaskIds);

                boolean isKilled = false;
                Cursor dbResult = db.getSubtaskData(finalDbID,
                        sortedSubtaskIds.get(position));
                while(dbResult.moveToNext()){
                    isKilled = dbResult.getInt(3) > 0;
                }
                dbResult.close();

                //Rename subtask
//                if(subTasksClickable && !subTasksKilled.get(sortedSubtaskIds.get(position))){

                if(subTasksClickable && !/*subTasksKilled.get(position)*/isKilled){

                    keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                    goToChecklistAdapter = false;

                    fadeSubTasks = true;

                    //Indicates that a task is being edited
                    subTaskBeingEdited = true;

                    checklistView.setAdapter(checklistAdapter[0]);

                    renameMe = sortedSubtaskIds.get(position);

                //Reinstate killed subtask
//                }else if(subTasksClickable && subTasksKilled.get(sortedSubtaskIds.get(position))){
                }else if(subTasksClickable && /*subTasksKilled.get(position)*/isKilled){

                    Log.i(TAG, "I'm in here");

                    //marks task as not killed in database
//                    db.updateSubtaskKilled(finalDbTaskId, String.valueOf(position), false);
                    db.updateSubtaskKilled(finalDbTaskId, String.valueOf(sortedSubtaskIds.get(position)), false);

                    subTasksKilled.set(position, false);

                    checklistView.setAdapter(checklistAdapter[0]);

                }

                return true;

            }

        });

        //Actions to occur when keyboard's 'Done' button is pressed
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

                        vibrate.vibrate(50);

                        if(!mute) {
                            blip.start();
                        }

                        //adding data to arraylists
                        checklist.add(checklistTaskName);
                        subTasksKilled.add(false);

                        //updating list size in database
                        db.updateChecklistSize(String.valueOf(finalDbID), checklist.size());
                        //getting unique subtask ID
                        int subtaskId = 0;
                        boolean idIsSet = false;
                        while (!idIsSet) {
                            Cursor dbIdResult = db.getSubtask(finalDbID);
                            while (dbIdResult.moveToNext()) {
                                if (dbIdResult.getInt(1) == subtaskId) {
                                    subtaskId++;
                                } else {
                                    idIsSet = true;
                                }
                            }
                            if (subtaskId == 0) {
                                idIsSet = true;
                            }
                            dbIdResult.close();
                        }

                        Calendar timeNow = new GregorianCalendar();

                        //saving subtask in database
                        db.insertSubtaskData(finalDbID, subtaskId, checklistTaskName, String.valueOf(timeNow.getTimeInMillis() / 1000));

                        //adding new ID to sortedIDs
                        sortedSubtaskIds.add(subtaskId);

                        //Reordering the list based on time subtask was created
                        reorderList(finalDbTaskId);

                    }

                    return true;

                //Actions to occur when editing sub tasks
                }else if(actionId == EditorInfo.IME_ACTION_DONE && subTaskBeingEdited){

                    vibrate.vibrate(50);

                    if(!mute) {
                        blip.start();
                    }

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    //Getting user data
                    String checklistTaskName = checklistEditText.getText().toString();

                    //Clear text from text box
                    checklistEditText.setText("");

                    //Don't allow blank tasks
                    if(!checklistTaskName.equals("")) {

                        //updating arraylists
                        checklist.set(renameMe, checklistTaskName);
                        subTasksKilled.set(renameMe, true);

                        //updating database
                        db.updateSubtask(finalDbTaskId, String.valueOf(renameMe), checklistTaskName);
                        checklistView.setAdapter(checklistAdapter[0]);

                    }

                    return true;

                }

                checklistView.setAdapter(checklistAdapter[0]);

                //Marking editing as complete
                subTaskBeingEdited = false;

                return true;

            }

        });

    }

    //Actions to occur when keyboard is showing
    public void checkIfKeyboardShowing() {

        subTasksClickable = true;

        //TODO check out the hard coded pixels will work on all devices
        checklistRootView.getViewTreeObserver().addOnGlobalLayoutListener
                (new ViewTreeObserver.OnGlobalLayoutListener() {
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

    //Ordering list by time created
    public void reorderList(String parentID){

        ArrayList<Integer> tempList = new ArrayList<>();

        //Saving timestamps into a temporary array
        for(int i = 0; i < checklist.size(); i++){

            //getting timestamp
            String dbTimestamp = "";
            Cursor dbResult = MainActivity.db.getSubtaskData(Integer.parseInt(parentID), sortedSubtaskIds.get(i));
            while (dbResult.moveToNext()) {
                dbTimestamp = dbResult.getString(1);
            }
            dbResult.close();

            tempList.add(Integer.valueOf(dbTimestamp));

        }

        //Ordering list by time task was created
        ArrayList<String> whenTaskCreated = new ArrayList<>();
        for(int i = 0; i < checklist.size(); i++){
            String created = "";
            Cursor createdResult = MainActivity.db.getSubtaskData(Integer.parseInt
                    (parentID), sortedSubtaskIds.get(i));
            while (createdResult.moveToNext()) {
                created = createdResult.getString(4);
            }
            createdResult.close();
            whenTaskCreated.add(created);
        }
        Collections.sort(whenTaskCreated);
        Collections.reverse(whenTaskCreated);

        ArrayList<String> tempIdsList = new ArrayList<>();
        ArrayList<String> tempTaskList = new ArrayList<>();

        //getting tasks
        for(int i = 0; i < checklist.size(); i++){

            //getting task data
            int dbId = 0;
            String dbTask = "";
            Cursor dbResult = MainActivity.db.getSubtaskDataByTimestamp(
                    whenTaskCreated.get(i));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(1);
                dbTask = dbResult.getString(2);
            }
            dbResult.close();

            tempIdsList.add(String.valueOf(dbId));
            tempTaskList.add(dbTask);

        }

        Collections.sort(tempList);

        //Setting the sorted ID in the database
        for(int i = 0; i < checklist.size(); i++){

            MainActivity.db.updateSubtaskSortedIndex(parentID, String.valueOf(i),
                    Integer.parseInt(tempIdsList.get(i)));

        }

        tempSortedIDs.clear();
        sortedSubtaskIds.clear();

        //setting the new sortedIds list
        for(int i = 0; i < checklist.size(); i++){
            tempSortedIDs.add(Integer.valueOf(tempIdsList.get(i)));
            sortedSubtaskIds.add(Integer.valueOf(tempIdsList.get(i)));
        }

        //setting the new task list in correct order
        checklist = tempTaskList;

        checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklist)};
        checklistView.setAdapter(checklistAdapter[0]);

    }

    @Override
    //Tasks are saved in a manner so that they don't vanish when app closed
    protected void onPause(){

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();

        inChecklist = true;

        getSavedData();

    }

    //Existing tasks are recalled when app opened
    private void getSavedData() {

        //clearing lists to prevent duplicates
        sortedSubtaskIds.clear();
        checklist.clear();
        subTasksKilled.clear();

        checkIfKeyboardShowing();

        //Keyboard is inactive without this line
        checklistEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        String id = null;
        Cursor dbResult = db.getUniversalData();
        while (dbResult.moveToNext()) {
            id = dbResult.getString(4);
        }
        dbResult.close();

        Cursor dbIdResult = db.getSubtask(Integer.parseInt(id));
        while (dbIdResult.moveToNext()) {
            //populating arraylists
            sortedSubtaskIds.add(dbIdResult.getInt(1));
            checklist.add(dbIdResult.getString(2));
            subTasksKilled.add(dbIdResult.getInt(3) > 0);
        }
        dbIdResult.close();

        //ordering the list by time subtask created
        reorderList(id);

        //if subtasks already exist keyboard isn't displayed and therefore tasks must be clickable
        if(checklist.size() == 0) {
            subTasksClickable = false;
        }else{
            subTasksClickable = true;
        }

    }

    @Override
    //Return to main screen when back pressed
    public void onBackPressed() {

        //prevents UI bugs
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

