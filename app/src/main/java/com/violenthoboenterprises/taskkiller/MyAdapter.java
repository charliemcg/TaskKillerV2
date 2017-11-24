package com.violenthoboenterprises.taskkiller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.NotificationCompat;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

class MyAdapter extends ArrayAdapter<String> {

    //Notify the user that something happened in the background
    NotificationManager notificationManager;

    //Tracks if notification is active in the task bar
    boolean isNotificActive = false;

    //Tracks notifications
    int notifID = 33;

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
        final View propertiesView = theInflater.inflate(R.layout.task_properties_layout,
                parent, false);
        final View dateView = theInflater.inflate(R.layout.date_layout, parent, false);
        final TextView theTextView = (TextView) taskView.findViewById(R.id.textView);
        final Intent intent = new Intent(getContext(), Checklist.class);

        //TODO make sure hard coded values work on all devices
        //Task cannot be centered unless it's in view. Moving selected task into view if not already in view in portrait.
        if ((MainActivity.activeTask > 6) && MainActivity.centerTask && (getContext().getResources().getConfiguration().orientation == 1)) {
            MainActivity.theListView.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.theListView.setSelection(MainActivity.activeTask);
                }
            });
            MainActivity.centerTask = false;
            //Same as above but for landscape.
        } else if ((MainActivity.activeTask > 3) && MainActivity.centerTask && (getContext().getResources().getConfiguration().orientation == 2)) {
            MainActivity.theListView.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.theListView.setSelection(MainActivity.activeTask);
                }
            });
            MainActivity.centerTask = false;
        }

        //Actions to occur if requesting task options
        if (task.equals("properties")) {

            //centering the selected item in the view
            MainActivity.listViewHeight = MainActivity.theListView.getMeasuredHeight();
            MainActivity.theListView.smoothScrollToPositionFromTop(position, (MainActivity.listViewHeight / 2));

            //Initialising variables
            Button complete = (Button) propertiesView.findViewById(R.id.complete);
            Button snooze = (Button) propertiesView.findViewById(R.id.snooze);
            Button more = (Button) propertiesView.findViewById(R.id.more);

            //Actions to occur if user selects 'complete'
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //set background white
                    MainActivity.activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //Task options disappear
                    MainActivity.taskList.remove(MainActivity.activeTask + 1);

                    //Visibly mark task as complete
                    MainActivity.taskList.set(MainActivity.activeTask,
                            MyAdapter.this.getItem(position - 1));

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

//                //set background white
//                MainActivity.activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//                //Task options disappear
//                MainActivity.taskList.remove(MainActivity.activeTask + 1);
//
//                notifyDataSetChanged();
//
//                MainActivity.taskPropertiesShowing = false;
//
//                Toast.makeText(v.getContext(), "Get it done, Wimp!", Toast.LENGTH_SHORT).show();
//
//                MainActivity.add.setVisibility(View.VISIBLE);
//
//                MainActivity.vibrate.vibrate(50);
//
//                MainActivity.params.height = MainActivity.addHeight;
//
//                v.setLayoutParams(MainActivity.params);

                    MainActivity.taskList.set(MainActivity.activeTask + 1, "date");
                    notifyDataSetChanged();

                }
            });

            //Actions to occur if user selects 'more'
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.checklistShowing = true;

                    MainActivity.vibrate.vibrate(50);

                    getContext().startActivity(intent);

                    notifyDataSetChanged();

                }
            });

            return propertiesView;

        }else if (task.equals("date")){

            final TimePicker timePicker = (TimePicker) dateView.findViewById(R.id.timePicker);
            Button dateBtn = (Button) dateView.findViewById(R.id.date);

            //TODO use this code to help with setting notification time
//            dateBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v){
//
//                    Toast.makeText(v.getContext(), "Selected " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute(), Toast.LENGTH_SHORT).show();
//
//                }
//
//            });

            return dateView;

        } else{

            if (MainActivity.fadeTasks) {

                //fade tasks when keyboard is up
                taskView.setBackgroundColor(Color.parseColor("#888888"));

            } else {

                //tasks are white when keyboard is down
                taskView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }

            //put data in text view
            theTextView.setText(task);

            //Preventing out of bounds problems
            if (position < MainActivity.tasksKilled.size()) {

                //crossing out completed tasks if task properties are not showing
                if (!MainActivity.taskPropertiesShowing) {

                    //check is task has to be crossed out
                    if (MainActivity.tasksKilled.get(position)) {

                        theTextView.setPaintFlags(theTextView.getPaintFlags() |
                                Paint.STRIKE_THRU_TEXT_FLAG);

                        //TODO use this code as a basis for setting up notification icons
                        ImageView bell = (ImageView) taskView.findViewById(R.id.bell);

                        bell.setVisibility(View.VISIBLE);

                    }

                    //crossing out completed tasks which are above the showing task properties
                } else if (MainActivity.activeTask < position) {

                    if (position != 0) {

                        //check is task has to be crossed out
                        if (MainActivity.tasksKilled.get(position - 1)) {

                            theTextView.setPaintFlags(theTextView.getPaintFlags() |
                                    Paint.STRIKE_THRU_TEXT_FLAG);

                            //TODO use this code as a basis for setting up notification icons
                            ImageView bell = (ImageView) taskView.findViewById(R.id.bell);

                            bell.setVisibility(View.VISIBLE);

                        }

                    }

                    //crossing out completed tasks which are below the showing task properties
                } else {

                    //check is task has to be crossed out
                    if (MainActivity.tasksKilled.get(position)) {

                        theTextView.setPaintFlags(theTextView.getPaintFlags() |
                                Paint.STRIKE_THRU_TEXT_FLAG);

                        //TODO use this code as a basis for setting up notification icons
                        ImageView bell = (ImageView) taskView.findViewById(R.id.bell);

                        bell.setVisibility(View.VISIBLE);

                    }

                }

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

    }

}
