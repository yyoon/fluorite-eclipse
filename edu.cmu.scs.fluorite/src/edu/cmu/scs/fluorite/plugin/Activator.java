package edu.cmu.scs.fluorite.plugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.preferences.Initializer;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "EventLogger";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		if (getPreferenceStore().getBoolean(Initializer.Pref_EnableEventLogger)) {
			// NOTE: This event recording must start after the workbench is fully loaded.
			// So, run this in UIJob so it runs after the workbench loads.
			UIJob uiJob = new UIJob("Fluorite Initialization") {

				@Override
				public IStatus runInUIThread(IProgressMonitor arg0) {
					EventRecorder.getInstance().start();
					return Status.OK_STATUS;
				}
				
			};
			
			uiJob.setSystem(true);
			uiJob.setUser(false);
			uiJob.schedule();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		EventRecorder.getInstance().stop();
		
		plugin = null;
		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
