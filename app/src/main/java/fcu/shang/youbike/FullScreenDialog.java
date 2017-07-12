package fcu.shang.youbike;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by SERS on 2017/7/5.
 */

public class FullScreenDialog extends Dialog{


    public FullScreenDialog(Context context) {
        super(context,R.style.MyFullScreenDialog);
        setContentView(R.layout.dialoglayout);

        TextView desTv=(TextView)findViewById(R.id.desTv);
        desTv.setText(context.getResources().getString(R.string.description));

        Button button=(Button)findViewById(R.id.desBt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
