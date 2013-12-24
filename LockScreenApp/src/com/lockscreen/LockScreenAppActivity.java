package com.lockscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.Calendar;

public class LockScreenAppActivity extends Activity {

	// наши часы
	private TextView clock;
	// наши картинки
	private ImageView droid, home;
	// отслеживаем в этот массив координаты пальца
	private int[] droidpos;
	// и передаем в этим переменные
	private int home_x, home_y;
	// лайот на котором находится весь лок скрин
	private LayoutParams layoutParams;
	// размеры экрана
	private int windowheight;
	private int windowwidth;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// делаем наше экран полностью залоченым
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		// идентифицируем наще яблоко
		droid = (ImageView) findViewById(R.id.apple);
		if ((getIntent() != null) && getIntent().hasExtra("kill")
				&& (getIntent().getExtras().getInt("kill") == 1)) {
			finish();
		}
		try {
			// инициализация рисивера
			startService(new Intent(this, MyService.class));
			// стартуем отслеживание состояния телефона
			StateListener phoneStateListener = new StateListener();
			// узнаем все сервисы которые есть
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			// слушаем когда телефон уходит в сон и включаем нашего блокировщика
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			// хватаем размеры экрана и растягиваем под него локскрин
			windowwidth = getWindowManager().getDefaultDisplay().getWidth();
			windowheight = getWindowManager().getDefaultDisplay().getHeight();
			// выставляем позицию нашего яблока, что бы оно было по центру экрана
			MarginLayoutParams marginParams2 = new MarginLayoutParams(droid.getLayoutParams());
			marginParams2.setMargins((windowwidth / 24) * 10, ((windowheight / 32) * 8), 0, 0);
			// ставим по центру релайтив лайота
			RelativeLayout.LayoutParams layoutdroid = new RelativeLayout.LayoutParams(marginParams2);
			droid.setLayoutParams(layoutdroid);
			// дальше инициализируем наш текствью в который будем выводить время
			clock = (TextView) findViewById(R.id.textView1);
			// инициализируем линейр в котором будет дроид
			LinearLayout homelinear = (LinearLayout) findViewById(R.id.homelinearlayout);
			homelinear.setPadding(0, 0, 0, 0);
			// инициализируем самого дроида
			home = (ImageView) findViewById(R.id.droid);
			// выставляем его положение, в нашем случае отступ от низа экрана
			MarginLayoutParams marginParams1 = new MarginLayoutParams(home.getLayoutParams());
			marginParams1.setMargins((windowwidth / 24) * 10, 0, (windowheight / 32) * 8, 0);
			// отслеживаем его перемещение
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(marginParams1);
			home.setLayoutParams(layout);
			// вложеная функция обработки перемещения имейджвью яблока по экрану
			droid.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					layoutParams = (LayoutParams) v.getLayoutParams();
					// обрабатываем позиции
					switch (event.getAction()) {
					// нажал на экран
						case MotionEvent.ACTION_DOWN: {
							int[] hompos = new int[2];
							droidpos = new int[2];
							home.getLocationOnScreen(hompos);
							home_x = hompos[0];
							home_y = hompos[1];
						}
							break;
						// перемещаем по экрану яблоко за пальцем
						case MotionEvent.ACTION_MOVE: {
							int x_cord = (int) event.getRawX();
							int y_cord = (int) event.getRawY();
							if (x_cord > (windowwidth - (windowwidth / 24))) {
								x_cord = windowwidth - ((windowwidth / 24) * 2);
							}
							if (y_cord > (windowheight - (windowheight / 32))) {
								y_cord = windowheight - ((windowheight / 32) * 2);
							}
							layoutParams.leftMargin = x_cord;
							layoutParams.topMargin = y_cord;
							droid.getLocationOnScreen(droidpos);
							v.setLayoutParams(layoutParams);
							if ((((x_cord - home_x) <= ((windowwidth / 24) * 5)) && ((home_x - x_cord) <= ((windowwidth / 24) * 4)))
									&& ((home_y - y_cord) <= ((windowheight / 32) * 5))) {
								v.setVisibility(View.GONE);
								finish();
							}
						}
							break;
						// убрал палец с экрана, возаращаем на стартовую позицию
						case MotionEvent.ACTION_UP: {
							int x_cord1 = (int) event.getRawX();
							int y_cord2 = (int) event.getRawY();
							if ((((x_cord1 - home_x) <= ((windowwidth / 24) * 5)) && ((home_x - x_cord1) <= ((windowwidth / 24) * 4)))
									&& ((home_y - y_cord2) <= ((windowheight / 32) * 5))) {
								System.out.println("home overlapps");
								System.out.println("homeee" + home_x + "  " + (int) event.getRawX() + "  "
										+ x_cord1 + " " + droidpos[0]);
								System.out.println("homeee" + home_y + "  " + (int) event.getRawY() + "  "
										+ y_cord2 + " " + droidpos[1]);
							} else {
								layoutParams.leftMargin = (windowwidth / 24) * 10;
								layoutParams.topMargin = (windowheight / 32) * 8;
								v.setLayoutParams(layoutParams);
							}
						}
					}
					return true;
				}
			});
			// узнаем который час
			Calendar c = Calendar.getInstance();
			// выводим время
			clock.setText("Time: " + c.get(Calendar.HOUR_OF_DAY) + " : " + c.get(Calendar.MINUTE));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// класс слушатель тач евентов
	class StateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			// если яблоко коснулось дроида то выключаем лок скрин
				case TelephonyManager.CALL_STATE_OFFHOOK: {
					finish();
				}
					break;
			}
		}
	}
}