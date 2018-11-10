package com.zjianhao.album;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.zjianhao.R;

import java.util.ArrayList;

public class settingActivity extends Activity {
    private Spinner s;
    private String Cloud_directory;
    private String Local_directory;
    private int sort_type = 0;
    private int sort_time_type = 0;
    private String numStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final TextView tv = (TextView)findViewById(R.id.textView4); // 값들어오는지 확인용

        s = (Spinner)findViewById(R.id.spinner_time);
        s.setEnabled(false);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sort_time_type = 0;
                        break;
                    case 1:
                        sort_time_type = 1;
                        break;
                    case 2:
                        sort_time_type = 2;
                        break;
                    default:
                        break;
                }
                //tv.setText(""+ parent.getItemAtPosition(position)); //선택한 내용이 잘들어오는지 확인용
                //tv.setText(""+ sort_time_type); // 잘 저장되는지 확인용
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

/* 설정값 저장하기 - 박상혁 */
    public void click_save(View view) {
        EditText editText = (EditText) findViewById(R.id.cloud_Directory);  // editText의 값을 받아옴
        Cloud_directory = editText.getText().toString();  // 받아온 값을 string변수에 저장

        editText = (EditText) findViewById(R.id.local_Directory);
        Local_directory = editText.getText().toString();
    }

    public void click_radioButton_time(View view) {
        sort_type = 1;
        s.setEnabled(true);
//        numStr = String.valueOf(sort_type);    // 값이 잘 들어오는지 확인용 Toast
//        Toast.makeText(this, numStr, Toast.LENGTH_SHORT).show();
    }

    public void click_radioButton_location(View view) {
        sort_type = 2;
        s.setEnabled(false);
//        numStr = String.valueOf(sort_type);    // 값이 잘 들어오는지 확인용 Toast
//        Toast.makeText(this, numStr, Toast.LENGTH_SHORT).show();
    }
}
