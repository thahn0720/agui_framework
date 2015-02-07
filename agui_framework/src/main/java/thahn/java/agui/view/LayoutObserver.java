package thahn.java.agui.view;

public interface LayoutObserver {
	void onLayoutChanged(int l, int t, int r, int b);
	void onArrangeNeeded();
}
//public abstract class LayoutObserver {
//	
//	public void onChanged(int l, int t, int r, int b) {
//		
//	}
//}
