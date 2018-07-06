package com.per.epx.easytrain.helpers;

import android.content.DialogInterface;
import android.view.View;

public class DialogHelper {
    public static class DismissListener implements View.OnClickListener{
        private final DialogInterface dialog;
        public DismissListener(DialogInterface dialog){
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    }
}
