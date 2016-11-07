package com.example.theodhor.stripeandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import java.util.HashMap;
import java.util.Map;

public class PayActivity extends AppCompatActivity {

    Stripe stripe;
    Integer amount;
    String name;
    Card card;
    Token tok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Bundle extras = getIntent().getExtras();
        amount = extras.getInt("plan_price");
        name = extras.getString("plan_name");

        try {
            stripe = new Stripe("pk_test_pjA2qZL0x43hGfGDL0rwo2B9");
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

    }

    public void submitCard(View view) {
        // TODO: replace with your own test key
        TextView cardNumberField = (TextView) findViewById(R.id.cardNumber);
        TextView monthField = (TextView) findViewById(R.id.month);
        TextView yearField = (TextView) findViewById(R.id.year);
        TextView cvcField = (TextView) findViewById(R.id.cvc);

        card = new Card(
                cardNumberField.getText().toString(),
                Integer.valueOf(monthField.getText().toString()),
                Integer.valueOf(yearField.getText().toString()),
                cvcField.getText().toString()
        );

        card.setCurrency("usd");
        card.setName("Theodhor Pandeli");
        card.setAddressZip("1000");
        /*
        card.setNumber(4242424242424242);
        card.setExpMonth(12);
        card.setExpYear(19);
        card.setCVC("123");
        */


        stripe.createToken(card, "pk_test_pjA2qZL0x43hGfGDL0rwo2B9", new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();
                tok = token;
                new StripeCharge().doInBackground();
            }

            public void onError(Exception error) {
                Log.d("Stripe", error.getLocalizedMessage());
            }
        });
    }

    public class StripeCharge extends com.stripe.android.compat.AsyncTask {

        @Override
        protected String doInBackground(Object... params) {

            new Thread() {
                @Override
                public void run() {
                    try {
                        Map<String, Object> chargeParams = new HashMap<String, Object>();
                        chargeParams.put("amount", amount); // Amount in cents
                        chargeParams.put("currency", "usd");
                        chargeParams.put("card", tok.getId());
                        chargeParams.put("description", name);

                        Charge charge = Charge.create(chargeParams);
                        charge.capture();
                    } catch (CardException e) {
                        // The card has been declined
                        e.printStackTrace();
                    } catch (APIException e) {
                        e.printStackTrace();
                    } catch (InvalidRequestException e) {
                        e.printStackTrace();
                    } catch (APIConnectionException e) {
                        e.printStackTrace();
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return "Done";
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(PayActivity.this, "Payment done!", Toast.LENGTH_SHORT).show();
        }
    }
}
