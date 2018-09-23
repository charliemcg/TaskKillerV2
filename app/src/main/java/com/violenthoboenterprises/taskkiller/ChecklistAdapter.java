package com.violenthoboenterprises.taskkiller;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class ChecklistAdapter extends ArrayAdapter<String> {

    String TAG;

    public ChecklistAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.checklist_item, values);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final String item = getItem(position);
        final LayoutInflater theInflater = LayoutInflater.from(getContext());
        final View checklistItemView = theInflater.inflate
                (R.layout.checklist_item, parent, false);
        TextView checklistTextView = checklistItemView.findViewById(R.id.checklistTextView);
        final ImageView tick = checklistItemView.findViewById(R.id.subtaskComplete);
        final ImageView ticked = checklistItemView.findViewById(R.id.subtaskCompleted);
        final ImageView tickFaded = checklistItemView.findViewById(R.id.subtaskCompleteFaded);
        final ImageView tickedFaded = checklistItemView.findViewById(R.id.subtaskCompletedFaded);
        final ImageView tickWhite = checklistItemView.findViewById(R.id.subtaskCompleteWhite);
        final ImageView tickedWhite = checklistItemView.findViewById(R.id.subtaskCompletedWhite);
        final ImageView tickWhiteFaded = checklistItemView
                .findViewById(R.id.subtaskCompleteWhiteFaded);
        final ImageView tickedWhiteFaded = checklistItemView
                .findViewById(R.id.subtaskCompletedWhiteFaded);
        TAG = "ChecklistAdapter";

        String dbTaskId = "";
        Boolean dbLightDark = false;

        int dbID = 0;
        String dbNote = "";
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

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            dbTaskId = dbResult.getString(4);
            dbLightDark = dbResult.getInt(3) > 0;
        }
        //getting subtask data
        dbResult = MainActivity.db.getData(Integer.parseInt(dbTaskId));
        while (dbResult.moveToNext()) {
            dbID = dbResult.getInt(0);
            dbNote = dbResult.getString(1);
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
            dbChecklistSize = dbResult.getInt(17);
        }
        dbResult.close();

        final String finalDbTaskId = dbTaskId;

        final int finalDbID = dbID;
        final String finalDbNote = dbNote;
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

        //setting up UI based on light or dark mode
        if(!MainActivity.lightDark){
            checklistItemView.setBackgroundColor(Color.parseColor("#333333"));
            if(Checklist.fadeSubTasks){
                checklistTextView.setTextColor(Color.parseColor("#666666"));
                tickFaded.setVisibility(View.VISIBLE);
                tickWhiteFaded.setVisibility(View.GONE);
                tick.setVisibility(View.GONE);
                tickWhite.setVisibility(View.GONE);
            }else {
                tick.setVisibility(View.VISIBLE);
                tickWhite.setVisibility(View.GONE);
            }
        }else{
            checklistItemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            checklistTextView.setTextColor(Color.parseColor("#000000"));
            if(Checklist.fadeSubTasks){
                checklistTextView.setTextColor(Color.parseColor("#DDDDDD"));
                tickFaded.setVisibility(View.GONE);
                tick.setVisibility(View.GONE);
                tickWhite.setVisibility(View.GONE);
                tickWhiteFaded.setVisibility(View.VISIBLE);
            }else {
                tick.setVisibility(View.GONE);
                tickWhite.setVisibility(View.VISIBLE);

            }
        }

        //registering that subtask should be marked as done
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                markAsDone(position, finalDbTaskId);

            }

        });

        //registering that subtask should be marked as done
        tickWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                markAsDone(position, finalDbTaskId);

            }

        });

        checklistTextView.setText(item);

        Boolean isKilled = false;

        //finding out if the subtask is completed
        Cursor dbIdResult = MainActivity.db.getSubtaskData(Integer.parseInt(dbTaskId),
                Checklist.sortedSubtaskIds.get(position));
        while (dbIdResult.moveToNext()) {
            isKilled = dbIdResult.getInt(3) > 0;
        }
        dbIdResult.close();

        //sub task is crossed out if it is marked as done
        if(isKilled){

            checklistTextView.setPaintFlags(checklistTextView.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);

            if(!MainActivity.lightDark){
                checklistItemView.setBackgroundColor(Color.parseColor("#333333"));
                if(Checklist.fadeSubTasks){
                    tickedFaded.setVisibility(View.VISIBLE);
                    tickFaded.setVisibility(View.GONE);
                }else {
                    ticked.setVisibility(View.VISIBLE);
                    tick.setVisibility(View.GONE);
                }
            }else{
                checklistItemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if(Checklist.fadeSubTasks){
                    tickWhiteFaded.setVisibility(View.GONE);
                    tickedWhiteFaded.setVisibility(View.VISIBLE);
                }else {
                    tickWhite.setVisibility(View.GONE);
                    tickedWhite.setVisibility(View.VISIBLE);
                }
            }

        }

        //setting sub task name
        if (Checklist.subTaskBeingEdited && (Integer.parseInt(dbTaskId) ==
                Checklist.sortedSubtaskIds.get(position)) &&
                !Checklist.goToChecklistAdapter) {

            String oldSubTaskString = Checklist.checklist
                    .get(Checklist.sortedSubtaskIds.get(Checklist.renameMe));

            checklistTextView.setText("");

            Checklist.checklistEditText.setText(oldSubTaskString);

            Checklist.checklistEditText.setSelection(Checklist.checklistEditText
                    .getText().length());

        }

        return checklistItemView;

    }

    private void markAsDone(int position, String finalDbTaskId) {

        MainActivity.vibrate.vibrate(50);

        Rect screen = new Rect();

        Checklist.checklistRootView.getWindowVisibleDisplayFrame(screen);

        //Screen pixel values are used to determine how much of the screen is visible
        int heightDiff = Checklist.checklistRootView.getRootView().getHeight() -
                (screen.bottom - screen.top);

        //Value of more than 800 seems to indicate that the keyboard is showing
        //in portrait mode
        if ((heightDiff > 800) && (Checklist.checklistRootView.getResources()
                .getConfiguration().orientation == 1)) {

            Checklist.subTasksClickable = false;

        //Similar to above but for landscape mode
        }else if((heightDiff > 73) && (heightDiff < 800) && (Checklist.checklistRootView
                .getResources().getConfiguration().orientation == 2)){

            Checklist.subTasksClickable = false;

        }else{

            Checklist.subTasksClickable = true;

        }

        if (Checklist.subTasksClickable) {

            boolean isKilled = false;

            //finding out if subtask has been killed
            Cursor dbIdResult = MainActivity.db.getSubtaskData(Integer.parseInt(finalDbTaskId),
                    Checklist.sortedSubtaskIds.get(position));
            while (dbIdResult.moveToNext()) {
                isKilled = dbIdResult.getInt(3) > 0;
            }
            dbIdResult.close();

            //Marks sub task as complete
            if (!isKilled){

                MainActivity.db.updateSubtaskKilled(finalDbTaskId, String.valueOf(Checklist
                        .sortedSubtaskIds.get(position)), true);
                Checklist.subTasksKilled.set(position, true);

                if(!MainActivity.mute) {
                    MainActivity.blip.start();
                }

                notifyDataSetChanged();

            }

        }

    }

}
