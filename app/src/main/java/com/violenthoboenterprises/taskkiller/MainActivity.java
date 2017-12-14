package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.icu.util.GregorianCalendar;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import static java.security.AccessController.getContext;

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

    static boolean centerTask;

    boolean dateOrTime;


    //Indicates which task has it's properties showing
    static int activeTask;
    //Saves the size of the task list
    static int taskListSize;
    //Height of the 'add' button
    static int addHeight;
    //Measures to determine if keyboard is up
    private int heightDiff;

    static int checklistListSize;

    static int listViewHeight;

//    static int broadcastID;

    static int notificationCount;

    //TODO remove array
    static ArrayList<Integer> broadcastID;

    //TODO remove array
//    static Intent[] alertIntent;
    static ArrayList<Intent> alertIntent;

    //TODO remove intentCount and turn array into single variable if doesn't work. Might not even need this
//    static PendingIntent[] pendingIntent;
    static ArrayList<PendingIntent> pendingIntent;
    static int intentCount;


    //List of tasks
    public static ArrayList<String> taskList;
    //Keeps track of tasks that are completed but not removed
    static ArrayList<Boolean> tasksKilled;

    static ArrayList<Boolean> showTaskDueIcon;

    //TODO fix alarm manager
//    static ArrayList<AlarmManager> alarmManager;
    static AlarmManager alarmManager;

    //Message that shows up when there are no tasks
    private TextView noTasksToShow;

    //The editable text box that allows for creating and editing task names
    static EditText taskNameEditText;

    //The button that facilitates the adding of tasks
    static Button add;

    //Scrollable list
    static ListView theListView;

    static InputMethodManager keyboard;

    //Parameters of 'add' button
    static RelativeLayout.LayoutParams params;

    static SharedPreferences mSharedPreferences;
    static SharedPreferences nSharedPreferences;

    static View activityRootView;

    static Vibrator vibrate;

    public ListAdapter[] theAdapter;

    //Inflater for checklists
    static LayoutInflater inflater;

    //String used for debugging
    String TAG;

    //Notify the user that something happened in the background
    NotificationManager notificationManager;

    //Tracks if notification is active in the task bar
    boolean isNotificActive = false;

    //Tracks notifications
    int notifID = 33;

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
        //TODO fix alarm manager
//        alarmManager = new ArrayList<>();
        noTasksToShow = (TextView) findViewById(R.id.noTasks);
        taskNameEditText = (EditText) findViewById(R.id.taskNameEditText);
        add = (Button) findViewById(R.id.add);
        theListView = (ListView) findViewById(R.id.theListView);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        params = (RelativeLayout.LayoutParams) add.getLayoutParams();
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        inflater = LayoutInflater.from(getApplicationContext());
        addHeight = params.height;
        theAdapter = new ListAdapter[]{new MyAdapter(this, taskList)};
        TAG = "mainActivity";
        //Getting the main view layout
        activityRootView = findViewById(R.id.activityRoot);
        fadeTasks = false;
        dateOrTime = false;
        intentCount = 0;
        pendingIntent = new ArrayList<>();
        alertIntent = new ArrayList<>();
        notificationCount = 0;
        broadcastID = new ArrayList<>();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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

                    //Put options underneath selected task
                    taskList.add(position + 1, "properties");

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

                    MainActivity.checklistShowing = true;

                    //updating Checklist's shared preferences without inflating the class view

                    //getSavedData

                    //skip this management of sub tasks if there are no sub tasks
                    try {

                        checklistListSize = nSharedPreferences.getInt("checklistListSizeKey", 0);

                        for (int i = 0; i < checklistListSize; i++) {

                            Checklist.checklistSize = nSharedPreferences.getInt("checklistSizeKey" + String.valueOf(i), 0);

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

                                Checklist.checklistList.get(i).set(j, nSharedPreferences.getString("checklistItemKey"
                                        + String.valueOf(i) + String.valueOf(j), ""));

                                Checklist.subTasksKilled.get(i).set(j, nSharedPreferences.getBoolean("subTasksKilledKey"
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

                        nSharedPreferences.edit().putInt("checklistListSizeKey", Checklist.checklistListSize).apply();

                        for (int i = 0; i < checklistListSize; i++) {

                            //Getting and saving the size of each array list of sub tasks
                            Checklist.checklistSize = Checklist.checklistList.get(i).size();

                            nSharedPreferences.edit().putInt("checklistSizeKey" + String.valueOf(i),
                                    Checklist.checklistSize).apply();

                        }

                        //Saving each individual sub task
                        for (int i = 0; i < checklistListSize; i++) {

                            Checklist.checklistSize = Checklist.checklistList.get(i).size();

                            for (int j = 0; j < Checklist.checklistSize; j++) {

                                nSharedPreferences.edit().putString("checklistItemKey" + String.valueOf(i)
                                        + String.valueOf(j), Checklist.checklistList.get(i).get(j)).apply();

                                nSharedPreferences.edit().putBoolean("subTasksKilledKey" + String.valueOf(i)
                                        + String.valueOf(j), Checklist.subTasksKilled.get(i).get(j)).apply();

                            }

                        }

                    } catch (NullPointerException e) {

                    }

                    taskList.remove(position);

                    //Updates the view
                    theListView.setAdapter(theAdapter[0]);

                    tasksKilled.remove(position);

                    alertIntent.remove(position);

                    pendingIntent.remove(position);

                    showTaskDueIcon.remove(position);

                    broadcastID.remove(position);

                    //TODO fix alarm manager
//                    alarmManager.remove(position);

                    //Checks to see if there are still tasks left
                    noTasksLeft();

                //Removes task options from view
                } else {

                    //set background to white
                    activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //Removes the task's properties
                    taskList.remove(activeTask + 1);

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

                //Cannot long click last list item with another item's properties showing to prevent index out of bounds error
                if (taskPropertiesShowing && (position != 0)) {

                    i = position - 1;

                }

                //Determine if it's possible to edit task
                if (tasksAreClickable && !tasksKilled.get(i) && !taskPropertiesShowing) {

                    //Cannot update the list until after the task has been updated.
                    goToMyAdapter = false;

                    //Actions to occur when keyboard is showing
                    checkKeyboardShowing();

                    //Indicates that a task is being edited
                    taskBeingEdited = true;

                    activeTask = position;

                    tasksAreClickable = false;

                    fadeTasks = true;

                    centerTask = true;

                    theListView.setAdapter(theAdapter[0]);

                    //Can't change visibility of 'add' button. Have to set height to zero instead.
                    params.height = 0;

                    add.setLayoutParams(params);

                } else if (tasksAreClickable && tasksKilled.get(i) && !taskPropertiesShowing) {

                    Toast.makeText(MainActivity.this, "Task Reinstated", Toast.LENGTH_SHORT).show();

                    tasksKilled.set(i, false);

                    theListView.setAdapter(theAdapter[0]);

                }

                return true;

            }

        });

        //Actions to occur when 'add' selected
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(MainActivity.this, String.valueOf(notificationCount), Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "notification count: " + String.valueOf(notificationCount));
//                Log.i(TAG, "intent count: " + String.valueOf(intentCount));
//                Log.i(TAG, "alert intent: " + String.valueOf(alertIntent));
                Log.i(TAG, "pending intent: " + String.valueOf(pendingIntent));
                Log.i(TAG, "broadcast id: " + String.valueOf(broadcastID));

                goToMyAdapter = true;

                vibrate.vibrate(50);

                //Removes any visible task options
                if(taskPropertiesShowing){

                    taskList.remove(activeTask + 1);

                    theListView.setAdapter(theAdapter[0]);

                    taskPropertiesShowing = false;

                }

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

    public void showNotification(View view) {

        android.support.v4.app.NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Message").setContentText("New Message").setTicker("Alert New Message").setSmallIcon(R.drawable.bell);

        //Intention to open MainActivity when notification clicked
        Intent moreInfoIntent = new Intent(this, MainActivity.class);

        //stack tasks across activites so we go to the proper place when back is clicked
        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);

        //parents of this activity added to the stack
        tStackBuilder.addParentStack(MainActivity.class);

        //add intent to stack
        tStackBuilder.addNextIntent(moreInfoIntent);

        //update intent if needed
        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificBuilder.setContentIntent(pendingIntent);

        //remove notification on click
        notificBuilder.setAutoCancel(true);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        broadcastID = (int) (System.currentTimeMillis() / 10000);

        //show notification
//        notificationManager.notify(/*notifID*/broadcastID, notificBuilder.build());

        //can't stop notification that has already been stopped
        isNotificActive = true;

    }

    //TODO remove this method if not used
    public void stopNotification(View view) {

        // If the notification is still active close it
        if(isNotificActive){

            notificationManager.cancel(notifID);

        }

    }

    public void setAlarm(View view){

        DatePicker datePicker = findViewById(R.id.datePicker);

        TimePicker timePicker = findViewById(R.id.timePicker);

        Button dateButton = findViewById(R.id.date);

        //actions to occur when date has been chosen
        if(!dateOrTime){

            datePicker.setVisibility(View.GONE);

            timePicker.setVisibility(View.VISIBLE);

            dateOrTime = true;

            dateButton.setText("Set Time");

        //actions to occur when time has been chosen
        }else{

            Calendar calendar = Calendar.getInstance();

            //setting alarm
            calendar.set(Calendar.YEAR, datePicker.getYear());
            calendar.set(Calendar.MONTH, datePicker.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(calendar.SECOND, 0);

            //TODO this probably doesn't even need to be an arraylist
            //intention to execute AlertReceiver
            alertIntent.set(activeTask, new Intent(this, AlertReceiver.class));/* = new Intent(this, AlertReceiver.class);*/

            //setting the name of the task for which the notification is being set
            alertIntent.get(activeTask).putExtra("ToDo", taskList.get(activeTask));

            //TODO find better way to get IDs
//            broadcastID = (int) (/*calendar.getTimeInMillis()*/System.currentTimeMillis() / 10000);
            broadcastID.set(activeTask, (int) (System.currentTimeMillis() / 10000));

            //TODO use an array list for these and try cancelling the correct one when needed
//            pendingIntent[intentCount] = PendingIntent.getBroadcast(this, broadcastID/*intentCount/*requestCode*/, alertIntent.get(intentCount), PendingIntent./*FLAG_ONE_SHOT*/FLAG_UPDATE_CURRENT);
            pendingIntent.set(activeTask, PendingIntent.getBroadcast(this, broadcastID.get(activeTask)/*intentCount/*requestCode*/, alertIntent.get(activeTask), PendingIntent./*FLAG_ONE_SHOT*/FLAG_UPDATE_CURRENT));

            //setting the notification
            alarmManager/*.get(activeTask)*/.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent.get(activeTask)/*PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT)*/);

//            intentCount++;

//            notificationCount++;

            datePicker.setVisibility(View.VISIBLE);

            timePicker.setVisibility(View.GONE);

            dateOrTime = false;

            dateButton.setText("Set Time");

            //set background to white
            activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            taskList.remove(activeTask + 1);

            showTaskDueIcon.set(activeTask, true);

            theListView.setAdapter(theAdapter[0]);

            //Marks properties as not showing
            taskPropertiesShowing = false;

            //Returns the 'add' button
            params.height = addHeight;

            add.setLayoutParams(params);

        }

    }

    private void getSavedData() throws URISyntaxException {

        //clearing the lists before adding data back into them so as to avoid duplication
        taskList.clear();
        tasksKilled.clear();
        showTaskDueIcon.clear();
        alertIntent.clear();
        pendingIntent.clear();
        broadcastID.clear();

        checklistListSize = 0;

//        notificationCount = mSharedPreferences.getInt("notificationCountKey", 0);

//        notificationCount = notificationCount - AlertReceiver.notificationCount;

        //Existing tasks are recalled when app opened
        taskListSize = mSharedPreferences.getInt("taskListSizeKey", 0);

        for( int i = 0 ; i < taskListSize ; i++ ) {

            taskList.add(mSharedPreferences.getString("taskNameKey" + String.valueOf(i), ""));

            tasksKilled.add(mSharedPreferences.getBoolean("taskKilledKey" +
                    String.valueOf(i), false));

            showTaskDueIcon.add(mSharedPreferences.getBoolean("showTaskDueIcon" + String.valueOf(i), false));

            alertIntent.add(i, Intent.getIntent(mSharedPreferences.getString("alertIntentKey" + String.valueOf(i), "")));
//            alertIntent.set(i, new Intent(this, AlertReceiver.class));

            //TODO May have to rebuild this from scratch
//            pendingIntent.add(mSharedPreferences.getString("pendingIntentKey" + String.valueOf(i), ""));

            broadcastID.add(mSharedPreferences.getInt("broadcastIDKey" + String.valueOf(i), 0));

            Log.i(TAG, String.valueOf(broadcastID));
            Log.i(TAG, String.valueOf(alertIntent));

            pendingIntent.add(PendingIntent.getBroadcast(this, broadcastID.get(i), alertIntent.get(i), PendingIntent.FLAG_UPDATE_CURRENT));

        }

        theListView.setAdapter(theAdapter[0]);

        //Checks to see if there are still tasks left
        noTasksLeft();

    }

    //Actions to occur when keyboard is showing
    private void checkKeyboardShowing() {

        //TODO check out the hard coded pixels will work on all devices
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                heightDiff = activityRootView.getRootView().getHeight();

                Rect screen = new Rect();

                activityRootView.getWindowVisibleDisplayFrame(screen);

                //Screen pixel values are used to determine how much of the screen is visible
                heightDiff = activityRootView.getRootView().getHeight() -
                        (screen.bottom - screen.top);

                //Value of more than 800 seems to indicate that the keyboard is showing
                //in portrait mode
                if ((heightDiff > 800) && (getResources().getConfiguration().orientation == 1)) {

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
                }else if((heightDiff > 73) && (heightDiff < 800) && (getResources()
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

    //Create a new task
    private void createTask(final String taskName, ArrayList taskList, boolean taskBeingEdited) {

        //Don't allow blank tasks
        if(!taskName.equals("")) {

            if(!taskBeingEdited) {

                taskList.add(taskList.size(), taskName);

                tasksKilled.add(tasksKilled.size(), false);

                showTaskDueIcon.add(showTaskDueIcon.size(), false);

                alertIntent.add(alertIntent.size(), new Intent());

                pendingIntent.add(pendingIntent.size(), PendingIntent.getBroadcast(this, 0, alertIntent.get(0), PendingIntent.FLAG_UPDATE_CURRENT));

                broadcastID.add(broadcastID.size(), 0);

            }else{

                taskList.set(activeTask, taskName);

            }

        }

    }

    @Override
    protected void onPause(){

        super.onPause();

        mSharedPreferences.edit().putInt("notificationCountKey", notificationCount).apply();

        //Important to know if task properties are showing so as to not corrupt the list
        if (!taskPropertiesShowing) {

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

//                Log.i(TAG, String.valueOf(alertIntent.get(i).getData()));

                mSharedPreferences.edit().putString("alertIntentKey" + String.valueOf(i), String.valueOf(alertIntent.get(i))).apply();

                mSharedPreferences.edit().putString("pendingIntentKey" + String.valueOf(i), pendingIntent.get(i).toString()).apply();

                mSharedPreferences.edit().putInt("broadcastIDKey" + String.valueOf(i), broadcastID.get(i)).apply();

            }

        } else {

            //Removes the task properties from the list because they should not
            //be saved on pause.
            taskList.remove(activeTask + 1);

            //Update the list
            theListView.setAdapter(theAdapter[0]);

            taskPropertiesShowing = false;

            //Tasks are saved in a manner so that they don't vanish when app closed
            taskListSize = taskList.size();

            mSharedPreferences.edit().putInt("taskListSizeKey", taskListSize).apply();

            for (int i = 0; i < taskListSize - 1; i++) {

                mSharedPreferences.edit().putString("taskNameKey" + String.valueOf(i),
                        taskList.get(i)).apply();

                mSharedPreferences.edit().putBoolean("taskKilledKey" + String.valueOf(i),
                        tasksKilled.get(i)).apply();

                mSharedPreferences.edit().putBoolean("showTaskDueIcon" + String.valueOf(i),
                        showTaskDueIcon.get(i)).apply();

                mSharedPreferences.edit().putString("alertIntentKey" + String.valueOf(i), alertIntent.get(i).toString()).apply();

                mSharedPreferences.edit().putString("pendingIntentKey" + String.valueOf(i), pendingIntent.get(i).toString()).apply();

                mSharedPreferences.edit().putInt("broadcastIDKey" + String.valueOf(i), broadcastID.get(i)).apply();

            }

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

}