package com.tr8n.android.interfaces;


public interface Initializable {
	/**
	 * Called before Tr8n is initialized 
	 */
    public void onTr8nBeforeInit();

    /**
     * Asynchronous task, called during initialization phase
     */
    public void onTr8nInit();
    
    /**
     * Called after Tr8n is initialized
     */
    public void onTr8nAfterInit();
}
