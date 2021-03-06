package pranav.apps.amazing.hppoliceassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Entry_veh extends Fragment {
    Firebase mRootRef;
    EditText veh,phone,description,place,naka;
    Button submit_det;
    ImageButton upload;
    String v,ph,des,place_n,naka_n,path;
    private StorageReference mStorage,filepath;
    private static final int GALLERY_INTENT =2;
    private ProgressDialog progressDialog,progressDialog1;
    Uri uri=null,downloadUrl=null;
    String download_url_string="";
    private int ACTION_IMAGE_CAPTURE_ACTIVITY =1888;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");


        View  view=  getActivity().getLayoutInflater().inflate(R.layout.entry,container,false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog1 = new ProgressDialog(getActivity());
        veh=(EditText)view.findViewById(R.id.vehicle_num);
        phone=(EditText)view.findViewById(R.id.phone_num);
        description=(EditText)view.findViewById(R.id.description);
        place=(EditText)view.findViewById(R.id.place);
        naka=(EditText)view.findViewById(R.id.naka);
        upload=(ImageButton)view.findViewById(R.id.upload);
        submit_det=(Button)view.findViewById(R.id.make_entry);
        mStorage = FirebaseStorage.getInstance().getReference();
        submit_det.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
                //nak.setText("Done");                                 //function to generate UUID
                /*
                UUID idOne = UUID.randomUUID();
                UUID idTwo = UUID.randomUUID();
                UUID idThree = UUID.randomUUID();
                UUID idFour = UUID.randomUUID();

                String time = idOne.toString().replace("-", "");
                String time2 = idTwo.toString().replace("-", "");
                String time3 = idThree.toString().replace("-", "");
                String time4 = idFour.toString().replace("-", "");

                StringBuffer data = new StringBuffer();
                data.append(time);
                data.append(time2);
                data.append(time3);
                data.append(time4);

                SecureRandom random = new SecureRandom();
                int beginIndex = random.nextInt(100);       //Begin index + length of your string < data length
                int endIndex = beginIndex + 10;            //Length of string which you want
                String ID = data.substring(beginIndex, endIndex);
                */
                //Firebase idChild = mRootRef.push();           //unique ID generated by server firebase
            }
        });
        final Fragment fragment = this;
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               // intent.setType("image/*");
                startActivityForResult(intent,ACTION_IMAGE_CAPTURE_ACTIVITY);
            }
        });
        return  view;
    }

    private void startPosting() {
        Firebase idChild = mRootRef.push();
        v= veh.getText().toString();
        ph=phone.getText().toString();
        des=description.getText().toString();
        place_n=place.getText().toString();
        naka_n=naka.getText().toString();
        final Firebase off,oth,sec,veh_num,naka,image;
        off=idChild.child("VehicleNumber");
        oth=idChild.child("PhoneNumber");
        sec=idChild.child("Description");
        veh_num=idChild.child("Place");
        naka=idChild.child("Naka");
        image = idChild.child("Image");
        if(uri == null){
            Toast.makeText(getActivity(),"No Photo Selected, uploading data...",Toast.LENGTH_LONG).show();
            download_url_string ="Photo not available";
            off.setValue(v);
            oth.setValue(ph);
            sec.setValue(des);
            veh_num.setValue(place_n);
            naka.setValue(naka_n);
            image.setValue(download_url_string);
            Toast.makeText(getActivity(),"Upload Done ",Toast.LENGTH_SHORT).show();
        }
        if(uri!=null) {
            progressDialog1.setMessage("Uploading Image and data....");
            progressDialog1.show();
            filepath = mStorage.child("PhotosVehicleEntry").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Upload Done !", Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }
                    off.setValue(v);
                    oth.setValue(ph);
                    sec.setValue(des);
                    veh_num.setValue(place_n);
                    naka.setValue(naka_n);
                    image.setValue(download_url_string);
                    progressDialog1.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Upload Failed !", Toast.LENGTH_LONG).show();
                    progressDialog1.dismiss();
                }
            });
        }
        progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==ACTION_IMAGE_CAPTURE_ACTIVITY && resultCode==RESULT_OK){
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // convert byte array to Bitmap

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                    byteArray.length);

            upload.setImageBitmap(bitmap);
            /*
            uri = getImageUri(getActivity(),bitmap);
            upload.setImageURI(uri);
            */
            /*
           uri =data.getData();
            upload.setBackgroundColor(0);
            upload.setImageURI(uri);
            */

        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
