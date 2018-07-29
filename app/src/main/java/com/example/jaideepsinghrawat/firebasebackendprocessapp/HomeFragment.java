package com.example.jaideepsinghrawat.firebasebackendprocessapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jaideepsinghrawat.firebasebackendprocessapp.adapter.PostAdapter;
import com.example.jaideepsinghrawat.firebasebackendprocessapp.model.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView postRecylerView;
    private List<PostModel> postList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
   private  FirebaseFirestore firebaseFirestore;
   private FirebaseAuth firebaseAuth;
   private  PostAdapter postAdapter;
   private DocumentSnapshot lastvisible;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        postRecylerView=(RecyclerView)v.findViewById(R.id.recycler_View);
        postList=new ArrayList<>();
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        postRecylerView.setLayoutManager(manager);
        postRecylerView.setHasFixedSize(true);
        postAdapter=new PostAdapter(getActivity(),postList);
        firebaseAuth=FirebaseAuth.getInstance();
        postRecylerView.setAdapter(postAdapter);
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore=FirebaseFirestore.getInstance();
            postRecylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean reachedBottom=!recyclerView.canScrollVertically(1);
                    if(reachedBottom){
                        String desc=lastvisible.getString("description");
                        Toast.makeText(container.getContext(),"reached :"+desc,Toast.LENGTH_LONG).show();
                        loadMoreItems();
                    }
                }
            });
            //  Query for descending order
            Query query=firebaseFirestore.collection("Post").orderBy("timestamp",Query.Direction.DESCENDING).limit(3);
//        firebaseFirestore.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    lastvisible=documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                    for(DocumentChange doc:documentSnapshots.getDocumentChanges()){
                        if(doc.getType()==DocumentChange.Type.ADDED){
                            PostModel postModel=doc.getDocument().toObject(PostModel.class);
                            postList.add(postModel);
                            postAdapter.notifyDataSetChanged();

                        }
                    }
                }
            });
        }

        return v;
    }
    public void loadMoreItems(){
        Query query=firebaseFirestore.collection("Post").orderBy("timestamp",Query
                .Direction.DESCENDING)
                .startAfter(lastvisible)
                .limit(3);
//        firebaseFirestore.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null && !documentSnapshots.isEmpty()){
                    lastvisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        PostModel postModel = doc.getDocument().toObject(PostModel.class);
                        postList.add(postModel);
                        postAdapter.notifyDataSetChanged();

                    }
                }
            }
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
