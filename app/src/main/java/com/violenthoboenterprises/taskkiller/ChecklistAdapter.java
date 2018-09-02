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
import android.widget.Button;
import android.widget.ImageButton;
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
//        final Button tick = checklistItemView.findViewById(R.id.tick);
        final ImageView tick = checklistItemView.findViewById(R.id.subtaskComplete);
        final ImageView ticked = checklistItemView.findViewById(R.id.subtaskCompleted);
        TAG = "ChecklistAdapter";

        //actions to occur when sub task marked as done
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                    //Marks sub task as complete
                    if (!Checklist.subTasksKilled.get(Integer.parseInt(MainActivity
                            .sortedIdsForNote.get(MainActivity.activeTask))).get(position)) {

//                        MainActivity.vibrate.vibrate(50);

                        Checklist.subTasksKilled.get(Integer.parseInt(MainActivity
                                .sortedIdsForNote.get(MainActivity.activeTask)))
                                .set(position, true);

                        notifyDataSetChanged();

                    //Removes sub task
                    } else {

//                        MainActivity.vibrate.vibrate(50);

                        Checklist.checklistList.get(Integer.parseInt(MainActivity.sortedIdsForNote
                                .get(MainActivity.activeTask))).remove(position);

                        Checklist.subTasksKilled.get(Integer.parseInt(MainActivity.sortedIdsForNote
                                .get(MainActivity.activeTask))).remove(position);

                        notifyDataSetChanged();

                        Cursor result = MainActivity.noteDb.getData(Integer.parseInt(MainActivity
                                .sortedIdsForNote.get(MainActivity.activeTask)));
                        while(result.moveToNext()){
                            Checklist.noteExists = (result.getInt(2) == 1);
                        }

                        if(Checklist.checklistList.get(Integer.parseInt(MainActivity
                                .sortedIdsForNote.get(MainActivity.activeTask))).size() == 0){
                            //setting checklist in database to false
                            MainActivity.noteDb.updateData(MainActivity.sortedIdsForNote
                                    .get(MainActivity.activeTask), "", false);
                        }

                    }

                }

            }

        });

        checklistTextView.setText(item);

        try {
            //sub task is crossed out if it is marked as done
            if (Checklist.subTasksKilled.get(Integer.parseInt(MainActivity.sortedIdsForNote
                    .get(MainActivity.activeTask))).get(position)) {

                checklistTextView.setPaintFlags(checklistTextView.getPaintFlags() |
                        Paint.STRIKE_THRU_TEXT_FLAG);

                //The 'done' button changes to say 'remove' if task is complete
                tick.setBackgroundColor(Color.RED);

//                tick.setText("Remove");
                tick.setVisibility(View.GONE);
                ticked.setVisibility(View.VISIBLE);

            }
        } catch (IndexOutOfBoundsException e){

        }

        //greying out unselected sub tasks
        if (Checklist.subTaskBeingEdited && (position != Checklist.activeSubTask)) {

            checklistItemView.setBackgroundColor(Color.parseColor("#888888"));

        }

        //setting sub task name
        if (Checklist.subTaskBeingEdited && (position == Checklist.activeSubTask) &&
                !Checklist.goToChecklistAdapter) {

            String oldSubTaskString = checklistTextView.getText().toString();

            checklistTextView.setText("");

            Checklist.checklistEditText.setText(oldSubTaskString);

            Checklist.checklistEditText.setSelection(Checklist.checklistEditText
                    .getText().length());

        }

//        if (Checklist.fadeSubTasks) {
//
//            //fade sub tasks when keyboard is up
////            Checklist.checklistRootView.setBackgroundColor(Color.parseColor("#888888"));
//
////            tick.setBackgroundColor(Color.parseColor("#888888"));
//
//        } else {
//
//            //sub tasks are white when keyboard is down
//            Checklist.checklistRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//            if (Checklist.subTasksKilled.get(Integer.parseInt(MainActivity.sortedIdsForNote
//                    .get(MainActivity.activeTask))).get(position)) {
//
////                tick.setBackgroundColor(Color.parseColor("#FF0000"));
//
//            } else {
//
////                tick.setBackgroundColor(Color.parseColor("#00FF00"));
//
//            }
//
//        }

        return checklistItemView;

    }

}
