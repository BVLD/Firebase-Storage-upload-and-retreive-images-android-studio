package e.geertvanleuven.blog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    //LAYOUT
    private Toolbar mToolbar;

    //FIREBASE AUTH
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //LAYOUT
        mToolbar = (Toolbar) findViewById(R.id.Toolbar_Home);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Blog app");

        //FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();
    }


    //HANDLES AL THE MENUS THINGS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;

    }


    //HANDLES THE CLICK EVENTS FROM THE MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.Logout:

                LOGOUT();

                return true;

            case R.id.settings:

                SETTINGS();

                return true;


                default: return false;

        }


    }

    private void SETTINGS() {

        Intent intent = new Intent(Home.this, Setup.class);
        startActivity(intent);

    }

    private void LOGOUT() {

        mAuth.signOut();
        Intent LogoutIntent = new Intent(Home.this, Login.class);
        startActivity(LogoutIntent);
        finish();

    }
}
