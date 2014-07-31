package org.am.sl;

import at.vcity.androidim.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

/**
 * @author Cindy Espínola
 *
 */
public class Welcome extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
//		setTitle("Speech Language");
		final int welcomeScreenDisplay=2000;
		Thread welcomeThead= new Thread(){
		int wait=0;
		@Override
		public void run(){
			try{
				super.run();
				while(wait<welcomeScreenDisplay){
					sleep(100);
					wait+=100;
				}
			}catch(Exception e){
				System.out.println(e);
			}finally{
				startActivity(new Intent(Welcome.this,Login.class));
				finish();
			}
		}
		
		};
		welcomeThead.start();
	}

}
