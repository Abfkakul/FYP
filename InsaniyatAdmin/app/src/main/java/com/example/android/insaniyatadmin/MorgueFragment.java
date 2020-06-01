package com.example.android.insaniyatadmin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MorgueFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MorgueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MorgueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MorgueFragment newInstance(String param1, String param2) {
        MorgueFragment fragment = new MorgueFragment();
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.fragment_morgue, container, false);
//        ImageView refresh = view.findViewById(R.id.refresh);

        final ArrayList<MorgueRequest> list = new ArrayList<MorgueRequest>();
        final MorgueRequestAdapter listAdapter = new MorgueRequestAdapter(getActivity(),list,R.color.darkskyblue);
        final ListView listView = view.findViewById(R.id.morgueList);
        listView.setAdapter(listAdapter);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference pendingRequestsRef=db.collection("PendingMorgueRequests");
        final String[] currentUserName = new String[1];
        final MorgueRequest[] newMorgueRequest = {new MorgueRequest()};

        pendingRequestsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    String username = documentSnapshot.getString("name");
                    String bodyName = documentSnapshot.getString("bodyName");
                    String phn = documentSnapshot.getString("phoneNumber");
                    String date = documentSnapshot.getString("date");
                    MorgueRequest newApproval = new MorgueRequest(username,bodyName,phn,date);
                    currentUserName[0]=username;
                    list.add(newApproval);
                    listView.setAdapter(listAdapter);
                    newMorgueRequest[0] = newApproval;
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final MorgueRequest newApproval = list.get(position);
                btn_showDialog(newApproval);
            }
        });

//        refresh.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                refresh();
//            }
//        });
        return view;
    }

    public void btn_showDialog(final MorgueRequest newApproval)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.morgue_custom_dialog, null);

        Button callButton, completedButton, cancelButton,acceptButton,rejectButton;
        callButton = mView.findViewById(R.id.call);
        completedButton = mView.findViewById(R.id.accepted);
        cancelButton = mView.findViewById(R.id.cancel);
        acceptButton= mView.findViewById(R.id.accept);
        rejectButton = mView.findViewById(R.id.reject);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        callButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + newApproval.getPhoneNumber()));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else
                {
                    startActivity(intent);
                }
            }
        });

        completedButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Morgue Request Completion")
                        .setMessage("Have you completed this request?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                final CollectionReference pendingMorgueRequestsRef = FirebaseFirestore.getInstance().collection("PendingMorgueRequests");
                                pendingMorgueRequestsRef.document(""+newApproval.getPhoneNumber()).delete();
                                refresh();
                            }
                        })

                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                alertDialog.dismiss();
                                refresh();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Morgue Request")
                        .setMessage("Do you want to accept this request?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                refresh();

                                try
                                {
                                    SmsManager smgr = SmsManager.getDefault();
                                    smgr.sendTextMessage(newApproval.getPhoneNumber(),null,"Your request for the morgue is accepted.",null,null);
                                    Toast.makeText(getActivity(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })

                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                alertDialog.dismiss();
                                refresh();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Morgue Request")
                        .setMessage("Do you want to reject this request?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                refresh();

                                try
                                {
                                    SmsManager smgr = SmsManager.getDefault();
                                    smgr.sendTextMessage(newApproval.getPhoneNumber(),null,"Sorry, no space avalibale at Morgue.",null,null);
                                    Toast.makeText(getActivity(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                                }

                                final CollectionReference pendingMorgueRequestsRef = FirebaseFirestore.getInstance().collection("PendingMorgueRequests");
                                pendingMorgueRequestsRef.document(""+newApproval.getPhoneNumber()).delete();
                            }
                        })

                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                alertDialog.dismiss();
                                refresh();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void refresh()
    {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

}
