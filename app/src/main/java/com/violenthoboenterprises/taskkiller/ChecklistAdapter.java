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

        //actions to occur when sub task marked as done
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                markAsDone(position);

            }

        });

        tickWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                markAsDone(position);

            }

        });

        checklistTextView.setText(item);

        String id = "";
        Boolean isKilled = false;

        Cursor dbTaskResult = MainActivity.db.getUniversalData();
        while (dbTaskResult.moveToNext()) {
            id = dbTaskResult.getString(4);
        }
        dbTaskResult.close();
        Cursor dbIdResult = MainActivity.db.getSubtaskData(Integer.parseInt(id),
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

        String resultId = "";

        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            resultId = dbResult.getString(4);
        }
        dbResult.close();

        //setting sub task name
        if (Checklist.subTaskBeingEdited && (Integer.parseInt(resultId) ==
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

    private void markAsDone(int position) {

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
            String id = "";

            Cursor dbTaskResult = MainActivity.db.getUniversalData();
            while (dbTaskResult.moveToNext()) {
                id = dbTaskResult.getString(4);
            }
            dbTaskResult.close();

            Cursor dbIdResult = MainActivity.db.getSubtaskData(Integer.parseInt(id),
                    Checklist.sortedSubtaskIds.get(position));
            while (dbIdResult.moveToNext()) {
                isKilled = dbIdResult.getInt(3) > 0;
            }
            dbIdResult.close();

            //Marks sub task as complete
            if (!isKilled){

                MainActivity.db.updateSubtaskKilled(id, String.valueOf(Checklist
                        .sortedSubtaskIds.get(position)), true);
                Checklist.subTasksKilled.set(
                        Checklist.sortedSubtaskIds.get(position), true);

                if(!MainActivity.mute) {
                    MainActivity.punch.start();
                }

                notifyDataSetChanged();

            }

        }

    }

}
