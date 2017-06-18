package com.example.sitar.buddyuapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutBuddyU.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutBuddyU#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutBuddyU extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView aboutBuddyU;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AboutBuddyU() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutBuddyU.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutBuddyU newInstance(String param1, String param2) {
        AboutBuddyU fragment = new AboutBuddyU();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_about_buddyu, container, false);

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
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        ((TextView)view.findViewById(R.id.about_buddyu_text))
                .setMovementMethod(new ScrollingMovementMethod());
        aboutBuddyU= (TextView) view.findViewById(R.id.about_buddyu_text);
        fillText();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fillText()
    {

        String data="";
        data += "<p><b><font color=\"#6ac6aF\"> What is BuddyU?</font></b> <br>";
        data +=" The purpose of the BuddyU app is to provide a social platform for college students,specifically Drexel University, to collaborate on their schedules in order to make class time more efficient, collaborative and fun.<br><br>" ;
        data+="</p>";
        //Adds on to the string the "title" Monday in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Platforms Available </font> </b> <br>";
        data +="Currently the BuddyU app is designed for the Android platform." ;
        data +="</p>";
        //Adds on to the string the "title" Tuesday in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Features of BuddyU </font> </b> <br>";
        data +="Home<br>" +
                "-Displays a welcome message to the user.  A user can learn about what the app is about and read our terms and conditions.<br><br>"+
                "Profile<br>" +
                "-Allows user to change their image, add buddies, and once selected/added view schedules for those buddies.<br><br>" +
                "Classes Screen<br>" +
                "-Allows users to add classes to their schedule.<br><br>" +
                "Schedule<br>" +
                "-Displays classes user selected in the Classes screen creating a schedule for that user. ";
        data +="</p>";
        aboutBuddyU.setText(Html.fromHtml(data));

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
