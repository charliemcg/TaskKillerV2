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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

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
    static View checklistRootView;
    private int heightDiff;
    private boolean restoreListView;
    static boolean subTasksClickable;
    private static String TAG;
    static boolean fadeSubTasks;
    static boolean noteExists;
    private Toolbar subTasksToolbar;
    static int renameMe;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);
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

        //getting app-wide data
        String dbTaskId = "";
        Boolean dbLightDark = false;

        //getting task data
        int dbID = 0;
        String dbNote = "";
//        Boolean dbChecklist = false;
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

        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            dbTaskId = dbResult.getString(4);
            dbLightDark = dbResult.getInt(3) > 0;
        }
        dbResult = MainActivity.db.getData(Integer.parseInt(dbTaskId));
        while (dbResult.moveToNext()) {
            dbID = dbResult.getInt(0);
            dbNote = dbResult.getString(1);
//            dbChecklist = dbResult.getInt(2) > 0;
            dbTimestamp = dbResult.getString(3);
            dbTask = dbResult.getString(4);
            dbDue = dbResult.getInt(5) > 0;
            dbKilled = dbResult.getInt(6) > 0;
            dbBroadcast = dbResult.getInt(7);
            dbRepeat = dbResult.getInt(8) > 0;
            dbOverdue = dbResult.getInt(9) > 0;
            dbSnooze = dbResult.getInt(10) > 0;
            dbShowOnce = dbResult.getInt(11) > 0;
            dbInterval = dbResult.getInt(12);
            dbRepeatInterval = dbResult.getString(13);
            dbIgnored = dbResult.getInt(14) > 0;
            dbTimeCreated = dbResult.getString(15);
            dbSortedIndex = dbResult.getInt(16);
            tempSortedIDs.add(dbSortedIndex);
            dbChecklistSize = dbResult.getInt(17);
        }
        dbResult.close();

        final String finalDbTaskId = dbTaskId;

        final int finalDbID = dbID;
        final String finalDbNote = dbNote;
//        final Boolean finalDbChecklist = dbChecklist;
        final String finalDbTimestamp = dbTimestamp;
        final Boolean finalDbDue = dbDue;
        final Boolean finalDbKilled = dbKilled;
        final int finalDbBroadcast = dbBroadcast;
        final Boolean finalDbRepeat = dbRepeat;
        final Boolean finalDbOverdue = dbOverdue;
        final Boolean finalDbSnooze = dbSnooze;
        final Boolean finalDbShowOnce = dbShowOnce;
        final int finalDbInterval = dbInterval;
        final String finalDbRepeatInterval = dbRepeatInterval;
        final Boolean finalDbIgnored = dbIgnored;
        final String finalDbTimeCreated = dbTimeCreated;
        final int finalDbSortedIndex = dbSortedIndex;
        final int finalDbChecklistSize = dbChecklistSize;

        checklistEditText.setBackgroundColor(Color.parseColor(MainActivity.highlight));
        subTasksToolbar.setTitleTextColor(Color.parseColor(highlight));

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

        if(!dbLightDark){
            checklistView.setBackgroundColor(Color.parseColor("#333333"));
            subTasksToolbar.setBackgroundColor(Color.parseColor("#333333"));
        }else{
            checklistView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            subTasksToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        Collections.sort(tempSortedIDs);

        for(int i = 0; i < taskListSize; i ++){

            sortedIDs.add(String.valueOf(tempSortedIDs.get(i)));

        }

        checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklist)};

        if(checklist.size() != 0) {

            checklistAdapter = new ListAdapter[]{new ChecklistAdapter(this, checklist)};

            checklistView.setAdapter(checklistAdapter[0]);

        }

        checklistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                boolean isKilled = false;
                Cursor dbResult = db.getSubtaskData(finalDbID,
                        sortedSubtaskIds.get(position));
                while(dbResult.moveToNext()){
                    isKilled = dbResult.getInt(3) > 0;
                }
                dbResult.close();

                if(isKilled && subTasksClickable){

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

        //Long click allows for editing the sub task name
        checklistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                Log.i(TAG, "killed: " + subTasksKilled.get(sortedSubtaskIds.get(position)));

                //Rename subtask
                if(subTasksClickable && !subTasksKilled.get(sortedSubtaskIds.get(position))){

                    keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                    goToChecklistAdapter = false;

                    fadeSubTasks = true;

                    //Indicates that a task is being edited
                    subTaskBeingEdited = true;

                    checklistView.setAdapter(checklistAdapter[0]);

                    renameMe = sortedSubtaskIds.get(position);

                //Reinstate killed subtask
                }else if(subTasksClickable && subTasksKilled.get(sortedSubtaskIds.get(position))){

                    //marks task as not killed in database
                    db.updateSubtaskKilled(finalDbTaskId, String.valueOf(position), false);

                    subTasksKilled.set(position, false);

                    checklistView.setAdapter(checklistAdapter[0]);

                }

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

                        checklist.add(checklistTaskName);
                        subTasksKilled.add(false);

                        db.updateChecklistSize(String.valueOf(finalDbID), checklist.size());
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
                        db.insertSubtaskData(finalDbID, subtaskId, checklistTaskName);
                        sortedSubtaskIds.add(subtaskId);
                        checklistView.setAdapter(checklistAdapter[0]);

                    }
                    return true;

                //Actions to occur when editing sub tasks
                }else if(actionId == EditorInfo.IME_ACTION_DONE && subTaskBeingEdited){

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    //Getting user data
                    String checklistTaskName = checklistEditText.getText().toString();

                    //Clear text from text box
                    checklistEditText.setText("");

                    //Don't allow blank tasks
                    if(!checklistTaskName.equals("")) {

                        checklist.set(renameMe, checklistTaskName);
                        subTasksKilled.set(renameMe, true);

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
            sortedSubtaskIds.add(dbIdResult.getInt(1));
            checklist.add(dbIdResult.getString(2));
            subTasksKilled.add(dbIdResult.getInt(3) > 0);
        }
        dbIdResult.close();
        checklistView.setAdapter(checklistAdapter[0]);

        if(checklist.size() == 0) {
            subTasksClickable = false;
        }else{
            subTasksClickable = true;
        }

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

