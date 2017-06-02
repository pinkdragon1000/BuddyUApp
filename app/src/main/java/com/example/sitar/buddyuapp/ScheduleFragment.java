package com.example.sitar.buddyuapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.sitar.buddyuapp.R.id.courses;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
class Item {
    Item(String day, int time, String description) {
        this.day = day;
        this.description = description;
        this.time=time;
    }

    private String day;
    private String description;
    private int time;

    public String getDay()
    {
        return day;
    }
    public String getDescription()
    {
        return description;
    }
    public int getTime()
    {
        return time;
    }


}
public class ScheduleFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView mySchedule;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myCourses;
    private DatabaseReference catalog;
    private ArrayList<Item> items;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private String itemsForDay(String day)
    {
        String temp="";
        for(int x=0;x<items.size();x++)
        {
            if (day.equals("TBD") && items.get(x).getDay().contains("TBD"))
            {
                temp +=items.get(x).getDescription()+"<br> <br>";
            }
            else if(!day.equals("TBD") && items.get(x).getDay().contains(day)&& !items.get(x).getDay().contains("TBD"))
            {
                temp +=items.get(x).getDescription()+"<br> <br>";
            }

        }
        return temp;
    }

    private void updateItems()
    {
        //Sorts items in order of time
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getTime() < rhs.getTime() ? -1 : (lhs.getTime() > rhs.getTime() ) ? 1 : 0;
            }
        });

        String data="";
        data += "<p><b><font color=\"#6ac6aF\"> Day TBD </font></b> <br>";
        data += itemsForDay("TBD");
        data += "<p><b><font color=\"#6ac6aF\"> Monday </font> </b> <br>";
        data += itemsForDay("M");
        data +="</p>";
        data += "<p><b><font color=\"#6ac6aF\"> Tuesday </font> </b> <br>";
        data += itemsForDay("T");
        data +="</p>";
        data += "<p><b><font color=\"#6ac6aF\"> Wednesday </font> </b> <br>";
        data += itemsForDay("W");
        data +="</p>";
        data += "<p><b><font color=\"#6ac6aF\"> Thursday </font> </b> <br>";
        data += itemsForDay("R");
        data +="</p>";
        data += "<p><b><font color=\"#6ac6aF\"> Friday </font> </b> <br>";
        data += itemsForDay("F");
        data +="</p>";


        mySchedule.setText(Html.fromHtml(data));

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        items=new ArrayList<Item>();
        firebaseAuth = firebaseAuth.getInstance();
        TextView userSch=(TextView)view.findViewById(R.id.userSchedule);
        mySchedule= (TextView) view.findViewById(R.id.my_schedule);
        String tempUID = firebaseAuth.getCurrentUser().getUid();
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            String tempUID2 = bundle.getString("UID","");
            if (!tempUID2.equals(""))
            {
                tempUID = tempUID2;
                userSch.setText("Schedule for "+ bundle.getString("Name","???"));

            }
        }

        final String UID = tempUID;
        myCourses= FirebaseDatabase.getInstance().getReference("mycourses/"+UID);
        catalog=FirebaseDatabase.getInstance().getReference("Catalog");

        ((TextView)view.findViewById(R.id.my_schedule))
                .setMovementMethod(new ScrollingMovementMethod());
        myCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                for (int x = 0; x < courses.size(); x++) {
                    courses.get(x).setSelected(false);
                }
                */
                //String s="";
                mySchedule.setText("");
                items.clear();
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    Log.d("blah",u.getValue().toString());

                    final String currentCRN=u.getValue().toString();

                    catalog.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot c_item : dataSnapshot.getChildren()) {
                                String c_name = c_item.child("college").getValue(String.class);


                                for (DataSnapshot s_item : c_item.child("subjects").getChildren()) {
                                    String s_name = s_item.child("subject").getValue(String.class);

                                    for (DataSnapshot co_item : s_item.child("courses").getChildren()) {
                                        if (co_item.child("crn").getValue(String.class).equals(currentCRN)) {
                                            String co_name = co_item.child("subject_code").getValue(String.class) + " " +
                                                    co_item.child("course_no").getValue(String.class) + " - " +
                                                    co_item.child("course_title").getValue(String.class)+ " <br> Sec #: " +
                                                    co_item.child("sec").getValue(String.class) + "<br> Times:  " +
                                                    co_item.child("days").getValue(String.class) + " " +
                                                    co_item.child("time").getValue(String.class) +
                                                    "<br> Instructor: " + co_item.child("instructor").getValue(String.class)+
                                                    "<br> CRN #: "+ co_item.child("crn").getValue(String.class);

                                            Log.d("blah4",co_name);
                                            int iHour=0;

                                            String sTime=co_item.child("time").getValue(String.class);
                                            if(sTime.substring(2,3).equals(":"))
                                            {
                                                //Parse out the time and convert it to an integer
                                                String hour=sTime.substring(0,2);
                                                String minute=sTime.substring(3,5);
                                                String timeDay=sTime.substring(6,8);
                                                iHour=Integer.parseInt(hour);
                                                int iMinute=Integer.parseInt(minute);
                                                if(timeDay.equals("pm"))
                                                {
                                                    //Changes the hour to military time adding 12 Ex. 6pm==1800 hrs
                                                    iHour+=12;
                                                }
                                                iHour=iHour*100+iMinute;
                                            }
                                            //Adds classes to schedule
                                            items.add(new Item(co_item.child("days").getValue(String.class),iHour, co_name));




                                        }
                                    }
                                }
                            }
                            updateItems();
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });



                }
                //mySchedule.setText(s);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
