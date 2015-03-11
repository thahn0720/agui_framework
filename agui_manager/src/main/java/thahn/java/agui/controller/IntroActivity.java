package thahn.java.agui.controller;

import thahn.java.agui.app.Activity;
import thahn.java.agui.app.Bundle;
import thahn.java.agui.app.Intent;
import thahn.java.agui.utils.Log;
import thahn.java.agui.view.View;
import thahn.java.agui.view.View.OnClickListener;

public class IntroActivity extends Activity {

	public static final int										REQUEST_CODE 		= 1;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout3);
		findViewById(R.id.view1).setOnClickListener(onClickListener);
		findViewById(R.id.view2).setOnClickListener(onClickListener);
		findViewById(R.id.view3).setOnClickListener(onClickListener);
		// start service 
		Intent intent = new Intent(IntroActivity.this, TestService.class);
		startService(intent);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.e("click : " + v.getId());
		}
	};
}
