package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Indicates if a tasks properties are showing
    static boolean taskPropertiesShowing;
    //Indicates if a task's options are showing
    static boolean taskOptionsShowing;
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
    //Used to indicate that user is in the note screen
    static boolean inNote;
    //Used to indicate that user is in the sub-tasks screen
    static boolean inChecklist;
    //Used to indicate that date row is showing
    static boolean dateRowShowing;
    //Used to indicate that a repeating alarm is being set
    static boolean repeating;
    //Used to indicate that date picker is showing
    static boolean datePickerShowing;
    //Used to indicate that time picker is showing
    static boolean timePickerShowing;
    //Used to indicate that alarm options are showing
    static boolean alarmOptionsShowing;
    //Used to indicate that repeat options is showing
    static boolean repeatShowing;
    //Used to indicate that list needs to be reordered
    static boolean reorderList;
    //Reinstate alarm after reinstating task
    static boolean reinstateAlarm;
    //Used to determine that task is being set to complete
    static boolean completeTask;
    //Used to determine if sound effects should play or not
    static boolean mute;

    //Indicates which task has it's properties showing
    static int activeTask;
    //Saves the size of the task list
    public static int taskListSize;
    //Height of the 'add' button
    static int addHeight;
    static int addIconHeight;
    //Measures to determine if keyboard is up
    private int heightDiff;
    //Size of checklist of checklists
    static int checklistListSize;
    //Height of list view as viewable on screen
    static int listViewHeight;
    static int thePosition;

    //Interval between repeating alarms
    static long repeatInterval;

    //Helps to determine if keyboard is up in portrait orientation
    private double portraitKeyboardMeasure;
    //Helps to determine if keyboard is up in landscape orientation
    private double landscapeKeyboardMeasure;

    //List of task names
    public static ArrayList<String> taskList;
    //Keeps track of task IDs sorted by due date
    public static ArrayList<String> sortedIDs;
    //Keeps track of task IDs sorted by due date to be used by note class
    public static ArrayList<String> sortedIdsForNote;

    //Toasts which show up when adding new task
    String[] motivation = new String[] {"Get it done!", "Smash that task!",
            "Be a winner!", "Only wimps give up!", "Don't be a failure!", "Be Victorious"};
    //Keep track of last phrase used so as to not have the same thing twice in a row
    String lastToast;

    //Required for setting notification alarms
    static Intent alertIntent;

    //Managers notification alarms
    static AlarmManager alarmManager;

    //Message that shows up when there are no tasks
    private ImageView noTasksToShow;

    static PendingIntent pendIntent;

    //The editable text box that allows for creating and editing task names
    static EditText taskNameEditText;

    //The button that facilitates the adding of tasks
    static Button add;
    static TextView addIcon;

    //Used for debugging purposes. Should not be visible in final version.
//    Button showDb;
//    Button showAlarmDb;
//    Button showSnoozeDb;
//    Button showUniversalDb;

    //Scrollable list
    static ListView theListView;

    //The master view
    static View activityRootView;

    //The keyboard
    static InputMethodManager keyboard;

    //Parameters of 'add' button
    static RelativeLayout.LayoutParams params;
    static RelativeLayout.LayoutParams iconParams;

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

    //for generating random number to select toast phrases
    Random random = new Random();

    //The user selectable highlight color
    static String highlight;

    static MediaPlayer punch;

    private Toolbar mTopToolbar;

    MenuItem muteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO figure out what to do about older versions
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.dark_gray));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getPreferences(MODE_PRIVATE);

        mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        //Initialising variables
        taskPropertiesShowing = false;
        tasksAreClickable = true;
        taskList = new ArrayList<>();
        noTasksToShow = findViewById(R.id.noTasks);
        taskNameEditText = findViewById(R.id.taskNameEditText);
        add = findViewById(R.id.add);
        addIcon = findViewById(R.id.addIcon);
//        showDb = findViewById(R.id.showDb);
//        showAlarmDb = findViewById(R.id.showAlarmDb);
//        showSnoozeDb = findViewById(R.id.showSnoozeDb);
//        showUniversalDb = findViewById(R.id.showUniversalDb);
        theListView = findViewById(R.id.theListView);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        params = (RelativeLayout.LayoutParams) add.getLayoutParams();
        iconParams = (RelativeLayout.LayoutParams) addIcon.getLayoutParams();
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        inflater = LayoutInflater.from(getApplicationContext());
        addHeight = params.height;
        addIconHeight = iconParams.height;
        theAdapter = new ListAdapter[]{new MyAdapter(this, taskList)};
        TAG = "MainActivity";
        activityRootView = findViewById(R.id.activityRoot);
        fadeTasks = false;
        dateOrTime = false;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        noteDb = new Database(this);
        lastToast = "";
        inNote = false;
        inChecklist = false;
        taskOptionsShowing = false;
        dateRowShowing = false;
        repeating = false;
        repeatInterval = 0;
        datePickerShowing = false;
        timePickerShowing = false;
        alarmOptionsShowing = false;
        repeatShowing = false;
        reorderList = false;
        sortedIDs = new ArrayList<>();
        reinstateAlarm = false;
        completeTask = false;
//        highlight = "#FFFF69B4";
        punch = MediaPlayer.create(this, R.raw.punch);
        mute = false;
//        blah = mTopToolbar.getMenu().getItem(R.id.mute);
//        blah = mTopToolbar.getMenu().getItem(0);
//        muteBtn = mTopToolbar.getMenu().findItem(R.id.mute);

        noteDb.insertUniversalData(mute);

        //getting app-wide data
        Cursor dbResult = MainActivity.noteDb.getUniversalData();
        while (dbResult.moveToNext()) {
            highlight = dbResult.getString(2);
        }

        //Put data in list
        theListView.setAdapter(theAdapter[0]);

        mTopToolbar.setTitleTextColor(Color.parseColor(highlight));

        addIcon.setTextColor(Color.parseColor(highlight));
        taskNameEditText.setBackgroundColor(Color.parseColor(highlight));

        muteSounds(mute);

        //Set list view dividers
        String digits = "0123456789ABCDEF";
        int val = 0;
        for (int i = 1; i < highlight.length(); i++) {
            char c = highlight.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        int[] colors = {0, val, 0};
        theListView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        theListView.setDividerHeight(1);

        //Make task clickable
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Tasks are not clickable if keyboard is up
                if(tasksAreClickable && !completeTask) {

//                    vibrate.vibrate(50);

                    //checking if task has been killed
                    Boolean killed = false;
                    Cursor result = MainActivity.noteDb.getData(Integer
                            .parseInt(sortedIDs.get(position)));
                    while (result.moveToNext()) {
                        killed = result.getInt(6) > 0;
                    }

                    //Selecting a task to view options
                    if (!taskPropertiesShowing && !killed) {

                        viewProperties(position);

                    //Removes completed task
                    } else if (!taskPropertiesShowing && killed) {

                        removeTask(position);

                    //Removes task options from view
                    } else {

                        removeTaskProperties();

                    }

                }else {

                    completeTask = false;

                }

            }

        });

        //Long click allows for editing/reinstating task
        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                int i = position;

                //checking if task has been killed
                Boolean killed = false;
                Cursor result = MainActivity.noteDb.getData(Integer
                        .parseInt(sortedIDs.get(i)));
                while (result.moveToNext()) {
                    killed = result.getInt(6) > 0;
                }

                //Determine if it's possible to edit task
                if (tasksAreClickable && !killed && !taskPropertiesShowing) {

                    rename(i);

                //long click reinstates task that is crossed out
                } else if (tasksAreClickable && killed && !taskPropertiesShowing) {

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

//                vibrate.vibrate(50);

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

        //Used for debugging purposes
//        showDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = noteDb.getAllData();
//
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("NOTE: " + res.getString(1) + "\n");
//                    buffer.append("CHECKLIST: " + res.getString(2) + "\n");
//                    buffer.append("TIMESTAMP: " + res.getString(3) + "\n");
//                    buffer.append("TASK: " + res.getString(4) + "\n");
//                    buffer.append("DUE: " + res.getString(5) + "\n");
//                    buffer.append("KILLED: " + res.getString(6) + "\n");
//                    buffer.append("BROADCAST: " + res.getString(7) + "\n");
//                    buffer.append("REPEAT: " + res.getString(8) + "\n");
//                    buffer.append("OVERDUE: " + res.getString(9) + "\n");
//                    buffer.append("SNOOZED: " + res.getString(10) + "\n");
//                    buffer.append("SHOWONCE: " + res.getString(11) + "\n");
//                    buffer.append("INTERVAL: " + res.getString(12) + "\n");
//                    buffer.append("REPEATINTERVAL: " + res.getString(13) + "\n");
//                    buffer.append("IGNORED: " + res.getString(14) + "\n");
//                    buffer.append("CREATETIMESTAMP: " + res.getString(15) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
//
//            }
//
//        });

        //Used for debugging purposes
//        showAlarmDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = noteDb.getAllAlarmData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("HOUR: " + res.getString(1) + "\n");
//                    buffer.append("MINUTE: " + res.getString(2) + "\n");
//                    buffer.append("AMPM: " + res.getString(3) + "\n");
//                    buffer.append("DAY: " + res.getString(4) + "\n");
//                    buffer.append("MONTH: " + res.getString(5) + "\n");
//                    buffer.append("YEAR: " + res.getString(6) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
//
//            }
//
//        });

//        //Used for debugging purposes
//        showSnoozeDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = noteDb.getAllSnoozeData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("HOUR: " + res.getString(1) + "\n");
//                    buffer.append("MINUTE: " + res.getString(2) + "\n");
//                    buffer.append("AMPM: " + res.getString(3) + "\n");
//                    buffer.append("DAY: " + res.getString(4) + "\n");
//                    buffer.append("MONTH: " + res.getString(5) + "\n");
//                    buffer.append("YEAR: " + res.getString(6) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
//
////                Toast.makeText(v.getContext(), String.valueOf(sortedIDs),
////                        Toast.LENGTH_LONG).show();
//
//            }
//
//        });

//        //Used for debugging purposes
//        showUniversalDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = noteDb.getAllUniversalData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("MUTE: " + res.getString(1) + "\n");
//                    buffer.append("HIGHLIGHT: " + res.getString(2) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
//
////                Toast.makeText(v.getContext(), String.valueOf(sortedIDs),
////                        Toast.LENGTH_LONG).show();
//
//            }
//
//        });

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

                    Calendar timeNow = new GregorianCalendar();

                    //create records in database
                    noteDb.insertData(Integer.parseInt(sortedIDs
                            .get(taskListSize - 1)), "", taskName, Integer.parseInt(sortedIDs
                            .get(taskListSize - 1)), String.valueOf(timeNow.getTimeInMillis() / 1000));
                    noteDb.insertAlarmData(Integer.parseInt(sortedIDs
                                    .get(taskListSize - 1)), "", "",
                            "", "", "", "");
                    noteDb.insertSnoozeData(Integer.parseInt(sortedIDs
                                    .get(taskListSize - 1)), "", "",
                            "", "", "", "");

                    reorderList = true;

                    if(!taskName.equals("")) {
                        //showing motivational toast
                        int i = random.nextInt(6);
                        while (motivation[i].equals(lastToast)) {
                            i = random.nextInt(6);
                        }
                        lastToast = motivation[i];
                        Toast.makeText(v.getContext(), motivation[i],
                                Toast.LENGTH_SHORT).show();
                    }

                    theListView.setAdapter(theAdapter[0]);

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
                    iconParams.height = addIconHeight;

                    add.setLayoutParams(params);
                    addIcon.setLayoutParams(iconParams);

                    return true;

                }

                return false;

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        muteBtn = this.mTopToolbar.getMenu().findItem(R.id.mute);
        if(mute){
            muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted));
        }else{
            muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.unmuted));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //TODO find out if return statements are necessary
        //noinspection SimplifiableIfStatement
        if (id == R.id.mute) {
            muteBtn = this.mTopToolbar.getMenu().findItem(R.id.mute);
            if(mute){
                mute = false;
                muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.unmuted));
                noteDb.updateMute(mute);
            }else{
                mute = true;
                muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted));
                noteDb.updateMute(mute);
            }
            muteSounds(mute);
            return true;
        }else if (id == R.id.lightDark) {
            Toast.makeText(MainActivity.this, "Light/dark clicked", Toast.LENGTH_SHORT).show();
            return true;
        }else if (id == R.id.highlight) {
            //Change this to a color picker
            String[] highlightColor = new String[] {"#FFFF69B4", "#FFFFFF00", "#FFFF0000", "#FF00FF00", "#FF0000FF", "#FFFF00FF"};
            int i = random.nextInt(6);
            while (highlightColor[i].equals(highlight)) {
                i = random.nextInt(6);
            }
            highlight = highlightColor[i];
            noteDb.updateHighlight(highlight);

            mTopToolbar.setTitleTextColor(Color.parseColor(highlight));
            addIcon.setTextColor(Color.parseColor(highlight));
            taskNameEditText.setBackgroundColor(Color.parseColor(highlight));
            //Set list view dividers
            String digits = "0123456789ABCDEF";
            int val = 0;
            for (int j = 1; j < highlight.length(); j++) {
                char c = highlight.charAt(j);
                int d = digits.indexOf(c);
                val = 16 * val + d;
            }
            int[] colors = {0, val, 0};
            theListView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
            theListView.setDividerHeight(1);
            theListView.setAdapter(theAdapter[0]);
            return true;
        }else if (id == R.id.buy) {
            Toast.makeText(MainActivity.this, "Buy clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void muteSounds(boolean mute) {
        if(mute){
            add.setSoundEffectsEnabled(false);
            theListView.setSoundEffectsEnabled(false);
        }else{
            add.setSoundEffectsEnabled(true);
            theListView.setSoundEffectsEnabled(true);
        }
    }

    ////Shows table results for debugging purposes////
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    //////////////////////////////////////////////////

    private void removeTask(int position) {

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

        taskListSize--;

        //deleting data related to deleted task
        noteDb.deleteData(String.valueOf(sortedIDs.get(position)));
        noteDb.deleteAlarmData(String.valueOf(sortedIDs.get(position)));
        noteDb.deleteSnoozeData(String.valueOf(sortedIDs.get(position)));

        //Cancel notification alars if one is set
        alarmManager.cancel(pendIntent.getService(this,
                Integer.parseInt(sortedIDs.get(position)), alertIntent, 0));

        sortedIDs.remove(position);

        Cursor result;
        String id;
        String note;
        Boolean checklist;

        //Getting existing data before going to Database class to change ids.
        for(int i = (activeTask + 1); i < taskListSize; i++){
            result = noteDb.getData(Integer.parseInt(sortedIDs.get(i)));
            id = "";
            note = "";
            checklist = false;
            while(result.moveToNext()){
                id = result.getString(0);
                note = result.getString(1);
                checklist = result.getInt(2) == 1;
            }
            noteDb.updateData(id, note, checklist);
        }

        //Updates the view
        theListView.setAdapter(theAdapter[0]);

        //Checks to see if there are still tasks left
        noTasksLeft();
    }

    //Show selected task's properties
    private void viewProperties(int position) {

        onPause();

        //Marks task as having it's properties showing
        taskPropertiesShowing = true;

        alarmOptionsShowing = false;

        centerTask = true;

        //Gets position of selected task
        activeTask = position;

        //Updates the view
        theListView.setAdapter(theAdapter[0]);

        //Can't change visibility of 'add' button. Have to set height to zero instead.
        params.height = 0;
        iconParams.height = 0;

        add.setLayoutParams(params);
        addIcon.setLayoutParams(iconParams);

    }

    //remove selected task's properties
    private void removeTaskProperties() {

        //Updates the view
        theListView.setAdapter(theAdapter[0]);

        //Marks properties as not showing
        taskPropertiesShowing = false;

        alarmOptionsShowing = false;

        //Returns the 'add' button
        params.height = addHeight;
        iconParams.height = addIconHeight;

        add.setLayoutParams(params);
        addIcon.setLayoutParams(iconParams);
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
        iconParams.height = 0;

        add.setLayoutParams(params);
        addIcon.setLayoutParams(iconParams);
    }

    //reinstates completed task
    public void reinstate(int i) {

        Toast.makeText(MainActivity.this, "Task reinstated",
                Toast.LENGTH_SHORT).show();

        //marks task as not killed in database
        noteDb.updateKilled(toString().valueOf(MainActivity.sortedIDs.get(i)), false);

        reinstateAlarm = true;

        reorderList();

        theListView.setAdapter(theAdapter[0]);

    }

    //TODO make a reorder class that both MainActivity and MyAdapter can access
    public void reorderList() {

        ArrayList<Integer> tempList = new ArrayList<>();

        //Saving timestamps into a temporary array
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting timestamp
            String dbTimestamp = "";
            Cursor dbResult = MainActivity.noteDb.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(i)));
            while (dbResult.moveToNext()) {
                dbTimestamp = dbResult.getString(3);
            }

            tempList.add(Integer.valueOf(dbTimestamp));

        }

        //Ordering list by time task was created
        ArrayList<String> whenTaskCreated = new ArrayList<>();
        for(int i = 0; i < MainActivity.taskListSize; i++){
            String created = "";
            Cursor createdResult = MainActivity.noteDb.getData(Integer.parseInt
                    (MainActivity.sortedIDs.get(i)));
            while (createdResult.moveToNext()) {
                created = createdResult.getString(15);
            }
            whenTaskCreated.add(created);
        }
        Collections.sort(whenTaskCreated);
        Collections.reverse(whenTaskCreated);

        ArrayList<String> tempIdsList = new ArrayList<>();
        ArrayList<String> tempTaskList = new ArrayList<>();
        ArrayList<String> tempKilledIdsList = new ArrayList<>();
        ArrayList<String> tempKilledTaskList = new ArrayList<>();
        ArrayList<String> tempDueIdsList = new ArrayList<>();
        ArrayList<String> tempDueTaskList = new ArrayList<>();

        //getting tasks which have no due date
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting task data
            int dbId = 0;
            String dbTimestamp = "";
            String dbTask = "";
            Boolean dbKilled = false;
            Cursor dbResult = MainActivity.noteDb.getDataByTimestamp(
                    whenTaskCreated.get(i));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTimestamp = dbResult.getString(3);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
            }

            //Filtering out killed tasks
            if((Integer.parseInt(dbTimestamp) == 0) && (!dbKilled)){
                tempIdsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            }else if((Integer.parseInt(dbTimestamp) == 0) && (dbKilled)){
                tempKilledIdsList.add(String.valueOf(dbId));
                tempKilledTaskList.add(dbTask);
            }else {
                tempDueIdsList.add(String.valueOf(dbId));
                tempDueTaskList.add(dbTask);
            }

        }

        Collections.sort(tempList);

        //Adding due tasks to middle of task list
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting task data
            int dbId = 0;
            String dbTask = "";
            Cursor dbResult = MainActivity.noteDb.getDataByDueTime(
                    String.valueOf(tempList.get(i)));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTask = dbResult.getString(4);
            }

            if((tempList.get(i) != 0)){
                tempIdsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            }

        }

        //Adding killed tasks to end of task list
        for(int i = 0; i < tempKilledIdsList.size(); i++){
            tempTaskList.add(tempKilledTaskList.get(i));
            tempIdsList.add(tempKilledIdsList.get(i));
        }

        MainActivity.sortedIDs = tempIdsList;
        MainActivity.taskList = tempTaskList;

        //Updating the view with the new order
        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                this, MainActivity.taskList)};
        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

    }

    //Create a new task
    private void createTask(final String taskName, ArrayList taskList, boolean taskBeingEdited) {

        //Don't allow blank tasks
        if(!taskName.equals("")) {

            if(!taskBeingEdited) {

                taskList.add(taskName);

                taskListSize++;

                //finding unique ID for task
                int i = 0;
                boolean idIsSet = false;
                while (!idIsSet) {
                    if (sortedIDs.contains(String.valueOf(i))) {
                        i++;
                    } else {
                        sortedIDs.add(taskListSize - 1, String.valueOf(i));
                        idIsSet = true;
                    }
                }

                alertIntent = new Intent(this, AlertReceiver.class);

                pendIntent = PendingIntent.getBroadcast(this, i, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

            }else{

                taskList.set(activeTask, taskName);

                noteDb.updateName(sortedIDs.get(activeTask), taskName);

            }

        }

    }

    //Tells user to add tasks when task list is empty
    private void noTasksLeft() {

        //Checks if there are any existing tasks
        if (taskListSize == 0){

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

                        //Screen pixel values are used to determine how much of
                        // the screen is visible
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

                            taskNameEditText.setFocusable(true);

                            taskNameEditText.requestFocus();

                            //Textbox is visible and 'add' button is gone
                            // whenever keyboard is showing
                            taskNameEditText.setVisibility(View.VISIBLE);

                            params.height = 0;
                            iconParams.height = 0;

                            add.setLayoutParams(params);
                            addIcon.setLayoutParams(iconParams);

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

                            taskNameEditText.setFocusable(true);

                            taskNameEditText.requestFocus();

                            //Textbox is visible and 'add' button is gone
                            // whenever keyboard is showing
                            taskNameEditText.setVisibility(View.VISIBLE);

                            //Keyboard is inactive without this line
                            taskNameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                            params.height = 0;
                            iconParams.height = 0;

                            add.setLayoutParams(params);
                            addIcon.setLayoutParams(iconParams);

                            tasksAreClickable = false;

                            restoreNormalListView = true;

                        }else if(restoreNormalListView){

                            fadeTasks = false;

                            //Textbox is gone and 'add' button is visible whenever
                            // keyboard is not showing
                            taskNameEditText.setVisibility(View.GONE);

                            params.height = addHeight;
                            iconParams.height = addIconHeight;

                            add.setLayoutParams(params);
                            addIcon.setLayoutParams(iconParams);

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

        mSharedPreferences.edit().putInt("taskListSizeKey", taskListSize).apply();

//        mSharedPreferences.edit().putBoolean("muteKey", mute).apply();

        for (int i = 0; i < taskListSize; i++) {

            mSharedPreferences.edit().putString("taskNameKey" + String.valueOf(i),
                    taskList.get(i)).apply();

            mSharedPreferences.edit().putString("sortedIDsKey" + String.valueOf(i),
                    sortedIDs.get(i)).apply();

        }

        sortedIdsForNote = sortedIDs;

    }

    @Override
    protected void onResume() {

        super.onResume();

        taskBeingEdited = false;
        dateOrTime = false;
        removeTaskProperties();

        try {
            getSavedData();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private void getSavedData() throws URISyntaxException {

        //clearing the lists before adding data back into them so as to avoid duplication
        taskList.clear();
        sortedIDs.clear();

        //Existing tasks are recalled when app opened
        taskListSize = mSharedPreferences.getInt("taskListSizeKey", 0);

        mute = mSharedPreferences.getBoolean("muteKey", false);

        //getting app-wide data
        Cursor dbResult = MainActivity.noteDb.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
            highlight = dbResult.getString(2);
        }

        muteSounds(mute);

        for( int i = 0 ; i < taskListSize ; i++ ) {

            taskList.add(mSharedPreferences.getString("taskNameKey" + String.valueOf(i), ""));

            sortedIDs.add(mSharedPreferences.getString("sortedIDsKey" +
                    String.valueOf(i), ""));

        }

        alertIntent = new Intent(this, AlertReceiver.class);

        theListView.setAdapter(theAdapter[0]);

        //Checks to see if there are still tasks left
        noTasksLeft();

    }

    @Override
    //Return to previous selection when back is pressed
    public void onBackPressed() {

        //options to properties
        if(taskOptionsShowing){
            theListView.setAdapter(theAdapter[0]);
            taskOptionsShowing = false;
        }else if(datePickerShowing) {
            //date picker to properties
            if(!alarmOptionsShowing) {
                datePickerShowing = false;
                theListView.setAdapter(theAdapter[0]);
            //change due date to alarm options
            }else{
                datePickerShowing = false;
                theListView.setAdapter(theAdapter[0]);
            }
        //time picker to date picker
        }else if(timePickerShowing) {
            datePickerShowing = true;
            timePickerShowing = false;
            theListView.setAdapter(theAdapter[0]);
        //repeat to alarm options
        }else if(repeatShowing){
            repeatShowing = false;
            theListView.setAdapter(theAdapter[0]);
        //alarm options to properties
        }else if(alarmOptionsShowing){
            alarmOptionsShowing = false;
            theListView.setAdapter(theAdapter[0]);
        //Properties to home
        }else if (taskPropertiesShowing){
            removeTaskProperties();
        //Exit app
        }else{
            super.onBackPressed();
        }

    }

    public void complete(View view) {

        if(!mute) {
            punch.start();
        }

        LinearLayout parentLayout = (LinearLayout)view.getParent();

        thePosition = theListView.getPositionForView(parentLayout);

        completeTask = true;

        theListView.performItemClick(theListView.getAdapter().getView(
                thePosition, null, null), thePosition,
                theListView.getAdapter().getItemId(thePosition));

    }

}