package id.sch.smktelkom_mlg.privateassignment.xirpl109.marveljos;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import id.sch.smktelkom_mlg.privateassignment.xirpl109.marveljos.adapter.CharacterAdapter;
import id.sch.smktelkom_mlg.privateassignment.xirpl109.marveljos.model.Character;
import id.sch.smktelkom_mlg.privateassignment.xirpl109.marveljos.model.Response;
import id.sch.smktelkom_mlg.privateassignment.xirpl109.marveljos.service.GsonGetRequest;
import id.sch.smktelkom_mlg.privateassignment.xirpl109.marveljos.service.VolleySingleton;


/**
 * A simple {@link Fragment} subclass.
 */
public class CharacterFragment extends Fragment implements CharacterAdapter.charListener {

    public CharacterAdapter charAdapter;
    public List<Character> charList = new ArrayList<>();
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;

    public CharacterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        charAdapter = new CharacterAdapter(charList, getContext(), this);
    }

    private void fillData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        String url = "https://gateway.marvel.com:443/v1/public/characters?ts=1&apikey=f4dbb78409bc6ed6f31319830b30a4d5&hash=1b4a1c0351f6be2a613dd55c4246f3d9";
        GsonGetRequest<Response> req = new GsonGetRequest<Response>(url, Response.class, null, new com.android.volley.Response.Listener<Response>() {
            @Override
            public void onResponse(Response response) {
                if (response.status.equals("Ok")) {
                    charList.addAll(response.data.results);
                }
                charAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CHAR FRAGMENT", "Error : ", error);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        Toast.makeText(getContext(),
                                "Oops. Timeout error!",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("error", "Timeout");
                        startActivity(intent);
                    }
                }
                progressDialog.dismiss();
            }
        });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(charAdapter);
        fillData();

        return view;
    }

    @Override
    public void detail(int pos) {
        Character chari = charList.get(pos);
        Intent intent = new Intent(getActivity(), CharacterDetailActivity.class);
        intent.putExtra("detail", chari);
        startActivity(intent);
    }
}
