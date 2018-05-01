package ie.fastway.scansort.device.printer;

import android.support.annotation.NonNull;

/**
 * A device that prints to a label.
 */
public interface LabelPrinter {

    public void print(@NonNull LabelMessage label);

}
