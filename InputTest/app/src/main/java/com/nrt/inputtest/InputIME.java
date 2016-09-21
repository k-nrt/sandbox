package com.nrt.inputtest;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.view.View;



class SushiCode {
	public static final int MAGURO = 10000;
	public static final int DUKE_MAGURO = 10001;
	public static final int BINTORO = 10002;
	public static final int MADAI = 10003;
	public static final int HAMACHI = 10004;
	public static final int MAIWASHI = 10005;
	public static final int SALMON = 10006;
	public static final int SODEIKA = 10007;
}

public class InputIME
	extends InputMethodService
	implements KeyboardView.OnKeyboardActionListener
{

	private InputMethodManager mInputMethodManager;
	private MyKeyboardView mInputView;
	private MyKeyboard mMyKeyboard;
	private int mLastDisplayWidth;

	@Override
	public void onCreate() 
	{
		super.onCreate();
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	}

	@Override
	public void onInitializeInterface() 
	{
		super.onInitializeInterface();
		if (mMyKeyboard != null) 
		{
			// 再表示する場合の調整
			int displayWidth = getMaxWidth();
			if (displayWidth == mLastDisplayWidth) 
			{
				return;
			}
			mLastDisplayWidth = displayWidth;
		}
		mMyKeyboard = new MyKeyboard(this, R.xml.mykeyboard);

		
	}

	@Override
	public View onCreateInputView()
	{
		mInputView = (MyKeyboardView) getLayoutInflater().inflate(R.layout.input, null);
		mInputView.setOnKeyboardActionListener(this);
		mInputView.setKeyboard(mMyKeyboard);
		return mInputView;
	}
/*
	@Override
	public void onKey(int primaryCode, int[] keyCodes)
	{

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		return super.onKeyDown(keyCode, event);
	}
*/
	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		if (primaryCode == Keyboard.KEYCODE_DELETE) {
			keyDownUp(KeyEvent.KEYCODE_DEL);
		} else if(primaryCode == SushiCode.MAGURO){
			getCurrentInputConnection().commitText("まぐろ", 1);
		} else if(primaryCode == SushiCode.DUKE_MAGURO){
			getCurrentInputConnection().commitText("漬けまぐろ", 1);
		} else if(primaryCode == SushiCode.BINTORO){
			getCurrentInputConnection().commitText("ビントロ", 1);
		} else if(primaryCode == SushiCode.MADAI){
			getCurrentInputConnection().commitText("真だい", 1);
		} else if(primaryCode == SushiCode.HAMACHI){
			getCurrentInputConnection().commitText("はまち", 1);
		} else if(primaryCode == SushiCode.MAIWASHI){
			getCurrentInputConnection().commitText("真いわし", 1);
		} else if(primaryCode == SushiCode.SALMON){
			getCurrentInputConnection().commitText("サーモン", 1);
		} else if(primaryCode == SushiCode.SODEIKA){
			getCurrentInputConnection().commitText("そでいか", 1);
		} else {
// 文字の割り当てのあるKEYCODEの場合（アルファベットなど）
			getCurrentInputConnection().commitText(
				String.valueOf((char) primaryCode), 1);
		}
	}

	private void keyDownUp(int keyEventCode) {
		getCurrentInputConnection().sendKeyEvent(
			new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		getCurrentInputConnection().sendKeyEvent(
			new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}
	
	@Override
	public void onUpdateSelection
	(
		int oldSelStart, int oldSelEnd,
		int newSelStart, int newSelEnd, int candidatesStart,
		int candidatesEnd
	)
	{
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
								candidatesStart, candidatesEnd);
	}

	@Override
	public void onPress(int primaryCode) {

	}

	@Override
	public void onRelease(int primaryCode) {

	}

	@Override
	public void onText(CharSequence text) {

	}

	@Override
	public void swipeDown() {

	}

	@Override
	public void swipeLeft() {

	}

	@Override
	public void swipeRight() {

	}

	@Override
	public void swipeUp() {

	}

}

