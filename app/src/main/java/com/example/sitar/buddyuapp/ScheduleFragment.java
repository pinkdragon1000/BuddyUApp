package com.example.sitar.buddyuapp;

//imports
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;




class Item
{
    //Constructor
    Item(String day, int time, String description, String crn)
    {
        this.day = day;
        this.description = description;
        this.time=time;
        this.crn=crn;
    }
    //Attributes for day, description, time, and crn
    private String day;
    private String description;
    private int time;
    private String crn;

    //Accessor methods
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
    public String getCRN()
    {
        return crn;
    }

}

public class ScheduleFragment extends android.app.Fragment
{
    //Attributes
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView mySchedule;
    private ImageView userPicture;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myCourses;
    private DatabaseReference catalog;
    private ArrayList<Item> items;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Required empty public constructor
    public ScheduleFragment()
    {

    }


    public static ScheduleFragment newInstance(String param1, String param2)
    {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //Goes through the array list called items and checks whether the day equals TBD, Monday, Tuesday, etc.
    private String itemsForDay(String day)
    {
        //String variable called temp
        String temp="";
        //Goes through the array list
        for(int x=0;x<items.size();x++)
        {
            //Checks to see whether the day equals TBD and that items contains TBD
            if (day.equals("TBD") && items.get(x).getDay().contains("TBD"))
            {
                //Adds on the item description to temp followed by a blank space
                temp +=items.get(x).getDescription()+"<br> <br>";
            }
            //Makes sure the day does not equal TBD and checks that items contains any other day of the week other than TBD
            else if(!day.equals("TBD") && items.get(x).getDay().contains(day)&& !items.get(x).getDay().contains("TBD"))
            {
                temp +=items.get(x).getDescription()+"<br> <br>";
            }
        }
        //Returns the temp variable with all class descriptions
        return temp;
    }
    //This method sorts the items or this case classes in order of time.
    private void updateItems()
    {
        //Sorts items in order of time
        Collections.sort(items, new Comparator<Item>()
        {
            @Override
            public int compare(Item lhs, Item rhs)
            {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getTime() < rhs.getTime() ? -1 : (lhs.getTime() > rhs.getTime() ) ? 1 : 0;
            }
        });

        //Creates a string called data equal to an empty string
        String data="";
        //Adds on to the string the "title" Day TBD in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Day TBD </font></b> <br>";
        //Inserts the items whose days under times are equal to TBD under the "title" Day TBD
        data += itemsForDay("TBD");
        //Adds on to the string the "title" Monday in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Monday </font> </b> <br>";
        //Inserts the items whose days under times are equal to M under the "title" Monday
        data += itemsForDay("M");
        data +="</p>";
        //Adds on to the string the "title" Tuesday in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Tuesday </font> </b> <br>";
        //Inserts the items whose days under times are equal to T under the "title" Day TBD
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        items=new ArrayList<Item>();
        firebaseAuth = firebaseAuth.getInstance();
        TextView userSch=(TextView)view.findViewById(R.id.userSchedule);
        mySchedule= (TextView) view.findViewById(R.id.my_schedule);
        userPicture = (ImageView) view.findViewById(R.id.user_picture);
        String tempUID = firebaseAuth.getCurrentUser().getUid();
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            String tempUID2 = bundle.getString("UID","");
            if (!tempUID2.equals(""))
            {
                tempUID = tempUID2;
                userSch.setText("Schedule for "+ "\n"+ bundle.getString("Name","???"));

            }
        }

        final String UID = tempUID;
        myCourses= FirebaseDatabase.getInstance().getReference("mycourses/"+UID);
        catalog=FirebaseDatabase.getInstance().getReference("Catalog");

        ((TextView)view.findViewById(R.id.my_schedule))
                .setMovementMethod(new ScrollingMovementMethod());
        myCourses.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mySchedule.setText("");
                items.clear();
                for (DataSnapshot u : dataSnapshot.getChildren())
                {
                    final String currentCRN=u.getValue().toString();

                    catalog.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot c_item : dataSnapshot.getChildren())
                            {
                                String c_name = c_item.child("college").getValue(String.class);


                                for (DataSnapshot s_item : c_item.child("subjects").getChildren())
                                {
                                    String s_name = s_item.child("subject").getValue(String.class);

                                    for (DataSnapshot co_item : s_item.child("courses").getChildren())
                                    {
                                        if (co_item.child("crn").getValue(String.class).equals(currentCRN))
                                        {
                                            String co_name = co_item.child("subject_code").getValue(String.class) + " " +
                                                    co_item.child("course_no").getValue(String.class) + " - " +
                                                    co_item.child("course_title").getValue(String.class)+ " <br> Sec #: " +
                                                    co_item.child("sec").getValue(String.class) + "<br> Times:  " +
                                                    co_item.child("days").getValue(String.class) + " " +
                                                    co_item.child("time").getValue(String.class) +
                                                    "<br> Instructor: " + co_item.child("instructor").getValue(String.class)+
                                                    "<br> CRN #: "+ co_item.child("crn").getValue(String.class);
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
                                            boolean alreadyThere=false;
                                            for (Item i: items)
                                            {
                                                if (i.getCRN().equals(currentCRN))
                                                {
                                                    alreadyThere=true;
                                                }
                                            }
                                            if(!alreadyThere)
                                            {
                                                items.add(new Item(co_item.child("days").getValue(String.class),iHour, co_name, currentCRN));
                                            }
                                        }
                                    }
                                }
                            }
                            updateItems();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                        }
                    });

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError)
            {
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

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}