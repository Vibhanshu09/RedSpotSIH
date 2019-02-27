package com.sistec.redspot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    TextView tvSignUp, tvForget;
    Button btnLogin;
    EditText id, pass;
    private FirebaseAuth auth;
    private SharedPreferences file;
    private AlertDialog.Builder alertDialog = null;
    private AlertDialog dialog;

    MaterialEditText Edtuser;

    private ProgressDialog processDialog;
    private LinearLayout loginAcyivityBaseLayout;

    Button Validate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        processDialog = new ProgressDialog(this);
        loginAcyivityBaseLayout = findViewById(R.id.login_activity_base_layout);
        initItems();
        setButtonClickListeners();
    }

    private void initItems() {
        tvSignUp = findViewById(R.id.TVSignup);
        tvForget = findViewById(R.id.TVforget);
        btnLogin = findViewById(R.id.BTLogin);
        id = findViewById(R.id.ETemail);
        pass = findViewById(R.id.ETpass);
        auth = FirebaseAuth.getInstance();
    }


    private void setButtonClickListeners() {
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                processDialog.setTitle("Login");
                processDialog.setMessage("Please Wait");
                processDialog.show();
                startUserLogin();
            }
        });

        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgetPassDialog();
            }
        });
    }

    private void startUserLogin(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pass.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);

        if (id.getText().toString().trim().equals("")) {
            id.setError("Email Required");
            processDialog.dismiss();
        } else {
            if (pass.getText().toString().trim().equals("")) {
                pass.setError("Password Required");
                processDialog.dismiss();
            } else {
                auth.signInWithEmailAndPassword(id.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                String _errorMessage = task.getException() != null ? task.getException().getMessage() : "";
                                if (task.isSuccessful()) {
                                    processDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                } else {
                                    processDialog.dismiss();
                                    showSnackbar(_errorMessage);
                                }

                            }
                        });
            }
        }
    }

    private void showSnackbar(String msg){
        Snackbar snackbar = Snackbar.make(loginAcyivityBaseLayout,msg, 5000);
        // Changing action button text color
        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void showForgetPassDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(LoginActivity.this);
            alertDialog.setTitle("Password Recovery");
            // alertDialog.setMessage("Please Fill Full Information");

            LayoutInflater inflater = this.getLayoutInflater();
            View sign_up_layout = inflater.inflate(R.layout.pass_recovery_layout, null);

            Edtuser = sign_up_layout.findViewById(R.id.phone);
            Validate = sign_up_layout.findViewById(R.id.BTverify);
            dialog = alertDialog.create();
            Validate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String idd = Edtuser.getText().toString().trim();
                    if (idd.isEmpty()){
                        Edtuser.setError("Please Enter Email Id");
                    } else {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(idd)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showSnackbar("Email Sent to " + idd + "!");
                                            dialog.dismiss();
                                        }
                                    }
                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showSnackbar("" + e.getMessage());
                                Edtuser.setError(e.getMessage());
                            }
                        })
                        ;
                    }
                }

            });
            alertDialog.setView(sign_up_layout);
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                    alertDialog = null;
                }
            });
            alertDialog.setIcon(R.drawable.ic_pass_recover_24dp);
            alertDialog.show();
        }

    }

}
