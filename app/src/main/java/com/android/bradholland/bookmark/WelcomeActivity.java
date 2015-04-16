package com.android.bradholland.bookmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

/**
 * Created by Brad on 10/8/2014.
 */
public class WelcomeActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;
    private EditText emailEditText;
    private boolean resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        resetPassword = false;
        final Button registerButton = (Button) findViewById(R.id.signup_button);
        final Button loginButton = (Button) findViewById(R.id.login_button);
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        passwordAgainEditText = (EditText) findViewById(R.id.passwordagain);
        emailEditText = (EditText) findViewById(R.id.email);


        usernameEditText.setAlpha(0);
        passwordEditText.setAlpha(0);
        passwordAgainEditText.setAlpha(0);
        emailEditText.setAlpha(0);
        usernameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        passwordAgainEditText.setEnabled(false);
        emailEditText.setEnabled(false);

        ImageView background = (ImageView) findViewById(R.id.iv_background);
        //ImageView logo = (ImageView) findViewById(R.id.logo);
        Picasso.with(this).load(R.drawable.bookshelfbg).fit().centerCrop().into(background);
        //Picasso.with(this).load(R.drawable.logo).fit().into(logo);


        // Log in button click handler
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                loginButton.setBackgroundColor(getResources().getColor(R.color.primary));
                resetPassword = true;
                registerButton.setBackgroundColor(getResources().getColor(R.color.accent));
                registerButton.setText(getResources().getString(R.string.forgot_password));

                usernameEditText.setAlpha(1);
                passwordEditText.setAlpha(1);
                usernameEditText.setEnabled(true);
                passwordEditText.setEnabled(true);

                passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == R.id.edittext_action_login ||
                                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                            login();
                            return true;
                        }
                        return false;
                    }
                });

                // Set up the submit button click handler
                loginButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        login();
                    }
                });
            }
        });

        // Sign up button click handler
        registerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Set up the signup form.
                if (!resetPassword) {
                    usernameEditText.setAlpha(1);
                    passwordEditText.setAlpha(1);
                    passwordAgainEditText.setAlpha(1);
                    emailEditText.setAlpha(1);
                    usernameEditText.setEnabled(true);
                    passwordEditText.setEnabled(true);
                    passwordAgainEditText.setEnabled(true);
                    emailEditText.setEnabled(true);
                    loginButton.setEnabled(false);
                    loginButton.setAlpha(0);

                    passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == R.id.edittext_action_signup ||
                                    actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                                signup();
                                return true;
                            }
                            return false;
                        }
                    });

                    // Set up the submit button click handler
                    registerButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            signup();
                        }
                    });
                } else {
                    showPasswordResetDialog();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(WelcomeActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        // Call the Parse login method
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(WelcomeActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void signup() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        if (email.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_email));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_no_email));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(WelcomeActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);


        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(WelcomeActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public void showPasswordResetDialog() {
        new MaterialDialog.Builder(this)
                .title("Password reset")
                .customView(R.layout.dialog_reset_password, false)
                .positiveText("send")
                .negativeText("cancel")
                .negativeColorRes(R.color.primary)
                .positiveColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View v = dialog.getCustomView();
                        EditText email = (EditText) v.findViewById(R.id.et_dialog_email);
                        String s = email.getText().toString().trim();
                        ParseUser.requestPasswordResetInBackground(s, new RequestPasswordResetCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getBaseContext(), "Email sent", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                })
                .show();
    }

    @Override
    public void onPause() {
        resetPassword = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        resetPassword = false;
        super.onDestroy();
    }

}