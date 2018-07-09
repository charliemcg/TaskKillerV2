package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
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
        final DatePicker datePicker = taskView.findViewById(R.id.datePicker);
        final TimePicker timePicker = taskView.findViewById(R.id.timePicker);
        TextView dueTextView = taskView.findViewById(R.id.dueTextView);

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

        //actions to occur in regards to selected task
        if(MainActivity.taskPropertiesShowing && position == MainActivity.activeTask){

            //actions to occur if setting alarm
            if(MainActivity.alarmBeingSet) {

                //Determine whether to show datepicker
                if (!MainActivity.dateOrTime) {

                    dateRow.setVisibility(View.VISIBLE);
                    MainActivity.dateOrTime = true;
                }

            //show the tasks properties
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
            Button snooze = taskView.findViewById(R.id.snooze);
            Button more = taskView.findViewById(R.id.more);
            final Button rename = taskView.findViewById(R.id.rename);
            Button subTasks = taskView.findViewById(R.id.subTasks);
            Button note = taskView.findViewById(R.id.note);
            final Button dateButton = taskView.findViewById(R.id.date);

            //put data in text view
            theTextView.setText(task);

            //"set due date" button becomes "remove due date" button if due date already set
            if (MainActivity.showTaskDueIcon.get(MainActivity.activeTask)){

                snooze.setText("Remove Due Date");

            }

            //Actions to occur if user selects 'complete'
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //set background white
                    MainActivity.activityRootView.setBackgroundColor(Color
                            .parseColor("#FFFFFF"));

                    //Visibly mark task as complete
                    MainActivity.taskList.set(MainActivity.activeTask,
                            MyAdapter.this.getItem(position));

                    notifyDataSetChanged();

                    MainActivity.taskPropertiesShowing = false;

                    //Marks task as complete
                    MainActivity.tasksKilled.set(MainActivity.activeTask, true);

                    Toast.makeText(v.getContext(), "You killed this task!",
                            Toast.LENGTH_SHORT).show();

                    MainActivity.add.setVisibility(View.VISIBLE);

                    MainActivity.vibrate.vibrate(50);

                    MainActivity.params.height = MainActivity.addHeight;

                    v.setLayoutParams(MainActivity.params);

                }

            });

            //Actions to occur if user selects 'set due date'
            snooze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO reword this
//                    Toast.makeText(v.getContext(), "Upgrade to the Pro version to" +
//                                    " get this feature", Toast.LENGTH_SHORT).show();

                    //actions to occur if alarm not already set
                    if (!MainActivity.showTaskDueIcon.get(MainActivity.activeTask)) {

                        MainActivity.alarmBeingSet = true;

                        MainActivity.dateRowShowing = true;

                        notifyDataSetChanged();

                    //actions to occur when cancelling alarm
                    } else {

                        MainActivity.showTaskDueIcon.set(MainActivity.activeTask, false);

                        MainActivity.alarmManager.cancel(MainActivity.pendingIntent
                                .get(MainActivity.activeTask));

                        MainActivity.noteDb.updateAlarmData(String.valueOf(MainActivity.activeTask),
                                "", "", "", "", "", "");

                        notifyDataSetChanged();

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

        //crossing out completed tasks

        //check if task has to be crossed out
        if (MainActivity.tasksKilled.get(position)) {

            theTextView.setPaintFlags(theTextView.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);

        }

        //Show due date notification if required
        if(MainActivity.showTaskDueIcon.get(position)) {

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
            result = MainActivity.noteDb.getAlarmData(position);
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
                        && currentDate.get(Calendar.MINUTE) > Integer.valueOf(minute)){
                    overdue.setVisibility(View.VISIBLE);
                //Not overdue
                }else{
                    due.setVisibility(View.VISIBLE);
                }
            //Not overdue
            }else{
                due.setVisibility(View.VISIBLE);
            }

            //If task due on same day show the due time
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
            //If task due on different day show the due date
            }else{
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

        //Show checklist/note icon if required
        boolean showChecklist = false;
        String showNote = "";
        Cursor result = MainActivity.noteDb.getData(position);
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
        }else {

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
                    && currentDate.get(Calendar.DAY_OF_MONTH) > Integer.valueOf(datePicker.getDayOfMonth())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
                    && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
                    && currentDate.get(Calendar.DAY_OF_MONTH) == Integer.valueOf(datePicker.getDayOfMonth())
                    && currentDate.get(Calendar.HOUR_OF_DAY) > Integer.valueOf(timePicker.getHour())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(datePicker.getYear())
                    && currentDate.get(Calendar.MONTH) == Integer.valueOf(datePicker.getMonth())
                    && currentDate.get(Calendar.DAY_OF_MONTH) == Integer.valueOf(datePicker.getDayOfMonth())
                    && currentDate.get(Calendar.HOUR_OF_DAY) == Integer.valueOf(timePicker.getHour())
                    && currentDate.get(Calendar.MINUTE) > Integer.valueOf(timePicker.getMinute())) {
                Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                        Toast.LENGTH_SHORT).show();
            } else {

                //intention to execute AlertReceiver
                MainActivity.alertIntent = new Intent(getContext(), AlertReceiver.class);

                MainActivity.noteDb.updateAlarmData(String.valueOf(MainActivity.activeTask),
                        String.valueOf(calendar.get(calendar.HOUR)),
                        String.valueOf(calendar.get(calendar.MINUTE)),
                        String.valueOf(calendar.get(calendar.AM_PM)),
                        String.valueOf(calendar.get(calendar.DAY_OF_MONTH)),
                        String.valueOf(calendar.get(calendar.MONTH)),
                        String.valueOf(calendar.get(calendar.YEAR)));

                //setting the name of the task for which the notification is being set
                MainActivity.alertIntent.putExtra("ToDo", MainActivity.taskList
                        .get(MainActivity.activeTask));

                int i = 0;

                while (MainActivity.broadcastID.contains(i)) {

                    i++;

                }

                MainActivity.broadcastID.set(MainActivity.activeTask, i);

                MainActivity.pendingIntent.set(MainActivity.activeTask,
                        PendingIntent.getBroadcast(getContext(),
                                MainActivity.broadcastID.get(MainActivity.activeTask),
                                MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                //setting the notification
                MainActivity.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar
                        .getTimeInMillis(), MainActivity.pendingIntent.get(MainActivity.activeTask));

                MainActivity.showTaskDueIcon.set(MainActivity.activeTask, true);

            }

            datePicker.setVisibility(View.VISIBLE);

            timePicker.setVisibility(View.GONE);

            MainActivity.dateOrTime = false;

            //set background to white
            MainActivity.activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

            //Marks properties as not showing
            MainActivity.taskPropertiesShowing = false;

            MainActivity.alarmBeingSet = false;

            //Returns the 'add' button
            MainActivity.params.height = MainActivity.addHeight;

            MainActivity.add.setLayoutParams(MainActivity.params);

            MainActivity.dateRowShowing = false;

        }

    }

}