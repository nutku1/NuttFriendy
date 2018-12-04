package nutt.com.hitachi_tstv.supasit.nuttfriendy;


import android.icu.text.Replaceable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    } //Constructor


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Register Controller
        registerController();

//        Login Controller
        loginController();
    } //Main Method

    private void loginController() {
        Button button = getView().findViewById(R.id.btnLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText userEditText = getView().findViewById(R.id.edtUser);
                EditText PasswordEditText = getView().findViewById(R.id.edtPassword);

                String userString = userEditText.getText().toString().trim();
                String passwordString = PasswordEditText.getText().toString().trim();

                MyAlert myAlert = new MyAlert(getActivity());

                if (userString.isEmpty() || passwordString.isEmpty()) {
                    myAlert.normalDialog(getString(R.string.title_space), getString(R.string.message_space));
                } else {

//                    Check User and Password
                    checkUserAndPass(userString, passwordString);
                }

            }
        });
    }

    private void checkUserAndPass(String userString, String passwordString) {
        MyAlert myAlert = new MyAlert(getActivity());
        MyConstant myConstant = new MyConstant();
        boolean b = true; //True == user false , False == user true


        try {
            GetAllDataThread getAllDataThread = new GetAllDataThread(getActivity());
            getAllDataThread.execute(myConstant.getUrlGetAllUser());
            String jsonString = getAllDataThread.get();
            Log.d("4DecV2", "jsonString ==>" + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerController() {
        TextView textView = getView().findViewById(R.id.textRegister);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Replace Fragment
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentMainFragment, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    } //Create View

}
