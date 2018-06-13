package clonetechapps.recipes;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

//Due to dialogs being used across a few classes, I have created a class for setting some
//common things such as OK, Cancel and Delete button preferences.
//These tend not to change look so as to display consistency across the application.
public class DialogSettings {

    public Button setOkBtn(Button button){
        Button okBtn = button;
        LinearLayout.LayoutParams posBtnLP = (LinearLayout.LayoutParams) okBtn.getLayoutParams();
        posBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBtn.setPadding(50, 10, 10, 10);   // Set Position
        okBtn.setTextColor(Color.BLUE);
        okBtn.setLayoutParams(posBtnLP);

        return okBtn;
    }

    public Button setDeleteBtn(Button button){
        Button delBtn = button;
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) delBtn.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        delBtn.setPadding(50,10,10,10);
        delBtn.setTextColor(Color.RED);
        delBtn.setLayoutParams(neutralBtnLP);

        return delBtn;
    }

    public Button setCancelBtn(Button button){
        Button cancelBtn = button;
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) cancelBtn.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBtn.setTextColor(Color.GRAY);
        cancelBtn.setLayoutParams(negBtnLP);

        return cancelBtn;
    }

    public TextView setDialogTitle(TextView toSet, String text){
        TextView title = toSet;
        title.setText(text);
        title.setPadding(10,10,10,10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);

        return title;
    }
}
