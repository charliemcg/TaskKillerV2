package com.violenthoboenterprises.taskkiller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
        final TableRow propertyRow = taskView.findViewById(R.id.properties);
        final TableRow dateRow = taskView.findViewById(R.id.dateTime);
        final TableRow optionsRow = taskView.findViewById(R.id.options);
        final DatePicker datePicker = taskView.findViewById(R.id.datePicker);
        final TimePicker timePicker = taskView.findViewById(R.id.timePicker);

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

                //Determine whether to show datepicker or timepicker
                if (!MainActivity.dateOrTime) {
                    dateRow.setVisibility(View.VISIBLE);
                } else {
                    dateRow.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
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
                    MainActivity.activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));


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
            Button rename = taskView.findViewById(R.id.rename);
            Button subTasks = taskView.findViewById(R.id.subTasks);

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

                    Toast.makeText(v.getContext(), "Upgrade to the Pro version to" +
                                    " get this feature", Toast.LENGTH_SHORT).show();

                    //TODO worry about this later. This is a pro feature. Do free features first
//                    //actions to occur if alarm not already set
//                    if (!MainActivity.showTaskDueIcon.get(MainActivity.activeTask)) {
//
//                        MainActivity.alarmBeingSet = true;
//
//                        notifyDataSetChanged();
//
//                    //actions to occur when cancelling alarm
//                    } else {
//
//                        MainActivity.showTaskDueIcon.set(MainActivity.activeTask, false);
//
//                        MainActivity.alarmManager.cancel(MainActivity.pendingIntent
//                                .get(MainActivity.activeTask));
//
//                        notifyDataSetChanged();
//
//                    }

                }
            });

            //Actions to occur if user selects 'more'
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    propertyRow.setVisibility(View.GONE);
                    optionsRow.setVisibility(View.VISIBLE);

//                    MainActivity.checklistShowing = true;
//
//                    MainActivity.vibrate.vibrate(50);
//
//                    getContext().startActivity(intent);
//
//                    notifyDataSetChanged();

                }
            });

            //Actions to occur if user selects 'rename' or 'reinstate'
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    //Cannot update the list until after the task has been updated.
//                    MainActivity.goToMyAdapter = false;
//
//                    //Actions to occur when keyboard is showing
////                    MainActivity.checkKeyboardShowing();
//
//                    //Indicates that a task is being edited
//                    MainActivity.taskBeingEdited = true;
//
////                    MainActivity.activeTask = position;
//
//                    MainActivity.tasksAreClickable = false;
//
//                    MainActivity.fadeTasks = true;
//
//                    MainActivity.centerTask = true;
//
//                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);
//
//                    //Can't change visibility of 'add' button. Have to set height to zero instead.
//                    MainActivity.params.height = 0;
//
//                    MainActivity.add.setLayoutParams(MainActivity.params);

                }
            });

            //Actions to occur if user selects 'more'
            subTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.checklistShowing = true;

                    MainActivity.vibrate.vibrate(50);

                    getContext().startActivity(intent);

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

        //crossing out completed tasks

        //check if task has to be crossed out
        if (MainActivity.tasksKilled.get(position)) {

            theTextView.setPaintFlags(theTextView.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);

        }

        //Show due date notification if required
        if(MainActivity.showTaskDueIcon.get(position)) {

            ImageView due = taskView.findViewById(R.id.due);

            due.setVisibility(View.VISIBLE);

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
