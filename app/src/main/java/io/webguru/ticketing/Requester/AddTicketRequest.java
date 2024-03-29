package io.webguru.ticketing.Requester;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.webguru.ticketing.Agent.AgentMainActivity;
import io.webguru.ticketing.Global.GlobalFunctions;
import io.webguru.ticketing.POJO.Analytics;
import io.webguru.ticketing.POJO.FieldAgentData;
import io.webguru.ticketing.POJO.RequesterData;
import io.webguru.ticketing.POJO.Ticket;
import io.webguru.ticketing.POJO.UserInfo;
import io.webguru.ticketing.R;
import io.webguru.ticketing.Welcome.SplashScreen;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static io.webguru.ticketing.Global.GlobalConstant.FILE_STORAGE_PATH;

public class AddTicketRequest extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1991;
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1992;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.text_site)
    TextInputEditText editSite;
    @Bind(R.id.text_description)
    TextInputEditText editDescription;
    @Bind(R.id.spinner_location)
    Spinner spinnerLocation;
    @Bind(R.id.priority_seekbar)
    SeekBar prioritySeekbar;
    @Bind(R.id.value_priority)
    TextView textViewPriorityValue;
    @Bind(R.id.text_shop)
    TextInputEditText editShop;
    @Bind(R.id.photo_badge)
    ImageView photoBadge;
    @Bind(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    boolean isAgent = false;
    FirebaseStorage storage;
    StorageReference storageRef, photoRef;
    Analytics analytics;
    ProgressDialog progressDialog;
    private String TAG = "ADDTICKETREQUEST";
    private String mCurrentPhotoPath;
    private boolean isSignatureTaken = false;
    private FieldAgentData fieldAgentData;
    private RequesterData requesterData;
    private Ticket ticket;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket_request);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //TODO Handle null case
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isAgent = bundle.getBoolean("isAgent");
        }
        if (isAgent && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //Receving UserInfo Object from Intent
        userInfo = new UserInfo(true);//(UserInfo) bundle.get("UserInfo");

        fieldAgentData = new FieldAgentData();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(FILE_STORAGE_PATH);
        prioritySeekbar.setProgress(30);
        prioritySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                if (value >= 0 && value < 30) {
                    textViewPriorityValue.setText("LOW");
                    textViewPriorityValue.setTextColor(Color.BLACK);
                }
                if (value >= 30 && value < 70) {
                    textViewPriorityValue.setText("MEDIUM");
                    textViewPriorityValue.setTextColor(Color.rgb(255, 165, 0)); // Orange
                }
                if (value >= 70 && value <= 100) {
                    textViewPriorityValue.setText("HIGH");
                    textViewPriorityValue.setTextColor(Color.RED);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick(R.id.location_previous)
    public void locationPrevious() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        viewFlipper.showPrevious();
    }

    @OnClick(R.id.location_next)
    public void locationNext() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        viewFlipper.showNext();
    }

    @OnClick(R.id.description_previous)
    public void descriptionPrevious() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        viewFlipper.showPrevious();
    }

    @OnClick(R.id.click_photo)
    public void clickPhoto() {
        Log.d(TAG, "Inside click_photo");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1999);
        } else {
            takePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1999: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    takePhoto();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @OnClick(R.id.request_ticket_1)
    public void requestTicket1() {
        final String description = editDescription.getText().toString();
        if ("".equals(description)) {
            editDescription.setError("Invalid Description");
            return;
        }
        final String location = spinnerLocation.getSelectedItem().toString();
        String progressMessage = "";
        if (mCurrentPhotoPath == null || "".equals(mCurrentPhotoPath)) {
            progressMessage = "Updating information of ticket..Please wait";
        } else {
            progressMessage = "Uploading Image...Please wait";
        }
        final String priority = textViewPriorityValue.getText().toString();
        progressDialog = new ProgressDialog(this);
        progressDialog.show(this, "Creating Ticket", progressMessage, false, false);
        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("analytics").child(GlobalFunctions.getTodaysDateAsFireBaseKey());
        mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null) {
                    analytics = snapshot.getValue(Analytics.class);
                } else {
                    System.out.println("No data exists for analyitcs on " + GlobalFunctions.getTodaysDateFormatted());
                    analytics = new Analytics(true);
                }
                analytics.setIncomingCount(analytics.getIncomingCount() + 1);
                if ("HIGH".equals(priority)) {
                    analytics.setHighCount(analytics.getHighCount() + 1);
                }
                if ("MEDIUM".equals(priority)) {
                    analytics.setMediumCount(analytics.getMediumCount() + 1);
                }
                if ("LOW".equals(priority)) {
                    analytics.setLowCount(analytics.getLowCount() + 1);
                }
                UploadTicekt(description, priority, location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                showFailureMessage();
            }
        });

    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        Log.d(TAG, "Inside takePictureIntent.resolveActivity(getPackageManager()) != null >> " + (takePictureIntent.resolveActivity(getPackageManager()) != null));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "io.webguru.ticketing.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
//            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("IMAGEPATH", "path of image mCurrentPhotoPath >>> " + mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoBadge.setVisibility(View.VISIBLE);
//            setPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 350;//imageViewPhoto.getWidth();
        int targetH = 350;//imageViewPhoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        imageViewPhoto.setImageBitmap(bitmap);
//        imageViewPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.fromFile(new File(mCurrentPhotoPath)), "image/*");
//                startActivity(intent);
//            }
//        });
    }

    private void UploadTicekt(String description, final String priority, String location) {
        String ticketNumber = GlobalFunctions.getCurrentDateInMilliseconds();
        requesterData = new RequesterData(description, priority, location, "", "", userInfo);
        String searchKeyword = requesterData.getIssue().toLowerCase();
        if (mCurrentPhotoPath == null || "".equals(mCurrentPhotoPath)) {
            //TODO HARDCODED AGENT ASSIGNED
            //Ticket Object without image
            ticket = new Ticket(ticketNumber, "Incoming", null, Integer.parseInt(userInfo.getUserid()),
                    1, 0, 0, GlobalFunctions.getCurrentDateTime(), requesterData, "1_Incoming", searchKeyword);
            updateTicketInfo();
        } else {
            //TODO HARDCODED AGENT ASSIGNED
            //Ticket Object with image
            ticket = new Ticket(ticketNumber, "Incoming", ticketNumber + ".jpg", Integer.parseInt(userInfo.getUserid()),
                    1, 0, 0, GlobalFunctions.getCurrentDateTime(), requesterData, "1_Incoming", searchKeyword);
            InputStream stream = null;
            try {
                stream = new FileInputStream(new File(mCurrentPhotoPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            photoRef = storageRef.child("issue_image/" + ticketNumber + ".jpg");
            UploadTask uploadTask = photoRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Log.d(TAG, "FAIL TO UPLOAD IMAGE");
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Success TO UPLOAD IMAGE");
                    progressDialog.setMessage("Updating information of ticket..Please wait");
                    updateTicketInfo();

                }
            }).addOnFailureListener(AddTicketRequest.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    showFailureMessage();
                }
            });
        }

    }

    private void updateTicketInfo() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/ticketing/" + ticket.getTicketNumber(), ticket.toMap());
        mDatabase.updateChildren(childUpdates).addOnCompleteListener(AddTicketRequest.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> childUpdates1 = new HashMap<>();
                childUpdates1.put("/analytics/" + GlobalFunctions.getTodaysDateAsFireBaseKey(), analytics);
                mDatabase1.updateChildren(childUpdates1).addOnCompleteListener(AddTicketRequest.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Intent intent;
                        if (isAgent) {
                            intent = new Intent(AddTicketRequest.this, AgentMainActivity.class);
                        } else {
                            intent = new Intent(AddTicketRequest.this, SplashScreen.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(AddTicketRequest.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showFailureMessage();
                    }
                });
            }
        }).addOnFailureListener(AddTicketRequest.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showFailureMessage();
            }
        });
    }

    private void showFailureMessage() {
        GlobalFunctions.showToast(AddTicketRequest.this, "Some problem occured while Creating Ticket. Please check Internet Connection and try again.", Toast.LENGTH_LONG);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
