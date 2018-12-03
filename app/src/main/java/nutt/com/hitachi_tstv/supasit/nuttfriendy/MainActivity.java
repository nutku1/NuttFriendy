package nutt.com.hitachi_tstv.supasit.nuttfriendy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { //savedInstanceState เอาไว้เก็บการทำงานว่าทำถึงตอนไหน แล้วเมื่อเปิดใหม่ ให้ทำต่อ
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Add Fragment to Activity
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.contentMainFragment, new MainFragment()).commit();
        }
    } ///Main Method
} ///Main Class
