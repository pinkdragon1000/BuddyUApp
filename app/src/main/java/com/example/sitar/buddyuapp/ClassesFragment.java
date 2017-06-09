package com.example.sitar.buddyuapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClassesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClassesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ClassesFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG="ClassesFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth;
    private ListView listView;
    private DatabaseReference mycourses;

    MyCustomAdapter2 dataAdapter = null;

    private OnFragmentInteractionListener mListener;

    public ClassesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassesFragment newInstance(String param1, String param2) {
        ClassesFragment fragment = new ClassesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        firebaseAuth = firebaseAuth.getInstance();
        final String UID =firebaseAuth.getCurrentUser().getUid();
        DatabaseReference catalog = FirebaseDatabase.getInstance().getReference("Catalog");
        mycourses=FirebaseDatabase.getInstance().getReference("mycourses/"+UID);
        listView = (ListView) view.findViewById(R.id.courses);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final ArrayList<Course> courses = new ArrayList();

        /*
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_multiple_choice, (ArrayList) courses) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 300;
                view.setLayoutParams(params);

                return view;
            }
        };
        */
        dataAdapter = new MyCustomAdapter2(getActivity(),
                R.layout.pick_courses, courses);


        listView.setAdapter(dataAdapter);

        EditText classSearch=(EditText)view.findViewById(R.id.searchClasses);
        classSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
               dataAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });

        catalog.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot c_item : dataSnapshot.getChildren()) {
                    String c_name = c_item.child("college").getValue(String.class);

                    for (DataSnapshot s_item : c_item.child("subjects").getChildren()) {
                        String s_name = s_item.child("subject").getValue(String.class);
                        courses.add(new Course("> " + c_name + " / " + s_name,false, null));

                        for (DataSnapshot co_item : s_item.child("courses").getChildren()) {
                            String co_name = co_item.child("subject_code").getValue(String.class) + " " +
                                    co_item.child("course_no").getValue(String.class) + " - " +
                                    co_item.child("course_title").getValue(String.class) + " \n Sec #: " +
                                    co_item.child("sec").getValue(String.class) + ", Times:  " +
                                    co_item.child("days").getValue(String.class) + " " +
                                    co_item.child("time").getValue(String.class) +
                                    ", Instructor: " + co_item.child("instructor").getValue(String.class);

                            courses.add(new Course(co_name,false,co_item.child("crn").getValue(String.class)));
                        }
                    }
                }
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mycourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int x = 0; x < courses.size(); x++) {
                     courses.get(x).setSelected(false);
                }
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    for (int y = 0; y < courses.size(); y++) {

                        if (u.getValue().equals(courses.get(y).getCRN())) {
                            courses.get(y).setSelected(true);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        final Activity thisActivity = this.getActivity();

     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item clicked " + position);
                Log.d(TAG, "Item " + courses.get(position));
                Log.d(TAG, "Item CRN "+((Course)courses.get(position)).getCRN());

                ((Course)courses.get(position)).setSelected(listView.isItemChecked(position));

                mycourses.removeValue();
                for (int x = 0; x < courses.size(); x++) {
                    Course b =(Course) courses.get(x);
                    if (b.isSelected()) {
                        DatabaseReference r = mycourses.push();
                        r.setValue(b.getCRN());
                    }
                }

            }
        });
        */

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
        return inflater.inflate(R.layout.fragment_classes, container, false);
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

    private class MyCustomAdapter2 extends ArrayAdapter<Course> implements Filterable {

        private ArrayList<Course> courses;
        private ArrayList<Course> coursesCopy;

        public MyCustomAdapter2(Context context, int textViewResourceId,
                               ArrayList<Course> courses) {
            super(context, textViewResourceId, courses);
            this.courses = courses;//new ArrayList<Buddy>();
            this.coursesCopy=courses;
            //this.buddiesList.addAll(buddiesList);
        }

        private class ViewHolder {
            //TextView code;
            CheckBox name;
            TextView section;
        }

        public int getCount()
        {
            return courses.size();
        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    courses = (ArrayList<Course>) results.values;
                    notifyDataSetChanged();

                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<Course> filteredArrayCourses = new ArrayList<Course>();

                    // perform your search here using the searchConstraint String.
                    for(int x=0;x<coursesCopy.size();x++)
                    {
                        if(coursesCopy.get(x).getName().toUpperCase().contains(constraint.toString().toUpperCase())||constraint.equals(""))
                        {
                            filteredArrayCourses.add(coursesCopy.get(x));
                        }
                    }


                    results.count = filteredArrayCourses.size();
                    results.values = filteredArrayCourses;
                    Log.e("VALUES", results.values.toString());

                    return results;
                }
            };

            return filter;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ClassesFragment.MyCustomAdapter2.ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.pick_courses, null);

                holder = new ClassesFragment.MyCustomAdapter2.ViewHolder();
                // holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);
                holder.section=(TextView) convertView.findViewById(R.id.textView1);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Course course = (Course) cb.getTag();
                        course.setSelected(cb.isChecked());
                        if (cb.isChecked()) {
                            Toast.makeText(getApplicationContext(),
                                    "You added " + cb.getText()+" to your schedule",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "You removed " + cb.getText()+" from your schedule" ,
                                    Toast.LENGTH_SHORT).show();
                        }

                        mycourses.removeValue();
                        for (int x = 0; x < coursesCopy.size(); x++) {
                            Course b = coursesCopy.get(x);
                            if (b.isSelected()) {
                                DatabaseReference r = mycourses.push();
                                r.setValue(b.getCRN());
                            }
                        }

                    }
                });
            } else {
                holder = (ClassesFragment.MyCustomAdapter2.ViewHolder) convertView.getTag();
            }

            if (position < courses.size()) {
            Course course = courses.get(position);
            //holder.code.setText(" (" + buddy.getCode() + ")");
            holder.name.setText(course.getName());
            holder.section.setText(course.getName());
            holder.name.setChecked(course.isSelected());
            holder.name.setTag(course);
            holder.name.setVisibility(course.getCRN().equals("")? View.INVISIBLE:View.VISIBLE);
            holder.section.setVisibility(!course.getCRN().equals("")? View.INVISIBLE:View.VISIBLE);
            }
            return convertView;

        }


    }

}

