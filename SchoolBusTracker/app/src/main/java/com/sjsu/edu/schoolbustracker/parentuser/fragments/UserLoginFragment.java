package com.sjsu.edu.schoolbustracker.parentuser.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.login.LoginManager;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;



import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.activity.BottomNavigationActivity;
import com.sjsu.edu.schoolbustracker.parentuser.activity.UserRegistration;
import com.sjsu.edu.schoolbustracker.parentuser.model.ParentUsers;
import com.sjsu.edu.schoolbustracker.parentuser.model.Profile;
import com.sjsu.edu.schoolbustracker.parentuser.model.UserSettings;


public class UserLoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UserLoginFragment";
    private static final int RC_SIGN_IN = 9001;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private DatabaseReference mDatabase, mCheckUserTypeRef, mProfileRef,userSettingsReference;


    private LoginButton mFbLoginButton;
    private CallbackManager callbackManager;
    private AppCompatEditText loginUserID,loginPassword;
    private AppCompatButton loginButton,signUpButton,signOutButton,testButton;
    public ProgressDialog mProgressDialog;


    //Google Auth
    GoogleApiClient mGoogleApiClient;

    // TODO: Rename and change types of parameters



    private OnFragmentInteractionListener mListener;

    public UserLoginFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + " " + user.getEmail());


                    mCheckUserTypeRef = FirebaseUtil.getCheckUserRef();

                    mCheckUserTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean doesProfileExists = dataSnapshot.hasChild(user.getUid());
                            if(doesProfileExists){
                                Log.d("ProfileExists", user.getEmail() + " profile exists in the database" );

                                Boolean isDriver = dataSnapshot.child(user.getUid()).child("isDriver").getValue(Boolean.class);
                                Log.d("ProfileExists", isDriver.toString());
                                if(isDriver){

                                    mProfileRef = FirebaseUtil.getDriverRef();

                                }else{
                                    mProfileRef = FirebaseUtil.getParentUserRef().child(user.getUid()).getRef();

                                    mProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ParentUsers existingParent = dataSnapshot.getValue(ParentUsers.class);
                                            Log.d("ProfileExists", existingParent.getEmailId() + " email from existing parent");
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }else{
                                Log.d("ProfileExists", user.getEmail() + " profile doesn't exist in the database" );
                                mCheckUserTypeRef.child(user.getUid()).child("isDriver").setValue(false);

                                userSettingsReference = FirebaseUtil.getBaseRef()
                                        .child(getString(R.string.firebase_settings_node))
                                        .child(user.getUid());
                               

                                UserSettings newSettings = new UserSettings();
                                newSettings.setEmailNotification(true);
                                newSettings.setPushNotification(true);
                                newSettings.setTextNotification(true);

                                userSettingsReference.setValue(newSettings);

                                Profile newParent = new ParentUsers();
                                newParent.setUUID(user.getUid());
                                newParent.setName(user.getDisplayName());
                                newParent.setEmailId(user.getEmail());
                                FirebaseUtil.getParentUserRef()
                                        .child(newParent.getUUID())
                                        .setValue(newParent);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    if(UserLoginFragment.this.isAdded()){
                        Intent intent =new Intent(getActivity(),BottomNavigationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);
        mFbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginUserID =  (AppCompatEditText) view.findViewById(R.id.LoginUserEmail);
        loginPassword =  (AppCompatEditText) view.findViewById(R.id.LoginUserPassword);
        loginButton = (AppCompatButton) view.findViewById(R.id.LoginButton);
        signUpButton = (AppCompatButton) view.findViewById(R.id.SignUpButton);
        signOutButton = (AppCompatButton) view.findViewById(R.id.sign_out_button);



        mFbLoginButton.setReadPermissions("email", "public_profile");
        // If using in a fragment
        mFbLoginButton.setFragment(this);
        // Other app specific specialization

        callbackManager = CallbackManager.Factory.create();


        mFbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mFbLoginButton.getText()
                        .equals(getResources().getString(R.string.com_facebook_loginview_log_in_button_continue)))
                    showProgressDialog();
            }
        });


        // Callback registration
        mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {

                        hideProgressDialog();

                        Log.d(TAG, "Facebook cancelled the login process");
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        hideProgressDialog();

                        Log.e(TAG,"Facebook exception", new Exception());
                    }

                });
        // Inflate the layout for this fragment

        //Google Authentication
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) getActivity() /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //Email Password Login using Firebase
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(loginUserID.getText().toString(), loginPassword.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                                    Toast.makeText(getActivity() , R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Log.w(TAG, "signInWithEmail:successful");
                                    // Start the Landing Activity
                                }

                                // ...
                            }
                        });
            }
        });

        //Start Activity to Register New User
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerNewUserActivity = new Intent(getActivity(),UserRegistration.class);
                startActivity(registerNewUserActivity);
            }
        });

        //Sign Out of Google
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // [START_EXCLUDE]
                              //  updateUI(false);
                                // [END_EXCLUDE]
                            }
                        });
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }




    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("ULF","On Detach has been called");
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
            showProgressDialog();

        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
           // updateUI(true);
            firebaseAuthWithGoogle(acct);

            Log.d(TAG, "auth successful:");
        } else {
            // Signed out, show unauthenticated UI.
           // updateUI(false);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            LoginManager.getInstance().logOut();
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        hideProgressDialog();


                    }
                });
    }

    public void showProgressDialog() {

        if (mProgressDialog == null && this.isAdded()) {

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }


        if(mProgressDialog != null)
            mProgressDialog.show();

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideProgressDialog();
    }

}