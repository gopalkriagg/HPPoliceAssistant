package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import java.security.SecureRandom;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Challan extends Fragment {
    Firebase mRootRef;
    CheckBox helmet,rc,insurance,license,rash_drive,mobile,number_plate,horn,seat_belt,triple_riding,
            idle_parking,restricted_park;
    EditText other,offence_section,veh_number,place_name,naka_name;
    Button submit;
    ImageButton upload_photo;
    String details,section,no_veh,place_n,naka_n;
    TextView nak;
    Uri uri=null,downloadUrl=null;
    String download_url_string="";
    String crime="";
    private StorageReference mStorage,filepath;
    private static final int GALLERY_INTENT =2;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;
    private static final int CAMERA_REQUEST = 1888;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootRef = new Firebase("https://hppoliceassistant.firebaseio.com/challan");

        View  view=  getActivity().getLayoutInflater().inflate(R.layout.challan,container,false);
        helmet=(CheckBox)view.findViewById(R.id.helmet);
        rc=(CheckBox)view.findViewById(R.id.rc);
        insurance=(CheckBox)view.findViewById(R.id.insurance);
        license=(CheckBox)view.findViewById(R.id.license);
        rash_drive=(CheckBox)view.findViewById(R.id.rash_drive);
        mobile=(CheckBox)view.findViewById(R.id.mobile);
        number_plate=(CheckBox)view.findViewById(R.id.number_plate);
        seat_belt=(CheckBox)view.findViewById(R.id.seat_belt);
        horn=(CheckBox)view.findViewById(R.id.pressure_horn);
        triple_riding=(CheckBox)view.findViewById(R.id.triple_riding);
        idle_parking=(CheckBox)view.findViewById(R.id.idle_parking);
        restricted_park=(CheckBox)view.findViewById(R.id.restricted);
        other=(EditText)view.findViewById(R.id.other_offence);
        offence_section=(EditText)view.findViewById(R.id.offence_section);
        veh_number=(EditText)view.findViewById(R.id.vehicle_number);
        place_name=(EditText)view.findViewById(R.id.place_name);
        naka_name=(EditText)view.findViewById(R.id.naka_name);
        upload_photo=(ImageButton) view.findViewById(R.id.upload_photo);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog1 = new ProgressDialog(getActivity());
        nak=(TextView)view.findViewById(R.id.nak_name);
        mStorage = FirebaseStorage.getInstance().getReference();
        submit=(Button)view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helmet.isChecked()){
                    crime.concat("w/o Helmet,");
                }
                if(insurance.isChecked()){
                    crime.concat("w/o Insurance,");
                }
                if(license.isChecked()){
                    crime.concat("w/o License,");
                }
                if(rc.isChecked()){
                    crime.concat("w/o RC,");
                }
                if(rash_drive.isChecked()){
                    crime.concat("Rash Ans Negligent Driving,");
                }
                if(mobile.isChecked()){
                    crime.concat("Using Mobile during driving,");
                }
                if(number_plate.isChecked()){
                    crime.concat("w/o NumberPlate,");
                }
                if(horn.isChecked()){
                    crime.concat("Using Pressure Horn,");
                }
                if(seat_belt.isChecked()){
                    crime.concat("w/o SeatBelt,");
                }
                if(triple_riding.isChecked()){
                    crime.concat("Triple Riding,");
                }
                if(idle_parking.isChecked()){
                    crime.concat("Idle Parking,");
                }
                if(restricted_park.isChecked()){
                    crime.concat("Restricted Area Parking,");
                }
                startPosting();
                //nak.setText("Done");
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
                Firebase idChild = mRootRef.child(ID);
                */
            }
        });
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
                /*
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                */
            }
        });
        return  view;
    }
    public void startPosting(){
        Firebase idChild = mRootRef.push();
        details = other.getText().toString();
        section = offence_section.getText().toString();
        no_veh = veh_number.getText().toString();
        place_n = place_name.getText().toString();
        final Firebase off, oth, sec, veh_num, pl_name, naka, image,crim;
        off = idChild.child("OffenceDetails");
        oth = idChild.child("Others");
        crim = idChild.child("Crime");
        sec = idChild.child("OffenceSection");
        veh_num = idChild.child("VehicleNumber");
        pl_name = idChild.child("PlaceName");
        naka = idChild.child("NakaName");
        image = idChild.child("Image");
        if(uri == null){
            Toast.makeText(getActivity(),"No Photo Selected, uploading data...",Toast.LENGTH_LONG).show();
            download_url_string ="Photo not available";
            off.setValue(details);
            oth.setValue(details);
            sec.setValue(section);
            veh_num.setValue(no_veh);
            pl_name.setValue(place_n);
            naka.setValue(naka_n);
            crim.setValue(crime);
            image.setValue(download_url_string);
            Toast.makeText(getActivity(),"Upload Done ",Toast.LENGTH_SHORT).show();
        }
        if(uri!=null) {
            progressDialog1.setMessage("Uploading Image and data....");
            progressDialog1.show();
            filepath = mStorage.child("PhotosChallan").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Upload Done !", Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }
                    off.setValue(details);
                    oth.setValue(details);
                    sec.setValue(section);
                    veh_num.setValue(no_veh);
                    pl_name.setValue(place_n);
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
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            uri =data.getData();
            upload_photo.setBackgroundColor(0);
            upload_photo.setImageURI(uri);
        }
    }

}
