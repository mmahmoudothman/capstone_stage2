package com.example.osos.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osos.myapplication.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private DatabaseReference mUsersDatabase;
    private RecyclerView mUsersList;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Mido Chat");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


//
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }


        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, final Users model, int position) {
                usersViewHolder.setName(model.getName());
                usersViewHolder.setUserStatus(model.getStatus());
                usersViewHolder.setImage(model.getImage(), getApplicationContext());

                final String user_id = getRef(position).getKey();
                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage(R.string.message_dialog);
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                R.string.show_profile,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                                        profileIntent.putExtra("user_id", user_id);
                                        startActivity(profileIntent);

                                        dialog.cancel();
                                    }
                                });

                        builder1.setNegativeButton(
                                "Send Message",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                                        chatIntent.putExtra("user_id", user_id);
                                        chatIntent.putExtra("user_name", model.getName());
                                        startActivity(chatIntent);

                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);


    }

    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_layout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        } else if (item.getItemId() == R.id.main_Settingd_btn) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
//        } else if (item.getItemId() == R.id.float_widget) {
////            initializeView();
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
//                initializeView();
            } else { //Permission is not available
                Toast.makeText(this, R.string.prmmision, Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    private void initializeView() {
//        startService(new Intent(MainActivity.this, FloatingViewService.class));
//        finish();
//    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        TextView userNameView;
        TextView userstatusView;
        CircleImageView userImage;

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userstatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userImage = (CircleImageView) mView.findViewById(R.id.user_single_image);
        }


        public void setName(String name) {
            userNameView.setText(name);
        }

        public void setUserStatus(String userStatus) {
            userstatusView.setText(userStatus);
        }

        public void setImage(String image, Context applicationContext) {
            if (!image.equals("default")) {
                Picasso.with(applicationContext).load(image).placeholder(R.drawable.ma).into(userImage);
            }
        }
    }


}


