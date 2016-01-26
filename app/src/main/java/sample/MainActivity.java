package sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.empty.launchredpacket.LaunchRedPacketLayout;
import com.empty.launchredpacket.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LaunchRedPacketLayout launchRedPacketLayout;
    private Button launchBtn, reStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        launchRedPacketLayout = (LaunchRedPacketLayout) findViewById(R.id.launchRedPacket);
        launchBtn = (Button) findViewById(R.id.launchBtn);
        reStartBtn = (Button) findViewById(R.id.reStart);

        launchBtn.setOnClickListener(this);
        reStartBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.reStart:
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                    break;
                case R.id.launchBtn:
                    launchRedPacketLayout.launch(3);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
