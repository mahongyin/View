package android.signin.com.myapplication;

import android.signin.com.myapplication.view.Signin;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button one;
    private Button two;
    private Button three;
    private Button four;
    private Button fif;
    private Button six;
    private Button seven;
    private Button clear;
    private Button add;
    private Signin signin;
    private List<String> signInData = new ArrayList<>();
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        four = (Button) findViewById(R.id.four);
        fif = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        seven = (Button) findViewById(R.id.seven);
        clear = (Button) findViewById(R.id.clear);
        add = (Button) findViewById(R.id.add);
        signin = (Signin) findViewById(R.id.sigin);
        editText = (EditText) findViewById(R.id.et);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        fif.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        clear.setOnClickListener(this);
        add.setOnClickListener(this);
        signInData.add("第一天");
        signInData.add("第二天");
        signInData.add("第三天");
        signInData.add("第四天");
        signInData.add("第五天");
        signInData.add("第六天");
        signInData.add("第七天");
        signin.setSignInData(signInData);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MainActivity.this,"dianjile ",Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one:
                signin.setCurretn(1);
                break;
            case R.id.two:
                signin.setCurretn(2);
                break;
            case R.id.three:
                signin.setCurretn(3);
                break;
            case R.id.four:
                signin.setCurretn(4);
                break;
            case R.id.five:
                signin.setCurretn(5);
                break;
            case R.id.six:
                signin.setCurretn(6);
                break;
            case R.id.seven:
                signin.setCurretn(7);
                break;
            case R.id.add:
                signin.setSignInEvent();
                break;
            case R.id.clear:
                signin.setSignInClear();
                break;
        }
    }
}
