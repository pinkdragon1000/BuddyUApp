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
 * {@link TermsAndConditions.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TermsAndConditions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TermsAndConditions extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView terms;
    private OnFragmentInteractionListener mListener;

    public TermsAndConditions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TermsAndConditions.
     */
    // TODO: Rename and change types and number of parameters
    public static TermsAndConditions newInstance(String param1, String param2) {
        TermsAndConditions fragment = new TermsAndConditions();
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
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
    }
@Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        ((TextView)view.findViewById(R.id.terms_and_conditions))
                .setMovementMethod(new ScrollingMovementMethod());
        terms= (TextView) view.findViewById(R.id.terms_and_conditions);
        fillText();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void fillText()
    {

        String data="";
        data += "<p><b><font color=\"#6ac6aF\"> Last updated: 5/7/17 </font></b> <br><br>";
        data +=" Please read these Terms and Conditions carefully before using the BuddyU app operated by the BuddyU Team.<br><br> " +
                "Your access to and use of the Service is conditioned on your acceptance of and compliance with these Terms.<br><br>  " +
                "These terms apply to all users who access or use the Service.<br><br>" +
                "By accessing or using this Service you agree to be bound by these Terms.  " +
                "If you disagree with any part of these terms then you may not access the Service.";
        data+="</p>";
        //Adds on to the string the "title" Accounts in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Accounts </font> </b> <br>";
        data +="When you create an account with us, you must provide us information that is " +
                "accurate, complete, and current at all times.  Failure to do so constitutes a " +
                "breach of the Terms, which may result in immediate termination of your account " +
                "on our Service.<br><br>" +
                "You are responsible for safeguarding the password that you use to access the " +
                "Service and for any activities or actions under your password, whether your " +
                "password is with our Service or a third-party service.<br><br>" +
                "You agree not to disclose your password to any third party. You must notify us " +
                "immediately upon becoming aware of any breach of security or unauthorized use of your account." ;
        data +="</p>";
        //Adds on to the string the "title" Changes in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Changes </font> </b> <br>";
        data +="We reserve the right, at our sole discretion, to modify or replace these Terms at any time. " +
                "If a revision is material we will try to provide at least 30 days' notice prior to any new terms taking effect. " +
                "What constitutes a material change will be determined at our sole discretion.";
        data +="</p>";

        //Adds on to the string the "title" Contact Us in a greenish color
        data += "<p><b><font color=\"#6ac6aF\"> Contact Us </font> </b> <br>";
        data +="If you have any questions about these Terms, please contact the email sita.robinson@drexel.edu.";
        data +="</p>";

        terms.setText(Html.fromHtml(data));

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
