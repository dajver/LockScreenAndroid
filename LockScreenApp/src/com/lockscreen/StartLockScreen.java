package com.lockscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartLockScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// запускаем сервис
		startService(new Intent(this, MyService.class));
		// убиваем активность
		finish();
	}
}
