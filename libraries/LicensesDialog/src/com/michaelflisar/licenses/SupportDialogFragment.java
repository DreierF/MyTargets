package com.michaelflisar.licenses;

import android.os.Bundle;

public interface SupportDialogFragment {
	void show(Bundle savedInstanceState);
	void onSaveDialogInstanceState(Bundle out);
	String getKey();
	boolean isShowing();
}
