package com.example.sitar.buddyuapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YourBuddiesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YourBuddiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
class Buddy
{

    //String code = null;
    String name = null;
    boolean selected = false;
    String uid = null;

    public Buddy(String name, boolean selected, String uid)
    {
        super();
        //this.code = code;
        this.name = name;
        this.selected = selected;
        this.uid = uid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUID()
    {
        return uid;
    }

    public void setUID(String uid)
    {
        this.uid = uid;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }


}
public class YourBuddiesFragment extends android.app.Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    YourBuddiesFragment.MyCustomAdapter dataAdapter = null;

    private FirebaseAuth firebaseAuth;
    private StorageReference mstorage;
    private DatabaseReference mybuddies;
    private ListView listView;


    private OnFragmentInteractionListener mListener;

    public YourBuddiesFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YourBuddiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YourBuddiesFragment newInstance(String param1, String param2)
    {
        YourBuddiesFragment fragment = new YourBuddiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

    private void displayListView() {

        //Array list of buddies
        final ArrayList<Buddy> buddiesList = new ArrayList<Buddy>();
        firebaseAuth = firebaseAuth.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();
        final String UID = firebaseAuth.getCurrentUser().getUid();
        StorageReference filepath = mstorage.child("Users/" + UID);
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users2");
        mybuddies = FirebaseDatabase.getInstance().getReference("mybuddies/" + UID);
        Buddy buddy;

        //create an ArrayAdapter from the String Array
        dataAdapter = new MyCustomAdapter(getActivity(),
                R.layout.pick_buddies, buddiesList);

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        users.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot u : dataSnapshot.getChildren())
                {
                    if (UID.equals(u.getKey()))
                        continue;
                    Buddy buddy = new Buddy(u.child("email").getValue(String.class), false, u.getKey());
                    buddiesList.add(buddy);
                }
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });

        mybuddies.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (int x = 0; x < buddiesList.size(); x++)
                {
                    buddiesList.get(x).setSelected(false);
                }
                for (DataSnapshot u : dataSnapshot.getChildren())
                {
                    for (int y = 0; y < buddiesList.size(); y++)
                    {

                        if (u.getValue().equals(buddiesList.get(y).getUID()))
                        {
                            buddiesList.get(y).setSelected(true);
                        }
                    }
                }
                dataAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // When clicked, show a toast with the TextView text
                Buddy buddy = (Buddy) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + buddy.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        listView = (ListView) view.findViewById(R.id.yourbuddies);

        EditText buddySearch=(EditText)view.findViewById(R.id.searchBuddies);
        buddySearch.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
            {
                // When user changed the Text
                dataAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {

            }

            @Override
            public void afterTextChanged(Editable arg0)
            {

            }
        });
        displayListView();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_buddies, container, false);
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
        }
        else
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
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private class MyCustomAdapter extends ArrayAdapter<Buddy> implements Filterable
    {

        private ArrayList<Buddy> buddiesList;
        private ArrayList<Buddy> buddiesListCopy;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Buddy> buddiesList) {
            super(context, textViewResourceId, buddiesList);
            this.buddiesList = buddiesList;//new ArrayList<Buddy>();
            this.buddiesListCopy=buddiesList;
            //this.buddiesList.addAll(buddiesList);
        }

        private class ViewHolder {
            //TextView code;
            CheckBox name;
            Button buddiesSchedule;
        }

        public int getCount()
        {
            return buddiesList.size();
        }

        @Override
        public Filter getFilter()
        {

            Filter filter = new Filter()
            {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results)
                {

                    buddiesList = (ArrayList<Buddy>) results.values;
                    notifyDataSetChanged();

                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint)
                {

                    FilterResults results = new FilterResults();
                    ArrayList<Buddy> filteredArrayBuddies = new ArrayList<Buddy>();

                    // perform your search here using the searchConstraint String.
                    for (int x = 0; x < buddiesListCopy.size(); x++)
                    {
                        if (buddiesListCopy.get(x).getName().toUpperCase().contains(constraint.toString().toUpperCase()) || constraint.equals(""))
                        {
                            filteredArrayBuddies.add(buddiesListCopy.get(x));
                        }
                    }

                    results.count = filteredArrayBuddies.size();
                    results.values = filteredArrayBuddies;

                    return results;
                }
            };

            return filter;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            YourBuddiesFragment.MyCustomAdapter.ViewHolder holder = null;

            if (convertView == null)
            {
               LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
               convertView = vi.inflate(R.layout.pick_buddies, null);

                holder = new YourBuddiesFragment.MyCustomAdapter.ViewHolder();
                final YourBuddiesFragment.MyCustomAdapter.ViewHolder vHolder=holder;
                holder.buddiesSchedule=(Button) convertView.findViewById(R.id.buddiesSchedule);

                holder.buddiesSchedule.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Bundle args=new Bundle();
                        Buddy buddy=(Buddy)v.getTag();
                        args.putString("UID", buddy.getUID());
                        args.putString("Name",buddy.getName());
                        ScheduleFragment schFragment=new ScheduleFragment();
                        schFragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.content_main, schFragment).commit();
                    }
                });

                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        CheckBox cb = (CheckBox) v;
                        Buddy buddy = (Buddy) cb.getTag();
                        if (buddy.isSelected() != cb.isChecked())
                            buddy.setSelected(cb.isChecked());
                        vHolder.buddiesSchedule.setVisibility(cb.isChecked()?View.VISIBLE:View.INVISIBLE);
                        if (cb.isChecked())
                        {
                            Toast.makeText(getApplicationContext(),
                                    "You added " + cb.getText() +
                                            " as a buddy",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),
                                    "You removed " + cb.getText() +
                                            " as a buddy",
                                    Toast.LENGTH_SHORT).show();
                        }

                        mybuddies.removeValue();
                        for (int x = 0; x < buddiesListCopy.size(); x++)
                        {
                            Buddy b = buddiesListCopy.get(x);
                            if (b.isSelected())
                            {
                                DatabaseReference r = mybuddies.push();
                                r.setValue(b.getUID());
                            }
                        }
                    }
                });
            }
            else
            {
                holder = (YourBuddiesFragment.MyCustomAdapter.ViewHolder) convertView.getTag();
            }


            Buddy buddy = buddiesList.get(position);

            holder.name.setText(buddy.getName());
            holder.name.setChecked(buddy.isSelected());
            holder.name.setTag(buddy);
            holder.buddiesSchedule.setTag(buddy);
            holder.buddiesSchedule.setVisibility(buddy.isSelected()?View.VISIBLE:View.INVISIBLE);

            return convertView;

        }

    }

}
