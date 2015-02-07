package thahn.java.agui.view;

public interface OnScrollListener {
	void onFirstScrolled();
	void onLastScrolled();
	void onScroll(int scrollX, int scrollY, int amount);
}
