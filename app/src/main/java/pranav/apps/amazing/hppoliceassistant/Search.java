package pranav.apps.amazing.hppoliceassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Search extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerview;
    private List<CountryModel> mCountryModel;
    private RVAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseDatabase database =FirebaseDatabase.getInstance();              //it return root url
        DatabaseReference myRef = database.getReference("vehicle_entry");              //migrate from tree in other branches
        View view = getActivity().getLayoutInflater().inflate(R.layout.search, container, false);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        myRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Map<String,String> entry = (Map)dataSnapshot.getValue();
                String v = entry.get("VehicleNumber");
                String p = entry.get("PhoneNumber");
                String pl = entry.get("Place");
                String nk = entry.get("Naka");
                String d = entry.get("Description");
                mCountryModel.add(new CountryModel(v,p,pl,nk,d));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                /*
                Map<String,String> entry = (Map)dataSnapshot.getValue();
                String v = entry.get("VehicleNumber");
                String p = entry.get("PhoneNumber");
                String pl = entry.get("Place");
                String nk = entry.get("Naka");
                String d = entry.get("Description");
                String i = entry.get("Image");
                veh_entry.add(new Friend(v,p,pl,nk,d,i));
                adapter.notifyDataSetChanged();
                */
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                /*
                Map<String,String> entry = (Map)dataSnapshot.getValue();
                String v = entry.get("VehicleNumber");
                String p = entry.get("PhoneNumber");
                String pl = entry.get("Place");
                String nk = entry.get("Naka");
                String d = entry.get("Description");
                String i = entry.get("Image");
                veh_entry.add(new Friend(v,p,pl,nk,d,i));
                adapter.notifyDataSetChanged();
                */
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);
        //String[] locales = Locale.getISOCountries();
        mCountryModel = new ArrayList<>();
/*
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            mCountryModel.add(new CountryModel(obj.getDisplayCountry(), obj.getISO3Country()));
        }
*/
        adapter = new RVAdapter(mCountryModel);
        recyclerview.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(mCountryModel);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<CountryModel> filteredModelList = filter(mCountryModel, newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<CountryModel> filter(List<CountryModel> models, String query) {
        query = query.toLowerCase();

        final List<CountryModel> filteredModelList = new ArrayList<>();
        for (CountryModel model : models) {
            final String text1 = model.getName().toLowerCase();
            final String text2 = model.getisoCode().toLowerCase();
            final String text3 = model.getPlace().toLowerCase();
            final String text4 = model.getNaka().toLowerCase();
            final String text5 = model.getD().toLowerCase();
            if (text1.contains(query)|| text2.contains(query)|| text3.contains(query)|| text4.contains(query)||text5.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}


