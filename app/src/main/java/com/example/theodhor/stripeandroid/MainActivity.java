package com.example.theodhor.stripeandroid;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.stripe.android.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Plan;
import com.stripe.model.PlanCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {


    Stripe stripe;
    ArrayList<Plan> planArrayList;
    PlanCollection planCollection;
    RecyclerView recyclerView;
    ItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        com.stripe.Stripe.apiKey = "sk_test_XLVACAQ3PHWMo1Ycx4YD2e1B";
        stripe = new Stripe();
        try {
            stripe.setDefaultPublishableKey("pk_test_pjA2qZL0x43hGfGDL0rwo2B9");
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        planArrayList = new ArrayList<>();



        new Async().execute();
    }

    public void showRcv(ArrayList<Plan> plans){
        adapter = new ItemsAdapter(this,plans);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public class Async extends AsyncTask<Void,String,ArrayList<Plan>> {

        @Override
        protected ArrayList<Plan> doInBackground(Void... params) {

            final Map<String, Object> productParams = new HashMap<String, Object>();
            productParams.put("limit", 3);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        planCollection = Plan.list(productParams);
                        planArrayList.addAll(planCollection.getData());
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    } catch (InvalidRequestException e) {
                        e.printStackTrace();
                    } catch (APIConnectionException e) {
                        e.printStackTrace();
                    } catch (CardException e) {
                        e.printStackTrace();
                    } catch (APIException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return planArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<Plan> plan) {
            super.onPostExecute(plan);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRcv(plan);
                }
            },3000);
        }
    }
}
