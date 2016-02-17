package by.laguta.skryaga.service.util;

import android.os.AsyncTask;
import by.laguta.skryaga.service.UpdateListener;

/**
 * Author : Anatoly
 * Created : 17.02.2016 21:48
 *
 * @author Anatoly
 */
public abstract class UpdateTask<T> extends AsyncTask<UpdateListener<T>, Integer, T> {

    private UpdateListener<T>[] listeners;

    @Override
    protected T doInBackground(UpdateListener<T>... params) {
        this.listeners = params;

        return performInBackground();
    }

    protected abstract T performInBackground();

    @Override
    protected void onPostExecute(T t) {
        if (listeners != null) {
            for (UpdateListener<T> listener : listeners) {
                listener.onUpdated(t);
            }
        }
    }
}
