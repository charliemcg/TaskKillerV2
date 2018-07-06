package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //Indicates if a tasks properties are showing
    static boolean taskPropertiesShowing;
    //Indicates if tasks can be clicked on
    static boolean tasksAreClickable;
    //Indicates if a task is being edited
    static boolean taskBeingEdited;
    //Indicates there are no menus or unusual stuff showing
    private boolean restoreNormalListView;
    //Allows the list to be updated
    static boolean goToMyAdapter;
    //Indicates that a checklist is showing
    static boolean checklistShowing;
    //Indicates that tasks should be faded out due to keyboard being up
    static boolean fadeTasks;
    //Used when making task centered in list view
    static boolean centerTask;
    //Used when displaying UI elements for either picking time or date
    static boolean dateOrTime;
    //Used to indicate an alarm is being set
    static boolean alarmBeingSet;

    //Indicates which task has it's properties showing
    static int activeTask;
    //Saves the size of the task list
    static int taskListSize;
    //Height of the 'add' button
    static int addHeight;
    //Measures to determine if keyboard is up
    private int heightDiff;
    //Size of checklist of checklists
    static int checklistListSize;
    //Height of list view as viewable on screen
    static int listViewHeight;

    //Helps to determine if keyboard is up in portrait orientation
    private double portraitKeyboardMeasure;
    //Helps to determine if keyboard is up in landscape orientation
    private double landscapeKeyboardMeasure;

    //Each task is assigned a unique broadcast ID when assigning a notification alarm
    static ArrayList<Integer> broadcastID;
    //Each task is assigned a unique pending intent when assigning a notification alarm
    static ArrayList<PendingIntent> pendingIntent;
    //List of tasks
    public static ArrayList<String> taskList;
    //Keeps track of tasks that are completed but not removed
    static ArrayList<Boolean> tasksKilled;
    //Keeps track of tasks which require a due date notification
    static ArrayList<Boolean> showTaskDueIcon;
    //Keeps track of tasks which require a checklist icon
    static ArrayList<Boolean> showChecklistIcon;

    //Required for setting notification alarms
    static Intent alertIntent;

    //Managers notification alarms
    static AlarmManager alarmManager;

    //Message that shows up when there are no tasks
    private TextView noTasksToShow;

    //The editable text box that allows for creating and editing task names
    static EditText taskNameEditText;

    //The button that facilitates the adding of tasks
    static Button add;

    //Scrollable list
    static ListView theListView;

    //The master view
    static View activityRootView;

    //The keyboard
    static InputMethodManager keyboard;

    //Parameters of 'add' button
    static RelativeLayout.LayoutParams params;

    //Save data from main activity on close
    static SharedPreferences mSharedPreferences;

    //Save data related to checklist on close
    static SharedPreferences nSharedPreferences;

    //Allow phone to vibrate
    static Vibrator vibrate;

    //Allow for updating the list
    public static ListAdapter[] theAdapter;

    //Inflater for checklists
    static LayoutInflater inflater;

    //String used for debugging
    String TAG;

    //Database for keeping track of notes
    static Database noteDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getPreferences(MODE_PRIVATE);

        //Initialising variables
        taskPropertiesShowing = false;
        tasksAreClickable = true;
        taskList = new ArrayList<>();
        tasksKilled = new ArrayList<>();
        showTaskDueIcon = new ArrayList<>();
        showChecklistIcon = new ArrayList<>();
        noTasksToShow = findViewById(R.id.noTasks);
        taskNameEditText = findViewById(R.id.taskNameEditText);
        add = findViewById(R.id.add);
        theListView = findViewById(R.id.theListView);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        params = (RelativeLayout.LayoutParams) add.getLayoutParams();
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        inflater = LayoutInflater.from(getApplicationContext());
        addHeight = params.height;
        theAdapter = new ListAdapter[]{new MyAdapter(this, taskList)};
        TAG = "MainActivity";
        activityRootView = findViewById(R.id.activityRoot);
        fadeTasks = false;
        dateOrTime = false;
        pendingIntent = new ArrayList<>();
        broadcastID = new ArrayList<>();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmBeingSet = false;
        noteDb = new Database(this);

        //Put data in list
        theListView.setAdapter(theAdapter[0]);

        //Make task clickable
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Tasks are not clickable if keyboard is up
                if(tasksAreClickable) {

                    vibrate.vibrate(50);

                    //Selecting a task to view options
                    if (!taskPropertiesShowing && !tasksKilled.get(position)) {

                        //fade out inactive tasks
                        activityRootView.setBackgroundColor(Color.parseColor("#888888"));

                        onPause();

                        //Marks task as having it's properties showing
                        taskPropertiesShowing = true;

                        centerTask = true;

                        //Gets position of selected task
                        activeTask = position;

                        //Updates the view
                        theListView.setAdapter(theAdapter[0]);

                        //Can't change visibility of 'add' button. Have to set height to
                        //zero instead.
                        params.height = 0;

                        add.setLayoutParams(params);

                    //Removes completed task
                    } else if (!taskPropertiesShowing && tasksKilled.get(position)) {

                        taskList.remove(position);

                        MainActivity.checklistShowing = true;

                        //updating Checklist's shared preferences without inflating the class view

                        //getSavedData

                        //skip this management of sub tasks if there are no sub tasks
                        try {

                            checklistListSize = nSharedPreferences
                                    .getInt("checklistListSizeKey", 0);

                            for (int i = 0; i < checklistListSize; i++) {

                                Checklist.checklistSize = nSharedPreferences
                                        .getInt("checklistSizeKey" + String.valueOf(i), 0);

                                try {

                                    Checklist.checklistList.get(i);

                                } catch (IndexOutOfBoundsException e) {

                                    Checklist.checklistList.add(new ArrayList<String>());

                                }

                                try {

                                    Checklist.subTasksKilled.get(i);

                                } catch (IndexOutOfBoundsException e) {

                                    Checklist.subTasksKilled.add(new ArrayList<Boolean>());

                                }

                                for (int j = 0; j < Checklist.checklistSize; j++) {

                                    Checklist.checklistList.get(i).set(j, nSharedPreferences
                                            .getString("checklistItemKey"
                                            + String.valueOf(i) + String.valueOf(j), ""));

                                    Checklist.subTasksKilled.get(i).set(j, nSharedPreferences
                                            .getBoolean("subTasksKilledKey"
                                            + String.valueOf(i) + String.valueOf(j), false));

                                }

                            }

                            //onPause

                            checklistListSize = Checklist.checklistList.size();

                            for (int i = 0; i < checklistListSize; i++) {

                                if (i == MainActivity.activeTask) {

                                    Checklist.checklistList.remove(activeTask);

                                    Checklist.subTasksKilled.remove(activeTask);

                                }

                            }

                            //Getting and saving the size of the task array list
                            checklistListSize = Checklist.checklistList.size();

                            nSharedPreferences.edit().putInt("checklistListSizeKey",
                                    Checklist.checklistListSize).apply();

                            for (int i = 0; i < checklistListSize; i++) {

                                //Getting and saving the size of each array list of sub tasks
                                Checklist.checklistSize = Checklist.checklistList.get(i).size();

                                nSharedPreferences.edit().putInt("checklistSizeKey"
                                                + String.valueOf(i),
                                        Checklist.checklistSize).apply();

                            }

                            //Saving each individual sub task
                            for (int i = 0; i < checklistListSize; i++) {

                                Checklist.checklistSize = Checklist.checklistList.get(i).size();

                                for (int j = 0; j < Checklist.checklistSize; j++) {

                                    nSharedPreferences.edit().putString("checklistItemKey"
                                            + String.valueOf(i) + String.valueOf(j),
                                            Checklist.checklistList.get(i).get(j)).apply();

                                    nSharedPreferences.edit().putBoolean("subTasksKilledKey"
                                            + String.valueOf(i) + String.valueOf(j),
                                            Checklist.subTasksKilled.get(i).get(j)).apply();

                                }

                            }

                        } catch (NullPointerException e) {
                            //TODO don't leave this blank
                        }

                        //deleting note related to deleted task
                        noteDb.deleteData(String.valueOf(activeTask));

                        Cursor result;
                        String note;
                        int newId;

                        //note ID must match task list index. Decrementing id value of all
                        // notes with id greater than the deleted task index.
                        for(int i = activeTask; i <= taskList.size(); i++){
                            result = noteDb.getData(i);
                            note = "";
                            while(result.moveToNext()){
                                note = result.getString(1);
                            }
                            newId = i - 1;
                            noteDb.updateAfterDelete(String.valueOf(newId), note);
                        }

                        //Updates the view
                        theListView.setAdapter(theAdapter[0]);

                        tasksKilled.remove(position);

                        //Cancel notification alarms if one is set
                        alarmManager.cancel(pendingIntent.get(position));

                        pendingIntent.remove(position);

                        showTaskDueIcon.remove(position);

                        showChecklistIcon.remove(position);

                        broadcastID.remove(position);

                        //Checks to see if there are still tasks left
                        noTasksLeft();

                    //Removes task options from view
                    } else {

                        //set background to white
                        activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        //Updates the view
                        theListView.setAdapter(theAdapter[0]);

                        //Marks properties as not showing
                        taskPropertiesShowing = false;

                        //Returns the 'add' button
                        params.height = addHeight;

                        add.setLayoutParams(params);

                    }

                }

            }

        });

        //Long click allows for editing the task name
        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                int i = position;

                //Determine if it's possible to edit task
                if (tasksAreClickable && !tasksKilled.get(i) && !taskPropertiesShowing) {

                    rename(i);

                //long click reinstates task that is crossed out
                } else if (tasksAreClickable && tasksKilled.get(i) && !taskPropertiesShowing) {

                    reinstate(i);

                }

                return true;

            }

        });

        //Actions to occur when 'add' selected
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToMyAdapter = true;

                vibrate.vibrate(50);

                //Show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);

                //Set return button to 'Done'
                taskNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

                //Ensure that there is no previous text in the text box
                taskNameEditText.setText("");

                //Actions to occur when keyboard is showing
                checkKeyboardShowing();

            }

        });

        //Actions to occur when user submits new task
        taskNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Actions to take when creating new task
                if((actionId == EditorInfo.IME_ACTION_DONE) && !taskBeingEdited){

                    //Text box and keyboard disappear
                    taskNameEditText.setVisibility(View.GONE);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    //Getting user data
                    String taskName = taskNameEditText.getText().toString();

                    //Clear text from text box
                    taskNameEditText.setText("");

                    //Add new task in task list
                    createTask(taskName, taskList, taskBeingEdited);

                    //Checks to see if there are still tasks available
                    noTasksLeft();

                    //create a record in the database for tracking icons
                    noteDb.insertData(activeTask, "");

                    return true;

                //Actions to take when editing existing task
                }else if(actionId == EditorInfo.IME_ACTION_DONE){

                    taskNameEditText.setVisibility(View.GONE);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    //Getting user data
                    String editedTaskString = taskNameEditText.getText().toString();

                    createTask(editedTaskString, taskList, taskBeingEdited);

                    theListView.setAdapter(theAdapter[0]);

                    tasksAreClickable = true;

                    //Marking editing as complete
                    taskBeingEdited = false;

                    //Bringing back the 'add' button
                    params.height = addHeight;

                    add.setLayoutParams(params);

                    return true;

                }

                return false;

            }

        });

    }

    //renames task
    public void rename(int i) {

        //Cannot update the list until after the task has been updated.
        goToMyAdapter = false;

        //Actions to occur when keyboard is showing
        checkKeyboardShowing();

        //Indicates that a task is being edited
        taskBeingEdited = true;

        activeTask = i;

        tasksAreClickable = false;

        fadeTasks = true;

        centerTask = true;

        theListView.setAdapter(theAdapter[0]);

        //Can't change visibility of 'add' button. Have to set height to zero instead.
        params.height = 0;

        add.setLayoutParams(params);
    }

    //reinstates completed task
    public void reinstate(int i) {

        Toast.makeText(MainActivity.this, "Task Reinstated",
                Toast.LENGTH_SHORT).show();

        tasksKilled.set(i, false);

        theListView.setAdapter(theAdapter[0]);
    }

    //Create a new task
    private void createTask(final String taskName, ArrayList taskList, boolean taskBeingEdited) {

        //Don't allow blank tasks
        if(!taskName.equals("")) {

            if(!taskBeingEdited) {

                taskList.add(taskList.size(), taskName);

                tasksKilled.add(tasksKilled.size(), false);

                showTaskDueIcon.add(showTaskDueIcon.size(), false);

                showChecklistIcon.add(showChecklistIcon.size(), false);

                alertIntent = new Intent(this, AlertReceiver.class);

                pendingIntent.add(pendingIntent.size(), PendingIntent.getBroadcast(this,
                        0, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                broadcastID.add(broadcastID.size(), 0);

            }else{

                taskList.set(activeTask, taskName);

            }

        }

    }

    //Tells user to add tasks when task list is empty
    private void noTasksLeft() {

        //Checks if there are any existing tasks
        if (taskList.size() == 0){

            //Inform user to add some tasks
            noTasksToShow.setVisibility(View.VISIBLE);

        }else{

            noTasksToShow.setVisibility(View.GONE);

        }

    }

    //Actions to occur when keyboard is showing
    void checkKeyboardShowing() {

        //TODO check out the hard coded pixels will work on all devices
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener
                (new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (getResources().getConfiguration().orientation == 1) {

                            portraitKeyboardMeasure = /*heightDiff*/activityRootView.getRootView()
                                    .getHeight() / 2.4;
                            landscapeKeyboardMeasure = activityRootView.getRootView()
                                    .getWidth() / 13.7945205479452054794;

                        } else if (getResources().getConfiguration().orientation == 2) {

                            landscapeKeyboardMeasure = /*heightDiff*/activityRootView
                                    .getHeight() / 13.7945205479452054794;
                            portraitKeyboardMeasure = activityRootView.getRootView()
                                    .getRootView().getWidth() / 2.4;

                        }

                        Rect screen = new Rect();

                        activityRootView.getWindowVisibleDisplayFrame(screen);

                        //Screen pixel values are used to determine how much of the screen is visible
                        heightDiff = activityRootView.getRootView().getHeight() -
                                (screen.bottom - screen.top);

                        //Value of more than 800 seems to indicate that the keyboard is showing
                        //in portrait mode
                        if ((heightDiff > /*800*/portraitKeyboardMeasure) && (getResources()
                                .getConfiguration().orientation == 1)) {

                            fadeTasks = true;

                            if (goToMyAdapter) {

                                theListView.setAdapter(theAdapter[0]);

                                goToMyAdapter = false;

                            }

                            //fade background when something is in focus
                            activityRootView.setBackgroundColor(Color.parseColor("#888888"));

                            taskNameEditText.setFocusable(true);

                            taskNameEditText.requestFocus();

                            //Textbox is visible and 'add' button is gone whenever keyboard is showing
                            taskNameEditText.setVisibility(View.VISIBLE);

                            params.height = 0;

                            add.setLayoutParams(params);

                            tasksAreClickable = false;

                            restoreNormalListView = true;

                            //Similar to above but for landscape mode
                        }else if((heightDiff > /*73*/landscapeKeyboardMeasure) &&
                                (heightDiff < /*800*/portraitKeyboardMeasure) && (getResources()
                                .getConfiguration().orientation == 2)){

                            fadeTasks = true;

                            if (goToMyAdapter) {

                                theListView.setAdapter(theAdapter[0]);

                                goToMyAdapter = false;

                            }

                            //fade background when something is in focus
                            activityRootView.setBackgroundColor(Color.parseColor("#888888"));

                            taskNameEditText.setFocusable(true);

                            taskNameEditText.requestFocus();

                            //Textbox is visible and 'add' button is gone whenever keyboard is showing
                            taskNameEditText.setVisibility(View.VISIBLE);

                            //Keyboard is inactive without this line
                            taskNameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                            params.height = 0;

                            add.setLayoutParams(params);

                            tasksAreClickable = false;

                            restoreNormalListView = true;

                        }else if(restoreNormalListView){

                            fadeTasks = false;

                            //setting background to white
                            activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                            //Textbox is gone and 'add' button is visible whenever keyboard is not showing
                            taskNameEditText.setVisibility(View.GONE);

                            params.height = addHeight;

                            add.setLayoutParams(params);

                            tasksAreClickable = true;

                            theListView.setAdapter(theAdapter[0]);

                            restoreNormalListView = false;

                            //Once editing is complete the adapter can update the list
                            goToMyAdapter = true;

                        }

                    }

                });

    }

    @Override
    protected void onPause(){

        super.onPause();

        //Tasks are saved in a manner so that they don't vanish when app closed
        taskListSize = taskList.size();

        mSharedPreferences.edit().putInt("taskListSizeKey", taskListSize).apply();

        for (int i = 0; i < taskListSize; i++) {

            mSharedPreferences.edit().putString("taskNameKey" + String.valueOf(i),
                    taskList.get(i)).apply();

            mSharedPreferences.edit().putBoolean("taskKilledKey" + String.valueOf(i),
                    tasksKilled.get(i)).apply();

            mSharedPreferences.edit().putBoolean("showTaskDueIcon" + String.valueOf(i),
                    showTaskDueIcon.get(i)).apply();

//            nSharedPreferences.edit().putBoolean("showChecklistIcon" + String.valueOf(i),
//                    showChecklistIcon.get(i)).apply();

            mSharedPreferences.edit().putString("pendingIntentKey" + String.valueOf(i),
                    pendingIntent.get(i).toString()).apply();

            mSharedPreferences.edit().putInt("broadcastIDKey" + String.valueOf(i),
                    broadcastID.get(i)).apply();

        }

    }

    @Override
    protected void onResume() {

        super.onResume();

        try {
            getSavedData();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private void getSavedData() throws URISyntaxException {

        //clearing the lists before adding data back into them so as to avoid duplication
        taskList.clear();
        tasksKilled.clear();
        showTaskDueIcon.clear();
        showChecklistIcon.clear();
        pendingIntent.clear();
        broadcastID.clear();

        checklistListSize = 0;

        //Existing tasks are recalled when app opened
        taskListSize = mSharedPreferences.getInt("taskListSizeKey", 0);

        for( int i = 0 ; i < taskListSize ; i++ ) {

            taskList.add(mSharedPreferences.getString("taskNameKey" + String.valueOf(i), ""));

            tasksKilled.add(mSharedPreferences.getBoolean("taskKilledKey" +
                    String.valueOf(i), false));

            showTaskDueIcon.add(mSharedPreferences.getBoolean("showTaskDueIcon"
                    + String.valueOf(i), false));

//            showChecklistIcon.add(nSharedPreferences.getBoolean("showChecklistIcon"
//                    + String.valueOf(i), false));

            alertIntent = new Intent(this, AlertReceiver.class);

            broadcastID.add(mSharedPreferences.getInt("broadcastIDKey" + String.valueOf(i), 0));

            pendingIntent.add(PendingIntent.getBroadcast(this, broadcastID.get(i),
                    alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        }

        theListView.setAdapter(theAdapter[0]);

        //Checks to see if there are still tasks left
        noTasksLeft();

    }
}