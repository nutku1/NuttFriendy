package nutt.com.hitachi_tstv.supasit.nuttfriendy;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.security.Permission;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    //    Explicit
    private boolean aBoolean = true;
    private ImageView imageView;
    private Uri uri;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Create Toolbar
        createToolbar();

//        Avatar Controller
        avatarController();
    } /// Main Method

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            aBoolean = false;
            uri = data.getData();

            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 800, 600, false);
                imageView.setImageBitmap(bitmap1);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } ///IF
    }

    private void avatarController() {
        imageView = getView().findViewById(R.id.imbAvatar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Please App and Picture"), 9);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemUpload) {
            CheckAndUploadValues();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void CheckAndUploadValues() {

        MyAlert myAlert = new MyAlert(getActivity());

//        Get values from Username Check
        EditText nameEditText = getView().findViewById(R.id.edtName);
        EditText userEditText = getView().findViewById(R.id.edtUser);
        EditText passwordEditText = getView().findViewById(R.id.edtPassword);

        String nameString = nameEditText.getText().toString().trim();
        String userString = userEditText.getText().toString().trim();
        String passwordString = passwordEditText.getText().toString().trim();

        if (aBoolean) {
            myAlert.normalDialog("Non Choose Avatar ???", "Please Choose Avatar");
        } else if (nameString.isEmpty() || userString.isEmpty() || passwordString.isEmpty()) {
            myAlert.normalDialog("Have Space", "Please Fill Every Blank");
        } else {

//            Find Path Choose  Avatar
            String pathImageString = null;
            String[] strings = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, strings, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                pathImageString = cursor.getString(index);

            } else {
                pathImageString = uri.getPath();
            }

            Log.d("4DecV1", "Path ==>" + pathImageString); /// แสดง log

//            Find name of Image
            String nameImageString = pathImageString.substring(pathImageString.lastIndexOf("/"));
            Log.d("4DecV1", "Name Image ==>" + nameImageString);

//            Upload File to Server
//            Create File
            File file = new File(pathImageString);


//            Permission Policy
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy
                    .Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            FTPClient ftpClient = new FTPClient();

            try {

                ftpClient.connect("ftp.androidthai.in.th", 21);
                ftpClient.login("hit@androidthai.in.th", "Abc12345");
                ftpClient.setType(FTPClient.TYPE_BINARY);
                ftpClient.changeDirectory("nuutt");
                ftpClient.upload(file, new UploadListener());

//                Add Values To Server
                MyConstant myConstant = new MyConstant();
                AddUserToServerThread addUserToServerThread = new AddUserToServerThread(getActivity());
                addUserToServerThread.execute(
                        nameString,  ///1
                        userString,  ///2
                        passwordString,   ///3
                        myConstant.getUrlPreAvata() + nameImageString, ///4
                        myConstant.getUrlAddUser()); ///5

                String result = addUserToServerThread.get();
                Log.d("4DecV1", "Result  ==>" + result);

                if (Boolean.parseBoolean(result)) {
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    myAlert.normalDialog("Cannot Upload","Please try again!!");
                }

            } catch (Exception e) {
                e.printStackTrace();

                try {
                    ftpClient.disconnect(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }///IF


    } ///CheckAndUploadVales

    public class UploadListener implements FTPDataTransferListener {

        @Override
        public void started() {
            Toast.makeText(getActivity(), "Start Upload", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void transferred(int i) {
            Toast.makeText(getActivity(), "Continue Upload", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void completed() {
            Toast.makeText(getActivity(), "Success Upload", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void aborted() {

        }

        @Override
        public void failed() {

        }

    } ///UploadListener


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_register, menu);
    }

    private void createToolbar() {
        android.support.v7.widget.Toolbar toolbar = getView().findViewById(R.id.toolbarRegister);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.register));
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Please fill all Blank");
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Create POP
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

}
