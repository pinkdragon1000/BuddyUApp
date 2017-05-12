package com.example.sitar.buddyuapp;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

class Buddy {

    //String code = null;
    String name = null;
    boolean selected = false;

    public Buddy(String name, boolean selected) {
        super();
        //this.code = code;
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}

public class AddBuddies extends AppCompatActivity
{

    MyCustomAdapter dataAdapter = null;

    private FirebaseAuth firebaseAuth;
    private StorageReference mstorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buddies);

        //Generate list View from ArrayList
        displayListView();

        checkButtonClick();

    }

    private void displayListView() {

        //Array list of countries
        final ArrayList<Buddy> buddiesList = new ArrayList<Buddy>();
        firebaseAuth=firebaseAuth.getInstance();
        mstorage= FirebaseStorage.getInstance().getReference();
        final String UID =firebaseAuth.getCurrentUser().getUid();
        StorageReference filepath=mstorage.child("Users/"+UID);
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users2");
        //String Email=firebaseAuth.getCurrentUser().getEmail();
        //Buddy buddy = new Buddy(filepath,Email,false);
        //buddiesList.add(buddy);
        Buddy buddy;
        buddy= new Buddy("Albania",true);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.pick_buddies, buddiesList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot u: dataSnapshot.getChildren())
                {
                    Log.d("blah", UID + " " + u.toString());
                    if(UID.equals(u.getKey()))
                        continue;
                    Buddy buddy = new Buddy(u.child("name").getValue(String.class),false);
                    buddiesList.add(buddy);
                }
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Buddy buddy = (Buddy) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + buddy.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Buddy> {

        private ArrayList<Buddy> buddiesList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Buddy> buddiesList) {
            super(context, textViewResourceId, buddiesList);
            this.buddiesList = buddiesList;//new ArrayList<Buddy>();
            //this.buddiesList.addAll(buddiesList);
        }

        private class ViewHolder {
            //TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.pick_buddies, null);

                holder = new ViewHolder();
               // holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Buddy buddy = (Buddy) cb.getTag();
                        if(cb.isChecked())
                        {
                            Toast.makeText(getApplicationContext(),
                                    "You selected " + cb.getText() +
                                            " to be added as a buddy ",
                                    Toast.LENGTH_SHORT).show();
                            buddy.setSelected(cb.isChecked());
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),
                                    "You unselected " + cb.getText() +
                                            " to be added as a buddy ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            //if (position > 0) {
            Buddy buddy = buddiesList.get(position);
            //holder.code.setText(" (" + buddy.getCode() + ")");
            holder.name.setText(buddy.getName());
            holder.name.setChecked(buddy.isSelected());
            holder.name.setTag(buddy);
            //}
            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Buddy> buddiesList = dataAdapter.buddiesList;
                for(int i=0;i<buddiesList.size();i++){
                    Buddy buddy = buddiesList.get(i);
                    if(buddy.isSelected()){
                        responseText.append("\n" + buddy.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }

}
