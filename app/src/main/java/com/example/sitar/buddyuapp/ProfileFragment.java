package com.example.sitar.buddyuapp;

//imports
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.InputStream;
import static android.app.Activity.RESULT_OK;
import static com.example.sitar.buddyuapp.R.layout.fragment_profile;



public class ProfileFragment extends android.app.Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView username;
    private ImageView profilePicture;
    private Button uploadImage;

    private static final int GALLERY_INTENT = 2;
    private FirebaseAuth firebaseAuth;
    private StorageReference mstorage;

    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public ProfileFragment()
    {

    }


    public static ProfileFragment newInstance(String param1, String param2)
    {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            String UID =firebaseAuth.getCurrentUser().getUid();
            StorageReference filepath = mstorage.child("Users/"+UID+"/ProfilePic");
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {

                    Toast.makeText(getActivity(), "Upload Done.", Toast.LENGTH_LONG).show();
                    loadProfilePicture();
                }
            });
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try
            {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);
        }
    }



    public void loadProfilePicture()
    {
        String UID =firebaseAuth.getCurrentUser().getUid();
        mstorage.child("Users/"+UID+"/ProfilePic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                new DownloadImageTask(profilePicture).execute(uri.toString());

            }
        });


    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        firebaseAuth = firebaseAuth.getInstance();
        username = (TextView) view.findViewById(R.id.username);
        username.setText(firebaseAuth.getCurrentUser().getEmail());
        mstorage = FirebaseStorage.getInstance().getReference();
        uploadImage = (Button) view.findViewById(R.id.upload_image);
        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);

        loadProfilePicture();

        uploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        view.findViewById(R.id.yourbuddiesbtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().beginTransaction().replace(R.id.content_main, new YourBuddiesFragment()).commit();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
