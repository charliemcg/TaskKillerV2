package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

class MyAdapter extends ArrayAdapter<String> {

    //String for debugging
    final String TAG = "MyAdapter";

    public MyAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.task_layout, values);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        //get data from array
        final String task = getItem(position);
        //Uses unique layout for the new item
        final LayoutInflater theInflater = LayoutInflater.from(getContext());
        final View taskView = theInflater.inflate(R.layout.task_layout, parent, false);
        final TextView theTextView = taskView.findViewById(R.id.textView);
        final Intent intent = new Intent(getContext(), Checklist.class);
        final Intent noteIntent = new Intent(getContext(), Note.class);
        final TableRow propertyRow = taskView.findViewById(R.id.properties);
        final TableRow dateRow = taskView.findViewById(R.id.dateTime);
        final TableRow optionsRow = taskView.findViewById(R.id.options);
        final TableRow alarmOptionsRow = taskView.findViewById(R.id.alarmOptions);
        final TableRow repeatRow = taskView.findViewById(R.id.repeat);
        final TableRow adRow = taskView.findViewById(R.id.adRow);
        final TableRow snoozeRow = taskView.findViewById(R.id.snoozeRow);
        final TableRow taskOverdueRow = taskView.findViewById(R.id.taskIsOverdue);
        final DatePicker datePicker = taskView.findViewById(R.id.datePicker);
        final TimePicker timePicker = taskView.findViewById(R.id.timePicker);
        TextView dueTextView = taskView.findViewById(R.id.dueTextView);

        //Displaying ad if there are five or more tasks
        if(position == 4) {
            adRow.setVisibility(View.VISIBLE);
            boolean networkAvailable = false;
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                networkAvailable = true;
            }

            //Initialising banner ad
            final AdView adView = taskView.findViewById(R.id.adView);
            ImageView banner = taskView.findViewById(R.id.banner);

            if (networkAvailable) {
                adView.setVisibility(View.VISIBLE);
                final AdRequest banRequest = new AdRequest.Builder()
                        .addTestDevice("7A57C74D0EDE338C302869CB538CD3AC")/*.addTestDevice
                    (AdRequest.DEVICE_ID_EMULATOR)*/.build();//TODO remove .addTestDevice()
                adView.loadAd(banRequest);
            } else {
                banner.setVisibility(View.VISIBLE);
            }
        }

        //TODO make sure hard coded values work on all devices
        //Task cannot be centered unless it's in view. Moving selected task into view
        // if not already in view in portrait.
        if ((MainActivity.activeTask > 6) && MainActivity.centerTask && (getContext()
                .getResources().getConfiguration().orientation == 1)) {
            MainActivity.theListView.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.theListView.setSelection(MainActivity.activeTask);
                }
            });
            MainActivity.centerTask = false;
        //Same as above but for landscape.
        } else if ((MainActivity.activeTask > 3) && MainActivity.centerTask && (getContext()
                .getResources().getConfiguration().orientation == 2)) {
            MainActivity.theListView.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.theListView.setSelection(MainActivity.activeTask);
                }
            });
            MainActivity.centerTask = false;
        }

        if(MainActivity.reorderList){
            reorderList();
            MainActivity.reorderList = false;
        }

        if(MainActivity.reinstateAlarm){

            String stamp = "";
            int broadcast = 0;
            Boolean isDue = false;
            Boolean isRepeating = false;
            Cursor alarmResult = MainActivity.noteDb.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(position)));
            while (alarmResult.moveToNext()) {
                stamp = alarmResult.getString(3);
                isDue = alarmResult.getInt(5) > 0;
                broadcast = alarmResult.getInt(7);
                isRepeating = alarmResult.getInt(8) > 0;
            }

            //check if there's an alarm in the first place
            if(isDue) {

                //setting a repeating notification
                if (isRepeating) {

                    MainActivity.alarmManager.setInexactRepeating(AlarmManager.RTC,
                            Long.parseLong(stamp), MainActivity.repeatInterval,
                            MainActivity.pendIntent.getService(getContext(), broadcast,
                                    MainActivity.alertIntent, 0));

                    MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                            .get(MainActivity.activeTask), true);

                    MainActivity.repeatShowing = false;

                //setting a one-time notification
                } else {

                    MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong(stamp),
                            MainActivity.pendIntent.getService(getContext(),
                                    broadcast, MainActivity.alertIntent, 0));

                }

            }

            MainActivity.reinstateAlarm = false;

        }

        //actions to occur in regards to selected task
        if(MainActivity.taskPropertiesShowing && position == MainActivity.activeTask){

            Boolean showOverdue = false;
            Cursor dueResult = MainActivity.noteDb.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(position)));
            while (dueResult.moveToNext()) {
                showOverdue = dueResult.getInt(9) > 0;
            }

            //Determine whether to show datepicker
            if(MainActivity.datePickerShowing) {

                dateRow.setVisibility(View.VISIBLE);
                MainActivity.dateOrTime = true;

            //TODO put this in a separate method to avoid code duplication
            }else if(MainActivity.alarmOptionsShowing){

                Button killAlarmBtn = taskView.findViewById(R.id.killAlarmBtn);
                Button resetAlarmBtn = taskView.findViewById(R.id.resetAlarmBtn);
                Button repeatAlarmBtn = taskView.findViewById(R.id.repeatBtn);

                alarmOptionsRow.setVisibility(View.VISIBLE);

                propertyRow.setVisibility(View.GONE);

                MainActivity.alarmOptionsShowing = true;

                //Actions to occur if user selects 'remove alarm'
                killAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.noteDb.updateDue(toString().valueOf(MainActivity
                                .sortedIDs.get(MainActivity.activeTask)), false);

                        MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                                .get(position), false);

                        MainActivity.alarmManager.cancel(MainActivity.pendIntent.getService(
                                getContext(), Integer.parseInt(MainActivity.sortedIDs
                                        .get(position)), MainActivity.alertIntent, 0));

                        MainActivity.noteDb.updateAlarmData(String.valueOf(MainActivity.activeTask),
                                "", "", "", "", "", "");

                        MainActivity.alarmOptionsShowing = false;

                        notifyDataSetChanged();

                    }
                });

                //Actions to occur if user selects 'change due date'
                resetAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.datePickerShowing = true;

                        MainActivity.dateRowShowing = true;

                        notifyDataSetChanged();

                    }
                });

                //Actions to occur if user selects 'repeat alarm'
                repeatAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alarmOptionsRow.setVisibility(View.GONE);

                        repeatRow.setVisibility(View.VISIBLE);

                        MainActivity.alarmOptionsShowing = false;

                    }
                });
            //show the tasks properties
            }else if(showOverdue){

                taskOverdueRow.setVisibility(View.VISIBLE);
//                MainActivity.noteDb.updateOverdue(toString().valueOf(
//                MainActivity.sortedIDs.get(position)));

                Button snoozeTask = taskView.findViewById(R.id.snoozeTask);
                Button taskDone = taskView.findViewById(R.id.taskDone);
                Button taskIgnore = taskView.findViewById(R.id.taskIgnore);
                final Button oneHourBtn = taskView.findViewById(R.id.oneHour);
                final Button fourHourBtn = taskView.findViewById(R.id.fourHours);
                final Button tomorrowBtn = taskView.findViewById(R.id.tomorrow);

                //Actions to occur if user selects 'snooze'
                snoozeTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        taskOverdueRow.setVisibility(View.GONE);

                        snoozeRow.setVisibility(View.VISIBLE);

                        //Actions to occur if user selects '1 hour'
                        oneHourBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainActivity.alarmManager.cancel(MainActivity.pendIntent.getService(getContext(),
                                        Integer.parseInt(MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                        MainActivity.alertIntent, 0));

                                Calendar currentDate = new GregorianCalendar();

                                //Checking that task due date is in the future
//                                if (currentDate.get(Calendar.YEAR) > Integer.valueOf(datePicker.getYear())) {
//                                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
//                                            Toast.LENGTH_SHORT).show();
//                                } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
//                                        && currentDate.get(Calendar.MONTH) > Integer.valueOf(datePicker.getMonth())) {
//                                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
//                                            Toast.LENGTH_SHORT).show();
//                                } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
//                                        && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
//                                        && currentDate.get(Calendar.DAY_OF_MONTH) >
//                                        Integer.valueOf(datePicker.getDayOfMonth())) {
//                                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
//                                            Toast.LENGTH_SHORT).show();
//                                } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
//                                        && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
//                                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
//                                        Integer.valueOf(datePicker.getDayOfMonth())
//                                        && currentDate.get(Calendar.HOUR_OF_DAY) >
//                                        Integer.valueOf(timePicker.getHour())) {
//                                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
//                                            Toast.LENGTH_SHORT).show();
//                                } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
//                                        && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
//                                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
//                                        Integer.valueOf(datePicker.getDayOfMonth())
//                                        && currentDate.get(Calendar.HOUR_OF_DAY) ==
//                                        Integer.valueOf(timePicker.getHour())
//                                        && currentDate.get(Calendar.MINUTE) > Integer.valueOf(timePicker.getMinute())) {
//                                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
//                                            Toast.LENGTH_SHORT).show();
//                                } else {

//                                    Calendar futureDate= new GregorianCalendar(datePicker.getYear(),
//                                            datePicker.getMonth(), datePicker.getDayOfMonth(),
//                                            timePicker.getHour(), timePicker.getMinute());
//                                    MainActivity.noteDb.updateTimestamp(toString().valueOf(
//                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
//                                            String.valueOf(futureDate.getTimeInMillis() / 1000));

                                    MainActivity.noteDb.updateDue(toString().valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)), false);

                                    //intention to execute AlertReceiver
                                    MainActivity.alertIntent = new Intent(getContext(), AlertReceiver.class);

//                                    MainActivity.noteDb.updateAlarmData(String.valueOf(
//                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
//                                            String.valueOf(calendar.get(calendar.HOUR)),
//                                            String.valueOf(calendar.get(calendar.MINUTE)),
//                                            String.valueOf(calendar.get(calendar.AM_PM)),
//                                            String.valueOf(calendar.get(calendar.DAY_OF_MONTH)),
//                                            String.valueOf(calendar.get(calendar.MONTH)),
//                                            String.valueOf(calendar.get(calendar.YEAR)));
                                //TODO need to account for if current hour is last hour of day.
                                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            String.valueOf(currentDate.get(Calendar.HOUR)),
                                            String.valueOf(currentDate.get(Calendar.MINUTE)),
                                            String.valueOf(currentDate.get(Calendar.AM_PM)),
                                            String.valueOf(currentDate.get(Calendar.DAY_OF_MONTH)),
                                            String.valueOf(currentDate.get(Calendar.MONTH)),
                                            String.valueOf(currentDate.get(Calendar.YEAR)));

                                    String task = "";
                                    Cursor result = MainActivity.noteDb.getData(Integer.parseInt(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)));
                                    while (result.moveToNext()) {
                                        task = result.getString(4);
                                    }

                                    //setting the name of the task for which the notification is being set
                                    MainActivity.alertIntent.putExtra("ToDo", task);

                                    int broadcast = 0;
                                    Cursor broadcastResult = MainActivity.noteDb.getData(Integer.parseInt(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)));
                                    while(broadcastResult.moveToNext()){
                                        broadcast = broadcastResult.getInt(7);
                                    }

                                    broadcast = broadcast + 1000;

                                    MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(), broadcast,
                                            MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.set(AlarmManager.RTC, (currentDate.getTimeInMillis() + 3600000),
                                            MainActivity.pendIntent);

//                                }

                                datePicker.setVisibility(View.VISIBLE);

                                timePicker.setVisibility(View.GONE);

                                MainActivity.dateOrTime = false;

                                //set background to white
                                MainActivity.activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                //Marks properties as not showing
                                MainActivity.taskPropertiesShowing = false;

                                //Returns the 'add' button
                                MainActivity.params.height = MainActivity.addHeight;

                                MainActivity.add.setLayoutParams(MainActivity.params);

                                MainActivity.dateRowShowing = false;

                                MainActivity.repeating = false;

                                MainActivity.timePickerShowing = false;

                                reorderList();

                                notifyDataSetChanged();

                            }
                        });

                        //Actions to occur if user selects '4 hours'
                        fourHourBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.i(TAG, "In 4 hours");

                            }
                        });

                        //Actions to occur if user selects 'tomorrow'
                        tomorrowBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.i(TAG, "In tomorrow");

                            }
                        });

                    }
                });

                //Actions to occur if user selects 'Done'
                taskDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        taskOverdueRow.setVisibility(View.GONE);

                        MainActivity.noteDb.updateOverdue(toString().valueOf(
                                MainActivity.sortedIDs.get(position)));

                        //TODO code duplication warning
                        //set background white
                        MainActivity.activityRootView.setBackgroundColor(Color
                                .parseColor("#FFFFFF"));

                        notifyDataSetChanged();

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.noteDb.updateKilled(toString().valueOf(
                                MainActivity.sortedIDs.get(MainActivity.activeTask)), true);

                        Toast.makeText(v.getContext(), "You killed this task!",
                                Toast.LENGTH_SHORT).show();

                        MainActivity.alarmManager.cancel(MainActivity.pendIntent.getService(
                                getContext(), Integer.parseInt(MainActivity.sortedIDs.get(position))
                                , MainActivity.alertIntent, 0));

                        MainActivity.add.setVisibility(View.VISIBLE);

                        MainActivity.vibrate.vibrate(50);

                        MainActivity.params.height = MainActivity.addHeight;

                        v.setLayoutParams(MainActivity.params);

                    }
                });

                //Actions to occur if user selects 'ignore'
                taskIgnore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        taskOverdueRow.setVisibility(View.GONE);

                        MainActivity.noteDb.updateOverdue(toString().valueOf(
                                MainActivity.sortedIDs.get(position)));

                        //Updates the view
                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    }
                });

            }else{

                propertyRow.setVisibility(View.VISIBLE);

            }

            //Making extra row visible removes clickability. Clickability needs to be reinstated.
            taskView.findViewById(R.id.taskName).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    //set background to white
                    MainActivity.activityRootView.setBackgroundColor(Color
                            .parseColor("#FFFFFF"));

                    //Updates the view
                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    //Marks properties as not showing
                    MainActivity.taskPropertiesShowing = false;

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);

                }
            });

            //centering the selected item in the view
            MainActivity.listViewHeight = MainActivity.theListView.getMeasuredHeight();
            MainActivity.theListView.smoothScrollToPositionFromTop(position,
                    (MainActivity.listViewHeight / 2));

            //Initialising variables
            Button complete = taskView.findViewById(R.id.complete);
            Button alarm = taskView.findViewById(R.id.alarm);
            Button more = taskView.findViewById(R.id.more);
            final Button rename = taskView.findViewById(R.id.rename);
            Button subTasks = taskView.findViewById(R.id.subTasks);
            Button note = taskView.findViewById(R.id.note);
            final Button dateButton = taskView.findViewById(R.id.date);
            final Button daily = taskView.findViewById(R.id.daily);
            final Button weekly = taskView.findViewById(R.id.weekly);
            final Button monthly = taskView.findViewById(R.id.monthly);

            //put data in text view
            theTextView.setText(task);

            Boolean due = false;
            Cursor result = MainActivity.noteDb.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(MainActivity.activeTask)));
            while (result.moveToNext()) {
                due = result.getInt(5) > 0;
            }

            //"set due date" button becomes "remove due date" button if due date already set
            if (due){

                alarm.setText("Alarm options");

            }

            //Actions to occur if user selects 'complete'
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //set background white
                    MainActivity.activityRootView.setBackgroundColor(Color
                            .parseColor("#FFFFFF"));

                    notifyDataSetChanged();

                    MainActivity.taskPropertiesShowing = false;

                    MainActivity.noteDb.updateKilled(toString().valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)), true);

                    Toast.makeText(v.getContext(), "You killed this task!",
                            Toast.LENGTH_SHORT).show();

                    MainActivity.alarmManager.cancel(MainActivity.pendIntent.getService(
                            getContext(), Integer.parseInt(MainActivity.sortedIDs.get(position))
                            , MainActivity.alertIntent, 0));

                    MainActivity.add.setVisibility(View.VISIBLE);

                    MainActivity.vibrate.vibrate(50);

                    MainActivity.params.height = MainActivity.addHeight;

                    v.setLayoutParams(MainActivity.params);

                }

            });

            //Actions to occur if user selects 'set due date'
            alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO reword this
//                    Toast.makeText(v.getContext(), "Upgrade to the Pro version to" +
//                                    " get this feature", Toast.LENGTH_SHORT).show();

                    Boolean due = false;
                    Cursor dueResult = MainActivity.noteDb.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)));
                    while (dueResult.moveToNext()) {
                        due = dueResult.getInt(5) > 0;
                    }

                    //actions to occur if alarm not already set
                    if (!due) {

                        MainActivity.dateRowShowing = true;

                        MainActivity.datePickerShowing = true;

                        notifyDataSetChanged();

                    //actions to occur when viewing alarm properties
                    } else {

                        Button killAlarmBtn = taskView.findViewById(R.id.killAlarmBtn);
                        Button resetAlarmBtn = taskView.findViewById(R.id.resetAlarmBtn);
                        Button repeatAlarmBtn = taskView.findViewById(R.id.repeatBtn);

                        alarmOptionsRow.setVisibility(View.VISIBLE);

                        propertyRow.setVisibility(View.GONE);

                        MainActivity.alarmOptionsShowing = true;

                        //Actions to occur if user selects 'remove alarm'
                        killAlarmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainActivity.noteDb.updateDue(toString().valueOf(MainActivity
                                        .sortedIDs.get(MainActivity.activeTask)), false);
                                MainActivity.noteDb.removeTimestamp(toString().valueOf(MainActivity
                                        .sortedIDs.get(MainActivity.activeTask)));

                                MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                                        .get(position), false);

                                MainActivity.alarmManager.cancel(MainActivity.pendIntent
                                        .getService(getContext(), Integer.parseInt(MainActivity
                                                        .sortedIDs.get(position)),
                                                MainActivity.alertIntent, 0));

                                MainActivity.noteDb.updateAlarmData
                                        (toString().valueOf(MainActivity.sortedIDs.get(position)),
                                                "", "", "",
                                                "", "", "");

                                MainActivity.noteDb.updateDue(toString().valueOf(
                                        MainActivity.sortedIDs.get(position)), false);

                                MainActivity.alarmOptionsShowing = false;

                                reorderList();

                                MainActivity.taskPropertiesShowing = false;

                                MainActivity.activityRootView
                                        .setBackgroundColor(Color.parseColor("#FFFFFF"));

                                MainActivity.add.setVisibility(View.VISIBLE);

                                MainActivity.vibrate.vibrate(50);

                                MainActivity.params.height = MainActivity.addHeight;

                                v.setLayoutParams(MainActivity.params);

                                notifyDataSetChanged();

                            }
                        });

                        //Actions to occur if user selects 'change due date'
                        resetAlarmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainActivity.datePickerShowing = true;

                                MainActivity.dateRowShowing = true;

                                notifyDataSetChanged();

                            }
                        });

                        //Actions to occur if user selects 'repeat alarm'
                        repeatAlarmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alarmOptionsRow.setVisibility(View.GONE);

                                repeatRow.setVisibility(View.VISIBLE);

                                MainActivity.repeatShowing = true;

                            }
                        });

                    }

                }
            });

            //Actions to occur if user selects 'more'
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    propertyRow.setVisibility(View.GONE);

                    optionsRow.setVisibility(View.VISIBLE);

                    MainActivity.taskOptionsShowing = true;

                }
            });

            //Actions to occur if user selects 'rename' or 'reinstate'
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Cannot update the list until after the task has been updated.
                    MainActivity.goToMyAdapter = false;

                    //Indicates that a task is being edited
                    MainActivity.taskBeingEdited = true;

                    MainActivity.activeTask = position;

                    MainActivity.tasksAreClickable = false;

                    MainActivity.fadeTasks = true;

                    MainActivity.taskPropertiesShowing = false;

                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                }
            });

            //Actions to occur if user selects 'Sub-Tasks'
            subTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.checklistShowing = true;

                    MainActivity.vibrate.vibrate(50);

                    getContext().startActivity(intent);

                }
            });

            //Actions to occur if user selects 'Add Note'
            note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    getContext().startActivity(noteIntent);

                }
            });

            //Actions to occur if user selects 'Set Time'
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dateButton.setText("Set Time");

                    setAlarm(dateRow, datePicker, timePicker);

                }
            });

            //Actions to occur if user selects to repeat daily
            daily.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.dateRowShowing = true;

                    MainActivity.repeatInterval = AlarmManager.INTERVAL_DAY;

                    MainActivity.repeating = true;

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(dateRow, datePicker, timePicker);

                    notifyDataSetChanged();

                }
            });

            //Actions to occur if user selects to repeat weekly
            weekly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.dateRowShowing = true;

                    MainActivity.repeatInterval = (AlarmManager.INTERVAL_DAY * 7);

                    MainActivity.repeating = true;

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(dateRow, datePicker, timePicker);

                    notifyDataSetChanged();

                }
            });

            //Actions to occur if user selects to repeat monthly
            monthly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.dateRowShowing = true;

                    MainActivity.repeatInterval = (AlarmManager.INTERVAL_DAY * 30);

                    MainActivity.repeating = true;

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(dateRow, datePicker, timePicker);

                    notifyDataSetChanged();

                }
            });

        }

        if (MainActivity.fadeTasks) {

            //fade tasks when keyboard is up
            taskView.setBackgroundColor(Color.parseColor("#888888"));

        }else{

            //tasks are white when keyboard is down
            taskView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }

        //put data in text view
        theTextView.setText(task);

        Boolean killed = false;

        Cursor killedResult = MainActivity.noteDb.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while (killedResult.moveToNext()) {
            killed = killedResult.getInt(6) > 0;
        }

        //crossing out completed tasks

        //check if task has to be crossed out
        if (killed) {

            theTextView.setPaintFlags(theTextView.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);

        }

        Boolean isDue = false;
        Cursor dueResult = MainActivity.noteDb.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while (dueResult.moveToNext()) {
            isDue = dueResult.getInt(5) > 0;
        }

        //Show due date notification if required
        if(isDue) {

            Calendar currentDate = new GregorianCalendar();

            ImageView due = taskView.findViewById(R.id.due);
            ImageView overdue = taskView.findViewById(R.id.dueRed);

            Cursor result;
            String hour;
            String minute;
            String ampm;
            String day;
            String month;
            String year;

            //Getting time data
            result = MainActivity.noteDb.getAlarmData(Integer.parseInt(
                    MainActivity.sortedIDs.get(position)));
            hour = "";
            minute = "";
            ampm = "";
            day = "";
            month = "";
            year = "";
            while(result.moveToNext()){
                hour = result.getString(1);
                minute = result.getString(2);
                ampm = result.getString(3);
                day = result.getString(4);
                month = result.getString(5);
                year = result.getString(6);
            }

            //Checking for overdue tasks
            String formattedTime;
            Boolean sameDay = false;
            //Overdue
            if(currentDate.get(Calendar.YEAR) > Integer.valueOf(year)) {
                overdue.setVisibility(View.VISIBLE);
            //Overdue
            }else if(currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
                    && currentDate.get(Calendar.MONTH) > Integer.valueOf(month)) {
                overdue.setVisibility(View.VISIBLE);
            //Overdue
            }else if(currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
                    && currentDate.get(Calendar.MONTH) == Integer.valueOf(month)
                    && currentDate.get(Calendar.DAY_OF_MONTH) > Integer.valueOf(day)) {
                overdue.setVisibility(View.VISIBLE);
            }else if(currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
                    && currentDate.get(Calendar.MONTH) == Integer.valueOf(month)
                    && currentDate.get(Calendar.DAY_OF_MONTH) == Integer.valueOf(day)){
                sameDay = true;
                //Saved hours are in 12 hour time. Accounting for am/pm.
                int adjustedHour = 0;
                if (Integer.valueOf(ampm) == 1) {
                    adjustedHour = Integer.valueOf(hour) + 12;
                }else{
                    adjustedHour = Integer.valueOf(hour);
                }
                //Overdue
                if(currentDate.get(Calendar.HOUR_OF_DAY) > adjustedHour){
                    overdue.setVisibility(View.VISIBLE);
                //Overdue
                }else if(currentDate.get(Calendar.HOUR_OF_DAY) == adjustedHour
                        && currentDate.get(Calendar.MINUTE) >= Integer.valueOf(minute)){
                    overdue.setVisibility(View.VISIBLE);
                //Not overdue
                }else{
                    due.setVisibility(View.VISIBLE);
                }
            //Not overdue
            }else{
                due.setVisibility(View.VISIBLE);
            }

            //If task due on same day show the due date
            if(!sameDay){
                //TODO account for MM/DD/YYYY https://en.wikipedia.org/wiki/Date_format_by_country
                //Formatting date
                String formattedDay;
                String formattedMonth;
                String formattedDate;
                if(Integer.valueOf(day) < 10){
                    formattedDay = "0" + day;
                }else{
                    formattedDay = day;
                }
                if(Integer.valueOf(month) < 10){
                    formattedMonth = "0" + String.valueOf(Integer.valueOf(month) + 1);
                }else{
                    formattedMonth = String.valueOf(Integer.valueOf(month) + 1);
                }

                formattedDate = formattedDay + "/" + formattedMonth + "/" + year;

                dueTextView.setText(formattedDate);
            //If task due on different day show the due time
            }else{

                if(Integer.valueOf(hour) == 0){
                    hour = "12";
                }
                if(Integer.valueOf(minute) < 10){
                    if(Integer.valueOf(ampm) == 0) {
                        formattedTime = hour + ":0" + minute + "am";
                    }else{
                        formattedTime = hour + ":0" + minute + "pm";
                    }
                }else{
                    if(Integer.valueOf(ampm) == 0) {
                        formattedTime = hour + ":" + minute + "am";
                    }else{
                        formattedTime = hour + ":" + minute + "pm";
                    }
                }
                dueTextView.setText(formattedTime);
            }

        }

        boolean showRepeatIcon = false;
        Cursor repeatResult = MainActivity.noteDb.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while (repeatResult.moveToNext()){
            showRepeatIcon = (repeatResult.getInt(8) > 0);
        }
        //show repeat icon if required
        if(showRepeatIcon){

            ImageView repeat = taskView.findViewById(R.id.repeatIcon);

            repeat.setVisibility(View.VISIBLE);

        }

        //Show checklist/note icon if required
        boolean showChecklist = false;
        String showNote = "";
        Cursor result = MainActivity.noteDb.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while(result.moveToNext()){
            showChecklist = (result.getInt(2) == 1);
            showNote = result.getString(1);
        }
        if(showChecklist){
            ImageView checklistImg = taskView.findViewById(R.id.checklistIcon);
            checklistImg.setVisibility(View.VISIBLE);
        }
        if(!showNote.equals("")){
            ImageView noteImg = taskView.findViewById(R.id.noteIcon);
            noteImg.setVisibility(View.VISIBLE);
        }

        //greying out unselected tasks
        if (MainActivity.taskPropertiesShowing && (position != MainActivity.activeTask)) {

            //fade out inactive tasks
            taskView.setBackgroundColor(Color.parseColor("#888888"));

        }

        //Actions to take when editing task
        if (MainActivity.taskBeingEdited && (position == MainActivity.activeTask) &&
                !MainActivity.goToMyAdapter) {

            MainActivity.keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

            String oldTaskString = theTextView.getText().toString();

            theTextView.setVisibility(View.GONE);

            MainActivity.taskNameEditText.setText(oldTaskString);

            MainActivity.taskNameEditText.setVisibility(View.VISIBLE);

            //Keyboard is inactive without this line
            MainActivity.taskNameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI |
                    EditorInfo.IME_ACTION_DONE);

            MainActivity.taskNameEditText.setFocusable(true);

            MainActivity.taskNameEditText.requestFocus();

            MainActivity.taskNameEditText.setSelection(MainActivity.taskNameEditText
                    .getText().length());

        }

        return taskView;

    }

    //set notification alarm for selected task
    public void setAlarm(TableRow dateRow, DatePicker datePicker, TimePicker timePicker){

        if(MainActivity.dateOrTime) {

            dateRow.setVisibility(View.VISIBLE);
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);
            MainActivity.dateOrTime = false;
            MainActivity.datePickerShowing = false;
            MainActivity.timePickerShowing = true;

        }else{

            MainActivity.alarmManager.cancel(MainActivity.pendIntent.getService(getContext(),
                    Integer.parseInt(MainActivity.sortedIDs.get(MainActivity.activeTask)),
                    MainActivity.alertIntent, 0));

            Calendar calendar = Calendar.getInstance();

            //setting alarm
            calendar.set(Calendar.YEAR, datePicker.getYear());
            calendar.set(Calendar.MONTH, datePicker.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());

            Calendar currentDate = new GregorianCalendar();

            //Checking that task due date is in the future
            if (currentDate.get(Calendar.YEAR) > Integer.valueOf(datePicker.getYear())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
                    && currentDate.get(Calendar.MONTH) > Integer.valueOf(datePicker.getMonth())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
                    && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
                    && currentDate.get(Calendar.DAY_OF_MONTH) >
                    Integer.valueOf(datePicker.getDayOfMonth())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
                    && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
                    && currentDate.get(Calendar.DAY_OF_MONTH) ==
                    Integer.valueOf(datePicker.getDayOfMonth())
                    && currentDate.get(Calendar.HOUR_OF_DAY) >
                    Integer.valueOf(timePicker.getHour())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
                    && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
                    && currentDate.get(Calendar.DAY_OF_MONTH) ==
                    Integer.valueOf(datePicker.getDayOfMonth())
                    && currentDate.get(Calendar.HOUR_OF_DAY) ==
                    Integer.valueOf(timePicker.getHour())
                    && currentDate.get(Calendar.MINUTE) > Integer.valueOf(timePicker.getMinute())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else {

                Calendar futureDate= new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(), datePicker.getDayOfMonth(),
                        timePicker.getHour(), timePicker.getMinute());
                MainActivity.noteDb.updateTimestamp(toString().valueOf(
                        MainActivity.sortedIDs.get(MainActivity.activeTask)),
                        String.valueOf(futureDate.getTimeInMillis() / 1000));

                MainActivity.noteDb.updateDue(toString().valueOf(
                        MainActivity.sortedIDs.get(MainActivity.activeTask)), true);

                //intention to execute AlertReceiver
                MainActivity.alertIntent = new Intent(getContext(), AlertReceiver.class);

                MainActivity.noteDb.updateAlarmData(String.valueOf(
                        MainActivity.sortedIDs.get(MainActivity.activeTask)),
                        String.valueOf(calendar.get(calendar.HOUR)),
                        String.valueOf(calendar.get(calendar.MINUTE)),
                        String.valueOf(calendar.get(calendar.AM_PM)),
                        String.valueOf(calendar.get(calendar.DAY_OF_MONTH)),
                        String.valueOf(calendar.get(calendar.MONTH)),
                        String.valueOf(calendar.get(calendar.YEAR)));

                String task = "";
                Cursor result = MainActivity.noteDb.getData(Integer.parseInt(
                        MainActivity.sortedIDs.get(MainActivity.activeTask)));
                while (result.moveToNext()) {
                    task = result.getString(4);
                }

                //setting the name of the task for which the notification is being set
                MainActivity.alertIntent.putExtra("ToDo", task);
//                Intent alertIntent = new Intent();
//                alertIntent.putExtra("ToDo", task);

                int broadcast = 0;
                Cursor broadcastResult = MainActivity.noteDb.getData(Integer.parseInt(
                        MainActivity.sortedIDs.get(MainActivity.activeTask)));
                while(broadcastResult.moveToNext()){
                    broadcast = broadcastResult.getInt(7);
                }

                MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(), broadcast,
                                MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //setting a repeating notification
                if(MainActivity.repeating) {

                    MainActivity.alarmManager.setInexactRepeating(AlarmManager.RTC,
                            calendar.getTimeInMillis(), MainActivity.repeatInterval,
                            MainActivity.pendIntent);

                    MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                            .get(MainActivity.activeTask), true);

                    MainActivity.repeatShowing = false;

                //setting a one-time notification
                }else{

                    MainActivity.alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),
                            MainActivity.pendIntent);

                }

                MainActivity.noteDb.updateDue(toString().valueOf(
                        MainActivity.sortedIDs.get(MainActivity.activeTask)), true);

            }

            datePicker.setVisibility(View.VISIBLE);

            timePicker.setVisibility(View.GONE);

            MainActivity.dateOrTime = false;

            //set background to white
            MainActivity.activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

            //Marks properties as not showing
            MainActivity.taskPropertiesShowing = false;

            //Returns the 'add' button
            MainActivity.params.height = MainActivity.addHeight;

            MainActivity.add.setLayoutParams(MainActivity.params);

            MainActivity.dateRowShowing = false;

            MainActivity.repeating = false;

            MainActivity.timePickerShowing = false;

            reorderList();

            notifyDataSetChanged();

        }

    }

    public void reorderList(){

        //Reordering tasks by due date
        ArrayList<Integer> tempList = new ArrayList<>();
        //Saving timestamps into a temporary array
        for(int i = 0; i < MainActivity.taskListSize; i++){
            int stamp = 0;
            Cursor result = MainActivity.noteDb.getData(i);
            while(result.moveToNext()){
                stamp = result.getInt(3);
            }
            tempList.add(i, stamp);
        }

        ArrayList<String> yetAnotherList = new ArrayList<>();
        ArrayList<String> tempTaskList = new ArrayList<>();

        int count = 0;
        for(int i = 0; i < MainActivity.taskListSize; i++){
            String id = "";
            int stamp = 0;
            String task = "";
            Cursor result = MainActivity.noteDb.getData(i);
            while(result.moveToNext()){
                id = result.getString(0);
                stamp = result.getInt(3);
                task = result.getString(4);
            }
            if((tempList.get(i) == 0) && (stamp == 0)){
                yetAnotherList.add(id);
                tempTaskList.add(task);
                count++;
            }
        }

        while(tempList.size() > 0) {
            int minValue = Collections.min(tempList);
            if(minValue != 0) {
                for (int i = 0; i < MainActivity.taskListSize; i++) {
                    String id = "";
                    int stamp = 0;
                    String task = "";
                    Cursor result = MainActivity.noteDb.getData(i);
                    while (result.moveToNext()) {
                        id = result.getString(0);
                        stamp = result.getInt(3);
                        task = result.getString(4);
                    }
                    if (minValue == stamp) {
                        yetAnotherList.add(id);
                        tempTaskList.add(task);
                        tempList.remove(Collections.min(tempList));
                    }
                }
            }else{
                tempList.remove(Collections.min(tempList));
            }
        }

        MainActivity.sortedIDs = yetAnotherList;
        MainActivity.taskList = tempTaskList;

        //Updating the view with the new order
        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                getContext(), MainActivity.taskList)};
        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

    }

}