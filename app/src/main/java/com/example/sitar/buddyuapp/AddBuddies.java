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

class Buddy2 {

    //String code = null;
    String name = null;
    boolean selected = false;
    String uid = null;

    public Buddy2(String name, boolean selected, String uid) {
        super();
        //this.code = code;
        this.name = name;
        this.selected = selected;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}


public class AddBuddies extends AppCompatActivity {

    MyCustomAdapter2 dataAdapter = null;

    private FirebaseAuth firebaseAuth;
    private StorageReference mstorage;
    private DatabaseReference mybuddies;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buddies);

        //Generate list View from ArrayList
        displayListView();

        //checkButtonClick();
    }

    private void displayListView() {

        //Array list of countries
        final ArrayList<Buddy2> buddiesList = new ArrayList<Buddy2>();
        firebaseAuth = firebaseAuth.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();
        final String UID = firebaseAuth.getCurrentUser().getUid();
        StorageReference filepath = mstorage.child("Users/" + UID);
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users2");
        mybuddies = FirebaseDatabase.getInstance().getReference("mybuddies/" + UID);
        //String Email=firebaseAuth.getCurrentUser().getEmail();
        //Buddy buddy = new Buddy(filepath,Email,false);
        //buddiesList.add(buddy);
        Buddy2 buddy;
        //buddy= new Buddy("Albania",true);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter2(this,
                R.layout.pick_buddies, buddiesList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    Log.d("blah", UID + " " + u.toString());
                    if (UID.equals(u.getKey()))
                        continue;
                    Buddy2 buddy = new Buddy2(u.child("name").getValue(String.class), false, u.getKey());
                    buddiesList.add(buddy);
                }
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mybuddies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int x = 0; x < buddiesList.size(); x++) {
                    buddiesList.get(x).setSelected(false);
                }
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    for (int y = 0; y < buddiesList.size(); y++) {

                        if (u.getValue().equals(buddiesList.get(y).getUID())) {
                            buddiesList.get(y).setSelected(true);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Buddy2 buddy = (Buddy2) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + buddy.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter2 extends ArrayAdapter<Buddy2> {

        private ArrayList<Buddy2> buddiesList;

        public MyCustomAdapter2(Context context, int textViewResourceId,
                                ArrayList<Buddy2> buddiesList) {
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
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.pick_buddies, null);

                holder = new ViewHolder();
                // holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Buddy2 buddy = (Buddy2) cb.getTag();
                        buddy.setSelected(cb.isChecked());
                        if (cb.isChecked()) {
                            Toast.makeText(getApplicationContext(),
                                    "You selected " + cb.getText() +
                                            " to be added as a buddy ",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "You unselected " + cb.getText() +
                                            " to be added as a buddy ",
                                    Toast.LENGTH_SHORT).show();
                        }

                        mybuddies.removeValue();
                        for (int x = 0; x < buddiesList.size(); x++) {
                            Buddy2 b = buddiesList.get(x);
                            if (b.isSelected()) {
                                DatabaseReference r = mybuddies.push();
                                r.setValue(b.getUID());
                            }
                        }

                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //if (position > 0) {
            Buddy2 buddy = buddiesList.get(position);
            //holder.code.setText(" (" + buddy.getCode() + ")");
            holder.name.setText(buddy.getName());
            holder.name.setChecked(buddy.isSelected());
            holder.name.setTag(buddy);
            //}
            return convertView;

        }

    }
    //private void checkButtonClick() {


      /*  Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                ArrayList<Buddy2> buddiesList = dataAdapter.buddiesList;
                for(int i=0;i<buddiesList.size();i++){
                    Buddy2 buddy = buddiesList.get(i);
                    if(buddy.isSelected()){
                        responseText.append("\n" + buddy.getName());
                    }
                }
                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
            }
        });
        */

    //}

}