package nrt.gles30test;

import android.app.*;
import android.os.*;
import android.widget.*;
import com.nrt.framework.Subsystem;

public class MainActivity extends Activity 
{
	private android.os.Handler m_handler = new android.os.Handler();

	private static GameContext m_gameContext = new GameContext();
	
	private int m_nbOnCreated=0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		if( m_nbOnCreated <= 0 )
		{
			Subsystem.Initialize
			(
				getResources().getAssets(), 
				(TextView) findViewById(R.id.logview),
				m_handler,
				m_gameContext
			);
		}
		else
		{
			Subsystem.ResourceQueue.ReloadResources();
		}

		Subsystem.Log.WriteLine("MainActivity.onCreate() " + m_nbOnCreated);
		m_nbOnCreated++;
    }
}
