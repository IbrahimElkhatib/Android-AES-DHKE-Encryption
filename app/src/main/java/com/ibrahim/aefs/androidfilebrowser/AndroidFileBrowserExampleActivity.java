package com.ibrahim.aefs.androidfilebrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrahim.aefs.DHKE;
import com.ibrahim.aefs.MainAESActivity;
import com.ibrahim.aefs.R;

import java.io.File;
import java.math.BigInteger;

import javax.crypto.SecretKey;

//import android.os.Environment;

public class AndroidFileBrowserExampleActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private final static String LOGTAG = AndroidFileBrowserExampleActivity.class.getSimpleName();

    private final int REQUEST_CODE_Encrypt_FILE = 3;
    private final int REQUEST_CODE_Decrypt_FILE = 4;
    //Arbitrary constant to discriminate against values returned to onActivityResult
    // as requestCode
    EditText password;
    byte[] salt = {1, 2, 3};
    private TextView publicKey, p, g;
    boolean showpass = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        final Activity activityForButton = this;

        password = (EditText) findViewById(R.id.password);
        publicKey = (TextView) findViewById(R.id.txt_publicKey);
        p = (TextView) findViewById(R.id.txt_p);
        g = (TextView) findViewById(R.id.txt_g);
        Button btn_pass = (Button) findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showpass) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showpass = false;
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    showpass = true;
                }
                password.setSelection(password.getText().length());
            }
        });

        Button dhke = (Button) findViewById(R.id.btn_DHKE);
        dhke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GenerateDHKEparms().execute("");
//				try {
//					if(publicKey.getText().toString().isEmpty()) {
//						AlertDialog.Builder builder = new AlertDialog.Builder(AndroidFileBrowserExampleActivity.this);
//						builder.setMessage("Please wait to generate !!!")
//								.setCancelable(false);
//						AlertDialog alert = builder.create();
//						alert.show();
//
//						BigInteger[] values = DHKE.createDHKEKey();
//
//
//						Intent sendIntent = new Intent();
//						sendIntent.setAction(Intent.ACTION_SEND);
//						sendIntent.putExtra(Intent.EXTRA_TEXT, "Public Key: " + values[1]+
//								"\np: "+values[2]+
//						"\nq: "+values[3]);
//						sendIntent.setType("text/plain");
//						startActivity(sendIntent);
//						alert.dismiss();
//					}else {
//						DHKE.createSpecificKey((BigInteger)p.getText(),(BigInteger)g.getText(),(BigInteger)publicKey.getText());
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
            }
        });
        Button recieveDHKE = (Button) findViewById(R.id.btn_reciveDHKE);
        recieveDHKE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RecieveDHKEparms().execute("");
//				try {
//					if(!publicKey.getText().toString().isEmpty() && !p.getText().toString().isEmpty() && !g.getText().toString().isEmpty()) {
//						AlertDialog.Builder builder = new AlertDialog.Builder(AndroidFileBrowserExampleActivity.this);
//						builder.setMessage("Please wait to generate !!!")
//								.setCancelable(false);
//						AlertDialog alert = builder.create();
//						alert.show();
//
//						BigInteger[] values = DHKE.createSpecificKey(new BigInteger(p.getText().toString()),
//								new BigInteger(g.getText().toString()),new BigInteger(publicKey.getText().toString()));
//						BigInteger commonKey = values[0];
//						password.setText(commonKey.toString());
//						Intent sendIntent = new Intent();
//						sendIntent.setAction(Intent.ACTION_SEND);
//						sendIntent.putExtra(Intent.EXTRA_TEXT, "Public Key: " + values[1]);
//						sendIntent.setType("text/plain");
//						startActivity(sendIntent);
//						alert.dismiss();
//					}else if(!publicKey.getText().toString().isEmpty()) {
//						BigInteger commonKey = DHKE.createKeyfromPublic(new BigInteger(publicKey.getText().toString()));
//						password.setText(commonKey.toString());
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
            }
        });


        Button encryptFile = (Button) findViewById(R.id.encryptFile);
        encryptFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(
                            AndroidFileBrowserExampleActivity.this,
                            "Please enter Password",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(LOGTAG, "encryptFile button pressed");
                Intent fileExploreIntent = new Intent(
                        com.ibrahim.aefs.androidfilebrowser.FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
                        null,
                        activityForButton,
                        com.ibrahim.aefs.androidfilebrowser.FileBrowserActivity.class
                );
//        		fileExploreIntent.putExtra(
//        				com.ibrahim.aefs.androidfilebrowser.FileBrowserActivity.startDirectoryParameter,
//        				"/sdcard"
//        				);
                startActivityForResult(
                        fileExploreIntent,
                        REQUEST_CODE_Encrypt_FILE
                );
            }//public void onClick(View v) {
        });
        final Button decryptFile = (Button) findViewById(R.id.decryptFile);
        decryptFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(
                            AndroidFileBrowserExampleActivity.this,
                            "Please enter Password",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(LOGTAG, "decryptFile button pressed");
                Intent fileExploreIntent = new Intent(
                        com.ibrahim.aefs.androidfilebrowser.FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
                        null,
                        activityForButton,
                        com.ibrahim.aefs.androidfilebrowser.FileBrowserActivity.class
                );
//        		fileExploreIntent.putExtra(
//        				com.ibrahim.aefs.androidfilebrowser.FileBrowserActivity.startDirectoryParameter,
//        				"/sdcard"
//        				);
                startActivityForResult(
                        fileExploreIntent,
                        REQUEST_CODE_Decrypt_FILE
                );
            }//public void onClick(View v) {
        });


    }//public void onCreate(Bundle savedInstanceState) {

    private class RecieveDHKEparms extends AsyncTask<String, Void, String> {
        AlertDialog.Builder builder;
        AlertDialog alert;
        BigInteger pB, gB, publicKeyB;
        boolean specificKEY;
        BigInteger commonKey;

        @Override
        protected String doInBackground(String... params) {

            try {
                if (specificKEY) {

                    BigInteger[] values = DHKE.createSpecificKey(pB,
                            gB, publicKeyB);
                    commonKey = values[0];
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Public Key: " + values[1]);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    alert.dismiss();
                } else {
                    commonKey = DHKE.createKeyfromPublic(publicKeyB);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            alert.dismiss();
            password.setText(commonKey.toString());
            Toast.makeText(
                    AndroidFileBrowserExampleActivity.this,
                    "Common Key established",
                    Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onPreExecute() {
            if (!publicKey.getText().toString().isEmpty() && !p.getText().toString().isEmpty() && !g.getText().toString().isEmpty()) {
                pB = new BigInteger(p.getText().toString().trim());
                gB = new BigInteger(g.getText().toString().trim());
                publicKeyB = new BigInteger(publicKey.getText().toString().trim());
                specificKEY = true;
            } else if (!publicKey.getText().toString().isEmpty()) {
                publicKeyB = new BigInteger(publicKey.getText().toString().trim());
                specificKEY = false;
            }

            builder = new AlertDialog.Builder(AndroidFileBrowserExampleActivity.this);
            builder.setMessage("Please wait to generate Keys !!!")
                    .setCancelable(false);
            alert = builder.create();
            alert.show();
        }


        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    private class GenerateDHKEparms extends AsyncTask<String, Void, String> {
        AlertDialog.Builder builder;
        AlertDialog alert;

        @Override
        protected String doInBackground(String... params) {

            try {
                BigInteger[] values = DHKE.createDHKEKey();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Public Key: " + values[1] +
                        "\np: " + values[2] +
                        "\nq: " + values[3]);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            } catch (Exception e) {
                Toast.makeText(
                        AndroidFileBrowserExampleActivity.this,
                        "Error in operation",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            alert.dismiss();
        }

        @Override
        protected void onPreExecute() {
            p.setText("");
            g.setText("");
            publicKey.setText("");
            builder = new AlertDialog.Builder(AndroidFileBrowserExampleActivity.this);
            builder.setMessage("Please wait to generate Keys !!!")
                    .setCancelable(false);
            alert = builder.create();
            alert.show();
        }


        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_Encrypt_FILE) {
            if (resultCode == RESULT_OK) {
                String newFile = data.getStringExtra(
                        com.ibrahim.aefs.androidfilebrowser.FileBrowserActivity.returnFileParameter);
                String newFileName = data.getStringExtra(
                        FileBrowserActivity.returnFileName);
                Toast.makeText(
                        this,
                        "Received FILE path from file browser:\n" + newFile,
                        Toast.LENGTH_SHORT).show();
                SecretKey yourKey;
                try {

                    yourKey = MainAESActivity.generateKey(password.getText().toString().trim().toCharArray(), salt);
                    Log.d("Encryption", "Encrypt: " + yourKey.toString());

                    final String file = MainAESActivity.saveFile(
                            MainAESActivity.encodeFile(yourKey, MainAESActivity.readFile(newFile)), newFileName, true);

                    AlertDialog.Builder builder = new AlertDialog.Builder(AndroidFileBrowserExampleActivity.this);

                    builder.setTitle("Share File");
                    builder.setMessage("Do you want to share the file ?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            if (!file.isEmpty()) {
                                Uri uri = Uri.fromFile(new File(file));
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                shareIntent.setType("*/*");
                                startActivity(Intent.createChooser(shareIntent, "Send To"));
                            }

                            dialog.dismiss();
                        }

                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            this,
                            "Error in operation",
                            Toast.LENGTH_LONG).show();
                }

            } else {//if(resultCode == this.RESULT_OK) {
                Toast.makeText(
                        this,
                        "Received NO result from file browser",
                        Toast.LENGTH_LONG).show();
            }//END } else {//if(resultCode == this.RESULT_OK) {
        }//if (requestCode == REQUEST_CODE_PICK_FILE) {

        if (requestCode == REQUEST_CODE_Decrypt_FILE) {
            if (resultCode == RESULT_OK) {
                String newFile = data.getStringExtra(
                        FileBrowserActivity.returnFileParameter);
                String newFileName = data.getStringExtra(
                        FileBrowserActivity.returnFileName);
                Toast.makeText(
                        this,
                        "Received FILE path from file browser:\n" + newFile,
                        Toast.LENGTH_SHORT).show();
                SecretKey yourKey;
                try {

                    yourKey = MainAESActivity.generateKey(password.getText().toString().toCharArray(), salt);
                    Log.d("Encryption", "Decrypt: " + yourKey.toString());

                    MainAESActivity.saveFile(
                            MainAESActivity.decodeFile(yourKey, MainAESActivity.readFile(newFile)), newFileName, false);
                    Toast.makeText(
                            this,
                            "Decription finished",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(
                            this,
                            "Error in operation",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            } else {//if(resultCode == this.RESULT_OK) {
                Toast.makeText(
                        this,
                        "Received NO result from file browser",
                        Toast.LENGTH_LONG).show();
            }//END } else {//if(resultCode == this.RESULT_OK) {
        }//if (requestCode == REQUEST_CODE_PICK_FILE) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}