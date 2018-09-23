package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

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
    //Used to determine what colour scheme to use
    static boolean lightDark;
    //used to indicate if color picker is showing
    static boolean colorPickerShowing;
    //used to indicate if purchase options are showing
    static boolean purchasesShowing;
    //used to indicate that user purchased ad removal
    boolean adsRemoved;
    //used to indicate that user purchased reminders
    boolean remindersAvailable;
    //used to indicate that user purchased color cycling
    boolean cycleColors;
    //used to indicate that the snooze options are showing
    static boolean snoozeRowShowing;
    //used to indicate that a task was long clicked
    static boolean longClicked;
    static boolean killedAnimation;
    static boolean alarmAnimation;
    static boolean reinstateAnimation;
    static int animatePosition;
    static int animateID;

    //task properties require exit animation
    static boolean exitTaskProperties;
    //alarm options require exit animation
    static boolean exitAlarmOptions;
    //date picker requires exit animation
    static boolean exitDatePicker;
    //time picker requires exit animation
    static boolean exitTimePicker;
    //date picker when changing due date requires exit animation
    static boolean exitChangeDueDate;
    //repeat row requires exit animation
    static boolean exitRepeat;

    //Indicates which task has it's properties showing
    static int activeTask;
    //Saves the size of the task list
    public static int taskListSize;
    //Height of the 'add' button
    static int addHeight;
    static int addIconHeight;
    //Measures to determine if keyboard is up
    private int heightDiff;
    //Height of list view as viewable on screen
    static int listViewHeight;
    static int thePosition;
    //portrait width of device
    static int deviceWidthPortrait;

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
    String[] motivation;
    //Keep track of last phrase used so as to not have the same thing twice in a row
    String lastToast;

    //Required for setting notification alarms
    static Intent alertIntent;

    //Managers notification alarms
    static AlarmManager alarmManager;

    //Message that shows up when there are no tasks
    private ImageView noTasksToShow;
    private ImageView noTasksToShowWhite;

    //Custom toast
    static TextView toast;

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
//    Button showSubtasksDb;

    //Scrollable list
    static ListView theListView;

    //The master view
    static View activityRootView;

    //The keyboard
    static InputMethodManager keyboard;

    //Parameters of 'add' button
    static RelativeLayout.LayoutParams params;
    static RelativeLayout.LayoutParams iconParams;

    //Allow phone to vibrate
    static Vibrator vibrate;

    //Allow for updating the list
    public static ListAdapter[] theAdapter;

    //Inflater for checklists
    static LayoutInflater inflater;

    //String used for debugging
    String TAG;

    //Database for keeping track of notes
    static Database db;

    //for generating random number to select toast phrases
    Random random = new Random();

    //The user selectable highlight color
    static String highlight;

    //Sound played when task marked as complete
    static MediaPlayer punch;
    //Sound played when toast displays
    static MediaPlayer sweep;
    //Default sound played throughout the app
    static MediaPlayer blip;
    //Sound played when user selects an in-app purchase
    static MediaPlayer chime;

    //The action bar
    private Toolbar topToolbar;

    //Action bar options
    MenuItem muteBtn;
    MenuItem lightDarkBtn;
    MenuItem customiseBtn;
    MenuItem proBtn;

    //The color picker view and it's corresponding buttons
    LinearLayout colorPicker;
    Button white;
    Button black;
    Button lightYellow;
    Button darkYellow;
    Button lightBlue;
    Button darkBlue;
    Button lightOrange;
    Button darkOrange;
    Button lightPurple;
    Button darkPurple;
    Button lightRed;
    Button darkRed;
    Button lightPink;
    Button darkPink;
    Button lightGreen;
    Button darkGreen;

    BillingProcessor bp;

    //In-app purchases view and it's elements
    LinearLayout purchases;
    LinearLayout removeAdsLayout;
    LinearLayout getRemindersLayout;
    LinearLayout cycleColorsLayout;
    LinearLayout unlockAllLayout;
    TextView colorPickerTitle;
    TextView removeAdsTitle;
    TextView removeAdsDescription;
    TextView getRemindersTitle;
    TextView getRemindersDescription;
    TextView cycleColorsTitle;
    TextView cycleColorsDescription;
    TextView unlockAllTitle;
    TextView unlockAllDescription;
    ImageView removeAdsImage;
    ImageView getRemindersImage;
    ImageView cycleColorsImage;
    ImageView unlockAllImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_gray));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_right);

        topToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolbar);

        //Initialising variables
        taskPropertiesShowing = false;
        tasksAreClickable = true;
        taskList = new ArrayList<>();
        noTasksToShow = findViewById(R.id.noTasks);
        noTasksToShowWhite = findViewById(R.id.noTasksWhite);
        taskNameEditText = findViewById(R.id.taskNameEditText);
        add = findViewById(R.id.add);
        addIcon = findViewById(R.id.addIcon);
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
        db = new Database(this);
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
        punch = MediaPlayer.create(this, R.raw.punch);
        sweep = MediaPlayer.create(this, R.raw.sweep);
        blip = MediaPlayer.create(this, R.raw.blip);
        chime = MediaPlayer.create(this, R.raw.chime);
        mute = false;
        colorPicker = findViewById(R.id.colorPicker);
        white = findViewById(R.id.white);
        black = findViewById(R.id.black);
        lightYellow = findViewById(R.id.lightYellow);
        darkYellow = findViewById(R.id.darkYellow);
        lightBlue = findViewById(R.id.lightBlue);
        darkBlue = findViewById(R.id.darkBlue);
        lightOrange = findViewById(R.id.lightOrange);
        darkOrange = findViewById(R.id.darkOrange);
        lightPurple = findViewById(R.id.lightPurple);
        darkPurple = findViewById(R.id.darkPurple);
        lightRed = findViewById(R.id.lightRed);
        darkRed = findViewById(R.id.darkRed);
        lightPink = findViewById(R.id.lightPink);
        darkPink = findViewById(R.id.darkPink);
        darkGreen = findViewById(R.id.darkGreen);
        lightGreen = findViewById(R.id.lightGreen);
        colorPickerTitle = findViewById(R.id.colorPickerTitle);
        removeAdsTitle = findViewById(R.id.removeAdsTitle);
        removeAdsDescription = findViewById(R.id.removeAdsDescription);
        getRemindersTitle = findViewById(R.id.getRemindersTitle);
        getRemindersDescription = findViewById(R.id.getremindersDescription);
        cycleColorsTitle = findViewById(R.id.cycleColorsTitle);
        cycleColorsDescription = findViewById(R.id.cycleColorsDescription);
        unlockAllTitle = findViewById(R.id.unlockAllTitle);
        unlockAllDescription = findViewById(R.id.unlockAllDescription);
        bp = new BillingProcessor(this, /*TODO"YOUR LICENSE KEY FROM
        GOOGLE PLAY CONSOLE HERE"*/null, this);
        purchases = findViewById(R.id.purchases);
        adsRemoved = false;
        remindersAvailable = false;
        cycleColors = false;
        removeAdsLayout = findViewById(R.id.removeAds);
        getRemindersLayout = findViewById(R.id.getReminders);
        cycleColorsLayout = findViewById(R.id.cycleColors);
        unlockAllLayout = findViewById(R.id.unlockAll);
        motivation = new String[]{getString(R.string.getItDone),
                getString(R.string.smashThatTask), getString(R.string.beAWinner),
                getString(R.string.onlyWimpsGiveUp), getString(R.string.dontBeAFailure),
                getString(R.string.beVictorious)};
        taskListSize = 0;
        exitTaskProperties = false;
        exitAlarmOptions = false;
        exitDatePicker = false;
        exitChangeDueDate = false;
        exitRepeat = false;
        snoozeRowShowing = false;
        toast = findViewById(R.id.toast);
        killedAnimation = false;
        reinstateAnimation = false;
        animatePosition = 0;
        animateID = 0;
        longClicked = false;
        //TODO add support for landscape
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceWidthPortrait = displayMetrics.widthPixels;
//        showDb = findViewById(R.id.showDb);
//      showAlarmDb = findViewById(R.id.showAlarmDb);
//        showUniversalDb = findViewById(R.id.showUniversalDb);
//        showSubtasksDb = findViewById(R.id.showSubtasksDb);

        final View child = topToolbar.getChildAt(2);
        if (child instanceof ActionMenuView)
        {
            final ActionMenuView actionMenuView = ((ActionMenuView) child);
            actionMenuView.getChildAt(actionMenuView.getChildCount() - 1)
                    .setSoundEffectsEnabled(false);
        }

        db.insertUniversalData(mute);

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            highlight = dbResult.getString(2);
            lightDark = dbResult.getInt(3) > 0;
            adsRemoved = dbResult.getInt(5) > 0;
            remindersAvailable = dbResult.getInt(6) > 0;
            cycleColors = dbResult.getInt(7) > 0;
            taskListSize = dbResult.getInt(8);
        }
        dbResult.close();

        //Put data in list
        theListView.setAdapter(theAdapter[0]);

        topToolbar.setTitleTextColor(Color.parseColor(highlight));

        addIcon.setTextColor(Color.parseColor(highlight));
        taskNameEditText.setBackgroundColor(Color.parseColor(highlight));
        toast.setBackgroundColor(Color.parseColor(highlight));

        checkLightDark(lightDark);

        //Make task clickable
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Tasks are not clickable if keyboard is up
                if(tasksAreClickable && !completeTask) {

                    if(!mute) {
                        blip.start();
                    }

//                    vibrate.vibrate(50);

                    //checking if task has been killed
                    Boolean killed = false;
                    Cursor result = db.getData(Integer
                            .parseInt(sortedIDs.get(position)));
                    while (result.moveToNext()) {
                        killed = result.getInt(6) > 0;
                    }
                    result.close();

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
                Cursor result = db.getData(Integer
                        .parseInt(sortedIDs.get(i)));
                while (result.moveToNext()) {
                    killed = result.getInt(6) > 0;
                }
                result.close();

                //Determine if it's possible to edit task
                if (tasksAreClickable && !killed && !taskPropertiesShowing) {

                    longClicked = true;

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

                if(!mute) {
                    blip.start();
                }

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
//                Cursor res = db.getAllData();
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
//                    buffer.append("CREATETIMESTAMP: " + res.getString(15) + "\n");
//                    buffer.append("SORTEDINDEX: " + res.getString(16) + "\n");
//                    buffer.append("CHECKLISTSIZE: " + res.getString(17) + "\n\n");
//                }
//                res.close();
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
//            }
//
//        });

        //Used for debugging purposes
//        showUniversalDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = db.getAllUniversalData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("MUTE: " + res.getString(1) + "\n");
//                    buffer.append("HIGHLIGHT: " + res.getString(2) + "\n");
//                    buffer.append("DARKLIGHT: " + res.getString(3) + "\n");
//                    buffer.append("ACTIVETASKNAME: " + res.getString(4) + "\n");
//                    buffer.append("ADSREMOVED: " + res.getString(5) + "\n");
//                    buffer.append("REMINDERSAVAILABLE: " + res.getString(6) + "\n");
//                    buffer.append("CYCLECOLORS: " + res.getString(7) + "\n");
//                    buffer.append("TASKLISTSIZE: " + res.getString(8) + "\n");
//                    buffer.append("CHECKLISTLISTSIZE: " + res.getString(9) + "\n\n");
//                }
//                res.close();
//
//                showMessage("Data", buffer.toString());
//
//            }
//
//        });

        //Used for debugging purposes
//        showSubtasksDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = db.getAllSubtaskData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("SUBTASKID: " + res.getString(1) + "\n");
//                    buffer.append("SUBTASK: " + res.getString(2) + "\n");
//                    buffer.append("SUBTASKKILLED: " + res.getString(3) + "\n");
//                    buffer.append("TIMECREATED: " + res.getString(4) + "\n\n");
//                }
//                res.close();
//
//                showMessage("Data", buffer.toString());
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
                    db.insertData(Integer.parseInt(sortedIDs
                            .get(taskListSize - 1)), "", taskName, Integer.parseInt(sortedIDs
                            .get(taskListSize - 1)), String.valueOf(timeNow
                            .getTimeInMillis() / 1000));
                    db.insertAlarmData(Integer.parseInt(sortedIDs
                                    .get(taskListSize - 1)), "", "",
                            "", "", "", "");
                    db.insertSnoozeData(Integer.parseInt(sortedIDs
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
                        toast.setText(motivation[i]);
                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                sweep.start();
                                toast.startAnimation(AnimationUtils.loadAnimation
                                        (MainActivity.this, R.anim.enter_from_right_fast));
                                toast.setVisibility(View.VISIBLE);
                                final Handler handler2 = new Handler();
                                final Runnable runnable2 = new Runnable(){
                                    public void run(){
                                        toast.startAnimation(AnimationUtils.loadAnimation
                                                (MainActivity.this, android.R.anim.fade_out));
                                        toast.setVisibility(View.GONE);
                                    }
                                };
                                handler2.postDelayed(runnable2, 1500);
                            }
                        };

                        handler.postDelayed(runnable, 500);
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

    private void checkLightDark(boolean lightDark) {
        if(!lightDark){
            theListView.setBackgroundColor(Color.parseColor("#333333"));
            topToolbar.setBackgroundColor(Color.parseColor("#333333"));
            topToolbar.setSubtitleTextColor(Color.parseColor("#AAAAAA"));
            black.setVisibility(View.GONE);
            darkYellow.setVisibility(View.GONE);
            darkBlue.setVisibility(View.GONE);
            darkOrange.setVisibility(View.GONE);
            darkPurple.setVisibility(View.GONE);
            darkRed.setVisibility(View.GONE);
            darkPink.setVisibility(View.GONE);
            darkGreen.setVisibility(View.GONE);
            colorPicker.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border));
            colorPickerTitle.setTextColor(Color.parseColor("#AAAAAA"));
            removeAdsTitle.setTextColor(Color.parseColor("#AAAAAA"));
            removeAdsDescription.setTextColor(Color.parseColor("#AAAAAA"));
            getRemindersTitle.setTextColor(Color.parseColor("#AAAAAA"));
            getRemindersDescription.setTextColor(Color.parseColor("#AAAAAA"));
            cycleColorsTitle.setTextColor(Color.parseColor("#AAAAAA"));
            cycleColorsDescription.setTextColor(Color.parseColor("#AAAAAA"));
            unlockAllTitle.setTextColor(Color.parseColor("#AAAAAA"));
            unlockAllDescription.setTextColor(Color.parseColor("#AAAAAA"));
            purchases.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border));
            removeAdsLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border));
            getRemindersLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border));
            cycleColorsLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border));
            unlockAllLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border));
            setDividers(lightDark);
            theListView.setAdapter(theAdapter[0]);
        }else{
            theListView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            topToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            topToolbar.setSubtitleTextColor(Color.parseColor("#000000"));
            white.setVisibility(View.GONE);
            lightYellow.setVisibility(View.GONE);
            lightBlue.setVisibility(View.GONE);
            lightOrange.setVisibility(View.GONE);
            lightPurple.setVisibility(View.GONE);
            lightRed.setVisibility(View.GONE);
            lightPurple.setVisibility(View.GONE);
            lightPink.setVisibility(View.GONE);
            lightGreen.setVisibility(View.GONE);
            colorPicker.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border_white));
            colorPickerTitle.setTextColor(Color.parseColor("#000000"));
            removeAdsTitle.setTextColor(Color.parseColor("#000000"));
            removeAdsDescription.setTextColor(Color.parseColor("#000000"));
            getRemindersTitle.setTextColor(Color.parseColor("#000000"));
            getRemindersDescription.setTextColor(Color.parseColor("#000000"));
            cycleColorsTitle.setTextColor(Color.parseColor("#000000"));
            cycleColorsDescription.setTextColor(Color.parseColor("#000000"));
            unlockAllTitle.setTextColor(Color.parseColor("#000000"));
            unlockAllDescription.setTextColor(Color.parseColor("#000000"));
            purchases.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.color_picker_border_white));
            removeAdsLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.purchases_dropshadow));
            getRemindersLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.purchases_dropshadow));
            cycleColorsLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.purchases_dropshadow));
            unlockAllLayout.setBackgroundDrawable(ContextCompat.getDrawable
                    (this, R.drawable.purchases_dropshadow));
            setDividers(lightDark);
            theListView.setAdapter(theAdapter[0]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            muteBtn = this.topToolbar.getMenu().findItem(R.id.mute);
            lightDarkBtn = this.topToolbar.getMenu().findItem(R.id.lightDark);
            customiseBtn = this.topToolbar.getMenu().findItem(R.id.highlight);
            proBtn = this.topToolbar.getMenu().findItem(R.id.buy);
            if (!lightDark) {
                if (mute) {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted));
                } else {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.unmuted));
                }
                lightDarkBtn.setTitle("Light Mode");
            } else {
                if (mute) {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted_white));
                } else {
                    muteBtn.setIcon(ContextCompat.getDrawable
                            (this, R.drawable.unmuted_white));
                }
                lightDarkBtn.setTitle("Dark Mode");
            }
            return true;
        }else if(colorPickerShowing || purchasesShowing){
            proBtn.setEnabled(false);
            muteBtn.setEnabled(false);
            return false;
        }else{
            proBtn.setEnabled(true);
            muteBtn.setEnabled(true);
            return false;
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        if(!mute){
            blip.start();
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //TODO find out if return statements are necessary
        //noinspection SimplifiableIfStatement
        if (id == R.id.mute) {

            if(mute){
                blip.start();
            }

            muteBtn = this.topToolbar.getMenu().findItem(R.id.mute);
            if (mute) {
                mute = false;
                if (lightDark) {
                    muteBtn.setIcon(ContextCompat.getDrawable
                            (this, R.drawable.unmuted_white));
                } else {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.unmuted));
                }
                db.updateMute(mute);
            } else {
                mute = true;
                if (lightDark) {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted_white));
                } else {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted));
                }
                db.updateMute(mute);
            }
            return true;
        } else if (id == R.id.lightDark) {

            if(!mute){
                blip.start();
            }

            if (lightDark) {
                lightDark = false;
                white.setVisibility(View.VISIBLE);
                lightYellow.setVisibility(View.VISIBLE);
                lightBlue.setVisibility(View.VISIBLE);
                lightOrange.setVisibility(View.VISIBLE);
                lightPurple.setVisibility(View.VISIBLE);
                lightRed.setVisibility(View.VISIBLE);
                lightPink.setVisibility(View.VISIBLE);
                lightGreen.setVisibility(View.VISIBLE);
                if (highlight.equals("#FF000000")) {
                    highlight = "#FFFFFFFF";
                } else if (highlight.equals("#FFFF0000")) {
                    highlight = "#FFFF6347";
                } else if (highlight.equals("#FF228B22")) {
                    highlight = "#FF00FF00";
                } else if (highlight.equals("#FFFFD700")) {
                    highlight = "#FFFFFF00";
                } else if (highlight.equals("#FF4169E1")) {
                    highlight = "#FF00FFFF";
                } else if (highlight.equals("#FFFF8C00")) {
                    highlight = "#FFFFA500";
                } else if (highlight.equals("#FF8A2BE2")) {
                    highlight = "#FF9370DB";
                } else if (highlight.equals("#FFFF69B4")) {
                    highlight = "#FFFF1493";
                }
                db.updateHighlight(highlight);
                checkLightDark(lightDark);
                topToolbar.setTitleTextColor(Color.parseColor(highlight));
                addIcon.setTextColor(Color.parseColor(highlight));
                taskNameEditText.setBackgroundColor(Color.parseColor(highlight));
                if (mute) {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted));
                } else {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.unmuted));
                }
                db.updateDarkLight(false);
                lightDarkBtn.setTitle("Light Mode");
            } else {
                lightDark = true;
                black.setVisibility(View.VISIBLE);
                darkYellow.setVisibility(View.VISIBLE);
                darkBlue.setVisibility(View.VISIBLE);
                darkOrange.setVisibility(View.VISIBLE);
                darkPurple.setVisibility(View.VISIBLE);
                darkRed.setVisibility(View.VISIBLE);
                darkPink.setVisibility(View.VISIBLE);
                darkGreen.setVisibility(View.VISIBLE);
                if (highlight.equals("#FFFFFFFF")) {
                    highlight = "#FF000000";
                } else if (highlight.equals("#FFFF6347")) {
                    highlight = "#FFFF0000";
                } else if (highlight.equals("#FF00FF00")) {
                    highlight = "#FF228B22";
                } else if (highlight.equals("#FFFFFF00")) {
                    highlight = "#FFFFD700";
                } else if (highlight.equals("#FF00FFFF")) {
                    highlight = "#FF4169E1";
                } else if (highlight.equals("#FFFFA500")) {
                    highlight = "#FFFF8C00";
                } else if (highlight.equals("#FF9370DB")) {
                    highlight = "#FF8A2BE2";
                } else if (highlight.equals("#FFFF1493")) {
                    highlight = "#FFFF69B4";
                }
                db.updateHighlight(highlight);
                checkLightDark(lightDark);
                topToolbar.setTitleTextColor(Color.parseColor(highlight));
                addIcon.setTextColor(Color.parseColor(highlight));
                taskNameEditText.setBackgroundColor(Color.parseColor(highlight));
                if (mute) {
                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted_white));
                } else {
                    muteBtn.setIcon(ContextCompat.getDrawable
                            (this, R.drawable.unmuted_white));
                }
//                proBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.pro_white));
                db.updateDarkLight(true);
                lightDarkBtn.setTitle("Dark Mode");
            }
            noTasksLeft();
            return true;
        } else if (id == R.id.highlight) {

            if(!mute){
                blip.start();
            }

            colorPickerShowing = true;
            add.setClickable(false);
            theListView.setOnItemClickListener(null);
            taskPropertiesShowing = false;
            onCreateOptionsMenu(topToolbar.getMenu());
            theListView.setAdapter(theAdapter[0]);
            colorPicker.startAnimation(AnimationUtils.loadAnimation
                    (this, R.anim.enter_from_right));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
                    colorPicker.setVisibility(View.VISIBLE);
                }
            };

            handler.postDelayed(runnable, 200);

            return true;
        } else if (id == R.id.buy) {

            if(!mute){
                blip.start();
            }

            purchasesShowing = true;
            add.setClickable(false);
            theListView.setOnItemClickListener(null);
            taskPropertiesShowing = false;
            onCreateOptionsMenu(topToolbar.getMenu());
            theListView.setAdapter(theAdapter[0]);
            purchases.startAnimation(AnimationUtils.loadAnimation
                    (this, R.anim.enter_from_right));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
                    purchases.setVisibility(View.VISIBLE);
                }
            };

            handler.postDelayed(runnable, 200);

            return true;

        }

        return super.onOptionsItemSelected(item);
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

        taskListSize--;
        db.updateTaskListSize(taskListSize);
        db.updateChecklistListSize(taskListSize);

        //deleting data related to deleted task
        db.deleteData(String.valueOf(sortedIDs.get(position)));
        db.deleteAlarmData(String.valueOf(sortedIDs.get(position)));
        db.deleteSnoozeData(String.valueOf(sortedIDs.get(position)));
        db.deleteAllSubtaskData(String.valueOf(sortedIDs.get(position)));

        //Cancel notification alarms if one is set
        alarmManager.cancel(pendIntent.getService(this,
                Integer.parseInt(sortedIDs.get(position)), alertIntent, 0));

        sortedIDs.remove(position);

        Cursor result;
        String id;
        String note;
        Boolean checklist;

        //Getting existing data before going to Database class to change ids.
        for(int i = (activeTask + 1); i < taskListSize; i++){
            result = db.getData(Integer.parseInt(sortedIDs.get(i)));
            id = "";
            note = "";
            checklist = false;
            while(result.moveToNext()){
                id = result.getString(0);
                note = result.getString(1);
                checklist = result.getInt(2) == 1;
            }
            db.updateData(id, note, checklist);
            result.close();
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

        datePickerShowing = false;

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

        toast.setText(R.string.taskReinstated);
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                sweep.start();
                toast.startAnimation(AnimationUtils.loadAnimation
                        (MainActivity.this, R.anim.enter_from_right_fast));
                toast.setVisibility(View.VISIBLE);
                final Handler handler2 = new Handler();
                final Runnable runnable2 = new Runnable(){
                    public void run(){
                        toast.startAnimation(AnimationUtils.loadAnimation
                                (MainActivity.this, android.R.anim.fade_out));
                        toast.setVisibility(View.GONE);
                    }
                };
                handler2.postDelayed(runnable2, 1500);
            }
        };

        handler.postDelayed(runnable, 500);

        //marks task as not killed in database
        db.updateKilled(toString().valueOf(MainActivity.sortedIDs.get(i)), false);

        reinstateAlarm = true;

//        reinstateAnimation = true;
//        animatePosition = i;
//        animateID = Integer.parseInt(MainActivity.sortedIDs.get(i));

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
            Cursor dbResult = db.getData(Integer.parseInt(
                    sortedIDs.get(i)));
            while (dbResult.moveToNext()) {
                dbTimestamp = dbResult.getString(3);
            }
            dbResult.close();

            tempList.add(Integer.valueOf(dbTimestamp));

        }

        //Ordering list by time task was created
        ArrayList<String> whenTaskCreated = new ArrayList<>();
        for(int i = 0; i < MainActivity.taskListSize; i++){
            String created = "";
            Cursor createdResult = db.getData(Integer.parseInt
                    (sortedIDs.get(i)));
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
        ArrayList<String> tempDueIdsList = new ArrayList<>();
        ArrayList<String> tempDueTaskList = new ArrayList<>();

        //getting tasks which have no due date
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting task data
            int dbId = 0;
            String dbTimestamp = "";
            String dbTask = "";
            Boolean dbKilled = false;
            Cursor dbResult = db.getDataByTimestamp(
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
            Cursor dbResult = db.getDataByDueTime(
                    String.valueOf(tempList.get(i)));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTask = dbResult.getString(4);
            }
            dbResult.close();

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

        for(int i = 0; i < MainActivity.taskListSize; i++){

            MainActivity.db.updateSortedIndex(String.valueOf(i),
                    Integer.parseInt(tempIdsList.get(i)));

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
                db.updateTaskListSize(taskListSize);
                db.updateChecklistListSize(taskListSize);

                //finding unique ID for task
                int i = 0;
                boolean idIsSet = false;
                while (!idIsSet) {
                    if (sortedIDs.contains(String.valueOf(i))) {
                        i++;
                    } else {
                        sortedIDs.add(taskListSize - 1, String.valueOf(i));
                        db.updateSortedIndex(String.valueOf(taskListSize - 1), i);
                        idIsSet = true;
                    }
                }

                alertIntent = new Intent(this, AlertReceiver.class);

                pendIntent = PendingIntent.getBroadcast(this, i, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

            }else{

                taskList.set(activeTask, taskName);

                db.updateName(sortedIDs.get(activeTask), taskName);

            }

        }

    }

    //Tells user to add tasks when task list is empty
    private void noTasksLeft() {

        //Checks if there are any existing tasks
        if (taskListSize == 0){

            //Inform user to add some tasks
            if(lightDark) {
                noTasksToShowWhite.setVisibility(View.VISIBLE);
            }else{
                noTasksToShowWhite.setVisibility(View.GONE);
                noTasksToShow.setVisibility(View.VISIBLE);
            }

        }else{

            noTasksToShow.setVisibility(View.GONE);
            noTasksToShowWhite.setVisibility(View.GONE);

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

    private void colorPickerShowing() {
        if(colorPickerShowing) {
            colorPicker.startAnimation(AnimationUtils.loadAnimation
                    (this, R.anim.exit_out_left));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
                    colorPicker.setVisibility(View.GONE);
                }
            };

            handler.postDelayed(runnable, 200);
            colorPickerShowing = false;
        }else{
            purchases.startAnimation(AnimationUtils.loadAnimation
                    (this, R.anim.exit_out_left));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
                    purchases.setVisibility(View.GONE);
                }
            };

            handler.postDelayed(runnable, 200);
            purchasesShowing = false;
        }
            theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    //Tasks are not clickable if keyboard is up
                    if(tasksAreClickable && !completeTask) {

//                    vibrate.vibrate(50);

                        //checking if task has been killed
                        Boolean killed = false;
                        Cursor result = db.getData(Integer
                                .parseInt(sortedIDs.get(position)));
                        while (result.moveToNext()) {
                            killed = result.getInt(6) > 0;
                        }
                        result.close();

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
            add.setClickable(true);
            onCreateOptionsMenu(topToolbar.getMenu());
            theListView.setAdapter(theAdapter[0]);
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

    public void setHighlightWhite(View view) {
        setHighlight("#FFFFFFFF");
    }

    public void setHighlightBlack(View view) {
        setHighlight("#FFFFFFFF");
    }

    public void setHighlightLightYellow(View view) {
        setHighlight("#FFFFFF00");
    }

    public void setHighlightDarkYellow(View view) {
        setHighlight("#FFFFD700");
    }

    public void setHighlightLightBlue(View view) {
        setHighlight("#FF00FFFF");
    }

    public void setHighlightDarkBlue(View view) {
        setHighlight("#FF4169E1");
    }

    public void setHighlightLightOrange(View view) {
        setHighlight("#FFFFA500");
    }

    public void setHighlightDarkOrange(View view) {
        setHighlight("#FFFF8C00");
    }

    public void setHighlightLightPurple(View view) {
        setHighlight("#FF9370DB");
    }

    public void setHighlightDarkPurple(View view) {
        setHighlight("#FF8A2BE2");
    }

    public void setHighlightDarkRed(View view) {
        setHighlight("#FFFF0000");
    }

    public void setHighlightLightRed(View view) {
        setHighlight("#FFFF6347");
    }

    public void setHighlightLightPink(View view) {
        setHighlight("#FFFF69B4");
    }

    public void setHighlightDarkPink(View view) {
        setHighlight("#FFFF1493");
    }

    public void setHighlightLightGreen(View view) {
        setHighlight("#FF00FF00");
    }

    public void setHighlightDarkGreen(View view) {
        setHighlight("#FF228B22");
    }

    private void setHighlight(String s) {
        if(!mute){
            blip.start();
        }
        db.updateHighlight(s);
        highlight = s;
        topToolbar.setTitleTextColor(Color.parseColor(s));
        addIcon.setTextColor(Color.parseColor(s));
        taskNameEditText.setBackgroundColor(Color.parseColor(s));
        setDividers(lightDark);
        toast.setBackgroundColor(Color.parseColor(highlight));
        colorPickerShowing();
    }

    private void setDividers(boolean lightDark) {
        String digits = "0123456789ABCDEF";
        int val = 0;
        for (int i = 1; i < highlight.length(); i++) {
            char c = highlight.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        int[] colors = {0, val, 0};
        theListView.setDivider(new GradientDrawable
                (GradientDrawable.Orientation.RIGHT_LEFT, colors));
        if(!lightDark) {
            theListView.setDividerHeight(1);
        }else{
            theListView.setDividerHeight(3);
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId,
                                   @Nullable TransactionDetails details) {
        toast.setText("You purchased something");
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                sweep.start();
                toast.startAnimation(AnimationUtils.loadAnimation
                        (MainActivity.this, R.anim.enter_from_right_fast));
                toast.setVisibility(View.VISIBLE);
                final Handler handler2 = new Handler();
                final Runnable runnable2 = new Runnable(){
                    public void run(){
                        toast.startAnimation(AnimationUtils.loadAnimation
                                (MainActivity.this, android.R.anim.fade_out));
                        toast.setVisibility(View.GONE);
                    }
                };
                handler2.postDelayed(runnable2, 1500);
            }
        };

        handler.postDelayed(runnable, 500);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        toast.setText("Something went wrong");
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                sweep.start();
                toast.startAnimation(AnimationUtils.loadAnimation
                        (MainActivity.this, R.anim.enter_from_right_fast));
                toast.setVisibility(View.VISIBLE);
                final Handler handler2 = new Handler();
                final Runnable runnable2 = new Runnable(){
                    public void run(){
                        toast.startAnimation(AnimationUtils.loadAnimation
                                (MainActivity.this, android.R.anim.fade_out));
                        toast.setVisibility(View.GONE);
                    }
                };
                handler2.postDelayed(runnable2, 1500);
            }
        };

        handler.postDelayed(runnable, 500);
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    public void removeAds(View view) {

        if(!mute) {
            chime.start();
        }

        //TODO replace this test stuff with real stuff
        bp.purchase(this, "android.test.purchased");
        //TODO verify purchase before updating to true
        db.updateAdsRemoved(true);
    }

    public void getReminders(View view) {

        if(!mute){
            chime.start();
        }

        //TODO replace this test stuff with real stuff
        bp.purchase(this, "android.test.purchased");
        //TODO verify purchase before updating to true
        db.updateRemindersAvailable(true);
    }

    public void cycleColors(View view) {

        if(!mute){
            chime.start();
        }

        //TODO replace this test stuff with real stuff
        bp.purchase(this, "android.test.purchased");
        //TODO verify purchase before updating to true
        db.updateCycleColors(true);
    }

    public void unlockAll(View view) {

        if(!mute){
            chime.start();
        }

        //TODO replace this test stuff with real stuff
        bp.purchase(this, "android.test.purchased");
        //TODO verify purchase before updating to true
        db.updateAdsRemoved(true);
        db.updateRemindersAvailable(true);
        db.updateCycleColors(true);
    }

//TODO use the following where users make purchases
    //bp.purchase(MainActivity.this, "android.test.purchased");
    //TODO fill in information
    //Without developer payload
    //bp.purchase(YOUR_ACTIVITY, "YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE");
    //With developer payload
    //bp.purchase(YOUR_ACTIVITY, "YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE", "DEVELOPER PAYLOAD HERE");

    //TODO find out if this is required
//            Bundle extraParams = new Bundle()
//            extraParams.putString("accountId", "MY_ACCOUNT_ID");
//            bp.purchase(YOUR_ACTIVITY, "YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE", null /*or developer payload*/, extraParams);
//            bp.subscribe(YOUR_ACTIVITY, "YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE", null /*or developer payload*/, extraParams);

    @Override
    protected void onPause(){

        super.onPause();

        //TODO find out what's going on here. Mute back button?
//        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);

        //TODO check if this line is needed
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

        //TODO find out what's going on here. Mute back button?
//        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

    }

    private void getSavedData() throws URISyntaxException {

        //clearing the lists before adding data back into them so as to avoid duplication
        taskList.clear();
        sortedIDs.clear();

        //getting app-wide data
        Cursor dbResult = db.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
            highlight = dbResult.getString(2);
            lightDark = dbResult.getInt(3) > 0;
            adsRemoved = dbResult.getInt(5) > 0;
            remindersAvailable = dbResult.getInt(6) > 0;
            cycleColors = dbResult.getInt(7) > 0;
            taskListSize = dbResult.getInt(8);
        }
        dbResult.close();

        checkLightDark(lightDark);

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

        for(int i = 0; i < taskListSize; i++){
            Cursor sharedPreferencesResult = db.getData(Integer
                    .parseInt(sortedIDs.get(i)));
            while (sharedPreferencesResult.moveToNext()) {
                taskList.add(sharedPreferencesResult.getString(4));
            }
            sharedPreferencesResult.close();
        }

        reorderList();

        alertIntent = new Intent(this, AlertReceiver.class);

        theListView.setAdapter(theAdapter[0]);

        //Checks to see if there are still tasks left
        noTasksLeft();

    }

    @Override
    //Return to previous selection when back is pressed
    public void onBackPressed() {

        //options to properties
        if(colorPickerShowing) {
            colorPickerShowing();
        }else if (purchasesShowing){
            colorPickerShowing();
        }else if(taskOptionsShowing){
            theListView.setAdapter(theAdapter[0]);
            taskOptionsShowing = false;
        }else if(datePickerShowing) {
            //date picker to properties
            if(!alarmOptionsShowing) {
                exitDatePicker = true;
                datePickerShowing = false;
                theListView.setAdapter(theAdapter[0]);
                //change due date to alarm options
            }else{
                exitChangeDueDate = true;
                datePickerShowing = false;
                theListView.setAdapter(theAdapter[0]);
            }
            //time picker to date picker
        }else if(timePickerShowing) {
            exitTimePicker = true;
            datePickerShowing = true;
            timePickerShowing = false;
            theListView.setAdapter(theAdapter[0]);
            //repeat to alarm options
        }else if(repeatShowing){
            exitRepeat = true;
            repeatShowing = false;
            theListView.setAdapter(theAdapter[0]);
            //alarm options to properties
        }else if(alarmOptionsShowing){
            exitAlarmOptions  = true;
            alarmOptionsShowing = false;
            theListView.setAdapter(theAdapter[0]);
            //Properties to home
        }else if (taskPropertiesShowing){
            exitTaskProperties = true;
            removeTaskProperties();
            //Exit app
        }else{
            super.onBackPressed();
        }

    }

}