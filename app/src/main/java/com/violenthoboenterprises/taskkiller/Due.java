//package com.violenthoboenterprises.taskkiller;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.graphics.Color;
//import android.view.View;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.TimePicker;
//
//import java.util.Calendar;
//
//public class Due extends MainActivity{
//
//    //set notification alarm for selected task
//    public void setAlarm(View view){
//        //TODO this date and time picker is different to the date and time picker in MyAdapter
//        //TODO this could be why it isn't saving time properly
//        DatePicker datePicker = findViewById(R.id.datePicker);
//
//        TimePicker timePicker = findViewById(R.id.timePicker);
//
//        Button dateButton = findViewById(R.id.date);
//
//        //actions to occur when date has been chosen
//        if(!dateOrTime){
//
//            datePicker.setVisibility(View.GONE);
//
//            timePicker.setVisibility(View.VISIBLE);
//
//            //Updates the view
//            theListView.setAdapter(theAdapter[0]);
//
//            dateOrTime = true;
//
//            dateButton.setText("Set Time");
//
//            //actions to occur when time has been chosen
//        }else{
//
//            Calendar calendar = Calendar.getInstance();
//
//            //setting alarm
//            calendar.set(Calendar.YEAR, datePicker.getYear());
//            calendar.set(Calendar.MONTH, datePicker.getMonth());
//            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
//            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
//            calendar.set(Calendar.MINUTE, timePicker.getMinute());
//
//            //intention to execute AlertReceiver
//            alertIntent = new Intent(this, AlertReceiver.class);
//
//            //setting the name of the task for which the notification is being set
//            alertIntent.putExtra("ToDo", taskList.get(activeTask));
//
//            int i = 0;
//
//            while (broadcastID.contains(i)){
//
//                i++;
//
//            }
//
//            broadcastID.set(activeTask, i);
//
//            pendingIntent.set(activeTask, PendingIntent.getBroadcast(this,
//                    broadcastID.get(activeTask), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
//
//            //setting the notification
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar
//                    .getTimeInMillis(), pendingIntent.get(activeTask));
//
//            datePicker.setVisibility(View.VISIBLE);
//
//            timePicker.setVisibility(View.GONE);
//
//            dateOrTime = false;
//
//            dateButton.setText("Set Time");
//
//            //set background to white
//            activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//            showTaskDueIcon.set(activeTask, true);
//
//            theListView.setAdapter(theAdapter[0]);
//
//            //Marks properties as not showing
//            taskPropertiesShowing = false;
//
//            alarmBeingSet = false;
//
//            //Returns the 'add' button
//            params.height = addHeight;
//
//            add.setLayoutParams(params);
//
//        }
//    }
//
//}
